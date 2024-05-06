package com.andrea.tabs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/* Person Unit Tests */
public class TestPerson {

    @Test
    void testExpectedCase() {

        Person person = new Person("Testing");

        assertEquals(0.00, person.owes());

        person.getBalance().spend(1.00);
        person.spend(2.00);
        person.spend(3.00);

        assertEquals(6.00, person.owes());

        person.pay(30.00);

        assertEquals(-24.00, person.owes());
    }

    @Test
    void testInvalidBeverageCost() {

        Person person = new Person("Testing");

        Exception exception = assertThrows(NumberFormatException.class, () -> {
            person.spend(-1.00);
        });

        assertTrue(exception.getMessage().contains("Negative costs are not allowed."));
    }

    @Test
    void testInvalidPayABill() {

        Person person = new Person("Testing");

        Exception exception = assertThrows(NumberFormatException.class, () -> {
            person.pay(-100.00);
        });

        assertTrue(exception.getMessage().contains("Negative payments are not allowed."));
    }

}
