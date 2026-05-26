// src/exceptions/UnauthorizedException.java
package exceptions;

/**
 * Exceção verificada para falhas de autenticação ou autorização.
 * Cobertura: Capítulo 11 – Tratamento de exceção.
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}