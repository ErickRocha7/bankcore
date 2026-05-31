package repository;

import collections.BinarySearchTree;
import domain.account.Account;
import domain.customer.Customer;
import exceptions.CustomerNotFoundException;
import exceptions.UnauthorizedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerRepository {

    private final BinarySearchTree<Customer> customerTree;

    public CustomerRepository() {
        customerTree = new BinarySearchTree<>();
    }

    public void add(Customer customer) {
        if (customer == null)
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        if (customerTree.contains(customer))
            throw new IllegalArgumentException("Cliente com CPF " + customer.getCpf() + " já cadastrado.");
        customerTree.insert(customer);
    }

    public Customer findByCpf(String cpf) throws CustomerNotFoundException {
        Customer template = createDummyCustomer(cpf);
        Customer found = customerTree.search(template);
        if (found == null)
            throw new CustomerNotFoundException("Cliente com CPF " + cpf + " não encontrado.");
        return found;
    }

    public void authenticate(String cpf, String password) throws UnauthorizedException {
        try {
            Customer c = findByCpf(cpf);
            if (!c.authenticate(password))
                throw new UnauthorizedException("CPF ou senha inválidos.");
        } catch (CustomerNotFoundException e) {
            throw new UnauthorizedException("CPF ou senha inválidos.");
        }
    }

    public void update(Customer customer) throws CustomerNotFoundException {
        if (customer == null)
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        if (!customerTree.contains(customer))
            throw new CustomerNotFoundException(
                    "Cliente com CPF " + customer.getCpf() + " não encontrado para atualização.");
        // A chave (CPF) não muda, então apenas mantemos o mesmo nó.
    }

    public void delete(String cpf) throws CustomerNotFoundException {
        Customer template = createDummyCustomer(cpf);
        if (!customerTree.remove(template))
            throw new CustomerNotFoundException("Cliente com CPF " + cpf + " não encontrado.");
    }

    public List<Customer> findAll() {
        return customerTree.getAll();
    }

    public boolean exists(String cpf) {
        if (cpf == null || cpf.isBlank())
            return false;
        Customer template = createDummyCustomer(cpf);
        return customerTree.contains(template);
    }

    public int count() {
        return customerTree.size();
    }

    public void clear() {
        customerTree.clear();
    }

    public void replaceAll(Map<String, Customer> customersMap) {
        customerTree.clear();
        if (customersMap != null) {
            for (Customer c : customersMap.values()) {
                customerTree.insert(c);
            }
        }
    }

    public Map<String, Customer> getMap() {
        Map<String, Customer> map = new HashMap<>();
        for (Customer c : customerTree.getAll()) {
            map.put(c.getCpf(), c);
        }
        return map;
    }

    public List<Account> getCustomerAccounts(String cpf) throws CustomerNotFoundException {
        Customer customer = findByCpf(cpf);
        return new ArrayList<>(customer.getAccounts());
    }

    private Customer createDummyCustomer(String cpf) {
        // Dummy apenas para busca baseada no CPF
        return new Customer(cpf, "dummy", "dummyPass");
    }
}