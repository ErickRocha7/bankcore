package domain.account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import collections.GenericLinkedList;
import domain.enums.AccountStatus;
import domain.enums.TransactionType;
import domain.transaction.Transaction;
import exceptions.InsufficientFundsException;
import util.CurrencyFormatter;

public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int accountCounter = 10000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String accountNumber;
    private String holderName;
    protected BigDecimal balance;
    protected GenericLinkedList<Transaction> transactions;
    private AccountStatus status;

    public Account(String holderName, BigDecimal initialBalance) {
        validateHolderName(holderName);
        validateNonNegativeValue(initialBalance, "Saldo inicial");

        this.accountNumber = generateAccountNumber();
        this.holderName = holderName.trim();
        this.balance = initialBalance;
        this.transactions = new GenericLinkedList<>();
        this.status = AccountStatus.ACTIVE;

        addTransaction(TransactionType.ACCOUNT_OPENING, "Abertura de conta", initialBalance);
    }

    public void deposit(BigDecimal amount) {
        ensureActive();
        validatePositiveValue(amount, "Depósito");
        balance = balance.add(amount);
        addTransaction(TransactionType.DEPOSIT, "Depósito", amount);
    }

    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        ensureActive();
        validatePositiveValue(amount, "Saque");

        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException(
                    String.format(
                            "Saldo insuficiente. Saldo atual: %s | Valor solicitado: %s",
                            CurrencyFormatter.format(balance),
                            CurrencyFormatter.format(amount)));
        }

        balance = balance.subtract(amount);
        addTransaction(TransactionType.WITHDRAW, "Saque", amount);
    }

    public void recordTransaction(TransactionType type, String description, BigDecimal amount) {
        ensureActive();
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Descrição da transação não pode ser vazia.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser positivo.");
        }
        addTransaction(type, description, amount);
    }

    private void addTransaction(TransactionType type, String description, BigDecimal amount) {
        Transaction t = new Transaction(type, amount, description, accountNumber);
        transactions.add(t);
    }

    public String getStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("=============================================\n");
        sb.append(String.format("Conta: %s\n", accountNumber));
        sb.append(String.format("Titular: %s\n", holderName));
        sb.append(String.format("Status: %s\n", status.getDescription()));
        sb.append("=============================================\n");

        if (transactions.isEmpty()) {
            sb.append("Nenhuma movimentação registrada.\n");
        } else {
            for (Transaction t : transactions) {
                sb.append(String.format("[%s] %-30s %s\n",
                        t.getTimestamp().format(DATE_FORMATTER),
                        t.getDescription(),
                        CurrencyFormatter.format(t.getSignedAmount())));
            }
        }

        sb.append("=============================================\n");
        sb.append(String.format("Saldo atual: %s\n",
                CurrencyFormatter.format(balance)));

        return sb.toString();
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Conta " + accountNumber + " não está ativa. Status: " + status.getDescription());
        }
    }

    // ---------- Validações (inalteradas) ----------
    private void validateHolderName(String holderName) {
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Nome do titular não pode ser vazio.");
        }
    }

    protected void validatePositiveValue(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
        }
    }

    protected void validateNonNegativeValue(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " não pode ser negativo.");
        }
    }

    // ---------- Getters e Setters (inalterados) ----------
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public GenericLinkedList<Transaction> getTransactions() {
        return transactions;
    }

    public void setHolderName(String holderName) {
        validateHolderName(holderName);
        this.holderName = holderName.trim();
    }

    // ---------- Object (inalterado, exceto inclusão do status no toString)
    // ----------
    @Override
    public String toString() {
        return String.format("%s | %s | Saldo: %s | Status: %s",
                accountNumber,
                holderName,
                CurrencyFormatter.format(balance),
                status.getDescription());
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

    private static String generateAccountNumber() {
        return String.format("%05d", ++accountCounter);
    }
}