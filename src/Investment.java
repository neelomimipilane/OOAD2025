public class Investment extends Account implements Withdrawal, InterestBearing {
    private double interestRate;
    private static final double MIN_BALANCE = 1000;

    public Investment(String accountNumber, String branch, Customer customer, double initialDeposit, double interestRate) {
        super(accountNumber, branch, customer);
        this.interestRate = interestRate;
        if (initialDeposit < MIN_BALANCE) {
            throw new IllegalArgumentException("Investment account requires minimum deposit of P" + MIN_BALANCE);
        }
        this.balance = initialDeposit;
        // Add initial deposit transaction
        addTransaction(new Transaction(
                java.time.LocalDate.now().toString(),
                "Initial Deposit",
                initialDeposit,
                "Deposit",
                balance
        ));
    }

    @Override
    public void withdraw(double amount) {
        withdraw(amount, "Withdrawal");
    }

    @Override
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
            System.out.println("Withdrawal denied. Investment account must maintain minimum balance of P" + MIN_BALANCE);
            return false;
        }
    }

    @Override
    public void displayAccountInfo() {

    }

    @Override
    protected boolean canWithdraw(double amount) {
        return amount > 0 && amount <= balance && (balance - amount) >= MIN_BALANCE;
    }

    @Override
    protected String getAccountType() {
        return "";
    }

    @Override
    public void calculateInterest() {
        double interest = balance * interestRate;
        if (interest > 0) {
            deposit(interest, "Monthly Interest");
            System.out.println("Added P " + String.format("%.2f", interest) + " interest to investment account " + accountNumber);
        }
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    public String getInterestRatePercentage() {
        return String.format("%.1f%%", interestRate * 100);
    }

    public double getMinBalance() {
        return MIN_BALANCE;
    }

    @Override
    public String toString() {
        return "Investment Account " + accountNumber +
                " (Balance: P " + String.format("%.2f", balance) +
                ", Interest Rate: " + getInterestRatePercentage() + ")";
    }
}