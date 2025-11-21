import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class PayInterestController {
    private Customer customer;

    @FXML private Label titleLabel;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        titleLabel.setText("Pay Interest - Customer: " + customer.getFirstname());

        // Automatically pay interest when window opens
        payInterest();
    }

    private void payInterest() {
        int count = 0;
        for (Account acc : customer.getAccounts()) {
            if (acc instanceof InterestBearing) {
                ((InterestBearing) acc).calculateInterest();
                count++;
            }
        }

        showAlert("Interest Paid",
                "Monthly interest applied to " + count + " account(s)\n" +
                        "Total Balance: P" + String.format("%.2f", Main.getTotalBalance(customer)));
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