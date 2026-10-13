import pytest
from selenium import webdriver
from selenium.common.exceptions import WebDriverException


@pytest.fixture
def scenario_state():
    return {}


@pytest.fixture(params=("chrome", "firefox", "edge"), ids=("chrome", "firefox", "edge"))
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
    except WebDriverException as error:
        pytest.skip(f"{browser_name} is unavailable: {error.msg}")

    try:
        yield browser
    finally:
        browser.quit()
