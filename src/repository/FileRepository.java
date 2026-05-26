package repository;

import java.io.IOException;
import java.util.Map;

import domain.account.Account;
import domain.customer.Customer;
import infrastructure.persistence.SerializationManager;

/**
 * Repositório de arquivo para persistência completa do estado do banco.
 * Utiliza SerializationManager para salvar/carregar um objeto BankData.
 * 
 * Capítulos abordados:
 * 15 – Arquivos, fluxos e serialização de objetos
 * 20 – Métodos genéricos (SerializationManager é genérico)
 * 11 – Tratamento de exceções (IOException, ClassNotFoundException)
 */
public class FileRepository {
    private String filePath;
    private SerializationManager<BankData> serializationManager;

    /**
     * @param filePath caminho do arquivo de dados (ex: "data/bankdata.ser")
     */
    public FileRepository(String filePath) {
        this.filePath = filePath;
        this.serializationManager = new SerializationManager<>();
    }

    /**
     * Salva todos os dados do banco em um arquivo.
     * 
     * @param accountRepo  repositório de contas
     * @param customerRepo repositório de clientes
     * @throws IOException se ocorrer erro de I/O
     */
    public void save(AccountRepository accountRepo, CustomerRepository customerRepo) throws IOException {
        BankData data = new BankData();
        data.setAccounts(accountRepo.getMap());
        data.setCustomers(customerRepo.getMap());
        serializationManager.saveObject(data, filePath);
    }

    /**
     * Carrega os dados do arquivo e preenche os repositórios.
     * 
     * @param accountRepo  repositório de contas (será populado)
     * @param customerRepo repositório de clientes (será populado)
     * @throws IOException            se ocorrer erro de I/O
     * @throws ClassNotFoundException se a classe não for encontrada
     */
    public void load(AccountRepository accountRepo, CustomerRepository customerRepo)
            throws IOException, ClassNotFoundException {
        BankData data = serializationManager.loadObject(filePath);
        if (data != null) {
            if (data.getAccounts() != null) {
                accountRepo.replaceAll(data.getAccounts());
            }
            if (data.getCustomers() != null) {
                customerRepo.replaceAll(data.getCustomers());
            }
        }
    }
}