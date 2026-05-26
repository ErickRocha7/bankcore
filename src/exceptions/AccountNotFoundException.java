// src/exceptions/AccountNotFoundException.java
package exceptions;

/**
 * Exceção verificada para quando uma conta bancária não é encontrada.
 * Cobertura: Capítulo 11 – Tratamento de exceção (exceções personalizadas).
 */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}