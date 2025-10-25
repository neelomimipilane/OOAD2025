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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getIdNumber() {
        return idNumber;
    }

    @Override
    public String toString() {
        return "Individual Customer: " + getFirstname() + " " + getSurname() + " (" + getCustomerID() + ")";
    }

    @Override
    public void displayCustomerInfo() {

    }
}