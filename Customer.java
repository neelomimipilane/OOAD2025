import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    protected String firstname;
    protected String surname;
    protected String address;
    protected String customerID;
    protected List<Account> accounts;

    public Customer(String firstname, String surname, String address, String customerID) {
        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.customerID = customerID;
        this.accounts = new ArrayList<>();
    }

    public void openAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public abstract void displayCustomerInfo();
}
