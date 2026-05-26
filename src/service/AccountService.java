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

/**
 * Serviço responsável pelas operações básicas de conta:
 * criação, depósito, saque e encerramento.
 *
 * Capítulos:
 * 6, 8 - Métodos, encapsulamento, composição
 * 11 - Tratamento de exceções
 * 15 - Uso de logger (AuditLogger)
 */
public class AccountService {
    private AccountRepository accountRepo;
    private CustomerRepository customerRepo;
    private AuditLogger logger;

    public AccountService(AccountRepository accountRepo, CustomerRepository customerRepo, AuditLogger logger) {
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
        this.logger = logger;
    }

    /**
     * Cria uma nova conta para um cliente existente.
     * 
     * @param cpf            CPF do cliente
     * @param type           "poupanca" ou "corrente"
     * @param initialBalance saldo inicial
     * @param extraRateOrFee taxa de juros (poupança) ou tarifa mensal (corrente)
     * @return conta criada
     * @throws IllegalArgumentException se tipo inválido ou cliente não existir
     */
    public Account createAccount(String cpf, String type, double initialBalance, double extraRateOrFee) {
        Customer customer = customerRepo.findByCpf(cpf); // lança exceção se não encontrar
        Account account;
        if (type.equalsIgnoreCase("poupanca")) {
            account = new SavingsAccount(customer.getName(), initialBalance, extraRateOrFee);
        } else if (type.equalsIgnoreCase("corrente")) {
            account = new CheckingAccount(customer.getName(), initialBalance, extraRateOrFee);
        } else {
            throw new IllegalArgumentException("Tipo de conta inválido. Use 'poupanca' ou 'corrente'.");
        }
        accountRepo.add(account);
        customer.addAccount(account);
        logger.info("Conta " + account.getAccountNumber() + " criada para cliente " + customer.getCpf());
        return account;
    }

    /**
     * Realiza depósito em uma conta.
     */
    public void deposit(String accountNumber, double amount) throws AccountNotFoundException {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor de depósito deve ser positivo.");
        Account account = accountRepo.findById(accountNumber);
        account.deposit(amount);
        account.recordTransaction("Depósito", amount); // método a ser adicionado em Account
        logger.info("Depósito de R$ " + amount + " na conta " + accountNumber);
    }

    /**
     * Realiza saque em uma conta.
     */
    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InsufficientFundsException {
        if (amount <= 0)
            throw new IllegalArgumentException("Valor de saque deve ser positivo.");
        Account account = accountRepo.findById(accountNumber);
        account.withdraw(amount);
        account.recordTransaction("Saque", -amount);
        logger.info("Saque de R$ " + amount + " da conta " + accountNumber);
    }

    /**
     * Encerra uma conta, removendo-a do cliente e do repositório.
     */
    public void closeAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accountRepo.findById(accountNumber);
        // Para simplificar, não verificamos saldo zero; poderíamos exigir.
        // Encontrar o cliente dono: como não há referência inversa, teríamos que
        // iterar.
        // Alternativa: cliente tem lista de contas; podemos remover de lá também.
        // Para este exercício, apenas removemos do repositório.
        accountRepo.delete(accountNumber);
        logger.info("Conta " + accountNumber + " encerrada.");
    }
}