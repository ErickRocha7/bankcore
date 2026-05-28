package service;

import collections.GenericStack;
import domain.account.Account;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.TransferFailedException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

import java.math.BigDecimal;

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
 * 11 - Exceções e rollback manual (agora com TransferFailedException)
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
    public TransferService(AccountRepository accountRepo, AuditLogger logger) {
        if (accountRepo == null) {
            throw new IllegalArgumentException("AccountRepository não pode ser nulo.");
        }
        if (logger == null) {
            throw new IllegalArgumentException("AuditLogger não pode ser nulo.");
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
     * @param amount      valor (BigDecimal)
     * @throws AccountNotFoundException   se alguma das contas não existir
     * @throws InsufficientFundsException se saldo insuficiente na origem
     * @throws TransferFailedException    se o crédito falhar (com rollback
     *                                    automático)
     */
    public void transfer(String fromAccount, String toAccount, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException, TransferFailedException {

        validateTransfer(fromAccount, toAccount, amount);

        Account source = accountRepo.findById(fromAccount);
        Account target = accountRepo.findById(toAccount);

        logger.info("Iniciando transferência | Origem: " + fromAccount +
                " | Destino: " + toAccount + " | Valor: R$ " + amount);

        // ETAPA 1 - DÉBITO (já registra transação e pode lançar
        // InsufficientFundsException)
        source.withdraw(amount);
        undoStack.push(buildRollbackEntry(fromAccount, amount));
        logger.info("Débito realizado na conta " + fromAccount);

        // ETAPA 2 - CRÉDITO
        try {
            target.deposit(amount);
            logger.info("Crédito realizado na conta " + toAccount);
            logger.audit("Transferência concluída | Origem: " + fromAccount +
                    " | Destino: " + toAccount + " | Valor: R$ " + amount);
        } catch (Exception transferError) {
            logger.error("Falha no crédito da transferência: " + transferError.getMessage());

            // Tenta reverter o débito
            rollback(source);

            // Lança exceção verificada com a causa original, para o chamador decidir o
            // tratamento
            throw new TransferFailedException(
                    "Falha ao creditar o valor na conta destino. Transferência revertida.",
                    transferError);
        }
    }

    /**
     * Executa rollback do débito.
     *
     * @param source conta origem (para estornar o débito)
     */
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

            // Estorna o débito (deposita de volta)
            source.deposit(amount);
            logger.audit("Rollback executado | Conta: " + accountNumber +
                    " | Valor estornado: R$ " + amount);

        } catch (Exception rollbackError) {
            logger.error("Falha crítica no rollback: " + rollbackError.getMessage());
        }
    }

    /**
     * Cria entrada padronizada de rollback.
     */
    private String buildRollbackEntry(String accountNumber, BigDecimal amount) {
        return "debit:" + accountNumber + ":" + amount.toPlainString();
    }

    /**
     * Validação completa da transferência.
     */
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

    /**
     * Validação básica de conta.
     */
    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Número da conta não pode ser vazio.");
        }
    }

    /**
     * Retorna quantidade de operações pendentes na pilha de rollback.
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
        logger.info("Histórico de rollback limpo.");
    }
}