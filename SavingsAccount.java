public class SavingsAccount extends Account implements Withdrawal, InterestBearing {
    private double interestRate;
    private static final double MIN_BALANCE = 100;

    public SavingsAccount(String accountNumber, String branch, Customer customer, double interestRate) {
        super(accountNumber, branch, customer);
        this.interestRate = interestRate;
    }

    @Override
    public void withdraw(double amount) {
        // Completely disable withdrawals
        System.out.println("Withdrawals are not allowed from Savings Account. Please use a different account type.");
    }

    @Override
    public boolean withdraw(double amount, String description) {
        // Completely disable withdrawals
        System.out.println("Withdrawals are not allowed from Savings Account. Please use a different account type.");
        return false;
    }

    @Override
    protected boolean canWithdraw(double amount) {
        // Always return false - no withdrawals allowed
        return false;
    }

    @Override
    public void calculateInterest() {
        double interest = balance * interestRate / 12; // Monthly interest
        if (interest > 0) {
            deposit(interest, "Monthly Interest");
            System.out.println("Monthly interest of P " + String.format("%.2f", interest) + " added to savings account.");
        }
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    public String getInterestRatePercentage() {
        return String.format("%.3f%%", interestRate * 100);
    }

    public double getMinBalance() {
        return MIN_BALANCE;
    }

    @Override
    public String toString() {
        return "Savings Account " + accountNumber +
                " (Balance: P " + String.format("%.2f", getBalance()) +
                ", Interest Rate: " + getInterestRatePercentage() + ")";
    }
}