package com.ristorante.ui.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class UiDialogs {

    public static boolean showConfirm(String windowTitle, String title, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogPane(pane);

        Label icon = buildIcon("?");
        icon.setStyle("""
            -fx-background-color: #dbeafe;
            -fx-text-fill: #1d4ed8;
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-alignment: center;
            -fx-min-width: 44;
            -fx-min-height: 44;
            -fx-max-width: 44;
            -fx-max-height: 44;
            -fx-background-radius: 999;
        """);

        VBox content = buildDialogContent(title, message, icon);
        pane.setContent(content);

        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) pane.lookupButton(ButtonType.CANCEL);

        okButton.setText("Conferma");
        cancelButton.setText("Annulla");

        stylePrimaryButton(okButton);
        styleSecondaryButton(cancelButton);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void showSuccess(String windowTitle, String title, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(ButtonType.OK);
        styleDialogPane(pane);

        Label icon = buildIcon("✓");
        icon.setStyle("""
            -fx-background-color: #dcfce7;
            -fx-text-fill: #15803d;
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-alignment: center;
            -fx-min-width: 44;
            -fx-min-height: 44;
            -fx-max-width: 44;
            -fx-max-height: 44;
            -fx-background-radius: 999;
        """);

        VBox content = buildDialogContent(title, message, icon);
        pane.setContent(content);

        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        okButton.setText("OK");
        stylePrimaryButton(okButton);

        dialog.showAndWait();
    }

    public static void showError(String windowTitle, String title, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(ButtonType.OK);
        styleDialogPane(pane);

        Label icon = buildIcon("!");
        icon.setStyle("""
            -fx-background-color: #fee2e2;
            -fx-text-fill: #b91c1c;
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-alignment: center;
            -fx-min-width: 44;
            -fx-min-height: 44;
            -fx-max-width: 44;
            -fx-max-height: 44;
            -fx-background-radius: 999;
        """);

        VBox content = buildDialogContent(title, message, icon);
        pane.setContent(content);

        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        okButton.setText("Chiudi");
        stylePrimaryButton(okButton);

        dialog.showAndWait();
    }

    public static void showInfo(String windowTitle, String title, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(ButtonType.OK);
        styleDialogPane(pane);

        Label icon = buildIcon("i");
        icon.setStyle("""
            -fx-background-color: #e0f2fe;
            -fx-text-fill: #0369a1;
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-alignment: center;
            -fx-min-width: 44;
            -fx-min-height: 44;
            -fx-max-width: 44;
            -fx-max-height: 44;
            -fx-background-radius: 999;
        """);

        VBox content = buildDialogContent(title, message, icon);
        pane.setContent(content);

        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        okButton.setText("OK");
        stylePrimaryButton(okButton);

        dialog.showAndWait();
    }

    private static VBox buildDialogContent(String title, String message, Label icon) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-text-fill: #1f2937;
        """);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: #6b7280;
        """);

        VBox textBox = new VBox(6, titleLabel, messageLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox header = new HBox(14, icon, textBox);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(18, header);
        content.setPadding(new Insets(24));
        content.setPrefWidth(420);

        return content;
    }

    private static Label buildIcon(String text) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private static void styleDialogPane(DialogPane pane) {
        pane.setStyle("""
            -fx-background-color: #f9fafb;
            -fx-background-radius: 18;
            -fx-border-radius: 18;
        """);
        pane.setMinWidth(460);
    }

    private static void stylePrimaryButton(Button button) {
        button.setPrefHeight(42);
        button.setMinWidth(110);
        ButtonBar.setButtonData(button, ButtonBar.ButtonData.OK_DONE);
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

    private static void styleSecondaryButton(Button button) {
        button.setPrefHeight(42);
        button.setMinWidth(110);
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
