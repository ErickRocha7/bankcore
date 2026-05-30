package domain.account;

import domain.enums.TransactionType;
import exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new CheckingAccount("João Silva", new BigDecimal("1000.00"), BigDecimal.ZERO);
    }

    @Test
    void deveCriarContaComSaldoInicial() {
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
        assertEquals("João Silva", account.getHolderName());
        assertNotNull(account.getAccountNumber());
        assertTrue(account.getAccountNumber().matches("\\d{5}"));
    }

    @Test
    void deveDepositarValorPositivo() {
        account.deposit(new BigDecimal("250.00"));
        assertEquals(new BigDecimal("1250.00"), account.getBalance());
        assertEquals(2, account.getTransactions().size()); // abertura + depósito
    }

    @Test
    void deveSacarComSaldoSuficiente() throws InsufficientFundsException {
        account.withdraw(new BigDecimal("300.00"));
        assertEquals(new BigDecimal("700.00"), account.getBalance());
        assertEquals(2, account.getTransactions().size());
    }

    @Test
    void deveLancarExcecaoAoSacarMaisQueSaldo() {
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new BigDecimal("1500.00")));
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    @Test
    void deveRejeitarDepositoNegativoOuZero() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(new BigDecimal("-10.00")));
    }

    @Test
    void deveRejeitarSaqueNegativoOuZero() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(new BigDecimal("-5.00")));
    }

    @Test
    void deveRegistrarTransacaoManual() {
        // Transação manual com tipo FEE (tarifa) – sinal negativo no extrato
        account.recordTransaction(TransactionType.FEE, "Taxa extra", new BigDecimal("50.00"));
        assertEquals(2, account.getTransactions().size());
    }

    @Test
    void deveGerarExtratoNaoVazio() {
        account.deposit(new BigDecimal("100.00"));
        String statement = account.getStatement();
        assertTrue(statement.contains("Abertura de conta"));
        assertTrue(statement.contains("Depósito"));
    }

    @Test
    void equalsBaseadoNoNumeroDaConta() {
        Account outra = new CheckingAccount("Maria", new BigDecimal("100.00"), BigDecimal.ZERO);
        assertNotEquals(account, outra);
    }

    @Test
    void naoDevePermitirOperacaoEmContaFechada() {
        account.setStatus(domain.enums.AccountStatus.CLOSED);
        assertThrows(IllegalStateException.class, () -> account.deposit(new BigDecimal("10.00")));
        assertThrows(IllegalStateException.class, () -> account.withdraw(new BigDecimal("10.00")));
    }
}