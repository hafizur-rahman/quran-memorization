package com.dreamer.ui;

import com.dreamer.corpus.QuranDictionary;
import com.dreamer.corpus.QuranWord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VocabularyTableView extends TableView<QuranWord> {
    private QuranDictionary quranDictionary;

    public VocabularyTableView(QuranDictionary quranDictionary) {
        this.quranDictionary = quranDictionary;

        setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setStyle("-fx-font-size: 15px; -fx-font-family: \"Times New Roman\";");

        setupColumns();
    }

    private void setupColumns() {
        TableColumn<QuranWord, String> colArabic = new TableColumn<>("Arabic");
        colArabic.setCellValueFactory(new PropertyValueFactory<>("arabic"));
        colArabic.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().arabic));
        colArabic.setMaxWidth(80);
        colArabic.setStyle("-fx-font-size: 20px; -fx-font-family: \"Times New Roman\";");

        TableColumn<QuranWord, String> colTrans = new TableColumn<>("Translation");
        colTrans.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().translation));
        colTrans.setPrefWidth(100);

        TableColumn<QuranWord, String> colRoot = new TableColumn<>("Root");
        colRoot.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().root));
        colRoot.setPrefWidth(35);
        colRoot.setStyle("-fx-font-size: 18px; -fx-font-family: \"Times New Roman\";");

        TableColumn<QuranWord, String> colSubheading = new TableColumn<>("Subheading");
        colSubheading.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().subheading));
        colSubheading.setPrefWidth(160);

        TableColumn<QuranWord, String> colCpPair = new TableColumn<>("CP Pair");
        colCpPair.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().cpPair));
        colCpPair.setPrefWidth(100);
        colCpPair.setStyle("-fx-font-size: 18px; -fx-font-family: \"Times New Roman\";");

        TableColumn<QuranWord, String> colRemarks = new TableColumn<>("Remarks (Preview)");
        colRemarks.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().remarks));
        colRemarks.setPrefWidth(100);

        TableColumn<QuranWord, Void> colAction = new TableColumn<>("Details");
        colAction.setMaxWidth(60);
        colAction.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    // Get the word associated with this specific row
                    QuranWord clickedWord = getTableRow().getItem();

                    if (clickedWord.remarks != null && !clickedWord.remarks.isEmpty()) {
                        // Create the Button
                        Button btn = new Button("...");
                        btn.setStyle("-fx-background-color: gray; -fx-text-fill: white;");

                        btn.setOnAction(event -> showDetailsWindow(clickedWord));
                        setPrefWidth(-1);
                        setGraphic(btn);
                    }
                }
            }
        });

        getColumns().addAll(colArabic, colTrans, colRoot, colSubheading, colCpPair, colAction);

        for (TableColumn column: getColumns()) {
            column.setSortable(false);
        }
    }

    public void updateUI(int suraId, int verseId) {
        try {
            ObservableList<QuranWord> results = FXCollections.observableArrayList(quranDictionary.getVerseDictionary(suraId, verseId));

            setItems(results);
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logic to open a Pop-up Window with the full text
     */
    private void showDetailsWindow(QuranWord word) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL); // Blocks main window until closed
        detailStage.setTitle("Word Details: " + word.arabic);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        // Large Arabic Header
        Label lblHeader = new Label(word.arabic);
        lblHeader.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        // Translation and Root info
        Label lblInfo = new Label(String.format("Translation: %s\nRoot: %s",
                word.translation, word.root));
        lblInfo.setStyle("-fx-font-size: 14px;");

        // The heavy text area (The Remarks)
        TextArea textArea = new TextArea(word.remarks);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(300);
        textArea.setFont(Font.font("Arial", 12));

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> detailStage.close());

        content.getChildren().addAll(lblHeader, lblInfo, new Separator(), textArea, closeBtn);

        detailStage.setScene(new Scene(content, 500, 500));
        detailStage.show();
    }
}
