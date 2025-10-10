public class ChequeAccount extends Account {
    private double overdraftLimit;

    public ChequeAccount(String accountNumber, double balance, String branch, double overdraftLimit) {
        super(accountNumber, balance, branch);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        if (amount <= (balance + overdraftLimit)) {
            balance -= amount;
            addTransaction(String.format("Withdraw (with overdraft allowed): %.2f", amount));
        } else {
            throw new IllegalStateException("Exceeded overdraft limit");
        }
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }
}
