public class SavingsAccount extends Account implements InterestBearing {
    private double interestRate;

    public SavingsAccount(String accountNumber, String branch, Customer customer, double interestRate) {
        super(accountNumber, branch, customer);
        this.interestRate = interestRate;
    }

    // Constructor for loading from file
    public SavingsAccount(String accountNumber, Customer customer, double balance, double interestRate) {
        super(accountNumber, customer, balance);
        this.interestRate = interestRate;
    }

    @Override
    public void calculateInterest() {
        double interest = balance * (interestRate / 100);
        deposit(interest, "Interest Payment");
        System.out.println("Interest calculated and applied: $" + interest);
    }

    // Remove the duplicate getInterestRate() method and use the one from Account class
    // The getInterestRate() method is already provided by the Account base class

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        calculateInterest(); // Use the interface method
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Savings Account: " + accountNumber);
        System.out.println("Balance: $" + balance);
        System.out.println("Interest Rate: " + interestRate + "%");
        System.out.println("Branch: " + branch);
        System.out.println("Transactions: " + transactions.size());
    }

    @Override
    protected String getAccountType() {
        return "Savings";
    }

    @Override
    public double getInterestRate() {  // Changed from protected to public
        return interestRate;
    }

    // Static method to create from FileManager data
    public static SavingsAccount createFromData(FileManager.AccountData data, Customer customer) {
        double interestRate = 2.5; // default
        try {
            interestRate = Double.parseDouble(data.extraData);
        } catch (NumberFormatException e) {
            System.out.println("Using default interest rate for savings account");
        }

        SavingsAccount account = new SavingsAccount(data.accountNumber, customer, data.balance, interestRate);
        account.setBranch(data.branch);
        return account;
    }
}