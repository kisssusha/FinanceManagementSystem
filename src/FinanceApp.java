import entities.User;
import entities.Wallet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FinanceApp {
    public static final String FILE_OUTPUT_PATH = "userdata/";
    private static final Map<String, User> users = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadAllUsers();
        while (true) {
            System.out.println("Enter 'register', 'login' or 'exit':");
            String command = scanner.nextLine();
            if (command.equalsIgnoreCase("exit")) {
                saveAllUsers();
                System.out.println("Exiting application.");
                break;
            }
            if (command.equalsIgnoreCase("register")) {
                register();
            } else if (command.equalsIgnoreCase("login")) {
                User currentUser = login();
                if (currentUser != null) {
                    userMenu(currentUser);
                }
            }
        }
    }

    private static void register() {
        System.out.println("Enter login:");
        String login = scanner.nextLine();
        if (users.containsKey(login)) {
            System.out.println("A user with this login already exists.");
            return;
        }
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        User user = new User(login, password);
        users.put(login, user);
        System.out.println("Registration successful.");
    }

    private static User login() {
        System.out.println("Login:");
        String login = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();
        User user = users.get(login);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful.");
            return user;
        }
        System.out.println("Incorrect login or password.");
        return null;
    }

    private static void userMenu(User user) {
        while (true) {
            System.out.println("Commands: add_income, add_expense, set_budget, stats, conversion, exit");
            String cmd = scanner.nextLine();
            if (cmd.equalsIgnoreCase("exit")) break;
            switch (cmd.toLowerCase()) {
                case "add_income":
                    addIncome(user);
                    break;
                case "add_expense":
                    addExpense(user);
                    break;
                case "set_budget":
                    setBudget(user);
                    break;
                case "stats":
                    showStats(user);
                    break;
                case "conversion":
                    currencyConversion();
                    break;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private static void addIncome(User user) {
        System.out.println("Enter income category:");
        String category = scanner.nextLine();
        System.out.println("Enter income amount:");
        double amount = Double.parseDouble(scanner.nextLine());
        user.getWallet().addIncome(category, amount);
        System.out.println("Income added.");
    }

    private static void addExpense(User user) {
        System.out.println("Enter expense category:");
        String category = scanner.nextLine();
        System.out.println("Enter expense amount:");
        double amount = Double.parseDouble(scanner.nextLine());
        user.getWallet().addExpense(category, amount);
        System.out.println("Expense added.");
    }

    private static void setBudget(User user) {
        Wallet wallet = user.getWallet();
        System.out.println("Enter category for budget:");
        String category = scanner.nextLine();
        System.out.println("Enter budget amount:");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect amount. Please try again.");
            return;
        }
        wallet.setBudget(category, amount);
        System.out.println("Budget set for category \"" + category + "\": " + amount);
    }

    private static void showStats(User user) {
        Wallet wallet = user.getWallet();

        System.out.println("Total income: " + wallet.getTotalIncome());

        System.out.println("Incomes by category:");
        Map<String, Double> incomesByCat = wallet.getIncomesByCategory();
        for (Map.Entry<String, Double> entry : incomesByCat.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Total expenses: " + wallet.getTotalExpense());

        System.out.println("Budget by categories:");
        Map<String, Double> budgets = wallet.getBudgets();
        Map<String, Double> expensesByCat = wallet.getExpensesByCategory();
        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            String category = entry.getKey();
            double budgetAmount = entry.getValue();
            double spent = expensesByCat.getOrDefault(category, 0.0);
            double remaining = budgetAmount - spent;
            System.out.println(category + ": " + budgetAmount + ", Remaining budget: " + remaining);
        }
    }

    private static void currencyConversion() {
        System.out.println("Enter source currency (e.g., USD, EUR, RUB):");
        String fromCurrency = scanner.nextLine().toUpperCase();
        System.out.println("Enter target currency:");
        String toCurrency = scanner.nextLine().toUpperCase();
        System.out.println("Enter amount to convert:");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect amount.");
            return;
        }

        try {
            double result = CurrencyConverter.convert(fromCurrency, toCurrency, amount);
            System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void saveAllUsers() {
        for (User user : users.values()) {
            saveUserData(user);
        }
    }

    private static void loadAllUsers() {
        File currentDir = new File(FILE_OUTPUT_PATH);
        File[] userFiles = currentDir.listFiles((dir, name) -> name.endsWith(".dat"));
        if (userFiles != null) {
            for (File file : userFiles) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    User user = (User) ois.readObject();
                    users.put(user.getLogin(), user);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error loading data from file " + file.getName());
                }
            }
        }
    }

    private static void saveUserData(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_OUTPUT_PATH + user.getLogin() + ".dat"))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.out.println("Error saving data for user " + user.getLogin());
        }
    }
}
