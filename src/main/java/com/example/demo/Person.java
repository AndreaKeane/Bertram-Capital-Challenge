package com.example.demo;

import lombok.Value;

@Value
public class Person {

    // Must be final bc of hash/equals usage¬
    private String name;
    // When a new person is instantiated, add a $0 balance object
    private Balance balance = new Balance();

    // Allows us to use person.spend() which reads a little easier than person.getBalance().spend()
    public void spend(Double cost) {
        this.getBalance().spend(cost);
    }

    // Allows us to use person.pay() which reads a little easier than person.getBalance().pay()
    public void pay(Double cost) {
        this.getBalance().pay(cost);
    }

    // Allows us to use person.owes() which reads a little easier than person.getBalance().owes()
    public Double owes() {
        return this.getBalance().owes();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", amount_spent=" + getBalance().getAmountSpent() +
                ", amount_paid=" + getBalance().getAmountPaid() +
                ", amount_owed=" + owes() +
                '}';
    }
}
