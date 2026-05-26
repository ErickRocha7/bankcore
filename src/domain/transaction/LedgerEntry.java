package domain.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa um lançamento contábil individual (partida dobrada simplificada).
 * Cada entrada de ledger está associada a uma conta e pode ser de débito ou
 * crédito.
 *
 * Capítulos abordados:
 * 6, 8 – Classes, objetos, encapsulamento
 * 14 – Strings, formatação
 * 15 – Serialização
 */
public final class LedgerEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String accountNumber; // conta afetada
    private final TransactionType type; // tipo de transação
    private final BigDecimal amount; // valor absoluto da movimentação
    private final BigDecimal signedAmount; // valor com sinal (positivo para crédito, negativo para débito)
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

    // Getters
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