package app;

import java.util.Scanner;

/**
 * Fase 1 - Aplicação bancária procedural.
 *
 * Cobertura dos capítulos:
 * 2 - Entrada/saída (Scanner, System.out) e operadores aritméticos/atribuição.
 * 3 - Introdução a classes, objetos, métodos e strings (métodos estáticos,
 * String.format).
 * 4 - Instruções de controle: parte 1 (while, switch, if, operadores ++ e --).
 * 5 - Instruções de controle: parte 2 (operadores lógicos &&, ||, ! em
 * validações).
 *
 * Evolução futura: substituir as variáveis estáticas por uma classe Account,
 * extrair lógica para classes de domínio e adicionar coleções/herança.
 */
public class BankApplication {

    // Variáveis globais para simular uma única conta (evoluirão para atributos de
    // objeto)
    private static String accountNumber = "00001";
    private static double balance = 0.0;
    private static int transactionCount = 0; // demonstra operador ++

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;

        System.out.println("=== BankCore EDU - Fase 1 ===");
        System.out.println("Conta simulada: " + accountNumber);

        // Loop principal - while (Cap 4)
        do {
            printMenu(); // chamada de método estático (Cap 3)
            System.out.print("Opção: ");
            option = scanner.nextInt(); // entrada de inteiro (Cap 2)

            // switch (Cap 4)
            switch (option) {
                case 1:
                    deposit(scanner); // delega para método (Cap 3)
                    break;
                case 2:
                    withdraw(scanner);
                    break;
                case 3:
                    displayBalance(); // método que usa String.format (Cap 3)
                    break;
                case 4:
                    displayTransactionCount(); // demonstra operador ++
                    break;
                case 0:
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (option != 0); // condição de saída (Cap 4/5)

        scanner.close();
    }

    /**
     * Exibe o menu formatado (Cap 2 - System.out.println, Cap 3 - strings)
     */
    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Depositar");
        System.out.println("2. Sacar");
        System.out.println("3. Ver saldo");
        System.out.println("4. Contador de transações");
        System.out.println("0. Sair");
    }

    /**
     * Realiza depósito com validações (Cap 2 - operadores, Cap 5 - operadores
     * lógicos)
     */
    private static void deposit(Scanner scanner) {
        System.out.print("Valor do depósito: R$ ");
        double amount = scanner.nextDouble(); // entrada de double (Cap 2)

        // Validação com operadores lógicos (Cap 5) - deve ser positivo
        if (amount > 0) {
            balance += amount; // operador de atribuição composta (Cap 2)
            transactionCount++; // operador de incremento (Cap 4)
            System.out.println("Depósito realizado.");
        } else {
            System.out.println("Valor inválido. Use um valor positivo.");
        }
    }

    /**
     * Realiza saque com verificação de saldo (Cap 4 - if/else, Cap 5 - &&)
     */
    private static void withdraw(Scanner scanner) {
        System.out.print("Valor do saque: R$ ");
        double amount = scanner.nextDouble();

        // Uso de operadores lógicos: valor positivo E saldo suficiente (Cap 5)
        if (amount > 0 && amount <= balance) {
            balance -= amount; // operador de atribuição composta
            transactionCount++;
            System.out.println("Saque realizado.");
        } else if (amount <= 0) {
            System.out.println("Valor inválido.");
        } else {
            System.out.println("Saldo insuficiente.");
        }
    }

    /**
     * Exibe saldo formatado (Cap 3 - String.format, concatenação de strings)
     */
    private static void displayBalance() {
        // String.format para saída profissional (Cap 3, Cap 14)
        String message = String.format("Conta %s - Saldo: R$ %.2f", accountNumber, balance);
        System.out.println(message);
    }

    /**
     * Demonstra o operador ++ e a exibição do contador (Cap 4)
     */
    private static void displayTransactionCount() {
        System.out.println("Total de transações realizadas: " + transactionCount);
    }
}