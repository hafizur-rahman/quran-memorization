package com.dreamer.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuranDB {
    private List<String> jdbcUrls;

    public QuranDB(List<String> jdbcUrls) {
        this.jdbcUrls = jdbcUrls;
    }

    public String getVerseDetails(int chapterId, int verseId) {
        List<String> texts = new ArrayList<>();

        for (String jdbcUrl: jdbcUrls) {
            String text = VerseDetail.getVerseDetails(chapterId, verseId, jdbcUrl);

            if (text != null) {
                texts.add(text);
            }
        }

        return String.join("\r\n\r\n", texts);
    }
}
