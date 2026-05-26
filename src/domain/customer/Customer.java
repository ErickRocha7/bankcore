// src/domain/customer/Customer.java
package domain.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import domain.account.Account;
import infrastructure.security.PinHasher;

/**
 * Representa um cliente do banco.
 *
 * Cada cliente pode possuir múltiplas contas bancárias.
 *
 * Capítulos abordados:
 * 3, 6, 8 - Classes, encapsulamento, construtores e validações
 * 7, 16 - Coleções genéricas (List<Account>)
 * 10 - Composição
 * 14 - Expressões regulares e Strings
 * 15 - Serialização
 */
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * CPF do cliente.
     * Imutável após criação.
     */
    private final String cpf;

    /**
     * Nome completo do cliente.
     */
    private String name;

    /**
     * Senha armazenada como hash.
     */
    private String passwordHash;

    /**
     * Contas vinculadas ao cliente.
     */
    private List<Account> accounts;

    /**
     * Construtor principal.
     *
     * @param cpf      CPF formatado
     * @param name     nome completo
     * @param password senha em texto puro
     */
    public Customer(String cpf, String name, String password) {

        validateCpf(cpf);
        validateName(name);
        validatePassword(password);

        this.cpf = cpf;
        this.name = name.trim();
        this.passwordHash = PinHasher.hash(password);
        this.accounts = new ArrayList<>();
    }

    /**
     * Adiciona uma conta ao cliente.
     *
     * @param account conta
     */
    public void addAccount(Account account) {

        if (account == null) {
            throw new IllegalArgumentException("Conta não pode ser nula.");
        }

        if (accounts.contains(account)) {
            throw new IllegalArgumentException(
                    "Conta já vinculada ao cliente.");
        }

        accounts.add(account);
    }

    /**
     * Remove conta pelo número.
     *
     * @param accountNumber número da conta
     * @return true se removida
     */
    public boolean removeAccount(String accountNumber) {

        if (accountNumber == null || accountNumber.isBlank()) {
            return false;
        }

        return accounts.removeIf(
                account -> account.getAccountNumber().equals(accountNumber));
    }

    /**
     * Busca uma conta pelo número.
     *
     * @param accountNumber número da conta
     * @return conta encontrada ou null
     */
    public Account findAccount(String accountNumber) {

        if (accountNumber == null || accountNumber.isBlank()) {
            return null;
        }

        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }

        return null;
    }

    /**
     * Verifica autenticação do cliente.
     *
     * @param password senha em texto puro
     * @return true se válida
     */
    public boolean authenticate(String password) {

        if (password == null) {
            return false;
        }

        return PinHasher.verify(password, passwordHash);
    }

    /**
     * Altera senha do cliente.
     *
     * @param currentPassword senha atual
     * @param newPassword     nova senha
     */
    public void changePassword(
            String currentPassword,
            String newPassword) {

        if (!authenticate(currentPassword)) {
            throw new IllegalArgumentException("Senha atual inválida.");
        }

        validatePassword(newPassword);

        this.passwordHash = PinHasher.hash(newPassword);
    }

    /**
     * Validação simples de CPF formatado.
     *
     * Formato esperado:
     * 000.000.000-00
     *
     * @param cpf CPF
     * @return true se formato válido
     */
    public static boolean isValidCPF(String cpf) {

        if (cpf == null) {
            return false;
        }

        return cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    /**
     * Valida CPF.
     */
    private void validateCpf(String cpf) {

        if (!isValidCPF(cpf)) {
            throw new IllegalArgumentException(
                    "CPF inválido. Formato esperado: 000.000.000-00");
        }
    }

    /**
     * Valida nome.
     */
    private void validateName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Nome não pode ser vazio.");
        }

        if (!name.matches("[A-Za-zÀ-ÿ ]+")) {
            throw new IllegalArgumentException(
                    "Nome deve conter apenas letras e espaços.");
        }
    }

    /**
     * Valida senha.
     */
    private void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Senha não pode ser vazia.");
        }

        if (password.length() < 4) {
            throw new IllegalArgumentException(
                    "Senha deve possuir no mínimo 4 caracteres.");
        }
    }

    // =========================
    // Getters
    // =========================

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    /**
     * Retorna cópia defensiva da lista.
     */
    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    // =========================
    // Setters controlados
    // =========================

    public void setName(String name) {

        validateName(name);

        this.name = name.trim();
    }

    // =========================
    // Métodos utilitários
    // =========================

    /**
     * Retorna quantidade de contas.
     */
    public int getAccountCount() {
        return accounts.size();
    }

    /**
     * Verifica se o cliente possui contas.
     */
    public boolean hasAccounts() {
        return !accounts.isEmpty();
    }

    // =========================
    // Object methods
    // =========================

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Cliente: ")
                .append(name)
                .append(" | CPF: ")
                .append(cpf)
                .append(System.lineSeparator());

        if (accounts.isEmpty()) {

            sb.append("  Nenhuma conta vinculada.");

        } else {

            sb.append("  Contas:")
                    .append(System.lineSeparator());

            for (Account account : accounts) {
                sb.append("    ")
                        .append(account)
                        .append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Customer other)) {
            return false;
        }

        return Objects.equals(cpf, other.cpf);
    }
}