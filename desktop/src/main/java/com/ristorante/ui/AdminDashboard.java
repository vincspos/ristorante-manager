package com.ristorante.ui;

import com.ristorante.ui.view.DashboardHomeView;
import com.ristorante.ui.view.UtentiView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboard {

    private final List<Button> menuButtons = new ArrayList<>();

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

        if (!menuButtons.isEmpty()) {
            setActiveMenu(menuButtons.get(0));
        }

        sidebar.getChildren().addAll(logo, menu);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        contentArea.getChildren().setAll(new DashboardHomeView().build(username));

        Scene scene = new Scene(root, 1300, 800);
        scene.getStylesheets().add(getClass().getResource("/style/app.css").toExternalForm());

        stage.setTitle("Ristorante Manager - Dashboard Admin");
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text, VBox contentArea, String username) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        setMenuButtonStyle(button, false);

        button.setOnAction(e -> {
            setActiveMenu(button);

            switch (text) {
                case "Dashboard" -> contentArea.getChildren().setAll(new DashboardHomeView().build(username));
                case "Utenti" -> contentArea.getChildren().setAll(new UtentiView().build());
                default -> contentArea.getChildren().setAll(buildPlaceholder(text));
            }
        });

        menuButtons.add(button);
        return button;
    }

    private void setActiveMenu(Button activeButton) {
        for (Button button : menuButtons) {
            setMenuButtonStyle(button, button == activeButton);
        }
    }

    private void setMenuButtonStyle(Button button, boolean active) {
        if (active) {
            button.setStyle("""
                -fx-background-color: #1f2937;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-alignment: center-left;
                -fx-cursor: hand;
                -fx-background-radius: 10;
                -fx-padding: 0 14 0 14;
            """);
        } else {
            button.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #d1d5db;
                -fx-font-size: 14px;
                -fx-font-weight: normal;
                -fx-alignment: center-left;
                -fx-cursor: hand;
                -fx-background-radius: 10;
                -fx-padding: 0 14 0 14;
            """);
        }
    }

    private VBox buildPlaceholder(String sectionName) {
        Label title = new Label(sectionName);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label placeholder = new Label("Sezione in costruzione");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        VBox box = new VBox(20, title, placeholder);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }
}