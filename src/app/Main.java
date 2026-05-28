package app;

import infrastructure.logging.AuditLogger;
import repository.AccountRepository;
import repository.CustomerRepository;
import repository.FileRepository;
import service.AccountService;
import service.AuthenticationService;
import service.InterestService;
import service.LedgerService;
import service.TransferService;

import java.io.IOException;

/**
 * Ponto de entrada do sistema bancário orientado a objetos.
 * Configura os componentes e inicia a aplicação.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Logger de auditoria
        AuditLogger logger = new AuditLogger("logs/audit.log");
        logger.info("Iniciando sistema bancário...");

        // 2. Repositórios
        AccountRepository accountRepo = new AccountRepository();
        CustomerRepository customerRepo = new CustomerRepository();

        // 3. Persistência (carrega dados salvos, se existirem)
        FileRepository fileRepo = new FileRepository("data/bankdata.ser");
        try {
            fileRepo.load(accountRepo, customerRepo);
            logger.info("Dados carregados do arquivo com sucesso.");
        } catch (IOException | ClassNotFoundException e) {
            logger.warning("Nenhum dado anterior encontrado ou falha ao carregar. Iniciando com dados vazios.");
        }

        // 4. Serviços (injeção manual de dependências)
        AccountService accountService = new AccountService(accountRepo, customerRepo, logger);
        AuthenticationService authService = new AuthenticationService(customerRepo, logger);
        TransferService transferService = new TransferService(accountRepo, logger);
        LedgerService ledgerService = new LedgerService(accountRepo, logger);
        InterestService interestService = new InterestService(accountRepo, logger);

        // 5. Aplicação
        BankApplicationOO app = new BankApplicationOO(
                authService, accountService, transferService,
                ledgerService, interestService, logger);
        app.run();

        // 6. Salvar dados ao encerrar
        try {
            fileRepo.save(accountRepo, customerRepo);
            logger.info("Dados salvos. Sistema encerrado.");
        } catch (IOException e) {
            logger.error("Falha ao salvar dados: " + e.getMessage());
        }
    }
}