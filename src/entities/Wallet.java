package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet implements Serializable {
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Double> budgets = new HashMap<>();

    public void addIncome(String category, double amount) {
        transactions.add(new Income(category, amount));
    }

    public void addExpense(String category, double amount) {
        transactions.add(new Expense(category, amount));
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t instanceof Income)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t instanceof Expense)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getIncomesByCategory() {
        Map<String, Double> incomesByCat = new HashMap<>();
        for (Transaction t : transactions) {
            if (t instanceof Income) {
                incomesByCat.put(
                        t.category,
                        incomesByCat.getOrDefault(t.category, 0.0) + t.getAmount()
                );
            }
        }
        return incomesByCat;
    }

    public Map<String, Double> getExpensesByCategory() {
        Map<String, Double> expensesByCat = new HashMap<>();
        for (Transaction t : transactions) {
            if (t instanceof Expense) {
                expensesByCat.put(
                        t.category,
                        expensesByCat.getOrDefault(t.category, 0.0) + t.getAmount()
                );
            }
        }
        return expensesByCat;
    }

    public Map<String, Double> getBudgets() {
        return budgets;
    }

    public void setBudget(String category, double amount) {
        budgets.put(category, amount);
    }
}
