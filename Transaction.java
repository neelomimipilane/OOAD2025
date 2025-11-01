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

    // JavaFX Property getters (for TableView binding)
    public StringProperty dateProperty() { return date; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty balanceProperty() { return balance; }

    // Regular getter methods (for direct access)
    public String getDate() { return date.get(); }
    public String getDescription() { return description.get(); }
    public double getAmount() { return amount.get(); }
    public String getType() { return type.get(); }
    public double getBalance() { return balance.get(); }

    // Optional setter methods if you need them
    public void setDate(String date) { this.date.set(date); }
    public void setDescription(String description) { this.description.set(description); }
    public void setAmount(double amount) { this.amount.set(amount); }
    public void setType(String type) { this.type.set(type); }
    public void setBalance(double balance) { this.balance.set(balance); }
}