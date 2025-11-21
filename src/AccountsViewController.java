import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AccountsViewController {

    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, String> accountNumberColumn;
    @FXML private TableColumn<Account, String> accountTypeColumn;
    @FXML private TableColumn<Account, Double> balanceColumn;
    @FXML private TableColumn<Account, Double> interestRateColumn;

    private Customer customer;

    @FXML
    private void initialize() {
        // Set up table columns
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        // Account type column - handle different account types
        accountTypeColumn.setCellValueFactory(cellData -> {
            Account account = cellData.getValue();
            if (account instanceof SavingsAccount) {
                return new javafx.beans.property.SimpleStringProperty("Savings Account");
            } else if (account instanceof ChequeAccount) {
                return new javafx.beans.property.SimpleStringProperty("Cheque Account");
            } else if (account instanceof Investment) {
                return new javafx.beans.property.SimpleStringProperty("Investment Account");
            } else {
                return new javafx.beans.property.SimpleStringProperty("Account");
            }
        });

        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        interestRateColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));

        // Format balance column
        balanceColumn.setCellFactory(column -> new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText(String.format("P %.2f", balance));
                }
            }
        });

        // Format interest rate column - show as percentage
        interestRateColumn.setCellFactory(column -> new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double rate, boolean empty) {
                super.updateItem(rate, empty);
                if (empty || rate == null) {
                    setText(null);
                } else {
                    // Convert decimal to percentage (0.0005 -> 0.05%)
                    setText(String.format("%.3f%%", rate * 100));
                }
            }
        });
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateAccountsDisplay();
    }

    private void updateAccountsDisplay() {
        if (customer != null) {
            accountsTable.getItems().setAll(customer.getAccounts());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) accountsTable.getScene().getWindow();
        stage.close();
    }
}