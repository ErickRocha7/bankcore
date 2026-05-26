package repository;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import domain.account.Account;
import exceptions.AccountNotFoundException;

/**
 * Repositório de contas bancárias.
 * Gerencia uma coleção de contas indexadas pelo número da conta.
 * 
 * Capítulos abordados:
 * 7, 16 – Coleções genéricas (HashMap<String, Account>)
 * 11 – Tratamento de exceções (AccountNotFoundException)
 * 6, 8 – Encapsulamento, métodos de acesso
 */
public class AccountRepository {
    private Map<String, Account> accounts;

    public AccountRepository() {
        accounts = new HashMap<>();
    }

    /**
     * Adiciona uma nova conta ao repositório.
     * 
     * @param account conta a ser adicionada
     * @throws IllegalArgumentException se a conta já existir
     */
    public void add(Account account) {
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException("Conta " + account.getAccountNumber() + " já existe.");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Busca uma conta pelo número.
     * 
     * @param accountNumber número da conta (5 dígitos)
     * @return a conta encontrada
     * @throws AccountNotFoundException se a conta não existir
     */
    public Account findById(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Conta " + accountNumber + " não encontrada.");
        }
        return account;
    }

    /**
     * Atualiza uma conta existente. Substitui a entrada no mapa.
     * 
     * @param account conta com os dados atualizados
     * @throws AccountNotFoundException se a conta não existir
     */
    public void update(Account account) throws AccountNotFoundException {
        if (!accounts.containsKey(account.getAccountNumber())) {
            throw new AccountNotFoundException(
                    "Conta " + account.getAccountNumber() + " não encontrada para atualização.");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Remove uma conta pelo número.
     * 
     * @param accountNumber número da conta
     * @throws AccountNotFoundException se a conta não existir
     */
    public void delete(String accountNumber) throws AccountNotFoundException {
        if (accounts.remove(accountNumber) == null) {
            throw new AccountNotFoundException("Conta " + accountNumber + " não encontrada para remoção.");
        }
    }

    /**
     * Lista todas as contas.
     * 
     * @return lista de contas
     */
    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    /**
     * Verifica se uma conta existe.
     * 
     * @param accountNumber número da conta
     * @return true se existir
     */
    public boolean exists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    /**
     * Retorna o número total de contas.
     * 
     * @return quantidade de contas
     */
    public int count() {
        return accounts.size();
    }

    /**
     * Substitui todo o mapa de contas (usado na restauração de dados).
     * 
     * @param accounts novo mapa de contas
     */
    public void replaceAll(Map<String, Account> accounts) {
        this.accounts.clear();
        this.accounts.putAll(accounts);
    }

    /**
     * Retorna o mapa interno (cópia defensiva) para persistência.
     * 
     * @return cópia do mapa de contas
     */
    public Map<String, Account> getMap() {
        return new HashMap<>(accounts);
    }
}