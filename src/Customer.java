import java.util.*;

public abstract class Customer {
    protected String customerID;
    protected String firstname;
    protected String surname;
    protected String address;
    protected List<Account> accounts = new ArrayList<>();

    public Customer(String firstname, String surname, String address, String customerID) {
        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.customerID = customerID;
    }

    // Getters
    public String getCustomerID() { return customerID; }
    public String getFirstname() { return firstname; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public List<Account> getAccounts() { return accounts; }

    // Setters
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setAddress(String address) { this.address = address; }

    // Account management
    public void openAccount(Account account) {
        if (account != null && !hasAccount(account.getAccountNumber())) {
            accounts.add(account);
            System.out.println("Account opened: " + account.getAccountNumber());
        }
    }

    /**
     * Add an account to the customer (used when loading from files)
     */
    public void addAccount(Account account) {
        if (account != null && !hasAccount(account.getAccountNumber())) {
            accounts.add(account);
        }
    }

    public boolean hasAccount(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return true;
            }
        }
        return false;
    }

    public Account getAccount(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return acc;
            }
        }
        return null;
    }

    public void closeAccount(String accountNumber) {
        accounts.removeIf(acc -> acc.getAccountNumber().equals(accountNumber));
    }

    // Abstract methods
    public abstract String getEmail();
    public abstract void displayCustomerInfo();

    // File operations
    public void saveCustomer() {
        FileManager.saveCustomer(this);
    }

    public static Customer loadCustomer(String customerID) {
        Map<String, Customer> customers = FileManager.loadAllCustomers();
        return customers.get(customerID);
    }

    public static boolean customerExists(String customerID) {
        Map<String, Customer> customers = FileManager.loadAllCustomers();
        return customers.containsKey(customerID);
    }

    // Utility methods
    public String getFullName() {
        return firstname + " " + surname;
    }

    public int getAccountCount() {
        return accounts.size();
    }

    public double getTotalBalance() {
        double total = 0;
        for (Account acc : accounts) {
            total += acc.getBalance();
        }
        return total;
    }

    // ---------------- CUSTOMER SELF-SERVICE CRUD OPERATIONS ----------------

    /**
     * Update customer's own information (self-service)
     */
    public boolean updateMyInfo(String currentPassword, String newFirstname, String newSurname,
                                String newAddress, String newEmail) {
        // Verify current password first
        if (!Main.verifyPassword(this.customerID, currentPassword)) {
            System.out.println("Password verification failed. Cannot update information.");
            return false;
        }

        // Update information
        this.firstname = newFirstname;
        this.surname = newSurname;
        this.address = newAddress;

        // Update email based on customer type
        if (this instanceof CustomerIndividual) {
            ((CustomerIndividual) this).setEmail(newEmail);
        } else if (this instanceof CustomerBusiness) {
            ((CustomerBusiness) this).setEmail(newEmail);
        }

        // Save changes to file
        boolean success = FileManager.updateCustomer(this);
        if (success) {
            System.out.println("Your information has been updated successfully.");
        }
        return success;
    }

    /**
     * Change customer's own password (self-service)
     */
    public boolean changeMyPassword(String currentPassword, String newPassword) {
        // Verify current password first
        if (!Main.verifyPassword(this.customerID, currentPassword)) {
            System.out.println("Current password is incorrect. Cannot change password.");
            return false;
        }

        // Change password
        boolean success = Main.updateCustomerPassword(this.customerID, newPassword);
        if (success) {
            System.out.println("Your password has been changed successfully.");
        }
        return success;
    }

    /**
     * Delete customer's own account (self-service with confirmation)
     */
    public boolean deleteMyAccount(String confirmationPassword) {
        // Verify password for security
        if (!Main.verifyPassword(this.customerID, confirmationPassword)) {
            System.out.println("Password verification failed. Cannot delete account.");
            return false;
        }

        System.out.println("WARNING: This action will permanently delete your account and all data.");
        System.out.println("All accounts, transactions, and personal information will be lost.");

        // Delete from file system
        boolean success = FileManager.deleteCustomer(this.customerID);
        if (success) {
            System.out.println("Your account has been deleted successfully. Thank you for banking with us.");
        }
        return success;
    }

    /**
     * Close customer's own specific account (self-service)
     */
    public boolean closeMyAccount(String accountNumber, String confirmationPassword) {
        // Verify password for security
        if (!Main.verifyPassword(this.customerID, confirmationPassword)) {
            System.out.println("Password verification failed. Cannot close account.");
            return false;
        }

        Account account = getAccount(accountNumber);
        if (account == null) {
            System.out.println("Account not found: " + accountNumber);
            return false;
        }

        // Check if this is the last account
        if (accounts.size() <= 1) {
            System.out.println("Cannot close your last account. Please use account deletion instead.");
            return false;
        }

        // Check account balance
        if (account.getBalance() > 0) {
            System.out.println("Account has remaining balance. Please withdraw funds before closing.");
            return false;
        }

        // Remove from customer's account list
        accounts.remove(account);

        // Delete from file system
        boolean success = FileManager.deleteAccount(accountNumber);
        if (success) {
            System.out.println("Account " + accountNumber + " has been closed successfully.");
        }
        return success;
    }

    /**
     * Update customer's email address (self-service)
     */
    public boolean updateMyEmail(String currentPassword, String newEmail) {
        if (!Main.verifyPassword(this.customerID, currentPassword)) {
            System.out.println("Password verification failed. Cannot update email.");
            return false;
        }

        // Update email
        if (this instanceof CustomerIndividual) {
            ((CustomerIndividual) this).setEmail(newEmail);
        } else if (this instanceof CustomerBusiness) {
            ((CustomerBusiness) this).setEmail(newEmail);
        }

        // Save changes
        boolean success = FileManager.updateCustomer(this);
        if (success) {
            System.out.println("Your email has been updated to: " + newEmail);
        }
        return success;
    }

    /**
     * Update customer's address (self-service)
     */
    public boolean updateMyAddress(String currentPassword, String newAddress) {
        if (!Main.verifyPassword(this.customerID, currentPassword)) {
            System.out.println("Password verification failed. Cannot update address.");
            return false;
        }

        this.address = newAddress;
        boolean success = FileManager.updateCustomer(this);
        if (success) {
            System.out.println("Your address has been updated successfully.");
        }
        return success;
    }

    /**
     * Get customer details for display
     */
    public String getMyDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Customer ID: ").append(customerID).append("\n");
        details.append("Name: ").append(firstname).append(" ").append(surname).append("\n");
        details.append("Address: ").append(address).append("\n");
        details.append("Email: ").append(getEmail()).append("\n");
        details.append("Number of Accounts: ").append(accounts.size()).append("\n");
        details.append("Total Balance: BWP ").append(String.format("%.2f", getTotalBalance())).append("\n");

        if (this instanceof CustomerIndividual) {
            CustomerIndividual ind = (CustomerIndividual) this;
            details.append("Customer Type: Individual\n");
            details.append("Date of Birth: ").append(ind.getDateOfBirth()).append("\n");
            details.append("ID Number: ").append(ind.getIdNumber()).append("\n");
        } else if (this instanceof CustomerBusiness) {
            CustomerBusiness bus = (CustomerBusiness) this;
            details.append("Customer Type: Business\n");
            details.append("Business Name: ").append(bus.getBusinessName()).append("\n");
            details.append("Registration: ").append(bus.getRegistrationNumber()).append("\n");
            details.append("Business Address: ").append(bus.getBusinessAddress()).append("\n");
        }

        return details.toString();
    }

    /**
     * Get all accounts information for display
     */
    public String getMyAccountsInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Your Accounts:\n\n");

        for (Account account : accounts) {
            String type = account instanceof SavingsAccount ? "Savings" :
                    account instanceof ChequeAccount ? "Cheque" :
                            account instanceof Investment ? "Investment" : "Unknown";

            info.append("Account: ").append(account.getAccountNumber()).append("\n");
            info.append("Type: ").append(type).append("\n");
            info.append("Balance: BWP ").append(String.format("%.2f", account.getBalance())).append("\n");
            info.append("Branch: ").append(account.getBranch()).append("\n");

            if (account instanceof SavingsAccount) {
                info.append("Interest Rate: ").append(((SavingsAccount) account).getInterestRate()).append("%\n");
            } else if (account instanceof Investment) {
                info.append("Interest Rate: ").append(((Investment) account).getInterestRate()).append("%\n");
            } else if (account instanceof ChequeAccount) {
                info.append("Overdraft Limit: BWP ").append(((ChequeAccount) account).getOverdraftLimit()).append("\n");
            }

            info.append("Transactions: ").append(account.getTransactions().size()).append("\n");
            info.append("--------------------\n");
        }

        return info.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerID.equals(customer.customerID);
    }

    @Override
    public int hashCode() {
        return customerID.hashCode();
    }
}