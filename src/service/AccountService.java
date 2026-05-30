package service;

import domain.account.Account;
import domain.account.CheckingAccount;
import domain.account.SavingsAccount;
import domain.customer.Customer;
import domain.enums.AccountStatus;
import exceptions.AccountNotFoundException;
import exceptions.CustomerNotFoundException;
import exceptions.InsufficientFundsException;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;
import repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.List;

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

        Customer customer = customerRepo.findByCpf(cpf);
        Account account;

        if (type.equalsIgnoreCase("poupanca")) {
            account = new SavingsAccount(customer.getName(), initialBalance, extraRateOrFee);
        } else {
            account = new CheckingAccount(customer.getName(), initialBalance, extraRateOrFee);
        }

        accountRepo.add(account);
        customer.addAccount(account);

        logger.info("Conta criada | Número: " + account.getAccountNumber() +
                " | Cliente: " + customer.getCpf() + " | Tipo: " + type);

        return account;
    }

    public void deposit(String accountNumber, BigDecimal amount)
            throws AccountNotFoundException {
        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);
        Account account = accountRepo.findById(accountNumber);
        account.deposit(amount);
        logger.info("Depósito realizado | Conta: " + accountNumber +
                " | Valor: R$ " + amount);
    }

    public void withdraw(String accountNumber, BigDecimal amount)
            throws AccountNotFoundException, InsufficientFundsException {
        validateAccountNumber(accountNumber);
        validatePositiveAmount(amount);
        Account account = accountRepo.findById(accountNumber);
        account.withdraw(amount);
        logger.info("Saque realizado | Conta: " + accountNumber +
                " | Valor: R$ " + amount);
    }

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

    public void closeAccount(String accountNumber)
            throws AccountNotFoundException {
        validateAccountNumber(accountNumber);
        Account account = accountRepo.findById(accountNumber);

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "A conta não pode ser encerrada porque possui saldo diferente de zero.");
        }

        // Altera o status ao invés de remover do repositório
        account.setStatus(AccountStatus.CLOSED);
        removeAccountFromOwner(account);
        logger.info("Conta encerrada | Conta: " + accountNumber);
    }

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

    // --- validações internas (inalteradas) ---
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