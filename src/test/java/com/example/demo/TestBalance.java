package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class TestBalance {

    @Test
    void testExpectedCase() {

        // Build a new balance POJO, check that everything starts at $0
        Balance balance = new Balance();

        assertThat(balance.getAmountSpent()).isEqualTo(0);
        assertThat(balance.getAmountPaid()).isEqualTo(0);
        assertThat(balance.owes()).isEqualTo(0);

        // Spend money, check that the Spent total is incrementing and the Paid total is still $0
        balance.spend(1.00);
        balance.spend(2.00);
        balance.spend(3.00);

        assertThat(balance.getAmountSpent()).isEqualTo(6.00);
        assertThat(balance.getAmountPaid()).isEqualTo(0);
        assertThat(balance.owes()).isEqualTo(6.00);

        // Pay off the balance with excess, check that the Paid total and the Owes total are math-ing properly
        balance.pay(9.00);

        assertThat(balance.getAmountSpent()).isEqualTo(6.00);
        assertThat(balance.getAmountPaid()).isEqualTo(9.00);
        assertThat(balance.owes()).isEqualTo(-3.00);

        // Reset the balance, check that everything is zero'd
        balance.resetBalance();

        assertThat(balance.getAmountSpent()).isEqualTo(0);
        assertThat(balance.getAmountPaid()).isEqualTo(0);
        assertThat(balance.owes()).isEqualTo(0);

    }

    @Test
    void testNegativeSpend() {

        // Build a new balance POJO, check that everything starts at $0
        Balance balance = new Balance();

        assertThrows(NumberFormatException.class, () -> {
            balance.spend(-10);
        });
        assertThrows(NumberFormatException.class, () -> {
            balance.pay(-10);
        });
    }
}
