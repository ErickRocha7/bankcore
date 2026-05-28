package domain.account;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conta Poupança que rende juros compostos anuais.
 *
 * Cobertura:
 * 9 - Herança (extends Account)
 * 10 - Polimorfismo (override de calculateInterest)
 * 18 - Recursão (método privado compoundInterest)
 */
public class SavingsAccount extends Account {
    private double interestRate; // taxa de juros anual em percentual (ex: 5.0 para 5%)

    public SavingsAccount(String holderName, BigDecimal initialBalance, double interestRate) {
        super(holderName, initialBalance);
        if (interestRate < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");
        }
        this.interestRate = interestRate;
    }

    /**
     * Calcula e exibe a projeção do saldo com juros compostos ao longo de anos.
     * Utiliza método recursivo privado (double para o cálculo).
     *
     * @param years quantidade de anos
     */
    @Override
    public void calculateInterest(int years) {
        if (years < 0) {
            System.out.println("Número de anos inválido.");
            return;
        }
        double principal = balance.doubleValue(); // conversão para cálculo
        double rate = interestRate / 100.0;
        double futureValue = compoundInterest(principal, rate, years);
        System.out.printf("Poupança %s: após %d ano(s) a %.2f%% a.a., saldo projetado = R$ %s\n",
                getAccountNumber(), years, interestRate,
                BigDecimal.valueOf(futureValue).setScale(2, RoundingMode.HALF_EVEN).toPlainString());
    }

    // Método recursivo: cálculo de juros compostos (double)
    private double compoundInterest(double principal, double rate, int years) {
        if (years == 0) {
            return principal;
        } else {
            return (1 + rate) * compoundInterest(principal, rate, years - 1);
        }
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return "Poupança " + super.toString() + " | Juros: " + interestRate + "% a.a.";
    }
}