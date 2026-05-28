package service;

import domain.account.Account;
import domain.account.CheckingAccount;
import domain.account.SavingsAccount;
import domain.customer.Customer;
import exceptions.AccountNotFoundException;
import exceptions.CustomerNotFoundException;
import exceptions.InsufficientFundsException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;
import repository.CustomerRepository;

import java.math.BigDecimal;
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

    public AccountService(AccountRepository accountRepo,
            CustomerRepository customerRepo,
            AuditLogger logger) {
        if (accountRepo == null)
            throw new IllegalArgumentException("AccountRepository não pode ser nulo.");
        if (customerRepo == null)
            throw new IllegalArgumentException("CustomerRepository não pode ser nulo.");
        if (logger == null)
            throw new IllegalArgumentException("AuditLogger não pode ser nulo.");
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
        this.logger = logger;
    }

    /**
     * Cria uma nova conta para um cliente existente.
     *
     * @param cpf            CPF do cliente
     * @param type           "poupanca" ou "corrente"
     * @param initialBalance saldo inicial (BigDecimal)
     * @param extraRateOrFee taxa de juros (poupança) ou tarifa (corrente) como
     *                       BigDecimal
     * @return Conta criada
     * @throws CustomerNotFoundException se o cliente não existir
     * @throws IllegalArgumentException  se parâmetros inválidos
     */
    public Account createAccount(String cpf, String type,
            BigDecimal initialBalance,
            BigDecimal extraRateOrFee)
            throws CustomerNotFoundException {

        validateCpf(cpf);
        validateAccountType(type);
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        if (extraRateOrFee.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Taxa ou tarifa não pode ser negativa.");

        // Lança CustomerNotFoundException se o cliente não existir
        Customer customer = customerRepo.findByCpf(cpf);
        Account account;

        if (type.equalsIgnoreCase("poupanca")) {
            double rate = extraRateOrFee.doubleValue();
            account = new SavingsAccount(customer.getName(), initialBalance, rate);
        } else {
            account = new CheckingAccount(customer.getName(), initialBalance, extraRateOrFee);
        }

        accountRepo.add(account);
        customer.addAccount(account);

        logger.info("Conta criada | Número: " + account.getAccountNumber() +
                " | Cliente: " + customer.getCpf() + " | Tipo: " + type);

        return account;
    }

    /**
     * Realiza depósito em uma conta.
     *
     * @param accountNumber número da conta
     * @param amount        valor do depósito (BigDecimal)
     * @throws AccountNotFoundException se a conta não existir
     */
    public void deposit(String accountNumber, BigDecimal amount)
            throws AccountNotFoundException {
        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);
        Account account = accountRepo.findById(accountNumber);
        account.deposit(amount);
        logger.info("Depósito realizado | Conta: " + accountNumber +
                " | Valor: R$ " + amount);
    }

    /**
     * Realiza saque em uma conta.
     *
     * @param accountNumber número da conta
     * @param amount        valor do saque (BigDecimal)
     * @throws AccountNotFoundException   se a conta não existir
     * @throws InsufficientFundsException se saldo insuficiente
     */
    public void withdraw(String accountNumber, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException {
        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);
        Account account = accountRepo.findById(accountNumber);
        account.withdraw(amount);
        logger.info("Saque realizado | Conta: " + accountNumber +
                " | Valor: R$ " + amount);
    }

    /**
     * Busca conta pelo número.
     *
     * @param accountNumber número da conta
     * @return Conta encontrada
     * @throws AccountNotFoundException se a conta não existir
     */
    public Account findAccount(String accountNumber)
            throws AccountNotFoundException {
        validateAccountNumber(accountNumber);
        return accountRepo.findById(accountNumber);
    }

    public List<Account> listAccounts() {
        return accountRepo.findAll();
    }

    public int totalAccounts() {
        return accountRepo.count();
    }

    public boolean accountExists(String accountNumber) {
        return accountNumber != null && !accountNumber.isBlank()
                && accountRepo.exists(accountNumber);
    }

    /**
     * Encerra uma conta.
     *
     * @param accountNumber número da conta
     * @throws AccountNotFoundException se a conta não existir
     */
    public void closeAccount(String accountNumber)
            throws AccountNotFoundException {
        validateAccountNumber(accountNumber);
        Account account = accountRepo.findById(accountNumber);

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "A conta não pode ser encerrada porque possui saldo diferente de zero.");
        }

        removeAccountFromOwner(account);
        accountRepo.delete(accountNumber);
        logger.info("Conta encerrada | Conta: " + accountNumber);
    }

    /**
     * Remove a conta do cliente proprietário, percorrendo todos os clientes.
     */
    private void removeAccountFromOwner(Account account) {
        List<Customer> customers = customerRepo.findAll();
        for (Customer customer : customers) {
            if (customer.getAccounts().contains(account)) {
                customer.removeAccount(account.getAccountNumber());
                logger.info("Conta " + account.getAccountNumber() +
                        " removida do cliente " + customer.getCpf());
                return;
            }
        }
    }

    // --- validações internas ---

    private void validateCpf(String cpf) {
        if (cpf == null || cpf.isBlank())
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
    }

    private void validateAccountType(String type) {
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Tipo de conta não pode ser vazio.");
        if (!type.equalsIgnoreCase("poupanca") && !type.equalsIgnoreCase("corrente"))
            throw new IllegalArgumentException("Tipo de conta inválido. Use 'poupanca' ou 'corrente'.");
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank())
            throw new IllegalArgumentException("Número da conta não pode ser vazio.");
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
    }
}