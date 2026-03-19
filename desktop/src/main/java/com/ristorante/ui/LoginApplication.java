package com.ristorante.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApplication extends Application {

    @Override
    public void start(Stage stage) {
        Label appTitle = new Label("Ristorante Manager");
        appTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Accedi al gestionale");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Label userLabel = new Label("Username");
        userLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Inserisci username");
        usernameField.setPrefHeight(42);
        usernameField.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 0 12 0 12;
            -fx-font-size: 14px;
        """);

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Inserisci password");
        passwordField.setPrefHeight(42);
        passwordField.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 0 12 0 12;
            -fx-font-size: 14px;
        """);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px;");
        errorLabel.setVisible(false);

        Button loginButton = new Button("Accedi");
        loginButton.setPrefHeight(44);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("""
            -fx-background-color: #0f766e;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                errorLabel.setText("Inserisci username e password.");
                errorLabel.setVisible(true);
                return;
            }

            try {
                String jsonInput = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(username, password);

                java.net.URL url = new java.net.URL("http://localhost:8081/api/utenti/login");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int status = conn.getResponseCode();

                if (status == 200) {
                    errorLabel.setVisible(false);

                    StringBuilder response = new StringBuilder();
                    try (java.io.BufferedReader br = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line.trim());
                        }
                    }

                    errorLabel.setVisible(false);
                    new AdminDashboard().show(stage, username);

                } else if (status == 401) {
                    errorLabel.setText("Credenziali non valide.");
                    errorLabel.setVisible(true);
                } else {
                    errorLabel.setText("Errore del server.");
                    errorLabel.setVisible(true);
                }

                conn.disconnect();

            } catch (Exception e) {
                errorLabel.setText("Errore di connessione al server.");
                errorLabel.setVisible(true);
                e.printStackTrace();
            }
        });

        VBox formBox = new VBox(10,
                userLabel, usernameField,
                passLabel, passwordField,
                errorLabel, loginButton
        );
        formBox.setFillWidth(true);

        VBox card = new VBox(16, appTitle, subtitle, formBox);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(32));
        card.setMaxWidth(430);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 20, 0.2, 0, 4);
        """);

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(40));
        root.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #f8fafc, #e2e8f0);
        """);

        Scene scene = new Scene(root, 1100, 700);

        stage.setTitle("Ristorante Manager - Login");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
