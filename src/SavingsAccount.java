public class SavingsAccount extends Account implements InterestBearing {

    public SavingsAccount(String accountNumber, String branch, Customer customer) {
        super(accountNumber, branch, customer);
    }

    @Override
    public void withdraw(double amount) {
        // Withdrawals are not allowed
        System.out.println("Withdrawals not allowed from Savings Account.");
    }

    @Override
    public void calculateInterest() {
        double interestRate = 0.0005; // 0.05% monthly
        double interest = balance * interestRate;
        balance += interest;
        addTransaction(new Transaction(
                java.time.LocalDate.now().toString(),
                "Monthly Interest",
                interest,
                "Credit",
                balance
        ));
    }
}
