import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Transaction {
    private final StringProperty date;
    private final StringProperty description;
    private final DoubleProperty amount;
    private final StringProperty type;
    private final DoubleProperty balance;

    public Transaction(String date, String description, double amount, String type, double balance) {
        this.date = new SimpleStringProperty(date);
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleDoubleProperty(amount);
        this.type = new SimpleStringProperty(type);
        this.balance = new SimpleDoubleProperty(balance);
    }

    public StringProperty dateProperty() { return date; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty balanceProperty() { return balance; }
}