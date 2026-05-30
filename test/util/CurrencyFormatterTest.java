package util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyFormatterTest {

    @Test
    void deveFormatarValorMonetarioPadraoBrasileiro() {
        assertEquals("R$ 1.500,50", CurrencyFormatter.format(new BigDecimal("1500.50")));
        assertEquals("R$ 0,00", CurrencyFormatter.format(BigDecimal.ZERO));
        assertEquals("R$ 0,00", CurrencyFormatter.format(null));
    }

    @Test
    void deveParsearFormatoBrasileiroValido() {
        assertEquals(new BigDecimal("1500.50"), CurrencyFormatter.parse("1.500,50"));
        assertEquals(new BigDecimal("1500.50"), CurrencyFormatter.parse("1500,50"));
        assertEquals(new BigDecimal("0.01"), CurrencyFormatter.parse("0,01"));
    }

    @Test
    void deveRejeitarFormatoComPontoDecimal() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyFormatter.parse("1500.50"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyFormatter.parse("1,500.00"));
    }

    @Test
    void deveRejeitarEntradaVazia() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyFormatter.parse(""));
        assertThrows(IllegalArgumentException.class, () -> CurrencyFormatter.parse(null));
    }

    @Test
    void deveRejeitarFormatoSemCentavos() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyFormatter.parse("1500"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyFormatter.parse("1.500"));
    }
}