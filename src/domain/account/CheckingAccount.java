package domain.account;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    private BigDecimal maintenanceFee; // BigDecimal

    /**
     * Construtor da conta corrente.
     *
     * @param holderName     titular da conta
     * @param initialBalance saldo inicial (BigDecimal)
     * @param maintenanceFee taxa mensal de manutenção (BigDecimal)
     */
    public CheckingAccount(String holderName, BigDecimal initialBalance, BigDecimal maintenanceFee) {
        super(holderName, initialBalance);
        validateNonNegativeValue(maintenanceFee, "Taxa de manutenção");
        this.maintenanceFee = maintenanceFee;
    }

    @Override
    public void calculateInterest(int years) {
        System.out.printf("Conta corrente %s não possui rendimento automático.%n", getAccountNumber());
        System.out.printf("Saldo atual: R$ %s%n",
                balance.setScale(2, RoundingMode.HALF_EVEN).toPlainString());
    }

    /**
     * Aplica tarifa de manutenção mensal.
     *
     * @throws InsufficientFundsException se saldo insuficiente
     */
    public void applyMaintenanceFee() throws InsufficientFundsException {
        if (maintenanceFee.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (balance.compareTo(maintenanceFee) < 0) {
            throw new InsufficientFundsException(
                    String.format("Saldo insuficiente para cobrança da tarifa mensal. "
                            + "Saldo atual: R$ %s | Tarifa: R$ %s",
                            balance.setScale(2, RoundingMode.HALF_EVEN).toPlainString(),
                            maintenanceFee.setScale(2, RoundingMode.HALF_EVEN).toPlainString()));
        }
        balance = balance.subtract(maintenanceFee);
        addTransaction("Tarifa de manutenção", maintenanceFee.negate());
    }

    public BigDecimal getMaintenanceFee() {
        return maintenanceFee;
    }

    public void setMaintenanceFee(BigDecimal maintenanceFee) {
        validateNonNegativeValue(maintenanceFee, "Taxa de manutenção");
        this.maintenanceFee = maintenanceFee;
    }

    @Override
    public String toString() {
        return String.format("Conta Corrente | %s | Tarifa mensal: R$ %s",
                super.toString(),
                maintenanceFee.setScale(2, RoundingMode.HALF_EVEN).toPlainString());
    }
}