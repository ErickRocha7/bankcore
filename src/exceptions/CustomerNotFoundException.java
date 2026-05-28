package exceptions;

/**
 * Exceção verificada para quando um cliente não é encontrado no repositório.
 *
 * Capítulo 11 – Tratamento de exceções (exceções personalizadas).
 */
public class CustomerNotFoundException extends Exception {

    /**
     * Cria a exceção com uma mensagem descritiva.
     *
     * @param message descrição do erro
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }

    /**
     * Cria a exceção com mensagem e causa.
     *
     * @param message descrição do erro
     * @param cause   exceção original que causou a falha
     */
    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}