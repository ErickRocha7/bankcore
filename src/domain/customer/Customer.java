package domain.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import domain.account.Account;

/**
 * Representa um cliente do banco.
 * Cada cliente pode ter múltiplas contas vinculadas.
 * 
 * Capítulos abordados:
 * 3, 6, 8 - Classes, encapsulamento, construtores, métodos acessores, static,
 * final
 * 7, 16 - Uso de coleção ArrayList (List<Account>)
 * 14 - Validação de CPF com expressão regular (String.matches)
 * 15 - Serialização (implements Serializable)
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String cpf; // CPF validado, imutável após criação
    private String name;
    private String password; // senha simples (para demonstração de autenticação)
    private List<Account> accounts; // composição: cliente possui contas

    // Construtor
    public Customer(String cpf, String name, String password) {
        if (cpf == null || !isValidCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido. Formato esperado: 000.000.000-00");
        }
        if (name == null || !name.matches("[A-Za-zÀ-Úà-ú ]+")) {
            throw new IllegalArgumentException("Nome deve conter apenas letras e espaços.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 4 caracteres.");
        }
        this.cpf = cpf;
        this.name = name;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    // Valida CPF com regex (simplificação; não valida dígitos verificadores)
    // Capítulo 14: expressões regulares
    public static boolean isValidCPF(String cpf) {
        return cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    // Adiciona uma conta ao cliente (composição)
    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Conta não pode ser nula.");
        }
        accounts.add(account);
    }

    // Remove conta pelo número (busca linear – adequado para listas pequenas)
    public boolean removeAccount(String accountNumber) {
        return accounts.removeIf(acc -> acc.getAccountNumber().equals(accountNumber));
    }

    // Autenticação simples (sem hash, para fins pedagógicos)
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    // Getters
    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    } // retorna cópia defensiva

    // Representação textual
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Cliente: %s | CPF: %s\n", name, cpf));
        if (accounts.isEmpty()) {
            sb.append("  Nenhuma conta vinculada.");
        } else {
            sb.append("  Contas:\n");
            for (Account acc : accounts) {
                sb.append("    ").append(acc).append("\n");
            }
        }
        return sb.toString();
    }
}