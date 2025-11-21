public class CustomerBusiness extends Customer {
    private String businessName;
    private String registrationNumber;
    private String businessAddress;
    private String email;

    public CustomerBusiness(String firstname, String surname, String address,
                            String customerID, String businessName, String registrationNumber,
                            String businessAddress, String email) {
        super(firstname, surname, address, customerID);
        this.businessName = businessName;
        this.registrationNumber = registrationNumber;
        this.businessAddress = businessAddress;
        this.email = email;
    }

    // Getters and setters
    public String getBusinessName() { return businessName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getBusinessAddress() { return businessAddress; }
    public String getEmail() { return email; }

    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public void displayCustomerInfo() {
        System.out.println("Business Customer: " + businessName);
        System.out.println("Owner: " + getFullName() + ", Email: " + email);
        System.out.println("Registration: " + registrationNumber);
        System.out.println("Accounts: " + getAccountCount());
    }

    /**
     * Update business-specific information
     */
    public boolean updateBusinessInfo(String currentPassword, String newBusinessName,
                                      String newRegistrationNumber, String newBusinessAddress) {
        if (!Main.verifyPassword(this.customerID, currentPassword)) {
            System.out.println("Password verification failed. Cannot update information.");
            return false;
        }

        this.businessName = newBusinessName;
        this.registrationNumber = newRegistrationNumber;
        this.businessAddress = newBusinessAddress;

        boolean success = FileManager.updateCustomer(this);
        if (success) {
            System.out.println("Your business information has been updated successfully.");
        }
        return success;
    }
}