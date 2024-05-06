package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/* TAB CONTROLLER INTEGRATION TESTS */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TabControllerTests {

    @Autowired
    private TestRestTemplate template;

    // Reset the Tabs cache between each test
    @Autowired
    private TabService tabService;

    @BeforeEach
    public void resetContext() {
        tabService.resetTabsMap();
    }

    @Test
    public void getRoot() {
        // Our root doesn't do much, so this is a mostly canary for application startup success
        ResponseEntity<String> response = this.template.getForEntity("/", String.class);
        assertThat(response.getBody()).isEqualTo("Welcome to the Who Buys Coffee App!");
    }

    @Test
    public void postTab() {
        TabRequest req = makeTestTabRequest();
        ResponseEntity<TabResponse> res = this.template.postForEntity("/tab", req, TabResponse.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getItems()).isEqualTo(req.getItems());
    }

    @Test
    public void postTabDuplicate() {
        TabRequest req = makeTestTabRequest();
        ResponseEntity<TabResponse> res1 = this.template.postForEntity("/tab", req, TabResponse.class);
        assertThat(res1.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<TabResponse> res2 = this.template.postForEntity("/tab", req, TabResponse.class);
        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    public void putTab() {
        // Create a Tab
        setupTabContext(makeTestTabRequest());

        // Update the Tab
        Map<String, Double> items = testItems();
        items.put("newitem", 4.00);
        TabRequest req = new TabRequest("test", LocalDate.now(), items);
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
        return new TabRequest("test", LocalDate.now(), testItems());
    }

    private TabRequest makeTestTabRequest(Integer dayDelta) {
        // Generate startDate by subtracting the specified number of days from today
        // The (-1) accounts for the TabService being inclusive of both start and end dates.
        // dayDelta = 3 should give us today/Friday -> -1/Thursday -> -2/Wednesday
        LocalDate startDate = LocalDate.now().minusDays(dayDelta - 1);

        // Use the artificial date to generate a TabRequest
        return new TabRequest("test", startDate, testItems());
    }

    private Map<String, Double> testItems() {
        Map<String, Double> items = new HashMap<>(4);
        items.put("personA", 1.00);
        items.put("personB", 2.00);
        items.put("personC", 3.00);
        return items;
    }

    private void setupTabContext(TabRequest req) {
        ResponseEntity<TabResponse> res = this.template.postForEntity("/tab", req, TabResponse.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
