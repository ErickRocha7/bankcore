package repository;

import collections.BinarySearchTree;
import domain.account.Account;
import domain.account.SavingsAccount;
import exceptions.AccountNotFoundException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountRepository {

    private final BinarySearchTree<Account> accountTree;

    public AccountRepository() {
        accountTree = new BinarySearchTree<>();
    }

    public void add(Account account) {
        if (account == null)
            throw new IllegalArgumentException("Conta não pode ser nula.");
        if (accountTree.contains(account))
            throw new IllegalArgumentException("Conta " + account.getAccountNumber() + " já existe.");
        accountTree.insert(account);
    }

    public Account findById(String accountNumber) throws AccountNotFoundException {
        Account template = createDummyAccount(accountNumber);
        Account found = accountTree.search(template);
        if (found == null)
            throw new AccountNotFoundException("Conta " + accountNumber + " não encontrada.");
        return found;
    }

    public void update(Account account) throws AccountNotFoundException {
        if (account == null)
            throw new IllegalArgumentException("Conta não pode ser nula.");
        if (!accountTree.contains(account))
            throw new AccountNotFoundException(
                    "Conta " + account.getAccountNumber() + " não encontrada para atualização.");
    }

    public void delete(String accountNumber) throws AccountNotFoundException {
        Account dummy = createDummyAccount(accountNumber);
        if (!accountTree.remove(dummy))
            throw new AccountNotFoundException("Conta " + accountNumber + " não encontrada para remoção.");
    }

    public List<Account> findAll() {
        return accountTree.getAll();
    }

    public boolean exists(String accountNumber) {
        Account dummy = createDummyAccount(accountNumber);
        return accountTree.contains(dummy);
    }

    public int count() {
        return accountTree.size();
    }

    public void replaceAll(Map<String, Account> accountsMap) {
        accountTree.clear();
        if (accountsMap != null) {
            for (Account acc : accountsMap.values()) {
                accountTree.insert(acc);
            }
        }
    }

    public Map<String, Account> getMap() {
        Map<String, Account> map = new HashMap<>();
        for (Account acc : accountTree.getAll()) {
            map.put(acc.getAccountNumber(), acc);
        }
        return map;
    }

    // Cria uma conta "dummy" cujo único propósito é ser comparada pelo número da
    // conta.
    // Usamos SavingsAccount porque é uma subclasse concreta de Account.
    private Account createDummyAccount(String accountNumber) {
        return new SavingsAccount("dummy", BigDecimal.ZERO, BigDecimal.ZERO) {
            @Override
            public String getAccountNumber() {
                return accountNumber;
            }
        };
    }
}