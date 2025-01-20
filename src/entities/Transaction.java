package entities;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Transaction implements Serializable {
    protected String category;
    protected double amount;
    protected LocalDate date;

    public Transaction(String category, double amount) {
        this.category = category;
        this.amount = amount;
        this.date = LocalDate.now();
    }

    public double getAmount() {
        return amount;
    }
}
