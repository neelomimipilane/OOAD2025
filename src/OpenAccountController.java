import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Optional;

public class OpenAccountController {

    // Match these with your FXML fx:id values
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField accountNumberField;
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private VBox dynamicFieldsContainer;
    @FXML private Label featuresLabel;

    // Additional fields we need to handle
    private TextField initialDepositField;
    private TextField employerNameField;
    private TextField employerAddressField;

    private Customer currentCustomer;
    private Stage dialogStage;
    private boolean accountCreated = false;
    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        System.out.println("OpenAccountController initialized");

        // Set up account type options
        accountTypeCombo.getItems().addAll("Savings", "Cheque", "Investment");

        // Add listener to account type selection
        accountTypeCombo.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    handleAccountTypeSelection(newValue);
                });

        // Create dynamic fields
        createDynamicFields();

        // Auto-generate account number
        generateAccountNumber();

        // Set initial features text
        updateFeaturesText("Select an account type to view features");
    }

    private void createDynamicFields() {
        // Initial Deposit Field
        VBox depositBox = new VBox(5);
        Label depositLabel = new Label("Initial Deposit *");
        depositLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        initialDepositField = new TextField();
        initialDepositField.setPromptText("Enter initial deposit amount");
        initialDepositField.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: #888; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 5; -fx-pref-height: 35; -fx-pref-width: 300;");
        depositBox.getChildren().addAll(depositLabel, initialDepositField);

        // Employer Name Field (initially hidden)
        VBox employerNameBox = new VBox(5);
        employerNameBox.setVisible(false);
        Label employerNameLabel = new Label("Employer Name *");
        employerNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        employerNameField = new TextField();
        employerNameField.setPromptText("Enter employer name");
        employerNameField.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: #888; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 5; -fx-pref-height: 35; -fx-pref-width: 300;");
        employerNameBox.getChildren().addAll(employerNameLabel, employerNameField);

        // Employer Address Field (initially hidden)
        VBox employerAddressBox = new VBox(5);
        employerAddressBox.setVisible(false);
        Label employerAddressLabel = new Label("Employer Address *");
        employerAddressLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        employerAddressField = new TextField();
        employerAddressField.setPromptText("Enter employer address");
        employerAddressField.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: #888; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 5; -fx-pref-height: 35; -fx-pref-width: 300;");
        employerAddressBox.getChildren().addAll(employerAddressLabel, employerAddressField);

        // Add all fields to dynamic container
        dynamicFieldsContainer.getChildren().addAll(depositBox, employerNameBox, employerAddressBox);
    }

    private void handleAccountTypeSelection(String accountType) {
        if (accountType == null) return;

        // Show/hide employer fields based on account type
        boolean showEmployerFields = "Cheque".equals(accountType) || "Investment".equals(accountType);

        // Find and update employer fields visibility
        for (int i = 1; i < dynamicFieldsContainer.getChildren().size(); i++) {
            dynamicFieldsContainer.getChildren().get(i).setVisible(showEmployerFields);
        }

        // Update features text
        updateFeaturesTextBasedOnType(accountType);

        // Regenerate account number
        generateAccountNumber();
    }

    private void updateFeaturesTextBasedOnType(String accountType) {
        String features = "";
        switch (accountType) {
            case "Savings":
                features = "• Earn interest on your balance\n• Minimum deposit: BWP 50.00\n• No withdrawals allowed\n• Perfect for long-term saving";
                break;
            case "Cheque":
                features = "• No minimum balance required\n• Free transfers and withdrawals\n• Employer information required\n• Ideal for daily transactions";
                break;
            case "Investment":
                features = "• Higher interest rates\n• Minimum deposit: BWP 1000.00\n• Employer information required\n• For long-term investments";
                break;
            default:
                features = "Select an account type to view features";
        }
        updateFeaturesText(features);
    }

    private void updateFeaturesText(String text) {
        if (featuresLabel != null) {
            featuresLabel.setText(text);
            featuresLabel.setStyle("-fx-text-fill: #64ffda; -fx-font-size: 11; -fx-wrap-text: true;");
        }
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        if (titleLabel != null) {
            titleLabel.setText("Create a New Account for " + customer.getFirstname() + " " + customer.getSurname());
        }
        generateAccountNumber();
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void generateAccountNumber() {
        if (currentCustomer != null && accountTypeCombo != null && accountTypeCombo.getValue() != null) {
            String typePrefix = getAccountTypePrefix(accountTypeCombo.getValue());
            String customerIdPart = currentCustomer.getCustomerID().length() > 3 ?
                    currentCustomer.getCustomerID().substring(3) : currentCustomer.getCustomerID();
            String accountNumber = typePrefix + customerIdPart + (System.currentTimeMillis() % 10000);
            if (accountNumberField != null) {
                accountNumberField.setText(accountNumber);
            }
        }
    }

    private String getAccountTypePrefix(String accountType) {
        switch (accountType) {
            case "Savings": return "SAV";
            case "Cheque": return "CHQ";
            case "Investment": return "INV";
            default: return "ACC";
        }
    }

    @FXML
    private void handleCreateAccount() {
        if (currentCustomer == null) {
            showMessage("Error: No customer selected.", true);
            return;
        }

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            String accountType = accountTypeCombo.getValue();
            String accountNumber = accountNumberField.getText();
            String employerName = employerNameField != null ? employerNameField.getText() : "";
            String employerAddress = employerAddressField != null ? employerAddressField.getText() : "";
            double initialDeposit = Double.parseDouble(initialDepositField.getText());

            // Validate initial deposit
            if (initialDeposit < 0) {
                showMessage("Initial deposit cannot be negative.", true);
                return;
            }

            // Check minimum deposit requirements
            if ("Savings".equals(accountType) && initialDeposit < 50.0) {
                showMessage("Savings account requires minimum deposit of BWP 50.00", true);
                return;
            }

            if ("Investment".equals(accountType) && initialDeposit < 1000.0) {
                showMessage("Investment account requires minimum deposit of BWP 1000.00", true);
                return;
            }

            // Create account using Main class
            boolean success = Main.createAccount(currentCustomer, accountType, accountNumber,
                    employerName, employerAddress, initialDeposit);

            if (success) {
                showMessage("Account created successfully!", false);
                accountCreated = true;

                // Refresh the dashboard if available
                if (dashboardController != null) {
                    dashboardController.refreshAccounts();
                }

                // Show success dialog
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Account Created");
                alert.setHeaderText("Account Created Successfully");
                alert.setContentText("Account Number: " + accountNumber +
                        "\nType: " + accountType +
                        "\nInitial Deposit: BWP " + String.format("%.2f", initialDeposit));
                alert.showAndWait();

                // Close dialog
                if (dialogStage != null) {
                    dialogStage.close();
                }
            } else {
                showMessage("Failed to create account. Please try again.", true);
            }

        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for initial deposit.", true);
        } catch (Exception e) {
            showMessage("An error occurred: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleBack() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void showMessage(String message, boolean isError) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: " + (isError ? "#ff6b6b" : "#64ffda") + "; -fx-font-size: 11;");
            messageLabel.setVisible(true);
        }
    }

    private boolean validateInputs() {
        if (accountTypeCombo.getValue() == null) {
            showMessage("Please select an account type.", true);
            return false;
        }

        if (accountNumberField.getText().isEmpty()) {
            showMessage("Account number is required.", true);
            return false;
        }

        if (initialDepositField.getText().isEmpty()) {
            showMessage("Initial deposit is required.", true);
            return false;
        }

        try {
            double deposit = Double.parseDouble(initialDepositField.getText());
            if (deposit < 0) {
                showMessage("Initial deposit cannot be negative.", true);
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for initial deposit.", true);
            return false;
        }

        // Validate employer fields for Cheque and Investment accounts
        String accountType = accountTypeCombo.getValue();
        if (("Cheque".equals(accountType) || "Investment".equals(accountType))) {
            if (employerNameField.getText().isEmpty()) {
                showMessage("Employer name is required for " + accountType + " accounts.", true);
                return false;
            }
            if (employerAddressField.getText().isEmpty()) {
                showMessage("Employer address is required for " + accountType + " accounts.", true);
                return false;
            }
        }

        return true;
    }
}