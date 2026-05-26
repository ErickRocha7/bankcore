package domain.account;

import java.io.Serializable;
import collections.GenericLinkedList;
import exceptions.InsufficientFundsException;

/**
 * Classe abstrata que representa uma conta bancária genérica.
 * Fornece a estrutura comum para contas correntes e poupanças.
 * 
 * Cobertura dos capítulos:
 * 3, 6, 8 - Classes, objetos, encapsulamento, construtores, static, final
 * 7 - Uso de GenericLinkedList como estrutura de dados (coleção)
 * 9 - Herança (será estendida por SavingsAccount e CheckingAccount)
 * 10 - Polimorfismo (método abstrato calculateInterest)
 * 11 - Tratamento de exceções (lança InsufficientFundsException)
 * 14 - Strings (formatação, concatenação)
 * 15 - Serialização (implementa Serializable)
 * 20, 21 - Genéricos (usa GenericLinkedList<T>)
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    // Contador estático para geração de número de conta sequencial (operador ++)
    private static int accountCounter = 10000;

    private final String accountNumber; // final para imutabilidade do número
    private String holderName;
    protected double balance; // protected para acesso nas subclasses
    protected GenericLinkedList<String> transactions; // histórico simples (evoluirá para Transaction)

    // Construtor
    public Account(String holderName, double initialBalance) {
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Nome do titular não pode ser vazio.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }
        this.holderName = holderName;
        this.balance = initialBalance;
        this.accountNumber = String.format("%05d", ++accountCounter); // incremento e formatação (cap 4, 14)
        this.transactions = new GenericLinkedList<>();
        // Registra a transação de abertura
        addTransaction("Abertura de conta", initialBalance);
    }

    // Métodos públicos (operações bancárias)

    /**
     * Deposita um valor na conta.
     * 
     * @param amount valor positivo
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser positivo.");
        }
        balance += amount; // operador de atribuição composta (cap 2)
        addTransaction("Depósito", amount);
    }

    /**
     * Saca um valor da conta, se houver saldo suficiente.
     * 
     * @param amount valor positivo
     * @throws InsufficientFundsException se saldo insuficiente
     */
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser positivo.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Saldo insuficiente. Saldo atual: R$ "
                    + String.format("%.2f", balance) + ", solicitado: R$ " + String.format("%.2f", amount));
        }
        balance -= amount;
        addTransaction("Saque", -amount);
    }

    /**
     * Método abstrato para cálculo de juros/rendimentos.
     * Cada tipo de conta implementa sua política.
     * 
     * @param years número de anos para projeção
     */
    public abstract void calculateInterest(int years);

    /**
     * Registra uma transação no histórico (método público para uso externo).
     * 
     * @param description descrição da transação
     * @param amount      valor (positivo para crédito, negativo para débito)
     */
    public void recordTransaction(String description, double amount) {
        addTransaction(description, amount);
    }

    // Métodos acessadores

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    /**
     * Retorna o extrato de transações como uma string formatada.
     * 
     * @return histórico de transações
     */
    public String getStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Extrato da conta %s - Titular: %s\n", accountNumber, holderName));
        sb.append("---------------------------------------------\n");
        for (String entry : transactions) {
            sb.append(entry).append("\n");
        }
        sb.append(String.format("Saldo atual: R$ %.2f\n", balance));
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Saldo: R$ %.2f", accountNumber, holderName, balance);
    }

    // Método auxiliar protegido para adicionar transação (composição)
    protected void addTransaction(String description, double amount) {
        // Formata a entrada com data/hora simplificada (para evoluir para Transaction)
        String entry = String.format("%s - %s: R$ %.2f",
                java.time.LocalDateTime.now().toString(), description, amount);
        transactions.add(entry);
    }
}