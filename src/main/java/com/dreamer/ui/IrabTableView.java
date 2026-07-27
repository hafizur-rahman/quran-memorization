package com.dreamer.ui;

import com.dreamer.corpus.QuranObject;
import com.dreamer.util.AnalysisRow;
import com.dreamer.util.JsonParserService;
import com.dreamer.util.Segment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.Optional;

public class IrabTableView extends TableView<AnalysisRow> {
    private Optional<QuranObject> bookRef;
    private String resourceBase;
    private String irabSetId;

    public IrabTableView(Optional<QuranObject> bookRef, String resourceBase, String irabSetId) {
        this.bookRef = bookRef;
        this.resourceBase = resourceBase;
        this.irabSetId = irabSetId;

        setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT); // Essential for Arabic
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setStyle("-fx-font-size: 22px; -fx-font-family: \"Arial\";");

        setupColumns();
    }

    private void setupColumns() {
        TableColumn<AnalysisRow, AnalysisRow> column = new TableColumn<>("الإعراب");
        column.setPrefWidth(700);

        // We point the value factory to the object itself so the cell has access to all segments
        column.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));

        // 3. The Magic: Custom Cell Factory using TextFlow
        column.setCellFactory(param -> new TableCell<AnalysisRow, AnalysisRow>() {
            @Override
            protected void updateItem(AnalysisRow row, boolean empty) {
                super.updateItem(row, empty);

                if (empty || row == null) {
                    setGraphic(null);
                } else {
                    // Create a TextFlow to hold multiple colored text segments
                    TextFlow textFlow = new TextFlow();
                    textFlow.setTextAlignment(TextAlignment.JUSTIFY);

                    for (Segment segment : row.getSegments()) {
                        Text textNode = new Text(segment.getText());
                        try {
                            // Convert string color (e.g., "brown") to JavaFX Color
                            textNode.setFill(javafx.scene.paint.Color.web(segment.getColor()));
                        } catch (Exception e) {
                            textNode.setFill(Color.BLACK); // Fallback if color is invalid
                        }
                        textFlow.getChildren().add(textNode);
                    }
                    setGraphic(textFlow);

                    textFlow.maxWidthProperty().bind(getTableColumn().widthProperty().subtract(6));

                    double height = (Math.ceil(textFlow.prefWidth(-1) / 650) + 1) * textFlow.prefHeight(-1) * 1.5;
                    setPrefHeight(height);
                }
            }
        });

        getColumns().add(column);
    }

    public void updateUI(int suraId, int verseId) {
        try {
            String filePath = resourceBase + "\\quran-grammer\\" + irabSetId + "\\" + bookRef.get().getChapter(suraId).get().getName().toLowerCase() + "\\" + verseId + ".json";

            ObservableList<AnalysisRow> masterData = FXCollections.observableArrayList(JsonParserService.loadData(filePath));

            setItems(masterData);
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIrabSetId() {
        return irabSetId;
    }
}
