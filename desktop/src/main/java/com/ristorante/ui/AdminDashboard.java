package com.ristorante.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.http.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ristorante.ui.model.UtenteDTO;

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
        scene.getStylesheets().add(getClass().getResource("/style/app.css").toExternalForm());
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
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Visualizza e gestisci gli utenti del sistema");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Button nuovoUtenteButton = new Button("+ Nuovo utente");
        nuovoUtenteButton.setPrefHeight(38);
        nuovoUtenteButton.setStyle("""
            -fx-background-color: #0f766e;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            -fx-padding: 0 16 0 16;
        """);

        HBox topBar = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(new VBox(4, title, subtitle), spacer, nuovoUtenteButton);

        TableView<UtenteDTO> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(520);
        table.setFixedCellSize(42);
        table.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: transparent;
            -fx-font-size: 13px;
            -fx-table-cell-border-color: #eef2f7;
            -fx-padding: 6;
        """);

        // Hover righe
        table.setRowFactory(tv -> {
            TableRow<UtenteDTO> row = new TableRow<>();

            row.hoverProperty().addListener((obs, oldVal, isHovered) -> {
                if (!row.isEmpty()) {
                    if (isHovered) {
                        row.setStyle("-fx-background-color: #f8fafc;");
                    } else {
                        row.setStyle("-fx-background-color: white;");
                    }
                }
            });

            return row;
        });

        TableColumn<UtenteDTO, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(70);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<UtenteDTO, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<UtenteDTO, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<UtenteDTO, String> colCognome = new TableColumn<>("Cognome");
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));

        TableColumn<UtenteDTO, String> colRuolo = new TableColumn<>("Ruolo");
        colRuolo.setCellValueFactory(new PropertyValueFactory<>("ruolo"));

        // Badge ruolo colorato
        colRuolo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String ruolo, boolean empty) {
                super.updateItem(ruolo, empty);

                if (empty || ruolo == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(ruolo);
                badge.setPadding(new Insets(4, 10, 4, 10));
                badge.setFont(Font.font("System", FontWeight.BOLD, 11));
                badge.setTextFill(Color.WHITE);

                switch (ruolo) {
                    case "ADMIN" -> badge.setStyle("""
                        -fx-background-color: #0f766e;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                    case "CASSA" -> badge.setStyle("""
                        -fx-background-color: #2563eb;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                    case "SALA" -> badge.setStyle("""
                        -fx-background-color: #7c3aed;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                    case "CUCINA" -> badge.setStyle("""
                        -fx-background-color: #ea580c;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                    case "PIZZERIA" -> badge.setStyle("""
                        -fx-background-color: #dc2626;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                    case "RIDER" -> badge.setStyle("""
                        -fx-background-color: #0891b2;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                    default -> badge.setStyle("""
                        -fx-background-color: #6b7280;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                }

                setGraphic(badge);
                setText(null);
            }
        });

        table.getColumns().addAll(colId, colUsername, colNome, colCognome, colRuolo);
        table.getItems().addAll(loadUtenti());

        // Header più pulito: usiamo card esterna e colonna ben leggibile
        VBox tableCard = new VBox(table);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0.2, 0, 2);
        """);

        VBox container = new VBox(20, topBar, tableCard);
        container.setPadding(new Insets(4, 0, 0, 0));

        return container;
    }

    private VBox buildPlaceholder(String sectionName) {
        Label title = new Label(sectionName);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label placeholder = new Label("Sezione in costruzione");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        return new VBox(20, title, placeholder);
    }
    
    private List<UtenteDTO> loadUtenti() {
        List<UtenteDTO> lista = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/utenti"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            var jsonList = mapper.readTree(response.body());

            for (var node : jsonList) {
                lista.add(new UtenteDTO(
                        node.get("id").asLong(),
                        node.get("username").asText(),
                        node.get("nome").asText(),
                        node.get("cognome").asText(),
                        node.get("ruolo").get("codice").asText()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}