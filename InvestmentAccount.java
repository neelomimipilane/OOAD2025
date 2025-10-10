public class InvestmentAccount extends Account implements InterestBearing {
    private final double interestRate = 0.05; // fixed 5%

    public InvestmentAccount(String accountNumber, double balance, String branch) {
        super(accountNumber, balance, branch);
        if (balance < 500) {
            throw new IllegalArgumentException("Investment Account requires a minimum initial balance of 500.");
        }
    }

    @Override
    public void calculateInterest() {
        double interest = getBalance() * interestRate;
        if (interest > 0) {
            deposit(interest);
            addTransaction(String.format("Investment interest credited: %.2f", interest));
            System.out.println("5% interest added to Investment Account " + getAccountNumber());
        }
    }

    public double getInterestRate() {
        return interestRate;
    }
}
