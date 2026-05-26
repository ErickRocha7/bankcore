// src/service/TransferService.java
package service;

import collections.GenericStack;
import domain.account.Account;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

/**
 * Serviço responsável por transferências entre contas.
 *
 * Funcionalidades:
 * - Transferência bancária
 * - Controle de rollback manual
 * - Registro transacional
 * - Auditoria
 *
 * Capítulos abordados:
 * 8 - Composição
 * 11 - Exceções e rollback manual
 * 16 - Estruturas de dados
 * 21 - Estruturas genéricas customizadas
 */
public class TransferService {

    private final AccountRepository accountRepo;
    private final AuditLogger logger;
    private final GenericStack<String> undoStack;

    /**
     * Construtor principal.
     *
     * @param accountRepo repositório de contas
     * @param logger      logger de auditoria
     */
    public TransferService(AccountRepository accountRepo,
            AuditLogger logger) {

        if (accountRepo == null) {
            throw new IllegalArgumentException(
                    "AccountRepository não pode ser nulo.");
        }

        if (logger == null) {
            throw new IllegalArgumentException(
                    "AuditLogger não pode ser nulo.");
        }

        this.accountRepo = accountRepo;
        this.logger = logger;
        this.undoStack = new GenericStack<>();
    }

    /**
     * Realiza transferência entre contas.
     *
     * Fluxo:
     * 1. Validação
     * 2. Débito
     * 3. Crédito
     * 4. Rollback em caso de falha
     *
     * @param fromAccount conta origem
     * @param toAccount   conta destino
     * @param amount      valor
     *
     * @throws AccountNotFoundException   conta inexistente
     * @throws InsufficientFundsException saldo insuficiente
     */
    public void transfer(String fromAccount,
            String toAccount,
            double amount)
            throws AccountNotFoundException,
            InsufficientFundsException {

        validateTransfer(fromAccount, toAccount, amount);

        Account source = accountRepo.findById(fromAccount);
        Account target = accountRepo.findById(toAccount);

        logger.info(
                "Iniciando transferência | Origem: "
                        + fromAccount
                        + " | Destino: "
                        + toAccount
                        + " | Valor: R$ "
                        + String.format("%.2f", amount));

        // ETAPA 1 - DÉBITO (já registra transação)
        source.withdraw(amount);

        undoStack.push(
                buildRollbackEntry(fromAccount, amount));

        logger.info(
                "Débito realizado na conta "
                        + fromAccount);

        try {

            // ETAPA 2 - CRÉDITO (já registra transação)
            target.deposit(amount);

            logger.info(
                    "Crédito realizado na conta "
                            + toAccount);

            logger.audit(
                    "Transferência concluída | Origem: "
                            + fromAccount
                            + " | Destino: "
                            + toAccount
                            + " | Valor: R$ "
                            + String.format("%.2f", amount));

        } catch (Exception transferError) {

            logger.error(
                    "Falha no crédito da transferência: "
                            + transferError.getMessage());

            rollback(source);

            throw new RuntimeException(
                    "Transferência falhou e foi revertida.",
                    transferError);
        }
    }

    /**
     * Executa rollback do débito.
     *
     * @param source conta origem
     */
    private void rollback(Account source) {

        try {

            if (undoStack.isEmpty()) {

                logger.error(
                        "Rollback impossível: pilha vazia.");

                return;
            }

            String rollbackData = undoStack.pop();

            String[] parts = rollbackData.split(":");

            if (parts.length != 3) {

                logger.error(
                        "Formato inválido de rollback.");

                return;
            }

            String operation = parts[0];
            String accountNumber = parts[1];
            double amount = Double.parseDouble(parts[2]);

            if (!"debit".equals(operation)) {

                logger.error(
                        "Operação de rollback inválida.");

                return;
            }

            source.deposit(amount); // reverte o débito, já registra transação

            logger.audit(
                    "Rollback executado | Conta: "
                            + accountNumber
                            + " | Valor estornado: R$ "
                            + String.format("%.2f", amount));

        } catch (Exception rollbackError) {

            logger.error(
                    "Falha crítica no rollback: "
                            + rollbackError.getMessage());
        }
    }

    /**
     * Cria entrada padronizada de rollback.
     */
    private String buildRollbackEntry(String accountNumber,
            double amount) {

        return "debit:" + accountNumber + ":" + amount;
    }

    /**
     * Validação completa da transferência.
     */
    private void validateTransfer(String fromAccount,
            String toAccount,
            double amount) {

        validateAccountNumber(fromAccount);
        validateAccountNumber(toAccount);

        if (fromAccount.equals(toAccount)) {

            throw new IllegalArgumentException(
                    "Conta de origem e destino devem ser diferentes.");
        }

        if (amount <= 0) {

            throw new IllegalArgumentException(
                    "Valor da transferência deve ser maior que zero.");
        }
    }

    /**
     * Validação básica de conta.
     */
    private void validateAccountNumber(String accountNumber) {

        if (accountNumber == null
                || accountNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Número da conta não pode ser vazio.");
        }
    }

    /**
     * Retorna quantidade de operações pendentes
     * na pilha de rollback.
     *
     * Método útil para debug/auditoria.
     */
    public int rollbackStackSize() {
        return undoStack.size();
    }

    /**
     * Limpa histórico de rollback.
     */
    public void clearRollbackHistory() {

        while (!undoStack.isEmpty()) {
            undoStack.pop();
        }

        logger.info(
                "Histórico de rollback limpo.");
    }
}