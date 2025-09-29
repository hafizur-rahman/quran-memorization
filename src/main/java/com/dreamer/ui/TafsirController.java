package com.dreamer.ui;


import javafx.scene.control.TabPane;

import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class TafsirController {
    private String resourcePath;

    private TabPane tafsirPane;

    private List<TafsirView> tafsirViewList = new ArrayList<>();

    public TafsirController(String resourcePath, TabPane tafsirPane) {
        this.resourcePath = resourcePath;
        this.tafsirPane = tafsirPane;

        initialize();
    }

    private void initialize() {
        tafsirViewList.add(new SingleColumnTafsir("Ma'ariful Quran",
                "jdbc:sqlite:"+ resourcePath + "/dbs/bn_mkhan.db",
                TextAlignment.JUSTIFY));

        tafsirViewList.add(new SingleColumnTafsir("Zakaria",
                "jdbc:sqlite:"+ resourcePath + "/dbs/bn_tafsirzakaria.db",
                TextAlignment.JUSTIFY));
        tafsirViewList.add(new SingleColumnTafsir("Bayaan",
                "jdbc:sqlite:"+ resourcePath + "/dbs/bn_tafsirbayaan.db",
                TextAlignment.JUSTIFY));
        tafsirViewList.add(new DoubleColumnTafsir(
                "Jalalayn",
                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_jalalayn.db",
                "jdbc:sqlite:"+ resourcePath + "/dbs/en_jalalayn.db"));

        for (TafsirView view: tafsirViewList) {
            tafsirPane.getTabs().add(view.getTab());
        }
    }

    public void updateUI(int chapterId, int verseId) {
        for (TafsirView view: tafsirViewList) {
            view.updateUI(chapterId, verseId);
        }
    }
}