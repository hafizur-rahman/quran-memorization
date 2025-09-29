package com.dreamer.ui;

import com.dreamer.util.VerseDetail;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class SingleColumnTafsir implements TafsirView {
    private String title;

    private String jdbcUrl;

    private Tab tab = new Tab();
    private Text text;
    private boolean isArabic;

    public SingleColumnTafsir(String title, String jdbcUrl, boolean isArabic) {
        this.title = title;
        this.jdbcUrl = jdbcUrl;
        this.isArabic = isArabic;

        buildUI();
    }

    private void buildUI() {
        tab.setText(title);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        text = new Text();
        text.setFont(Font.font("Arial", isArabic ? 24 : 16));
        text.setFill(Paint.valueOf("dimgray"));
        text.setLineSpacing(6);
        text.setWrappingWidth(930);
        text.setTextAlignment(isArabic ? TextAlignment.RIGHT : TextAlignment.JUSTIFY);

        BorderPane borderPane = new BorderPane();

        scrollPane.setContent(new VBox(10, new Separator(), text));
        borderPane.setCenter(scrollPane);

        tab.setContent(scrollPane);
        tab.setClosable(false);
    }

    public Tab getTab() {
        return tab;
    }

    public void updateUI(int chapterId, int verseId) {
        text.setText(VerseDetail.getVerseDetails(chapterId, verseId, jdbcUrl));
    }
}
