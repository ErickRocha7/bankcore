package domain.enums;

/**
 * Estados possíveis de uma conta bancária.
 * 
 * Capítulos abordados:
 * 6, 8 - Enums
 */
public enum AccountStatus {
    ACTIVE("Ativa"),
    BLOCKED("Bloqueada"),
    CLOSED("Encerrada");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}