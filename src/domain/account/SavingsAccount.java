package domain.account;

import domain.interfaces.InterestBearing;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Conta Poupança que rende juros compostos anuais.
 *
 * Cobertura:
 * 9 - Herança (extends Account)
 * 10 - Polimorfismo (override de calculateInterest)
 * 18 - Recursão (método privado compoundFactor)
 */
public class SavingsAccount extends Account implements InterestBearing {

    private static final long serialVersionUID = 1L;
    private static final MathContext MC = MathContext.DECIMAL128;

    private BigDecimal interestRate; // taxa de juros anual percentual (ex: 5.0)

    /**
     * Construtor da conta poupança.
     *
     * @param holderName     titular da conta
     * @param initialBalance saldo inicial (BigDecimal)
     * @param interestRate   taxa de juros anual percentual (BigDecimal)
     */
    public SavingsAccount(String holderName, BigDecimal initialBalance, BigDecimal interestRate) {
        super(holderName, initialBalance);
        if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");
        }
        this.interestRate = interestRate;
    }

    /**
     * Calcula e exibe a projeção do saldo com juros compostos ao longo de anos.
     * Utiliza método recursivo privado com BigDecimal para máxima precisão.
     *
     * @param years quantidade de anos
     */
    @Override
    public void calculateInterest(int years) {
        if (years < 0) {
            System.out.println("Número de anos inválido.");
            return;
        }

        BigDecimal rateDecimal = interestRate.divide(BigDecimal.valueOf(100), MC); // taxa decimal
        BigDecimal factor = compoundFactor(rateDecimal, years);
        BigDecimal futureValue = balance.multiply(factor, MC)
                .setScale(2, RoundingMode.HALF_EVEN);

        System.out.printf("Poupança %s: após %d ano(s) a %s%% a.a., saldo projetado = R$ %s\n",
                getAccountNumber(), years,
                interestRate.setScale(2, RoundingMode.HALF_EVEN).toPlainString(),
                futureValue.toPlainString());
    }

    /**
     * Calcula recursivamente o fator de juros compostos: (1 + rate)^years.
     *
     * @param rate  taxa de juros na forma decimal (ex: 0.05 para 5%)
     * @param years número de anos
     * @return fator multiplicativo como BigDecimal
     */
    private BigDecimal compoundFactor(BigDecimal rate, int years) {
        if (years == 0) {
            return BigDecimal.ONE;
        }
        return BigDecimal.ONE.add(rate).multiply(compoundFactor(rate, years - 1), MC);
    }

    @Override
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return "Poupança " + super.toString() + " | Juros: " +
                interestRate.setScale(2, RoundingMode.HALF_EVEN).toPlainString() + "% a.a.";
    }
}