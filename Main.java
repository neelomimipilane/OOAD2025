import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Main {
    private static Map<String, Customer> customerDatabase = new HashMap<>();
    private static Map<String, String> passwordDatabase = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("=== BANKING SYSTEM STARTING ===");

        // Initialize file system
        FileManager.initializeFiles();

        // Load all data from files
        loadAllData();

        // Initialize sample data only if no customers exist
        if (customerDatabase.isEmpty()) {
            System.out.println("No customers found, creating sample data...");
            initializeSampleData();
        } else {
            System.out.println("Found " + customerDatabase.size() + " existing customers.");
        }

        System.out.println("=== BANKING SYSTEM READY ===");
    }

    // ---------------- DATA LOADING ----------------

    private static void loadAllData() {
        System.out.println("Loading data from files...");

        // Load customers first
        customerDatabase = FileManager.loadAllCustomers();

        // Load passwords
        passwordDatabase = FileManager.loadAllPasswords();

        // Load accounts and assign to customers
        List<FileManager.AccountData> accountDataList = FileManager.loadAllAccounts(customerDatabase);
        for (FileManager.AccountData data : accountDataList) {
            Customer customer = customerDatabase.get(data.customerID);
            if (customer != null) {
                Account account = createAccountFromData(data, customer);
                if (account != null) {
                    // Load transactions for this account
                    List<Transaction> transactions = FileManager.loadTransactionsForAccount(data.accountNumber);
                    for (Transaction transaction : transactions) {
                        account.getTransactions().add(transaction);
                    }

                    customer.openAccount(account);
                    System.out.println("Loaded account: " + data.accountNumber + " for customer: " + data.customerID);
                }
            }
        }
    }

    private static Account createAccountFromData(FileManager.AccountData data, Customer customer) {
        try {
            switch (data.accountType) {
                case "SAVINGS":
                    double savingsRate = Double.parseDouble(data.extraData);
                    SavingsAccount savings = new SavingsAccount(data.accountNumber, data.branch, customer, savingsRate);
                    // Set balance directly for savings
                    savings.deposit(data.balance, "Account Loaded");
                    return savings;

                case "CHEQUE":
                    String[] empData = data.extraData.split(";");
                    String empName = empData.length > 0 ? empData[0] : "";
                    String empAddress = empData.length > 1 ? empData[1] : "";
                    ChequeAccount cheque = new ChequeAccount(data.accountNumber, data.branch, customer, empName, empAddress);
                    // Set balance directly for cheque
                    cheque.deposit(data.balance, "Account Loaded");
                    return cheque;

                case "INVESTMENT":
                    double investmentRate = Double.parseDouble(data.extraData);
                    // For investment, we need to recreate with the existing balance
                    Investment investment = new Investment(data.accountNumber, data.branch, customer, data.balance, investmentRate);
                    return investment;

                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error creating account from data: " + e.getMessage());
            return null;
        }
    }

    // ---------------- SAMPLE DATA INITIALIZATION ----------------

    private static void initializeSampleData() {
        System.out.println("Creating sample data...");

        // Sample Individual Customer
        boolean johnRegistered = registerCustomer("John", "Doe", "123 Main St", "IND12345",
                "password123", "I", "1990-01-01", "ID123456", "", "", "", "john.doe@gmail.com");

        // Sample Business Customer
        boolean sarahRegistered = registerCustomer("Sarah", "Smith", "456 Business Ave", "BUS12345",
                "password123", "B", "1985-05-15", "ID789012",
                "Tech Solutions", "REG123", "456 Business Ave", "sarah@techsolutions.co.bw");

        System.out.println("John registered: " + johnRegistered);
        System.out.println("Sarah registered: " + sarahRegistered);

        // Add sample accounts
        Customer john = customerDatabase.get("IND12345");
        if (john != null) {
            createAccount(john, "Savings", "SAV001", "", "", 0);
            createAccount(john, "Investment", "INV001", "", "", 1000);
            // Add initial deposits
            depositToAccount(john, "SAV001", 1500.00);
            depositToAccount(john, "INV001", 50.00);
        }

        Customer sarah = customerDatabase.get("BUS12345");
        if (sarah != null) {
            createAccount(sarah, "Cheque", "CHQ001", "Tech Corp", "123 Office Park", 0);
            depositToAccount(sarah, "CHQ001", 2500.00);
        }

        System.out.println("Sample data creation completed.");
    }

    // ---------------- LOGIN & REGISTRATION ----------------

    public static Customer login(String id, String password) {
        System.out.println("Login attempt - ID: " + id);

        if (customerDatabase.containsKey(id) && passwordDatabase.get(id).equals(password)) {
            System.out.println("Login successful for: " + id);
            return customerDatabase.get(id);
        } else {
            System.out.println("Login failed for: " + id);
            return null;
        }
    }

    public static Customer loginWithIdOrEmail(String loginInput, String password) {
        System.out.println("Login attempt - Input: " + loginInput);

        // Check if input is an email (contains @)
        if (loginInput.contains("@")) {
            // Search by email
            Customer customer = findCustomerByEmail(loginInput);
            if (customer != null) {
                // Found customer by email, now verify password using their customer ID
                String customerId = customer.getCustomerID();
                if (passwordDatabase.containsKey(customerId) &&
                        passwordDatabase.get(customerId).equals(password)) {
                    System.out.println("Email login successful for: " + customerId);
                    return customer;
                }
            }
        } else {
            // Search by Customer ID (original method)
            return login(loginInput, password);
        }
        System.out.println("Login failed for: " + loginInput);
        return null;
    }

    public static boolean registerCustomer(String firstname, String surname, String address, String customerID,
                                           String password, String type, String dob, String idNumber,
                                           String businessName, String regNumber, String businessAddress,
                                           String email) {

        System.out.println("Registration attempt - ID: " + customerID + ", Email: " + email);

        if (customerDatabase.containsKey(customerID)) {
            System.out.println("Registration failed - Customer ID already exists: " + customerID);
            return false;
        }

        // Check if email already exists
        if (isEmailAlreadyRegistered(email)) {
            System.out.println("Registration failed - Email already exists: " + email);
            return false;
        }

        Customer customer;
        if ("I".equalsIgnoreCase(type)) {
            customer = new CustomerIndividual(firstname, surname, address, customerID, dob, idNumber, email);
        } else {
            customer = new CustomerBusiness(firstname, surname, address, customerID, businessName, regNumber, businessAddress, email);
        }

        customerDatabase.put(customerID, customer);
        passwordDatabase.put(customerID, password);

        // Save to file
        FileManager.saveCustomer(customer);
        FileManager.savePassword(customerID, password);

        System.out.println("Registration successful for: " + customerID);
        return true;
    }

    // ---------------- EMAIL MANAGEMENT ----------------

    public static Customer findCustomerByEmail(String email) {
        for (Customer customer : customerDatabase.values()) {
            String customerEmail = getCustomerEmail(customer);
            if (customerEmail != null && customerEmail.equalsIgnoreCase(email)) {
                return customer;
            }
        }
        return null;
    }

    public static String getCustomerEmail(Customer customer) {
        if (customer instanceof CustomerIndividual) {
            return ((CustomerIndividual) customer).getEmail();
        } else if (customer instanceof CustomerBusiness) {
            return ((CustomerBusiness) customer).getEmail();
        }
        return null;
    }

    public static boolean isEmailAlreadyRegistered(String email) {
        return findCustomerByEmail(email) != null;
    }

    // ---------------- ACCOUNT MANAGEMENT ----------------

    public static boolean createAccount(Customer customer, String type, String accNum, String empName,
                                        String empAddress, double initialDeposit) {
        // Prevent duplicate account types
        if (hasAccountType(customer, type)) {
            return false;
        }

        // Prevent duplicate account numbers across all customers
        if (findAccountByNumber(accNum) != null) {
            return false;
        }

        Account account = null;

        // Determine interest rates based on customer type
        boolean isBusinessCustomer = customer instanceof CustomerBusiness;

        switch (type) {
            case "Savings":
                double savingsInterestRate = isBusinessCustomer ? 0.0005 : 0.00025;
                account = new SavingsAccount(accNum, "Main Branch", customer, savingsInterestRate);
                break;
            case "Cheque":
                account = new ChequeAccount(accNum, "Main Branch", customer, empName, empAddress);
                break;
            case "Investment":
                double investmentInterestRate = isBusinessCustomer ? 0.05 : 0.03;
                try {
                    account = new Investment(accNum, "Main Branch", customer, initialDeposit, investmentInterestRate);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                break;
        }

        if (account != null) {
            customer.openAccount(account);
            // Save account to file
            FileManager.saveAccount(account, customer.getCustomerID());
            return true;
        }
        return false;
    }

    private static boolean hasAccountType(Customer customer, String type) {
        for (Account acc : customer.getAccounts()) {
            switch (type) {
                case "Savings":
                    if (acc instanceof SavingsAccount) return true;
                    break;
                case "Cheque":
                    if (acc instanceof ChequeAccount) return true;
                    break;
                case "Investment":
                    if (acc instanceof Investment) return true;
                    break;
            }
        }
        return false;
    }

    public static boolean depositToAccount(Customer customer, String accNum, double amount) {
        if (amount <= 0) {
            return false; // Invalid amount
        }

        Account account = findAccount(customer, accNum);
        if (account != null) {
            account.deposit(amount);
            return true;
        }
        return false;
    }

    // UPDATED: Add Savings Account protection
    public static boolean withdrawFromAccount(Customer customer, String accNum, double amount) {
        if (amount <= 0) {
            return false; // Invalid amount
        }

        Account account = findAccount(customer, accNum);
        if (account != null) {
            // Check if it's a Savings Account - prevent withdrawals
            if (account instanceof SavingsAccount) {
                System.out.println("Withdrawal denied: Savings accounts do not allow withdrawals");
                return false;
            }

            // For other account types, check balance and withdraw
            if (account.getBalance() >= amount) {
                account.withdraw(amount);
                return true;
            }
        }
        return false;
    }

    // ---------------- NEW BUSINESS METHODS ----------------

    // UPDATED: Add Savings Account protection for transfers
    public static boolean transferFunds(Customer customer, String fromAccNum,
                                        String toAccNum, double amount) {
        Account fromAccount = findAccount(customer, fromAccNum);
        Account toAccount = findAccount(customer, toAccNum);

        if (fromAccount == null || toAccount == null) {
            return false;
        }

        // Check if fromAccount is Savings Account - prevent transfers
        if (fromAccount instanceof SavingsAccount) {
            System.out.println("Transfer denied: Transfers are not allowed from Savings accounts");
            return false;
        }

        // Check sufficient funds
        if (fromAccount.getBalance() >= amount) {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            return true;
        }
        return false;
    }

    public static void payAllInterest(Customer customer) {
        for (Account acc : customer.getAccounts()) {
            if (acc instanceof InterestBearing) {
                ((InterestBearing) acc).calculateInterest();
            }
        }
    }

    public static double getTotalBalance(Customer customer) {
        double total = 0;
        for (Account acc : customer.getAccounts()) {
            total += acc.getBalance();
        }
        return total;
    }

    public static String getAccountsInfo(Customer customer) {
        StringBuilder sb = new StringBuilder();
        for (Account acc : customer.getAccounts()) {
            String type;
            if (acc instanceof SavingsAccount) type = "Savings Account";
            else if (acc instanceof ChequeAccount) type = "Cheque Account";
            else if (acc instanceof Investment) type = "Investment Account";
            else type = "Unknown";

            sb.append(acc.getAccountNumber())
                    .append(" | Type: ").append(type)
                    .append(" | Balance: BWP ").append(String.format("%.2f", acc.getBalance()))
                    .append(" | Branch: ").append(acc.getBranch())
                    .append("\n");
        }
        return sb.toString();
    }

    private static Account findAccount(Customer customer, String accNum) {
        for (Account acc : customer.getAccounts()) {
            if (acc.getAccountNumber().equals(accNum)) return acc;
        }
        return null;
    }

    // ---------------- GETTERS FOR GUI ----------------

    public static Map<String, Customer> getCustomerDatabase() {
        return customerDatabase;
    }

    public static Map<String, String> getPasswordDatabase() {
        return passwordDatabase;
    }

    // ---------------- UTILITY METHODS ----------------

    public static Account findAccountByNumber(Customer customer, String accountNumber) {
        for (Account account : customer.getAccounts()) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public static int getAccountCount(Customer customer) {
        return customer.getAccounts().size();
    }

    // ---------------- ADD THIS METHOD FOR TRANSFER FUNCTIONALITY ----------------

    /**
     * Returns an ObservableList of all customers in the system
     * This is used by the TransferController to search for destination accounts
     */
    public static ObservableList<Customer> getCustomers() {
        return FXCollections.observableArrayList(customerDatabase.values());
    }

    /**
     * Alternative method that returns a regular List if you prefer
     */
    public static List<Customer> getCustomerList() {
        return new ArrayList<>(customerDatabase.values());
    }

    /**
     * Enhanced method to find any account by number across all customers
     * This is crucial for transfer functionality
     */
    public static Account findAccountByNumber(String accountNumber) {
        for (Customer customer : customerDatabase.values()) {
            Account account = findAccountByNumber(customer, accountNumber);
            if (account != null) {
                return account;
            }
        }
        return null;
    }

    /**
     * Enhanced transfer method that works across different customers
     * UPDATED: Add Savings Account protection
     */
    public static boolean transferFunds(Account fromAccount, String toAccountNumber, double amount, String description) {
        Account toAccount = findAccountByNumber(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            return false;
        }

        // Check if fromAccount is Savings Account - prevent transfers
        if (fromAccount instanceof SavingsAccount) {
            System.out.println("Transfer denied: Transfers are not allowed from Savings accounts");
            return false;
        }

        // Check if transferring to same account
        if (fromAccount.getAccountNumber().equals(toAccountNumber)) {
            return false;
        }

        // Check sufficient funds
        if (fromAccount.getBalance() >= amount && amount > 0) {
            // Withdraw from source account with description
            String withdrawDescription = description.isEmpty() ?
                    "Transfer to " + toAccountNumber : description;

            // Use the two-parameter withdraw method
            boolean withdrawSuccess = fromAccount.withdraw(amount, withdrawDescription);

            if (withdrawSuccess) {
                // Deposit to destination account with description
                String depositDescription = description.isEmpty() ?
                        "Transfer from " + fromAccount.getAccountNumber() : description;

                // Use the two-parameter deposit method
                toAccount.deposit(amount, depositDescription);
                return true;
            }
        }
        return false;
    }
}