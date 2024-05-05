package com.example.demo;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;

@Builder
public record Tab(String id, LocalDate startDate, Map<Person, Double> items) {

    protected Double calculateTotal() {
        double total = 0.00;
        for (Map.Entry<Person, Double> order : this.items.entrySet()) {
            total = total + order.getValue();
        }
        return total;
    }

    protected Person payBill() {
        // Determine who has the largest spent/paid ratio - they will get the bill today
        Person whoPaysToday = this.items.entrySet().iterator().next().getKey(); // Initialize who pays today, doesn't matter who

        for (Map.Entry<Person, Double> item : this.items.entrySet()) {
            Person person = item.getKey();

            if (person.owes() > whoPaysToday.owes()) {
                whoPaysToday = person;
            }
        }

        whoPaysToday.getBalance().pay(calculateTotal());
        return whoPaysToday;
    }

    protected void submitOrders() {
        // Increment each person's amount spent
        for (Map.Entry<Person, Double> item : this.items().entrySet()) {
            Person person = item.getKey();
            Double cost = item.getValue();
            person.getBalance().spend(cost);
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
