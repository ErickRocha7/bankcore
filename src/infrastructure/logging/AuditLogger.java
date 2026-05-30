package infrastructure.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger de auditoria simples baseado em arquivo texto.
 * Registra operações, erros e trilhas de auditoria.
 * 
 * Capítulos abordados:
 * 15 - Arquivos, fluxos (FileWriter, PrintWriter)
 * 14 - Strings, formatação (DateTimeFormatter, StringBuilder)
 * 11 - Tratamento de exceções
 */
public class AuditLogger {
    private String logFilePath;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Cria um logger que escreve no arquivo especificado.
     * 
     * @param logFilePath Caminho do arquivo de log (ex: "logs/audit.log")
     */
    public AuditLogger(String logFilePath) {
        this.logFilePath = logFilePath;
        // Garante que o diretório de logs existe
        try {
            java.io.File logFile = new java.io.File(logFilePath);
            java.io.File parent = logFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Não foi possível criar diretório de logs: " + e.getMessage());
        }
    }

    /**
     * Registra uma mensagem de informação no log.
     * 
     * @param message Mensagem a ser registrada
     */
    public void info(String message) {
        log("INFO", message);
    }

    /**
     * Registra uma mensagem de aviso no log.
     * 
     * @param message Mensagem a ser registrada
     */
    public void warning(String message) {
        log("WARNING", message);
    }

    /**
     * Registra uma mensagem de erro no log.
     * 
     * @param message Mensagem a ser registrada
     */
    public void error(String message) {
        log("ERROR", message);
    }

    /**
     * Registra uma mensagem de erro com a stack trace da exceção.
     * 
     * @param message descrição do erro
     * @param t       exceção associada
     */
    public void error(String message, Throwable t) {
        String fullMessage = message + System.lineSeparator() + stackTraceToString(t);
        log("ERROR", fullMessage);
    }

    /**
     * Registra uma trilha de auditoria (ex: transação realizada).
     * 
     * @param auditTrail String contendo a trilha de auditoria
     */
    public void audit(String auditTrail) {
        log("AUDIT", auditTrail);
    }

    // Método interno que escreve a entrada no arquivo
    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(dtf);
        String logEntry = String.format("[%s] [%s] %s", timestamp, level, message);

        // Escreve no arquivo (append) e também imprime no console para depuração
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Falha ao escrever no log: " + e.getMessage());
        }
        // Opcional: também exibir no console
        System.out.println(logEntry);
    }

    /**
     * Converte a stack trace de uma exceção para String.
     */
    private String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}