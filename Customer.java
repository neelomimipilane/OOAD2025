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

    // Standard Getters and Setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    // Account management
    public void openAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    // Abstract method to be implemented by subclasses
    public abstract void displayCustomerInfo();
}
