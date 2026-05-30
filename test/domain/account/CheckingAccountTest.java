package domain.account;

import domain.enums.TransactionType;
import domain.transaction.Transaction;
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
        // Verifica se a transação foi registrada com o tipo FEE
        assertEquals(2, conta.getTransactions().size()); // abertura + tarifa
        Transaction ultima = conta.getTransactions().get(1);
        assertEquals(TransactionType.FEE, ultima.getType());
        // O valor no extrato deve ser negativo (saída de dinheiro)
        assertEquals(new BigDecimal("-15.00"), ultima.getSignedAmount());
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
        // Não deve gerar transação adicional
        assertEquals(1, semTarifa.getTransactions().size()); // apenas abertura
    }

    // Teste removido: calculateInterestApenasInformaQueNaoRende()
    // O método calculateInterest não existe mais – sua responsabilidade foi movida
    // para o InterestService, e não há necessidade de testá-lo na conta corrente.
}