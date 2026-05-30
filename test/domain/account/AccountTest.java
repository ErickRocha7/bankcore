package domain.account;

import exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe abstrata Account através de uma implementação
 * concreta.
 */
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
        assertEquals(2, countTransactions(account)); // abertura + depósito
    }

    @Test
    void deveSacarComSaldoSuficiente() throws InsufficientFundsException {
        account.withdraw(new BigDecimal("300.00"));
        assertEquals(new BigDecimal("700.00"), account.getBalance());
        assertEquals(2, countTransactions(account));
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
        account.recordTransaction("Taxa extra", new BigDecimal("-50.00"));
        assertEquals(2, countTransactions(account));
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

    private int countTransactions(Account acc) {
        int count = 0;
        for (String t : acc.getTransactions()) {
            count++;
        }
        return count;
    }
}