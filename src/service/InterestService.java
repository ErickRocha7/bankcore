// src/service/InterestService.java
package service;

import domain.account.Account;
import domain.interfaces.InterestBearing;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de juros e rendimentos.
 *
 * Responsável por:
 * - Projeção de juros (saída no console)
 * - Geração de relatórios de projeção
 * - Filtragem de contas que rendem juros
 * - Cálculo manual de projeção (usando taxa de juros)
 *
 * Capítulos abordados:
 * 10 - Interfaces e polimorfismo
 * 11 - Tratamento de exceções
 * 18 - Recursão (indiretamente, via SavingsAccount)
 */
public class InterestService {

    private final AccountRepository accountRepo;
    private final AuditLogger logger;

    public InterestService(AccountRepository accountRepo, AuditLogger logger) {
        if (accountRepo == null) {
            throw new IllegalArgumentException("AccountRepository não pode ser nulo.");
        }
        if (logger == null) {
            throw new IllegalArgumentException("AuditLogger não pode ser nulo.");
        }
        this.accountRepo = accountRepo;
        this.logger = logger;
    }

    /**
     * Projeta juros (apenas exibe no console) para todas as contas que rendem.
     *
     * @param years número de anos (deve ser > 0)
     */
    public void projectInterest(int years) {
        validateYears(years);

        List<Account> interestAccounts = getInterestBearingAccounts();
        if (interestAccounts.isEmpty()) {
            logger.info("Nenhuma conta com rendimento para projeção.");
            return;
        }

        for (Account acc : interestAccounts) {
            try {
                acc.calculateInterest(years); // método void – exibe no console
                logger.info("Projeção de juros exibida para conta " + acc.getAccountNumber());
            } catch (Exception e) {
                logger.error("Erro ao projetar juros da conta " + acc.getAccountNumber() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Retorna as contas que implementam InterestBearing.
     *
     * @return lista de contas com rendimento
     */
    public List<Account> getInterestBearingAccounts() {
        List<Account> result = new ArrayList<>();
        for (Account acc : accountRepo.findAll()) {
            if (acc instanceof InterestBearing) {
                result.add(acc);
            }
        }
        return result;
    }

    /**
     * Calcula o valor projetado de uma conta com juros compostos.
     *
     * @param account conta que rende juros (precisa implementar InterestBearing)
     * @param years   número de anos
     * @return valor projetado
     */
    public double calculateProjectedValue(Account account, int years) {
        if (account == null) {
            throw new IllegalArgumentException("Conta não pode ser nula.");
        }
        if (!(account instanceof InterestBearing)) {
            throw new IllegalArgumentException("Conta não rende juros.");
        }
        validateYears(years);

        InterestBearing ib = (InterestBearing) account;
        double rate = ib.getInterestRate() / 100.0;
        double principal = account.getBalance();
        // Fórmula de juros compostos (mesma lógica da SavingsAccount)
        double projected = principal * Math.pow(1 + rate, years);
        return projected;
    }

    /**
     * Gera relatório textual com projeção de todas as contas.
     *
     * @param years anos de projeção
     * @return string formatada
     */
    public String generateProjectionReport(int years) {
        validateYears(years);

        StringBuilder sb = new StringBuilder();
        sb.append("\n========== RELATÓRIO DE PROJEÇÃO ==========\n");

        List<Account> accounts = getInterestBearingAccounts();
        if (accounts.isEmpty()) {
            sb.append("Nenhuma conta com rendimento.\n");
            return sb.toString();
        }

        double total = 0.0;
        for (Account acc : accounts) {
            double projected = calculateProjectedValue(acc, years);
            total += projected;
            sb.append(String.format("Conta: %s | Titular: %s | Projeção: R$ %.2f\n",
                    acc.getAccountNumber(), acc.getHolderName(), projected));
        }
        sb.append("-------------------------------------------\n");
        sb.append(String.format("Total projetado: R$ %.2f\n", total));

        logger.info("Relatório de projeção de juros gerado.");
        return sb.toString();
    }

    /**
     * Verifica se uma conta rende juros.
     */
    public boolean isInterestBearing(Account account) {
        return account instanceof InterestBearing;
    }

    /**
     * Retorna a quantidade de contas que rendem juros.
     */
    public int countInterestBearingAccounts() {
        return getInterestBearingAccounts().size();
    }

    private void validateYears(int years) {
        if (years <= 0) {
            throw new IllegalArgumentException("Anos deve ser maior que zero.");
        }
    }
}