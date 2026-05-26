// src/service/AuthenticationService.java
package service;

import domain.customer.Customer;
import exceptions.UnauthorizedException;
import infrastructure.logging.AuditLogger;
import repository.CustomerRepository;

/**
 * Serviço de autenticação de clientes.
 * 
 * Capítulos:
 * 11 - Exceções (UnauthorizedException)
 * 6 - Métodos
 */
public class AuthenticationService {
    private CustomerRepository customerRepo;
    private AuditLogger logger;

    public AuthenticationService(CustomerRepository customerRepo, AuditLogger logger) {
        this.customerRepo = customerRepo;
        this.logger = logger;
    }

    /**
     * Realiza login verificando CPF e senha.
     * 
     * @param cpf      CPF do cliente
     * @param password senha
     * @return objeto Customer autenticado
     * @throws UnauthorizedException se credenciais inválidas
     */
    public Customer login(String cpf, String password) throws UnauthorizedException {
        // O repositório lança UnauthorizedException se falhar
        customerRepo.authenticate(cpf, password);
        Customer customer = customerRepo.findByCpf(cpf);
        logger.info("Login bem-sucedido para cliente " + cpf);
        return customer;
    }
}