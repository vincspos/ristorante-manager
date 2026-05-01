package com.ristorante.ui.view;

import com.ristorante.ui.common.ServiceResult;
import com.ristorante.ui.model.CategoriaArticoloDTO;
import com.ristorante.ui.service.CategoriaArticoloService;
import com.ristorante.ui.util.UiDialogs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategorieArticoliView {

    private final CategoriaArticoloService categoriaService = new CategoriaArticoloService();

    private final TableView<CategoriaArticoloDTO> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<String> statoFilter = new ComboBox<>();

    private List<CategoriaArticoloDTO> categorieComplete = new ArrayList<>();

    private static final Map<String, String> COLORI_PREDEFINITI = new LinkedHashMap<>();

    static {
        COLORI_PREDEFINITI.put("Rosso", "#DC2626");
        COLORI_PREDEFINITI.put("Blu", "#2563EB");
        COLORI_PREDEFINITI.put("Verde", "#16A34A");
        COLORI_PREDEFINITI.put("Arancione", "#EA580C");
        COLORI_PREDEFINITI.put("Viola", "#7C3AED");
        COLORI_PREDEFINITI.put("Turchese", "#0891B2");
        COLORI_PREDEFINITI.put("Grigio", "#6B7280");
    }

    public VBox build() {
        Label title = new Label("Categorie articoli");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Visualizza, crea, modifica e attiva o disattiva le categorie articoli");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Button nuovaCategoriaButton = new Button("+ Nuova categoria");
        nuovaCategoriaButton.setPrefHeight(38);
        nuovaCategoriaButton.setStyle("""
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
        topBar.getChildren().addAll(new VBox(4, title, subtitle), spacer, nuovaCategoriaButton);

        HBox filtersBar = buildFiltersBar();

        configureCategorieTable();
        loadInitialData();

        nuovaCategoriaButton.setOnAction(e -> showNuovaCategoriaDialog());

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

    private void configureCategorieTable() {
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

        Label emptyLabel = new Label("Nessuna categoria trovata");
        emptyLabel.setStyle("""
            -fx-text-fill: #6b7280;
            -fx-font-size: 14px;
            -fx-padding: 24 0 24 0;
        """);
        table.setPlaceholder(emptyLabel);

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(CategoriaArticoloDTO item, boolean empty) {
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

        TableColumn<CategoriaArticoloDTO, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<CategoriaArticoloDTO, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setStyle("-fx-alignment: CENTER;");

        TableColumn<CategoriaArticoloDTO, Boolean> colStato = new TableColumn<>("Stato");
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
                    badge.setText("ATTIVA");
                    badge.setStyle("""
                        -fx-background-color: #16a34a;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                } else {
                    badge.setText("DISATTIVA");
                    badge.setStyle("""
                        -fx-background-color: #6b7280;
                        -fx-background-radius: 999;
                        -fx-text-fill: white;
                    """);
                }

                setGraphic(wrapper);
            }
        });

        TableColumn<CategoriaArticoloDTO, Void> colAzioni = new TableColumn<>("Azioni");
        colAzioni.setPrefWidth(150);
        colAzioni.setMinWidth(150);
        colAzioni.setMaxWidth(150);
        colAzioni.setResizable(false);
        colAzioni.setStyle("-fx-alignment: CENTER;");

        colId.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        colNome.prefWidthProperty().bind(table.widthProperty().multiply(0.48));
        colStato.prefWidthProperty().bind(table.widthProperty().multiply(0.18));

        colNome.setCellFactory(column -> new TableCell<>() {
            private final HBox wrapper = new HBox();
            private final Label badge = new Label();

            {
                wrapper.setAlignment(Pos.CENTER);
                badge.setPadding(new Insets(4, 12, 4, 12));
                badge.setFont(Font.font("System", FontWeight.BOLD, 11));
                wrapper.getChildren().add(badge);
            }

            @Override
            protected void updateItem(String nome, boolean empty) {
                super.updateItem(nome, empty);

                if (empty || nome == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                CategoriaArticoloDTO categoria = getTableView().getItems().get(getIndex());

                badge.setText(nome);
                badge.setTextFill(Color.WHITE);
                badge.setStyle("""
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-background-radius: 999;
                """.formatted(safeColor(categoria.getColore())));

                setText(null);
                setGraphic(wrapper);
            }
        });

        colAzioni.setCellFactory(param -> new TableCell<>() {
        	private final Button editButton = new Button();
        	private final Button toggleButton = new Button();
        	private final Button deleteButton = new Button();
        	private final HBox actionsBox = new HBox(8, editButton, toggleButton, deleteButton);

            {
                actionsBox.setAlignment(Pos.CENTER);
                
                styleActionIconButton(editButton, "#2563eb", createEditIcon());
                styleActionIconButton(deleteButton, "#dc2626", createDeleteIcon());

                editButton.setTooltip(new Tooltip("Modifica categoria"));
                toggleButton.setTooltip(new Tooltip("Disattiva / riattiva categoria"));
                deleteButton.setTooltip(new Tooltip("Elimina categoria"));

                editButton.setOnAction(event -> {
                    CategoriaArticoloDTO categoria = getTableView().getItems().get(getIndex());
                    showModificaCategoriaDialog(categoria);
                });

                toggleButton.setOnAction(event -> {
                    CategoriaArticoloDTO categoria = getTableView().getItems().get(getIndex());
                    showCambioStatoConferma(categoria);
                });

                deleteButton.setOnAction(event -> {
                    CategoriaArticoloDTO categoria = getTableView().getItems().get(getIndex());
                    showEliminaConferma(categoria);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                CategoriaArticoloDTO categoria = getTableView().getItems().get(getIndex());

                if (categoria.isAttivo()) {
                    styleActionIconButton(toggleButton, "#f97316", createPauseIcon());
                    toggleButton.setTooltip(new Tooltip("Disattiva categoria"));
                } else {
                    styleActionIconButton(toggleButton, "#6b7280", createRefreshIcon());
                    toggleButton.setTooltip(new Tooltip("Riattiva categoria"));
                }

                setGraphic(actionsBox);
            }
        });

        table.getColumns().setAll(colId, colNome, colStato, colAzioni);
    }

    private HBox buildFiltersBar() {
        styleTextField(searchField, "Cerca per nome categoria");
        searchField.setPrefWidth(320);

        statoFilter.getItems().addAll("Tutti", "Attive", "Disattive");
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
        statoFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        aggiornaButton.setOnAction(e -> refreshData());

        HBox filtersBar = new HBox(12, searchField, statoFilter, aggiornaButton);
        filtersBar.setAlignment(Pos.CENTER_LEFT);

        return filtersBar;
    }

    private void loadInitialData() {
        categorieComplete = new ArrayList<>(categoriaService.loadCategorie());
        sortCategorieList();
        applyFilters();
    }

    private void refreshData() {
        categorieComplete = new ArrayList<>(categoriaService.loadCategorie());
        sortCategorieList();
        applyFilters();
    }

    private void refreshTable() {
        refreshData();
    }

    private void applyFilters() {
        String ricerca = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String statoSelezionato = statoFilter.getValue();

        List<CategoriaArticoloDTO> filtrate = categorieComplete.stream()
                .filter(c -> {
                    boolean matchRicerca =
                            ricerca.isBlank() || containsIgnoreCase(c.getNome(), ricerca);

                    boolean matchStato =
                            statoSelezionato == null
                                    || statoSelezionato.equals("Tutti")
                                    || (statoSelezionato.equals("Attive") && c.isAttivo())
                                    || (statoSelezionato.equals("Disattive") && !c.isAttivo());

                    return matchRicerca && matchStato;
                })
                .toList();

        table.getItems().setAll(filtrate);
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }

    private void showNuovaCategoriaDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuova categoria");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);

        TextField nomeField = new TextField();
        styleTextField(nomeField, "Inserisci nome categoria");

        ComboBox<String> coloreCombo = new ComboBox<>();
        coloreCombo.getItems().addAll(COLORI_PREDEFINITI.keySet());
        coloreCombo.setPromptText("Seleziona colore");
        styleComboBox(coloreCombo);

        Label title = new Label("Nuova categoria");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitle = new Label("Inserisci il nome e scegli il colore della nuova categoria");
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
                createFormLabel("Nome"), nomeField,
                createFormLabel("Colore"), coloreCombo,
                errorLabel
        );

        VBox content = new VBox(18, new VBox(4, title, subtitle), form);
        content.setPadding(new Insets(24));
        content.setPrefWidth(420);

        dialogPane.setContent(content);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

        okButton.setText("Crea categoria");
        cancelButton.setText("Annulla");

        okButton.setPrefWidth(150);
        cancelButton.setPrefWidth(120);

        stylePrimaryButton(okButton);
        styleSecondaryButton(cancelButton);

        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String nome = nomeField.getText();
            String nomeNormalizzato = nome != null ? nome.trim().toUpperCase() : "";
            String coloreLabel = coloreCombo.getValue();
            String coloreHex = coloreLabel != null ? COLORI_PREDEFINITI.get(coloreLabel) : null;

            if (nome == null || nome.isBlank() || coloreHex == null) {
                errorLabel.setText("Compila tutti i campi prima di continuare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            ServiceResult result = categoriaService.createCategoria(nomeNormalizzato, coloreHex);

            if (!result.isSuccess()) {
                errorLabel.setText("Impossibile creare la categoria. Verifica i dati o il backend.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();
            dialog.close();

            UiDialogs.showSuccess(
                    "Successo",
                    "Categoria creata",
                    "La categoria è stata creata correttamente."
            );
        });

        dialog.showAndWait();
    }

    private void showModificaCategoriaDialog(CategoriaArticoloDTO categoria) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica categoria");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);

        TextField nomeField = new TextField(categoria.getNome());
        styleTextField(nomeField, "Inserisci nome categoria");

        ComboBox<String> coloreCombo = new ComboBox<>();
        coloreCombo.getItems().addAll(COLORI_PREDEFINITI.keySet());
        styleComboBox(coloreCombo);
        coloreCombo.setValue(findColorLabelByHex(categoria.getColore()));

        Label title = new Label("Modifica categoria");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitle = new Label("Aggiorna il nome e il colore della categoria selezionata");
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
                createFormLabel("Nome"), nomeField,
                createFormLabel("Colore"), coloreCombo,
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
            String nome = nomeField.getText();
            String nomeNormalizzato = nome != null ? nome.trim().toUpperCase() : "";
            String coloreLabel = coloreCombo.getValue();
            String coloreHex = coloreLabel != null ? COLORI_PREDEFINITI.get(coloreLabel) : null;

            if (nome == null || nome.isBlank() || coloreHex == null) {
                errorLabel.setText("Compila tutti i campi prima di continuare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            ServiceResult result = categoriaService.updateCategoria(
                    categoria.getId(),
                    nomeNormalizzato,
                    coloreHex,
                    categoria.isAttivo()
            );

            if (!result.isSuccess())  {
                errorLabel.setText("Impossibile aggiornare la categoria.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();
            dialog.close();
            UiDialogs.showSuccess(
                    "Successo",
                    "Categoria aggiornata",
                    "La categoria è stata aggiornata correttamente."
            );
        });

        dialog.showAndWait();
    }

    private void showCambioStatoConferma(CategoriaArticoloDTO categoria) {
        String azione = categoria.isAttivo() ? "disattivare" : "riattivare";

        boolean confermato = UiDialogs.showConfirm(
                "Conferma",
                "Cambio stato categoria",
                "Vuoi davvero " + azione + " la categoria \"" + categoria.getNome() + "\"?"
        );

        if (confermato) {
        	ServiceResult result = categoriaService.updateStatoCategoria(
        	        categoria.getId(),
        	        !categoria.isAttivo()
        	);

        	if (result.isSuccess()) {
        	    refreshTable();
        	} else {
        	    UiDialogs.showError(
        	            "Errore",
        	            "Operazione non riuscita",
        	            result.getMessage()
        	    );
        	}
        }
    }

    private void showEliminaConferma(CategoriaArticoloDTO categoria) {
    	boolean confermato = UiDialogs.showConfirm(
    	        "Conferma eliminazione",
    	        "Elimina categoria",
    	        "Vuoi davvero eliminare la categoria \"" + categoria.getNome() + "\"?"
    	);

    	if (confermato) {
    		ServiceResult result = categoriaService.deleteCategoria(categoria.getId());

    		if (result.isSuccess()) {
    	        refreshTable();

    	        UiDialogs.showSuccess(
    	                "Successo",
    	                "Categoria eliminata",
    	                "La categoria è stata eliminata correttamente."
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

    private void sortCategorieList() {
        categorieComplete.sort((c1, c2) -> {
            String nome1 = c1.getNome() != null ? c1.getNome().trim().toLowerCase() : "";
            String nome2 = c2.getNome() != null ? c2.getNome().trim().toLowerCase() : "";

            int nomeCompare = nome1.compareTo(nome2);
            if (nomeCompare != 0) {
                return nomeCompare;
            }

            Long id1 = c1.getId() != null ? c1.getId() : 0L;
            Long id2 = c2.getId() != null ? c2.getId() : 0L;

            return id1.compareTo(id2);
        });
    }

    private void applyRowStyle(TableRow<CategoriaArticoloDTO> row, boolean hovered) {
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

    private void styleComboBox(ComboBox<?> combo) {
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

    private String safeColor(String color) {
        if (color == null || color.isBlank()) {
            return "#6B7280";
        }
        return color;
    }

    private String findColorLabelByHex(String hex) {
        if (hex == null) {
            return null;
        }

        return COLORI_PREDEFINITI.entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(hex))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    
    private void styleActionIconButton(Button button, String backgroundColor, SVGPath icon) {
        button.setText(null);
        button.setGraphic(icon);
        button.setPrefSize(34, 34);
        button.setMinSize(34, 34);
        button.setMaxSize(34, 34);

        button.setStyle("""
            -fx-background-color: %s;
            -fx-background-radius: 9;
            -fx-cursor: hand;
            -fx-padding: 0;
        """.formatted(backgroundColor));
        
        button.setOnMouseEntered(e ->
        button.setStyle(button.getStyle() + "-fx-opacity: 0.85;"));

        button.setOnMouseExited(e ->
        button.setStyle(button.getStyle().replace("-fx-opacity: 0.85;", "")));
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
}