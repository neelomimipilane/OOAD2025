import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private String email;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String id, String name, String address, String email) {
        super(id, name, address);
        this.email = email;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts); // return a copy for safety
    }

    public void viewAccounts() {
        System.out.println("Accounts for " + getName() + ":");
        if (accounts.isEmpty()) {
            System.out.println("  No accounts opened yet.");
        } else {
            for (Account a : accounts) {
                System.out.println("  - " + a);
            }
        }
    }

    // Find account by account number
    public Account findAccount(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return acc;
            }
        }
        return null; // not found
    }

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
