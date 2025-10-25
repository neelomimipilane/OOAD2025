import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

        // Perform transfer - CALL THE METHOD, DON'T DEFINE IT HERE
        boolean success = performTransfer(account, toAccount, amount, description);

        if (success) {
            showStatus("Transfer successful!", false);
            clearForm();
            updateAccountDisplay();
            loadRecentTransfers();
        } else {
            // Show specific withdrawal failure message
            if (account instanceof SavingsAccount) {
                showStatus("Transfer failed: Savings account must maintain minimum balance", true);
            } else if (account instanceof Investment) {
                showStatus("Transfer failed: Investment account must maintain minimum balance of P1000", true);
            } else {
                showStatus("Transfer failed: Insufficient funds or withdrawal not allowed", true);
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

    private boolean performTransfer(Account fromAccount, Account toAccount, double amount, String description) {
        try {
            // Withdraw from source account
            boolean withdrawSuccess = fromAccount.withdraw(amount, "Transfer to " + toAccount.getAccountNumber());

            if (withdrawSuccess) {
                // Deposit to destination account
                boolean depositSuccess = toAccount.deposit(amount,
                        description.isEmpty() ? "Transfer from " + fromAccount.getAccountNumber() : description);

                if (depositSuccess) {
                    return true;
                } else {
                    // If deposit fails, refund the withdrawal
                    fromAccount.deposit(amount, "Refund - transfer failed");
                    showStatus("Transfer failed: Could not deposit to destination account", true);
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Transfer error: " + e.getMessage(), true);
            return false;
        }
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

        // Search in all customers (you might need to access your main customer list)
        for (Customer customer : Main.getCustomers()) {
            for (Account acc : customer.getAccounts()) {
                if (acc.getAccountNumber().equals(accountNumber)) {
                    return acc;
                }
            }
        }

        return null;
    }

    private Customer findCustomerForAccount(Account account) {
        for (Customer customer : Main.getCustomers()) {
            if (customer.getAccounts().contains(account)) {
                return customer;
            }
        }
        return null;
    }

    private void loadRecentTransfers() {
        if (account != null) {
            ObservableList<Transaction> recentTransfers = FXCollections.observableArrayList();

            // Get last 5 transfer transactions - use getType() method
            account.getTransactions().stream()
                    .filter(t -> t.getType().equalsIgnoreCase("Transfer"))
                    .limit(5)
                    .forEach(recentTransfers::add);

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