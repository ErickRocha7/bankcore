package service;

import domain.account.Account;
import domain.enums.TransactionType;
import exceptions.AccountNotFoundException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

import java.math.BigDecimal;

public class LedgerService {
    private AccountRepository accountRepo;
    private AuditLogger logger;

    public LedgerService(AccountRepository accountRepo, AuditLogger logger) {
        this.accountRepo = accountRepo;
        this.logger = logger;
    }

    public void record(String accountNumber, TransactionType type, String description, BigDecimal amount)
            throws AccountNotFoundException {
        Account account = accountRepo.findById(accountNumber);
        account.recordTransaction(type, description, amount);
        logger.audit("Transação na conta " + accountNumber + ": " + description + " R$ " + amount);
    }

    public String getStatement(String accountNumber) throws AccountNotFoundException {
        Account account = accountRepo.findById(accountNumber);
        return account.getStatement();
    }
}