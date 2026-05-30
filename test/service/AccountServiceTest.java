package service;

import domain.account.Account;
import domain.account.CheckingAccount;
import domain.account.SavingsAccount;
import domain.customer.Customer;
import exceptions.AccountNotFoundException;
import exceptions.CustomerNotFoundException;
import exceptions.InsufficientFundsException;
import infrastructure.logging.AuditLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.CustomerRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountRepository accountRepo;
    private CustomerRepository customerRepo;
    private AccountService service;
    private Customer cliente;

    @BeforeEach
    void setUp() throws CustomerNotFoundException {
        accountRepo = new AccountRepository();
        customerRepo = new CustomerRepository();
        AuditLogger logger = new AuditLogger("logs/test_audit.log");
        service = new AccountService(accountRepo, customerRepo, logger);

        cliente = new Customer("123.456.789-09", "Teste Cliente", "senha123");
        customerRepo.add(cliente);
    }

    @Test
    void deveCriarContaCorrente() throws CustomerNotFoundException {
        Account conta = service.createAccount(cliente.getCpf(), "corrente", new BigDecimal("1000.00"),
                new BigDecimal("20.00"));
        assertTrue(conta instanceof CheckingAccount);
        assertEquals(new BigDecimal("1000.00"), conta.getBalance());
        assertTrue(cliente.getAccounts().contains(conta));
    }

    @Test
    void deveCriarContaPoupanca() throws CustomerNotFoundException {
        Account conta = service.createAccount(cliente.getCpf(), "poupanca", new BigDecimal("500.00"),
                new BigDecimal("2.5"));
        assertTrue(conta instanceof SavingsAccount);
        assertEquals(new BigDecimal("500.00"), conta.getBalance());
    }

    @Test
    void deveDepositarComSucesso() throws AccountNotFoundException, CustomerNotFoundException {
        service.createAccount(cliente.getCpf(), "corrente", new BigDecimal("100.00"), BigDecimal.ZERO);
        String num = cliente.getAccounts().get(0).getAccountNumber();
        service.deposit(num, new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), accountRepo.findById(num).getBalance());
    }

    @Test
    void deveSacarComSucesso() throws AccountNotFoundException, InsufficientFundsException, CustomerNotFoundException {
        service.createAccount(cliente.getCpf(), "corrente", new BigDecimal("200.00"), BigDecimal.ZERO);
        String num = cliente.getAccounts().get(0).getAccountNumber();
        service.withdraw(num, new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), accountRepo.findById(num).getBalance());
    }

    @Test
    void deveLancarExcecaoAoDepositarEmContaInexistente() {
        assertThrows(AccountNotFoundException.class,
                () -> service.deposit("99999", new BigDecimal("100.00")));
    }

    @Test
    void deveLancarExcecaoAoCriarContaClienteInexistente() {
        assertThrows(CustomerNotFoundException.class,
                () -> service.createAccount("000.000.000-00", "corrente", BigDecimal.TEN, BigDecimal.ZERO));
    }
}