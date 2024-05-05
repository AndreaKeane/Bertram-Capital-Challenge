package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TabControllerIT {

    // Tab Controller - Integration Tests
    @Autowired
    private TestRestTemplate template;

    // Reset in-memory context between tests
    @Autowired
    private TabService tabService;

    @BeforeEach
    public void setup() {
        tabService.resetTabsMap();
        // TODO: Try @DirtiesContext annotation: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/annotation/D
    }

    @Test
    public void getRoot() {
        // Our root doesn't do much, so this is a canary for application startup success
        ResponseEntity<String> response = this.template.getForEntity("/", String.class);
        assertThat(response.getBody()).isEqualTo("Welcome to the Who Buys Coffee App!");
    }

    @Test
    public void whoPaysDayCountDay1() {
        // Who pays on Day 1 - Using the Day Count endpoint (not Dates)
        // Expected: PersonC always pays on day 1 because they have the highest owes() = spent ($3) - paid ($0)
        setupTabContext(makeTestTabRequest());

        ResponseEntity<TabPayerResponse> response = template.getForEntity("/tab/test/day/1", TabPayerResponse.class);
        assertThat(response.getBody().getName()).isEqualTo("personC");
        assertThat(response.getBody().getAmount()).isEqualTo(6.00);
    }

    @Test
    public void whoPaysDayCountBreakeven() {
        // Who pays on Day 6/Breakeven - Using the Day Count endpoint (not Dates)
        // Expected: After paying for Day 6 - Everyone's payments should break even. Assert that no one has an outstanding balance
        // Note: We are not being opinionated about the order in which everyone pays, only that we reach "fair" after the expected number of iterations
        setupTabContext(makeTestTabRequest());

        ResponseEntity<TabPayerResponse> response = template.getForEntity("/tab/test/day/6", TabPayerResponse.class);
        for (PersonResponse r : response.getBody().getDetails()) {
            assertThat(r.getOwes()).isEqualTo(0);
        }
    }

    @Test
    public void whoPaysToday() {
        // Who pays on Day 1 - Using the Dates logic
        // Expected: PersonC always pays on day 1 because they have the highest owes() = spent ($3) - paid ($0)
        setupTabContext(makeTestTabRequest());

        ResponseEntity<TabPayerResponse> response = template.getForEntity("/tab/test/today/", TabPayerResponse.class);
        assertThat(response.getBody().getName()).isEqualTo("personC");
        assertThat(response.getBody().getAmount()).isEqualTo(6.00);
    }

    @Test
    public void whoPaysTodayBreakeven() {
        // Who pays on Day 6/Breakeven - Using the Dates logic
        // After paying for Day 6 - Everyone's payments should break even. Assert that no one has an outstanding balance
        // Note: We are not being opinionated about the order in which everyone pays, only that we reach "fair" after the expected number of iterations
        setupTabContext(makeTestTabRequest(6));

        ResponseEntity<TabPayerResponse> res2 = template.getForEntity("/tab/test/today/", TabPayerResponse.class);
        for (PersonResponse r : res2.getBody().getDetails()) {
            assertThat(r.getOwes()).isEqualTo(0);
        }
    }

    @Test
    public void getTestTabSchedule() {
        // Generate a payment schedule for N number of days
        // I chose 12 days, which is 2 times through the rotation
        // Expect: The schedule contains results for 12 days
        // Expect: PersonC pays on Days 1 and 7, which are the "top" of the rotation
        setupTabContext(makeTestTabRequest());

        ResponseEntity<TabScheduleResponse> response = template.getForEntity("/tab/test/schedule/12", TabScheduleResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<Integer, String> schedule = response.getBody().getSchedule();
        assertThat(schedule.size()).isEqualTo(12);
        assertThat(schedule.get(1)).isEqualTo("personC");
        assertThat(schedule.get(7)).isEqualTo("personC");
    }


    /* TAB SERVICE CRUD INTEGRATION TESTS --------------------------------------------------------------------------- */
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
