package repository;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import domain.customer.Customer;
import domain.account.Account;
import exceptions.UnauthorizedException;
import exceptions.AccountNotFoundException;

/**
 * Repositório de clientes do banco.
 * Gerencia clientes indexados pelo CPF.
 * 
 * Capítulos abordados:
 * 7, 16 – Coleções genéricas (HashMap<String, Customer>)
 * 11 – Exceções personalizadas (UnauthorizedException)
 * 6, 8 – Encapsulamento
 */
public class CustomerRepository {
    private Map<String, Customer> customers;

    public CustomerRepository() {
        customers = new HashMap<>();
    }

    /**
     * Adiciona um novo cliente.
     * 
     * @param customer cliente a adicionar
     * @throws IllegalArgumentException se CPF já estiver cadastrado
     */
    public void add(Customer customer) {
        if (customers.containsKey(customer.getCpf())) {
            throw new IllegalArgumentException("Cliente com CPF " + customer.getCpf() + " já cadastrado.");
        }
        customers.put(customer.getCpf(), customer);
    }

    /**
     * Busca cliente pelo CPF.
     * 
     * @param cpf CPF do cliente
     * @return cliente encontrado
     * @throws IllegalArgumentException se CPF não existir (poderia ser exceção
     *                                  customizada)
     */
    public Customer findByCpf(String cpf) {
        Customer customer = customers.get(cpf);
        if (customer == null) {
            throw new IllegalArgumentException("Cliente com CPF " + cpf + " não encontrado.");
        }
        return customer;
    }

    /**
     * Autentica um cliente por CPF e senha.
     * 
     * @param cpf      CPF do cliente
     * @param password senha informada
     * @return true se autenticado com sucesso
     * @throws UnauthorizedException se credenciais inválidas
     */
    public boolean authenticate(String cpf, String password) throws UnauthorizedException {
        Customer customer = customers.get(cpf);
        if (customer == null || !customer.authenticate(password)) {
            throw new UnauthorizedException("CPF ou senha inválidos.");
        }
        return true;
    }

    /**
     * Remove cliente pelo CPF.
     * 
     * @param cpf CPF do cliente
     * @throws IllegalArgumentException se não encontrado
     */
    public void delete(String cpf) {
        if (customers.remove(cpf) == null) {
            throw new IllegalArgumentException("Cliente com CPF " + cpf + " não encontrado.");
        }
    }

    /**
     * Lista todos os clientes.
     * 
     * @return lista de clientes
     */
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Verifica se cliente existe.
     * 
     * @param cpf CPF
     * @return true se existir
     */
    public boolean exists(String cpf) {
        return customers.containsKey(cpf);
    }

    /**
     * Retorna total de clientes.
     * 
     * @return número de clientes
     */
    public int count() {
        return customers.size();
    }

    /**
     * Substitui todo o mapa de clientes (para restauração de dados).
     * 
     * @param customers novo mapa
     */
    public void replaceAll(Map<String, Customer> customers) {
        this.customers.clear();
        this.customers.putAll(customers);
    }

    /**
     * Retorna cópia defensiva do mapa interno.
     * 
     * @return mapa de clientes
     */
    public Map<String, Customer> getMap() {
        return new HashMap<>(customers);
    }
}