package com.ristorante.ui.view;

import com.ristorante.ui.model.ArticoloDTO;
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

import java.util.List;

public class StoricoMagazzinoView {

    private final ArticoloService articoloService = new ArticoloService();

    public void show(ArticoloDTO articolo) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Storico magazzino - " + articolo.getNome());

        Label title = new Label("Storico magazzino");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label(articolo.getCodice() + " - " + articolo.getNome());
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        TableView<MovimentoMagazzinoDTO> table = new TableView<>();
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
        
        table.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Region header = (Region) table.lookup("TableHeaderRow");
            if (header != null) {
                header.setStyle("""
                    -fx-background-color: #f8fafc;
                    -fx-border-color: transparent transparent #e5e7eb transparent;
                """);
            }
        });
        
        VBox tableCard = new VBox(table);
        tableCard.setPadding(new Insets(18));
        tableCard.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0.2, 0, 2);
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
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(data);
                    setText(dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                } catch (Exception e) {
                    setText(data);
                }
            }
        });

        TableColumn<MovimentoMagazzinoDTO, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setCellFactory(column -> new TableCell<>() {
            private final Label badge = new Label();
            private final HBox wrapper = new HBox(badge);

            {
                wrapper.setAlignment(Pos.CENTER);
                badge.setPadding(new Insets(4, 10, 4, 10));
                badge.setStyle("-fx-background-radius: 999; -fx-text-fill: white; -fx-font-weight: bold;");
            }

            @Override
            protected void updateItem(String tipo, boolean empty) {
                super.updateItem(tipo, empty);

                if (empty || tipo == null) {
                    setGraphic(null);
                    return;
                }

                if ("CARICO".equals(tipo)) {
                    badge.setText("CARICO");
                    badge.setStyle("-fx-background-color: #16a34a; -fx-background-radius: 999; -fx-text-fill: white; -fx-font-weight: bold;");
                } else if ("SCARICO_MANUALE".equals(tipo)) {
                    badge.setText("SCARICO");
                    badge.setStyle("-fx-background-color: #dc2626; -fx-background-radius: 999; -fx-text-fill: white; -fx-font-weight: bold;");
                } else {
                    badge.setText(tipo);
                    badge.setStyle("-fx-background-color: #6b7280; -fx-background-radius: 999; -fx-text-fill: white; -fx-font-weight: bold;");
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

        table.getColumns().setAll(colData, colTipo, colQuantita, colUtente, colNote);

        List<MovimentoMagazzinoDTO> movimenti = articoloService.loadMovimentiArticolo(articolo.getId());
        table.getItems().setAll(movimenti);

        Label emptyLabel = new Label("Nessun movimento trovato");
        emptyLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px;");
        table.setPlaceholder(emptyLabel);

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
                new VBox(4, title, subtitle),
                tableCard,
                actions
        );
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color: #f3f4f6;");

        Scene scene = new Scene(root, 850, 560);
        stage.setScene(scene);
        stage.showAndWait();
    }
}