package com.ristorante.ui.view;

import com.ristorante.ui.common.ServiceResult;
import com.ristorante.ui.model.RuoloDTO;
import com.ristorante.ui.service.RuoloService;
import com.ristorante.ui.util.UiDialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
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

import java.util.ArrayList;
import java.util.List;

public class RuoliView {

    private final RuoloService ruoloService = new RuoloService();

    private final TableView<RuoloDTO> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final ComboBox<String> statoFilter = new ComboBox<>();

    private List<RuoloDTO> ruoliCompleti = new ArrayList<>();

    public VBox build() {
        Label title = new Label("Gestione Ruoli");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Visualizza, crea, modifica e attiva o disattiva i ruoli del sistema");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Button nuovoRuoloButton = new Button("+ Nuovo ruolo");
        nuovoRuoloButton.setPrefHeight(38);
        nuovoRuoloButton.setStyle("""
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
        topBar.getChildren().addAll(new VBox(4, title, subtitle), spacer, nuovoRuoloButton);

        HBox filtersBar = buildFiltersBar();

        configureRuoliTable();
        loadInitialData();

        nuovoRuoloButton.setOnAction(e -> showNuovoRuoloDialog());

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

    private void configureRuoliTable() {
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
        
        Label emptyLabel = new Label("Nessun ruolo trovato");
        emptyLabel.setStyle("""
            -fx-text-fill: #6b7280;
            -fx-font-size: 14px;
            -fx-padding: 24 0 24 0;
        """);
        table.setPlaceholder(emptyLabel);

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(RuoloDTO item, boolean empty) {
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

        TableColumn<RuoloDTO, String> colCodice = new TableColumn<>("Codice");
        colCodice.setCellValueFactory(new PropertyValueFactory<>("codice"));
        colCodice.setStyle("-fx-alignment: CENTER;");

        TableColumn<RuoloDTO, String> colDescrizione = new TableColumn<>("Descrizione");
        colDescrizione.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        colDescrizione.setStyle("-fx-alignment: CENTER;");

        TableColumn<RuoloDTO, Boolean> colStato = new TableColumn<>("Stato");
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

        TableColumn<RuoloDTO, Void> colAzioni = new TableColumn<>("Azioni");
        colAzioni.setPrefWidth(300);
        colAzioni.setMinWidth(300);
        colAzioni.setMaxWidth(300);
        colAzioni.setResizable(false);
        colAzioni.setStyle("-fx-alignment: CENTER;");

        colCodice.prefWidthProperty().bind(table.widthProperty().multiply(0.28));
        colDescrizione.prefWidthProperty().bind(table.widthProperty().multiply(0.44));
        colStato.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        colCodice.setCellFactory(column -> new TableCell<>() {
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
            protected void updateItem(String codice, boolean empty) {
                super.updateItem(codice, empty);

                if (empty || codice == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                badge.setText(codice);

                switch (codice.toUpperCase()) {
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
                    RuoloDTO ruolo = getTableView().getItems().get(getIndex());
                    showModificaRuoloDialog(ruolo);
                });

                toggleButton.setOnAction(event -> {
                    RuoloDTO ruolo = getTableView().getItems().get(getIndex());
                    showCambioStatoConferma(ruolo);
                });
                
                deleteButton.setOnAction(event -> {
                    RuoloDTO ruolo = getTableView().getItems().get(getIndex());
                    showEliminaRuoloConferma(ruolo);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                RuoloDTO ruolo = getTableView().getItems().get(getIndex());
                
                boolean isAdminRole = ruolo.getCodice() != null
                        && ruolo.getCodice().equalsIgnoreCase("ADMIN");

                if (isAdminRole) {
                    HBox onlyEdit = new HBox(editButton);
                    onlyEdit.setAlignment(Pos.CENTER);
                    setGraphic(onlyEdit);
                    return;
                }

                if (ruolo.isAttivo()) {
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

        table.getColumns().setAll(colCodice, colDescrizione, colStato, colAzioni);
    }

    private HBox buildFiltersBar() {
        styleTextField(searchField, "Cerca per codice o descrizione");
        searchField.setPrefWidth(320);

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
        statoFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        aggiornaButton.setOnAction(e -> refreshData());

        HBox filtersBar = new HBox(12, searchField, statoFilter, aggiornaButton);
        filtersBar.setAlignment(Pos.CENTER_LEFT);

        return filtersBar;
    }

    private void loadInitialData() {
        ruoliCompleti = new ArrayList<>(ruoloService.loadRuoli());
        sortRuoliList();
        applyFilters();
    }

    private void refreshData() {
        ruoliCompleti = new ArrayList<>(ruoloService.loadRuoli());
        sortRuoliList();
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

        List<RuoloDTO> filtrati = ruoliCompleti.stream()
                .filter(r -> {
                    boolean matchRicerca =
                            ricerca.isBlank()
                                    || containsIgnoreCase(r.getCodice(), ricerca)
                                    || containsIgnoreCase(r.getDescrizione(), ricerca);

                    boolean matchStato =
                            statoSelezionato == null
                                    || statoSelezionato.equals("Tutti")
                                    || (statoSelezionato.equals("Attivi") && r.isAttivo())
                                    || (statoSelezionato.equals("Disattivi") && !r.isAttivo());

                    return matchRicerca && matchStato;
                })
                .toList();

        table.getItems().setAll(filtrati);
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }

    private void showNuovoRuoloDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuovo ruolo");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);

        TextField codiceField = new TextField();
        styleTextField(codiceField, "Inserisci codice ruolo");

        TextField descrizioneField = new TextField();
        styleTextField(descrizioneField, "Inserisci descrizione ruolo");

        Label title = new Label("Nuovo ruolo");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitle = new Label("Inserisci i dati del nuovo ruolo");
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
                createFormLabel("Descrizione"), descrizioneField,
                errorLabel
        );

        VBox content = new VBox(18, new VBox(4, title, subtitle), form);
        content.setPadding(new Insets(24));
        content.setPrefWidth(420);

        dialogPane.setContent(content);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

        okButton.setText("Crea ruolo");
        cancelButton.setText("Annulla");

        okButton.setPrefWidth(140);
        cancelButton.setPrefWidth(120);

        stylePrimaryButton(okButton);
        styleSecondaryButton(cancelButton);

        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String codice = codiceField.getText();
            String descrizione = descrizioneField.getText();
            
            String codiceNormalizzato = codice != null
                    ? codice.trim().toUpperCase()
                    : "";

            if (codice == null || codice.isBlank()
                    || descrizione == null || descrizione.isBlank()) {

                errorLabel.setText("Compila tutti i campi prima di continuare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            ServiceResult result = ruoloService.createRuolo(
                    codiceNormalizzato,
                    descrizione.trim()
            );

            if (!result.isSuccess()){
                errorLabel.setText("Impossibile creare il ruolo. Verifica i dati o il backend.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();
            dialog.close();

            UiDialogs.showSuccess(
                    "Successo",
                    "Ruolo creato",
                    "Il ruolo è stato creato correttamente."
            );
        });

        dialog.showAndWait();
    }

    private void showModificaRuoloDialog(RuoloDTO ruolo) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica ruolo");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);

        TextField codiceField = new TextField(ruolo.getCodice());
        styleTextField(codiceField, "Inserisci codice ruolo");

        TextField descrizioneField = new TextField(ruolo.getDescrizione());
        styleTextField(descrizioneField, "Inserisci descrizione ruolo");

        Label title = new Label("Modifica ruolo");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitle = new Label("Aggiorna i dati del ruolo selezionato");
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
                createFormLabel("Descrizione"), descrizioneField,
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
            String codice = codiceField.getText();
            String descrizione = descrizioneField.getText();
            
            String codiceNormalizzato = codice != null
                    ? codice.trim().toUpperCase()
                    : "";

            if (codice == null || codice.isBlank()
                    || descrizione == null || descrizione.isBlank()) {

                errorLabel.setText("Compila tutti i campi prima di continuare.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            ServiceResult result =  ruoloService.updateRuolo(
                    ruolo.getId(),
                    codiceNormalizzato,
                    descrizione.trim(),
                    ruolo.isAttivo()
            );

            if (!result.isSuccess()) {
                errorLabel.setText("Impossibile aggiornare il ruolo.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }

            refreshTable();
            dialog.close();
            UiDialogs.showSuccess(
                    "Successo",
                    "Ruolo aggiornato",
                    "Il ruolo è stato aggiornato correttamente."
            );
        });

        dialog.showAndWait();
    }

    private void showCambioStatoConferma(RuoloDTO ruolo) {
    	String azione = ruolo.isAttivo() ? "disattivare" : "riattivare";

    	boolean confermato = UiDialogs.showConfirm(
    	        "Conferma",
    	        "Cambio stato ruolo",
    	        "Vuoi davvero " + azione + " il ruolo \"" + ruolo.getCodice() + "\"?"
    	);

    	if (confermato) {
    		ServiceResult result = ruoloService.updateStatoRuolo(ruolo.getId(), !ruolo.isAttivo());

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
    
    private void showEliminaRuoloConferma(RuoloDTO ruolo) {
        boolean confermato = UiDialogs.showConfirm(
                "Conferma eliminazione",
                "Elimina ruolo",
                "Vuoi davvero eliminare il ruolo \"" + ruolo.getCodice() + "\"?"
        );

        if (confermato) {
        	ServiceResult result = ruoloService.deleteRuolo(ruolo.getId());

        	if (result.isSuccess()) {
                refreshTable();

                UiDialogs.showSuccess(
                        "Successo",
                        "Ruolo eliminato",
                        "Il ruolo è stato eliminato correttamente."
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

    private void sortRuoliList() {
        ruoliCompleti.sort((r1, r2) -> {
            String codice1 = r1.getCodice() != null ? r1.getCodice().trim().toLowerCase() : "";
            String codice2 = r2.getCodice() != null ? r2.getCodice().trim().toLowerCase() : "";

            int codiceCompare = codice1.compareTo(codice2);
            if (codiceCompare != 0) {
                return codiceCompare;
            }

            String descrizione1 = r1.getDescrizione() != null ? r1.getDescrizione().trim().toLowerCase() : "";
            String descrizione2 = r2.getDescrizione() != null ? r2.getDescrizione().trim().toLowerCase() : "";

            return descrizione1.compareTo(descrizione2);
        });
    }

    private void applyRowStyle(TableRow<RuoloDTO> row, boolean hovered) {
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
}