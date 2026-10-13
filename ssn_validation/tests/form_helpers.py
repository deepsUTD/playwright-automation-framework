from pathlib import Path

from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select

FORM_PATH = Path(__file__).resolve().parents[1] / "index.html"


def _field_description(driver, element):
    label = driver.execute_script(
        "return Array.from(arguments[0].labels || []).map((item) => item.innerText).join(' ');",
        element,
    )
    attributes = [
        element.get_attribute("name"),
        element.get_attribute("id"),
        element.get_attribute("placeholder"),
        element.get_attribute("aria-label"),
        label,
    ]
    return " ".join(value or "" for value in attributes).lower()


def _text_value(element, description):
    input_type = (element.get_attribute("type") or "text").lower()
    if input_type == "email" or "email" in description:
        value = "qa.test@example.invalid"
    elif input_type == "tel" or "phone" in description:
        value = "5550100100"
    elif input_type == "password" or "password" in description:
        value = "Test!Pass123"
    elif input_type == "url":
        value = "https://example.invalid"
    elif input_type == "date":
        value = "2000-01-01"
    elif input_type in {"number", "range"}:
        value = element.get_attribute("min") or "1"
    elif "first" in description and "name" in description:
        value = "Test"
    elif "last" in description and "name" in description:
        value = "User"
    elif "name" in description:
        value = "Test User"
    elif "address" in description:
        value = "123 Test Street"
    elif "city" in description:
        value = "Testville"
    elif "state" in description:
        value = "CA"
    elif "zip" in description or "postal" in description:
        value = "90210"
    else:
        value = "Synthetic test data"

    max_length = element.get_attribute("maxlength")
    if max_length and int(max_length) >= 0:
        value = value[: int(max_length)]
    return value


def fill_form(driver, ssn):
    if not FORM_PATH.is_file():
        raise FileNotFoundError(f"SSN form not found: {FORM_PATH}")

    driver.get(FORM_PATH.as_uri())
    form = driver.find_element(By.TAG_NAME, "form")
    controls = form.find_elements(By.CSS_SELECTOR, "input, textarea, select")
    ssn_field = None
    handled_radio_groups = set()

    for control in controls:
        if not control.is_displayed() or not control.is_enabled():
            continue

        tag_name = control.tag_name.lower()
        input_type = (control.get_attribute("type") or "text").lower()
        if input_type in {"hidden", "submit", "button", "reset", "image", "file"}:
            continue

        description = _field_description(driver, control)
        if "ssn" in description or "social security" in description:
            control.clear()
            control.send_keys(ssn)
            ssn_field = control
        elif tag_name == "select":
            options = [option for option in Select(control).options if option.get_attribute("value")]
            if options:
                Select(control).select_by_value(options[0].get_attribute("value"))
        elif input_type == "radio":
            group_name = control.get_attribute("name") or control.get_attribute("id") or "radio"
            if group_name not in handled_radio_groups:
                control.click()
                handled_radio_groups.add(group_name)
        elif input_type == "checkbox":
            if control.get_attribute("required") and not control.is_selected():
                control.click()
        else:
            control.clear()
            control.send_keys(_text_value(control, description))

    if ssn_field is None:
        raise AssertionError("Could not find a visible field labeled or named SSN/Social Security")
    return form, ssn_field
