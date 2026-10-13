import { createHash } from 'node:crypto';
import { copyFile, mkdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import type {
  FullConfig,
  FullResult,
  Reporter,
  TestCase,
  TestResult,
  TestStep,
} from '@playwright/test/reporter';

interface ReportStep {
  title: string;
  category: string;
  duration: number;
  status: 'passed' | 'failed' | 'skipped';
  error?: string;
  children: ReportStep[];
}

interface ReportAttachment {
  name: string;
  contentType: string;
  file: string;
}

interface ReportAttempt {
  status: string;
  duration: number;
  retry: number;
  error?: string;
  steps: ReportStep[];
  attachments: ReportAttachment[];
}

interface ReportTest {
  id: string;
  title: string;
  titlePath: string[];
  file: string;
  line: number;
  project: string;
  outcome: string;
  attempts: ReportAttempt[];
}

interface ReporterOptions {
  outputDir?: string;
}

/** Escapes dynamic values before placing them in generated HTML. */
function escapeHtml(value: string): string {
  return value.replace(/[&<>"']/g, character => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
  })[character] ?? character);
}

/** Creates a stable file-safe identifier for a Playwright test. */
function fileId(testId: string): string {
  const readable = testId.replace(/[^a-zA-Z0-9_-]+/g, '-').slice(-48);
  const hash = createHash('sha1').update(testId).digest('hex').slice(0, 10);
  return `${readable}-${hash}`;
}

/** Converts Playwright's nested steps into reportable pass/fail rows. */
function mapSteps(steps: TestStep[]): ReportStep[] {
  return steps.map(step => {
    const children = mapSteps(step.steps);
    const status = step.error || children.some(child => child.status === 'failed')
      ? 'failed'
      : 'passed';
    return {
      title: step.title,
      category: step.category,
      duration: step.duration,
      status,
      error: step.error?.message,
      children,
    };
  });
}

/** Returns a readable error message for the test details page. */
function formatError(result: TestResult): string | undefined {
  const error = result.error ?? result.errors[0];
  if (!error) {
    return undefined;
  }
  return [error.message, error.stack].filter(Boolean).join('\n\n');
}

/** Renders a tree of test steps as nested HTML details. */
function renderSteps(steps: ReportStep[]): string {
  if (steps.length === 0) {
    return '<p>No step data was reported for this test.</p>';
  }
  return `<ol class="steps">${steps.map(step => `
    <li>
      <details ${step.status === 'failed' ? 'open' : ''}>
        <summary>
          <span class="badge ${step.status}">${escapeHtml(step.status)}</span>
          <span>${escapeHtml(step.title)}</span>
          <small>${step.duration} ms${step.category ? ` · ${escapeHtml(step.category)}` : ''}</small>
        </summary>
        ${step.error ? `<pre class="error">${escapeHtml(step.error)}</pre>` : ''}
        ${renderSteps(step.children)}
      </details>
    </li>`).join('')}</ol>`;
}

/** Returns the failed step label for associating a captured failure screenshot. */
function firstFailedStep(steps: ReportStep[]): string {
  for (const step of steps) {
    if (step.status === 'failed') {
      return step.title;
    }
    const nestedFailure = firstFailedStep(step.children);
    if (nestedFailure) {
      return nestedFailure;
    }
  }
  return 'Test failure';
}

