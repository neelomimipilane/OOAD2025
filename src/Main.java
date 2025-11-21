import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.function.Supplier;
import java.io.File;

public class Main {
    private static Map<String, Customer> customerDatabase = new HashMap<>();
    private static Map<String, String> passwordDatabase = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("=== BANKING SYSTEM STARTING ===");

        // Initialize file system
        FileManager.initializeFiles();

        // Load existing data from files
        loadAllDataFromFiles();

        // Status report
        System.out.println("System Status:");
        System.out.println("- Customers loaded: " + customerDatabase.size());
        System.out.println("- Database: " + (customerDatabase.isEmpty() ? "EMPTY" : "HAS DATA"));

        if (customerDatabase.isEmpty()) {
            System.out.println("Tip: Use the registration form to create your first customer.");
        }

        System.out.println("=== BANKING SYSTEM READY ===");
    }

    // ---------------- PASSWORD SECURITY ----------------
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // ---------------- FILE-BASED UTILITY METHODS ----------------
    public static boolean customerExists(String customerID) {
        return Customer.customerExists(customerID);
    }

    public static Customer refreshCustomer(String customerID) {
        Customer updatedCustomer = Customer.loadCustomer(customerID);
        if (updatedCustomer != null) {
            customerDatabase.put(customerID, updatedCustomer);
            return updatedCustomer;
        }
        return null;
    }

    // ---------------- FILE-BASED LOADING ----------------
    private static void loadAllDataFromFiles() {
        System.out.println("Loading data from files...");

        // Load customers from files
        loadCustomersFromFiles();

        // Load passwords from files
        loadPasswordsFromFiles();

        // Load accounts for each customer
        for (Customer customer : customerDatabase.values()) {
            loadCustomerAccountsFromFiles(customer);
        }

        System.out.println("Loaded " + customerDatabase.size() + " customers from files.");
    }

    private static void loadCustomersFromFiles() {
        Map<String, Customer> loadedCustomers = FileManager.loadAllCustomers();
        customerDatabase.putAll(loadedCustomers);
        System.out.println("Loaded " + loadedCustomers.size() + " customers from files.");
    }

    private static void loadPasswordsFromFiles() {
        passwordDatabase = FileManager.loadAllPasswords();
        System.out.println("Loaded " + passwordDatabase.size() + " passwords from files.");
    }

    /**
     * Load accounts for a customer from file data
     */
    private static void loadCustomerAccountsFromFiles(Customer customer) {
        try {
            // Clear existing accounts
            customer.getAccounts().clear();

            // Load all account data from files
            List<FileManager.AccountData> allAccountData = FileManager.loadAllAccounts(FileManager.loadAllCustomers());

            int accountCount = 0;
            for (FileManager.AccountData data : allAccountData) {
                if (data.customerID.equals(customer.getCustomerID())) {
                    Account account = createAccountFromFileData(data, customer);
                    if (account != null) {
                        customer.addAccount(account);
                        // Load transactions for this account
                        account.loadTransactions();
                        accountCount++;
                        System.out.println("Loaded account: " + data.accountNumber + " (" + data.accountType + ")");
                    }
                }
            }
            System.out.println("Loaded " + accountCount + " accounts for customer: " + customer.getCustomerID());

        } catch (Exception e) {
            System.err.println("Error loading accounts for customer " + customer.getCustomerID() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create Account object from file data
     */
    private static Account createAccountFromFileData(FileManager.AccountData data, Customer customer) {
        try {
            switch (data.accountType.toUpperCase()) {
                case "SAVINGS":
                    double savingsRate = 2.5; // default
                    try {
                        savingsRate = Double.parseDouble(data.extraData);
                    } catch (NumberFormatException e) {
                        System.out.println("Using default interest rate for savings account");
                    }
                    SavingsAccount savingsAccount = new SavingsAccount(data.accountNumber, customer, data.balance, savingsRate);
                    savingsAccount.setBranch(data.branch);
                    return savingsAccount;

                case "CHEQUE":
                    String[] employerParts = data.extraData.split(";");
                    String employerName = employerParts.length > 0 ? employerParts[0] : "";
                    String employerAddress = employerParts.length > 1 ? employerParts[1] : "";
                    ChequeAccount chequeAccount = new ChequeAccount(data.accountNumber, customer, data.balance, 500.0);
                    chequeAccount.setEmployerName(employerName);
                    chequeAccount.setEmployerAddress(employerAddress);
                    chequeAccount.setBranch(data.branch);
                    return chequeAccount;

                case "INVESTMENT":
                    double investmentRate = 5.0; // default
                    try {
                        investmentRate = Double.parseDouble(data.extraData);
                    } catch (NumberFormatException e) {
                        System.out.println("Using default interest rate for investment account");
                    }
                    // FIXED: Added branch parameter to Investment constructor
                    Investment investmentAccount = new Investment(data.accountNumber, data.branch, customer, data.balance, investmentRate);
                    return investmentAccount;

                default:
                    System.out.println("Unknown account type: " + data.accountType);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error creating account from data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ---------------- DATA RESET METHOD ----------------
    public static void resetData() {
        System.out.println("=== RESETTING ALL DATA ===");

        try {
            // Delete all data files
            String[] files = {
                    "customers.txt", "accounts.txt", "transactions.txt", "passwords.txt",
                    "accounts_temp.txt" // Also delete any temp files
            };

            for (String filename : files) {
                File file = new File(filename);
                if (file.exists() && file.delete()) {
                    System.out.println("Deleted: " + filename);
                }
            }

            // Clear in-memory data
            customerDatabase.clear();
            passwordDatabase.clear();

            // Reinitialize empty files
            FileManager.initializeFiles();

            System.out.println("Data reset successfully! All records deleted.");

        } catch (Exception e) {
            System.out.println("Error resetting data: " + e.getMessage());
        }
    }

    // ---------------- CLEANUP PARTIAL REGISTRATION ----------------
    public static void cleanupPartialRegistration(String customerId) {
        // Note: With file-based system, we can't easily remove a single customer
        // without rewriting the entire file. This would require a more complex implementation.
        System.out.println("Cleanup for file-based system would require file rewriting");

        // Remove from memory
        customerDatabase.remove(customerId);
    }

    // ---------------- RETRY METHODS FOR FILE OPERATIONS ----------------
    private static boolean customerExistsWithRetry(String customerID) {
        return executeWithRetry(() -> customerExists(customerID), 3);
    }

    private static boolean isEmailAlreadyRegisteredWithRetry(String email) {
        return executeWithRetry(() -> {
            Customer customer = findCustomerByEmail(email);
            return customer != null;
        }, 3);
    }

    private static boolean saveCustomerWithRetry(Customer customer, String password) {
        return executeWithRetry(() -> {
            customer.saveCustomer();

            // Save password directly using FileManager
            String hashedPassword = hashPassword(password);
            FileManager.savePassword(customer.getCustomerID(), hashedPassword);

            return true;
        }, 3);
    }

    private static Customer loadCustomerWithRetry(String customerId) {
        return executeWithRetryObject(() -> Customer.loadCustomer(customerId), 3);
    }

    private static boolean verifyPasswordWithRetry(String customerId, String password) {
        return executeWithRetry(() -> verifyPassword(customerId, password), 3);
    }

    // Generic retry method for boolean operations
    private static boolean executeWithRetry(Supplier<Boolean> operation, int maxRetries) {
        int attempts = 0;
        int retryDelay = 100;

        while (attempts < maxRetries) {
            try {
                boolean result = operation.get();
                return result;
            } catch (Exception e) {
                attempts++;
                System.out.println("File operation failed (attempt " + attempts + "): " + e.getMessage());

                if (attempts >= maxRetries) {
                    System.out.println("Operation failed after " + maxRetries + " attempts: " + e.getMessage());
                    return false;
                }

                // Wait before retrying (exponential backoff)
                try {
                    System.out.println("Retrying in " + retryDelay + "ms...");
                    Thread.sleep(retryDelay);
                    retryDelay *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }

    // Generic retry method for object operations
    private static <T> T executeWithRetryObject(Supplier<T> operation, int maxRetries) {
        int attempts = 0;
        int retryDelay = 100;

        while (attempts < maxRetries) {
            try {
                T result = operation.get();
                return result;
            } catch (Exception e) {
                attempts++;
                System.out.println("File operation failed (attempt " + attempts + "): " + e.getMessage());

                if (attempts >= maxRetries) {
                    System.out.println("Operation failed after " + maxRetries + " attempts: " + e.getMessage());
                    return null;
                }

                try {
                    System.out.println("Retrying in " + retryDelay + "ms...");
                    Thread.sleep(retryDelay);
                    retryDelay *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    // ---------------- LOGIN & REGISTRATION WITH RETRY LOGIC ----------------
    public static Customer login(String id, String password) {
        System.out.println("Login attempt - ID: " + id);

        Customer customer = loadCustomerWithRetry(id);

        if (customer != null) {
            if (verifyPasswordWithRetry(id, password)) {
                System.out.println("Login successful for: " + id);
                return customer;
            }
        }

        System.out.println("Login failed for: " + id);
        return null;
    }

    public static Customer loginWithIdOrEmail(String loginInput, String password) {
        System.out.println("Login attempt - Input: " + loginInput);

        try {
            // Reload data from files to ensure we have the latest
            loadAllDataFromFiles();

            Customer customer = null;
            String customerId = null;

            // Determine if input is Customer ID or Email
            if (loginInput.contains("@")) {
                // Input is email - find customer by email
                customer = findCustomerByEmail(loginInput);
                if (customer != null) {
                    customerId = customer.getCustomerID();
                    System.out.println("Found customer by email: " + customerId);
                }
            } else {
                // Input is Customer ID
                customer = customerDatabase.get(loginInput);
                customerId = loginInput;
                if (customer != null) {
                    System.out.println("Found customer by ID: " + customerId);
                }
            }

            if (customer == null) {
                System.out.println("Customer not found for: " + loginInput);
                return null;
            }

            // Verify password
            String storedPassword = passwordDatabase.get(customerId);
            if (storedPassword == null) {
                System.out.println("No password found for customer: " + customerId);
                return null;
            }

            String inputHash = hashPassword(password);
            if (!storedPassword.equals(inputHash)) {
                System.out.println("Password mismatch for customer: " + customerId);
                System.out.println("Stored: " + storedPassword + ", Input: " + inputHash);
                return null;
            }

            // Load accounts for this customer from files
            loadCustomerAccountsFromFiles(customer);

            System.out.println("Login successful for: " + customerId);
            System.out.println("Customer has " + customer.getAccounts().size() + " accounts");
            return customer;

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String customerId, String password) {
        // Check password from loaded passwords
        String storedPassword = passwordDatabase.get(customerId);
        if (storedPassword != null) {
            String inputHash = hashPassword(password);
            return storedPassword.equals(inputHash);
        }
        return false;
    }

    /**
     * Update customer password in the system
     */
    public static boolean updateCustomerPassword(String customerID, String newPassword) {
        if (!customerDatabase.containsKey(customerID)) {
            System.out.println("Customer not found: " + customerID);
            return false;
        }

        String hashedPassword = hashPassword(newPassword);
        boolean success = FileManager.updatePassword(customerID, hashedPassword);
        if (success) {
            passwordDatabase.put(customerID, hashedPassword);
            System.out.println("Password updated for customer: " + customerID);
        }
        return success;
    }

    public static boolean registerCustomer(String firstname, String surname, String address, String customerID,
                                           String password, String type, String dob, String idNumber,
                                           String businessName, String regNumber, String businessAddress,
                                           String email) {

        System.out.println("Registration attempt - ID: " + customerID + ", Email: " + email);

        // Check if customer already exists with retry
        if (customerExistsWithRetry(customerID)) {
            System.out.println("Registration failed - Customer ID already exists: " + customerID);
            return false;
        }

        if (isEmailAlreadyRegisteredWithRetry(email)) {
            System.out.println("Registration failed - Email already exists: " + email);
            return false;
        }

        Customer customer;
        if ("I".equalsIgnoreCase(type)) {
            customer = new CustomerIndividual(firstname, surname, address, customerID, dob, idNumber, email);
        } else {
            customer = new CustomerBusiness(firstname, surname, address, customerID, businessName, regNumber, businessAddress, email);
        }

        // Use retry logic for saving customer
        boolean saved = saveCustomerWithRetry(customer, password);
        if (saved) {
            customerDatabase.put(customerID, customer);
            passwordDatabase.put(customerID, hashPassword(password));
            System.out.println("Registration successful for: " + customerID);
            return true;
        } else {
            System.out.println("Registration failed - Could not save to files after retries");
            return false;
        }
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

    // ---------------- CUSTOMER SELF-SERVICE OPERATIONS ----------------

    /**
     * Customer self-service: Update own information
     */
    public static boolean customerUpdateInfo(String customerID, String currentPassword,
                                             String newFirstname, String newSurname,
                                             String newAddress, String newEmail) {
        Customer customer = customerDatabase.get(customerID);
        if (customer == null) {
            System.out.println("Customer not found.");
            return false;
        }

        return customer.updateMyInfo(currentPassword, newFirstname, newSurname, newAddress, newEmail);
    }

    /**
     * Customer self-service: Change own password
     */
    public static boolean customerChangePassword(String customerID, String currentPassword, String newPassword) {
        Customer customer = customerDatabase.get(customerID);
        if (customer == null) {
            System.out.println("Customer not found.");
            return false;
        }

        return customer.changeMyPassword(currentPassword, newPassword);
    }

    /**
     * Customer self-service: Delete own account
     */
    public static boolean customerDeleteAccount(String customerID, String confirmationPassword) {
        Customer customer = customerDatabase.get(customerID);
        if (customer == null) {
            System.out.println("Customer not found.");
            return false;
        }

        boolean success = customer.deleteMyAccount(confirmationPassword);
        if (success) {
            // Remove from memory
            customerDatabase.remove(customerID);
            passwordDatabase.remove(customerID);
        }
        return success;
    }

    /**
     * Customer self-service: Close specific account
     */
    public static boolean customerCloseAccount(String customerID, String accountNumber, String confirmationPassword) {
        Customer customer = customerDatabase.get(customerID);
        if (customer == null) {
            System.out.println("Customer not found.");
            return false;
        }

        return customer.closeMyAccount(accountNumber, confirmationPassword);
    }

    /**
     * Customer self-service: Update email
     */
    public static boolean customerUpdateEmail(String customerID, String currentPassword, String newEmail) {
        Customer customer = customerDatabase.get(customerID);
        if (customer == null) {
            System.out.println("Customer not found.");
            return false;
        }

        return customer.updateMyEmail(currentPassword, newEmail);
    }

    /**
     * Customer self-service: Update address
     */
    public static boolean customerUpdateAddress(String customerID, String currentPassword, String newAddress) {
        Customer customer = customerDatabase.get(customerID);
        if (customer == null) {
            System.out.println("Customer not found.");
            return false;
        }

        return customer.updateMyAddress(currentPassword, newAddress);
    }

    // ---------------- ACCOUNT MANAGEMENT ----------------
    public static boolean createAccount(Customer customer, String type, String accNum, String empName,
                                        String empAddress, double initialDeposit) {
        if (hasAccountType(customer, type)) {
            System.out.println("Account creation failed: Customer already has a " + type + " account");
            return false;
        }

        if (findAccountByNumber(accNum) != null) {
            System.out.println("Account creation failed: Account number already exists: " + accNum);
            return false;
        }

        // Create account based on type
        Account account;
        String branch = "Main Branch";

        try {
            switch (type) {
                case "Savings":
                    account = new SavingsAccount(accNum, branch, customer, 2.5); // 2.5% interest
                    break;
                case "Cheque":
                    account = new ChequeAccount(accNum, branch, customer, empName, empAddress);
                    break;
                case "Investment":
                    // FIXED: Added branch parameter to Investment constructor
                    account = new Investment(accNum, branch, customer, initialDeposit, 0.05); // 5% interest
                    break;
                default:
                    System.out.println("Unknown account type: " + type);
                    return false;
            }

            // Make initial deposit
            if (initialDeposit > 0) {
                account.deposit(initialDeposit, "Initial deposit");
            }

            // Add account to customer
            customer.openAccount(account);

            // ✅ CRITICAL FIX: Save account to file
            FileManager.saveAccount(account, customer.getCustomerID());

            System.out.println(type + " account created successfully for customer: " + customer.getCustomerID());
            return true;

        } catch (Exception e) {
            System.out.println("Account creation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- DEPOSIT AND WITHDRAWAL ----------------
    public static boolean depositToAccount(Customer customer, String accountNumber, double amount) {
        if (customer == null || accountNumber == null || amount <= 0) {
            System.out.println("Deposit failed: Invalid parameters");
            return false;
        }

        Account account = findAccountByNumber(customer, accountNumber);
        if (account == null) {
            System.out.println("Deposit failed: Account not found - " + accountNumber);
            return false;
        }

        boolean success = account.deposit(amount, "Deposit");
        if (success) {
            System.out.println("Deposit successful: BWP " + amount + " to account " + accountNumber);
            return true;
        } else {
            System.out.println("Deposit failed for account: " + accountNumber);
            return false;
        }
    }

    public static boolean withdrawFromAccount(Customer customer, String accountNumber, double amount) {
        if (customer == null || accountNumber == null || amount <= 0) {
            System.out.println("Withdrawal failed: Invalid parameters");
            return false;
        }

        Account account = findAccountByNumber(customer, accountNumber);
        if (account == null) {
            System.out.println("Withdrawal failed: Account not found - " + accountNumber);
            return false;
        }

        boolean success = account.withdraw(amount, "Withdrawal");
        if (success) {
            System.out.println("Withdrawal successful: BWP " + amount + " from account " + accountNumber);
            return true;
        } else {
            System.out.println("Withdrawal failed for account: " + accountNumber);
            return false;
        }
    }

    private static boolean hasAccountType(Customer customer, String type) {
        for (Account acc : customer.getAccounts()) {
            switch (type) {
                case "Savings": if (acc instanceof SavingsAccount) return true; break;
                case "Cheque": if (acc instanceof ChequeAccount) return true; break;
                case "Investment": if (acc instanceof Investment) return true; break;
            }
        }
        return false;
    }

    public static boolean transferFunds(Account fromAccount, String toAccountNumber, double amount, String description) {
        Account toAccount = findAccountByNumber(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            System.out.println("Transfer failed: One or both accounts not found");
            return false;
        }

        if (fromAccount instanceof SavingsAccount) {
            System.out.println("Transfer denied: Transfers are not allowed from Savings accounts");
            return false;
        }

        if (fromAccount.getAccountNumber().equals(toAccountNumber)) {
            System.out.println("Transfer failed: Cannot transfer to the same account");
            return false;
        }

        if (fromAccount.getBalance() >= amount && amount > 0) {
            String withdrawDescription = description.isEmpty() ? "Transfer to " + toAccountNumber : description;
            boolean withdrawSuccess = fromAccount.withdraw(amount, withdrawDescription);

            if (withdrawSuccess) {
                String depositDescription = description.isEmpty() ? "Transfer from " + fromAccount.getAccountNumber() : description;
                boolean depositSuccess = toAccount.deposit(amount, depositDescription);

                if (depositSuccess) {
                    System.out.println("Transfer successful: BWP " + amount + " from " + fromAccount.getAccountNumber() + " to " + toAccountNumber);
                    return true;
                } else {
                    fromAccount.deposit(amount, "Transfer rollback");
                    System.out.println("Transfer failed: Deposit to target account failed");
                }
            }
        } else {
            System.out.println("Transfer failed: Insufficient funds or invalid amount");
        }
        return false;
    }

    private static Account findAccount(Customer customer, String accNum) {
        for (Account acc : customer.getAccounts()) {
            if (acc.getAccountNumber().equals(accNum)) return acc;
        }
        return null;
    }

    public static Account findAccountByNumber(Customer customer, String accountNumber) {
        for (Account account : customer.getAccounts()) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public static Account findAccountByNumber(String accountNumber) {
        for (Customer customer : customerDatabase.values()) {
            Account account = findAccountByNumber(customer, accountNumber);
            if (account != null) return account;
        }
        return null;
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
            String type = acc instanceof SavingsAccount ? "Savings Account" :
                    acc instanceof ChequeAccount ? "Cheque Account" :
                            acc instanceof Investment ? "Investment Account" : "Unknown";

            sb.append(acc.getAccountNumber())
                    .append(" | Type: ").append(type)
                    .append(" | Balance: BWP ").append(String.format("%.2f", acc.getBalance()))
                    .append(" | Branch: ").append(acc.getBranch())
                    .append("\n");
        }
        return sb.toString();
    }

    public static int getAccountCount(Customer customer) {
        return customer.getAccounts().size();
    }

    // ---------------- DEBUG METHODS ----------------
    public static void debugLogin(String loginInput, String password) {
        try {
            System.out.println("=== DEBUG LOGIN ===");
            System.out.println("Input: " + loginInput);
            System.out.println("Password length: " + password.length());

            // Check customer database
            System.out.println("Customers in memory: " + customerDatabase.keySet());

            // Check password database
            System.out.println("Passwords in memory: " + passwordDatabase.keySet());

            // Check if customer exists
            Customer customer = null;
            if (loginInput.contains("@")) {
                customer = findCustomerByEmail(loginInput);
                System.out.println("Found by email: " + (customer != null ? customer.getCustomerID() : "null"));
            } else {
                customer = customerDatabase.get(loginInput);
                System.out.println("Found by ID: " + (customer != null ? "yes" : "no"));
            }

            if (customer != null) {
                String customerId = customer.getCustomerID();
                String storedPassword = passwordDatabase.get(customerId);
                String inputHash = hashPassword(password);

                System.out.println("Customer ID: " + customerId);
                System.out.println("Stored password hash: " + storedPassword);
                System.out.println("Input password hash: " + inputHash);
                System.out.println("Password match: " + (storedPassword != null && storedPassword.equals(inputHash)));

                // Check accounts
                System.out.println("Accounts for customer: " + customer.getAccounts().size());
                for (Account acc : customer.getAccounts()) {
                    System.out.println("  - " + acc.getAccountNumber() + " (" + acc.getClass().getSimpleName() + ")");
                }
            }

            System.out.println("=== END DEBUG ===");
        } catch (Exception e) {
            System.err.println("Debug error: " + e.getMessage());
        }
    }

    // ---------------- GETTERS ----------------
    public static Map<String, Customer> getCustomerDatabase() {
        return customerDatabase;
    }

    public static Map<String, String> getPasswordDatabase() {
        return passwordDatabase;
    }

    public static ObservableList<Customer> getCustomers() {
        return FXCollections.observableArrayList(customerDatabase.values());
    }

    public static List<Customer> getCustomerList() {
        return new ArrayList<>(customerDatabase.values());
    }
}