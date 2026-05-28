package service;

import collections.GenericStack;
import domain.account.Account;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

import java.math.BigDecimal;

/**
 * Serviço responsável por transferências entre contas.
 */
public class TransferService {

    private final AccountRepository accountRepo;
    private final AuditLogger logger;
    private final GenericStack<String> undoStack;

    public TransferService(AccountRepository accountRepo, AuditLogger logger) {
        if (accountRepo == null)
            throw new IllegalArgumentException("AccountRepository não pode ser nulo.");
        if (logger == null)
            throw new IllegalArgumentException("AuditLogger não pode ser nulo.");
        this.accountRepo = accountRepo;
        this.logger = logger;
        this.undoStack = new GenericStack<>();
    }

    /**
     * Realiza transferência entre contas.
     *
     * @param fromAccount conta origem
     * @param toAccount   conta destino
     * @param amount      valor (BigDecimal)
     */
    public void transfer(String fromAccount, String toAccount, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException {

        validateTransfer(fromAccount, toAccount, amount);

        Account source = accountRepo.findById(fromAccount);
        Account target = accountRepo.findById(toAccount);

        logger.info("Iniciando transferência | Origem: " + fromAccount +
                " | Destino: " + toAccount + " | Valor: R$ " + amount);

        // Débito
        source.withdraw(amount);
        undoStack.push(buildRollbackEntry(fromAccount, amount));
        logger.info("Débito realizado na conta " + fromAccount);

        try {
            // Crédito
            target.deposit(amount);
            logger.info("Crédito realizado na conta " + toAccount);
            logger.audit("Transferência concluída | Origem: " + fromAccount +
                    " | Destino: " + toAccount + " | Valor: R$ " + amount);
        } catch (Exception transferError) {
            logger.error("Falha no crédito da transferência: " + transferError.getMessage());
            rollback(source);
            throw new RuntimeException("Transferência falhou e foi revertida.", transferError);
        }
    }

    private void rollback(Account source) {
        try {
            if (undoStack.isEmpty()) {
                logger.error("Rollback impossível: pilha vazia.");
                return;
            }
            String rollbackData = undoStack.pop();
            String[] parts = rollbackData.split(":");
            if (parts.length != 3) {
                logger.error("Formato inválido de rollback.");
                return;
            }
            String operation = parts[0];
            String accountNumber = parts[1];
            BigDecimal amount = new BigDecimal(parts[2]);
            if (!"debit".equals(operation)) {
                logger.error("Operação de rollback inválida.");
                return;
            }
            source.deposit(amount);
            logger.audit("Rollback executado | Conta: " + accountNumber + " | Valor estornado: R$ " + amount);
        } catch (Exception rollbackError) {
            logger.error("Falha crítica no rollback: " + rollbackError.getMessage());
        }
    }

    private String buildRollbackEntry(String accountNumber, BigDecimal amount) {
        return "debit:" + accountNumber + ":" + amount.toPlainString();
    }

    private void validateTransfer(String fromAccount, String toAccount, BigDecimal amount) {
        validateAccountNumber(fromAccount);
        validateAccountNumber(toAccount);
        if (fromAccount.equals(toAccount)) {
            throw new IllegalArgumentException("Conta de origem e destino devem ser diferentes.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero.");
        }
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Número da conta não pode ser vazio.");
        }
    }

    public int rollbackStackSize() {
        return undoStack.size();
    }

    public void clearRollbackHistory() {
        while (!undoStack.isEmpty())
            undoStack.pop();
        logger.info("Histórico de rollback limpo.");
    }
}