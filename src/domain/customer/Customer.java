package domain.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import domain.account.Account;
import infrastructure.security.PinHasher;

public class Customer implements Serializable, Comparable<Customer> {

    private static final long serialVersionUID = 1L;

    private final String cpf;
    private String name;
    private String passwordHash;
    private List<Account> accounts;

    public Customer(String cpf, String name, String password) {
        validateCpf(cpf);
        validateName(name);
        validatePassword(password);

        this.cpf = cpf.trim();
        this.name = name.trim();
        this.passwordHash = PinHasher.hash(password);
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Conta não pode ser nula.");
        }
        if (accounts.contains(account)) {
            throw new IllegalArgumentException("Conta já vinculada ao cliente.");
        }
        accounts.add(account);
    }

    public boolean removeAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank())
            return false;
        return accounts.removeIf(account -> account.getAccountNumber().equals(accountNumber));
    }

    public Account findAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank())
            return null;
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber))
                return account;
        }
        return null;
    }

    public boolean authenticate(String password) {
        if (password == null)
            return false;
        return PinHasher.verify(password, passwordHash);
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!authenticate(currentPassword)) {
            throw new IllegalArgumentException("Senha atual inválida.");
        }
        validatePassword(newPassword);
        this.passwordHash = PinHasher.hash(newPassword);
    }

    // ---------- Validação de CPF (mantida igual) ----------
    public static boolean isValidCPF(String cpf) {
        if (cpf == null)
            return false;
        if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"))
            return false;
        String digits = cpf.replaceAll("[^0-9]", "");
        if (digits.matches("(\\d)\\1{10}"))
            return false;
        int sum = 0, weight = 10;
        for (int i = 0; i < 9; i++) {
            sum += (digits.charAt(i) - '0') * weight--;
        }
        int firstDigit = (sum % 11 < 2) ? 0 : 11 - (sum % 11);
        if (firstDigit != (digits.charAt(9) - '0'))
            return false;
        sum = 0;
        weight = 11;
        for (int i = 0; i < 10; i++) {
            sum += (digits.charAt(i) - '0') * weight--;
        }
        int secondDigit = (sum % 11 < 2) ? 0 : 11 - (sum % 11);
        return secondDigit == (digits.charAt(10) - '0');
    }

    private void validateCpf(String cpf) {
        if (!isValidCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido. Use o formato 000.000.000-00.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (!name.matches("[A-Za-zÀ-ÿ ]+")) {
            throw new IllegalArgumentException("Nome deve conter apenas letras e espaços.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser vazia.");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("Senha deve possuir no mínimo 4 caracteres.");
        }
    }

    // ---------- Getters ----------
    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public void setName(String name) {
        validateName(name);
        this.name = name.trim();
    }

    public int getAccountCount() {
        return accounts.size();
    }

    public boolean hasAccounts() {
        return !accounts.isEmpty();
    }

    // ---------- Comparable ----------
    @Override
    public int compareTo(Customer other) {
        return this.cpf.compareTo(other.cpf);
    }

    // ---------- Object ----------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(name).append(" | CPF: ").append(cpf).append(System.lineSeparator());
        if (accounts.isEmpty()) {
            sb.append("  Nenhuma conta vinculada.");
        } else {
            sb.append("  Contas:").append(System.lineSeparator());
            for (Account account : accounts) {
                sb.append("    ").append(account).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Customer other))
            return false;
        return Objects.equals(cpf, other.cpf);
    }
}