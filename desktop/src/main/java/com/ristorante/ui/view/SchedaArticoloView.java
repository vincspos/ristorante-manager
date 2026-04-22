package com.ristorante.ui.view;

import com.ristorante.ui.model.ArticoloDTO;
import com.ristorante.ui.model.CategoriaArticoloDTO;
import com.ristorante.ui.service.ArticoloService;
import com.ristorante.ui.service.CategoriaArticoloService;
import com.ristorante.ui.util.UiDialogs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class SchedaArticoloView {

    private final ArticoloService articoloService = new ArticoloService();
    private final CategoriaArticoloService categoriaService = new CategoriaArticoloService();

    private final ArticoloDTO articolo;
    private final Runnable onBack;
    private final boolean editMode;

    public SchedaArticoloView(Runnable onBack) {
        this.articolo = null;
        this.onBack = onBack;
        this.editMode = false;
    }

    public SchedaArticoloView(ArticoloDTO articolo, Runnable onBack) {
        this.articolo = articolo;
        this.onBack = onBack;
        this.editMode = true;
    }

    public VBox build() {
        Label title = new Label(editMode ? "Modifica articolo" : "Nuovo articolo");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label(editMode
                ? "Aggiorna i dati dell'articolo selezionato"
                : "Compila la scheda del nuovo articolo");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Button backButton = new Button("← Torna alla lista");
        backButton.setPrefHeight(38);
        backButton.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-text-fill: #374151;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-cursor: hand;
            -fx-padding: 0 16 0 16;
        """);
        backButton.setOnAction(e -> onBack.run());

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(new VBox(4, title, subtitle), spacer, backButton);

        TextField codiceField = new TextField();
        codiceField.setDisable(true);
        styleTextField(codiceField, "Inserisci codice articolo");
        codiceField.setPromptText("Generato automaticamente");

        TextField nomeField = new TextField();
        styleTextField(nomeField, "Inserisci nome articolo");

        ComboBox<CategoriaArticoloDTO> categoriaCombo = new ComboBox<>();
        categoriaCombo.setPromptText("Seleziona categoria");
        styleComboBox(categoriaCombo);

        TextArea descrizioneArea = new TextArea();
        descrizioneArea.setPromptText("Inserisci descrizione");
        descrizioneArea.setPrefRowCount(3);
        descrizioneArea.setWrapText(true);
        descrizioneArea.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #d1d5db;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 8 12 8 12;
            -fx-font-size: 14px;
        """);

        TextField prezzoField = new TextField();
        styleTextField(prezzoField, "Inserisci prezzo");

        ComboBox<Integer> ivaCombo = new ComboBox<>();
        ivaCombo.getItems().addAll(4, 10, 22);
        ivaCombo.setValue(10);
        ivaCombo.setPromptText("Seleziona IVA");
        styleComboBox(ivaCombo);

        CheckBox attivoCheck = new CheckBox("Articolo attivo");
        attivoCheck.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: #374151;
        """);
        attivoCheck.setSelected(true);

        Label errorLabel = new Label();
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setWrapText(true);
        errorLabel.setStyle("""
            -fx-text-fill: #dc2626;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
        """);

        List<CategoriaArticoloDTO> categorie = categoriaService.loadCategorie();

        categoriaCombo.getItems().addAll(
                categorie.stream()
                        .filter(CategoriaArticoloDTO::isAttivo)
                        .toList()
        );

        if (editMode && articolo != null) {
            codiceField.setText(articolo.getCodice());
            nomeField.setText(articolo.getNome());
            descrizioneArea.setText(articolo.getDescrizione());

            if (articolo.getPrezzo() != null) {
                prezzoField.setText(articolo.getPrezzo().toString());
            }

            if (articolo.getIva() != null) {
                ivaCombo.setValue(articolo.getIva());
            }

            attivoCheck.setSelected(articolo.isAttivo());

            categorie.stream()
		            .filter(c -> c.getId().equals(articolo.getCategoriaId()))
		            .findFirst()
		            .ifPresent(c -> {
		                if (!categoriaCombo.getItems().contains(c)) {
		                    categoriaCombo.getItems().add(c);
		                }
		                categoriaCombo.setValue(c);
            });
        }

        VBox datiPrincipaliCard = buildCard(
                "Dati principali",
                "Informazioni base dell'articolo",
                buildMainDataGrid(codiceField, nomeField, categoriaCombo, descrizioneArea)
        );

        VBox datiEconomiciCard = buildCard(
                "Dati economici",
                "Prezzo e aliquota IVA",
                buildEconomicGrid(prezzoField, ivaCombo)
        );

        VBox statoCard = buildCard(
                "Stato articolo",
                "Gestione disponibilità dell'articolo",
                new VBox(12, attivoCheck)
        );

        Button saveButton = new Button(editMode ? "Salva modifiche" : "Crea articolo");
        stylePrimaryButton(saveButton);
        saveButton.setPrefWidth(160);

        Button cancelButton = new Button("Annulla");
        styleSecondaryButton(cancelButton);
        cancelButton.setPrefWidth(120);
        cancelButton.setOnAction(e -> onBack.run());

        saveButton.setOnAction(e -> {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            
            String nome = nomeField.getText();
            String descrizione = descrizioneArea.getText();
            String prezzo = prezzoField.getText();
            CategoriaArticoloDTO categoria = categoriaCombo.getValue();
            Integer iva = ivaCombo.getValue();
            boolean attivo = attivoCheck.isSelected();

           
            String nomeNormalizzato = nome != null ? nome.trim() : "";
            String descrizioneNormalizzata = descrizione != null ? descrizione.trim() : "";
            String prezzoNormalizzato = normalizePrezzo(prezzo);
            BigDecimal prezzoValue = new BigDecimal(prezzoNormalizzato);

            if (nomeNormalizzato.isBlank()
                    || prezzoNormalizzato.isBlank()
                    || categoria == null
                    || iva == null) {
                showInlineError(errorLabel, "Compila tutti i campi obbligatori prima di continuare.");
                return;
            }

            if (!isPrezzoValido(prezzoNormalizzato)) {
                showInlineError(errorLabel, "Inserisci un prezzo valido, ad esempio 6.50");
                return;
            }

            boolean ok;
            if (editMode && articolo != null) {
            	ok = articoloService.updateArticolo(
            	        articolo.getId(),
            	        nomeNormalizzato,
            	        descrizioneNormalizzata,
            	        prezzoValue,
            	        categoria.getId(),
            	        iva,
            	        attivo
            	);
            } else {
            	ok = articoloService.createArticolo(
            	        nomeNormalizzato,
            	        descrizioneNormalizzata,
            	        prezzoValue,
            	        categoria.getId(),
            	        iva
            	);
            }

            if (!ok) {
                showInlineError(errorLabel, editMode
                        ? "Impossibile aggiornare l'articolo."
                        : "Impossibile creare l'articolo.");
                return;
            }

            UiDialogs.showSuccess(
                    "Successo",
                    editMode ? "Articolo aggiornato" : "Articolo creato",
                    editMode
                            ? "L'articolo è stato aggiornato correttamente."
                            : "L'articolo è stato creato correttamente."
            );

            onBack.run();
        });

        HBox actionsBar = new HBox(10, saveButton, cancelButton);
        actionsBar.setAlignment(Pos.CENTER_RIGHT);

        VBox container = new VBox(
                20,
                topBar,
                datiPrincipaliCard,
                datiEconomiciCard,
                statoCard,
                errorLabel,
                actionsBar
        );
        container.setPadding(new Insets(4, 0, 0, 0));

        return container;
    }

    private VBox buildMainDataGrid(TextField codiceField,
                                   TextField nomeField,
                                   ComboBox<CategoriaArticoloDTO> categoriaCombo,
                                   TextArea descrizioneArea) {

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(14);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        grid.getColumnConstraints().addAll(col1, col2);

        VBox codiceBox = new VBox(6, createFormLabel("Codice"), codiceField);
        VBox nomeBox = new VBox(6, createFormLabel("Nome *"), nomeField);
        VBox categoriaBox = new VBox(6, createFormLabel("Categoria *"), categoriaCombo);
        VBox descrizioneBox = new VBox(6, createFormLabel("Descrizione"), descrizioneArea);

        grid.add(codiceBox, 0, 0);
        grid.add(nomeBox, 1, 0);
        grid.add(categoriaBox, 0, 1);
        grid.add(descrizioneBox, 1, 1);

        return new VBox(grid);
    }

    private VBox buildEconomicGrid(TextField prezzoField, ComboBox<Integer> ivaCombo) {
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(14);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        grid.getColumnConstraints().addAll(col1, col2);

        VBox prezzoBox = new VBox(6, createFormLabel("Prezzo *"), prezzoField);
        VBox ivaBox = new VBox(6, createFormLabel("IVA *"), ivaCombo);

        grid.add(prezzoBox, 0, 0);
        grid.add(ivaBox, 1, 0);

        return new VBox(grid);
    }

    private VBox buildCard(String title, String subtitle, VBox content) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: #6b7280;
        """);

        VBox box = new VBox(16, new VBox(4, titleLabel, subtitleLabel), content);
        box.setPadding(new Insets(20));
        box.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0.2, 0, 2);
        """);

        return box;
    }

    private void showInlineError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
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