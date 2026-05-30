package app;

import domain.account.Account;
import domain.customer.Customer;
import exceptions.*;
import infrastructure.logging.AuditLogger;
import repository.CustomerRepository;
import service.*;
import util.CurrencyFormatter;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Aplicação bancária orientada a objetos (fase 2).
 * Utiliza os serviços e repositórios do domínio.
 * Trata entrada e saída monetária no padrão brasileiro (vírgula).
 */
public class BankApplicationOO {

    private final AuthenticationService authService;
    private final AccountService accountService;
    private final TransferService transferService;
    private final LedgerService ledgerService;
    private final InterestService interestService;
    private final AuditLogger logger;
    private final CustomerRepository customerRepo;
    private final Scanner scanner;

    private Customer currentCustomer;

    public BankApplicationOO(AuthenticationService authService,
            AccountService accountService,
            TransferService transferService,
            LedgerService ledgerService,
            InterestService interestService,
            AuditLogger logger,
            CustomerRepository customerRepo) {
        this.authService = authService;
        this.accountService = accountService;
        this.transferService = transferService;
        this.ledgerService = ledgerService;
        this.interestService = interestService;
        this.logger = logger;
        this.customerRepo = customerRepo;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("=== BankCore EDU - Sistema Orientado a Objetos ===");
        boolean exit = false;
        while (!exit) {
            try {
                if (currentCustomer == null) {
                    showMainMenu();
                    int option = readInt("Opção: ");
                    switch (option) {
                        case 1 -> login();
                        case 2 -> registerCustomer();
                        case 0 -> exit = true;
                        default -> System.out.println("Opção inválida.");
                    }
                } else {
                    showCustomerMenu();
                    int option = readInt("Opção: ");
                    switch (option) {
                        case 1 -> displayBalance();
                        case 2 -> deposit();
                        case 3 -> withdraw();
                        case 4 -> transfer();
                        case 5 -> showStatement();
                        case 6 -> projectInterest();
                        case 7 -> createAccount();
                        case 8 -> listMyAccounts();
                        case 9 -> logout();
                        case 0 -> exit = true;
                        default -> System.out.println("Opção inválida.");
                    }
                }
            } catch (AccountNotFoundException | InsufficientFundsException | UnauthorizedException
                    | TransferFailedException | CustomerNotFoundException e) {
                // Exceções de negócio esperadas – mensagem amigável
                System.out.println("Erro: " + e.getMessage());
                logger.error("Erro de negócio: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                // Erros de validação ou estado inválido – comunicar o problema
                System.out.println("Operação inválida: " + e.getMessage());
                logger.warning("Validação falhou: " + e.getMessage());
            } catch (RuntimeException e) {
                // Erro inesperado – registrar detalhes para depuração
                System.out.println("Erro inesperado. Por favor, tente novamente ou contate o suporte.");
                logger.error("Erro inesperado: " + e.getMessage());
                e.printStackTrace(); // pode ser redirecionado para log
            }
        }
        scanner.close();
    }

    // ---------- Menus ----------
    private void showMainMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Login");
        System.out.println("2. Cadastrar cliente");
        System.out.println("0. Sair");
    }

    private void showCustomerMenu() {
        System.out.println("\n--- MENU DO CLIENTE (" + currentCustomer.getName() + ") ---");
        System.out.println("1. Ver saldo");
        System.out.println("2. Depositar");
        System.out.println("3. Sacar");
        System.out.println("4. Transferir");
        System.out.println("5. Extrato");
        System.out.println("6. Projeção de juros (poupança)");
        System.out.println("7. Criar nova conta");
        System.out.println("8. Minhas contas");
        System.out.println("9. Logout");
        System.out.println("0. Sair do sistema");
    }

