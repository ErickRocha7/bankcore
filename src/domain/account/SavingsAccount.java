package domain.account;

/**
 * Conta Poupança que rende juros compostos anuais.
 * Implementa cálculo recursivo de juros e pode implementar InterestBearing no
 * futuro.
 *
 * Cobertura:
 * 9 - Herança (extends Account)
 * 10 - Polimorfismo (override de calculateInterest)
 * 18 - Recursão (método privado compoundInterest)
 */
public class SavingsAccount extends Account {
    private double interestRate; // taxa de juros anual em percentual (ex: 5.0 para 5%)

    public SavingsAccount(String holderName, double initialBalance, double interestRate) {
        super(holderName, initialBalance);
        if (interestRate < 0) {
            throw new IllegalArgumentException("Taxa de juros não pode ser negativa.");
        }
        this.interestRate = interestRate;
    }

    /**
     * Calcula e exibe a projeção do saldo com juros compostos ao longo de anos.
     * Utiliza método recursivo privado.
     * 
     * @param years quantidade de anos
     */
    @Override
    public void calculateInterest(int years) {
        if (years < 0) {
            System.out.println("Número de anos inválido.");
            return;
        }
        double futureValue = compoundInterest(balance, interestRate / 100.0, years);
        System.out.printf("Poupança %s: após %d ano(s) a %.2f%% a.a., saldo projetado = R$ %.2f\n",
                getAccountNumber(), years, interestRate, futureValue);
    }

    // Método recursivo: cálculo de juros compostos (Cap 18)
    private double compoundInterest(double principal, double rate, int years) {
        if (years == 0) {
            return principal;
        } else {
            return (1 + rate) * compoundInterest(principal, rate, years - 1);
        }
    }

    // Getters específicos
    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return "Poupança " + super.toString() + " | Juros: " + interestRate + "% a.a.";
    }
}