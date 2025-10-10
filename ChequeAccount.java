public class ChequeAccount extends Account implements Withdrawal {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, String branch, Customer customer,
                         String employerName, String employerAddress) {
        super(accountNumber, branch, customer);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println(amount + " withdrawn. New balance: " + balance);
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    @Override
    public void calculateInterest() {
        System.out.println("Cheque accounts do not earn interest.");
    }
}
