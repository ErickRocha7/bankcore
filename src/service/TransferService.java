// src/service/TransferService.java
package service;

import collections.GenericStack;
import domain.account.Account;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

/**
 * Serviço de transferência entre contas, com rollback manual.
 * Utiliza GenericStack para armazenar ações de desfazer.
 * 
 * Capítulos:
 * 11 - Exceções, tratamento de falhas
 * 21 - Estruturas de dados genéricas personalizadas (GenericStack)
 * 8 - Composição de serviços
 */
public class TransferService {
    private AccountRepository accountRepo;
    private AuditLogger logger;
    private GenericStack<String> undoStack; // armazena descrições de rollback

    public TransferService(AccountRepository accountRepo, AuditLogger logger) {
        this.accountRepo = accountRepo;
        this.logger = logger;
        this.undoStack = new GenericStack<>();
    }

    /**
     * Transfere valor entre duas contas.
     * Em caso de falha no crédito, tenta reverter o débito.
     */
    public void transfer(String fromAccount, String toAccount, double amount)
            throws AccountNotFoundException, InsufficientFundsException {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor de transferência deve ser positivo.");
        if (fromAccount.equals(toAccount))
            throw new IllegalArgumentException("Contas de origem e destino devem ser diferentes.");

        Account source = accountRepo.findById(fromAccount);
        Account target = accountRepo.findById(toAccount);

        // Débito
        source.withdraw(amount);
        source.recordTransaction("Transferência enviada para " + toAccount, -amount);
        undoStack.push("debit:" + fromAccount + ":" + amount); // registro para possível rollback
        logger.info("Débito de R$ " + amount + " da conta " + fromAccount);

        try {
            // Crédito
            target.deposit(amount);
            target.recordTransaction("Transferência recebida de " + fromAccount, amount);
            logger.info("Crédito de R$ " + amount + " na conta " + toAccount);
        } catch (Exception e) {
            // Rollback do débito
            logger.error("Falha no crédito para conta " + toAccount + ". Iniciando rollback...");
            try {
                source.deposit(amount); // reverte débito
                source.recordTransaction("Estorno de transferência falha", amount);
                logger.info("Rollback realizado: débito revertido na conta " + fromAccount);
            } catch (Exception rollbackError) {
                logger.error("Rollback falhou! Contate suporte. " + rollbackError.getMessage());
            }
            throw new RuntimeException("Transferência falhou e foi revertida.", e);
        }
    }
}