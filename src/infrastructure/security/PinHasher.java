package infrastructure.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitário para hash de PIN/senha usando SHA-256 com salt.
 * Simula segurança básica para autenticação no sistema bancário.
 * 
 * Capítulos abordados:
 * 14 - Strings e manipulação de caracteres
 * 6  - Métodos estáticos e encapsulamento
 * 11 - Tratamento de exceções (NoSuchAlgorithmException)
 */
public class PinHasher {

    /**
     * Gera um hash SHA-256 de uma senha combinada com um salt aleatório.
     * Retorna o salt e o hash codificados em Base64, separados por ":".
     *
     * @param password Senha em texto puro
     * @return String no formato "salt:hash"
     * @throws RuntimeException Se o algoritmo SHA-256 não estiver disponível
     */
    public static String hash(String password) {
        try {
            // Gera salt aleatório
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Cria hash SHA-256 da senha + salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Codifica em Base64 para armazenamento textual
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            return saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo SHA-256 não disponível.", e);
        }
    }

    /**
     * Verifica se uma senha em texto puro corresponde ao hash armazenado.
     *
     * @param password       Senha em texto puro
     * @param storedHash     String no formato "salt:hash" previamente gerado por hash()
     * @return true se a senha conferir
     */
    public static boolean verify(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] originalHash = Base64.getDecoder().decode(parts[1]);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] testHash = md.digest(password.getBytes());

            return MessageDigest.isEqual(originalHash, testHash);
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false;
        }
    }
}