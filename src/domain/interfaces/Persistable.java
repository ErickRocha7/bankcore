package domain.interfaces;

import java.io.IOException;

/**
 * Contrato para objetos que podem ser persistidos em arquivo.
 * Permite evoluir de serialização automática para formatos como JSON, sem
 * alterar o domínio.
 *
 * Capítulo 10 – Interfaces
 * Capítulo 15 – Arquivos, fluxos e serialização de objetos.
 */
public interface Persistable<T> {
    /**
     * Salva o estado atual do objeto em um arquivo.
     * 
     * @param filePath caminho do arquivo
     * @throws IOException em caso de erro de I/O
     */
    void save(String filePath) throws IOException;

    /**
     * Carrega um objeto a partir de um arquivo.
     * 
     * @param filePath caminho do arquivo
     * @return instância do objeto carregada
     * @throws IOException            em caso de erro de I/O
     * @throws ClassNotFoundException se a classe do objeto não for encontrada
     */
    T load(String filePath) throws IOException, ClassNotFoundException;
}