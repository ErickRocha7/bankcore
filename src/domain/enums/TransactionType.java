package domain.enums;

/**
 * Tipos de transação bancária que serão registradas no ledger.
 * 
 * Capítulos abordados:
 * 6, 8 - Enums e constantes
 * 
 * Evolução futura: associar um sinal (+1 ou -1) para facilitar soma do saldo.
 */
public enum TransactionType {
    DEPOSIT("Depósito"),
    WITHDRAW("Saque"),
    TRANSFER_IN("Transferência recebida"),
    TRANSFER_OUT("Transferência enviada"),
    FEE("Tarifa bancária"),
    INTEREST("Juros"),
    REVERSAL("Estorno");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Retorna o sinal do tipo de transação para efeito de cálculo de saldo.
     * Útil futuramente na soma do ledger.
     */
    public int getSign() {
        return switch (this) {
            case DEPOSIT, TRANSFER_IN, INTEREST, REVERSAL -> 1;
            case WITHDRAW, TRANSFER_OUT, FEE -> -1;
        };
    }
}