package com.ristorante.ui;

import com.ristorante.ui.model.ArticoloDTO;
import com.ristorante.ui.view.ArticoliView;
import com.ristorante.ui.view.CategorieArticoliView;
import com.ristorante.ui.view.DashboardHomeView;
import com.ristorante.ui.view.RuoliView;
import com.ristorante.ui.view.UtentiView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

    private VBox contentArea;
    private VBox articoliSubMenu;
    private boolean articoliExpanded = false;
    
    private void openContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    public void show(Stage stage, String username) {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #111827;");

        Label logo = new Label("Ristorante Manager");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(24));
        contentArea.setStyle("-fx-background-color: #f3f4f6;");

        VBox menu = new VBox(10);

        Button dashboardButton = createMenuButton("Dashboard", username);
        Button utentiButton = createMenuButton("Utenti", username);
        Button ruoliButton = createMenuButton("Ruoli", username);

        VBox articoliMenuGroup = buildArticoliMenuGroup();

        Button tavoliButton = createMenuButton("Tavoli", username);
        Button ordiniButton = createMenuButton("Ordini", username);
        Button asportoButton = createMenuButton("Asporto", username);
        Button domicilioButton = createMenuButton("Domicilio", username);
        Button incassiButton = createMenuButton("Incassi", username);
        Button speseButton = createMenuButton("Spese", username);
        Button reportButton = createMenuButton("Report", username);

        menu.getChildren().addAll(
                dashboardButton,
                utentiButton,
                ruoliButton,
                articoliMenuGroup,
                tavoliButton,
                ordiniButton,
                asportoButton,
                domicilioButton,
                incassiButton,
                speseButton,
                reportButton
        );

        if (!menuButtons.isEmpty()) {
            setActiveMenu(dashboardButton);
        }

        sidebar.getChildren().addAll(logo, menu);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        contentArea.getChildren().setAll(new DashboardHomeView().build(username));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/app.css").toExternalForm());

        stage.setTitle("Ristorante Manager - Dashboard Admin");
        stage.setScene(scene);
        stage.show();

        javafx.application.Platform.runLater(() -> {
            stage.setMaximized(true);

            // 🔥 forza dimensione reale schermo
            javafx.geometry.Rectangle2D screenBounds =
                    javafx.stage.Screen.getPrimary().getVisualBounds();

            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
        });
    }

    private VBox buildArticoliMenuGroup() {
        Button articoliButton = new Button("Articoli");
        articoliButton.setMaxWidth(Double.MAX_VALUE);
        articoliButton.setPrefHeight(40);
        setMenuButtonStyle(articoliButton, false);

        Button categorieArticoliButton = createSubMenuButton("Categorie articoli");
        Button listaArticoliButton = createSubMenuButton("Lista articoli");

        articoliSubMenu = new VBox(6, categorieArticoliButton, listaArticoliButton);
        articoliSubMenu.setPadding(new Insets(0, 0, 0, 10));
        articoliSubMenu.setVisible(false);
        articoliSubMenu.setManaged(false);

        articoliButton.setOnAction(e -> toggleArticoliSubMenu());

        VBox wrapper = new VBox(6, articoliButton, articoliSubMenu);

        menuButtons.add(articoliButton);
        return wrapper;
    }

    private void toggleArticoliSubMenu() {
        articoliExpanded = !articoliExpanded;
        articoliSubMenu.setVisible(articoliExpanded);
        articoliSubMenu.setManaged(articoliExpanded);
    }

    private Button createMenuButton(String text, String username) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        setMenuButtonStyle(button, false);

        button.setOnAction(e -> {
            setActiveMenu(button);

            switch (text) {
                case "Dashboard" -> contentArea.getChildren().setAll(new DashboardHomeView().build(username));
                case "Utenti" -> contentArea.getChildren().setAll(new UtentiView(username).build());
                case "Ruoli" -> contentArea.getChildren().setAll(new RuoliView().build());
                default -> contentArea.getChildren().setAll(buildPlaceholder(text));
            }
        });

        menuButtons.add(button);
        return button;
    }

    private Button createSubMenuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(34);
        setSubMenuButtonStyle(button, false);

        button.setOnAction(e -> {
            setActiveMenu(button);

            switch (text) {
                case "Categorie articoli" -> contentArea.getChildren().setAll(new CategorieArticoliView().build());
                case "Lista articoli" -> openContent(new ArticoliView(this::openContent).build());
                default -> contentArea.getChildren().setAll(buildPlaceholder(text));
            }
        });

        menuButtons.add(button);
        return button;
    }

    private void setActiveMenu(Button activeButton) {
        for (Button button : menuButtons) {
            boolean isSubMenu = button.getPrefHeight() == 34;

            if (isSubMenu) {
                setSubMenuButtonStyle(button, button == activeButton);
            } else {
                setMenuButtonStyle(button, button == activeButton);
            }
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

    private void setSubMenuButtonStyle(Button button, boolean active) {
        if (active) {
            button.setStyle("""
                -fx-background-color: #1f2937;
                -fx-text-fill: white;
                -fx-font-size: 13px;
                -fx-font-weight: bold;
                -fx-alignment: center-left;
                -fx-cursor: hand;
                -fx-background-radius: 8;
                -fx-padding: 0 14 0 24;
            """);
        } else {
            button.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #9ca3af;
                -fx-font-size: 13px;
                -fx-font-weight: normal;
                -fx-alignment: center-left;
                -fx-cursor: hand;
                -fx-background-radius: 8;
                -fx-padding: 0 14 0 24;
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