package com.ristorante.ui.view;

import com.ristorante.ui.common.ServiceResult;
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
import javafx.scene.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArticoliView {

    private final ArticoloService articoloService = new ArticoloService();
    private final CategoriaArticoloService categoriaService = new CategoriaArticoloService();

    private final TableView<ArticoloDTO> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<String> categoriaFilter = new ComboBox<>();
    private final ComboBox<String> statoFilter = new ComboBox<>();

    private List<ArticoloDTO> articoliCompleti = new ArrayList<>();
    private final java.util.Map<String, String> coloriCategorie = new java.util.HashMap<>();
    
    private final Consumer<Node> navigator;

    public ArticoliView(Consumer<Node> navigator) {
        this.navigator = navigator;
    }

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

        nuovoArticoloButton.setOnAction(e ->
		        navigator.accept(new SchedaArticoloView(() ->
		                navigator.accept(new ArticoliView(navigator).build())
		        ).build())
		);

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
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNome"));
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

                String colore = coloriCategorie.getOrDefault(categoriaUpper, "#6B7280");

                badge.setStyle("""
                    -fx-background-color: %s;
                    -fx-background-radius: 999;
                    -fx-text-fill: white;
                """.formatted(colore));

                setText(null);
                setGraphic(wrapper);
            }
        });

        colAzioni.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifica");
            private final Button toggleButton = new Button("Disattiva");
            private final Button deleteButton = new Button("Elimina");
            private final HBox actionsBox = new HBox(8, editButton, toggleButton, deleteButton);

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
                
                deleteButton.setPrefHeight(32);
                deleteButton.setStyle("""
                    -fx-background-color: #7f1d1d;
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
		                    navigator.accept(new SchedaArticoloView(articolo, () ->
		                    navigator.accept(new ArticoliView(navigator).build())
		            ).build());
                });

                toggleButton.setOnAction(event -> {
                    ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                    showCambioStatoConferma(articolo);
                });
                
                deleteButton.setOnAction(event -> {
                    ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                    showEliminaConferma(articolo);
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
    	
    	coloriCategorie.clear();
    	categoriaService.loadCategorie().forEach(c ->
    	        coloriCategorie.put(c.getNome().trim().toUpperCase(), c.getColore())
    	);
        sortArticoliList();

        categoriaFilter.getItems().clear();
        categoriaFilter.getItems().add("Tutte le categorie");
        categoriaFilter.getItems().addAll(
                articoliCompleti.stream()
                		.map(ArticoloDTO::getCategoriaNome)
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
                                    || categoriaSelezionata.equalsIgnoreCase(a.getCategoriaNome());

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
    
    private void showCambioStatoConferma(ArticoloDTO articolo) {
    	String azione = articolo.isAttivo() ? "disattivare" : "riattivare";

    	boolean confermato = UiDialogs.showConfirm(
    	        "Conferma",
    	        "Cambio stato articolo",
    	        "Vuoi davvero " + azione + " l'articolo \"" + articolo.getNome() + "\"?"
    	);

    	if (confermato) {
    		ServiceResult result = articoloService.updateStatoArticolo(articolo.getId(), !articolo.isAttivo());

    		if (result.isSuccess()) {
    		    refreshData();
    		} else {
    		    UiDialogs.showError(
    		            "Errore",
    		            "Operazione non riuscita",
    		            result.getMessage()
    		    );
    		}
    	}
    }
    
    private void showEliminaConferma(ArticoloDTO articolo) {
        boolean confermato = UiDialogs.showConfirm(
                "Conferma eliminazione",
                "Elimina articolo",
                "Vuoi davvero eliminare l'articolo \"" + articolo.getNome() + "\"?"
        );

        if (confermato) {
            ServiceResult result = articoloService.deleteArticolo(articolo.getId());

            if (result.isSuccess()) {
                refreshData();

                UiDialogs.showSuccess(
                        "Successo",
                        "Articolo eliminato",
                        "L'articolo è stato eliminato correttamente."
                );
            } else {
                UiDialogs.showError(
                        "Errore",
                        "Eliminazione non riuscita",
                        result.getMessage()
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
}
