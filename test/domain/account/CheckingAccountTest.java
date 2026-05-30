package domain.account;

import exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CheckingAccountTest {

    private CheckingAccount conta;

    @BeforeEach
    void setUp() {
        conta = new CheckingAccount("Ana Costa", new BigDecimal("500.00"), new BigDecimal("15.00"));
    }

    @Test
    void deveAplicarTarifaDeManutencao() throws InsufficientFundsException {
        conta.applyMaintenanceFee();
        assertEquals(new BigDecimal("485.00"), conta.getBalance());
    }

    @Test
    void deveLancarExcecaoSeSaldoInsuficienteParaTarifa() {
        CheckingAccount pobre = new CheckingAccount("Sem Fundos", new BigDecimal("5.00"), new BigDecimal("15.00"));
        assertThrows(InsufficientFundsException.class, pobre::applyMaintenanceFee);
    }

    @Test
    void tarifaZeroNaoAlteraSaldo() throws InsufficientFundsException {
        CheckingAccount semTarifa = new CheckingAccount("Sem Tarifa", new BigDecimal("100.00"), BigDecimal.ZERO);
        semTarifa.applyMaintenanceFee();
        assertEquals(new BigDecimal("100.00"), semTarifa.getBalance());
    }

    @Test
    void calculateInterestApenasInformaQueNaoRende() {
        assertDoesNotThrow(() -> conta.calculateInterest(1));
    }
}