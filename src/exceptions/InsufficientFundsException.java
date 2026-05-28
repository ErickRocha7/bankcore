package exceptions;

/**
 * Exceção verificada para quando uma operação não pode ser concluída por falta
 * de fundos.
 */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}