import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TransferController {

    private Account account;

    @FXML private Label titleLabel;

    public void setAccount(Account account) {
        this.account = account;
        titleLabel.setText("Transfer Funds - Account: " + account.getAccountNumber());
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
