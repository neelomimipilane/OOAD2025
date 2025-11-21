public class CustomerIndividual extends Customer {
    private String dateOfBirth;
    private String idNumber;
    private String email;

    public CustomerIndividual(String firstname, String surname, String address,
                              String customerID, String dateOfBirth, String idNumber, String email) {
        super(firstname, surname, address, customerID);
        this.dateOfBirth = dateOfBirth;
        this.idNumber = idNumber;
        this.email = email;
    }

    // Getters and setters
    public String getDateOfBirth() { return dateOfBirth; }
    public String getIdNumber() { return idNumber; }
    public String getEmail() { return email; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public void displayCustomerInfo() {
        System.out.println("Individual Customer: " + getFullName());
        System.out.println("ID: " + customerID + ", Email: " + email);
        System.out.println("Accounts: " + getAccountCount());
    }

    /**
     * Update individual-specific information
     */
    public boolean updateIndividualInfo(String currentPassword, String newDateOfBirth, String newIdNumber) {
        if (!Main.verifyPassword(this.customerID, currentPassword)) {
            System.out.println("Password verification failed. Cannot update information.");
            return false;
        }

        this.dateOfBirth = newDateOfBirth;
        this.idNumber = newIdNumber;

        boolean success = FileManager.updateCustomer(this);
        if (success) {
            System.out.println("Your individual information has been updated successfully.");
        }
        return success;
    }
}