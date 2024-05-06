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

/* PAYER CONTROLLER INTEGRATION TESTS */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PayerControllerTests {

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
    public void whoPaysDayCountDay1() {
        // Who pays on Day 1 - Using the Day Count endpoint (not Dates)
        // Expected: PersonC always pays on day 1 because they have the highest owes() = spent ($3) - paid ($0)
        setupTabContext(makeTestTabRequest());

        ResponseEntity<PayerResponse> response = template.getForEntity("/tab/test/day/1", PayerResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("personC");
        assertThat(response.getBody().getAmount()).isEqualTo(6.00);
    }

    @Test
    public void whoPaysDayCountBreakeven() {
        // Who pays on Day 6/Breakeven - Using the Day Count endpoint (not Dates)
        // Expected: After paying for Day 6 - Everyone's payments should break even. Assert that no one has an outstanding balance
        // Note: We are not being opinionated about the order in which everyone pays, only that we reach "fair" after the expected number of iterations
        setupTabContext(makeTestTabRequest());

        ResponseEntity<PayerResponse> response = template.getForEntity("/tab/test/day/6", PayerResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        for (PersonResponse r : response.getBody().getDetails()) {
            assertThat(r.getOwes()).isEqualTo(0);
        }
    }

    @Test
    public void whoPaysToday() {
        // Who pays on Day 1 - Using the Dates logic
        // Expected: PersonC always pays on day 1 because they have the highest owes() = spent ($3) - paid ($0)
        setupTabContext(makeTestTabRequest());

        ResponseEntity<PayerResponse> response = template.getForEntity("/tab/test/today", PayerResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("personC");
        assertThat(response.getBody().getAmount()).isEqualTo(6.00);
    }

    @Test
    public void whoPaysTodayBreakeven() {
        // Who pays on Day 6/Breakeven - Using the Dates logic
        // After paying for Day 6 - Everyone's payments should break even. Assert that no one has an outstanding balance
        // Note: We are not being opinionated about the order in which everyone pays, only that we reach "fair" after the expected number of iterations
        setupTabContext(makeTestTabRequest(6));

        ResponseEntity<PayerResponse> res2 = template.getForEntity("/tab/test/today", PayerResponse.class);
        assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.OK);
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
