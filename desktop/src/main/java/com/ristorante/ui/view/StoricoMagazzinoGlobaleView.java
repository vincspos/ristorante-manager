package com.ristorante.ui.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ristorante.ui.model.MovimentoMagazzinoDTO;
import com.ristorante.ui.service.ArticoloService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StoricoMagazzinoGlobaleView {

    private final ArticoloService articoloService = new ArticoloService();

    private final TableView<MovimentoMagazzinoDTO> table = new TableView<>();
    private List<MovimentoMagazzinoDTO> movimentiCompleti;

    private final TextField ricercaField = new TextField();
    private final DatePicker dataDaPicker = new DatePicker();
    private final DatePicker dataAPicker = new DatePicker();
    private final ComboBox<String> tipoFilter = new ComboBox<>();
    private final ComboBox<String> utenteFilter = new ComboBox<>();
    private final Label totaleLabel = new Label();

    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Storico magazzino globale");

        Label title = new Label("Storico magazzino globale");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Tutti i movimenti di magazzino registrati");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        totaleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        configureTable();

        movimentiCompleti = articoloService.loadMovimentiMagazzino();

        HBox filtersBar = buildFiltersBar();
        loadFilterValues();
        applyFilters();

        VBox tableCard = new VBox(table);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0.2, 0, 2);
        """);

        Button closeButton = new Button("Chiudi");
        closeButton.setPrefHeight(38);
        closeButton.setMinHeight(38);
        closeButton.setPrefWidth(92);
        closeButton.setMinWidth(92);
        closeButton.setStyle("""
            -fx-background-color: #0f766e;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            -fx-padding: 0 18 0 18;
        """);
        closeButton.setOnAction(e -> stage.close());

        HBox actions = new HBox(closeButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(0, 2, 0, 0));

        VBox root = new VBox(
                16,
                new VBox(4, title, subtitle, totaleLabel),
                filtersBar,
                tableCard,
                actions
        );
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color: #f3f4f6;");

        Scene scene = new Scene(root, 1050, 650);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void configureTable() {
        table.setPrefHeight(420);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(table, Priority.ALWAYS);

        table.setFixedCellSize(42);
        table.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 0;
            -fx-border-radius: 0;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 1;
            -fx-font-size: 13px;
            -fx-table-cell-border-color: #eef2f7;
        """);

        TableColumn<MovimentoMagazzinoDTO, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataMovimento"));
        colData.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String data, boolean empty) {
                super.updateItem(data, empty);

                if (empty || data == null || data.isBlank()) {
                    setText(null);
                    return;
                }

                try {
                    LocalDateTime dateTime = LocalDateTime.parse(data);
                    setText(dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                } catch (Exception e) {
                    setText(data);
                }
            }
        });

        TableColumn<MovimentoMagazzinoDTO, String> colArticolo = new TableColumn<>("Articolo");
        colArticolo.setCellValueFactory(new PropertyValueFactory<>("articoloNome"));

        TableColumn<MovimentoMagazzinoDTO, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setCellFactory(column -> new TableCell<>() {
            private final Label badge = new Label();
            private final HBox wrapper = new HBox(badge);

            {
                wrapper.setAlignment(Pos.CENTER);
                badge.setPadding(new Insets(4, 10, 4, 10));
            }

            @Override
            protected void updateItem(String tipo, boolean empty) {
                super.updateItem(tipo, empty);

                if (empty || tipo == null) {
                    setGraphic(null);
                    return;
                }

                switch (tipo) {
                    case "CARICO" -> {
                        badge.setText("CARICO");
                        badge.setStyle(badgeStyle("#16a34a"));
                    }
                    case "SCARICO_MANUALE" -> {
                        badge.setText("SCARICO");
                        badge.setStyle(badgeStyle("#dc2626"));
                    }
                    case "SCARICO_VENDITA" -> {
                        badge.setText("VENDITA");
                        badge.setStyle(badgeStyle("#f97316"));
                    }
                    case "RETTIFICA" -> {
                        badge.setText("RETTIFICA");
                        badge.setStyle(badgeStyle("#6b7280"));
                    }
                    default -> {
                        badge.setText(tipo);
                        badge.setStyle(badgeStyle("#6b7280"));
                    }
                }

                setText(null);
                setGraphic(wrapper);
            }
        });

        TableColumn<MovimentoMagazzinoDTO, Integer> colQuantita = new TableColumn<>("Q.tà");
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantita"));
        colQuantita.setStyle("-fx-alignment: CENTER;");

        TableColumn<MovimentoMagazzinoDTO, String> colUtente = new TableColumn<>("Utente");
        colUtente.setCellValueFactory(new PropertyValueFactory<>("utenteUsername"));

        TableColumn<MovimentoMagazzinoDTO, String> colNote = new TableColumn<>("Note");
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        colData.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
        colArticolo.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        colTipo.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        colQuantita.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        colUtente.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        colNote.prefWidthProperty().bind(table.widthProperty().multiply(0.30));

        table.getColumns().setAll(colData, colArticolo, colTipo, colQuantita, colUtente, colNote);

        Label emptyLabel = new Label("Nessun movimento trovato");
        emptyLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px;");
        table.setPlaceholder(emptyLabel);
    }

    private HBox buildFiltersBar() {
        styleTextField(ricercaField, "Cerca articolo o note");
        ricercaField.setPrefWidth(240);

        styleDatePicker(dataDaPicker, "Da");
        styleDatePicker(dataAPicker, "A");

        tipoFilter.getItems().addAll(
                "Tutti i tipi",
                "CARICO",
                "SCARICO_MANUALE",
                "RETTIFICA",
                "SCARICO_VENDITA"
        );
        tipoFilter.setValue("Tutti i tipi");
        styleComboBox(tipoFilter);
        tipoFilter.setPrefWidth(170);

        utenteFilter.setValue("Tutti gli utenti");
        styleComboBox(utenteFilter);
        utenteFilter.setPrefWidth(180);

        Button resetButton = new Button("Reset");
        resetButton.setPrefHeight(42);
        resetButton.setStyle("""
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

        ricercaField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dataDaPicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dataAPicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        tipoFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        utenteFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        resetButton.setOnAction(e -> {
            ricercaField.clear();
            dataDaPicker.setValue(null);
            dataAPicker.setValue(null);
            tipoFilter.setValue("Tutti i tipi");
            utenteFilter.setValue("Tutti gli utenti");
            applyFilters();
        });

        HBox filtersBar = new HBox(
                12,
                ricercaField,
                dataDaPicker,
                dataAPicker,
                tipoFilter,
                utenteFilter,
                resetButton
        );
        filtersBar.setAlignment(Pos.CENTER_LEFT);

        return filtersBar;
    }

    private void loadFilterValues() {
        utenteFilter.getItems().clear();
        utenteFilter.getItems().add("Tutti gli utenti");

        movimentiCompleti.stream()
                .map(MovimentoMagazzinoDTO::getUtenteUsername)
                .filter(u -> u != null && !u.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEach(utenteFilter.getItems()::add);

        utenteFilter.setValue("Tutti gli utenti");
    }

    private void applyFilters() {
        String ricerca = ricercaField.getText() == null
                ? ""
                : ricercaField.getText().trim().toLowerCase();

        LocalDate dataDa = dataDaPicker.getValue();
        LocalDate dataA = dataAPicker.getValue();

        String tipo = tipoFilter.getValue();
        String utente = utenteFilter.getValue();

        List<MovimentoMagazzinoDTO> filtrati = movimentiCompleti.stream()
                .filter(m -> {
                    boolean matchRicerca =
                            ricerca.isBlank()
                                    || containsIgnoreCase(m.getArticoloNome(), ricerca)
                                    || containsIgnoreCase(m.getNote(), ricerca);

                    boolean matchTipo =
                            tipo == null
                                    || tipo.equals("Tutti i tipi")
                                    || tipo.equals(m.getTipo());

                    boolean matchUtente =
                            utente == null
                                    || utente.equals("Tutti gli utenti")
                                    || utente.equalsIgnoreCase(m.getUtenteUsername());

                    boolean matchData = true;

                    try {
                        LocalDateTime dataMovimento = LocalDateTime.parse(m.getDataMovimento());
                        LocalDate giornoMovimento = dataMovimento.toLocalDate();

                        if (dataDa != null && giornoMovimento.isBefore(dataDa)) {
                            matchData = false;
                        }

                        if (dataA != null && giornoMovimento.isAfter(dataA)) {
                            matchData = false;
                        }
                    } catch (Exception ignored) {
                    }

                    return matchRicerca && matchTipo && matchUtente && matchData;
                })
                .toList();

        table.getItems().setAll(filtrati);
        totaleLabel.setText(filtrati.size() + " movimenti trovati");
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }

    private String badgeStyle(String color) {
        return """
            -fx-background-color: %s;
            -fx-background-radius: 999;
            -fx-text-fill: white;
            -fx-font-weight: bold;
        """.formatted(color);
    }

    private void styleTextField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setPrefHeight(42);
        field.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 0 12 0 12;
            -fx-font-size: 14px;
        """);
    }

    private void styleComboBox(ComboBox<String> combo) {
        combo.setPrefHeight(42);
        combo.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);
    }

    private void styleDatePicker(DatePicker picker, String prompt) {
        picker.setPromptText(prompt);
        picker.setPrefHeight(42);
        picker.setPrefWidth(140);
        picker.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);
    }
}