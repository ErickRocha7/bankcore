package domain.account;

import domain.interfaces.InterestBearing;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavingsAccount extends Account implements InterestBearing {

    private static final long serialVersionUID = 1L;
    private BigDecimal interestRate;

    public SavingsAccount(String holderName, BigDecimal initialBalance, BigDecimal interestRate) {
        super(holderName, initialBalance);
        if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");
        }
        this.interestRate = interestRate;
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