/** Creates the shared stylesheet and layout for report pages. */
function reportPage(title: string, content: string): string {
  return `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${escapeHtml(title)}</title>
  <style>
    :root { color-scheme: light; font: 15px/1.5 Segoe UI, Arial, sans-serif; color: #1f2937; background: #f3f4f6; }
    body { margin: 0; padding: 2rem; }
    main { max-width: 1100px; margin: auto; background: white; padding: 2rem; border-radius: 10px; box-shadow: 0 2px 12px #0001; }
    h1, h2 { line-height: 1.2; }
    table { width: 100%; border-collapse: collapse; margin-top: 1.5rem; }
    th, td { padding: .7rem; border-bottom: 1px solid #e5e7eb; text-align: left; vertical-align: top; }
    th { background: #f9fafb; }
    a { color: #075bbb; }
    .badge { display: inline-block; padding: .1rem .55rem; border-radius: 999px; font-weight: 700; text-transform: uppercase; font-size: .75rem; }
    .passed { color: #166534; background: #dcfce7; }
    .failed, .timedOut { color: #991b1b; background: #fee2e2; }
    .flaky { color: #854d0e; background: #fef3c7; }
    .skipped { color: #374151; background: #e5e7eb; }
    .summary { display: flex; gap: 1rem; flex-wrap: wrap; }
    .summary span { background: #f9fafb; border: 1px solid #e5e7eb; padding: .5rem .8rem; border-radius: 6px; }
    .steps { padding-left: 1.4rem; }
    .steps li { margin: .6rem 0; }
    summary { display: flex; align-items: center; gap: .6rem; cursor: pointer; }
    summary small { color: #6b7280; }
    pre { white-space: pre-wrap; overflow-wrap: anywhere; padding: 1rem; border-radius: 6px; background: #f9fafb; }
    pre.error { color: #991b1b; background: #fff1f2; }
    .screenshot { max-width: 100%; border: 1px solid #d1d5db; border-radius: 6px; }
    .artifact { margin: 1rem 0 1.5rem; }
    .muted { color: #6b7280; }
    @media (max-width: 700px) { body { padding: .5rem; } main { padding: 1rem; overflow-x: auto; } }
  </style>
</head>
<body><main>${content}</main></body>
</html>`;
}

export default class CustomHtmlReporter implements Reporter {
  private outputDir: string;
  private readonly tests = new Map<string, ReportTest>();
  private readonly pendingWrites: Promise<void>[] = [];

  constructor(options: ReporterOptions = {}) {
    this.outputDir = options.outputDir ?? 'custom-report';
  }

  /** Resolves the report output folder relative to the Playwright project root. */
  onBegin(config: FullConfig): void {
    this.outputDir = path.resolve(config.rootDir, this.outputDir);
  }

  /** Collects each test attempt, including nested steps, errors, screenshots, and traces. */
  onTestEnd(test: TestCase, result: TestResult): void {
    const record = this.tests.get(test.id) ?? {
      id: test.id,
      title: test.title,
      titlePath: test.titlePath(),
      file: path.relative(process.cwd(), test.location.file),
      line: test.location.line,
      project: test.parent.project()?.name ?? 'default',
      outcome: 'unknown',
      attempts: [],
    };

    record.outcome = test.outcome();
    const steps = mapSteps(result.steps);
    const attempt: ReportAttempt = {
      status: result.status,
      duration: result.duration,
      retry: result.retry,
      error: formatError(result),
      steps,
      attachments: [],
    };

    const detailId = fileId(test.id);
    const assetsDir = path.join(this.outputDir, 'assets');
    const attachments = this.collectAttachments(result);
    for (const [index, attachment] of attachments.entries()) {
      const extension = attachment.contentType === 'image/png' ? '.png'
        : attachment.contentType === 'application/zip' ? '.zip'
          : attachment.contentType === 'text/plain' ? '.txt' : '.bin';
      const name = `${detailId}-attempt-${result.retry}-${index}${extension}`;
      const destination = path.join(assetsDir, name);
      if (attachment.path) {
        this.pendingWrites.push(mkdir(assetsDir, { recursive: true })
          .then(() => copyFile(attachment.path!, destination)));
      } else if (attachment.body) {
        this.pendingWrites.push(mkdir(assetsDir, { recursive: true })
          .then(() => writeFile(destination, attachment.body!)));
      } else {
        continue;
      }
      attempt.attachments.push({
        name: attachment.name,
        contentType: attachment.contentType,
        file: `assets/${name}`,
      });
    }

    record.attempts.push(attempt);
    this.tests.set(test.id, record);
  }

