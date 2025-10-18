public class CustomerBusiness extends Customer {
    private String businessName;
    private String regNumber;
    private String businessAddress;

    public CustomerBusiness(String firstname, String surname, String address, String customerID,
                            String businessName, String regNumber, String businessAddress) {
        super(firstname, surname, address, customerID);
        this.businessName = businessName;
        this.regNumber = regNumber;
        this.businessAddress = businessAddress;
    }

    @Override
    public void displayCustomerInfo() {
        System.out.println("Business Customer: " + businessName + ", Reg#: " + regNumber);
    }
}
