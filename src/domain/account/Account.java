package domain.account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import collections.GenericLinkedList;
import exceptions.InsufficientFundsException;
import util.CurrencyFormatter;

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

    private static int accountCounter = 10000;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String accountNumber;
    private String holderName;
    protected BigDecimal balance;
    protected GenericLinkedList<String> transactions;

    /**
     * Construtor principal.
     *
     * @param holderName     titular da conta
     * @param initialBalance saldo inicial (BigDecimal)
     */
    public Account(String holderName, BigDecimal initialBalance) {
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
     * @param amount valor do depósito (BigDecimal)
     */
    public void deposit(BigDecimal amount) {
        validatePositiveValue(amount, "Depósito");
        balance = balance.add(amount);
        addTransaction("Depósito", amount);
    }

    /**
     * Realiza saque da conta.
     *
     * @param amount valor do saque (BigDecimal)
     * @throws InsufficientFundsException se não houver saldo suficiente
     */
    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        validatePositiveValue(amount, "Saque");

        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException(
                    String.format(
                            "Saldo insuficiente. Saldo atual: %s | Valor solicitado: %s",
                            CurrencyFormatter.format(balance),
                            CurrencyFormatter.format(amount)));
        }

        balance = balance.subtract(amount);
        addTransaction("Saque", amount.negate());
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
     * @param amount      valor (BigDecimal)
     */
    public void recordTransaction(String description, BigDecimal amount) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição da transação não pode ser vazia.");
        }
        addTransaction(description, amount);
    }

    /**
     * Adiciona uma entrada ao histórico.
     *
     * @param description descrição
     * @param amount      valor (BigDecimal)
     */
    protected void addTransaction(String description, BigDecimal amount) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String entry = String.format(
                "[%s] %-30s %s",
                timestamp,
                description,
                CurrencyFormatter.format(amount));
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
        sb.append(String.format("Saldo atual: %s\n",
                CurrencyFormatter.format(balance)));

        return sb.toString();
    }

    private static String generateAccountNumber() {
        return String.format("%05d", ++accountCounter);
    }

    private void validateHolderName(String holderName) {
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Nome do titular não pode ser vazio.");
        }
    }

    /**
     * Valida valor positivo (BigDecimal).
     */
    protected void validatePositiveValue(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
        }
    }

    /**
     * Valida valor não negativo (BigDecimal).
     */
    protected void validateNonNegativeValue(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " não pode ser negativo.");
        }
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public GenericLinkedList<String> getTransactions() {
        return transactions;
    }

    // Setters
    public void setHolderName(String holderName) {
        validateHolderName(holderName);
        this.holderName = holderName.trim();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Saldo: %s",
                accountNumber,
                holderName,
                CurrencyFormatter.format(balance));
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Account other))
            return false;
        return Objects.equals(accountNumber, other.accountNumber);
    }
}