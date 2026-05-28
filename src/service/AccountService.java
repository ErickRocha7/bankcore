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

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço responsável pelas operações relacionadas às contas bancárias.
 */
public class AccountService {

    private final AccountRepository accountRepo;
    private final CustomerRepository customerRepo;
    private final AuditLogger logger;

    public AccountService(AccountRepository accountRepo, CustomerRepository customerRepo, AuditLogger logger) {
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
     * Cria uma nova conta.
     *
     * @param cpf            CPF do cliente
     * @param type           "poupanca" ou "corrente"
     * @param initialBalance saldo inicial (BigDecimal)
     * @param extraRateOrFee taxa de juros (poupança) ou tarifa (corrente) como
     *                       BigDecimal
     */
    public Account createAccount(String cpf, String type, BigDecimal initialBalance, BigDecimal extraRateOrFee) {
        validateCpf(cpf);
        validateAccountType(type);
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        if (extraRateOrFee.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Taxa ou tarifa não pode ser negativa.");

        Customer customer = customerRepo.findByCpf(cpf);
        Account account;

        if (type.equalsIgnoreCase("poupanca")) {
            // para poupança, extraRateOrFee representa a taxa de juros (double)
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
     */
    public void deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);
        Account account = accountRepo.findById(accountNumber);
        account.deposit(amount);
        logger.info("Depósito realizado | Conta: " + accountNumber + " | Valor: R$ " + amount);
    }

    /**
     * Realiza saque.
     */
    public void withdraw(String accountNumber, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException {
        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);
        Account account = accountRepo.findById(accountNumber);
        account.withdraw(amount);
        logger.info("Saque realizado | Conta: " + accountNumber + " | Valor: R$ " + amount);
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
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
        return accountNumber != null && !accountNumber.isBlank() && accountRepo.exists(accountNumber);
    }

    public void closeAccount(String accountNumber) throws AccountNotFoundException {
        validateAccountNumber(accountNumber);
        Account account = accountRepo.findById(accountNumber);
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("A conta não pode ser encerrada porque possui saldo diferente de zero.");
        }
        removeAccountFromOwner(account);
        accountRepo.delete(accountNumber);
        logger.info("Conta encerrada | Conta: " + accountNumber);
    }

    private void removeAccountFromOwner(Account account) {
        for (Customer customer : customerRepo.findAll()) {
            if (customer.getAccounts().contains(account)) {
                customer.removeAccount(account.getAccountNumber());
                logger.info("Conta " + account.getAccountNumber() + " removida do cliente " + customer.getCpf());
                return;
            }
        }
    }

    private void validateCpf(String cpf) {
        if (cpf == null || cpf.isBlank())
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
    }

    private void validateAccountType(String type) {
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Tipo de conta não pode ser vazio.");
        boolean valid = type.equalsIgnoreCase("poupanca") || type.equalsIgnoreCase("corrente");
        if (!valid)
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