import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    private Customer currentCustomer;
    private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(data -> data.getValue().amountProperty().asObject());
        typeColumn.setCellValueFactory(data -> data.getValue().typeProperty());
        balanceColumn.setCellValueFactory(data -> data.getValue().balanceProperty().asObject());

        transactionsTable.setItems(transactionData);
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        if (customer != null) {
            usernameLabel.setText("Welcome, " + customer.getFirstname() + " " + customer.getSurname());
            accountsCombo.setItems(FXCollections.observableArrayList(customer.getAccounts()));
            if (!customer.getAccounts().isEmpty()) {
                accountsCombo.getSelectionModel().selectFirst();
                updateAccountDetails();
            } else {
                accountNumberLabel.setText("Account Number: -");
                accountTypeLabel.setText("Account Type: -");
                balanceLabel.setText("Balance: P 0.00");
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
            accountTypeLabel.setText("Account Type: " + selected.getClass().getSimpleName());
            balanceLabel.setText(String.format("Balance: P %.2f", selected.getBalance()));
            transactionData.setAll(selected.getTransactions());
        }
    }

    @FXML
    private void handleDepositFunds() {
        Account selected = getSelectedAccount();
        if (selected == null) return;
        openNewWindow("/views/DepositView.fxml", "Deposit Funds", selected);
    }

    @FXML
    private void handleWithdrawFunds() {
        Account selected = getSelectedAccount();
        if (selected == null) return;
        openNewWindow("/views/WithdrawView.fxml", "Withdraw Funds", selected);
    }

    @FXML
    private void handleTransferFunds() {
        Account selected = getSelectedAccount();
        if (selected == null) return;
        openNewWindow("/views/TransferView.fxml", "Transfer Funds", selected);
    }

    @FXML
    private void handlePayInterest() {
        openNewWindow("/views/PayInterestView.fxml", "Pay Monthly Interest", currentCustomer);
    }

    @FXML
    private void handleViewAccounts() {
        openNewWindow("/views/AccountsView.fxml", "All Accounts", currentCustomer);
    }

    @FXML
    private void handleOpenAccount() {
        openNewWindow("/views/OpenAccountView.fxml", "Open New Account", currentCustomer);
    }

    @FXML
    private void handleLogout() {
        showAlert("Logout", "You have been logged out.");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            Object controller = loader.getController();
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
                ((OpenAccountController) controller).setCustomer((Customer) dataToPass);
            }

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open window: " + title);
        }
    }
}
