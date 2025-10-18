public class Investment extends Account implements InterestBearing {

    private double interestRate = 0.05; // 5% monthly
    private static final double MIN_INITIAL_DEPOSIT = 500;

    public Investment(String accountNumber, String branch, Customer customer, double initialDeposit) {
        super(accountNumber, branch, customer);
        if (initialDeposit < MIN_INITIAL_DEPOSIT) {
            throw new IllegalArgumentException("Minimum initial deposit is BWP 500");
        }
        this.balance = initialDeposit;
        addTransaction(new Transaction(
                java.time.LocalDate.now().toString(),
                "Initial Deposit",
                initialDeposit,
                "Credit",
                balance
        ));
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            addTransaction(new Transaction(
                    java.time.LocalDate.now().toString(),
                    "Withdrawal",
                    amount,
                    "Debit",
                    balance
            ));
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    @Override
    public void calculateInterest() {
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
