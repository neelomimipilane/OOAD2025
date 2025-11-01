import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateAccountController {

    @FXML private Label titleLabel;
    @FXML private Label featuresLabel;
    @FXML private Label messageLabel;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField accountNumberField;
    @FXML private VBox dynamicFieldsContainer;

    // Dynamic fields
    private TextField initialDepositField;
    private TextField employerNameField;
    private TextField employerAddressField;

    private Customer customer;
    private DashboardController dashboardController;

    @FXML
    private void initialize() {
        // Setup account type options
        accountTypeCombo.getItems().addAll(
                "Savings Account",
                "Cheque Account",
                "Investment Account"
        );

        // Listen for account type changes
        accountTypeCombo.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateFormForAccountType(newValue)
        );

        // Set initial features text
        updateFeaturesText("Select an account type");

        // Generate a default account number
        generateDefaultAccountNumber();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            titleLabel.setText("Open New Account - " + customer.getFirstname() + " " + customer.getSurname());
            generateDefaultAccountNumber();
        }
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    private void generateDefaultAccountNumber() {
        if (customer != null) {
            // Generate account number based on customer ID and timestamp
            String baseNumber = customer.getCustomerID().toUpperCase();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String accountNumber = baseNumber + "-ACC-" + timestamp.substring(timestamp.length() - 4);
            accountNumberField.setText(accountNumber);
        }
    }

    private void updateFormForAccountType(String accountType) {
        // Clear previous dynamic fields
        dynamicFieldsContainer.getChildren().clear();
        messageLabel.setVisible(false);

        if (accountType == null) {
            updateFeaturesText("Select an account type");
            return;
        }

        // Determine customer type for interest rate display
        boolean isBusiness = customer instanceof CustomerBusiness;

        switch (accountType) {
            case "Savings Account":
                setupSavingsAccountFields();
                String savingsRate = isBusiness ? "0.05%" : "0.025%";
                updateFeaturesText(
                        "• Monthly Interest: " + savingsRate + "\n" +
                                "• Deposits Allowed\n" +
                                "• Withdrawals: Not Allowed\n" +
                                "• Transfers: Not Allowed\n" +
                                "• No Minimum Balance"
                );
                break;

            case "Cheque Account":
                setupChequeAccountFields();
                updateFeaturesText(
                        "• No Monthly Interest\n" +
                                "• Deposits Allowed\n" +
                                "• Withdrawals Allowed\n" +
                                "• Transfers Allowed\n" +
                                "• Employer Details Required\n" +
                                "• Ideal for Salary Payments"
                );
                break;

            case "Investment Account":
                setupInvestmentAccountFields();
                String investmentRate = isBusiness ? "5%" : "3%";
                updateFeaturesText(
                        "• Monthly Interest: " + investmentRate + "\n" +
                                "• Deposits Allowed\n" +
                                "• Withdrawals Allowed\n" +
                                "• Transfers Allowed\n" +
                                "• Minimum Deposit: P1000\n" +
                                "• High-Yield Investment"
                );
                break;
        }
    }

    private void setupSavingsAccountFields() {
        dynamicFieldsContainer.getChildren().add(
                createInfoLabel("No additional information required for Savings Account")
        );
    }

    private void setupChequeAccountFields() {
        VBox fieldsBox = new VBox(8);

        Label employerLabel = new Label("EMPLOYER INFORMATION *");
        employerLabel.setStyle("-fx-text-fill: #64ffda; -fx-font-size: 12; -fx-font-weight: bold;");

        employerNameField = new TextField();
        employerNameField.setPromptText("Employer Name");
        styleTextField(employerNameField);

        employerAddressField = new TextField();
        employerAddressField.setPromptText("Employer Address");
        styleTextField(employerAddressField);

        fieldsBox.getChildren().addAll(employerLabel, employerNameField, employerAddressField);
        dynamicFieldsContainer.getChildren().add(fieldsBox);
    }

    private void setupInvestmentAccountFields() {
        VBox fieldsBox = new VBox(8);

        Label depositLabel = new Label("INITIAL DEPOSIT *");
        depositLabel.setStyle("-fx-text-fill: #64ffda; -fx-font-size: 12; -fx-font-weight: bold;");

        initialDepositField = new TextField();
        initialDepositField.setPromptText("Minimum P1000");
        initialDepositField.setText("1000"); // Default value
        styleTextField(initialDepositField);

        fieldsBox.getChildren().addAll(depositLabel, initialDepositField);
        dynamicFieldsContainer.getChildren().add(fieldsBox);
    }

    private void styleTextField(TextField textField) {
        textField.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-prompt-text-fill: #888; -fx-border-color: #00d4ff; -fx-border-width: 0 0 1 0; -fx-background-radius: 5; -fx-pref-height: 35; -fx-pref-width: 300;");
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11; -fx-wrap-text: true;");
        label.setPrefWidth(350);
        return label;
    }

    private void updateFeaturesText(String text) {
        featuresLabel.setText(text);
    }

    @FXML
    private void handleCreateAccount() {
        try {
            // Validate inputs
            if (!validateForm()) {
                return;
            }

            String accountType = accountTypeCombo.getValue();
            String accountNumber = accountNumberField.getText().trim();

            // Create account based on type - USE MAIN.createAccount METHOD
            boolean success = false;

            switch (accountType) {
                case "Savings Account":
                    success = Main.createAccount(customer, "Savings", accountNumber, "", "", 0);
                    if (success) {
                        // Make initial deposit for savings account
                        Main.depositToAccount(customer, accountNumber, 100.00);
                    }
                    break;

                case "Cheque Account":
                    String empName = employerNameField.getText().trim();
                    String empAddress = employerAddressField.getText().trim();
                    success = Main.createAccount(customer, "Cheque", accountNumber, empName, empAddress, 0);
                    if (success) {
                        // Make initial deposit for cheque account
                        Main.depositToAccount(customer, accountNumber, 100.00);
                    }
                    break;

                case "Investment Account":
                    double initialDeposit = Double.parseDouble(initialDepositField.getText().trim());
                    success = Main.createAccount(customer, "Investment", accountNumber, "", "", initialDeposit);
                    break;
            }

            if (success) {
                showMessage("Account created successfully!", false);

                // REFRESH THE DASHBOARD
                if (dashboardController != null) {
                    dashboardController.refreshAccounts();
                }

                // Clear form for next account
                clearForm();
                generateDefaultAccountNumber();

            } else {
                showMessage("Failed to create account. You may already have this account type, invalid details, or the account number already exists.", true);
            }

        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for initial deposit", true);
        } catch (Exception e) {
            showMessage("Error creating account: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (accountTypeCombo.getValue() == null) {
            showMessage("Please select an account type", true);
            return false;
        }

        if (accountNumberField.getText().trim().isEmpty()) {
            showMessage("Please enter an account number", true);
            accountNumberField.requestFocus();
            return false;
        }

        String accountType = accountTypeCombo.getValue();

        // Validate specific fields based on account type
        if (accountType.equals("Cheque Account")) {
            if (employerNameField == null || employerNameField.getText().trim().isEmpty() ||
                    employerAddressField == null || employerAddressField.getText().trim().isEmpty()) {
                showMessage("Please fill all employer information fields", true);
                return false;
            }
        }

        if (accountType.equals("Investment Account")) {
            if (initialDepositField == null || initialDepositField.getText().trim().isEmpty()) {
                showMessage("Please enter initial deposit amount", true);
                return false;
            }
            try {
                double deposit = Double.parseDouble(initialDepositField.getText().trim());
                if (deposit < 1000) {
                    showMessage("Investment account requires minimum deposit of P1000", true);
                    return false;
                }
            } catch (NumberFormatException e) {
                showMessage("Please enter a valid number for initial deposit", true);
                return false;
            }
        }

        return true;
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ?
                "-fx-text-fill: #ff6b6b; -fx-font-size: 11; -fx-wrap-text: true;" :
                "-fx-text-fill: #64ffda; -fx-font-size: 11; -fx-wrap-text: true;"
        );
        messageLabel.setVisible(true);
    }

    private void clearForm() {
        accountTypeCombo.getSelectionModel().clearSelection();
        accountNumberField.clear();
        dynamicFieldsContainer.getChildren().clear();
        messageLabel.setVisible(false);
        updateFeaturesText("Select an account type to view features");
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}