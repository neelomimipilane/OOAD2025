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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    @Override
    public String toString() {
        return "Business Customer: " + businessName + " (" + getCustomerID() + ")";
    }

    @Override
    public void displayCustomerInfo() {

    }
}