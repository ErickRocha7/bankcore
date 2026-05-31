package service;

import domain.account.Account;
import domain.interfaces.InterestBearing;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;
import util.GenericUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

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

    public List<Account> getInterestBearingAccounts() {
        List<Account> all = accountRepo.findAll();
        return GenericUtils.filter(all, acc -> acc instanceof InterestBearing);
    }

    public BigDecimal calculateProjectedValue(Account account, int years) {
        if (account == null)
            throw new IllegalArgumentException("Conta não pode ser nula.");
        if (!(account instanceof InterestBearing))
            throw new IllegalArgumentException("Conta não rende juros.");
        validateYears(years);

        InterestBearing ib = (InterestBearing) account;
        BigDecimal ratePercent = ib.getInterestRate();
        BigDecimal rateDecimal = ratePercent.divide(BigDecimal.valueOf(100), MC);

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