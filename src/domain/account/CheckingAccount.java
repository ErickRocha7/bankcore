package domain.account;

import exceptions.InsufficientFundsException;

/**
 * Conta Corrente que possui taxa de manutenção mensal e não rende juros.
 *
 * Cobertura:
 * 9  - Herança
 * 10 - Polimorfismo (calculateInterest)
 * 11 - Exceções (applyMaintenanceFee pode lançar InsufficientFundsException)
 */
public class CheckingAccount extends Account {
    private double maintenanceFee; // taxa mensal de manutenção

    public CheckingAccount(String holderName, double initialBalance, double maintenanceFee) {
        super(holderName, initialBalance);
        if (maintenanceFee < 0) {
            throw new IllegalArgumentException("Taxa de manutenção não pode ser negativa.");
        }
        this.maintenanceFee = maintenanceFee;
    }

    /**
     * Conta corrente normalmente não oferece rendimento.
     * Apenas informa o saldo atual.
     */
    @Override
    public void calculateInterest(int years) {
        System.out.println("Conta corrente não possui rendimento automático.");
        System.out.printf("Saldo atual da conta %s: R$ %.2f\n", getAccountNumber(), balance);
    }

    /**
     * Aplica a taxa de manutenção mensal, debitando do saldo.
     * @throws InsufficientFundsException se saldo insuficiente para cobrir a taxa
     */
    public void applyMaintenanceFee() throws InsufficientFundsException {
        if (balance < maintenanceFee) {
            throw new InsufficientFundsException(
                String.format("Saldo insuficiente para taxa de manutenção de R$ %.2f. Saldo: R$ %.2f",
                        maintenanceFee, balance));
        }
        balance -= maintenanceFee;
        addTransaction("Taxa de manutenção", -maintenanceFee);
        System.out.printf("Taxa de manutenção de R$ %.2f aplicada na conta %s.\n", maintenanceFee, getAccountNumber());
    }

    public double getMaintenanceFee() {
        return maintenanceFee;
    }

    @Override
    public String toString() {
        return "Corrente " + super.toString() + " | Taxa mensal: R$ " + maintenanceFee;
    }
}