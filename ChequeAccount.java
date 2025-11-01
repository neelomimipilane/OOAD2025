public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;
    private static final double OVERDRAFT_LIMIT = 1000;

    public ChequeAccount(String accountNumber, String branch, Customer customer,
                         String employerName, String employerAddress) {
        super(accountNumber, branch, customer);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public void withdraw(double amount) {
        withdraw(amount, "Withdrawal");
    }

    @Override
    public boolean withdraw(double amount, String description) {
        if (amount > 0 && (balance - amount) >= -OVERDRAFT_LIMIT) {
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
            System.out.println("Withdrawal denied. Overdraft limit exceeded.");
            return false;
        }
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return false;
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
    public double getOverdraftLimit() { return OVERDRAFT_LIMIT; }
}