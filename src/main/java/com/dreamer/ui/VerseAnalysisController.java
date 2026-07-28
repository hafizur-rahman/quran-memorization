package com.dreamer.ui;

import com.dreamer.corpus.QuranDictionary;
import com.dreamer.corpus.QuranObject;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VerseAnalysisController {
    private Optional<QuranObject> bookRef;
    private String resourcePath;

    private TabPane analysisPane;

    private List<IrabTableView> irabTableViews = new ArrayList<>();
    private VocabularyTableView vocabularyTableView;

    private QuranDictionary quranDictionary;

    public VerseAnalysisController(Optional<QuranObject> bookRef, String resourcePath, TabPane analysisPane) {
        this.bookRef = bookRef;
        this.resourcePath = resourcePath;
        this.analysisPane = analysisPane;

        initialize();
    }

    private void initialize() {
        quranDictionary = new QuranDictionary();

        try {
            quranDictionary.loadFromFile(resourcePath + "\\quran_wbw_dictionary.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vocabularyTableView = new VocabularyTableView(quranDictionary);
        analysisPane.getTabs().add(createTab("Vocabulary", vocabularyTableView));

        irabTableViews.add(new IrabTableView(bookRef, resourcePath, "irob-default"));
        irabTableViews.add(new IrabTableView(bookRef, resourcePath, "irob-tasykil"));
        irabTableViews.add(new IrabTableView(bookRef, resourcePath, "irob-darwish"));

        for (IrabTableView view: irabTableViews) {
            analysisPane.getTabs().add(createTab(view.getIrabSetId(), view));
        }
    }

    private Tab createTab(String title, TableView<?> view) {
        Tab tab = new Tab();
        tab.setText(title);
        tab.setContent(view);
        tab.setClosable(false);

        return tab;
    }

    public void updateUI(int chapterId, int verseId) {
        vocabularyTableView.updateUI(chapterId, verseId);

        for (IrabTableView view: irabTableViews) {
            view.updateUI(chapterId, verseId);
        }
    }
}
