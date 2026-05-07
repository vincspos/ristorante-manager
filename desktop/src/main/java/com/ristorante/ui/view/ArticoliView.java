package com.ristorante.ui.view;

import com.ristorante.ui.common.ServiceResult;
import com.ristorante.ui.model.ArticoloDTO;
import com.ristorante.ui.service.ArticoloService;
import com.ristorante.ui.util.UiDialogs;
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
import javafx.scene.shape.SVGPath;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
    private final ComboBox<String> magazzinoFilter = new ComboBox<>();

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
        
        TableColumn<ArticoloDTO, Integer> colQuantita = new TableColumn<>("Q.tà");
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantitaDisponibile"));
        colQuantita.setStyle("-fx-alignment: CENTER;");
        colQuantita.setCellFactory(column -> new TableCell<>() {

            private final Button minusButton = new Button("-");
            private final Label quantityLabel = new Label();
            private final Button plusButton = new Button("+");
            private final HBox box = new HBox(4, minusButton, quantityLabel, plusButton);
            private Timeline holdTimeline;
            private int accumulatedDelta = 0;
            private ArticoloDTO currentArticolo;

            {
                box.setAlignment(Pos.CENTER);

                styleSmallQuantityButton(minusButton, "#dc2626");
                styleSmallQuantityButton(plusButton, "#16a34a");
                
                minusButton.setTooltip(new Tooltip("Diminuisci di 1 (SHIFT = -10)"));
                plusButton.setTooltip(new Tooltip("Aumenta di 1 (SHIFT = +10)"));


                quantityLabel.setMinWidth(30);
                quantityLabel.setPrefWidth(30);
                quantityLabel.setAlignment(Pos.CENTER);
                quantityLabel.setStyle("""
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #111827;
                """);

                minusButton.setOnMousePressed(e -> {
                    ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                    int delta = e.isShiftDown() ? -10 : -1;

                    updateQuantitaLocale(articolo, delta);

                    holdTimeline = new Timeline(
                            new KeyFrame(Duration.millis(350), ev -> startHoldUpdate(delta))
                    );
                    holdTimeline.setCycleCount(1);
                    holdTimeline.play();
                });

                plusButton.setOnMousePressed(e -> {
                    ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                    int delta = e.isShiftDown() ? 10 : 1;

                    updateQuantitaLocale(articolo, delta);

                    holdTimeline = new Timeline(
                            new KeyFrame(Duration.millis(350), ev -> startHoldUpdate(delta))
                    );
                    holdTimeline.setCycleCount(1);
                    holdTimeline.play();
                });

                minusButton.setOnMouseReleased(e -> stopHoldUpdate());
                plusButton.setOnMouseReleased(e -> stopHoldUpdate());

                minusButton.setOnMouseExited(e -> stopHoldUpdate());
                plusButton.setOnMouseExited(e -> stopHoldUpdate());
            }

            @Override
            protected void updateItem(Integer quantita, boolean empty) {
                super.updateItem(quantita, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                ArticoloDTO articolo = getTableView().getItems().get(getIndex());

                if (!Boolean.TRUE.equals(articolo.getGestioneMagazzino())) {
                    setText("—");
                    setGraphic(null);
                    return;
                }

                setText(null);
                quantityLabel.setText(String.valueOf(articolo.getQuantitaDisponibile()));
                minusButton.setDisable(articolo.getQuantitaDisponibile() <= 0);
                setGraphic(box);
            }
            
            private void startHoldUpdate(int delta) {
                stopHoldUpdate();

                holdTimeline = new Timeline(
                        new KeyFrame(Duration.millis(180), e -> {
                            if (getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                                stopHoldUpdate();
                                return;
                            }

                            ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                            updateQuantitaLocale(articolo, delta);
                            quantityLabel.setText(String.valueOf(articolo.getQuantitaDisponibile()));
                            minusButton.setDisable(articolo.getQuantitaDisponibile() <= 0);
                        })
                );

                holdTimeline.setCycleCount(Timeline.INDEFINITE);
                holdTimeline.play();
            }

            private void stopHoldUpdate() {
                if (holdTimeline != null) {
                    holdTimeline.stop();
                    holdTimeline = null;
                }

                if (currentArticolo != null && accumulatedDelta != 0) {
                    ServiceResult result = articoloService.updateQuantitaArticolo(
                            currentArticolo.getId(),
                            accumulatedDelta
                    );

                    if (!result.isSuccess()) {
                        UiDialogs.showError(
                                "Errore",
                                "Quantità non aggiornata",
                                result.getMessage()
                        );

                        currentArticolo.setQuantitaDisponibile(
                                currentArticolo.getQuantitaDisponibile() - accumulatedDelta
                        );

                        quantityLabel.setText(String.valueOf(currentArticolo.getQuantitaDisponibile()));
                    }

                    accumulatedDelta = 0;
                    currentArticolo = null;
                }
            }
            
            private void updateQuantitaLocale(ArticoloDTO articolo, int delta) {
                if (articolo == null || !Boolean.TRUE.equals(articolo.getGestioneMagazzino())) {
                    return;
                }

                int nuovaQuantita = articolo.getQuantitaDisponibile() + delta;

                if (nuovaQuantita < 0) {
                    return;
                }

                articolo.setQuantitaDisponibile(nuovaQuantita);
                accumulatedDelta += delta;
                currentArticolo = articolo;

                quantityLabel.setText(String.valueOf(articolo.getQuantitaDisponibile()));
                minusButton.setDisable(articolo.getQuantitaDisponibile() <= 0);
            }
        });
        
        TableColumn<ArticoloDTO, String> colMagazzino = new TableColumn<>("Magazzino");
        colMagazzino.setCellValueFactory(new PropertyValueFactory<>("statoMagazzino"));
        colMagazzino.setStyle("-fx-alignment: CENTER;");
        colMagazzino.setCellFactory(column -> new TableCell<>() {
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
            protected void updateItem(String stato, boolean empty) {
                super.updateItem(stato, empty);

                if (empty || stato == null) {
                    setGraphic(null);
                    return;
                }

                switch (stato) {
	                case "NON_GESTITO" -> {
	                    badge.setText("NON GESTITO");
	                    badge.setStyle("-fx-background-color: #6b7280; -fx-background-radius: 999;");
	                }
                    case "ESAURITO" -> {
                        badge.setText("ESAURITO");
                        badge.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 999;");
                    }
                    case "IN_ESAURIMENTO" -> {
                        badge.setText("IN ESAURIMENTO");
                        badge.setStyle("-fx-background-color: #f59e0b; -fx-background-radius: 999;");
                    }
                    default -> {
                        badge.setText("DISPONIBILE");
                        badge.setStyle("-fx-background-color: #16a34a; -fx-background-radius: 999;");
                    }
                }

                setGraphic(wrapper);
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
        colAzioni.setPrefWidth(180);
        colAzioni.setMinWidth(180);
        colAzioni.setMaxWidth(180);
        colAzioni.setResizable(false);
        colAzioni.setStyle("-fx-alignment: CENTER;");

        colCodice.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        colNome.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        colCategoria.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        colPrezzo.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        colQuantita.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        colMagazzino.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        colStato.prefWidthProperty().bind(table.widthProperty().multiply(0.09));

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
        	private final Button editButton = new Button();
        	private final Button toggleButton = new Button();
        	private final Button storicoButton = new Button();
        	private final Button deleteButton = new Button();
        	private final HBox actionsBox = new HBox(6, editButton, toggleButton, storicoButton, deleteButton);

            {
                actionsBox.setAlignment(Pos.CENTER);
                actionsBox.setPadding(new Insets(0, 4, 0, 4));

                styleActionIconButton(editButton, "#2563eb", createEditIcon());
                styleActionIconButton(deleteButton, "#dc2626", createDeleteIcon());
                styleActionIconButton(storicoButton, "#7c4f2c", createHistoryIcon());
                
                storicoButton.setTooltip(new Tooltip("Storico magazzino"));
                editButton.setTooltip(new Tooltip("Modifica articolo"));
                deleteButton.setTooltip(new Tooltip("Elimina articolo"));
                
                storicoButton.setOnAction(event -> {
                    ArticoloDTO articolo = getTableView().getItems().get(getIndex());
                    // qui poi apriremo la finestra storico
                    UiDialogs.showInfo(
                            "Storico magazzino",
                            articolo.getNome(),
                            "Qui mostreremo i movimenti di magazzino dell'articolo."
                    );
                });

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
                    styleActionIconButton(toggleButton, "#f97316", createPauseIcon());
                    toggleButton.setTooltip(new Tooltip("Disattiva articolo"));
                } else {
                    styleActionIconButton(toggleButton, "#6b7280", createRefreshIcon());
                    toggleButton.setTooltip(new Tooltip("Riattiva articolo"));
                }

                setGraphic(actionsBox);
            }
        });

        table.getColumns().setAll(colCodice, colNome, colCategoria, colPrezzo,  colQuantita, colMagazzino, colStato, colAzioni);
    }

    private HBox buildFiltersBar() {
        styleTextField(searchField, "Cerca per codice, nome o descrizione");
        searchField.setPrefWidth(280);

        categoriaFilter.setPrefWidth(200);
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
        statoFilter.setPrefWidth(150);
        statoFilter.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-size: 14px;
        """);
        
        magazzinoFilter.getItems().addAll(
                "Tutto magazzino",
                "Disponibile",
                "In esaurimento",
                "Esaurito",
                "Non gestito"
        );
        magazzinoFilter.setValue("Tutto magazzino");
        magazzinoFilter.setPrefHeight(42);
        magazzinoFilter.setPrefWidth(180);
        magazzinoFilter.setStyle("""
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
        magazzinoFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        statoFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        aggiornaButton.setOnAction(e -> refreshData());

        HBox filtersBar = new HBox(12, searchField, categoriaFilter, statoFilter, magazzinoFilter, aggiornaButton);
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
        String magazzinoSelezionato = magazzinoFilter.getValue();

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
                    
                    boolean matchMagazzino =
                            magazzinoSelezionato == null
                                    || magazzinoSelezionato.equals("Tutto magazzino")
                                    || (magazzinoSelezionato.equals("Disponibile")
                                        && "DISPONIBILE".equals(a.getStatoMagazzino()))
                                    || (magazzinoSelezionato.equals("In esaurimento")
                                        && "IN_ESAURIMENTO".equals(a.getStatoMagazzino()))
                                    || (magazzinoSelezionato.equals("Esaurito")
                                        && "ESAURITO".equals(a.getStatoMagazzino()))
                                    || (magazzinoSelezionato.equals("Non gestito")
                                        && "NON_GESTITO".equals(a.getStatoMagazzino()));

                    return matchRicerca && matchCategoria && matchStato && matchMagazzino;
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
    		    
    		    UiDialogs.showSuccess(
    		            "Successo",
    		            "Stato aggiornato",
    		            "L'articolo è stato aggiornato correttamente."
    		        );
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
        ArticoloDTO articolo = row.getItem();

        if (articolo == null) {
            row.setStyle("");
            return;
        }
        
        if (!Boolean.TRUE.equals(articolo.getGestioneMagazzino())) {
            row.setStyle(hovered ? "-fx-background-color: #f8fafc;" : "-fx-background-color: white;");
            return;
        }

        if ("ESAURITO".equals(articolo.getStatoMagazzino())) {
            row.setStyle("-fx-background-color: #fef2f2;");
        } else if ("IN_ESAURIMENTO".equals(articolo.getStatoMagazzino())) {
            row.setStyle("-fx-background-color: #fffbeb;");
        } else if (hovered) {
            row.setStyle("-fx-background-color: #f8fafc;");
        } else {
            row.setStyle("-fx-background-color: white;");
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
    
    private void styleActionIconButton(Button button, String backgroundColor, SVGPath icon) {
        button.setText(null);
        button.setGraphic(icon);
        button.setPrefSize(30, 30);
        button.setMinSize(30, 30);
        button.setMaxSize(30, 30);

        button.setStyle("""
            -fx-background-color: %s;
            -fx-background-radius: 9;
            -fx-cursor: hand;
            -fx-padding: 0;
        """.formatted(backgroundColor));
        
        button.setOnMouseEntered(e -> button.setOpacity(0.85));
        button.setOnMouseExited(e -> button.setOpacity(1.0));
    }

    private SVGPath createIcon(String content) {
        SVGPath icon = new SVGPath();
        icon.setContent(content);
        icon.setFill(Color.WHITE);
        icon.setScaleX(0.72);
        icon.setScaleY(0.72);
        return icon;
    }

    private SVGPath createEditIcon() {
        return createIcon("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z M20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34a.9959.9959 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
    }

    private SVGPath createPauseIcon() {
        return createIcon("M6 5h4v14H6V5zm8 0h4v14h-4V5z");
    }

    private SVGPath createRefreshIcon() {
        return createIcon("M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z");
    }

    private SVGPath createDeleteIcon() {
        return createIcon("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12z M8 9h8v10H8V9z M15.5 4l-1-1h-5l-1 1H5v2h14V4z");
    }
    
    private SVGPath createHistoryIcon() {
        return createIcon("M13 3a9 9 0 1 0 8.95 10h-2a7 7 0 1 1-2.05-4.95L15 11h7V4l-2.65 2.65A8.96 8.96 0 0 0 13 3z M12 7h1.5v5l4 2-.75 1.25L12 13V7z");
    }

    private void styleSmallQuantityButton(Button button, String color) {
        button.setPrefSize(24, 24);
        button.setMinSize(24, 24);
        button.setMaxSize(24, 24);
        button.setStyle("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-background-radius: 7;
            -fx-cursor: hand;
            -fx-padding: 0;
        """.formatted(color));

        button.setOnMouseEntered(e -> button.setOpacity(0.85));
        button.setOnMouseExited(e -> button.setOpacity(1.0));
    }
    
    
}
