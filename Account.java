import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    private String accountNumber;
    protected double balance;
    private String branch;
    private final List<String> transactionHistory = new ArrayList<>();

    public Account(String accountNumber, double balance, String branch) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        balance += amount;
        addTransaction(String.format("Deposit: %.2f", amount));
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        if (amount <= balance) {
            balance -= amount;
            addTransaction(String.format("Withdraw: %.2f", amount));
        } else {
            throw new IllegalStateException("Insufficient funds");
        }
    }

    protected void addTransaction(String description) {
        transactionHistory.add(LocalDateTime.now() + " - " + description);
    }

    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    @Override
    public String toString() {
        return String.format("%s{account=%s, balance=%.2f, branch=%s}",
                getClass().getSimpleName(), accountNumber, balance, branch);
    }
}
