package com.example.demo;

import org.junit.jupiter.api.Test;

import javax.management.InvalidAttributeValueException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/* Tab Unit Tests */
public class TestTab {

    Map<String, Double> testItems = TabRequest.testItems();

    @Test
    void testExpectedCase() throws InvalidAttributeValueException {
        // This test is doing a lot in a single test, but I think seeing the expected workflow is helpful documentation.

        // Make a new Tab
        Tab tab = new TabRequest("test", null, testItems).toTab();
        assertEquals(6.00, tab.calculateTotal());

        // Nothing had happened yet - everyone's spend and paid amounts should be 0
        for (Person p : tab.items().keySet()) {
            assertEquals(0, p.getBalance().getAmountSpent());
            assertEquals(0, p.getBalance().getAmountPaid());
        }

        // Submit orders. Everyone's spent amounts should increment to a non-zero value.
        tab.submitOrders();
        for (Person p : tab.items().keySet()) {
            assertNotEquals(0, p.getBalance().getAmountSpent());
            assertEquals(0, p.getBalance().getAmountPaid()); // No one has paid yet
        }

        // Submit payment. Person C's payment and owes amounts should increment
        Person payer = tab.payBill();
        assertEquals("personC", payer.getName());
        assertEquals(6.00, payer.getBalance().getAmountPaid());
        assertEquals(-3.00, payer.owes());

    }

}
