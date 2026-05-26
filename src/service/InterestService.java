// src/service/InterestService.java
package service;

import domain.account.Account;
import domain.interfaces.InterestBearing;
import infrastructure.logging.AuditLogger;
import repository.AccountRepository;

/**
 * Serviço para cálculo e aplicação de juros.
 * 
 * Capítulos:
 * 10 - Polimorfismo, interfaces (InterestBearing)
 * 18 - Recursão (método calculateInterest de SavingsAccount)
 */
public class InterestService {
    private AccountRepository accountRepo;
    private AuditLogger logger;

    public InterestService(AccountRepository accountRepo, AuditLogger logger) {
        this.accountRepo = accountRepo;
        this.logger = logger;
    }

    /**
     * Calcula e exibe projeção de juros para todas as contas que rendem juros.
     * 
     * @param years anos de projeção
     */
    public void projectInterest(int years) {
        for (Account acc : accountRepo.findAll()) {
            if (acc instanceof InterestBearing) {
                acc.calculateInterest(years); // polimorfismo
                logger.info("Projeção de juros para conta " + acc.getAccountNumber());
            }
        }
    }
}