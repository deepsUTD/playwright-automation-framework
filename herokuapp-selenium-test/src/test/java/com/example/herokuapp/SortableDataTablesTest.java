package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SortableDataTablesTest extends HerokuAppTest {
    @Override protected String path() { return "/tables"; }

    @Override
    protected PageLoadStrategy pageLoadStrategy() {
        return PageLoadStrategy.NORMAL;
    }

    @Override
    protected void waitForUseCase() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
            .withMessage(() -> "Expected four rows in both tables; URL=" + driver.getCurrentUrl()
                + ", table1 rows=" + driver.findElements(By.cssSelector("#table1 tbody tr")).size()
                + ", table2 rows=" + driver.findElements(By.cssSelector("#table2 tbody tr")).size())
            .until(d -> d.findElements(By.cssSelector("#table1 tbody tr")).size() == 4
                && d.findElements(By.cssSelector("#table2 tbody tr")).size() == 4);
    }

    @Test
    void tablesMeetStructureSortingAndDataIntegrityChecks() {
        bothTablesHaveHeadersAndRows();
        assertLastNameSorting("table1");
        assertLastNameSorting("table2");
        sortingPreservesAllRowsInBothTables();
    }

    private void bothTablesHaveHeadersAndRows() {
    assertEquals(2, driver.findElements(By.cssSelector("table")).size());
    for (String tableId : List.of("table1", "table2")) {
        assertEquals(6, driver.findElements(By.cssSelector("#" + tableId + " thead th")).size(),
            tableId + " should have six column headers");
        var rows = driver.findElements(By.cssSelector("#" + tableId + " tbody tr"));
        assertEquals(4, rows.size(), tableId + " should have four data rows");
        assertTrue(rows.stream().allMatch(row -> row.findElements(By.cssSelector("td")).size() == 6),
            tableId + " data rows should have six cells");
    }
    }

    private void sortingPreservesAllRowsInBothTables() {
    for (String tableId : List.of("table1", "table2")) {
        List<String> initialRows = rowValues(tableId);
        WebElement lastNameHeader = lastNameHeader(tableId);
        lastNameHeader.click();
        wait.until(d -> !rowValues(tableId).equals(initialRows));
        assertEquals(initialRows.stream().sorted().toList().size(), rowValues(tableId).size(),
            tableId + " should preserve its row count after sorting");
        assertEquals(initialRows.stream().sorted().toList(), rowValues(tableId).stream().sorted().toList(),
            tableId + " should preserve all rows after sorting");
    }
    }

    private void assertLastNameSorting(String tableId) {
    WebElement lastNameHeader = lastNameHeader(tableId);

    List<String> initialLastNames = lastNames(tableId);
    List<String> expectedAscending = initialLastNames.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    lastNameHeader.click();
    List<String> ascending = wait.until(d -> lastNames(tableId).equals(expectedAscending) ? lastNames(tableId) : null);
    assertEquals(expectedAscending, ascending,
        tableId + " first click should sort last names in ascending order");

    List<String> expectedDescending = expectedAscending.stream()
        .sorted(String.CASE_INSENSITIVE_ORDER.reversed()).toList();
    lastNameHeader.click();
    List<String> descending = wait.until(d -> lastNames(tableId).equals(expectedDescending) ? lastNames(tableId) : null);
    assertEquals(expectedDescending, descending,
        tableId + " second click should sort last names in descending order");
    }

    private WebElement lastNameHeader(String tableId) {
    return driver.findElements(By.cssSelector("#" + tableId + " thead th")).stream()
        .filter(header -> header.getText().trim().equals("Last Name"))
        .findFirst().orElseThrow(() -> new AssertionError("Last Name header should be present"));
    }

    private List<String> rowValues(String tableId) {
    return driver.findElements(By.cssSelector("#" + tableId + " tbody tr"))
        .stream().map(row -> row.getText().trim()).toList();
    }

    private List<String> lastNames(String tableId) {
    return driver.findElements(By.cssSelector("#" + tableId + " tbody tr td:first-child"))
        .stream().map(cell -> cell.getText().trim()).toList();
    }
}