    // ---------- Operações ----------
    private void login() throws UnauthorizedException, CustomerNotFoundException {
        System.out.print("CPF (formato 000.000.000-00): ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        currentCustomer = authService.login(cpf, password);
        System.out.println("Login bem-sucedido! Bem-vindo, " + currentCustomer.getName());
    }

    private void registerCustomer() {
        System.out.println("\n--- CADASTRO DE CLIENTE ---");
        System.out.print("CPF (formato 000.000.000-00): ");
        String cpf = scanner.nextLine();
        System.out.print("Nome completo: ");
        String name = scanner.nextLine();
        System.out.print("Senha (mínimo 4 caracteres): ");
        String password = scanner.nextLine();

        Customer newCustomer = new Customer(cpf, name, password);
        customerRepo.add(newCustomer);
        System.out.println("Cliente cadastrado com sucesso!");
        logger.info("Novo cliente cadastrado: " + cpf);
    }

    private void displayBalance() throws AccountNotFoundException {
        String accNum = selectAccount("consultar saldo");
        Account acc = accountService.findAccount(accNum);
        System.out.printf("Saldo da conta %s: %s%n",
                accNum, CurrencyFormatter.format(acc.getBalance()));
    }

    private void deposit() throws AccountNotFoundException {
        String accNum = selectAccount("depósito");
        BigDecimal amount = readBigDecimal("Valor do depósito: R$ ");
        accountService.deposit(accNum, amount);
        System.out.println("Depósito realizado com sucesso.");
    }

    private void withdraw() throws AccountNotFoundException, InsufficientFundsException {
        String accNum = selectAccount("saque");
        BigDecimal amount = readBigDecimal("Valor do saque: R$ ");
        accountService.withdraw(accNum, amount);
        System.out.println("Saque realizado com sucesso.");
    }

    private void transfer() throws AccountNotFoundException, InsufficientFundsException, TransferFailedException {
        String fromAcc = selectAccount("transferência (origem)");
        System.out.print("Conta destino: ");
        String toAcc = scanner.nextLine();
        BigDecimal amount = readBigDecimal("Valor da transferência: R$ ");
        transferService.transfer(fromAcc, toAcc, amount);
        System.out.println("Transferência concluída.");
    }

    private void showStatement() throws AccountNotFoundException {
        String accNum = selectAccount("extrato");
        System.out.println(ledgerService.getStatement(accNum));
    }

    private void projectInterest() {
        List<Account> interestAccounts = currentCustomer.getAccounts().stream()
                .filter(interestService::isInterestBearing)
                .toList();
        if (interestAccounts.isEmpty()) {
            System.out.println("Você não possui contas poupança.");
            return;
        }
        int years = readInt("Número de anos para projeção: ");
        for (Account acc : interestAccounts) {
            try {
                BigDecimal projected = interestService.calculateProjectedValue(acc, years);
                System.out.printf("Conta %s: Saldo atual %s -> Projeção em %d ano(s): %s%n",
                        acc.getAccountNumber(),
                        CurrencyFormatter.format(acc.getBalance()),
                        years,
                        CurrencyFormatter.format(projected));
            } catch (Exception e) {
                System.out
                        .println("Erro ao projetar juros para conta " + acc.getAccountNumber() + ": " + e.getMessage());
            }
        }
    }

    private void createAccount() throws CustomerNotFoundException {
        System.out.print("Tipo de conta (poupanca/corrente): ");
        String type = scanner.nextLine();
        BigDecimal initialBalance = readBigDecimal("Saldo inicial: R$ ");
        BigDecimal extra = BigDecimal.ZERO;
        if (type.equalsIgnoreCase("poupanca")) {
            System.out.print("Taxa de juros anual (%): ");
            double rate = scanner.nextDouble();
            scanner.nextLine(); // consumir newline
            extra = BigDecimal.valueOf(rate);
        } else if (type.equalsIgnoreCase("corrente")) {
            extra = readBigDecimal("Tarifa mensal: R$ ");
        } else {
            System.out.println("Tipo inválido.");
            return;
        }
        Account newAcc = accountService.createAccount(
                currentCustomer.getCpf(), type, initialBalance, extra);
        System.out.println("Conta criada com sucesso! Número: " + newAcc.getAccountNumber());
    }

    private void listMyAccounts() {
        List<Account> accounts = currentCustomer.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("Nenhuma conta associada.");
        } else {
            System.out.println("--- Minhas Contas ---");
            for (Account acc : accounts) {
                String tipo = acc.getClass().getSimpleName().replace("Account", "");
                System.out.printf("%s %s | %s | Saldo: %s%n",
                        tipo,
                        acc.getAccountNumber(),
                        acc.getHolderName(),
                        CurrencyFormatter.format(acc.getBalance()));
            }
        }
    }

    private void logout() {
        currentCustomer = null;
        System.out.println("Logout realizado.");
    }

    // ---------- Utilitários ----------
    private String selectAccount(String operation) {
        List<Account> accounts = currentCustomer.getAccounts();
        if (accounts.isEmpty()) {
            throw new IllegalStateException("Você não possui contas.");
        }
        if (accounts.size() == 1) {
            return accounts.get(0).getAccountNumber();
        }
        System.out.println("Escolha a conta para " + operation + ":");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("%d. %s - %s%n",
                    i + 1,
                    accounts.get(i).getAccountNumber(),
                    accounts.get(i).getClass().getSimpleName());
        }
        int choice = readInt("Número: ");
        if (choice < 1 || choice > accounts.size()) {
            throw new IllegalArgumentException("Seleção inválida.");
        }
        return accounts.get(choice - 1).getAccountNumber();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine(); // consumir newline
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
                scanner.nextLine(); // descartar a entrada incorreta
            }
        }
    }

    /**
     * Lê um valor monetário do teclado, aceitando vírgula ou ponto.
     */
    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return CurrencyFormatter.parse(input);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Use o formato 1.500,00 ou 1500.50");
            }
        }
    }
}