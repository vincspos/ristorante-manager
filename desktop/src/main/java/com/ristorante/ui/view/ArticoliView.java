package com.ristorante.ui.view;

import com.ristorante.ui.model.ArticoloDTO;
import com.ristorante.ui.service.ArticoloService;
import com.ristorante.ui.util.UiDialogs;
import com.ristorante.ui.model.CategoriaArticoloDTO;
import com.ristorante.ui.service.CategoriaArticoloService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ArticoliView {

    private final ArticoloService articoloService = new ArticoloService();
    private final CategoriaArticoloService categoriaService = new CategoriaArticoloService();

    private final TableView<ArticoloDTO> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<String> categoriaFilter = new ComboBox<>();
    private final ComboBox<String> statoFilter = new ComboBox<>();

    private List<ArticoloDTO> articoliCompleti = new ArrayList<>();

    public VBox build() {
        Label title = new Label("Gestione Articoli");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Visualizza, crea, modifica e attiva o disattiva gli articoli del menu");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Button nuovoArticoloButton = new Button("+ Nuovo articolo");
        nuovoArticoloButton.setPrefHeight(38);
        nuovoArticoloButton.setStyle("""
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
        topBar.getChildren().addAll(new VBox(4, title, subtitle), spacer, nuovoArticoloButton);

        HBox filtersBar = buildFiltersBar();

        configureArticoliTable();
        loadInitialData();

        nuovoArticoloButton.setOnAction(e -> showNuovoArticoloDialog());

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

    private void configureArticoliTable() {
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

        Label emptyLabel = new Label("Nessun articolo trovato");
        emptyLabel.setStyle("""
            -fx-text-fill: #6b7280;
            -fx-font-size: 14px;
            -fx-padding: 24 0 24 0;
        """);
        table.setPlaceholder(emptyLabel);

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ArticoloDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }

                applyRowStyle(this, isHover());
            }

            {
                hoverProperty().addListener((obs, oldValue, isHovered) -> {
                    if (getItem() != null && !isEmpty()) {
                        applyRowStyle(this, isHovered);
                    }
                });
            }
        });

        TableColumn<ArticoloDTO, String> colCodice = new TableColumn<>("Codice");
        colCodice.setCellValueFactory(new PropertyValueFactory<>("codice"));
        colCodice.setStyle("-fx-alignment: CENTER;");

        TableColumn<ArticoloDTO, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setStyle("-fx-alignment: CENTER;");

        TableColumn<ArticoloDTO, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCategoria.setStyle("-fx-alignment: CENTER;");

        TableColumn<ArticoloDTO, BigDecimal> colPrezzo = new TableColumn<>("Prezzo");
        colPrezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        colPrezzo.setStyle("-fx-alignment: CENTER;");
        colPrezzo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal prezzo, boolean empty) {
                super.updateItem(prezzo, empty);

                if (empty || prezzo == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText("€ " + prezzo.setScale(2, java.math.RoundingMode.HALF_UP));
            }
        });

        TableColumn<ArticoloDTO, Boolean> colStato = new TableColumn<>("Stato");
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

        TableColumn<ArticoloDTO, Void> colAzioni = new TableColumn<>("Azioni");
        colAzioni.setPrefWidth(220);
        colAzioni.setMinWidth(220);
        colAzioni.setMaxWidth(220);
        colAzioni.setResizable(false);
        colAzioni.setStyle("-fx-alignment: CENTER;");

        colCodice.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        colNome.prefWidthProperty().bind(table.widthProperty().multiply(0.28));
        colCategoria.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        colPrezzo.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        colStato.prefWidthProperty().bind(table.widthProperty().multiply(0.12));

        colCategoria.setCellFactory(column -> new TableCell<>() {
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
            protected void updateItem(String categoria, boolean empty) {
                super.updateItem(categoria, empty);

                if (empty || categoria == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                String categoriaUpper = categoria.trim().toUpperCase();
                badge.setText(categoriaUpper);

                switch (categoriaUpper) {
                    case "PIZZE" -> badge.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "BEVANDE" -> badge.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "DESSERT" -> badge.setStyle("-fx-background-color: #7c3aed; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "ANTIPASTI" -> badge.setStyle("-fx-background-color: #0891b2; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "PRIMI" -> badge.setStyle("-fx-background-color: #ea580c; -fx-background-radius: 999; -fx-text-fill: white;");
                    case "SECONDI" -> badge.setStyle("-fx-background-color: #0f766e; -fx-background-radius: 999; -fx-text-fill: white;");
                    default -> badge.setStyle("-fx-background-color: #6b7280; -fx-background-radius: 999; -fx-text-fill: white;");
                }

                setText(null);
                setGraphic(wrapper);
            }
        });

        colAzioni.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifica");
            private final Button toggleButton = new Button("Disattiva");
            private final HBox actionsBox = new HBox(8, editButton, toggleButton);

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

                toggleButton.setPrefHeight(32);

                editButton.setOnAction(event -> {
                    ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                    showModificaArticoloDialog(articolo);
                });

                toggleButton.setOnAction(event -> {
                    ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                    showCambioStatoConferma(articolo);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                ArticoloDTO articolo = getTableView().getItems().get(getIndex());

                if (articolo.isAttivo()) {
                    toggleButton.setText("Disattiva");
                    toggleButton.setStyle("""
                        -fx-background-color: #dc2626;
                        -fx-text-fill: white;
                        -fx-font-size: 12px;
                        -fx-font-weight: bold;
                        -fx-background-radius: 8;
                        -fx-cursor: hand;
                        -fx-padding: 0 12 0 12;
                    """);
                } else {
                    toggleButton.setText("Riattiva");
                    toggleButton.setStyle("""
                        -fx-background-color: #0f766e;
                        -fx-text-fill: white;
                        -fx-font-size: 12px;
                        -fx-font-weight: bold;
                        -fx-background-radius: 8;
                        -fx-cursor: hand;
                        -fx-padding: 0 12 0 12;
                    """);
                }

                setGraphic(actionsBox);
            }
        });

        table.getColumns().setAll(colCodice, colNome, colCategoria, colPrezzo, colStato, colAzioni);
    }

    private HBox buildFiltersBar() {
        styleTextField(searchField, "Cerca per codice, nome o descrizione");
        searchField.setPrefWidth(320);

        categoriaFilter.setPrefWidth(220);
        categoriaFilter.setPrefHeight(42);
        categoriaFilter.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);

        statoFilter.getItems().addAll("Tutti", "Attivi", "Disattivi");
        statoFilter.setValue("Tutti");
        statoFilter.setPrefHeight(42);
        statoFilter.setPrefWidth(180);
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
        categoriaFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        statoFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        aggiornaButton.setOnAction(e -> refreshData());

        HBox filtersBar = new HBox(12, searchField, categoriaFilter, statoFilter, aggiornaButton);
        filtersBar.setAlignment(Pos.CENTER_LEFT);

        return filtersBar;
    }

    private void loadInitialData() {
        articoliCompleti = new ArrayList<>(articoloService.loadArticoli());
        sortArticoliList();

        categoriaFilter.getItems().clear();
        categoriaFilter.getItems().add("Tutte le categorie");
        categoriaFilter.getItems().addAll(
                articoliCompleti.stream()
                        .map(ArticoloDTO::getCategoria)
                        .filter(c -> c != null && !c.isBlank())
                        .map(String::trim)
                        .distinct()
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList()
        );
        categoriaFilter.setValue("Tutte le categorie");

        applyFilters();
    }

    private void refreshData() {
        loadInitialData();
    }

    private void refreshTable() {
        refreshData();
    }

    private void applyFilters() {
        String ricerca = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String categoriaSelezionata = categoriaFilter.getValue();
        String statoSelezionato = statoFilter.getValue();

        List<ArticoloDTO> filtrati = articoliCompleti.stream()
                .filter(a -> {
                    boolean matchRicerca =
                            ricerca.isBlank()
                                    || containsIgnoreCase(a.getCodice(), ricerca)
                                    || containsIgnoreCase(a.getNome(), ricerca)
                                    || containsIgnoreCase(a.getDescrizione(), ricerca);

                    boolean matchCategoria =
                            categoriaSelezionata == null
                                    || categoriaSelezionata.equals("Tutte le categorie")
                                    || categoriaSelezionata.equalsIgnoreCase(a.getCategoria());

                    boolean matchStato =
                            statoSelezionato == null
                                    || statoSelezionato.equals("Tutti")
                                    || (statoSelezionato.equals("Attivi") && a.isAttivo())
                                    || (statoSelezionato.equals("Disattivi") && !a.isAttivo());

                    return matchRicerca && matchCategoria && matchStato;
                })
                .toList();

        table.getItems().setAll(filtrati);
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }

    private void showNuovoArticoloDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuovo articolo");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);

        TextField codiceField = new TextField();
        styleTextField(codiceField, "Inserisci codice articolo");

        TextField nomeField = new TextField();
        styleTextField(nomeField, "Inserisci nome articolo");

        TextField descrizioneField = new TextField();
        styleTextField(descrizioneField, "Inserisci descrizione");

        TextField prezzoField = new TextField();
        styleTextField(prezzoField, "Inserisci prezzo");
        
        ComboBox<Integer> ivaCombo = new ComboBox<>();
        ivaCombo.getItems().addAll(4, 10, 22);
        ivaCombo.setValue(10);
        ivaCombo.setPromptText("Seleziona IVA");
        ivaCombo.setPrefHeight(42);
        ivaCombo.setMaxWidth(Double.MAX_VALUE);
        ivaCombo.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);

        ComboBox<CategoriaArticoloDTO> categoriaCombo = new ComboBox<>();

        categoriaCombo.getItems().addAll(
                categoriaService.loadCategorie().stream()
                        .filter(CategoriaArticoloDTO::isAttivo)
                        .toList()
        );

        categoriaCombo.setPromptText("Seleziona categoria");
        categoriaCombo.setPrefHeight(42);
        categoriaCombo.setMaxWidth(Double.MAX_VALUE);
        categoriaCombo.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);

        Label title = new Label("Nuovo articolo");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitle = new Label("Inserisci i dati del nuovo articolo");
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
                createFormLabel("Codice"), codiceField,
                createFormLabel("Nome"), nomeField,
                createFormLabel("Descrizione"), descrizioneField,
                createFormLabel("Prezzo"), prezzoField,
                createFormLabel("IVA"), ivaCombo,
                createFormLabel("Categoria"), categoriaCombo,
                errorLabel
        );

        VBox content = new VBox(18, new VBox(4, title, subtitle), form);
        content.setPadding(new Insets(24));
        content.setPrefWidth(440);

        dialogPane.setContent(content);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

        okButton.setText("Crea articolo");
        cancelButton.setText("Annulla");

        okButton.setPrefWidth(140);
        cancelButton.setPrefWidth(120);

        stylePrimaryButton(okButton);
        styleSecondaryButton(cancelButton);

        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String codice = codiceField.getText();
            String nome = nomeField.getText();
            String descrizione = descrizioneField.getText();
            String prezzo = prezzoField.getText();
            Integer iva = ivaCombo.getValue();
            CategoriaArticoloDTO categoria = categoriaCombo.getValue();

            String codiceNormalizzato = codice != null ? codice.trim().toUpperCase() : "";
            String prezzoNormalizzato = normalizePrezzo(prezzo);

            if (codice == null || codice.isBlank()
                    || nome == null || nome.isBlank()
                    || prezzo == null || prezzo.isBlank()
            		|| categoria == null
                    || categoria == null ) {

                errorLabel.setText("Compila tutti i campi obbligatori prima di continuare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            if (!isPrezzoValido(prezzoNormalizzato)) {
                errorLabel.setText("Inserisci un prezzo valido, ad esempio 6.50");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            boolean ok = articoloService.createArticolo(
                    codiceNormalizzato,
                    nome.trim(),
                    descrizione != null ? descrizione.trim() : "",
                    prezzoNormalizzato,
                    categoria.getNome(),
                    iva
            );

            if (!ok) {
                errorLabel.setText("Impossibile creare l'articolo. Verifica i dati o il backend.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();
            dialog.close();

            UiDialogs.showSuccess(
                    "Successo",
                    "Articolo creato",
                    "L'articolo è stato creato correttamente."
            );
        });

        dialog.showAndWait();
    }

    private void showModificaArticoloDialog(ArticoloDTO articolo) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica articolo");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);

        TextField codiceField = new TextField(articolo.getCodice());
        styleTextField(codiceField, "Inserisci codice articolo");

        TextField nomeField = new TextField(articolo.getNome());
        styleTextField(nomeField, "Inserisci nome articolo");

        TextField descrizioneField = new TextField(articolo.getDescrizione());
        styleTextField(descrizioneField, "Inserisci descrizione");

        TextField prezzoField = new TextField(
                articolo.getPrezzo() != null
                        ? articolo.getPrezzo().setScale(2, java.math.RoundingMode.HALF_UP).toString()
                        : ""
        );
        styleTextField(prezzoField, "Inserisci prezzo");
        
        ComboBox<Integer> ivaCombo = new ComboBox<>();
        ivaCombo.getItems().addAll(4, 10, 22);
        ivaCombo.setValue(articolo.getIva() != null ? articolo.getIva() : 10);
        ivaCombo.setPromptText("Seleziona IVA");
        ivaCombo.setPrefHeight(42);
        ivaCombo.setMaxWidth(Double.MAX_VALUE);
        ivaCombo.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);

        ComboBox<CategoriaArticoloDTO> categoriaCombo = new ComboBox<>();

        List<CategoriaArticoloDTO> categorie = categoriaService.loadCategorie();

        // solo attive
        categoriaCombo.getItems().addAll(
                categorie.stream()
                        .filter(CategoriaArticoloDTO::isAttivo)
                        .toList()
        );

        // aggiungi anche quella attuale se disattiva
        categorie.stream()
                .filter(c -> c.getNome().equalsIgnoreCase(articolo.getCategoria()))
                .findFirst()
                .ifPresent(c -> {
                    if (!categoriaCombo.getItems().contains(c)) {
                        categoriaCombo.getItems().add(c);
                    }
                    categoriaCombo.setValue(c);
                });

        categoriaCombo.setPrefHeight(42);
        categoriaCombo.setMaxWidth(Double.MAX_VALUE);
        categoriaCombo.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);

        Label title = new Label("Modifica articolo");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitle = new Label("Aggiorna i dati dell'articolo selezionato");
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
                createFormLabel("Codice"), codiceField,
                createFormLabel("Nome"), nomeField,
                createFormLabel("Descrizione"), descrizioneField,
                createFormLabel("Prezzo"), prezzoField,
                createFormLabel("IVA"), ivaCombo,
                createFormLabel("Categoria"), categoriaCombo,
                errorLabel
        );

        VBox content = new VBox(18, new VBox(4, title, subtitle), form);
        content.setPadding(new Insets(24));
        content.setPrefWidth(440);

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
            String codice = codiceField.getText();
            String nome = nomeField.getText();
            String descrizione = descrizioneField.getText();
            String prezzo = prezzoField.getText();
            Integer iva = ivaCombo.getValue();
            CategoriaArticoloDTO categoria = categoriaCombo.getValue();

            String codiceNormalizzato = codice != null ? codice.trim().toUpperCase() : "";
            String prezzoNormalizzato = normalizePrezzo(prezzo);

            if (codice == null || codice.isBlank()
                    || nome == null || nome.isBlank()
                    || prezzo == null || prezzo.isBlank()
                    || categoria == null  || iva == null) {

                errorLabel.setText("Compila tutti i campi obbligatori prima di continuare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            if (!isPrezzoValido(prezzoNormalizzato)) {
                errorLabel.setText("Inserisci un prezzo valido, ad esempio 6.50");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            boolean ok = articoloService.updateArticolo(
                    articolo.getId(),
                    codiceNormalizzato,
                    nome.trim(),
                    descrizione != null ? descrizione.trim() : "",
                    prezzoNormalizzato,
                    categoria.getNome(),
                    iva,
                    articolo.isAttivo()
            );

            if (!ok) {
                errorLabel.setText("Impossibile aggiornare l'articolo.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();
            dialog.close();
            UiDialogs.showSuccess(
                    "Successo",
                    "Articolo aggiornato",
                    "L'articolo è stato aggiornato correttamente."
            );
        });

        dialog.showAndWait();
    }

    private void showCambioStatoConferma(ArticoloDTO articolo) {
    	String azione = articolo.isAttivo() ? "disattivare" : "riattivare";

    	boolean confermato = UiDialogs.showConfirm(
    	        "Conferma",
    	        "Cambio stato articolo",
    	        "Vuoi davvero " + azione + " l'articolo \"" + articolo.getNome() + "\"?"
    	);

    	if (confermato) {
    	    boolean ok = articoloService.updateStatoArticolo(articolo.getId(), !articolo.isAttivo());

    	    if (ok) {
    	        refreshTable();
    	    } else {
    	        UiDialogs.showError(
    	                "Errore",
    	                "Operazione non riuscita",
    	                "Non è stato possibile aggiornare lo stato dell'articolo."
    	        );
    	    }
    	}
    }

    private void sortArticoliList() {
        articoliCompleti.sort((a1, a2) -> {
            String nome1 = a1.getNome() != null ? a1.getNome().trim().toLowerCase() : "";
            String nome2 = a2.getNome() != null ? a2.getNome().trim().toLowerCase() : "";

            int nomeCompare = nome1.compareTo(nome2);
            if (nomeCompare != 0) {
                return nomeCompare;
            }

            String codice1 = a1.getCodice() != null ? a1.getCodice().trim().toLowerCase() : "";
            String codice2 = a2.getCodice() != null ? a2.getCodice().trim().toLowerCase() : "";

            return codice1.compareTo(codice2);
        });
    }

    private void applyRowStyle(TableRow<ArticoloDTO> row, boolean hovered) {
        if (hovered) {
            row.setStyle("""
                -fx-background-color: #f8fafc;
                -fx-border-color: transparent;
            """);
        } else {
            row.setStyle("""
                -fx-background-color: white;
                -fx-border-color: transparent;
            """);
        }
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

    private String normalizePrezzo(String prezzoInput) {
        if (prezzoInput == null) {
            return "";
        }
        return prezzoInput.trim().replace(",", ".");
    }

    private boolean isPrezzoValido(String prezzo) {
        try {
            BigDecimal value = new BigDecimal(prezzo);
            return value.compareTo(BigDecimal.ZERO) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}
