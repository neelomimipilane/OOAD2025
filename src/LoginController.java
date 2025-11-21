import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField customerIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void initialize() {
        System.out.println("LoginController initialized!");
        customerIdField.setPromptText("Enter Customer ID or Email");
    }

    @FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        try {
            String loginInput = customerIdField.getText().trim();
            String password = passwordField.getText();

            if (loginInput.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter both Customer ID/Email and Password");
                return;
            }

            // Use the enhanced login method that supports both Customer ID and Email
            Customer customer = Main.loginWithIdOrEmail(loginInput, password);

            if (customer != null) {
                showAlert("Login Successful",
                        "Welcome back, " + customer.getFirstname() + " " + customer.getSurname() + "!\n" +
                                "Login method: " + (loginInput.contains("@") ? "Email" : "Customer ID"));
                loadDashboard(customer);
            } else {
                showAlert("Login Failed",
                        "Invalid login credentials.\n\n" +
                                "You can login with:\n" +
                                "• Your Customer ID\n" +
                                "• Your registered email address");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Login Error", "Error during login: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(javafx.event.ActionEvent event) {
        try {
            Button registerButton = (Button) event.getSource();
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/Register.fxml"));
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load registration form: " + e.getMessage());
        }
    }

    @FXML
    private void handleForgotCredentials() {
        showAlert("Forgot Credentials",
                "If you forgot your login details:\n\n" +
                        "1. You can login with either:\n" +
                        "   • Your Customer ID\n" +
                        "   • Your registered email address\n\n" +
                        "2. If you still can't login, please:\n" +
                        "   • Contact customer support\n" +
                        "   • Visit your nearest branch\n\n" +
                        "Support: support@fnbb.co.bw | +267 123 4567");
    }

    private void loadDashboard(Customer customer) {
        try {
            Stage currentStage = (Stage) passwordField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashBoard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setCustomer(customer);
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}