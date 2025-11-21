import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileManager {
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String PASSWORDS_FILE = "passwords.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ---------------- CUSTOMER FILE OPERATIONS ----------------

    public static void saveCustomer(Customer customer) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE, true))) {
            if (customer instanceof CustomerIndividual) {
                CustomerIndividual ind = (CustomerIndividual) customer;
                writer.println(String.join("|",
                        ind.getCustomerID(),
                        "INDIVIDUAL",
                        ind.getFirstname(),
                        ind.getSurname(),
                        ind.getAddress(),
                        ind.getDateOfBirth(),
                        ind.getIdNumber(),
                        ind.getEmail(),
                        LocalDateTime.now().format(formatter)
                ));
            } else if (customer instanceof CustomerBusiness) {
                CustomerBusiness bus = (CustomerBusiness) customer;
                writer.println(String.join("|",
                        bus.getCustomerID(),
                        "BUSINESS",
                        bus.getFirstname(),
                        bus.getSurname(),
                        bus.getAddress(),
                        bus.getBusinessName(),
                        bus.getRegistrationNumber(),
                        bus.getBusinessAddress(),
                        bus.getEmail(),
                        LocalDateTime.now().format(formatter)
                ));
            }
            System.out.println("Customer saved: " + customer.getCustomerID());
        } catch (IOException e) {
            System.err.println("Error saving customer: " + e.getMessage());
        }
    }

    /**
     * Update customer information in file
     */
    public static boolean updateCustomer(Customer customer) {
        try {
            File inputFile = new File(CUSTOMERS_FILE);
            File tempFile = new File("customers_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean customerFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(customer.getCustomerID())) {
                    // Update this line with new customer data
                    if (customer instanceof CustomerIndividual) {
                        CustomerIndividual ind = (CustomerIndividual) customer;
                        writer.println(String.join("|",
                                ind.getCustomerID(),
                                "INDIVIDUAL",
                                ind.getFirstname(),
                                ind.getSurname(),
                                ind.getAddress(),
                                ind.getDateOfBirth(),
                                ind.getIdNumber(),
                                ind.getEmail(),
                                LocalDateTime.now().format(formatter)
                        ));
                    } else if (customer instanceof CustomerBusiness) {
                        CustomerBusiness bus = (CustomerBusiness) customer;
                        writer.println(String.join("|",
                                bus.getCustomerID(),
                                "BUSINESS",
                                bus.getFirstname(),
                                bus.getSurname(),
                                bus.getAddress(),
                                bus.getBusinessName(),
                                bus.getRegistrationNumber(),
                                bus.getBusinessAddress(),
                                bus.getEmail(),
                                LocalDateTime.now().format(formatter)
                        ));
                    }
                    customerFound = true;
                } else {
                    writer.println(line);
                }
            }

            writer.close();
            reader.close();

            if (customerFound) {
                if (inputFile.delete()) {
                    boolean success = tempFile.renameTo(inputFile);
                    if (success) {
                        System.out.println("Customer updated successfully: " + customer.getCustomerID());
                        return true;
                    }
                }
                System.err.println("Error replacing customer file");
                return false;
            } else {
                tempFile.delete();
                System.err.println("Customer not found for update: " + customer.getCustomerID());
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete customer from file
     */
    public static boolean deleteCustomer(String customerID) {
        try {
            File inputFile = new File(CUSTOMERS_FILE);
            File tempFile = new File("customers_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean customerFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(customerID)) {
                    customerFound = true;
                    // Skip this line (delete it)
                    continue;
                }
                writer.println(line);
            }

            writer.close();
            reader.close();

            if (customerFound) {
                // Also delete customer's accounts and passwords
                deleteCustomerAccounts(customerID);
                deleteCustomerPassword(customerID);

                if (inputFile.delete()) {
                    boolean success = tempFile.renameTo(inputFile);
                    if (success) {
                        System.out.println("Customer deleted successfully: " + customerID);
                        return true;
                    }
                }
                System.err.println("Error replacing customer file");
                return false;
            } else {
                tempFile.delete();
                System.err.println("Customer not found for deletion: " + customerID);
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    public static Map<String, Customer> loadAllCustomers() {
        Map<String, Customer> customers = new HashMap<>();
        File file = new File(CUSTOMERS_FILE);

        if (!file.exists()) {
            System.out.println("Customers file not found, starting fresh.");
            return customers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    String customerID = parts[0];
                    String type = parts[1];
                    String firstname = parts[2];
                    String surname = parts[3];
                    String address = parts[4];

                    if ("INDIVIDUAL".equals(type) && parts.length >= 8) {
                        String dob = parts[5];
                        String idNumber = parts[6];
                        String email = parts[7];
                        CustomerIndividual customer = new CustomerIndividual(firstname, surname, address, customerID, dob, idNumber, email);
                        customers.put(customerID, customer);
                        count++;
                    } else if ("BUSINESS".equals(type) && parts.length >= 9) {
                        String businessName = parts[5];
                        String regNumber = parts[6];
                        String businessAddress = parts[7];
                        String email = parts[8];
                        CustomerBusiness customer = new CustomerBusiness(firstname, surname, address, customerID, businessName, regNumber, businessAddress, email);
                        customers.put(customerID, customer);
                        count++;
                    }
                }
            }
            System.out.println("Loaded " + count + " customers from file.");
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return customers;
    }

    // ---------------- PASSWORD FILE OPERATIONS ----------------

    public static void savePassword(String customerID, String password) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PASSWORDS_FILE, true))) {
            writer.println(customerID + "|" + password);
            System.out.println("Password saved for: " + customerID);
        } catch (IOException e) {
            System.err.println("Error saving password: " + e.getMessage());
        }
    }

    /**
     * Update customer password in file
     */
    public static boolean updatePassword(String customerID, String newPassword) {
        try {
            File inputFile = new File(PASSWORDS_FILE);
            if (!inputFile.exists()) {
                System.out.println("Password file not found.");
                return false;
            }

            File tempFile = new File("passwords_temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean passwordFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(customerID)) {
                    // Update password
                    writer.println(customerID + "|" + newPassword);
                    passwordFound = true;
                } else {
                    writer.println(line);
                }
            }

            writer.close();
            reader.close();

            if (passwordFound) {
                if (inputFile.delete()) {
                    boolean success = tempFile.renameTo(inputFile);
                    if (success) {
                        System.out.println("Password updated successfully for: " + customerID);
                        return true;
                    }
                }
                System.err.println("Error replacing password file");
                return false;
            } else {
                tempFile.delete();
                System.err.println("Password not found for update: " + customerID);
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete customer password
     */
    private static void deleteCustomerPassword(String customerID) {
        try {
            File inputFile = new File(PASSWORDS_FILE);
            if (!inputFile.exists()) return;

            File tempFile = new File("passwords_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean passwordFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(customerID)) {
                    passwordFound = true;
                    continue; // Skip this line
                }
                writer.println(line);
            }

            writer.close();
            reader.close();

            if (passwordFound) {
                if (inputFile.delete()) {
                    tempFile.renameTo(inputFile);
                }
            } else {
                tempFile.delete();
            }

        } catch (IOException e) {
            System.err.println("Error deleting password: " + e.getMessage());
        }
    }

    public static Map<String, String> loadAllPasswords() {
        Map<String, String> passwords = new HashMap<>();
        File file = new File(PASSWORDS_FILE);

        if (!file.exists()) {
            System.out.println("Passwords file not found, starting fresh.");
            return passwords;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    passwords.put(parts[0], parts[1]);
                    count++;
                }
            }
            System.out.println("Loaded " + count + " passwords from file.");
        } catch (IOException e) {
            System.err.println("Error loading passwords: " + e.getMessage());
        }
        return passwords;
    }

    // ---------------- ACCOUNT FILE OPERATIONS ----------------

    public static void saveAccount(Account account, String customerID) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            String accountType = getAccountType(account);
            String extraData = getAccountExtraData(account);

            writer.println(String.join("|",
                    account.getAccountNumber(),
                    customerID,
                    accountType,
                    String.valueOf(account.getBalance()),
                    account.getBranch(),
                    extraData,
                    LocalDateTime.now().format(formatter)
            ));
            System.out.println("Account saved: " + account.getAccountNumber() + " for customer: " + customerID);
        } catch (IOException e) {
            System.err.println("Error saving account: " + e.getMessage());
        }
    }

    /**
     * Update account information in file
     */
    public static boolean updateAccount(Account account) {
        try {
            File inputFile = new File(ACCOUNTS_FILE);
            File tempFile = new File("accounts_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean accountFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(account.getAccountNumber())) {
                    // Update this line with new account data
                    String accountType = getAccountType(account);
                    String extraData = getAccountExtraData(account);
                    String customerID = getCustomerIDForAccount(account);

                    writer.println(String.join("|",
                            account.getAccountNumber(),
                            customerID,
                            accountType,
                            String.valueOf(account.getBalance()),
                            account.getBranch(),
                            extraData,
                            LocalDateTime.now().format(formatter)
                    ));
                    accountFound = true;
                } else {
                    writer.println(line);
                }
            }

            writer.close();
            reader.close();

            if (accountFound) {
                if (inputFile.delete()) {
                    boolean success = tempFile.renameTo(inputFile);
                    if (success) {
                        System.out.println("Account updated successfully: " + account.getAccountNumber());
                        return true;
                    }
                }
                System.err.println("Error replacing account file");
                return false;
            } else {
                tempFile.delete();
                System.err.println("Account not found for update: " + account.getAccountNumber());
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error updating account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete account from file
     */
    public static boolean deleteAccount(String accountNumber) {
        try {
            File inputFile = new File(ACCOUNTS_FILE);
            File tempFile = new File("accounts_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean accountFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(accountNumber)) {
                    accountFound = true;
                    // Skip this line (delete it)
                    continue;
                }
                writer.println(line);
            }

            writer.close();
            reader.close();

            if (accountFound) {
                // Also delete account's transactions
                deleteAccountTransactions(accountNumber);

                if (inputFile.delete()) {
                    boolean success = tempFile.renameTo(inputFile);
                    if (success) {
                        System.out.println("Account deleted successfully: " + accountNumber);
                        return true;
                    }
                }
                System.err.println("Error replacing account file");
                return false;
            } else {
                tempFile.delete();
                System.err.println("Account not found for deletion: " + accountNumber);
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete all accounts for a customer
     */
    private static void deleteCustomerAccounts(String customerID) {
        try {
            File inputFile = new File(ACCOUNTS_FILE);
            if (!inputFile.exists()) return;

            File tempFile = new File("accounts_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            List<String> accountNumbers = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[1].equals(customerID)) {
                    accountNumbers.add(parts[0]); // Collect account numbers for transaction deletion
                    continue; // Skip this line
                }
                writer.println(line);
            }

            writer.close();
            reader.close();

            if (!accountNumbers.isEmpty()) {
                // Delete transactions for these accounts
                for (String accountNumber : accountNumbers) {
                    deleteAccountTransactions(accountNumber);
                }

                if (inputFile.delete()) {
                    tempFile.renameTo(inputFile);
                }
            } else {
                tempFile.delete();
            }

        } catch (IOException e) {
            System.err.println("Error deleting customer accounts: " + e.getMessage());
        }
    }

    // ---------------- UPDATE ACCOUNT BALANCE METHOD ----------------
    public static boolean updateAccountBalance(String accountNumber, double newBalance) {
        try {
            File inputFile = new File(ACCOUNTS_FILE);

            // If accounts file doesn't exist or is empty, we can't update
            if (!inputFile.exists() || inputFile.length() == 0) {
                System.out.println("Accounts file not found or empty. Cannot update balance for: " + accountNumber);
                return false;
            }

            File tempFile = new File("accounts_temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean accountFound = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4 && parts[0].equals(accountNumber)) {
                    // Update the balance in this line
                    parts[3] = String.valueOf(newBalance);
                    String updatedLine = String.join("|", parts);
                    writer.println(updatedLine);
                    accountFound = true;
                    System.out.println("Updated balance for account " + accountNumber + " to " + newBalance);
                } else {
                    writer.println(line);
                }
            }

            writer.close();
            reader.close();

            // Replace the original file with the updated one
            if (accountFound) {
                if (inputFile.delete()) {
                    boolean success = tempFile.renameTo(inputFile);
                    if (success) {
                        System.out.println("Account balance updated successfully: " + accountNumber);
                        return true;
                    }
                }
                // If we get here, something went wrong with file operations
                System.err.println("Error replacing account file");
                return false;
            } else {
                tempFile.delete(); // Clean up temp file
                System.err.println("Account not found for balance update: " + accountNumber);
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
            return false;
        }
    }

    private static String getAccountType(Account account) {
        if (account instanceof SavingsAccount) return "SAVINGS";
        if (account instanceof ChequeAccount) return "CHEQUE";
        if (account instanceof Investment) return "INVESTMENT";
        return "UNKNOWN";
    }

    private static String getAccountExtraData(Account account) {
        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            return String.valueOf(savings.getInterestRate());
        } else if (account instanceof ChequeAccount) {
            ChequeAccount cheque = (ChequeAccount) account;
            return (cheque.getEmployerName() != null ? cheque.getEmployerName() : "") + ";" +
                    (cheque.getEmployerAddress() != null ? cheque.getEmployerAddress() : "");
        } else if (account instanceof Investment) {
            Investment investment = (Investment) account;
            return String.valueOf(investment.getInterestRate());
        }
        return "";
    }

    private static String getCustomerIDForAccount(Account account) {
        // This method would need to find which customer owns this account
        // For simplicity, we'll search through all customers
        Map<String, Customer> customers = loadAllCustomers();
        for (Customer customer : customers.values()) {
            for (Account acc : customer.getAccounts()) {
                if (acc.getAccountNumber().equals(account.getAccountNumber())) {
                    return customer.getCustomerID();
                }
            }
        }
        return "UNKNOWN";
    }

    public static List<AccountData> loadAllAccounts(Map<String, Customer> customers) {
        List<AccountData> accountDataList = new ArrayList<>();
        File file = new File(ACCOUNTS_FILE);

        if (!file.exists()) {
            System.out.println("Accounts file not found, starting fresh.");
            return accountDataList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String accountNumber = parts[0];
                    String customerID = parts[1];
                    String accountType = parts[2];
                    double balance = Double.parseDouble(parts[3]);
                    String branch = parts[4];
                    String extraData = parts[5];

                    AccountData data = new AccountData(accountNumber, customerID, accountType, balance, branch, extraData);
                    accountDataList.add(data);
                    count++;
                }
            }
            System.out.println("Loaded " + count + " accounts from file.");
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
        return accountDataList;
    }

    // ---------------- TRANSACTION FILE OPERATIONS ----------------

    public static void saveTransaction(Transaction transaction, String accountNumber) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            writer.println(String.join("|",
                    accountNumber,
                    transaction.getDate(),
                    transaction.getDescription(),
                    String.valueOf(transaction.getAmount()),
                    transaction.getType(),
                    String.valueOf(transaction.getBalance()),
                    LocalDateTime.now().format(formatter)
            ));
            System.out.println("Transaction saved for account: " + accountNumber);
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    /**
     * Delete all transactions for an account
     */
    private static void deleteAccountTransactions(String accountNumber) {
        try {
            File inputFile = new File(TRANSACTIONS_FILE);
            if (!inputFile.exists()) return;

            File tempFile = new File("transactions_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(accountNumber)) {
                    continue; // Skip this line
                }
                writer.println(line);
            }

            writer.close();
            reader.close();

            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
            } else {
                tempFile.delete();
            }

        } catch (IOException e) {
            System.err.println("Error deleting account transactions: " + e.getMessage());
        }
    }

    public static List<Transaction> loadTransactionsForAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);

        if (!file.exists()) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(accountNumber)) {
                    String date = parts[1];
                    String description = parts[2];
                    double amount = Double.parseDouble(parts[3]);
                    String type = parts[4];
                    double balance = Double.parseDouble(parts[5]);

                    Transaction transaction = new Transaction(date, description, amount, type, balance);
                    transactions.add(transaction);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }

    // ---------------- DEBUG METHODS ----------------

    /**
     * Debug method to check file contents
     */
    public static void debugFileContents() {
        try {
            System.out.println("=== DEBUG FILE CONTENTS ===");

            // Check accounts file
            File accountsFile = new File(ACCOUNTS_FILE);
            if (accountsFile.exists()) {
                System.out.println("Accounts file exists. Size: " + accountsFile.length() + " bytes");
                System.out.println("Accounts file contents:");
                try (BufferedReader reader = new BufferedReader(new FileReader(accountsFile))) {
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("  Line " + (++lineCount) + ": " + line);
                    }
                    if (lineCount == 0) {
                        System.out.println("  (Empty file)");
                    }
                }
            } else {
                System.out.println("Accounts file does not exist!");
            }

            // Check transactions file
            File transactionsFile = new File(TRANSACTIONS_FILE);
            if (transactionsFile.exists()) {
                System.out.println("Transactions file exists. Size: " + transactionsFile.length() + " bytes");
                System.out.println("First few transactions:");
                try (BufferedReader reader = new BufferedReader(new FileReader(transactionsFile))) {
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null && lineCount < 5) {
                        System.out.println("  " + line);
                        lineCount++;
                    }
                }
            } else {
                System.out.println("Transactions file does not exist!");
            }

            System.out.println("=== END DEBUG ===");
        } catch (Exception e) {
            System.err.println("Debug error: " + e.getMessage());
        }
    }

    // ---------------- HELPER CLASS FOR ACCOUNT DATA ----------------

    public static class AccountData {
        public String accountNumber;
        public String customerID;
        public String accountType;
        public double balance;
        public String branch;
        public String extraData;

        public AccountData(String accountNumber, String customerID, String accountType,
                           double balance, String branch, String extraData) {
            this.accountNumber = accountNumber;
            this.customerID = customerID;
            this.accountType = accountType;
            this.balance = balance;
            this.branch = branch;
            this.extraData = extraData;
        }
    }

    // ---------------- INITIALIZATION ----------------

    public static void initializeFiles() {
        // Create empty files if they don't exist
        try {
            boolean customersCreated = new File(CUSTOMERS_FILE).createNewFile();
            boolean accountsCreated = new File(ACCOUNTS_FILE).createNewFile();
            boolean transactionsCreated = new File(TRANSACTIONS_FILE).createNewFile();
            boolean passwordsCreated = new File(PASSWORDS_FILE).createNewFile();

            System.out.println("File initialization:");
            System.out.println("  customers.txt: " + (customersCreated ? "created" : "exists"));
            System.out.println("  accounts.txt: " + (accountsCreated ? "created" : "exists"));
            System.out.println("  transactions.txt: " + (transactionsCreated ? "created" : "exists"));
            System.out.println("  passwords.txt: " + (passwordsCreated ? "created" : "exists"));
        } catch (IOException e) {
            System.err.println("Error creating data files: " + e.getMessage());
        }
    }
}