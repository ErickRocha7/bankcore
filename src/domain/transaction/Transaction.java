package domain.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import domain.enums.TransactionType; // IMPORTAÇÃO CORRIGIDA

public final class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String description;
    private final String originAccount;
    private final String destinationAccount;
    private final LocalDateTime timestamp;

    // Construtor completo
    public Transaction(TransactionType type, BigDecimal amount, String description,
            String originAccount, String destinationAccount) {
        if (type == null)
            throw new IllegalArgumentException("Tipo de transação não pode ser nulo.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo.");
        }
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.description = (description != null) ? description : type.getDescription();
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.timestamp = LocalDateTime.now();
    }

    // Construtor simplificado
    public Transaction(TransactionType type, BigDecimal amount, String description, String accountNumber) {
        this(type, amount, description,
                type == TransactionType.WITHDRAW ? accountNumber : null,
                type == TransactionType.DEPOSIT ? accountNumber : null);
    }

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BigDecimal getSignedAmount() {
        return amount.multiply(BigDecimal.valueOf(type.getSign()));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | R$ %s | Origem: %s | Destino: %s | %s",
                id.substring(0, 8),
                timestamp.toString(),
                type.getDescription(),
                amount.toPlainString(),
                originAccount != null ? originAccount : "N/A",
                destinationAccount != null ? destinationAccount : "N/A",
                description);
    }
}