import { expect } from '@playwright/test';
import { navigateTo } from './support/navigation';
import { test } from './support/test';

test.describe('Herokuapp controls and input examples', () => {
  test.describe('AddRemoveElementsTest', () => {
    test('addButtonCreatesDeleteButtons: clicking add twice creates two delete buttons', async ({ page }) => {
      await navigateTo(page, '/add_remove_elements/');
      const addButton = page.locator("button[onclick='addElement()']");
      await addButton.click();
      await addButton.click();
      await expect(page.locator('.added-manually')).toHaveCount(2);
    });

    test('deleteButtonRemovesOnlyItsElement: deleting the only added element removes it', async ({ page }) => {
      await navigateTo(page, '/add_remove_elements/');
      await page.locator("button[onclick='addElement()']").click();
      await page.locator('.added-manually').click();
      await expect(page.locator('.added-manually')).toHaveCount(0);
    });
  });

  test.describe('CheckboxesTest', () => {
    test('pageHasTwoIndependentCheckboxes: the page has two checkboxes', async ({ page }) => {
      await navigateTo(page, '/checkboxes');
      await expect(page.locator("input[type='checkbox']")).toHaveCount(2);
    });

    test('checkboxesToggleIndependently: changing one checkbox does not change the other', async ({ page }) => {
      await navigateTo(page, '/checkboxes');
      const checkboxes = page.locator("input[type='checkbox']");
      const first = checkboxes.nth(0);
      const second = checkboxes.nth(1);

      await second.uncheck();
      await expect(second).not.toBeChecked();
      await first.check();
      await expect(first).toBeChecked();
      await expect(second).not.toBeChecked();
      await first.uncheck();
      await expect(first).not.toBeChecked();
    });
  });

  test.describe('ContextMenuTest', () => {
    test('rightClickTargetShowsExpectedAlert: right-clicking the target shows the expected dialog', async ({ page }) => {
      await navigateTo(page, '/context_menu');
      const dialogPromise = page.waitForEvent('dialog');
      await page.locator('#hot-spot').click({ button: 'right' });
      const dialog = await dialogPromise;
      expect(dialog.message()).toBe('You selected a context menu');
      await dialog.accept();
    });

    test('targetIsVisibleAndHasInstruction: the context-menu target and heading are visible', async ({ page }) => {
      await navigateTo(page, '/context_menu');
      await expect(page.locator('#hot-spot')).toBeVisible();
      await expect(page.locator('h3')).toContainText('Context Menu');
    });
  });

  test.describe('DropdownTest', () => {
    test('dropdownHasPlaceholderAndTwoOptions: the dropdown starts with a placeholder and has two choices', async ({ page }) => {
      await navigateTo(page, '/dropdown');
      const dropdown = page.locator('#dropdown');
      await expect(dropdown.locator('option')).toHaveCount(3);
      await expect(dropdown).toHaveValue('');
      await expect(dropdown.locator('option').first()).toHaveText('Please select an option');
    });

    test('bothOptionsCanBeSelected: each numbered dropdown option can be selected', async ({ page }) => {
      await navigateTo(page, '/dropdown');
      const dropdown = page.locator('#dropdown');
      for (const value of ['1', '2']) {
        await dropdown.selectOption(value);
        await expect(dropdown).toHaveValue(value);
      }
    });
  });

  test.describe('DynamicControlsTest', () => {
    test('checkboxCanBeRemovedAndAddedBack: the checkbox can be removed and restored', async ({ page }) => {
      await navigateTo(page, '/dynamic_controls');
      const checkbox = page.locator('#checkbox input');
      await page.locator('#checkbox-example button').click();
      await expect(page.locator('#message')).toContainText("It's gone!");
      await expect(checkbox).toHaveCount(0);
      await page.locator('#checkbox-example button').click();
      await expect(page.locator('#checkbox input')).toBeVisible();
    });

    test('inputCanBeEnabledAndDisabled: the input can be enabled and disabled', async ({ page }) => {
      await navigateTo(page, '/dynamic_controls');
      const input = page.locator('#input-example input');
      await expect(input).toBeDisabled();
      await page.locator('#input-example button').click();
      await expect(input).toBeEnabled();
      await page.locator('#input-example button').click();
      await expect(input).toBeDisabled();
    });
  });

  test.describe('HorizontalSliderTest', () => {
    test('sliderAndValueLabelArePresent: the slider and value label are visible', async ({ page }) => {
      await navigateTo(page, '/horizontal_slider');
      await expect(page.locator("input[type='range']")).toBeVisible();
      await expect(page.locator('#range')).toBeVisible();
    });

    test('keyboardChangesSliderValue: pressing the right arrow changes the slider value', async ({ page }) => {
      await navigateTo(page, '/horizontal_slider');
      const slider = page.locator("input[type='range']");
      await slider.click();
      await slider.press('ArrowRight');
      await expect(page.locator('#range')).not.toHaveText('0');
    });
  });

  test.describe('InputsTest', () => {
    test('numberInputAcceptsPositiveAndNegativeValues: the number field accepts signed numbers', async ({ page }) => {
      await navigateTo(page, '/inputs');
      const input = page.locator('input');
      await input.fill('42');
      await expect(input).toHaveValue('42');
      await input.fill('-7');
      await expect(input).toHaveValue('-7');
    });

    test('numberInputSupportsIncrementAndDecrement: arrow keys adjust the numeric value', async ({ page }) => {
      await navigateTo(page, '/inputs');
      const input = page.locator('input');
      await input.fill('5');
      await input.press('ArrowUp');
      await expect(input).toHaveValue('6');
      await input.press('ArrowDown');
      await expect(input).toHaveValue('5');
    });
  });

  test.describe('KeyPressesTest', () => {
    test('typingLettersUpdatesKeyResult: typing letters reports the last pressed key', async ({ page }) => {
      await navigateTo(page, '/key_presses');
      await page.locator('#target').pressSequentially('abc');
      await expect(page.locator('#result')).toContainText('C');
    });

    test('specialKeysAreReported: pressing Escape is reported', async ({ page }) => {
      await navigateTo(page, '/key_presses');
      await page.locator('#target').press('Escape');
      await expect(page.locator('#result')).toContainText('ESC');
    });
  });

  test.describe('DragAndDropTest', () => {
    test('bothDraggableColumnsArePresent: both draggable columns start in the expected order', async ({ page }) => {
      await navigateTo(page, '/drag_and_drop');
      await expect(page.locator('#columns > div')).toHaveCount(2);
      await expect(page.locator('#column-a')).toHaveText('A');
      await expect(page.locator('#column-b')).toHaveText('B');
    });

    test('draggingColumnAOverBChangesTheirLabels: dragging column A onto B swaps their labels', async ({ page }) => {
      await navigateTo(page, '/drag_and_drop');
      await page.locator('#column-a').dragTo(page.locator('#column-b'));
      await expect(page.locator('#column-a')).toHaveText('B');
      await expect(page.locator('#column-b')).toHaveText('A');
    });
  });
});
