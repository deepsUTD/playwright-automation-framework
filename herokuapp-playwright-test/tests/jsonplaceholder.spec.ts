import { expect } from '@playwright/test';
import { navigateTo } from './support/navigation';
import { test } from './support/test';

const apiUrl = 'https://jsonplaceholder.typicode.com';
const axiosScriptUrl = 'https://cdn.jsdelivr.net/npm/axios@1.7.9/dist/axios.min.js';

interface ApiResponse {
  status: number;
  data: unknown;
}

interface AxiosRequestConfig {
  method: string;
  url: string;
  data?: Record<string, unknown>;
}

/** Adds the same Axios browser client used by the Selenium API journeys. */
async function loadAxios(page: import('@playwright/test').Page): Promise<void> {
  await navigateTo(page, `${apiUrl}/posts/1`);
  await page.addScriptTag({ url: axiosScriptUrl });
}

/** Sends an Axios request from the browser and returns its status and response body. */
async function axiosRequest(page: import('@playwright/test').Page, config: {
  method: string;
  url: string;
  data?: Record<string, unknown>;
}): Promise<ApiResponse> {
  return page.evaluate(async requestConfig => {
    const browserWindow = window as typeof window & {
      axios: (options: AxiosRequestConfig & { validateStatus: () => boolean }) =>
        Promise<{ status: number; data: unknown }>;
    };
    const response = await browserWindow.axios({
      ...requestConfig,
      validateStatus: () => true,
    });
    return { status: response.status, data: response.data };
  }, config);
}

test.describe('JsonPlaceholderAxiosTest', () => {
  test('getListReturnsAllPosts: GET returns the complete list of posts', async ({ page }) => {
    await loadAxios(page);
    const response = await axiosRequest(page, { method: 'get', url: `${apiUrl}/posts` });
    expect(response.status).toBe(200);
    expect(response.data).toHaveLength(100);
  });

  test('getSinglePostReturnsExpectedResource: GET returns the requested post', async ({ page }) => {
    await loadAxios(page);
    const response = await axiosRequest(page, { method: 'get', url: `${apiUrl}/posts/1` });
    expect(response.status).toBe(200);
    expect(response.data).toMatchObject({ id: 1, userId: 1 });
    expect((response.data as { title: string }).title).toBeTruthy();
  });

  test('getUnknownPostReturnsNotFound: GET for an unknown post returns 404', async ({ page }) => {
    await loadAxios(page);
    const response = await axiosRequest(page, { method: 'get', url: `${apiUrl}/posts/9999` });
    expect(response.status).toBe(404);
  });

  test('postCreatesResource: POST returns the created post data', async ({ page }) => {
    await loadAxios(page);
    const newPost = { title: 'foo', body: 'bar', userId: 1 };
    const response = await axiosRequest(page, { method: 'post', url: `${apiUrl}/posts`, data: newPost });
    expect(response.status).toBe(201);
    expect(response.data).toMatchObject({ ...newPost, id: 101 });
  });

  test('putReplacesResource: PUT returns the replacement post data', async ({ page }) => {
    await loadAxios(page);
    const updatedPost = { id: 1, title: 'updated title', body: 'updated body', userId: 1 };
    const response = await axiosRequest(page, { method: 'put', url: `${apiUrl}/posts/1`, data: updatedPost });
    expect(response.status).toBe(200);
    expect(response.data).toEqual(updatedPost);
  });

  test('deleteRemovesResource: DELETE returns an empty object', async ({ page }) => {
    await loadAxios(page);
    const response = await axiosRequest(page, { method: 'delete', url: `${apiUrl}/posts/1` });
    expect(response.status).toBe(200);
    expect(response.data).toEqual({});
  });
});
