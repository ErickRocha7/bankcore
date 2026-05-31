package repository;

import domain.account.Account;
import domain.customer.Customer;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BankData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Account> accounts = new HashMap<>();
    private Map<String, Customer> customers = new HashMap<>();

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, Account> accounts) {
        this.accounts = accounts;
    }

    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Map<String, Customer> customers) {
        this.customers = customers;
    }
}