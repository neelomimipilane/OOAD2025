import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Account {
    protected String accountNumber;
    protected String branch;
    protected Customer customer;
    protected double balance;
    protected List<Transaction> transactions = new ArrayList<>();
    protected static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Account(String accountNumber, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.customer = customer;
        this.balance = 0.0;
    }

    // Additional constructor for loading existing accounts
    public Account(String accountNumber, Customer customer, double balance) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = balance;
        this.branch = "Main Branch"; // default
    }

    public String getAccountNumber() { return accountNumber; }
    public String getBranch() { return branch; }
    public Customer getCustomer() { return customer; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    public void setBranch(String branch) { this.branch = branch; }
    public void setBalance(double balance) { this.balance = balance; }

    // ---------------- ACCOUNT OPERATIONS ----------------
    public boolean deposit(double amount, String description) {
        if (amount <= 0) return false;

        balance += amount;
        Transaction transaction = new Transaction(
                LocalDate.now().format(dateFormatter),
                description,
                amount,
                "DEPOSIT",
                balance
        );

        addTransaction(transaction);

        // Save transaction to file and update account balance
        transaction.saveTransaction(this.accountNumber);
        updateAccountBalance();

        System.out.println("Deposit successful: " + amount + " to account " + accountNumber);
        return true;
    }

    // **Added convenience deposit method**
    public boolean deposit(double amount) {
        return deposit(amount, "Deposit");
    }

    public boolean withdraw(double amount, String description) {
        if (!canWithdraw(amount)) return false;

        balance -= amount;
        Transaction transaction = new Transaction(
                LocalDate.now().format(dateFormatter),
                description,
                amount,
                "WITHDRAWAL",
                balance
        );

        addTransaction(transaction);

        // Save transaction to file and update account balance
        transaction.saveTransaction(this.accountNumber);
        updateAccountBalance();

        System.out.println("Withdrawal successful: " + amount + " from account " + accountNumber);
        return true;
    }

    // Subclass helper to add transaction
    protected void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // File-based save method
    public void saveAccount(String customerID) {
        FileManager.saveAccount(this, customerID);
        System.out.println("Account saved to file: " + this.accountNumber);
    }

    // Update account balance in file
    protected void updateAccountBalance() {
        FileManager.updateAccountBalance(this.accountNumber, this.balance);
    }

    // Load transactions from file
    public void loadTransactions() {
        this.transactions = FileManager.loadTransactionsForAccount(this.accountNumber);
        System.out.println("Loaded " + this.transactions.size() + " transactions for account: " + this.accountNumber);
    }

    // Abstract method for account info display
    public abstract void displayAccountInfo();

    // Methods to support ChequeAccount and Investment overrides
    protected boolean canWithdraw(double amount) {
        return amount > 0 && amount <= balance;
    }

    protected String getAccountType() {
        return "Generic";
    }

    protected double getInterestRate() {
        return 0;
    }

    // Optional: convenience method for withdraw without description
    public void withdraw(double amount) {
        withdraw(amount, "Withdrawal");
    }

    // Get account summary for display
    public String getAccountSummary() {
        return String.format("%s - %s: $%.2f", accountNumber, getAccountType(), balance);
    }

    // Get recent transactions
    public List<Transaction> getRecentTransactions(int count) {
        int startIndex = Math.max(0, transactions.size() - count);
        return transactions.subList(startIndex, transactions.size());
    }
}