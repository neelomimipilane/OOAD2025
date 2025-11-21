import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CustomerSettingsController {

    @FXML private Label customerNameLabel;
    @FXML private TextField firstnameField;
    @FXML private TextField surnameField;
    @FXML private TextField addressField;
    @FXML private TextField emailField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField currentPasswordField2;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private PasswordField deletePasswordField;
    @FXML private TextArea customerInfoArea;

    private Customer currentCustomer;

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (currentCustomer != null) {
            customerNameLabel.setText("Settings for: " + currentCustomer.getFirstname() + " " + currentCustomer.getSurname());
            firstnameField.setText(currentCustomer.getFirstname());
            surnameField.setText(currentCustomer.getSurname());
            addressField.setText(currentCustomer.getAddress());
            emailField.setText(currentCustomer.getEmail());

            // Display customer information
            displayCustomerInfo();
        }
    }

    private void displayCustomerInfo() {
        if (currentCustomer != null) {
            String details = currentCustomer.getMyDetails();
            String accountsInfo = currentCustomer.getMyAccountsInfo();

            StringBuilder info = new StringBuilder();
            info.append("=== PERSONAL INFORMATION ===\n");
            info.append(details).append("\n");
            info.append("=== ACCOUNTS ===\n");
            info.append(accountsInfo);

            customerInfoArea.setText(info.toString());
        }
    }

    @FXML
    private void handleUpdateInfo() {
        String currentPassword = currentPasswordField.getText();
        String newFirstname = firstnameField.getText();
        String newSurname = surnameField.getText();
        String newAddress = addressField.getText();
        String newEmail = emailField.getText();

        if (currentPassword.isEmpty()) {
            showAlert("Error", "Please enter your current password to make changes.");
            return;
        }

        if (newFirstname.isEmpty() || newSurname.isEmpty() || newAddress.isEmpty() || newEmail.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        boolean success = currentCustomer.updateMyInfo(
                currentPassword,
                newFirstname,
                newSurname,
                newAddress,
                newEmail
        );

        if (success) {
            showAlert("Success", "Your information has been updated successfully.");
            loadCustomerData(); // Refresh displayed data
            currentPasswordField.clear();
        } else {
            showAlert("Error", "Failed to update information. Please check your password.");
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField2.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill in all password fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New passwords do not match.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long.");
            return;
        }

        boolean success = currentCustomer.changeMyPassword(currentPassword, newPassword);

        if (success) {
            showAlert("Success", "Your password has been changed successfully.");
            currentPasswordField2.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            showAlert("Error", "Failed to change password. Please check your current password.");
        }
    }

    @FXML
    private void handleUpdateEmail() {
        String currentPassword = currentPasswordField.getText();
        String newEmail = emailField.getText();

        if (currentPassword.isEmpty()) {
            showAlert("Error", "Please enter your current password.");
            return;
        }

        if (newEmail.isEmpty() || !newEmail.contains("@")) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        boolean success = currentCustomer.updateMyEmail(currentPassword, newEmail);

        if (success) {
            showAlert("Success", "Your email has been updated successfully.");
            loadCustomerData();
            currentPasswordField.clear();
        } else {
            showAlert("Error", "Failed to update email. Please check your password.");
        }
    }

    @FXML
    private void handleUpdateAddress() {
        String currentPassword = currentPasswordField.getText();
        String newAddress = addressField.getText();

        if (currentPassword.isEmpty()) {
            showAlert("Error", "Please enter your current password.");
            return;
        }

        if (newAddress.isEmpty()) {
            showAlert("Error", "Please enter your new address.");
            return;
        }

        boolean success = currentCustomer.updateMyAddress(currentPassword, newAddress);

        if (success) {
            showAlert("Success", "Your address has been updated successfully.");
            loadCustomerData();
            currentPasswordField.clear();
        } else {
            showAlert("Error", "Failed to update address. Please check your password.");
        }
    }

    @FXML
    private void handleRefreshInfo() {
        displayCustomerInfo();
        showAlert("Refreshed", "Information updated successfully.");
    }

    @FXML
    private void handleDeleteAccount() {
        String password = deletePasswordField.getText();

        if (password.isEmpty()) {
            showAlert("Error", "Please enter your password to confirm account deletion.");
            return;
        }

        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Account Deletion");
        confirmation.setHeaderText("WARNING: This action cannot be undone!");
        confirmation.setContentText("All your accounts, transactions, and personal information will be permanently deleted.\n\nAre you absolutely sure?");

        ButtonType yesButton = new ButtonType("Yes, Delete My Account", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No, Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getButtonTypes().setAll(yesButton, noButton);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                boolean success = currentCustomer.deleteMyAccount(password);

                if (success) {
                    showAlert("Account Deleted", "Your account has been deleted successfully. Thank you for banking with us.");
                    // Close the application or return to login
                    Stage stage = (Stage) customerNameLabel.getScene().getWindow();
                    stage.close();
                } else {
                    showAlert("Error", "Failed to delete account. Please check your password.");
                }
            }
        });
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) customerNameLabel.getScene().getWindow();
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