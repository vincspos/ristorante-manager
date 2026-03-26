package com.ristorante.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ristorante.ui.model.RuoloDTO;
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
	    table.setPrefHeight(520);
	    table.setFixedCellSize(46);
	    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
	    table.setStyle("""
	        -fx-background-color: white;
	        -fx-background-radius: 12;
	        -fx-border-radius: 12;
	        -fx-border-color: transparent;
	        -fx-font-size: 13px;
	        -fx-table-cell-border-color: #eef2f7;
	        -fx-padding: 0;
	    """);
	
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
	    colId.setResizable(false);
	    colId.setPrefWidth(70);
	    colId.setMinWidth(70);
	    colId.setMaxWidth(70);
	    colId.setStyle("-fx-alignment: CENTER;");
	
	    TableColumn<UtenteDTO, String> colUsername = new TableColumn<>("Username");
	    colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
	    colUsername.setStyle("-fx-alignment: CENTER;");
	
	    TableColumn<UtenteDTO, String> colNome = new TableColumn<>("Nome");
	    colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
	    colNome.setStyle("-fx-alignment: CENTER;");
	
	    TableColumn<UtenteDTO, String> colCognome = new TableColumn<>("Cognome");
	    colCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
	    colCognome.setStyle("-fx-alignment: CENTER;");
	
	    TableColumn<UtenteDTO, String> colRuolo = new TableColumn<>("Ruolo");
	    colRuolo.setCellValueFactory(new PropertyValueFactory<>("ruolo"));
	    colRuolo.setStyle("-fx-alignment: CENTER;");
	
	    // larghezze proporzionate
	    colUsername.prefWidthProperty().bind(table.widthProperty().multiply(0.28));
	    colNome.prefWidthProperty().bind(table.widthProperty().multiply(0.22));
	    colCognome.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
	    colRuolo.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
	
	    // badge ruolo
	    colRuolo.setCellFactory(column -> new TableCell<>() {
	        private final HBox wrapper = new HBox();
	        private final Label badge = new Label();
	
	        {
	            wrapper.setAlignment(Pos.CENTER);
	            badge.setPadding(new Insets(4, 10, 4, 10));
	            badge.setFont(Font.font("System", FontWeight.BOLD, 11));
	            badge.setTextFill(Color.WHITE);
	            wrapper.getChildren().add(badge);
	        }
	
	        @Override
	        protected void updateItem(String ruolo, boolean empty) {
	            super.updateItem(ruolo, empty);
	
	            if (empty || ruolo == null) {
	                setGraphic(null);
	                setText(null);
	                return;
	            }
	
	            badge.setText(ruolo);
	
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
	
	            setText(null);
	            setGraphic(wrapper);
	        }
	    });
	
	    table.getColumns().setAll(colId, colUsername, colNome, colCognome, colRuolo);
	    table.getItems().setAll(loadUtenti());
	
	    nuovoUtenteButton.setOnAction(e -> showNuovoUtenteDialog(table));
	
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
    
    private List<RuoloDTO> loadRuoli(){
    	List<RuoloDTO> lista = new ArrayList<>();
    	
    	try {
    		HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/ruoli"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            var jsonList = mapper.readTree(response.body());

            for (var node : jsonList) {
                lista.add(new RuoloDTO(
                        node.get("id").asLong(),
                        node.get("codice").asText(),
                        node.get("descrizione").asText()
                ));
            }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return lista;
    }
    
    private boolean createUtente(String username, String password, String nome, String cognome,  RuoloDTO ruolo) {
    	try {
    		String jsonInput = """
    	        {
    	          "username": "%s",
    	          "password": "%s",
    	          "ruolo": {
    	            "id": %d
    	          },
    	          "nome": "%s",
    	          "cognome": "%s",
    	          "attivo": true
    	        }
    	        """.formatted(username, password, ruolo.getId(), nome, cognome);

    	        HttpClient client = HttpClient.newHttpClient();

    	        HttpRequest request = HttpRequest.newBuilder()
    	                .uri(URI.create("http://localhost:8081/api/utenti"))
    	                .header("Content-Type", "application/json")
    	                .POST(HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
    	                .build();

    	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    	        return response.statusCode() == 200 || response.statusCode() == 201;

    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        return false;
    	    }
    }
    
    private void showNuovoUtenteDialog(TableView<UtenteDTO> table) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Nuovo utente");
    dialog.setHeaderText(null);

    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    dialogPane.setStyle("""
        -fx-background-color: #f9fafb;
        -fx-background-radius: 18;
        -fx-border-radius: 18;
    """);

    TextField usernameField = new TextField();
    styleTextField(usernameField, "Inserisci username");

    PasswordField passwordField = new PasswordField();
    styleTextField(passwordField, "Inserisci password");

    TextField nomeField = new TextField();
    styleTextField(nomeField, "Inserisci nome");

    TextField cognomeField = new TextField();
    styleTextField(cognomeField, "Inserisci cognome");

    ComboBox<RuoloDTO> ruoloCombo = new ComboBox<>();
    ruoloCombo.getItems().addAll(loadRuoli());
    styleComboBox(ruoloCombo, "Seleziona ruolo");

    Label title = new Label("Nuovo utente");
    title.setStyle("""
        -fx-font-size: 22px;
        -fx-font-weight: bold;
        -fx-text-fill: #1f2937;
    """);

    Label subtitle = new Label("Inserisci i dati del nuovo utente");
    subtitle.setStyle("""
        -fx-font-size: 13px;
        -fx-text-fill: #6b7280;
    """);

    Label errorLabel = new Label();
    errorLabel.setVisible(false);
    errorLabel.setManaged(false);
    errorLabel.setWrapText(true);
    errorLabel.setStyle("""
        -fx-text-fill: #dc2626;
        -fx-font-size: 13px;
        -fx-font-weight: bold;
    """);

    VBox form = new VBox(10,
            createFormLabel("Username"), usernameField,
            createFormLabel("Password"), passwordField,
            createFormLabel("Nome"), nomeField,
            createFormLabel("Cognome"), cognomeField,
            createFormLabel("Ruolo"), ruoloCombo,
            errorLabel
    );

    VBox content = new VBox(18,
            new VBox(4, title, subtitle),
            form
    );

    content.setPadding(new Insets(24));
    content.setPrefWidth(420);

    dialogPane.setContent(content);

    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
    
    okButton.setPrefWidth(140);
    cancelButton.setPrefWidth(120);

    okButton.setText("Crea utente");
    cancelButton.setText("Annulla");

    stylePrimaryButton(okButton);
    styleSecondaryButton(cancelButton);

    okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        RuoloDTO ruolo = ruoloCombo.getValue();

        if (username == null || username.isBlank()
                || password == null || password.isBlank()
                || nome == null || nome.isBlank()
                || cognome == null || cognome.isBlank()
                || ruolo == null) {

            errorLabel.setText("Compila tutti i campi prima di continuare.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            event.consume();
            return;
        }

        boolean ok = createUtente(username, password, nome, cognome, ruolo);

        if (!ok) {
            errorLabel.setText("Impossibile creare l'utente. Verifica i dati o il backend.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            event.consume();
            return;
        }

        table.getItems().setAll(loadUtenti());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText("Utente creato correttamente.");
        alert.showAndWait();
    });

    dialog.showAndWait();
}
    
    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle("""
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-text-fill: #374151;
        """);
        return label;
    }

    private void styleTextField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setPrefHeight(42);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 0 12 0 12;
            -fx-font-size: 14px;
        """);
    }

    private void styleComboBox(ComboBox<?> combo, String prompt) {
        combo.setPromptText(prompt);
        combo.setPrefHeight(42);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);
    }

    private void stylePrimaryButton(Button button) {
        button.setPrefHeight(42);
        button.setStyle("""
            -fx-background-color: #0f766e;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            -fx-padding: 0 18 0 18;
        """);
    }

    private void styleSecondaryButton(Button button) {
        button.setPrefHeight(42);
        button.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-text-fill: #374151;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            -fx-padding: 0 18 0 18;
        """);
    }
}