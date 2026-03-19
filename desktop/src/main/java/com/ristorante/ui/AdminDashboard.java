package com.ristorante.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboard {

    public void show(Stage stage, String username) {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #111827;");

        Label logo = new Label("Ristorante Manager");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(24));
        contentArea.setStyle("-fx-background-color: #f3f4f6;");

        VBox menu = new VBox(10,
                createMenuButton("Dashboard", contentArea, username),
                createMenuButton("Utenti", contentArea, username),
                createMenuButton("Ruoli", contentArea, username),
                createMenuButton("Articoli", contentArea, username),
                createMenuButton("Tavoli", contentArea, username),
                createMenuButton("Ordini", contentArea, username),
                createMenuButton("Asporto", contentArea, username),
                createMenuButton("Domicilio", contentArea, username),
                createMenuButton("Incassi", contentArea, username),
                createMenuButton("Spese", contentArea, username),
                createMenuButton("Report", contentArea, username)
        );

        sidebar.getChildren().addAll(logo, menu);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        contentArea.getChildren().setAll(buildDashboard(username));

        Scene scene = new Scene(root, 1300, 800);
        stage.setTitle("Ristorante Manager - Dashboard Admin");
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text, VBox contentArea, String username) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-alignment: center-left;
            -fx-cursor: hand;
        """);

        button.setOnAction(e -> {
            switch (text) {
                case "Dashboard":
                    contentArea.getChildren().setAll(buildDashboard(username));
                    break;
                case "Utenti":
                    contentArea.getChildren().setAll(buildUtenti());
                    break;
                default:
                    contentArea.getChildren().setAll(buildPlaceholder(text));
                    break;
            }
        });

        return button;
    }

    private VBox createCard(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox card = new VBox(10, titleLabel, valueLabel);
        card.setPadding(new Insets(20));
        card.setPrefWidth(240);
        card.setPrefHeight(120);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 16;
            -fx-border-radius: 16;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0.2, 0, 2);
        """);
        return card;
    }

    private VBox buildDashboard(String username) {
        Label title = new Label("Dashboard Admin");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label userInfo = new Label("Benvenuto, " + username);
        userInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        HBox header = new HBox(title, new Region(), userInfo);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox cardsRow1 = new HBox(16,
                createCard("Tavoli occupati", "14"),
                createCard("Ordini aperti", "8"),
                createCard("Asporti attivi", "3")
        );

        HBox cardsRow2 = new HBox(16,
                createCard("Domicili attivi", "5"),
                createCard("Incasso oggi", "€ 1.245"),
                createCard("Spese oggi", "€ 180")
        );

        return new VBox(20, header, cardsRow1, cardsRow2);
    }

    private VBox buildUtenti() {
        Label title = new Label("Gestione Utenti");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label placeholder = new Label("Qui vedrai la lista utenti");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        return new VBox(20, title, placeholder);
    }

    private VBox buildPlaceholder(String sectionName) {
        Label title = new Label(sectionName);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label placeholder = new Label("Sezione in costruzione");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        return new VBox(20, title, placeholder);
    }
}