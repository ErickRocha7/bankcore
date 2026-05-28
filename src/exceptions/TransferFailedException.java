package exceptions;

/**
 * Exceção verificada para falhas durante uma transferência entre contas.
 * 
 * Capítulo 11 – Tratamento de exceções (exceções personalizadas e
 * encadeamento).
 */
public class TransferFailedException extends Exception {

    /**
     * Cria uma exceção de falha de transferência com mensagem descritiva.
     *
     * @param message descrição do erro
     */
    public TransferFailedException(String message) {
        super(message);
    }

    /**
     * Cria uma exceção de falha de transferência com mensagem e causa.
     *
     * @param message descrição do erro
     * @param cause   exceção original que causou a falha
     */
    public TransferFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}