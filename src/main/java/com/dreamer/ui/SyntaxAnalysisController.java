package com.dreamer.ui;

import com.dreamer.corpus.QuranObject;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SyntaxAnalysisController {
    private Optional<QuranObject> bookRef;
    private String resourcePath;

    private TabPane analysisPane;

    private List<IrabTableView> irabTableViews = new ArrayList<>();

    public SyntaxAnalysisController(Optional<QuranObject> bookRef, String resourcePath, TabPane analysisPane) {
        this.bookRef = bookRef;
        this.resourcePath = resourcePath;
        this.analysisPane = analysisPane;

        initialize();
    }

    private void initialize() {
        irabTableViews.add(new IrabTableView(bookRef, resourcePath, "irob-default"));
        irabTableViews.add(new IrabTableView(bookRef, resourcePath, "irob-tasykil"));
        irabTableViews.add(new IrabTableView(bookRef, resourcePath, "irob-darwish"));

        for (IrabTableView view: irabTableViews) {
            Tab tab = new Tab();
            tab.setText(view.getIrabSetId());
            tab.setContent(view);
            tab.setClosable(false);

            analysisPane.getTabs().add(tab);
        }
    }

    public void updateUI(int chapterId, int verseId) {
        for (IrabTableView view: irabTableViews) {
            view.updateUI(chapterId, verseId);
        }
    }
}
