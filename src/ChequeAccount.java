import java.time.LocalDate;

    public class ChequeAccount extends Account implements Withdrawal {
    private String employerName;
    private String employerAddress;
    private double overdraftLimit;


    public ChequeAccount(String accountNumber, String branch, Customer customer,
                         String employerName, String employerAddress) {
        super(accountNumber, branch, customer);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
        this.overdraftLimit = 500.0; // Default overdraft limit
    }

    // Constructor for loading from file
    public ChequeAccount(String accountNumber, Customer customer, double balance, double overdraftLimit) {
        super(accountNumber, customer, balance);
        this.overdraftLimit = overdraftLimit;
        this.employerName = "";
        this.employerAddress = "";
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
    public double getOverdraftLimit() { return overdraftLimit; }

    public void setEmployerName(String employerName) { this.employerName = employerName; }
    public void setEmployerAddress(String employerAddress) { this.employerAddress = employerAddress; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }

    @Override
    protected boolean canWithdraw(double amount) {
        return amount > 0 && amount <= (balance + overdraftLimit);
    }

    @Override
    public boolean withdraw(double amount, String description) {
        if (!canWithdraw(amount)) {
            System.out.println("Withdrawal failed: Insufficient funds including overdraft");
            return false;
        }

        balance -= amount;
        Transaction transaction = new Transaction(
                LocalDate.now().format(dateFormatter),
                description,
                amount,
                "WITHDRAWAL",
                balance
        );

        addTransaction(transaction);

        // Save transaction to file and update account balance
        transaction.saveTransaction(this.accountNumber);
        updateAccountBalance();

        System.out.println("Withdrawal successful: " + amount + " from cheque account " + accountNumber);
        if (balance < 0) {
            System.out.println("Overdraft used. Current balance: " + balance);
        }
        return true;
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Cheque Account: " + accountNumber);
        System.out.println("Balance: $" + balance);
        System.out.println("Overdraft Limit: $" + overdraftLimit);
        System.out.println("Available: $" + (balance + overdraftLimit));
        System.out.println("Employer: " + employerName);
        System.out.println("Branch: " + branch);
        System.out.println("Transactions: " + transactions.size());
    }

    @Override
    protected String getAccountType() {
        return "Cheque";
    }

    // Static method to create from FileManager data
    public static ChequeAccount createFromData(FileManager.AccountData data, Customer customer) {
        ChequeAccount account = new ChequeAccount(data.accountNumber, customer, data.balance, 500.0);
        account.setBranch(data.branch);

        // Parse employer info from extraData
        if (data.extraData != null && !data.extraData.isEmpty()) {
            String[] employerParts = data.extraData.split(";");
            if (employerParts.length >= 1) account.setEmployerName(employerParts[0]);
            if (employerParts.length >= 2) account.setEmployerAddress(employerParts[1]);
        }

        return account;
    }
}