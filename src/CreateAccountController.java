import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CreateAccountController {

    @FXML private ChoiceBox<String> accountTypeChoice;
    @FXML private TextField accountNumberField;
    @FXML private TextField initialDepositField;
    @FXML private TextField employerNameField;
    @FXML private TextField employerAddressField;
    @FXML private HBox initialDepositBox;
    @FXML private HBox employerBox;
    @FXML private Label messageLabel;

    private Customer customer;

    @FXML
    public void initialize() {
        accountTypeChoice.getItems().addAll("Savings Account", "Cheque Account", "Investment Account");
        accountTypeChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFields(newVal));
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    private void updateFields(String type) {
        initialDepositBox.setVisible(false);
        employerBox.setVisible(false);
        messageLabel.setText("");

        switch (type) {
            case "Investment Account":
                initialDepositBox.setVisible(true);
                break;
            case "Cheque Account":
                employerBox.setVisible(true);
                break;
        }
    }

    @FXML
    private void handleCreate() {
        String type = accountTypeChoice.getValue();
        String accNum = accountNumberField.getText().trim();

        if (type == null || accNum.isEmpty()) {
            messageLabel.setText("Please select type and enter account number.");
            return;
        }

        // Prevent duplicate account types
        for (Account acc : customer.getAccounts()) {
            if (type.equals("Savings Account") && acc instanceof SavingsAccount ||
                    type.equals("Cheque Account") && acc instanceof ChequeAccount ||
                    type.equals("Investment Account") && acc instanceof Investment) {
                messageLabel.setText("You already have this type of account.");
                return;
            }
        }

        Account account = null;

        try {
            switch (type) {
                case "Savings Account":
                    account = new SavingsAccount(accNum, "Main Branch", customer);
                    break;
                case "Cheque Account":
                    String empName = employerNameField.getText().trim();
                    String empAddress = employerAddressField.getText().trim();
                    if (empName.isEmpty() || empAddress.isEmpty()) {
                        messageLabel.setText("Please enter employer details.");
                        return;
                    }
                    account = new ChequeAccount(accNum, "Main Branch", customer, empName, empAddress);
                    break;
                case "Investment Account":
                    String depositStr = initialDepositField.getText().trim();
                    if (depositStr.isEmpty()) {
                        messageLabel.setText("Enter initial deposit.");
                        return;
                    }
                    double deposit = Double.parseDouble(depositStr);
                    account = new Investment(accNum, "Main Branch", customer, deposit);
                    break;
            }

            if (account != null) {
                customer.openAccount(account);
                messageLabel.setText("Account created successfully!");
                Stage stage = (Stage) accountTypeChoice.getScene().getWindow();
                stage.close();
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number format.");
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        }
    }
}
