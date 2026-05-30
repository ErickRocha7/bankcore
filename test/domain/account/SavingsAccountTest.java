package domain.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void deveCalcularProjecaoDeJuros() {
        SavingsAccount poupanca = new SavingsAccount("Carlos", new BigDecimal("1000.00"), new BigDecimal("5.0"));
        assertDoesNotThrow(() -> poupanca.calculateInterest(2));
    }

    @Test
    void deveRejeitarTaxaNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount("Teste", new BigDecimal("100.00"), new BigDecimal("-1.0")));
    }

    @Test
    void deveImplementarInterestBearing() {
        SavingsAccount poupanca = new SavingsAccount("Teste", BigDecimal.TEN, new BigDecimal("3.0"));
        assertEquals(new BigDecimal("3.0"), poupanca.getInterestRate());
    }
}