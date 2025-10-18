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
    private PasswordField passwordField;  // This one works

    @FXML
    private void initialize() {
        System.out.println("LoginController initialized!");
        System.out.println("passwordField is null: " + (passwordField == null));
    }

    @FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        try {
            System.out.println("Login button clicked");
            loadDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Login Error", "Error during login: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(javafx.event.ActionEvent event) {
        try {
            System.out.println("Register button clicked");

            // Get the button that was clicked and use it to get the stage
            Button registerButton = (Button) event.getSource();
            Stage currentStage = (Stage) registerButton.getScene().getWindow();

            // Load register form from resources
            Parent root = FXMLLoader.load(getClass().getResource("/Register.fxml"));

            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load registration form: " + e.getMessage());
        }
    }

    private void loadDashboard() {
        try {
            System.out.println("Loading dashboard...");

            // Use the working passwordField to get the stage
            Stage currentStage = (Stage) passwordField.getScene().getWindow();

            // Make sure we're loading the FXML file, not HTML
            Parent root = FXMLLoader.load(getClass().getResource("/DashBoard.fxml"));

            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load dashboard: " + e.getMessage());
        }
    }

    // ADD THIS MISSING METHOD
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Optional: Add debug method to check files
    private void checkDashboardFile() {
        System.out.println("=== Checking Dashboard File ===");

        String[] possibleNames = {
                "/DashBoard.fxml",
                "/Dashboard.fxml",
                "/DashBoard.html",
                "/Dashboard.html"
        };

        for (String name : possibleNames) {
            java.net.URL url = getClass().getResource(name);
            if (url != null) {
                System.out.println("✓ FOUND: " + name);
            } else {
                System.out.println("✗ NOT FOUND: " + name);
            }
        }
    }
}