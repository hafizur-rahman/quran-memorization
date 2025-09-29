package com.dreamer.ui;

import javafx.scene.control.Tab;

public interface TafsirView {
    Tab getTab();

    void updateUI(int chapterId, int verseId);
}
