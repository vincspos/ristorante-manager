package com.ristorante.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DashboardHomeView {

    public VBox build(String username) {
        Label title = new Label("Dashboard Admin");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label userInfo = new Label("Benvenuto, " + username);
        userInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        HBox header = new HBox(title, new Region(), userInfo);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox cardsRow1 = new HBox(16,
                createCard("Tavoli occupati", "14"),
                createCard("Ordini aperti", "8"),
                createCard("Asporti attivi", "3")
        );

        HBox cardsRow2 = new HBox(16,
                createCard("Domicili attivi", "5"),
                createCard("Incasso oggi", "€ 1.245"),
                createCard("Spese oggi", "€ 180")
        );

        return new VBox(20, header, cardsRow1, cardsRow2);
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
}