  /** Writes the summary and per-test detail pages after Playwright finishes. */
  async onEnd(_result: FullResult): Promise<void> {
    await Promise.all(this.pendingWrites);
    await mkdir(this.outputDir, { recursive: true });
    const tests = [...this.tests.values()].sort((left, right) =>
      left.file.localeCompare(right.file) || left.line - right.line);

    for (const test of tests) {
      const details = this.renderTestDetails(test);
      await writeFile(path.join(this.outputDir, `${fileId(test.id)}.html`), details, 'utf8');
    }

    const counts = tests.reduce<Record<string, number>>((summary, test) => {
      const status = this.displayStatus(test.outcome);
      summary[status] = (summary[status] ?? 0) + 1;
      return summary;
    }, {});
    const summary = ['passed', 'failed', 'flaky', 'skipped']
      .map(status => `<span><strong>${escapeHtml(status)}:</strong> ${counts[status] ?? 0}</span>`)
      .join('');
    const rows = tests.map(test => {
      const status = this.displayStatus(test.outcome);
      return `<tr>
        <td><span class="badge ${status}">${escapeHtml(status)}</span></td>
        <td><a href="${fileId(test.id)}.html">${escapeHtml(test.title)}</a></td>
        <td>${escapeHtml(test.titlePath.slice(0, -1).join(' › '))}</td>
        <td>${escapeHtml(test.file)}:${test.line}</td>
        <td>${test.attempts.reduce((sum, attempt) => sum + attempt.duration, 0)} ms</td>
        <td>${test.attempts.length}</td>
      </tr>`;
    }).join('');

    const index = reportPage('Playwright Custom Test Report', `
      <h1>Playwright Test Report</h1>
      <p class="muted">Select a test name to view its step-by-step results, failure details, screenshots, and traces.</p>
      <div class="summary">${summary}<span><strong>Total:</strong> ${tests.length}</span></div>
      <table>
        <thead><tr><th>Result</th><th>Test</th><th>Suite</th><th>Source</th><th>Duration</th><th>Attempts</th></tr></thead>
        <tbody>${rows || '<tr><td colspan="6">No tests were reported.</td></tr>'}</tbody>
      </table>`);
    await writeFile(path.join(this.outputDir, 'index.html'), index, 'utf8');
  }

  private readonly collectAttachments = (result: TestResult) => {
    const attachments = [...result.attachments];
    const collectFromSteps = (steps: TestStep[]): void => {
      for (const step of steps) {
        attachments.push(...step.attachments);
        collectFromSteps(step.steps);
      }
    };
    collectFromSteps(result.steps);
    const hasFailureScreenshot = attachments.some(attachment => attachment.name === 'failure-screenshot');
    const selected = attachments.filter(attachment =>
      attachment.name === 'failure-screenshot'
      || attachment.contentType === 'application/zip'
      || attachment.contentType.startsWith('text/')
      || (!hasFailureScreenshot && attachment.contentType.startsWith('image/')));
    const seen = new Set<string>();
    return selected.filter(attachment => {
      const key = `${attachment.name}:${attachment.contentType}`;
      if (seen.has(key)) {
        return false;
      }
      seen.add(key);
      return true;
    });
  };

  private readonly displayStatus = (outcome: string): string => ({
    expected: 'passed',
    unexpected: 'failed',
    flaky: 'flaky',
    skipped: 'skipped',
  }[outcome] ?? outcome);

  private readonly renderTestDetails = (test: ReportTest): string => {
    const attempts = test.attempts.map((attempt, index) => {
      const status = attempt.status === 'timedOut' ? 'timedOut' : attempt.status;
      const failedStep = firstFailedStep(attempt.steps);
      const attachments = attempt.attachments.map(attachment => {
        const link = escapeHtml(attachment.file);
        if (attachment.contentType.startsWith('image/')) {
          return `<div class="artifact">
            <h3>Failure screenshot</h3>
            <p class="muted">Captured after the test failed. Associated failed step: ${escapeHtml(failedStep)}.</p>
            <a href="${link}"><img class="screenshot" src="${link}" alt="Failure screenshot for ${escapeHtml(failedStep)}"></a>
          </div>`;
        }
        return `<p class="artifact"><a href="${link}">${escapeHtml(attachment.name)}</a></p>`;
      }).join('');
      return `<section>
        <h2>Attempt ${index + 1} <span class="badge ${status}">${escapeHtml(status)}</span></h2>
        <p class="muted">Duration: ${attempt.duration} ms · Retry index: ${attempt.retry}</p>
        ${attempt.error ? `<h3>Failure details</h3><pre class="error">${escapeHtml(attempt.error)}</pre>` : ''}
        <h3>Steps</h3>
        ${renderSteps(attempt.steps)}
        ${attachments}
      </section>`;
    }).join('');

    return reportPage(test.title, `
      <p><a href="index.html">← Back to test summary</a></p>
      <h1>${escapeHtml(test.title)}</h1>
      <p><span class="badge ${this.displayStatus(test.outcome)}">${escapeHtml(this.displayStatus(test.outcome))}</span></p>
      <p class="muted">${escapeHtml(test.titlePath.join(' › '))}</p>
      <p class="muted">${escapeHtml(test.file)}:${test.line} · Project: ${escapeHtml(test.project)}</p>
      ${attempts}`);
  };
}
