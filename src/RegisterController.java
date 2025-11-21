import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Random;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField customerIdField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private RadioButton individualRadio;
    @FXML private RadioButton businessRadio;
    @FXML private VBox businessFields;
    @FXML private TextField businessNameField;
    @FXML private TextField regNumberField;

    private ToggleGroup customerTypeGroup;
    private Random random = new Random();

    @FXML
    private void initialize() {
        // Setup radio button group
        customerTypeGroup = new ToggleGroup();
        individualRadio.setToggleGroup(customerTypeGroup);
        businessRadio.setToggleGroup(customerTypeGroup);
        individualRadio.setSelected(true);

        // Generate automatic customer ID
        generateCustomerId();

        // Listen for customer type changes
        customerTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == businessRadio) {
                businessFields.setVisible(true);
            } else {
                businessFields.setVisible(false);
            }
            // Regenerate customer ID when type changes
            generateCustomerId();
        });

        // Make customer ID field non-editable
        customerIdField.setEditable(false);
        customerIdField.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");

        // Add listener to email field for real-time validation
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmailInRealTime();
        });
    }

    private void generateCustomerId() {
        String prefix = individualRadio.isSelected() ? "IND" : "BUS";
        int randomNumber = 10000 + random.nextInt(90000); // 5-digit random number
        String customerId = prefix + randomNumber;
        customerIdField.setText(customerId);
    }

    private void validateEmailInRealTime() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            emailField.setStyle("-fx-border-color: #ff6b6b; -fx-border-width: 2;");
        } else {
            emailField.setStyle("");
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email validation regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if (!email.matches(emailRegex)) {
            return false;
        }

        // Check for common email providers
        String[] validDomains = {
                "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
                "icloud.com", "protonmail.com", "aol.com", "zoho.com",
                "mail.com", "yandex.com", "gmail.co.bw", "yahoo.co.bw"
        };

        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();

        for (String validDomain : validDomains) {
            if (domain.equals(validDomain)) {
                return true;
            }
        }

        return false;
    }

    @FXML
    private void handleRegister() {
        try {
            // Validate all required fields
            if (!validateForm()) {
                return;
            }

            // Get form data
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String customerId = customerIdField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validate email format
            if (!isValidEmail(email)) {
                showAlert("Invalid Email",
                        "Please enter a valid email address with proper format.\n\n" +
                                "Examples:\n" +
                                "• yourname@gmail.com\n" +
                                "• your.name@yahoo.com\n" +
                                "• name@hotmail.com\n" +
                                "• name@outlook.com");
                emailField.requestFocus();
                return;
            }

            // Check password match
            if (!password.equals(confirmPassword)) {
                showAlert("Error", "Passwords do not match");
                confirmPasswordField.requestFocus();
                return;
            }

            // Check password strength
            if (password.length() < 6) {
                showAlert("Weak Password", "Password must be at least 6 characters long");
                passwordField.requestFocus();
                return;
            }

            // Determine customer type
            String customerType = individualRadio.isSelected() ? "I" : "B";
            String dob = "2000-01-01"; // Default DOB for demo
            String idNumber = "ID" + System.currentTimeMillis(); // Generate unique ID

            // Business-specific fields
            String businessName = businessNameField.getText().trim();
            String regNumber = regNumberField.getText().trim();
            String businessAddress = address; // Use same address for business

            // For business customers, validate business fields
            if (customerType.equals("B") && (businessName.isEmpty() || regNumber.isEmpty())) {
                showAlert("Error", "Please fill all business information fields");
                return;
            }

            // Register the customer
            boolean success = Main.registerCustomer(
                    firstName, lastName, address, customerId,
                    password, customerType, dob, idNumber,
                    businessName, regNumber, businessAddress,
                    email
            );

            if (success) {
                String customerTypeDisplay = customerType.equals("I") ? "Individual" : "Business";
                String interestRate = customerType.equals("I") ? "0.025%" : "0.05%";

                showAlert("Registration Successful",
                        "Account created successfully!\n\n" +
                                "Customer Type: " + customerTypeDisplay + "\n" +
                                "Customer ID: " + customerId + "\n" +
                                "Name: " + firstName + " " + lastName + "\n" +
                                "Email: " + email + "\n" +
                                "Interest Rate: " + interestRate + "\n\n" +
                                "You can now login with your Customer ID and Password.");

                handleBackToLogin();
            } else {
                showAlert("Registration Failed", "Customer ID already exists. Please try registering again.");
                generateCustomerId(); // Generate new ID if there's a conflict
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Registration Error", "An error occurred during registration: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        // Check all required fields
        if (firstNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your first name");
            firstNameField.requestFocus();
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your last name");
            lastNameField.requestFocus();
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your phone number");
            phoneField.requestFocus();
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your email address");
            emailField.requestFocus();
            return false;
        }
        if (addressField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter your address");
            addressField.requestFocus();
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter a password");
            passwordField.requestFocus();
            return false;
        }
        if (confirmPasswordField.getText().isEmpty()) {
            showAlert("Validation Error", "Please confirm your password");
            confirmPasswordField.requestFocus();
            return false;
        }
        return true;
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load login screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateNewId() {
        generateCustomerId();
        showAlert("New ID Generated", "New Customer ID has been generated: " + customerIdField.getText());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}