// src/service/LedgerService.java
package service;

import domain.account.Account;
import exceptions.AccountNotFoundException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

/**
 * Serviço de ledger (histórico transacional).
 * Registra movimentações e fornece extrato.
 * 
 * Capítulos:
 * 7, 16 - Coleções (GenericLinkedList)
 * 14 - Strings (formatação)
 */
public class LedgerService {
    private AccountRepository accountRepo;
    private AuditLogger logger;

    public LedgerService(AccountRepository accountRepo, AuditLogger logger) {
        this.accountRepo = accountRepo;
        this.logger = logger;
    }

    /**
     * Registra uma transação genérica na conta.
     * Assume que Account possui método recordTransaction.
     */
    public void record(String accountNumber, String description, double amount)
            throws AccountNotFoundException {
        Account account = accountRepo.findById(accountNumber);
        account.recordTransaction(description, amount);
        logger.audit("Transação na conta " + accountNumber + ": " + description + " R$ " + amount);
    }

    /**
     * Obtém o extrato formatado da conta.
     */
    public String getStatement(String accountNumber) throws AccountNotFoundException {
        Account account = accountRepo.findById(accountNumber);
        return account.getStatement();
    }
}