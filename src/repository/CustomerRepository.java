package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.account.Account;
import domain.customer.Customer;
import exceptions.CustomerNotFoundException;
import exceptions.UnauthorizedException;

/**
 * Repositório responsável pelo gerenciamento dos clientes do banco.
 *
 * Funcionalidades:
 * - Cadastro de clientes
 * - Busca por CPF
 * - Autenticação
 * - Remoção
 * - Controle de existência
 * - Restauração de estado para persistência
 *
 * Capítulos abordados:
 * 6 - Encapsulamento
 * 7 - Collections Framework
 * 8 - Composição
 * 11 - Exceções customizadas
 * 16 - Generics e Map
 */
public class CustomerRepository {

    private final Map<String, Customer> customers;

    /**
     * Construtor padrão.
     */
    public CustomerRepository() {
        this.customers = new HashMap<>();
    }

    /**
     * Adiciona um novo cliente ao repositório.
     *
     * @param customer Cliente a ser adicionado
     * @throws IllegalArgumentException se cliente for nulo ou CPF inválido
     * @throws IllegalArgumentException se CPF já existir
     */
    public void add(Customer customer) {
        validateCustomer(customer);

        String cpf = customer.getCpf();

        if (customers.containsKey(cpf)) {
            throw new IllegalArgumentException(
                    "Cliente com CPF " + cpf + " já cadastrado.");
        }

        customers.put(cpf, customer);
    }

    /**
     * Busca cliente pelo CPF.
     *
     * @param cpf CPF do cliente
     * @return Cliente encontrado
     * @throws CustomerNotFoundException se o cliente não existir
     * @throws IllegalArgumentException  se o CPF for nulo ou vazio
     */
    public Customer findByCpf(String cpf) throws CustomerNotFoundException {
        validateCpf(cpf);

        Customer customer = customers.get(cpf);

        if (customer == null) {
            throw new CustomerNotFoundException(
                    "Cliente com CPF " + cpf + " não encontrado.");
        }

        return customer;
    }

    /**
     * Realiza autenticação do cliente.
     *
     * @param cpf      CPF informado
     * @param password Senha informada
     * @throws UnauthorizedException    se credenciais inválidas
     * @throws IllegalArgumentException se CPF for nulo/vazio
     */
    public void authenticate(String cpf, String password)
            throws UnauthorizedException {

        validateCpf(cpf);

        if (password == null || password.isBlank()) {
            throw new UnauthorizedException("Senha inválida.");
        }

        Customer customer = customers.get(cpf);

        if (customer == null || !customer.authenticate(password)) {
            throw new UnauthorizedException("CPF ou senha inválidos.");
        }
    }

    /**
     * Atualiza os dados de um cliente já existente.
     *
     * @param customer Cliente atualizado
     * @throws CustomerNotFoundException se o cliente não existir
     * @throws IllegalArgumentException  se o cliente for nulo ou CPF inválido
     */
    public void update(Customer customer) throws CustomerNotFoundException {
        validateCustomer(customer);

        String cpf = customer.getCpf();

        if (!customers.containsKey(cpf)) {
            throw new CustomerNotFoundException(
                    "Cliente com CPF " + cpf + " não encontrado para atualização.");
        }

        customers.put(cpf, customer);
    }

    /**
     * Remove cliente do repositório.
     *
     * @param cpf CPF do cliente
     * @throws CustomerNotFoundException se o cliente não existir
     * @throws IllegalArgumentException  se o CPF for nulo/vazio
     */
    public void delete(String cpf) throws CustomerNotFoundException {
        validateCpf(cpf);

        Customer removed = customers.remove(cpf);

        if (removed == null) {
            throw new CustomerNotFoundException(
                    "Cliente com CPF " + cpf + " não encontrado.");
        }
    }

    /**
     * Lista todos os clientes cadastrados.
     *
     * @return Lista defensiva contendo os clientes
     */
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Verifica se um cliente existe.
     *
     * @param cpf CPF do cliente
     * @return true se existir
     */
    public boolean exists(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }
        return customers.containsKey(cpf);
    }

    /**
     * Quantidade total de clientes.
     *
     * @return Total de clientes
     */
    public int count() {
        return customers.size();
    }

    /**
     * Remove todos os clientes.
     */
    public void clear() {
        customers.clear();
    }

    /**
     * Substitui completamente os dados internos.
     *
     * @param customers Novo mapa de clientes
     */
    public void replaceAll(Map<String, Customer> customers) {
        this.customers.clear();
        if (customers != null) {
            this.customers.putAll(customers);
        }
    }

    /**
     * Retorna cópia defensiva do mapa interno.
     *
     * @return Cópia do mapa de clientes
     */
    public Map<String, Customer> getMap() {
        return new HashMap<>(customers);
    }

    /**
     * Busca todas as contas pertencentes a um cliente.
     *
     * @param cpf CPF do cliente
     * @return Lista de contas
     * @throws CustomerNotFoundException se o cliente não existir
     */
    public List<Account> getCustomerAccounts(String cpf) throws CustomerNotFoundException {
        Customer customer = findByCpf(cpf);
        return new ArrayList<>(customer.getAccounts());
    }

    // --- métodos auxiliares de validação ---

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        validateCpf(customer.getCpf());
    }

    private void validateCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
        }
    }
}