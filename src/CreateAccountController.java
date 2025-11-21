import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreateAccountController {

    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField accountNumberField;
    @FXML private TextField initialDepositField;
    @FXML private Label statusLabel;

    private Customer currentCustomer;
    private Stage dialogStage;
    private boolean accountCreated = false;
    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        // Set up account type options
        accountTypeComboBox.getItems().addAll("Savings", "Cheque", "Investment");

        // Auto-generate account number
        generateAccountNumber();
    }

    // Add this method - it was missing
    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        generateAccountNumber();
    }

    // Add this method - it was missing
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isAccountCreated() {
        return accountCreated;
    }

    private void generateAccountNumber() {
        if (currentCustomer != null && accountTypeComboBox.getValue() != null) {
            String typePrefix = getAccountTypePrefix(accountTypeComboBox.getValue());
            String customerIdPart = currentCustomer.getCustomerID().substring(3);
            String accountNumber = typePrefix + customerIdPart + System.currentTimeMillis() % 1000;
            accountNumberField.setText(accountNumber);
        }
    }

    private String getAccountTypePrefix(String accountType) {
        switch (accountType) {
            case "Savings": return "SAV";
            case "Cheque": return "CHQ";
            case "Investment": return "INV";
            default: return "ACC";
        }
    }

    @FXML
    private void handleCreateAccount() {
        if (currentCustomer == null) {
            showAlert("Error", "No customer selected.");
            return;
        }

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            String accountType = accountTypeComboBox.getValue();
            String accountNumber = accountNumberField.getText();
            double initialDeposit = Double.parseDouble(initialDepositField.getText());

            // Validate initial deposit
            if (initialDeposit < 0) {
                showAlert("Invalid Amount", "Initial deposit cannot be negative.");
                return;
            }

            // Check minimum deposit requirements
            if ("Savings".equals(accountType) && initialDeposit < 50.0) {
                showAlert("Minimum Deposit Required", "Savings account requires minimum deposit of BWP 50.00");
                return;
            }

            if ("Investment".equals(accountType) && initialDeposit < 1000.0) {
                showAlert("Minimum Deposit Required", "Investment account requires minimum deposit of BWP 1000.00");
                return;
            }

            // Create account using Main class
            boolean success = Main.createAccount(currentCustomer, accountType, accountNumber,
                    "", "", initialDeposit);

            if (success) {
                statusLabel.setText("Account created successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
                accountCreated = true;

                // Refresh the dashboard if available
                if (dashboardController != null) {
                    dashboardController.refreshAccounts();
                }

                // Show success dialog
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Account Created");
                alert.setHeaderText("Account Created Successfully");
                alert.setContentText("Account Number: " + accountNumber +
                        "\nType: " + accountType +
                        "\nInitial Deposit: BWP " + String.format("%.2f", initialDeposit));
                alert.showAndWait();

                // Close dialog
                if (dialogStage != null) {
                    dialogStage.close();
                }
            } else {
                statusLabel.setText("Failed to create account. Please try again.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid number for initial deposit.");
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (accountTypeComboBox.getValue() == null) {
            showAlert("Missing Information", "Please select an account type.");
            return false;
        }

        if (accountNumberField.getText().isEmpty()) {
            showAlert("Missing Information", "Account number is required.");
            return false;
        }

        if (initialDepositField.getText().isEmpty()) {
            showAlert("Missing Information", "Initial deposit is required.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void handleClear() {
        accountTypeComboBox.setValue(null);
        accountNumberField.clear();
        initialDepositField.clear();
        statusLabel.setText("");
        generateAccountNumber();
    }

    @FXML
    private void handleGenerateAccountNumber() {
        generateAccountNumber();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}