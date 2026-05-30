package service;

import domain.customer.Customer;
import exceptions.*;
import infrastructure.logging.AuditLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.CustomerRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {

    private AccountRepository accountRepo;
    private TransferService transferService;
    private AccountService accountService;
    private String contaOrigem, contaDestino;

    @BeforeEach
    void setUp() throws CustomerNotFoundException {
        accountRepo = new AccountRepository();
        CustomerRepository customerRepo = new CustomerRepository();
        AuditLogger logger = new AuditLogger("logs/test_transfer.log");
        transferService = new TransferService(accountRepo, logger);
        accountService = new AccountService(accountRepo, customerRepo, logger);

        Customer cliente = new Customer("987.654.321-00", "Dono", "senha");
        customerRepo.add(cliente);

        accountService.createAccount(cliente.getCpf(), "corrente", new BigDecimal("500.00"), BigDecimal.ZERO);
        accountService.createAccount(cliente.getCpf(), "corrente", new BigDecimal("100.00"), BigDecimal.ZERO);

        contaOrigem = cliente.getAccounts().get(0).getAccountNumber();
        contaDestino = cliente.getAccounts().get(1).getAccountNumber();
    }

    @Test
    void deveTransferirComSucesso() throws AccountNotFoundException, InsufficientFundsException, TransferFailedException {
        transferService.transfer(contaOrigem, contaDestino, new BigDecimal("200.00"));

        assertEquals(new BigDecimal("300.00"), accountRepo.findById(contaOrigem).getBalance());
        assertEquals(new BigDecimal("300.00"), accountRepo.findById(contaDestino).getBalance());
    }

    @Test
    void deveRejeitarTransferenciaSaldoInsuficiente() {
        assertThrows(InsufficientFundsException.class,
                () -> transferService.transfer(contaOrigem, contaDestino, new BigDecimal("600.00")));
    }

    @Test
    void deveRejeitarValorNegativoOuZero() {
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(contaOrigem, contaDestino, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(contaOrigem, contaDestino, new BigDecimal("-10.00")));
    }

    @Test
    void deveRejeitarContasIguais() {
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(contaOrigem, contaOrigem, new BigDecimal("50.00")));
    }

    @Test
    void deveRejeitarContaOrigemInexistente() {
        assertThrows(AccountNotFoundException.class,
                () -> transferService.transfer("00000", contaDestino, new BigDecimal("50.00")));
    }

    @Test
    void deveRejeitarContaDestinoInexistente() {
        assertThrows(AccountNotFoundException.class,
                () -> transferService.transfer(contaOrigem, "00000", new BigDecimal("50.00")));
    }
}