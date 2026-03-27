package com.ristorante.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.ristorante.ui.model.RuoloDTO;
import com.ristorante.ui.model.UtenteDTO;
import com.ristorante.ui.service.UtenteService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UtentiView {

    private final UtenteService utenteService = new UtenteService();
    
    private final TableView<UtenteDTO> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<String> ruoloFilter = new ComboBox<>();

    private List<UtenteDTO> utentiCompleti = new ArrayList<>();
    
    private final ComboBox<String> statoFilter = new ComboBox<>();
    
    private final String loggedUsername;

    public VBox build() {
        Label title = new Label("Gestione Utenti");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Visualizza e gestisci gli utenti del sistema");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        
        Label subtitle2 = new Label("In verde utente loggato");
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
        topBar.getChildren().addAll(new VBox(4, title, subtitle, subtitle2), spacer, nuovoUtenteButton);

        HBox filtersBar = buildFiltersBar();

        configureUtentiTable();
        loadInitialData();

        nuovoUtenteButton.setOnAction(e -> showNuovoUtenteDialog());

        VBox tableCard = new VBox(table);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0.2, 0, 2);
        """);

        VBox container = new VBox(20, topBar, filtersBar, tableCard);
        container.setPadding(new Insets(4, 0, 0, 0));

        return container;
    }

    private void configureUtentiTable() {
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

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(UtenteDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }

                applyRowStyle(this, item, isHover());
            }

            {
                hoverProperty().addListener((obs, oldValue, isHovered) -> {
                    UtenteDTO item = getItem();
                    if (item != null && !isEmpty()) {
                        applyRowStyle(this, item, isHovered);
                    }
                });
            }
        });

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
        
        TableColumn<UtenteDTO, Boolean> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(new PropertyValueFactory<>("attivo"));
        colStato.setStyle("-fx-alignment: CENTER;");
        
        colStato.setCellFactory(column -> new TableCell<>() {
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
            protected void updateItem(Boolean attivo, boolean empty) {
                super.updateItem(attivo, empty);

                if (empty || attivo == null) {
                    setGraphic(null);
                    return;
                }

                if (attivo) {
                    badge.setText("ATTIVO");
                    badge.setStyle("""
                        -fx-background-color: #16a34a;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                } else {
                    badge.setText("DISATTIVO");
                    badge.setStyle("""
                        -fx-background-color: #6b7280;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                }

                setGraphic(wrapper);
            }
        });
        
        TableColumn<UtenteDTO, Void> colAzioni = new TableColumn<>("Azioni");
        colAzioni.setPrefWidth(220);
        colAzioni.setMinWidth(220);
        colAzioni.setMaxWidth(220);
        colAzioni.setResizable(false);
        colAzioni.setStyle("-fx-alignment: CENTER;");

        colUsername.prefWidthProperty().bind(table.widthProperty().multiply(0.24));
        colNome.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        colCognome.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        colRuolo.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        colStato.prefWidthProperty().bind(table.widthProperty().multiply(0.12));

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
                    case "ADMIN" -> badge.setStyle("-fx-background-color: #0f766e; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "CASSA" -> badge.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "SALA" -> badge.setStyle("-fx-background-color: #7c3aed; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "CUCINA" -> badge.setStyle("-fx-background-color: #ea580c; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "PIZZERIA" -> badge.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "RIDER" -> badge.setStyle("-fx-background-color: #0891b2; -fx-background-radius: 999; -fx-text-fill: white;");
                    default -> badge.setStyle("-fx-background-color: #6b7280; -fx-background-radius: 999; -fx-text-fill: white;");
                }

                setText(null);
                setGraphic(wrapper);
            }
        });
        
        colAzioni.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifica");
            private final Button disableButton = new Button("Disattiva");
            private final HBox actionsBox = new HBox(8, editButton, disableButton);

            {
                actionsBox.setAlignment(Pos.CENTER);

                editButton.setPrefHeight(32);
                editButton.setStyle("""
                    -fx-background-color: #2563eb;
                    -fx-text-fill: white;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    -fx-padding: 0 12 0 12;
                """);

                disableButton.setPrefHeight(32);
                disableButton.setStyle("""
                    -fx-background-color: #dc2626;
                    -fx-text-fill: white;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    -fx-padding: 0 12 0 12;
                """);

                editButton.setOnAction(event -> {
                    UtenteDTO utente = getTableView().getItems().get(getIndex());
                    showModificaUtenteDialog(utente);
                });

                disableButton.setOnAction(event -> {
                    UtenteDTO utente = getTableView().getItems().get(getIndex());
                    showDisattivaConferma(utente);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    UtenteDTO utente = getTableView().getItems().get(getIndex());
                    disableButton.setText(utente.isAttivo() ? "Disattiva" : "Riattiva");
                    disableButton.setStyle(utente.isAttivo()
                            ? """
                              -fx-background-color: #dc2626;
                              -fx-text-fill: white;
                              -fx-font-size: 12px;
                              -fx-font-weight: bold;
                              -fx-background-radius: 8;
                              -fx-cursor: hand;
                              -fx-padding: 0 12 0 12;
                              """
                            : """
                              -fx-background-color: #0f766e;
                              -fx-text-fill: white;
                              -fx-font-size: 12px;
                              -fx-font-weight: bold;
                              -fx-background-radius: 8;
                              -fx-cursor: hand;
                              -fx-padding: 0 12 0 12;
                              """
                    );
                    setGraphic(actionsBox);
                }
            }
        });

        table.getColumns().setAll( colUsername, colNome, colCognome, colRuolo, colStato, colAzioni);
    }

    private void refreshTable() {
    	refreshData();
    }

    private void showNuovoUtenteDialog() {
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
        ruoloCombo.getItems().addAll(utenteService.loadRuoli());
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

        VBox content = new VBox(18, new VBox(4, title, subtitle), form);
        content.setPadding(new Insets(24));
        content.setPrefWidth(420);

        dialogPane.setContent(content);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

        okButton.setText("Crea utente");
        cancelButton.setText("Annulla");

        okButton.setPrefWidth(140);
        cancelButton.setPrefWidth(120);

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

            boolean ok = utenteService.createUtente(username, password, nome, cognome, ruolo);

            if (!ok) {
                errorLabel.setText("Impossibile creare l'utente. Verifica i dati o il backend.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();

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
    
    private HBox buildFiltersBar() {
        styleTextField(searchField, "Cerca per username, nome o cognome");
        searchField.setPrefWidth(320);

        ruoloFilter.setPrefWidth(220);
        ruoloFilter.setPrefHeight(42);
        ruoloFilter.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);
        
        statoFilter.getItems().addAll("Tutti", "Attivi", "Disattivi");
        statoFilter.setValue("Tutti");
        statoFilter.setPrefHeight(42);
        statoFilter.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);

        Button aggiornaButton = new Button("Aggiorna");
        aggiornaButton.setPrefHeight(42);
        aggiornaButton.setStyle("""
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

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        ruoloFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        
        statoFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        aggiornaButton.setOnAction(e -> refreshData());

        HBox filtersBar = new HBox(12, searchField, ruoloFilter, statoFilter, aggiornaButton);
        filtersBar.setAlignment(Pos.CENTER_LEFT);

        return filtersBar;
    }
    
    private void loadInitialData() {
        utentiCompleti = new ArrayList<>(utenteService.loadUtenti());
        
        utentiCompleti.sort((u1, u2) -> {
            boolean firstIsLogged = u1.getUsername() != null && u1.getUsername().equalsIgnoreCase(loggedUsername);
            boolean secondIsLogged = u2.getUsername() != null && u2.getUsername().equalsIgnoreCase(loggedUsername);

            if (firstIsLogged && !secondIsLogged) return -1;
            if (!firstIsLogged && secondIsLogged) return 1;
            return 0;
        });

        ruoloFilter.getItems().clear();
        ruoloFilter.getItems().add("Tutti i ruoli");
        ruoloFilter.getItems().addAll(
                utenteService.loadRuoli()
                        .stream()
                        .map(RuoloDTO::getCodice)
                        .toList()
        );
        ruoloFilter.setValue("Tutti i ruoli");

        applyFilters();
    }
    
    private void refreshData() {
        utentiCompleti = new ArrayList<>(utenteService.loadUtenti());
        
        utentiCompleti.sort((u1, u2) -> {
            boolean firstIsLogged = u1.getUsername() != null && u1.getUsername().equalsIgnoreCase(loggedUsername);
            boolean secondIsLogged = u2.getUsername() != null && u2.getUsername().equalsIgnoreCase(loggedUsername);

            if (firstIsLogged && !secondIsLogged) return -1;
            if (!firstIsLogged && secondIsLogged) return 1;
            return 0;
        });
        
        applyFilters();
    }
    
    private void applyFilters() {
        String ricerca = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String ruoloSelezionato = ruoloFilter.getValue();
        
        String statoSelezionato = statoFilter.getValue();

        List<UtenteDTO> filtrati = utentiCompleti.stream()
                .filter(u -> {
                    boolean matchRicerca =
                            ricerca.isBlank()
                                    || containsIgnoreCase(u.getUsername(), ricerca)
                                    || containsIgnoreCase(u.getNome(), ricerca)
                                    || containsIgnoreCase(u.getCognome(), ricerca);

                    boolean matchRuolo =
                            ruoloSelezionato == null
                                    || ruoloSelezionato.equals("Tutti i ruoli")
                                    || ruoloSelezionato.equalsIgnoreCase(u.getRuolo());
                    
                    boolean matchStato =
                            statoSelezionato == null
                                    || statoSelezionato.equals("Tutti")
                                    || (statoSelezionato.equals("Attivi") && Boolean.TRUE.equals(u.isAttivo()))
                                    || (statoSelezionato.equals("Disattivi") && Boolean.FALSE.equals(u.isAttivo()));

                    return matchRicerca && matchRuolo && matchStato;
                })
                .toList();

        table.getItems().setAll(filtrati);
    }
    
    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }
    
    private void showModificaUtenteDialog(UtenteDTO utente) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica utente");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);

        TextField usernameField = new TextField(utente.getUsername());
        styleTextField(usernameField, "Inserisci username");

        TextField nomeField = new TextField(utente.getNome());
        styleTextField(nomeField, "Inserisci nome");

        TextField cognomeField = new TextField(utente.getCognome());
        styleTextField(cognomeField, "Inserisci cognome");

        ComboBox<RuoloDTO> ruoloCombo = new ComboBox<>();
        ruoloCombo.getItems().addAll(utenteService.loadRuoli());
        styleComboBox(ruoloCombo, "Seleziona ruolo");

        ruoloCombo.getItems().stream()
                .filter(r -> r.getId().equals(utente.getRuoloId()))
                .findFirst()
                .ifPresent(ruoloCombo::setValue);

        Label title = new Label("Modifica utente");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitle = new Label("Aggiorna i dati dell'utente selezionato");
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
                createFormLabel("Nome"), nomeField,
                createFormLabel("Cognome"), cognomeField,
                createFormLabel("Ruolo"), ruoloCombo,
                errorLabel
        );

        VBox content = new VBox(18, new VBox(4, title, subtitle), form);
        content.setPadding(new Insets(24));
        content.setPrefWidth(420);

        dialogPane.setContent(content);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

        okButton.setText("Salva modifiche");
        cancelButton.setText("Annulla");

        okButton.setPrefWidth(150);
        cancelButton.setPrefWidth(120);

        stylePrimaryButton(okButton);
        styleSecondaryButton(cancelButton);

        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String username = usernameField.getText();
            String nome = nomeField.getText();
            String cognome = cognomeField.getText();
            RuoloDTO ruolo = ruoloCombo.getValue();

            if (username == null || username.isBlank()
                    || nome == null || nome.isBlank()
                    || cognome == null || cognome.isBlank()
                    || ruolo == null) {

                errorLabel.setText("Compila tutti i campi prima di continuare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            boolean ok = utenteService.updateUtente(
                    utente.getId(),
                    username,
                    nome,
                    cognome,
                    ruolo,
                    utente.isAttivo()
            );

            if (!ok) {
                errorLabel.setText("Impossibile aggiornare l'utente.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();
        });

        dialog.showAndWait();
    }
    
    private void showDisattivaConferma(UtenteDTO utente) {
        String azione = utente.isAttivo() ? "disattivare" : "riattivare";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma");
        alert.setHeaderText(null);
        alert.setContentText("Vuoi davvero " + azione + " l'utente \"" + utente.getUsername() + "\"?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = utenteService.updateStatoUtente(utente.getId(), !utente.isAttivo());

                if (ok) {
                    refreshTable();
                } else {
                    Alert errore = new Alert(Alert.AlertType.ERROR);
                    errore.setTitle("Errore");
                    errore.setHeaderText(null);
                    errore.setContentText("Operazione non riuscita.");
                    errore.showAndWait();
                }
            }
        });
    }
    
    public UtentiView(String loggedUsername) {
        this.loggedUsername = loggedUsername;
    }
    
    private void applyRowStyle(TableRow<UtenteDTO> row, UtenteDTO item, boolean hovered) {
        boolean isLoggedUser = item.getUsername() != null
                && item.getUsername().equalsIgnoreCase(loggedUsername);

        if (hovered) {
            if (isLoggedUser) {
                row.setStyle("""
                    -fx-background-color: #ecfdf5;
                    -fx-border-color: transparent transparent transparent #10b981;
                    -fx-border-width: 0 0 0 4;
                """);
            } else {
                row.setStyle("""
                    -fx-background-color: #f8fafc;
                    -fx-border-color: transparent;
                """);
            }
        } else {
            if (isLoggedUser) {
                row.setStyle("""
                    -fx-background-color: #f0fdf4;
                    -fx-border-color: transparent transparent transparent #10b981;
                    -fx-border-width: 0 0 0 4;
                """);
            } else {
                row.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: transparent;
                """);
            }
        }
    }
    
}