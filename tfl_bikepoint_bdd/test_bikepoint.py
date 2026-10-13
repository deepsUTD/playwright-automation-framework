import json
from urllib.parse import quote

from pytest_bdd import given, scenarios, then, when
from selenium.webdriver.common.by import By

BIKEPOINTS_URL = "https://api.tfl.gov.uk/BikePoint"


scenarios("features/bikepoint.feature")


def response_json(driver):
    return json.loads(driver.find_element(By.TAG_NAME, "body").text)


def fetch_api_path(driver, path):
    return driver.execute_async_script(
        """
        const path = arguments[0];
        const done = arguments[arguments.length - 1];
        fetch(path)
          .then(async (response) => done({ status: response.status, body: await response.text() }))
          .catch((error) => done({ error: String(error) }));
        """,
        path,
    )


@given("the TfL BikePoint collection is loaded")
def load_bikepoint_collection(driver, scenario_state):
    driver.get(BIKEPOINTS_URL)
    bikepoints = response_json(driver)
    assert isinstance(bikepoints, list) and bikepoints
    scenario_state["selected"] = bikepoints[0]


@when("I request the selected BikePoint by ID")
def request_selected_bikepoint(driver, scenario_state):
    selected = scenario_state["selected"]
    driver.get(f"{BIKEPOINTS_URL}/{quote(selected['id'], safe='')}")
    scenario_state["details"] = response_json(driver)


@then("its location matches and availability counts are non-negative integers")
def verify_bikepoint_details(scenario_state):
    selected = scenario_state["selected"]
    details = scenario_state["details"]

    assert details["id"] == selected["id"]
    assert details["commonName"] == selected["commonName"]
    assert details["placeType"] == "BikePoint"

    properties = {
        item["key"].lower(): item["value"]
        for item in details.get("additionalProperties", [])
    }
    counts = {
        "NbBikes": properties.get("nbbikes"),
        "NbDocks": properties.get("nbdocks"),
        "NbEmptyDocks/NbSpaces": properties.get("nbemptydocks") or properties.get("nbspaces"),
    }

    for name, value in counts.items():
        assert value is not None, f"{details['id']} is missing {name}"
        count = int(value)
        assert count >= 0, f"{name} must not be negative"


@given("the TfL BikePoint API is ready")
def open_api_origin(driver):
    driver.get(BIKEPOINTS_URL)


@when("I request all BikePoint locations")
def request_all_bikepoints(driver, scenario_state):
    scenario_state["response"] = fetch_api_path(driver, "/BikePoint")


@then("the API returns HTTP 200 with a non-empty collection")
def verify_collection_success(scenario_state):
    response = scenario_state["response"]
    assert response["status"] == 200
    bikepoints = json.loads(response["body"])
    assert isinstance(bikepoints, list) and bikepoints


@when("I search BikePoints with an empty query")
def search_with_empty_query(driver, scenario_state):
    scenario_state["response"] = fetch_api_path(driver, "/BikePoint/Search?query=")


@then("the API returns HTTP 400 with a required-query error")
def verify_empty_query_error(scenario_state):
    response = scenario_state["response"]
    assert response["status"] == 400
    error = json.loads(response["body"])
    assert error["httpStatusCode"] == 400
    assert "A value is required" in error["message"]


@when("I request an unknown BikePoint ID")
def request_unknown_bikepoint(driver, scenario_state):
    scenario_state["response"] = fetch_api_path(
        driver,
        "/BikePoint/BikePoints_DOES_NOT_EXIST_987654321",
    )


@then("the API returns HTTP 404 with a not-found error")
def verify_unknown_bikepoint_error(scenario_state):
    response = scenario_state["response"]
    assert response["status"] == 404
    error = json.loads(response["body"])
    assert error["httpStatusCode"] == 404
    assert error["httpStatus"] == "NotFound"
