package service;

import domain.account.Account;
import domain.interfaces.InterestBearing;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de juros e rendimentos.
 */
public class InterestService {

    private static final MathContext MC = MathContext.DECIMAL128;

    private final AccountRepository accountRepo;
    private final AuditLogger logger;

    public InterestService(AccountRepository accountRepo, AuditLogger logger) {
        if (accountRepo == null)
            throw new IllegalArgumentException("AccountRepository não pode ser nulo.");
        if (logger == null)
            throw new IllegalArgumentException("AuditLogger não pode ser nulo.");
        this.accountRepo = accountRepo;
        this.logger = logger;
    }

    public void projectInterest(int years) {
        validateYears(years);
        List<Account> interestAccounts = getInterestBearingAccounts();
        if (interestAccounts.isEmpty()) {
            logger.info("Nenhuma conta com rendimento para projeção.");
            return;
        }
        for (Account acc : interestAccounts) {
            try {
                acc.calculateInterest(years);
                logger.info("Projeção de juros exibida para conta " + acc.getAccountNumber());
            } catch (Exception e) {
                logger.error("Erro ao projetar juros da conta " + acc.getAccountNumber() + ": " + e.getMessage());
            }
        }
    }

    public List<Account> getInterestBearingAccounts() {
        List<Account> result = new ArrayList<>();
        for (Account acc : accountRepo.findAll()) {
            if (acc instanceof InterestBearing)
                result.add(acc);
        }
        return result;
    }

    /**
     * Calcula o valor projetado de uma conta que rende juros.
     *
     * @param account conta que implementa InterestBearing
     * @param years   número de anos
     * @return valor projetado com precisão BigDecimal
     */
    public BigDecimal calculateProjectedValue(Account account, int years) {
        if (account == null)
            throw new IllegalArgumentException("Conta não pode ser nula.");
        if (!(account instanceof InterestBearing))
            throw new IllegalArgumentException("Conta não rende juros.");
        validateYears(years);

        InterestBearing ib = (InterestBearing) account;
        BigDecimal ratePercent = ib.getInterestRate();
        BigDecimal rateDecimal = ratePercent.divide(BigDecimal.valueOf(100), MC);

        // Calcula (1 + rateDecimal)^years usando loop (evita perda de precisão)
        BigDecimal factor = BigDecimal.ONE;
        for (int i = 0; i < years; i++) {
            factor = factor.multiply(BigDecimal.ONE.add(rateDecimal), MC);
        }

        BigDecimal principal = account.getBalance();
        return principal.multiply(factor, MC).setScale(2, RoundingMode.HALF_EVEN);
    }

    public String generateProjectionReport(int years) {
        validateYears(years);
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== RELATÓRIO DE PROJEÇÃO ==========\n");

        List<Account> accounts = getInterestBearingAccounts();
        if (accounts.isEmpty()) {
            sb.append("Nenhuma conta com rendimento.\n");
            return sb.toString();
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Account acc : accounts) {
            BigDecimal projected = calculateProjectedValue(acc, years);
            total = total.add(projected);
            sb.append(String.format("Conta: %s | Titular: %s | Projeção: R$ %s\n",
                    acc.getAccountNumber(), acc.getHolderName(), projected.toPlainString()));
        }
        sb.append("-------------------------------------------\n");
        sb.append(String.format("Total projetado: R$ %s\n", total.toPlainString()));

        logger.info("Relatório de projeção de juros gerado.");
        return sb.toString();
    }

    public boolean isInterestBearing(Account account) {
        return account instanceof InterestBearing;
    }

    public int countInterestBearingAccounts() {
        return getInterestBearingAccounts().size();
    }

    private void validateYears(int years) {
        if (years <= 0)
            throw new IllegalArgumentException("Anos deve ser maior que zero.");
    }
}