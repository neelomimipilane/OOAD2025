public class SavingsAccount extends Account implements InterestBearing {
    private double interestRate = 0.0005; // 0.05% monthly

    public SavingsAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("Withdrawals are not allowed from Savings Account.");
    }

    @Override
    public void calculateInterest() {
        double interest = balance * interestRate;
        balance += interest;
        System.out.println("Interest added: " + interest + ". New balance: " + balance);
    }
}
