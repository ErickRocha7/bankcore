package domain.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import domain.enums.TransactionType; // IMPORTAÇÃO CORRIGIDA

public final class LedgerEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String accountNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal signedAmount;
    private final String description;
    private final LocalDateTime timestamp;

    public LedgerEntry(String accountNumber, TransactionType type, BigDecimal amount, String description) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Número da conta é obrigatório.");
        }
        if (type == null)
            throw new IllegalArgumentException("Tipo de transação é obrigatório.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo.");
        }
        this.id = UUID.randomUUID().toString();
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.signedAmount = amount.multiply(BigDecimal.valueOf(type.getSign()));
        this.description = description != null ? description : type.getDescription();
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getSignedAmount() {
        return signedAmount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s | Conta: %s | %s | R$ %s (Efetivo: %s) | %s",
                timestamp.toString(),
                accountNumber,
                type.getDescription(),
                amount.toPlainString(),
                signedAmount.toPlainString(),
                description);
    }
}