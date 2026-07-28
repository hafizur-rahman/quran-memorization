package com.dreamer.ui;

import com.dreamer.corpus.QuranDictionary;
import com.dreamer.corpus.QuranWord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
        colArabic.setMinWidth(25);
        colArabic.setStyle("-fx-font-size: 20px; -fx-font-family: \"Times New Roman\";");

        TableColumn<QuranWord, String> colTrans = new TableColumn<>("Translation");
        colTrans.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().translation));
        colTrans.setMinWidth(100);

        TableColumn<QuranWord, String> colRoot = new TableColumn<>("Root");
        colRoot.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().root));
        colRoot.setMinWidth(25);
        colRoot.setStyle("-fx-font-size: 18px; -fx-font-family: \"Times New Roman\";");

        TableColumn<QuranWord, String> colSubheading = new TableColumn<>("Subheading");
        colSubheading.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().subheading));
        colSubheading.setMinWidth(160);

        TableColumn<QuranWord, String> colCpPair = new TableColumn<>("CP Pair");
        colCpPair.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().cpPair));
        colCpPair.setMinWidth(80);
        colCpPair.setStyle("-fx-font-size: 18px; -fx-font-family: \"Times New Roman\";");

        TableColumn<QuranWord, String> colRemarks = new TableColumn<>("Remarks (Preview)");
        colRemarks.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().remarks));
        colRemarks.setMinWidth(100);

        getColumns().addAll(colArabic, colTrans, colRoot, colSubheading, colCpPair);

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
}
