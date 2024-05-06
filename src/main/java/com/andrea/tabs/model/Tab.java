package com.andrea.tabs.model;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Builder
public record Tab(String id, LocalDate startDate, Map<Person, Double> items) {

    public Double calculateTotal() {
        double total = 0.00;
        for (Map.Entry<Person, Double> order : this.items.entrySet()) {
            total = total + order.getValue();
        }
        return total;
    }

    public Person payBill() {
        // Determine who has the largest spent/paid ratio - they will get the bill today
        Person whoPaysToday = this.items.entrySet().iterator().next().getKey(); // Initialize who pays today, doesn't matter who

        for (Map.Entry<Person, Double> item : this.items.entrySet()) {
            Person person = item.getKey();

            if (person.owes() > whoPaysToday.owes()) {
                whoPaysToday = person;
            } else if (Objects.equals(person.owes(), whoPaysToday.owes())) {
                // Occasionally two people can have the same .owes() on the same day. When this happen, we use a
                // deterministic tie-breaker. This is a first-pass stake in the ground, subject to change.
                if (person.getBalance().getAmountSpent() > whoPaysToday.getBalance().getAmountPaid()) {
                    whoPaysToday = person;
                }
            }
        }

        whoPaysToday.getBalance().pay(calculateTotal());
        return whoPaysToday;
    }

    public void submitOrders() {
        // Increment each person's amount spent
        for (Map.Entry<Person, Double> item : this.items().entrySet()) {
            Person person = item.getKey();
            Double cost = item.getValue();
            person.getBalance().spend(cost);
        }
    }

    public void resetTab() {
        for (Person p : this.items.keySet()) {
            p.getBalance().resetBalance();
        }
    }

    @Override
    public String toString() {
        return "Tab{" +
                "items=" + items +
                ", total_price=" + this.calculateTotal() +
                '}';
    }

}
