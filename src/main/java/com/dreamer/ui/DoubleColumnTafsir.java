package com.dreamer.ui;

import com.dreamer.util.VerseDetail;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DoubleColumnTafsir implements TafsirView {
    private String title;

    private String jdbcUrl1;
    private String jdbcUrl2;

    private Tab tab = new Tab();
    private Text text1;

    private Text text2;

    public DoubleColumnTafsir(String title, String jdbcUrl1, String jdbcUrl2) {
        this.title = title;
        this.jdbcUrl1 = jdbcUrl1;
        this.jdbcUrl2 = jdbcUrl2;

        buildUI();
    }

    private void buildUI() {
        tab.setText(title);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);

        BorderPane borderPane = new BorderPane();

        text1 = new Text();
        text1.setFont(Font.font("Arial", 24));
        text1.setFill(Paint.valueOf("dimgray"));
        text1.setLineSpacing(6);
        text1.setWrappingWidth(450);
        text1.setTextAlignment(TextAlignment.RIGHT);

        text2 = new Text();
        text2.setFont(Font.font("Arial", 16));
        text2.setFill(Paint.valueOf("dimgray"));
        text2.setLineSpacing(6);
        text2.setWrappingWidth(450);
        text2.setTextAlignment(TextAlignment.JUSTIFY);

        HBox hBox = new HBox(10, text1, new Separator(), text2);
        borderPane.setBottom(hBox);
        borderPane.setTop(new Separator());
        scrollPane.setContent(borderPane);

        tab.setContent(scrollPane);
        tab.setClosable(false);
    }

    public Tab getTab() {
        return tab;
    }

    public void updateUI(int chapterId, int verseId) {
        text1.setText(VerseDetail.getVerseDetails(chapterId, verseId, jdbcUrl1));
        text2.setText(VerseDetail.getVerseDetails(chapterId, verseId, jdbcUrl2));
    }
}
