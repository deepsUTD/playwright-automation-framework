import pytest

from form_helpers import fill_form
from ssn_data import invalid_ssns


@pytest.mark.parametrize("ssn", invalid_ssns, ids=lambda ssn: ssn or "empty")
def test_invalid_ssns_are_rejected(driver, compare_form_screenshots, ssn):
    form, ssn_field = fill_form(driver, ssn)
    ssn_is_valid = driver.execute_script("return arguments[0].checkValidity();", ssn_field)
    form_is_valid = driver.execute_script("return arguments[0].checkValidity();", form)

    assert not ssn_is_valid, f"SSN field accepted invalid test value {ssn!r}"
    assert not form_is_valid, f"Form is valid with invalid test value {ssn!r}"
    compare_form_screenshots(driver, ssn_field, f"invalid:{ssn!r}")
