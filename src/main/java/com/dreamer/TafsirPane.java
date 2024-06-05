package com.dreamer;

import com.dreamer.corpus.Chapter;
import com.dreamer.corpus.QuranObject;
import com.dreamer.corpus.Verse;
import com.dreamer.util.loader.MDFileLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Optional;

public class TafsirPane {

    private TafsirTab[] tabs;

    private TafsirPane(TafsirTab[] tabs) {
        this.tabs = tabs;
    }

    public static TafsirPane build(TabPane parent, MDFileLoader loader, QuranObject quranObject) {
        TafsirTab tab1 = TafsirTab.build("Jalalain", parent,"ar-jalalayn-tanzil.md",
                loader, quranObject);
        TafsirTab tab2 = TafsirTab.build("Ibn Kathir", parent,"ar-ibn-kathir-qurancom.md",
                loader, quranObject);

        return new TafsirPane(new TafsirTab[]{tab1, tab2});
    }

    public void updateView(int chapterId, int verseId) {
        for (TafsirTab tab: tabs) {
            tab.updateView(chapterId, verseId);
        }
    }

    public static class TafsirTab {
        private final TextArea textArea;

        private final List<Chapter> chapters;

        private TafsirTab(TextArea textArea, List<Chapter> chapters) {
            this.textArea = textArea;
            this.chapters = chapters;
        }

        public static TafsirTab build(String id, TabPane parent, String fileName,
                               MDFileLoader loader, QuranObject quranObject) {
            TextArea textArea = buildUI(id, parent);

            List<Chapter> chapters = loader.loadTafsir(quranObject,
                    TafsirTab.class.getResource(fileName).getFile());

            return new TafsirTab(textArea, chapters);
        }

        public void updateView(int chapterId, int verseId) {
            Optional<Verse> tafsirRef = chapters.get(chapterId).getVerse(verseId);

            textArea.setText(tafsirRef.get().getText());
        }

        private static TextArea buildUI(String id, TabPane parent) {
            TextArea textArea = createTafsirTextArea();

            ScrollPane scrollPane=new ScrollPane();
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setContent(textArea);

            Label spacer = new Label("   ");
            spacer.setFont(new Font(20.0));

            Tab tab=new Tab(id);
            tab.setClosable(false);
            tab.setContent(new VBox(scrollPane, spacer));

            parent.getTabs().add(tab);

            return textArea;
        }

        private static TextArea createTafsirTextArea() {
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            textArea.setPrefHeight(300.0);
            textArea.setPrefWidth(950.0);
            textArea.setWrapText(true);
            textArea.setFont(new Font("Arial", 25.0));

            return textArea;
        }
    }
}
