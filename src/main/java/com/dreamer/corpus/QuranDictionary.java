package com.dreamer.corpus;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class QuranDictionary {
    // Map<ChapterID, Map<VerseID, List<Words>>>
    private final Map<Integer, Map<Integer, List<QuranWord>>> data = new HashMap<>();

    public void loadFromFile(String path) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withTrim())) {

            for (CSVRecord record : parser) {
                int chapter = Integer.parseInt(record.get("chapterID"));
                int verse = Integer.parseInt(record.get("verseID"));

                QuranWord word = new QuranWord(
                        record.get("arabic_word"),
                        record.get("translation"),
                        record.get("subheading"),
                        record.get("rootArabic"),
                        record.get("CPpair"),
                        record.isMapped("rootDesc") ? record.get("rootDesc") : ""
                );

                data.computeIfAbsent(chapter, k -> new HashMap<>())
                        .computeIfAbsent(verse, k -> new ArrayList<>())
                        .add(word);
            }
        }
    }

    public List<QuranWord> getVerseDictionary(int chapter, int verse) {
        return data.getOrDefault(chapter, Collections.emptyMap())
                .getOrDefault(verse, Collections.emptyList());
    }
}
