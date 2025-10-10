public class SavingsAccount extends Account implements InterestBearing {
    private final double interestRate = 0.05; // fixed 5%

    public SavingsAccount(String accountNumber, double balance, String branch) {
        super(accountNumber, balance, branch);
    }

    @Override
    public void calculateInterest() {
        double interest = getBalance() * interestRate;
        if (interest > 0) {
            deposit(interest);
            addTransaction(String.format("Interest credited: %.2f", interest));
            System.out.println("5% interest added to Savings Account " + getAccountNumber());
        }
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Withdrawals are not allowed from Savings Accounts.");
    }

    public double getInterestRate() {
        return interestRate;
    }
}
