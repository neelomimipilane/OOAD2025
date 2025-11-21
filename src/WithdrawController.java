import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class WithdrawController {

    private Account account;

    @FXML private Label titleLabel;
    @FXML private Label accountInfoLabel;
    @FXML private Label balanceLabel;
    @FXML private Label statusLabel;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;

    public void setAccount(Account account) {
        this.account = account;
        updateAccountDisplay();
    }

    private void updateAccountDisplay() {
        if (account != null) {
            titleLabel.setText("Withdraw Funds - Account: " + account.getAccountNumber());

            String accountType = getAccountType();
            accountInfoLabel.setText(accountType + " - " + account.getAccountNumber());
            balanceLabel.setText("Available Balance: P " + String.format("%.2f", account.getBalance()));

            // Show account-specific information
            if (account instanceof SavingsAccount) {
                balanceLabel.setText(balanceLabel.getText() + " (Min Balance: P 100)");
            } else if (account instanceof Investment) {
                balanceLabel.setText(balanceLabel.getText() + " (Min Balance: P 1000)");
            } else if (account instanceof ChequeAccount) {
                ChequeAccount cheque = (ChequeAccount) account;
                balanceLabel.setText(balanceLabel.getText() + " (Overdraft: P " + cheque.getOverdraftLimit() + ")");
            }
        }
    }

    private String getAccountType() {
        if (account instanceof SavingsAccount) return "Savings Account";
        else if (account instanceof ChequeAccount) return "Cheque Account";
        else if (account instanceof Investment) return "Investment Account";
        else return "Account";
    }

    @FXML
    private void handleWithdraw() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        double amount = Double.parseDouble(amountField.getText());
        String description = descriptionField.getText().trim();

        // Perform withdrawal
        boolean success = performWithdrawal(amount, description);

        if (success) {
            showStatus("Withdrawal successful!", false);
            clearForm();
            updateAccountDisplay();
        } else {
            showWithdrawalError(amount);
        }
    }

    @FXML
    private void handleQuickAmount(ActionEvent event) {
        Button button = (Button) event.getSource();
        String amount = button.getUserData().toString();
        amountField.setText(amount);
    }

    private boolean validateInputs() {
        statusLabel.setVisible(false);

        // Check amount
        if (amountField.getText().trim().isEmpty()) {
            showStatus("Please enter withdrawal amount", true);
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

    private boolean performWithdrawal(double amount, String description) {
        try {
            String withdrawDescription = description.isEmpty() ? "Withdrawal" : description;
            return account.withdraw(amount, withdrawDescription);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showWithdrawalError(double amount) {
        String errorMessage = "Withdrawal failed: ";

        if (account instanceof SavingsAccount) {
            if (amount > account.getBalance()) {
                errorMessage += "Insufficient funds";
            } else {
                errorMessage += "Must maintain minimum balance of P 100";
            }
        } else if (account instanceof Investment) {
            if (amount > account.getBalance()) {
                errorMessage += "Insufficient funds";
            } else {
                errorMessage += "Must maintain minimum balance of P 1000";
            }
        } else if (account instanceof ChequeAccount) {
            ChequeAccount cheque = (ChequeAccount) account;
            if ((account.getBalance() - amount) < -cheque.getOverdraftLimit()) {
                errorMessage += "Overdraft limit exceeded";
            } else {
                errorMessage += "Withdrawal not allowed";
            }
        } else {
            errorMessage += "Insufficient funds or withdrawal not allowed";
        }

        showStatus(errorMessage, true);
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #64ffda; -fx-font-weight: bold;");
        }
        statusLabel.setVisible(true);
    }

    private void clearForm() {
        amountField.clear();
        descriptionField.clear();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}