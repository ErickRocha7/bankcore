package domain.account;

public class Account {
    private String number;
    private double balance;

    public Account(String number, double initialBalance) {
        this.number = number;
        this.balance = initialBalance;
    }

    public void deposit(double amount) { ... }

    public void withdraw(double amount) { ... }
    // ...
}