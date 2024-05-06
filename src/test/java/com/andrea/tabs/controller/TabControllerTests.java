package com.andrea.tabs.controller;

import com.andrea.tabs.response.TabResponse;
import com.andrea.tabs.request.TabRequest;
import com.andrea.tabs.service.TabService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/* TAB CONTROLLER INTEGRATION TESTS */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TabControllerTests {

    /* SETUP */

    @Autowired
    private TestRestTemplate template;

    // Reset the Tabs cache between each test
    @Autowired
    private TabService tabService;

    @BeforeEach
    public void resetContext() {
        tabService.resetTabsMap();
    }

    Map<String, Double> testItems = TabRequest.testItems();

    /* TESTS */

    @Test
    public void postTab() {
        TabRequest req = makeTestTabRequest();
        ResponseEntity<TabResponse> res = postAndAssert(req, HttpStatus.OK);
        assertThat(res.getBody().getItems()).isEqualTo(req.getItems());
    }

    @Test
    public void postMaxTabs() {
        // Make 30 requests to fill the cache
        TabRequest req;
        for (int i = 0; i < 30; i++) {
            req = TabRequest.builder().id("test %s".formatted(i)).startDate(LocalDate.now()).items(testItems).build();
            postAndAssert(req, HttpStatus.OK);
        }

        // Fail on the 31st request
        req = TabRequest.builder().id("test 31").startDate(LocalDate.now()).items(testItems).build();
        postAndAssert(req, HttpStatus.INSUFFICIENT_STORAGE);
    }

    @Test
    public void postTabDuplicate() {
        TabRequest req = makeTestTabRequest();
        postAndAssert(req, HttpStatus.OK);
        postAndAssert(req, HttpStatus.CONFLICT);
    }

    @Test
    public void postTabDuplicateCaseSensitive() {
        TabRequest req1 = TabRequest.builder().id("test").startDate(LocalDate.now()).items(testItems).build();
        postAndAssert(req1, HttpStatus.OK);

        TabRequest req2 = TabRequest.builder().id("TEST").startDate(LocalDate.now()).items(testItems).build();
        postAndAssert(req2, HttpStatus.OK);
    }

    @Test
    public void postTabIdNull() {
        TabRequest req = TabRequest.builder().id(null).startDate(LocalDate.now()).items(testItems).build();
        postAndAssert(req, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void putTab() {
        // Create a Tab
        setupTabContext(makeTestTabRequest());

        // Update the Tab
        Map<String, Double> items = testItems;
        items.put("newitem", 4.00);
        TabRequest req = TabRequest.builder().id("test").startDate(LocalDate.now()).items(items).build();
        this.template.put("/tab", req, TabResponse.class);

        // Read and Verify the Update operation
        ResponseEntity<TabResponse> res2 = this.template.getForEntity("/tab/test", TabResponse.class);
        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res2.getBody().getItems()).isEqualTo(req.getItems());
    }

    @Test
    public void getTab() {
        // Create a Tab
        TabRequest req = makeTestTabRequest();
        setupTabContext(req);

        // Read the Tab
        ResponseEntity<TabResponse> res2 = this.template.getForEntity("/tab/test", TabResponse.class);
        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res2.getBody().getItems()).isEqualTo(req.getItems());
    }

    @Test
    public void deleteTab() {
        // Create a Tab
        setupTabContext(makeTestTabRequest());

        // Delete the Tab
        this.template.delete("/tab/test");

        // Read the Tab
        ResponseEntity<TabResponse> getRes = this.template.getForEntity("/tab/test", TabResponse.class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /* HELPERS ------------------------------------------------------------------------------------------------------ */

    private TabRequest makeTestTabRequest() {
        return TabRequest.builder().id("test").startDate(LocalDate.now()).items(testItems).build();
    }

    private TabRequest makeTestTabRequest(Integer dayDelta) {
        // Generate startDate by subtracting the specified number of days from today
        // The (-1) accounts for the TabService being inclusive of both start and end dates.
        // dayDelta = 3 should give us today/Friday -> -1/Thursday -> -2/Wednesday
        LocalDate startDate = LocalDate.now().minusDays(dayDelta - 1);

        // Use the artificial date to generate a TabRequest
        return TabRequest.builder().id("test").startDate(startDate).items(testItems).build();
    }

    private void setupTabContext(TabRequest req) {
        ResponseEntity<TabResponse> res = this.template.postForEntity("/tab", req, TabResponse.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<TabResponse> postAndAssert(TabRequest request, HttpStatus expectedStatus) {
        ResponseEntity<TabResponse> response = this.template.postForEntity("/tab", request, TabResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        return response;
    }

}
