import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String date;
    private String description;
    private double amount;
    private String type;
    private double balance;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Transaction(String date, String description, double amount, String type, double balance) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.balance = balance;
    }

    // Getters
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public double getBalance() { return balance; }

    // Setters
    public void setDate(String date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setBalance(double balance) { this.balance = balance; }

    // File-based save method
    public void saveTransaction(String accountNumber) {
        FileManager.saveTransaction(this, accountNumber);
        System.out.println("Transaction saved for account: " + accountNumber);
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s: $%.2f | Balance: $%.2f",
                date, description, type, amount, balance);
    }

    // Helper method to get formatted date
    public String getFormattedDate() {
        try {
            LocalDate transactionDate = LocalDate.parse(date, dateFormatter);
            return transactionDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            return date; // Return original date if parsing fails
        }
    }

    // Method to check if transaction is a deposit
    public boolean isDeposit() {
        return "DEPOSIT".equalsIgnoreCase(type);
    }

    // Method to check if transaction is a withdrawal
    public boolean isWithdrawal() {
        return "WITHDRAWAL".equalsIgnoreCase(type);
    }
}