package domain.interfaces;

/**
 * Interface para objetos que podem gerar uma trilha de auditoria.
 *
 * Capítulo 10 – Interfaces
 * Capítulo 14 – Strings e StringBuilder
 */
public interface Auditable {
    /**
     * Retorna uma representação textual da trilha de auditoria do objeto.
     * Normalmente inclui transações, datas e alterações de estado.
     * 
     * @return string com a auditoria completa
     */
    String generateAuditTrail();
}