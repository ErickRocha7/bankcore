package service;

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
 * - Controle de rollback atômico (desfaz débito se crédito falhar)
 * - Registro transacional
 * - Auditoria
 *
 * Capítulos abordados:
 * 8 - Composição
 * 11 - Exceções e rollback
 * 16 - Estruturas de dados
 * 21 - Estruturas genéricas customizadas (não mais utilizadas neste serviço)
 */
public class TransferService {

    private final AccountRepository accountRepo;
    private final AuditLogger logger;

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
    }

    /**
     * Realiza transferência entre contas com garantia de atomicidade.
     *
     * Fluxo:
     * 1. Validação dos parâmetros
     * 2. Débito da conta origem
     * 3. Crédito na conta destino
     * 4. Em caso de falha no crédito, executa rollback imediato do débito
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

        // ETAPA 1 - DÉBITO
        source.withdraw(amount);
        logger.info("Débito realizado na conta " + fromAccount);

        // Prepara dados para possível rollback (apenas armazena localmente)
        String rollbackData = buildRollbackEntry(fromAccount, amount);

        // ETAPA 2 - CRÉDITO
        try {
            target.deposit(amount);
            logger.info("Crédito realizado na conta " + toAccount);
            logger.audit("Transferência concluída | Origem: " + fromAccount +
                    " | Destino: " + toAccount + " | Valor: R$ " + amount);
        } catch (Exception transferError) {
            logger.error("Falha no crédito da transferência: " + transferError.getMessage());

            // Executa rollback do débito com os dados armazenados
            rollback(source, rollbackData);

            throw new TransferFailedException(
                    "Falha ao creditar o valor na conta destino. Transferência revertida.",
                    transferError);
        }
        // Se chegou aqui, a transferência foi bem-sucedida e nada mais é necessário.
        // Nenhum estado residual permanece no serviço.
    }

    /**
     * Executa o rollback do débito, depositando o valor de volta na conta origem.
     *
     * @param source       conta que sofreu o débito
     * @param rollbackData string codificada com os dados do débito
     */
    private void rollback(Account source, String rollbackData) {
        try {
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
            // Neste ponto, o sistema está inconsistente; a escalação humana seria
            // necessária.
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
}