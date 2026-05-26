// src/service/AccountService.java
package service;

import domain.account.Account;
import domain.account.CheckingAccount;
import domain.account.SavingsAccount;
import domain.customer.Customer;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;
import repository.CustomerRepository;

import java.util.List;

/**
 * Serviço responsável pelas operações relacionadas às contas bancárias.
 *
 * Funcionalidades:
 * - Criação de contas
 * - Depósito
 * - Saque
 * - Encerramento de contas
 * - Consulta de contas
 *
 * Capítulos abordados:
 * 6 - Métodos e encapsulamento
 * 8 - Composição entre objetos
 * 10 - Polimorfismo
 * 11 - Tratamento de exceções
 * 15 - Logging e persistência conceitual
 */
public class AccountService {

    private final AccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final AuditLogger logger;

    /**
     * Construtor principal.
     *
     * @param accountRepo  repositório de contas
     * @param customerRepo repositório de clientes
     * @param logger       logger de auditoria
     */
    public AccountService(
            AccountRepository accountRepo,
            CustomerRepository customerRepo,
            AuditLogger logger) {

        if (accountRepo == null) {
            throw new IllegalArgumentException("AccountRepository não pode ser nulo.");
        }

        if (customerRepo == null) {
            throw new IllegalArgumentException("CustomerRepository não pode ser nulo.");
        }

        if (logger == null) {
            throw new IllegalArgumentException("AuditLogger não pode ser nulo.");
        }

        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
        this.logger = logger;
    }

    /**
     * Cria uma nova conta para um cliente existente.
     *
     * @param cpf            CPF do cliente
     * @param type           Tipo da conta ("poupanca" ou "corrente")
     * @param initialBalance Saldo inicial
     * @param extraRateOrFee Taxa de juros (poupança) ou tarifa (corrente)
     * @return Conta criada
     */
    public Account createAccount(
            String cpf,
            String type,
            double initialBalance,
            double extraRateOrFee) {

        validateCpf(cpf);
        validateAccountType(type);

        if (initialBalance < 0) {
            throw new IllegalArgumentException(
                    "Saldo inicial não pode ser negativo.");
        }

        if (extraRateOrFee < 0) {
            throw new IllegalArgumentException(
                    "Taxa ou tarifa não pode ser negativa.");
        }

        Customer customer = customerRepo.findByCpf(cpf);

        Account account;

        if (type.equalsIgnoreCase("poupanca")) {

            account = new SavingsAccount(
                    customer.getName(),
                    initialBalance,
                    extraRateOrFee);

        } else {

            account = new CheckingAccount(
                    customer.getName(),
                    initialBalance,
                    extraRateOrFee);
        }

        accountRepo.add(account);

        customer.addAccount(account);

        logger.info(
                "Conta criada | Número: "
                        + account.getAccountNumber()
                        + " | Cliente: "
                        + customer.getCpf()
                        + " | Tipo: "
                        + type);

        return account;
    }

    /**
     * Realiza depósito em uma conta.
     *
     * @param accountNumber número da conta
     * @param amount        valor do depósito
     * @throws AccountNotFoundException se conta não existir
     */
    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException {

        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);

        Account account = accountRepo.findById(accountNumber);

        account.deposit(amount); // já registra a transação

        logger.info(
                "Depósito realizado | Conta: "
                        + accountNumber
                        + " | Valor: R$ "
                        + String.format("%.2f", amount));
    }

    /**
     * Realiza saque em uma conta.
     *
     * @param accountNumber número da conta
     * @param amount        valor do saque
     * @throws AccountNotFoundException   se conta não existir
     * @throws InsufficientFundsException se saldo insuficiente
     */
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException {

        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);

        Account account = accountRepo.findById(accountNumber);

        account.withdraw(amount); // já registra a transação

        logger.info(
                "Saque realizado | Conta: "
                        + accountNumber
                        + " | Valor: R$ "
                        + String.format("%.2f", amount));
    }

    /**
     * Busca conta pelo número.
     *
     * @param accountNumber número da conta
     * @return Conta encontrada
     * @throws AccountNotFoundException se conta não existir
     */
    public Account findAccount(String accountNumber)
            throws AccountNotFoundException {

        validateAccountNumber(accountNumber);

        return accountRepo.findById(accountNumber);
    }

    /**
     * Lista todas as contas cadastradas.
     *
     * @return Lista de contas
     */
    public List<Account> listAccounts() {
        return accountRepo.findAll();
    }

    /**
     * Retorna quantidade total de contas.
     *
     * @return total de contas
     */
    public int totalAccounts() {
        return accountRepo.count();
    }

    /**
     * Verifica existência de conta.
     *
     * @param accountNumber número da conta
     * @return true se existir
     */
    public boolean accountExists(String accountNumber) {

        if (accountNumber == null || accountNumber.isBlank()) {
            return false;
        }

        return accountRepo.exists(accountNumber);
    }

    /**
     * Encerra uma conta.
     *
     * Regras:
     * - Conta deve existir
     * - Saldo deve ser zero
     * - Conta é removida do cliente
     * - Conta é removida do repositório
     *
     * @param accountNumber número da conta
     * @throws AccountNotFoundException se conta não existir
     */
    public void closeAccount(String accountNumber)
            throws AccountNotFoundException {

        validateAccountNumber(accountNumber);

        Account account = accountRepo.findById(accountNumber);

        if (account.getBalance() != 0.0) {
            throw new IllegalStateException(
                    "A conta não pode ser encerrada porque possui saldo diferente de zero.");
        }

        removeAccountFromOwner(account);

        accountRepo.delete(accountNumber);

        logger.info(
                "Conta encerrada | Conta: "
                        + accountNumber);
    }

    /**
     * Remove conta do cliente proprietário.
     *
     * Como não existe relacionamento reverso explícito,
     * percorremos todos os clientes.
     */
    private void removeAccountFromOwner(Account account) {

        List<Customer> customers = customerRepo.findAll();

        for (Customer customer : customers) {

            if (customer.getAccounts().contains(account)) {

                // CORREÇÃO: chamar removeAccount com o número da conta (String)
                customer.removeAccount(account.getAccountNumber());

                logger.info(
                        "Conta "
                                + account.getAccountNumber()
                                + " removida do cliente "
                                + customer.getCpf());

                return;
            }
        }
    }

    /**
     * Valida CPF.
     */
    private void validateCpf(String cpf) {

        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException(
                    "CPF não pode ser nulo ou vazio.");
        }
    }

    /**
     * Valida tipo de conta.
     */
    private void validateAccountType(String type) {

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Tipo de conta não pode ser vazio.");
        }

        boolean valid = type.equalsIgnoreCase("poupanca")
                || type.equalsIgnoreCase("corrente");

        if (!valid) {
            throw new IllegalArgumentException(
                    "Tipo de conta inválido. Use 'poupanca' ou 'corrente'.");
        }
    }

    /**
     * Valida número da conta.
     */
    private void validateAccountNumber(String accountNumber) {

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Número da conta não pode ser vazio.");
        }
    }

    /**
     * Valida valores monetários positivos.
     */
    private void validatePositiveAmount(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "O valor deve ser maior que zero.");
        }
    }
}