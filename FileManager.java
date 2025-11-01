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
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
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