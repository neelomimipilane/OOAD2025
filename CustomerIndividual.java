public class CustomerIndividual extends Customer {
    private String dateOfBirth;
    private String IDNumber;

    public CustomerIndividual(String firstname, String surname, String address, String customerID,
                              String dateOfBirth, String IDNumber) {
        super(firstname, surname, address, customerID);
        this.dateOfBirth = dateOfBirth;
        this.IDNumber = IDNumber;
    }

    @Override
    public void displayCustomerInfo() {
        System.out.println("Individual Customer: " + firstname + " " + surname + ", ID: " + IDNumber);
    }
}
