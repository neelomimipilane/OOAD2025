import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Customer> customers = new ArrayList<>();

        System.out.println("=== Welcome to the Banking System ===");

        // ======== CREATE CUSTOMER ========
        System.out.print("Enter customer type (1 = Individual, 2 = Business): ");
        int custType = sc.nextInt();
        sc.nextLine(); // consume newline

        Customer customer = null;

        if (custType == 1) {
            System.out.print("Enter Firstname: ");
            String first = sc.nextLine();
            System.out.print("Enter Surname: ");
            String last = sc.nextLine();
            System.out.print("Enter Address: ");
            String address = sc.nextLine();
            System.out.print("Enter Customer ID: ");
            String custID = sc.nextLine();
            System.out.print("Enter Date of Birth (yyyy-mm-dd): ");
            String dob = sc.nextLine();
            System.out.print("Enter ID Number: ");
            String idNum = sc.nextLine();

            customer = new CustomerIndividual(first, last, address, custID, dob, idNum);
        } else if (custType == 2) {
            System.out.print("Enter Firstname: ");
            String first = sc.nextLine();
            System.out.print("Enter Surname: ");
            String last = sc.nextLine();
            System.out.print("Enter Address: ");
            String address = sc.nextLine();
            System.out.print("Enter Customer ID: ");
            String custID = sc.nextLine();
            System.out.print("Enter Business Name: ");
            String businessName = sc.nextLine();
            System.out.print("Enter Business Registration Number: ");
            String regNum = sc.nextLine();
            System.out.print("Enter Business Address: ");
            String businessAddress = sc.nextLine();

            customer = new CustomerBusiness(first, last, address, custID, businessName, regNum, businessAddress);
        }

        customers.add(customer);

        boolean addingAccounts = true;
        while (addingAccounts) {
            System.out.println("\nSelect account type to open:");
            System.out.println("1. Savings Account");
            System.out.println("2. Investment Account");
            System.out.println("3. Cheque Account");
            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Account Number: ");
            String accNum = sc.nextLine();
            System.out.print("Enter Branch: ");
            String branch = sc.nextLine();

            Account account = null;

            switch (choice) {
                case 1:
                    account = new SavingsAccount(accNum, branch, customer);
                    break;
                case 2:
                    System.out.print("Enter initial deposit (>= 500): ");
                    double initial = sc.nextDouble();
                    sc.nextLine();
                    try {
                        account = new Investment(accNum, branch, customer, initial);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                    break;
                case 3:
                    System.out.print("Enter Employer Name: ");
                    String empName = sc.nextLine();
                    System.out.print("Enter Employer Address: ");
                    String empAddress = sc.nextLine();
                    account = new ChequeAccount(accNum, branch, customer, empName, empAddress);
                    break;
                default:
                    System.out.println("Invalid choice");
                    continue;
            }

            customer.openAccount(account);
            System.out.print("Do you want to add another account? (yes/no): ");
            String ans = sc.nextLine();
            addingAccounts = ans.equalsIgnoreCase("yes");
        }

        // ======== OPERATIONS ========
        boolean running = true;
        while (running) {
            System.out.println("\nSelect operation:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Calculate Interest");
            System.out.println("4. Display Balances");
            System.out.println("5. Exit");
            System.out.print("Choice: ");
            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                    depositFunds(sc, customer);
                    break;
                case 2:
                    withdrawFunds(sc, customer);
                    break;
                case 3:
                    for (Account acc : customer.getAccounts()) {
                        acc.calculateInterest();
                    }
                    break;
                case 4:
                    displayBalances(customer);
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }

        sc.close();
        System.out.println("Thank you for using the Banking System.");
    }

    // ======== HELPER METHODS ========
    private static void depositFunds(Scanner sc, Customer customer) {
        Account acc = selectAccount(sc, customer);
        if (acc != null) {
            System.out.print("Enter amount to deposit: ");
            double amount = sc.nextDouble();
            sc.nextLine();
            acc.deposit(amount);
        }
    }

    private static void withdrawFunds(Scanner sc, Customer customer) {
        Account acc = selectAccount(sc, customer);
        if (acc != null) {
            System.out.print("Enter amount to withdraw: ");
            double amount = sc.nextDouble();
            sc.nextLine();
            if (acc instanceof Withdrawal) {
                ((Withdrawal) acc).withdraw(amount);
            } else {
                System.out.println("Withdrawals not allowed for this account.");
            }
        }
    }

    private static Account selectAccount(Scanner sc, Customer customer) {
        List<Account> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return null;
        }
        System.out.println("Select account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println((i + 1) + ". " + accounts.get(i).accountNumber + " (" + accounts.get(i).getClass().getSimpleName() + ")");
        }
        System.out.print("Choice: ");
        int choice = sc.nextInt();
        sc.nextLine();
        if (choice < 1 || choice > accounts.size()) {
            System.out.println("Invalid choice.");
            return null;
        }
        return accounts.get(choice - 1);
    }

    private static void displayBalances(Customer customer) {
        System.out.println("=== Account Balances ===");
        for (Account acc : customer.getAccounts()) {
            System.out.println(acc.accountNumber + " (" + acc.getClass().getSimpleName() + "): " + acc.balance);
        }
    }
}
