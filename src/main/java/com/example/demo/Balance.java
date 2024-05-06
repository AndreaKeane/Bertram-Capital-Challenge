package com.example.demo;

import lombok.Data;

@Data
public class Balance {

    // These hold state and get mutated for purchases and payments
    private double amountSpent = 0.00; // Dollar value of beverages ordered
    private double amountPaid = 0.00;  // Dollar value of bills paid (excl tax, tip)

    public double owes() {
        // Spent $30, Paid $20 >> Amount Owed $30 - $20 >> $10
        // Spent $30, Paid $50 >> Amount Owed $30 - $50 >> -$20
        return Math.round(amountSpent - amountPaid);
    }

    public void spend(double cost) {
        if (cost < 0) {
            throw new NumberFormatException("Negative costs are not allowed.");
        }
        this.amountSpent = this.amountSpent + cost;
    }

    public void pay(double cost) {
        if (cost < 0) {
            throw new NumberFormatException("Negative payments are not allowed.");
        }
        this.amountPaid = this.amountPaid + cost;
    }

    protected void resetBalance() {
        this.amountSpent = 0.00;
        this.amountPaid = 0.00;
    }


}
