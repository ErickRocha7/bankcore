package infrastructure.persistence;

import domain.interfaces.Persistable;
import java.io.*;

/**
 * Gerenciador de serialização genérico.
 * Implementa a interface Persistable, salvando e carregando objetos em
 * arquivos.
 * 
 * Capítulos abordados:
 * 15 - Arquivos, fluxos e serialização de objetos
 * 20 - Classes e métodos genéricos
 * 11 - Tratamento de exceções (IOException, ClassNotFoundException)
 *
 * @param <T> Tipo do objeto a ser persistido (deve ser Serializable)
 */
public class SerializationManager<T extends Serializable> implements Persistable<T> {

    /**
     * Salva um objeto em arquivo usando serialização.
     * 
     * @param object   Objeto a ser salvo
     * @param filePath Caminho do arquivo
     * @throws IOException Se ocorrer erro de I/O
     */
    public void saveObject(T object, String filePath) throws IOException {
        // Garante que o diretório existe
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(object);
        }
    }

    /**
     * Carrega um objeto de um arquivo serializado.
     * 
     * @param filePath Caminho do arquivo
     * @return Objeto carregado
     * @throws IOException            Se ocorrer erro de I/O
     * @throws ClassNotFoundException Se a classe do objeto não for encontrada
     */
    public T loadObject(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + filePath);
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            T object = (T) in.readObject();
            return object;
        }
    }

    /**
     * Implementação do método save da interface Persistable.
     * Salva o próprio gerenciador ou estado? Na realidade, o método recebe o
     * objeto.
     * Para cumprir a interface, adaptamos: save(String) salvará o estado deste
     * gerenciador (vazio).
     * Normalmente, os repositórios usarão saveObject diretamente.
     */
    @Override
    public void save(String filePath) throws IOException {
        // Este método é um placeholder; a persistência real é feita via saveObject.
        // Pode ser sobrescrito em repositórios concretos.
        throw new UnsupportedOperationException("Use saveObject(T object, String filePath) para persistir objetos.");
    }

    @Override
    public T load(String filePath) throws IOException, ClassNotFoundException {
        // Similarmente, é um placeholder.
        throw new UnsupportedOperationException("Use loadObject(String filePath) para carregar objetos.");
    }
}