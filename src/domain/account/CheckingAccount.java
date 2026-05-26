// src/domain/account/CheckingAccount.java
package domain.account;

import exceptions.InsufficientFundsException;

/**
 * Representa uma conta corrente bancária.
 *
 * Características:
 * - Possui tarifa mensal de manutenção
 * - Não possui rendimento automático
 * - Permite débito de taxa administrativa
 *
 * Capítulos abordados:
 * 9 - Herança
 * 10 - Polimorfismo
 * 11 - Exceções personalizadas
 * 14 - Strings e formatação
 */
public class CheckingAccount extends Account {

    private static final long serialVersionUID = 1L;

    /**
     * Taxa mensal de manutenção da conta.
     */
    private double maintenanceFee;

    /**
     * Construtor da conta corrente.
     *
     * @param holderName     titular da conta
     * @param initialBalance saldo inicial
     * @param maintenanceFee taxa mensal de manutenção
     */
    public CheckingAccount(
            String holderName,
            double initialBalance,
            double maintenanceFee) {

        super(holderName, initialBalance);

        validateNonNegativeValue(maintenanceFee, "Taxa de manutenção");

        this.maintenanceFee = maintenanceFee;
    }

    /**
     * Conta corrente não gera rendimento automático.
     *
     * @param years anos informados
     */
    @Override
    public void calculateInterest(int years) {

        System.out.printf(
                "Conta corrente %s não possui rendimento automático.%n",
                getAccountNumber());

        System.out.printf(
                "Saldo atual: R$ %,.2f%n",
                balance);
    }

    /**
     * Aplica tarifa de manutenção mensal.
     *
     * @throws InsufficientFundsException se saldo insuficiente
     */
    public void applyMaintenanceFee() throws InsufficientFundsException {

        if (maintenanceFee <= 0) {
            return;
        }

        if (balance < maintenanceFee) {
            throw new InsufficientFundsException(
                    String.format(
                            "Saldo insuficiente para cobrança da tarifa mensal. "
                                    + "Saldo atual: R$ %,.2f | Tarifa: R$ %,.2f",
                            balance,
                            maintenanceFee));
        }

        balance -= maintenanceFee;

        addTransaction("Tarifa de manutenção", -maintenanceFee);
    }

    /**
     * Retorna a taxa de manutenção mensal.
     *
     * @return tarifa mensal
     */
    public double getMaintenanceFee() {
        return maintenanceFee;
    }

    /**
     * Atualiza a taxa de manutenção.
     *
     * @param maintenanceFee nova tarifa
     */
    public void setMaintenanceFee(double maintenanceFee) {

        validateNonNegativeValue(maintenanceFee, "Taxa de manutenção");

        this.maintenanceFee = maintenanceFee;
    }

    @Override
    public String toString() {

        return String.format(
                "Conta Corrente | %s | Tarifa mensal: R$ %,.2f",
                super.toString(),
                maintenanceFee);
    }
}