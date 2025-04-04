package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;

/**
 * JavaFX App
 */
public class App extends Application {

    private TextField txtFirstName, txtLastName, txtEmail, txtMobile, txtRefEmail;
    private PasswordField txtPassword;

    @Override
    public void start(Stage primaryStage) {
        // Test database connection at startup
        if (!DatabaseUtil.testConnection()) {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                "Could not connect to the database. Please check your database configuration.");
            Platform.exit();
            return;
        }

        primaryStage.setTitle("Email Registration");

        // Create Labels and Input Fields
        Label lblFirstName = new Label("First Name:");
        txtFirstName = new TextField();

        Label lblLastName = new Label("Last Name:");
        txtLastName = new TextField();

        Label lblEmail = new Label("Email ID:");
        txtEmail = new TextField();

        Label lblPassword = new Label("Password:");
        txtPassword = new PasswordField(); // Hides characters

        Label lblMobile = new Label("Mobile Number:");
        txtMobile = new TextField();

        Label lblRefEmail = new Label("Reference Email ID:");
        txtRefEmail = new TextField();

        // Create Buttons
        Button btnRegister = new Button("Register");
        Button btnReset = new Button("Reset");
        Button btnCancel = new Button("Cancel");

        // Set button actions
        btnRegister.setOnAction(e -> registerUser());
        btnReset.setOnAction(e -> resetFields());
        btnCancel.setOnAction(e -> resetFields());

        // Layout using GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(lblFirstName, 0, 0);
        grid.add(txtFirstName, 1, 0);

        grid.add(lblLastName, 0, 1);
        grid.add(txtLastName, 1, 1);

        grid.add(lblEmail, 0, 2);
        grid.add(txtEmail, 1, 2);

        grid.add(lblPassword, 0, 3);
        grid.add(txtPassword, 1, 3);

        grid.add(lblMobile, 0, 4);
        grid.add(txtMobile, 1, 4);

        grid.add(lblRefEmail, 0, 5);
        grid.add(txtRefEmail, 1, 5);

        grid.add(btnRegister, 0, 6);
        grid.add(btnReset, 1, 6);
        grid.add(btnCancel, 1, 7);

        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }

    /**
     * Handles the registration process with improved SQL handling
     */
    private void registerUser() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String mobile = txtMobile.getText().trim();
        String refEmail = txtRefEmail.getText().trim();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Please fill in required fields: First Name, Last Name, Email, and Password.");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check if email already exists
            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        showAlert(Alert.AlertType.WARNING, "Registration Failed", 
                                "email already exists");
                        return;
                    }
                }
            }
            
            // Prepare SQL insert statement
            String sql = "INSERT INTO users (first_name, last_name, email, password, mobile, ref_email) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, firstName);
                pst.setString(2, lastName);
                pst.setString(3, email);
                pst.setString(4, password);
                pst.setString(5, mobile);
                pst.setString(6, refEmail);
                int rowsAffected = pst.executeUpdate();
                
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Registration Status", 
                            "successful registration");
                    resetFields();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Registration Status",
                            "Registration failed. Please try again.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                    "SQL Error: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Driver Error", 
                    "PostgreSQL JDBC driver not found: " + ex.getMessage());
        }
    }

    /**
     * Clears all input fields.
     */
    private void resetFields() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtMobile.clear();
        txtRefEmail.clear();
    }

    /**
     * Utility method to display an alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

}
