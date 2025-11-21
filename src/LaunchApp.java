import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LaunchApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("=== BANKING SYSTEM STARTING ===");

            // Initialize file system first
            System.out.println("Initializing file system...");
            FileManager.initializeFiles();

            // Check if file system is ready
            if (isFileSystemReady()) {
                System.out.println("File system is ready, loading application...");

                // Load and display the login screen
                Parent root = FXMLLoader.load(getClass().getResource("/LoginScreen.fxml"));
                Scene scene = new Scene(root);

                primaryStage.setTitle("Banking System");
                primaryStage.setScene(scene);
                // REMOVED: primaryStage.setResizable(false); - Now the window is resizable
                primaryStage.setMinWidth(800);  // Set minimum window size
                primaryStage.setMinHeight(600);

                primaryStage.show();

                System.out.println("=== BANKING SYSTEM STARTED SUCCESSFULLY ===");
            } else {
                showErrorScreen(primaryStage, "File System Error",
                        "Cannot initialize file system. Please check file permissions and try again.");
            }

        } catch (Exception e) {
            System.out.println("Fatal error during application startup: " + e.getMessage());
            e.printStackTrace();
            showErrorScreen(primaryStage, "Startup Error",
                    "Failed to start the application: " + e.getMessage());
        }
    }

    private boolean isFileSystemReady() {
        try {
            // Check if we can create/access the necessary files
            FileManager.initializeFiles();

            // Try to load customers (this will create the file if it doesn't exist)
            FileManager.loadAllCustomers();

            // Try to load accounts
            FileManager.loadAllAccounts(FileManager.loadAllCustomers());

            System.out.println("File system initialized successfully");
            return true;
        } catch (Exception e) {
            System.out.println("File system readiness check failed: " + e.getMessage());
            return false;
        }
    }

    private void showErrorScreen(Stage primaryStage, String title, String message) {
        try {
            // Create a simple error scene
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(message);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-padding: 20px;");

            javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
            root.getChildren().add(errorLabel);

            Scene scene = new Scene(root, 600, 400);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("Error screen displayed: " + message);
        } catch (Exception e) {
            System.out.println("Could not display error screen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Add shutdown hook for cleanup if needed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
            // Add any file cleanup here if needed
        }));

        try {
            launch(args);
        } catch (Exception e) {
            System.out.println("Application launch failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}