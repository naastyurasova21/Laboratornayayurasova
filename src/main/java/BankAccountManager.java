import java.util.*;
import java.text.SimpleDateFormat;
class BankAccount {
    private static int nextAccountNumber = 1001;

    private int accountNumber;
    private String ownerName;
    private double balance;
    private List<Transaction> transactions;

    public BankAccount(String ownerName, double initialDeposit) {
        this.accountNumber = nextAccountNumber++;
        this.ownerName = ownerName;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        addTransaction("Открытие счета", initialDeposit);
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Пополнение", amount);
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            addTransaction("Снятие", -amount);
            return true;
        }
        return false;
    }

    private void addTransaction(String type, double amount) {
        transactions.add(new Transaction(type, amount, balance));
    }

    public int getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    @Override
    public String toString() {
        return String.format("Счет: %d | Владелец: %s | Баланс: %.2f руб.",
                accountNumber, ownerName, balance);
    }
}

class Transaction {
    private Date date;
    private String type;
    private double amount;
    private double balanceAfter;
    private SimpleDateFormat sdf;

    public Transaction(String type, double amount, double balanceAfter) {
        this.date = new Date();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return String.format("%s | %s | Сумма: %.2f | Баланс: %.2f",
                sdf.format(date), type, amount, balanceAfter);
    }
}

public class BankAccountManager {
    private static List<BankAccount> accounts = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> openAccount();
                case 2 -> depositMoney();
                case 3 -> withdrawMoney();
                case 4 -> showBalance();
                case 5 -> showTransactions();
                case 6 -> searchTransactions();
                case 0 -> {
                    System.out.println("Выход из программы...");
                    return;
                }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nBank Manager");
        System.out.println("1. Открыть счет");
        System.out.println("2. Положить деньги");
        System.out.println("3. Снять деньги");
        System.out.println("4. Показать баланс");
        System.out.println("5. История транзакций");
        System.out.println("6. Поиск транзакций");
        System.out.println("0. Выход");
    }

    private static void openAccount() {
        System.out.println("\nОткрытие нового счета...");
        System.out.print("Имя владельца: ");
        String ownerName = scanner.nextLine();

        double initialDeposit = getDoubleInput("Начальный взнос: ");

        BankAccount account = new BankAccount(ownerName, initialDeposit);
        accounts.add(account);
        System.out.println("Счет открыт успешно");
        System.out.println(account);
    }

    private static void depositMoney() {
        BankAccount account = selectAccount();
        if (account == null) return;

        double amount = getDoubleInput("Сумма для пополнения: ");
        account.deposit(amount);
        System.out.println("Средства успешно зачислены");
        System.out.printf("Новый баланс: %.2f руб.\n", account.getBalance());
    }

    private static void withdrawMoney() {
        BankAccount account = selectAccount();
        if (account == null) return;

        double amount = getDoubleInput("Сумма для снятия: ");
        if (account.withdraw(amount)) {
            System.out.println("Средства успешно сняты");
            System.out.printf("Новый баланс: %.2f руб.\n", account.getBalance());
        } else {
            System.out.println("Ошибка: недостаточно средств или неверная сумма");
        }
    }

    private static void showBalance() {
        BankAccount account = selectAccount();
        if (account == null) return;

        System.out.println("\nИнформация о счете...");
        System.out.println(account);
    }

    private static void showTransactions() {
        BankAccount account = selectAccount();
        if (account == null) return;

        List<Transaction> transactions = account.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("История транзакций пуста");
            return;
        }

        System.out.println("\nИстория транзакций...");
        transactions.forEach(System.out::println);
    }

    private static void searchTransactions() {
        BankAccount account = selectAccount();
        if (account == null) return;

        System.out.println("\nПоиск транзакций...");
        System.out.println("1. По типу операции");
        System.out.println("2. По минимальной сумме");
        System.out.println("3. По максимальной сумме");

        int choice = getIntInput("Выберите тип поиска: ");
        List<Transaction> results = new ArrayList<>();
        List<Transaction> transactions = account.getTransactions();

        switch (choice) {
            case 1 -> {
                System.out.print("Введите тип операции (пополнение/снятие): ");
                String type = scanner.nextLine().toLowerCase();
                for (Transaction t : transactions) {
                    if (t.toString().toLowerCase().contains(type)) {
                        results.add(t);
                    }
                }
            }
            case 2 -> {
                double minAmount = getDoubleInput("Минимальная сумма: ");
                for (Transaction t : transactions) {
                    if (Math.abs(getTransactionAmount(t)) >= minAmount) {
                        results.add(t);
                    }
                }
            }
            case 3 -> {
                double maxAmount = getDoubleInput("Максимальная сумма: ");
                for (Transaction t : transactions) {
                    if (Math.abs(getTransactionAmount(t)) <= maxAmount) {
                        results.add(t);
                    }
                }
            }
            default -> {
                System.out.println("Неверный выбор");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("Транзакции не найдены");
        } else {
            System.out.println("\nРезультаты поиска:");
            results.forEach(System.out::println);
        }
    }

    private static BankAccount selectAccount() {
        if (accounts.isEmpty()) {
            System.out.println("Нет открытых счетов");
            return null;
        }

        System.out.println("\nДоступные счета:");
        accounts.forEach(System.out::println);

        int accountNumber = getIntInput("Введите номер счета: ");
        return accounts.stream()
                .filter(acc -> acc.getAccountNumber() == accountNumber)
                .findFirst()
                .orElse(null);
    }

    private static double getTransactionAmount(Transaction transaction) {
        String str = transaction.toString();
        try {
            String[] parts = str.split("Сумма: ");
            if (parts.length > 1) {
                String amountStr = parts[1].split(" ")[0];
                return Double.parseDouble(amountStr);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число");
            }
        }
    }
}