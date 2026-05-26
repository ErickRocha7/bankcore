package repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import domain.account.Account;
import domain.customer.Customer;

/**
 * Objeto de transferência que agrupa todos os dados do banco para persistência.
 * 
 * Capítulos abordados:
 * 6, 8 – Classes, encapsulamento, composição
 * 16 – Coleções genéricas (Map<String, Account>, Map<String, Customer>)
 * 15 – Serialização (implementa Serializable)
 */
public class BankData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Account> accounts;
    private Map<String, Customer> customers;

    public BankData() {
        accounts = new HashMap<>();
        customers = new HashMap<>();
    }

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