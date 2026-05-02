package com.ristorante.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.ristorante.ui.model.ArticoloDTO;
import com.ristorante.ui.service.ArticoloService;
import java.util.List;

public class DashboardHomeView {
	
	private final ArticoloService articoloService = new ArticoloService();

    public VBox build(String username) {
        Label title = new Label("Dashboard Admin");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label userInfo = new Label("Benvenuto, " + username);
        userInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        HBox header = new HBox(title, new Region(), userInfo);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        
        List<ArticoloDTO> articoli = articoloService.loadArticoli();

        long disponibili = articoli.stream()
                .filter(a -> "DISPONIBILE".equals(a.getStatoMagazzino()))
                .count();

        long inEsaurimento = articoli.stream()
                .filter(a -> "IN_ESAURIMENTO".equals(a.getStatoMagazzino()))
                .count();

        long esauriti = articoli.stream()
                .filter(a -> "ESAURITO".equals(a.getStatoMagazzino()))
                .count();

        long nonGestiti = articoli.stream()
                .filter(a -> "NON_GESTITO".equals(a.getStatoMagazzino()))
                .count();
        
        VBox tavoliSection = createSection(
                "Tavoli",
                new HBox(16,
                        createCard("Tavoli occupati", "0"),
                        createCard("Tavoli liberi", "0"),
                        createCard("Tavoli totali", "0")
                )
        );

        VBox ordiniSection = createSection(
                "Ordini",
                new HBox(16,
                        createCard("Ordini aperti", "0"),
                        createCard("Ordini in preparazione", "0"),
                        createCard("Ordini completati oggi", "0")
                )
        );

        VBox consegneSection = createSection(
                "Asporto e domicilio",
                new HBox(16,
                        createCard("Asporti attivi", "0"),
                        createCard("Domicili attivi", "0")
                )
        );

        VBox magazzinoSection = createSection(
                "Magazzino",
                new HBox(16,
                        createCard("Disponibili", String.valueOf(disponibili)),
                        createCard("In esaurimento", String.valueOf(inEsaurimento)),
                        createCard("Esauriti", String.valueOf(esauriti)),
                        createCard("Non gestiti", String.valueOf(nonGestiti))
                )
        );

        return new VBox(22, header, tavoliSection, ordiniSection, consegneSection, magazzinoSection);
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
    
    private VBox createSection(String title, HBox cards) {
        Label sectionTitle = new Label(title);
        sectionTitle.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        return new VBox(10, sectionTitle, cards);
    }
}
