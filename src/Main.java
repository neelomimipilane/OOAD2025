import java.util.HashMap;
import java.util.Map;

public class Main {
    private static Map<String, Customer> customerDatabase = new HashMap<>();
    private static Map<String, String> passwordDatabase = new HashMap<>();

    public static void main(String[] args) {

    }

    // ---------------- LOGIN & REGISTRATION ----------------

    public static Customer login(String id, String password) {
        if (customerDatabase.containsKey(id) && passwordDatabase.get(id).equals(password)) {
            return customerDatabase.get(id);
        } else {
            return null;
        }
    }

    public static boolean registerCustomer(String firstname, String surname, String address, String customerID,
                                           String password, String type, String dob, String idNumber,
                                           String businessName, String regNumber, String businessAddress) {

        if (customerDatabase.containsKey(customerID)) {
            return false;
        }

        Customer customer;
        if ("I".equalsIgnoreCase(type)) {
            customer = new CustomerIndividual(firstname, surname, address, customerID, dob, idNumber);
        } else {
            customer = new CustomerBusiness(firstname, surname, address, customerID, businessName, regNumber, businessAddress);
        }

        customerDatabase.put(customerID, customer);
        passwordDatabase.put(customerID, password);
        return true;
    }

    // ---------------- ACCOUNT MANAGEMENT ----------------

    public static boolean createAccount(Customer customer, String type, String accNum, String empName,
                                        String empAddress, double initialDeposit) {
        // Prevent duplicate account types
        if (hasAccountType(customer, type)) {
            return false;
        }

        Account account = null;
        switch (type) {
            case "Savings":
                account = new SavingsAccount(accNum, "Main Branch", customer);
                break;
            case "Cheque":
                account = new ChequeAccount(accNum, "Main Branch", customer, empName, empAddress);
                break;
            case "Investment":
                try {
                    account = new Investment(accNum, "Main Branch", customer, initialDeposit);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                break;
        }

        if (account != null) {
            customer.openAccount(account);
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
        Account account = findAccount(customer, accNum);
        if (account != null) {
            account.deposit(amount);
            return true;
        }
        return false;
    }

    public static boolean withdrawFromAccount(Customer customer, String accNum, double amount) {
        Account account = findAccount(customer, accNum);
        if (account != null) {
            account.withdraw(amount);
            return true;
        }
        return false;
    }

    public static String getAccountsInfo(Customer customer) {
        StringBuilder sb = new StringBuilder();
        for (Account acc : customer.getAccounts()) {
            String type;
            if (acc instanceof SavingsAccount) type = "Savings Account";
            else if (acc instanceof ChequeAccount) type = "Cheque Account";
            else if (acc instanceof Investment) type = "Investment Account";
            else type = "Unknown";

            sb.append(acc.accountNumber)
                    .append(" | Type: ").append(type)
                    .append(" | Balance: BWP ").append(acc.balance)
                    .append(" | Branch: ").append(acc.branch)
                    .append("\n");
        }
        return sb.toString();
    }

    private static Account findAccount(Customer customer, String accNum) {
        for (Account acc : customer.getAccounts()) {
            if (acc.accountNumber.equals(accNum)) return acc;
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
}
