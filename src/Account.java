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
    }

    // Deposit method
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction(new Transaction(
                    java.time.LocalDate.now().toString(),
                    "Deposit",
                    amount,
                    "Credit",
                    balance
            ));
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    // Abstract methods
    public abstract void withdraw(double amount);
}
