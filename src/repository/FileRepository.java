package repository;

import infrastructure.persistence.SerializationManager;
import java.io.IOException;

public class FileRepository {

    private final String filePath;
    private final SerializationManager<BankData> serializationManager;

    public FileRepository(String filePath) {
        this.filePath = filePath;
        this.serializationManager = new SerializationManager<>();
    }

    public void save(AccountRepository accountRepo, CustomerRepository customerRepo) throws IOException {
        BankData data = new BankData();
        data.setAccounts(accountRepo.getMap()); // converte BST para Map
        data.setCustomers(customerRepo.getMap());
        serializationManager.saveObject(data, filePath);
    }

    public void load(AccountRepository accountRepo, CustomerRepository customerRepo)
            throws IOException, ClassNotFoundException {
        BankData data = serializationManager.loadObject(filePath);
        if (data != null) {
            if (data.getAccounts() != null) {
                accountRepo.replaceAll(data.getAccounts()); // popula BST a partir do Map
            }
            if (data.getCustomers() != null) {
                customerRepo.replaceAll(data.getCustomers());
            }
        }
    }
}