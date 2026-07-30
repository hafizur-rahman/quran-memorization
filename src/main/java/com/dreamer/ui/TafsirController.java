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
//        tafsirViewList.add(new SingleColumnTafsir("Erab",
//                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_e3rab.db",
//                true));
//        tafsirViewList.add(new SingleColumnTafsir("Abu Bakar Zakaria",
//                "jdbc:sqlite:"+ resourcePath + "/dbs/bn_tafsirzakaria.db",
//                false));
//        tafsirViewList.add(new SingleColumnTafsir("Bayaan",
//                "jdbc:sqlite:"+ resourcePath + "/dbs/bn_tafsirbayaan.db",
//                false));
//        tafsirViewList.add(new SingleColumnTafsir("Ibn Kathir",
//                "jdbc:sqlite:"+ resourcePath + "/dbs/bn_mujibur.db",
//                false));

        tafsirViewList.add(new DoubleColumnTafsir(
                "Jalalayn",
                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_jalalayn.db",
                "jdbc:sqlite:"+ resourcePath + "/dbs/en_jalalayn.db"));

        tafsirViewList.add(new SingleColumnTafsir("Muyassar",
                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_muyassar.db",
                true));
        tafsirViewList.add(new SingleColumnTafsir("Qurtubi",
                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_qurtubi.db",
                true));

        tafsirViewList.add(new SingleColumnTafsir("Baghawi",
                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_baghawi.db",
                true));

//        tafsirViewList.add(new SingleColumnTafsir("Saddi",
//                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_saddi.db",
//                true));
//        tafsirViewList.add(new SingleColumnTafsir("Tanweer",
//                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_tanweer.db",
//                true));
//        tafsirViewList.add(new SingleColumnTafsir("Waseet",
//                "jdbc:sqlite:"+ resourcePath + "/dbs/ar_waseet.db",
//                true));

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