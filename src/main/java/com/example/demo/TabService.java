package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TabService {

    static Logger log = LoggerFactory.getLogger(TabService.class);

    // TODO: Should I use a cache pattern? : https://www.baeldung.com/spring-dirtiescontext
    // In-Memory Tab Storage - Allow CRUD operations for multiple Tab objects
    private static final Map<String, Tab> TABS = new ConcurrentHashMap<>(4);

    // Initialize TABS map with a Demo Tab
    @PostConstruct
    private static void loadDemoTab() throws IOException {
        Tab demoTab = TabRequest.tabFromJson("src/main/resources/demo_tab.json").toTab();
        TABS.putIfAbsent(demoTab.id(), demoTab);
    }

    // CREATE a new Tab instance and save it to our in-memory `tabs` map
    protected static Tab createTab(Tab tab) throws InstanceAlreadyExistsException {
        Tab result = TABS.putIfAbsent(tab.id(), tab);

        // TABS.putIfAbsent() returns a result if the key already exists in the map.
        // If we hit that case, raise an Exception and later convert it to a meaningful HTTP Response
        if (result != null) {
            throw new InstanceAlreadyExistsException("A Tab with ID %s already exists.".formatted(tab.id()));
        }
        return tab;
    }

    // READ a Tab instance from our in-memory `tabs` map
    protected static Tab readTab(String id) throws InstanceNotFoundException {
        Tab tab = TABS.get(id);
        if (tab == null) {
            throw new InstanceNotFoundException("A Tab with ID %s was not found.".formatted(id));
        }
        return tab;
    }

    // UPDATE a Tab instance in our in-memory `tabs` map
    protected static Tab updateTab(Tab tab) {
        TABS.replace(tab.id(), tab);
        return tab;
    }

    // DELETE a Tab instance in our in-memory `tabs` map
    protected static Tab deleteTab(String id) {
        return TABS.remove(id);
    }

    // GET who pays a tab on day <day_number>
    public static PayerResponse getWhoPays(String tabId, Integer dayNumber) throws InstanceNotFoundException {
        Tab tab = readTab(tabId);
        Person payer = findPayer(tab, dayNumber);
        List<PersonResponse> details = PayerResponse.generateDetails(tab);
        return new PayerResponse(payer.getName(), tab.calculateTotal(), details);
    }

    // GET who pays a tab today, based on tab.startDate
    public static PayerResponse getWhoPays(String tabId) throws InstanceNotFoundException {

        // Retrieve the Tab object for the given tabId
        Tab tab = readTab(tabId);
        log.info("Determining who pays today (%s) for Tab %s".formatted(LocalDate.now(), tab));

        // Determine the number of days between today and the Tab's startDate
        int daysSinceStart = Period.between(tab.startDate(), LocalDate.now()).getDays() + 1;
        log.info("Calculated date delta between %s : %s as %s days".formatted(
                tab.startDate(),
                LocalDate.now(),
                daysSinceStart));

        // Use the Tab details and the number of days since startDate to determine who pays today
        Person payer = findPayer(tab, daysSinceStart);

        // Give the user more information about the state
        List<PersonResponse> details = PayerResponse.generateDetails(tab);

        return new PayerResponse(payer.getName(), tab.calculateTotal(), details);
    }

    private static Person findPayer(Tab tab, long dayNumber) {
        Person payer = null;
        for (int i = 0; i < dayNumber; i++) {
            tab.submitOrders();
            payer = tab.payBill();
            log.info("Day %s; Tab Details: %s".formatted((i + 1), tab));
        }
        return payer;
    }

    public static TabScheduleResponse getSchedule(String tabId, Integer scheduleLength) throws InstanceNotFoundException {
        Tab tab = readTab(tabId);
        Map<Integer, String> schedule = new HashMap<>();

        Person payer = null;
        for (int i = 0; i < scheduleLength; i++) {
            tab.submitOrders();
            payer = tab.payBill();
            schedule.put(i + 1, payer.getName());
        }
        return TabScheduleResponse.builder()
                .schedule(schedule)
                .build();
    }

    // Visible For Testing - Compromise so we can reset the tabs map for tests.
    // We will not be able to run tests in parallel with this design
    protected void resetTabsMap() {
        TABS.clear();
    }
}
