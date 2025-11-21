import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Map;

public class TransferController {

    private Account account;
    private Customer currentCustomer;

    @FXML private Label titleLabel;
    @FXML private Label fromAccountLabel;
    @FXML private Label statusLabel;
    @FXML private TextField toAccountField;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private TableView<Transaction> recentTransfersTable;

    public void setAccount(Account account) {
        this.account = account;
        this.currentCustomer = findCustomerForAccount(account);

        if (account != null) {
            titleLabel.setText("Transfer Funds - Account: " + account.getAccountNumber());
            fromAccountLabel.setText(account.getAccountNumber() + " (Balance: P " + String.format("%.2f", account.getBalance()) + ")");
        }

        loadRecentTransfers();
    }

    @FXML
    private void handleTransfer() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        String toAccountNumber = toAccountField.getText().trim();
        double amount = Double.parseDouble(amountField.getText());
        String description = descriptionField.getText().trim();

        // Check if transferring to same account
        if (toAccountNumber.equals(account.getAccountNumber())) {
            showStatus("Cannot transfer to the same account", true);
            return;
        }

        // Check sufficient funds
        if (amount > account.getBalance()) {
            showStatus("Insufficient funds for transfer", true);
            return;
        }

        // Find destination account
        Account toAccount = findAccountByNumber(toAccountNumber);
        if (toAccount == null) {
            showStatus("Destination account not found", true);
            return;
        }

        // Perform transfer with file operations
        boolean success = performTransferWithFiles(account, toAccount, amount, description);

        if (success) {
            showStatus("Transfer successful!", false);
            clearForm();
            updateAccountDisplay();
            loadRecentTransfers();

            // Refresh account data from files
            refreshAccountData();
        } else {
            // Show specific failure message
            if (account instanceof SavingsAccount) {
                showStatus("Transfer failed: Transfers are not allowed from Savings accounts", true);
            } else if (account instanceof Investment) {
                showStatus("Transfer failed: Investment account must maintain minimum balance of P1000", true);
            } else {
                showStatus("Transfer failed: Insufficient funds or withdrawal not allowed", true);
            }
        }
    }

    /**
     * Perform transfer with file operations to ensure both withdrawal and deposit are saved
     */
    private boolean performTransferWithFiles(Account fromAccount, Account toAccount, double amount, String description) {
        // Check if fromAccount is Savings Account - prevent transfers
        if (fromAccount instanceof SavingsAccount) {
            showStatus("Transfer denied: Transfers are not allowed from Savings accounts", true);
            return false;
        }

        // Check sufficient funds
        if (fromAccount.getBalance() < amount) {
            showStatus("Insufficient funds for transfer", true);
            return false;
        }

        // For Investment accounts, check minimum balance
        if (fromAccount instanceof Investment) {
            double minBalance = 1000;
            if ((fromAccount.getBalance() - amount) < minBalance) {
                showStatus("Transfer failed: Investment account must maintain minimum balance of P1000", true);
                return false;
            }
        }

        // Perform withdrawal from source account
        boolean withdrawSuccess = fromAccount.withdraw(amount,
                description.isEmpty() ? "Transfer to " + toAccount.getAccountNumber() : description);

        if (!withdrawSuccess) {
            return false;
        }

        // Perform deposit to destination account
        toAccount.deposit(amount,
                description.isEmpty() ? "Transfer from " + fromAccount.getAccountNumber() : description);

        System.out.println("Transfer completed successfully and saved to files");
        return true;
    }

    /**
     * Refresh account data from files to ensure UI shows latest data
     */
    private void refreshAccountData() {
        if (account != null && currentCustomer != null) {
            // Refresh customer data from files by loading all customers and finding the specific one
            Map<String, Customer> allCustomers = FileManager.loadAllCustomers();
            Customer updatedCustomer = allCustomers.get(currentCustomer.getCustomerID());

            if (updatedCustomer != null) {
                // Find the updated account in the refreshed customer
                for (Account acc : updatedCustomer.getAccounts()) {
                    if (acc.getAccountNumber().equals(account.getAccountNumber())) {
                        this.account = acc;
                        break;
                    }
                }
            }
        }
    }

    private boolean validateInputs() {
        statusLabel.setVisible(false);

        // Check destination account
        if (toAccountField.getText().trim().isEmpty()) {
            showStatus("Please enter destination account number", true);
            return false;
        }

        // Check amount
        if (amountField.getText().trim().isEmpty()) {
            showStatus("Please enter transfer amount", true);
            return false;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showStatus("Amount must be greater than 0", true);
                return false;
            }
        } catch (NumberFormatException e) {
            showStatus("Please enter a valid amount", true);
            return false;
        }

        return true;
    }

    private Account findAccountByNumber(String accountNumber) {
        // Search in current customer's accounts first
        if (currentCustomer != null) {
            for (Account acc : currentCustomer.getAccounts()) {
                if (acc.getAccountNumber().equals(accountNumber)) {
                    return acc;
                }
            }
        }

        // Search in all customers using FileManager
        Map<String, Customer> allCustomers = FileManager.loadAllCustomers();
        for (Customer customer : allCustomers.values()) {
            for (Account acc : customer.getAccounts()) {
                if (acc.getAccountNumber().equals(accountNumber)) {
                    return acc;
                }
            }
        }
        return null;
    }

    private Customer findCustomerForAccount(Account account) {
        // Load all customers and find the one that owns this account
        Map<String, Customer> allCustomers = FileManager.loadAllCustomers();
        for (Customer customer : allCustomers.values()) {
            for (Account acc : customer.getAccounts()) {
                if (acc.getAccountNumber().equals(account.getAccountNumber())) {
                    return customer;
                }
            }
        }
        return null;
    }

    private void loadRecentTransfers() {
        if (account != null) {
            ObservableList<Transaction> recentTransfers = FXCollections.observableArrayList();

            // Get last 5 transfer transactions
            int count = 0;
            for (int i = account.getTransactions().size() - 1; i >= 0 && count < 5; i--) {
                Transaction transaction = account.getTransactions().get(i);
                if (transaction.getType().equalsIgnoreCase("WITHDRAWAL") &&
                        transaction.getDescription().toLowerCase().contains("transfer")) {
                    recentTransfers.add(transaction);
                    count++;
                }
            }

            recentTransfersTable.setItems(recentTransfers);
        }
    }

    /**
     * Shows status messages in the UI
     * @param message The message to display
     * @param isError true for error (red), false for success (green)
     */
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #64ffda; -fx-font-weight: bold;");
        }
        statusLabel.setVisible(true);
    }

    /**
     * Clears the form fields after successful transfer
     */
    private void clearForm() {
        toAccountField.clear();
        amountField.clear();
        descriptionField.clear();
    }

    /**
     * Updates the account balance display
     */
    private void updateAccountDisplay() {
        if (account != null) {
            fromAccountLabel.setText(account.getAccountNumber() + " (Balance: P " + String.format("%.2f", account.getBalance()) + ")");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}