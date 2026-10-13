import pytest

from form_helpers import fill_form
from ssn_data import valid_format_ssns


@pytest.mark.parametrize("ssn", valid_format_ssns, ids=lambda ssn: ssn)
def test_valid_format_ssns_are_accepted(driver, compare_form_screenshots, ssn):
    form, ssn_field = fill_form(driver, ssn)
    ssn_is_valid = driver.execute_script("return arguments[0].checkValidity();", ssn_field)
    form_is_valid = driver.execute_script("return arguments[0].checkValidity();", form)

    assert ssn_is_valid, f"SSN field rejected format-only test value {ssn}"
    assert form_is_valid, f"Form is invalid with format-only test value {ssn}"
    compare_form_screenshots(driver, ssn_field, f"valid:{ssn}")
