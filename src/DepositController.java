import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DepositController {
    private Account account;

    @FXML private Label titleLabel;
    @FXML private TextField amountField;

    public void setAccount(Account account) {
        this.account = account;
        titleLabel.setText("Deposit Funds - Account: " + account.getAccountNumber());
    }

    @FXML
    private void handleDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showAlert("Error", "Deposit amount must be positive");
                return;
            }

            account.deposit(amount);
            showAlert("Success", String.format("Successfully deposited P%.2f", amount));
            handleBack();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}