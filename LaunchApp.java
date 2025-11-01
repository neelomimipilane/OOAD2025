import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class LaunchApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load from resources folder
            Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setTitle("First National Bank - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading LoginScreen.fxml: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}