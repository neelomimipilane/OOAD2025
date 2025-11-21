import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;

public class DashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label accountNumberLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label balanceLabel;
    @FXML private ComboBox<Account> accountsCombo;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, Double> balanceColumn;

    @FXML private Button withdrawButton;
    @FXML private Button transferButton;

    private Customer currentCustomer;
    private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Fix: Use lambda expressions to access Transaction properties directly
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        amountColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getAmount()).asObject());
        typeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
        balanceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getBalance()).asObject());

        transactionsTable.setItems(transactionData);
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        refreshAccounts();
    }

    public Customer getCustomer() {
        return currentCustomer;
    }

    public void refreshAccounts() {
        if (currentCustomer != null) {
            usernameLabel.setText("Welcome, " + currentCustomer.getFirstname() + " " + currentCustomer.getSurname());

            // Clear and reload accounts
            accountsCombo.getItems().clear();
            accountsCombo.getItems().addAll(currentCustomer.getAccounts());

            if (!currentCustomer.getAccounts().isEmpty()) {
                accountsCombo.getSelectionModel().selectFirst();
                updateAccountDetails();
            } else {
                accountNumberLabel.setText("Account Number: -");
                accountTypeLabel.setText("Account Type: -");
                balanceLabel.setText("Balance: P 0.00");
                transactionsTable.getItems().clear();

                // Disable buttons if no accounts
                if (withdrawButton != null) withdrawButton.setDisable(true);
                if (transferButton != null) transferButton.setDisable(true);
            }
        }
    }

    @FXML
    private void handleAccountSelection() {
        updateAccountDetails();
    }

    private void updateAccountDetails() {
        Account selected = getSelectedAccount();
        if (selected != null) {
            accountNumberLabel.setText("Account Number: " + selected.getAccountNumber());

            // Show detailed account type with interest rate
            if (selected instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) selected;
                String customerType = (currentCustomer instanceof CustomerBusiness) ? "Business" : "Individual";
                accountTypeLabel.setText("Account Type: Savings Account (" + savings.getInterestRate() + "% interest - " + customerType + ")");

                // Disable withdraw and transfer buttons for Savings Account
                if (withdrawButton != null) withdrawButton.setDisable(true);
                if (transferButton != null) transferButton.setDisable(true);

            } else if (selected instanceof ChequeAccount) {
                accountTypeLabel.setText("Account Type: Cheque Account");

                // Enable buttons for Cheque Account
                if (withdrawButton != null) withdrawButton.setDisable(false);
                if (transferButton != null) transferButton.setDisable(false);

            } else if (selected instanceof Investment) {
                Investment investment = (Investment) selected;
                String customerType = (currentCustomer instanceof CustomerBusiness) ? "Business" : "Individual";
                accountTypeLabel.setText("Account Type: Investment Account (" + investment.getInterestRate() + "% interest - " + customerType + ")");

                // Enable buttons for Investment Account
                if (withdrawButton != null) withdrawButton.setDisable(false);
                if (transferButton != null) transferButton.setDisable(false);

            } else {
                accountTypeLabel.setText("Account Type: " + selected.getClass().getSimpleName());

                // Default to enabling buttons for other account types
                if (withdrawButton != null) withdrawButton.setDisable(false);
                if (transferButton != null) transferButton.setDisable(false);
            }

            balanceLabel.setText(String.format("Balance: P %.2f", selected.getBalance()));
            transactionData.setAll(selected.getTransactions());
        }
    }

    @FXML
    private void handleDepositFunds() {
        Account selected = getSelectedAccount();
        if (selected == null) return;
        openNewWindow("Deposit.fxml", "Deposit Funds", selected);
    }

    @FXML
    private void handleWithdrawFunds() {
        Account selected = getSelectedAccount();
        if (selected == null) return;

        // Additional check for Savings Account
        if (selected instanceof SavingsAccount) {
            showAlert("Withdrawal Not Allowed", "Withdrawals are not permitted from Savings Accounts. This account is for saving money and earning interest only.");
            return;
        }

        openNewWindow("WithdrawView.fxml", "Withdraw Funds", selected);
    }

    @FXML
    private void handleTransferFunds() {
        Account selected = getSelectedAccount();
        if (selected == null) return;

        // Additional check for Savings Account
        if (selected instanceof SavingsAccount) {
            showAlert("Transfer Not Allowed", "Transfers are not permitted from Savings Accounts. This account is for saving money and earning interest only.");
            return;
        }

        openNewWindow("TransferView.fxml", "Transfer Funds", selected);
    }

    @FXML
    private void handlePayInterest() {
        if (currentCustomer != null) {
            Main.payAllInterest(currentCustomer);
            updateAccountDetails(); // Refresh the display
            showAlert("Interest Paid", "Monthly interest has been applied to all eligible accounts");
        }
    }

    @FXML
    private void handleViewAccounts() {
        openNewWindow("AccountsView.fxml", "All Accounts", currentCustomer);
    }

    @FXML
    private void handleOpenAccount() {
        openNewWindow("OpenAccountView.fxml", "Open New Account", currentCustomer);
    }

    @FXML
    private void handleLogout() {
        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout Confirmation");
        confirmation.setHeaderText("Are you sure you want to logout?");
        confirmation.setContentText("You will be redirected to the login page.");

        // Customize the buttons
        ButtonType yesButton = new ButtonType("Yes, Logout");
        ButtonType noButton = new ButtonType("No, Cancel");
        confirmation.getButtonTypes().setAll(yesButton, noButton);

        // Show the dialog and wait for response
        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                // User confirmed logout - go back to login page
                try {
                    Stage currentStage = (Stage) usernameLabel.getScene().getWindow();

                    // Load login screen
                    Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen.fxml"));
                    Scene scene = new Scene(root);

                    currentStage.setScene(scene);
                    currentStage.setTitle("First National Bank Botswana - Login");
                    currentStage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Unable to logout: " + e.getMessage());
                }
            }
            // If user clicks "No", do nothing (dialog closes)
        });
    }

    private Account getSelectedAccount() {
        Account acc = accountsCombo.getSelectionModel().getSelectedItem();
        if (acc == null) showAlert("No Account Selected", "Please select an account.");
        return acc;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openNewWindow(String fxmlPath, String title, Object dataToPass) {
        try {
            System.out.println("Attempting to load: " + fxmlPath);

            // Check if resource exists
            URL resource = getClass().getResource("/" + fxmlPath);
            if (resource == null) {
                showAlert("Error", "FXML file not found: " + fxmlPath +
                        "\nAvailable resources in classpath: " + getClass().getResource("/"));
                return;
            }

            System.out.println("Resource found: " + resource);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            Object controller = loader.getController();
            if (controller != null) {
                if (controller instanceof DepositController && dataToPass instanceof Account) {
                    ((DepositController) controller).setAccount((Account) dataToPass);
                } else if (controller instanceof WithdrawController && dataToPass instanceof Account) {
                    ((WithdrawController) controller).setAccount((Account) dataToPass);
                } else if (controller instanceof TransferController && dataToPass instanceof Account) {
                    ((TransferController) controller).setAccount((Account) dataToPass);
                } else if (controller instanceof PayInterestController && dataToPass instanceof Customer) {
                    ((PayInterestController) controller).setCustomer((Customer) dataToPass);
                } else if (controller instanceof AccountsViewController && dataToPass instanceof Customer) {
                    ((AccountsViewController) controller).setCustomer((Customer) dataToPass);
                } else if (controller instanceof OpenAccountController && dataToPass instanceof Customer) {
                    OpenAccountController openAccountController = (OpenAccountController) controller;
                    openAccountController.setCustomer((Customer) dataToPass);
                    openAccountController.setDashboardController(this);
                } else if (controller instanceof CreateAccountController && dataToPass instanceof Customer) {
                    CreateAccountController createAccountController = (CreateAccountController) controller;
                    createAccountController.setCustomer((Customer) dataToPass);
                    createAccountController.setDashboardController(this);
                }
            } else {
                System.out.println("Controller is null for: " + fxmlPath);
            }

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open window: " + title +
                    "\nPath: " + fxmlPath +
                    "\nError: " + e.getMessage());
        }
    }

    // Add this method to your DashboardController
    public void showAccountCreationInfo() {
        if (currentCustomer != null) {
            StringBuilder info = new StringBuilder();
            info.append("Account Creation Rules:\n");
            info.append("• You can have multiple accounts of different types\n");
            info.append("• Only one account of each type allowed\n\n");
            info.append("Your current accounts:\n");

            boolean hasSavings = false;
            boolean hasCheque = false;
            boolean hasInvestment = false;

            for (Account acc : currentCustomer.getAccounts()) {
                if (acc instanceof SavingsAccount) {
                    hasSavings = true;
                    info.append("✓ Savings Account: ").append(acc.getAccountNumber()).append("\n");
                } else if (acc instanceof ChequeAccount) {
                    hasCheque = true;
                    info.append("✓ Cheque Account: ").append(acc.getAccountNumber()).append("\n");
                } else if (acc instanceof Investment) {
                    hasInvestment = true;
                    info.append("✓ Investment Account: ").append(acc.getAccountNumber()).append("\n");
                }
            }

            info.append("\nAvailable account types:\n");
            if (!hasSavings) info.append("• Savings Account\n");
            if (!hasCheque) info.append("• Cheque Account\n");
            if (!hasInvestment) info.append("• Investment Account\n");

            showAlert("Account Information", info.toString());
        }
    }

    @FXML
    private void handleSettings() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CustomerSettings.fxml"));
            Parent root = loader.load();

            CustomerSettingsController controller = loader.getController();
            controller.setCustomer(currentCustomer);

            stage.setTitle("Account Settings");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open settings: " + e.getMessage());
        }
    }
}