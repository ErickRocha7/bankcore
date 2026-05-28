package service;

import domain.customer.Customer;
import exceptions.CustomerNotFoundException;
import exceptions.UnauthorizedException;
import infrastructure.logging.AuditLogger;
import repository.CustomerRepository;

/**
 * Serviço de autenticação de clientes.
 *
 * Capítulos:
 * 11 - Exceções (UnauthorizedException, CustomerNotFoundException)
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
     * @throws UnauthorizedException     se credenciais inválidas
     * @throws CustomerNotFoundException se o cliente não for encontrado (por
     *                                   segurança, após autenticação)
     */
    public Customer login(String cpf, String password)
            throws UnauthorizedException, CustomerNotFoundException {

        // Autentica (lança UnauthorizedException se CPF ou senha incorretos)
        customerRepo.authenticate(cpf, password);

        // Recupera o cliente – pode lançar CustomerNotFoundException (improvavelmente,
        // se o cadastro foi removido)
        Customer customer = customerRepo.findByCpf(cpf);

        logger.info("Login bem-sucedido para cliente " + cpf);
        return customer;
    }
}