// src/domain/account/Account.java
package domain.account;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import collections.GenericLinkedList;
import exceptions.InsufficientFundsException;

/**
 * Classe abstrata que representa uma conta bancária genérica.
 *
 * Fornece a estrutura base para contas do sistema bancário.
 *
 * Cobertura dos capítulos:
 * 3, 6, 8 - Classes, objetos, encapsulamento, construtores, static e final
 * 9 - Herança
 * 10 - Polimorfismo
 * 11 - Exceções personalizadas
 * 14 - Strings e formatação
 * 15 - Serialização
 * 20, 21 - Genéricos e estruturas personalizadas
 */
public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Contador sequencial simples para geração de contas.
     */
    private static int accountCounter = 10000;

    /**
     * Formatação padrão de data/hora.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Número da conta.
     * Imutável após criação.
     */
    private final String accountNumber;

    /**
     * Nome do titular.
     */
    private String holderName;

    /**
     * Saldo da conta.
     * Protected para acesso controlado pelas subclasses.
     */
    protected double balance;

    /**
     * Histórico textual simplificado de transações.
     */
    protected GenericLinkedList<String> transactions;

    /**
     * Construtor principal.
     *
     * @param holderName     titular da conta
     * @param initialBalance saldo inicial
     */
    public Account(String holderName, double initialBalance) {

        validateHolderName(holderName);
        validateNonNegativeValue(initialBalance, "Saldo inicial");

        this.accountNumber = generateAccountNumber();
        this.holderName = holderName.trim();
        this.balance = initialBalance;
        this.transactions = new GenericLinkedList<>();

        addTransaction("Abertura de conta", initialBalance);
    }

    /**
     * Deposita um valor na conta.
     *
     * @param amount valor do depósito
     */
    public void deposit(double amount) {

        validatePositiveValue(amount, "Depósito");

        balance += amount;

        addTransaction("Depósito", amount);
    }

    /**
     * Realiza saque da conta.
     *
     * @param amount valor do saque
     * @throws InsufficientFundsException se não houver saldo suficiente
     */
    public void withdraw(double amount) throws InsufficientFundsException {

        validatePositiveValue(amount, "Saque");

        if (amount > balance) {
            throw new InsufficientFundsException(
                    String.format(
                            "Saldo insuficiente. Saldo atual: R$ %.2f | Valor solicitado: R$ %.2f",
                            balance,
                            amount));
        }

        balance -= amount;

        addTransaction("Saque", -amount);
    }

    /**
     * Método abstrato de projeção/aplicação de juros.
     *
     * @param years quantidade de anos
     */
    public abstract void calculateInterest(int years);

    /**
     * Registra uma transação manualmente.
     *
     * @param description descrição
     * @param amount      valor
     */
    public void recordTransaction(String description, double amount) {

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição da transação não pode ser vazia.");
        }

        addTransaction(description, amount);
    }

    /**
     * Adiciona uma entrada ao histórico.
     *
     * @param description descrição
     * @param amount      valor
     */
    protected void addTransaction(String description, double amount) {

        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);

        String entry = String.format(
                "[%s] %-30s R$ %,.2f",
                timestamp,
                description,
                amount);

        transactions.add(entry);
    }

    /**
     * Retorna extrato textual completo.
     *
     * @return extrato formatado
     */
    public String getStatement() {

        StringBuilder sb = new StringBuilder();

        sb.append("=============================================\n");
        sb.append(String.format("Conta: %s\n", accountNumber));
        sb.append(String.format("Titular: %s\n", holderName));
        sb.append("=============================================\n");

        if (transactions.isEmpty()) {
            sb.append("Nenhuma movimentação registrada.\n");
        } else {
            for (String transaction : transactions) {
                sb.append(transaction).append("\n");
            }
        }

        sb.append("=============================================\n");
        sb.append(String.format("Saldo atual: R$ %,.2f\n", balance));

        return sb.toString();
    }

    /**
     * Gera número sequencial formatado.
     */
    private static String generateAccountNumber() {
        return String.format("%05d", ++accountCounter);
    }

    /**
     * Valida nome do titular.
     */
    private void validateHolderName(String holderName) {

        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Nome do titular não pode ser vazio.");
        }
    }

    /**
     * Valida valor positivo.
     */
    protected void validatePositiveValue(double value, String fieldName) {

        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
        }
    }

    /**
     * Valida valor não negativo.
     */
    protected void validateNonNegativeValue(double value, String fieldName) {

        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " não pode ser negativo.");
        }
    }

    // =========================
    // Getters
    // =========================

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public GenericLinkedList<String> getTransactions() {
        return transactions;
    }

    // =========================
    // Setters controlados
    // =========================

    public void setHolderName(String holderName) {

        validateHolderName(holderName);

        this.holderName = holderName.trim();
    }

    // =========================
    // Object methods
    // =========================

    @Override
    public String toString() {

        return String.format(
                "%s | %s | Saldo: R$ %,.2f",
                accountNumber,
                holderName,
                balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Account other)) {
            return false;
        }

        return Objects.equals(accountNumber, other.accountNumber);
    }
}