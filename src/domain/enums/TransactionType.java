package domain.enums;

public enum TransactionType {
    DEPOSIT("Depósito", 1),
    WITHDRAW("Saque", -1),
    TRANSFER_IN("Transferência recebida", 1),
    TRANSFER_OUT("Transferência enviada", -1),
    FEE("Tarifa bancária", -1),
    INTEREST("Juros", 1),
    REVERSAL("Estorno", 1),
    ACCOUNT_OPENING("Abertura de conta", 1);

    private final String description;
    private final int sign;

    TransactionType(String description, int sign) {
        this.description = description;
        this.sign = sign;
    }

    public String getDescription() {
        return description;
    }

    public int getSign() {
        return sign;
    }
}