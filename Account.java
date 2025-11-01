import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer customer;
    protected List<Transaction> transactions;

    public Account(String accountNumber, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.customer = customer;
        this.balance = 0;
        this.transactions = new ArrayList<>();
    }

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public Customer getCustomer() { return customer; }
    public List<Transaction> getTransactions() { return transactions; }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        // Save transaction to file
        FileManager.saveTransaction(transaction, this.accountNumber);
    }

    // Deposit method with description
    public boolean deposit(double amount, String description) {
        if (amount > 0) {
            balance += amount;
            addTransaction(new Transaction(
                    java.time.LocalDate.now().toString(),
                    description,
                    amount,
                    "Deposit",
                    balance
            ));
            return true;
        } else {
            System.out.println("Deposit amount must be positive.");
            return false;
        }
    }

    // Overloaded deposit method without description
    public boolean deposit(double amount) {
        return deposit(amount, "Deposit");
    }

    // Withdraw method with description
    public boolean withdraw(double amount, String description) {
        if (canWithdraw(amount)) {
            balance -= amount;
            addTransaction(new Transaction(
                    java.time.LocalDate.now().toString(),
                    description,
                    -amount,
                    "Withdrawal",
                    balance
            ));
            return true;
        } else {
            System.out.println("Invalid withdrawal amount or insufficient funds.");
            return false;
        }
    }

    // Abstract method to check if withdrawal is allowed (let subclasses define rules)
    protected abstract boolean canWithdraw(double amount);

    // Single parameter withdraw for backward compatibility
    public void withdraw(double amount) {
        withdraw(amount, "Withdrawal");
    }

    @Override
    public String toString() {
        String type = "Account";
        if (this instanceof SavingsAccount) type = "Savings Account";
        else if (this instanceof ChequeAccount) type = "Cheque Account";
        else if (this instanceof Investment) type = "Investment Account";

        return type + " - " + accountNumber + " (P" + String.format("%.2f", balance) + ")";
    }
}