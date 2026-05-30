package domain.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

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