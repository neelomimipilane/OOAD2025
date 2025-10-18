import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AccountsViewController {

    private Customer customer;

    @FXML private Label titleLabel;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        titleLabel.setText("All Accounts - Customer: " + customer.getFirstname());
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
