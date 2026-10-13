from io import BytesIO

import pytest
from PIL import Image, ImageChops, ImageStat
from selenium import webdriver
from selenium.common.exceptions import WebDriverException


@pytest.fixture(scope="module", params=("chrome", "firefox", "edge"), ids=("chrome", "firefox", "edge"))
def driver(request):
    browser_name = request.param
    browser = None

    try:
        if browser_name == "chrome":
            options = webdriver.ChromeOptions()
            options.add_argument("--headless=new")
            browser = webdriver.Chrome(options=options)
        elif browser_name == "firefox":
            options = webdriver.FirefoxOptions()
            options.add_argument("-headless")
            browser = webdriver.Firefox(options=options)
        else:
            options = webdriver.EdgeOptions()
            options.add_argument("--headless=new")
            browser = webdriver.Edge(options=options)
        browser.set_window_size(1280, 1000)
        browser._ssn_browser_name = browser_name
    except WebDriverException as error:
        pytest.skip(f"{browser_name} is unavailable: {error.msg}")

    try:
        yield browser
    finally:
        browser.quit()


@pytest.fixture(scope="session")
def compare_form_screenshots():
    screenshots_by_case = {}

    def capture(driver, ssn_field, case_name):
        driver.execute_script(
            "arguments[0].value = '123-45-6789'; document.activeElement.blur();",
            ssn_field,
        )
        screenshot = Image.open(BytesIO(driver.get_screenshot_as_png())).convert("RGB")
        screenshots_by_case.setdefault(case_name, {})[driver._ssn_browser_name] = screenshot

    yield capture

    for case_name, browser_screenshots in screenshots_by_case.items():
        if len(browser_screenshots) < 2:
            continue

        reference_browser, reference = next(iter(browser_screenshots.items()))
        for browser_name, screenshot in browser_screenshots.items():
            if browser_name == reference_browser:
                continue
            if screenshot.size != reference.size:
                pytest.fail(
                    f"{case_name}: {browser_name} screenshot size {screenshot.size} "
                    f"does not match {reference_browser} size {reference.size}"
                )

            difference = ImageChops.difference(reference, screenshot).convert("L")
            changed_pixels = difference.point(lambda pixel: 255 if pixel > 32 else 0)
            changed_ratio = ImageStat.Stat(changed_pixels).sum[0] / (255 * reference.width * reference.height)
            assert changed_ratio <= 0.08, (
                f"{case_name}: {browser_name} screenshot differs from {reference_browser} "
                f"in {changed_ratio:.1%} of pixels"
            )
