package com.dreamer.corpus;

import com.dreamer.util.loader.MDFileLoader;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QuranTextLoader {
    private MDFileLoader mdFileLoader = new MDFileLoader();

    public MDFileLoader getMdFileLoader() {
        return mdFileLoader;
    }

    public Optional<QuranObject> loadMetaData(InputStream inputStream) {
        QuranObject quranObject = null;

        XmlMapper mapper = new XmlMapper();

        try {
            Map<String, Object> data = mapper.readValue(inputStream, Map.class);

            List<Chapter> chapters = initializeChapters(data);
            List<Chapter> translatedChapters = new ArrayList<>(chapters);

            List<Page> pages = initializePages(data);

            quranObject = new QuranObject(
                    (String) data.get("TranslationID"),
                    (String) data.get("Language"),
                    chapters,
                    translatedChapters,
                    pages
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(quranObject);
    }

    private List<Page> initializePages(Map<String, Object> data) {
        List<Page> pages = new ArrayList<>();

        List<Map<String, String>> pageDataMap = (List)((Map<String, Object>) data.get("pages")).get("page");
        for (Map<String, String> chapterData: pageDataMap) {
            int pageId = Integer.parseInt(chapterData.get("index"));
            int chapterId = Integer.parseInt(chapterData.get("sura"));
            int startVerseId = Integer.parseInt(chapterData.get("aya"));

            Page page = new Page(pageId, chapterId, startVerseId);
            pages.add(page);
        }

        return pages;
    }

    private static List<Chapter> initializeChapters(Map<String, Object> data) {
        List<Chapter> chapters = new ArrayList<>();

        List<Map<String, String>> chapterDataMap = (List)((Map<String, Object>) data.get("suras")).get("sura");
        for (Map<String, String> chapterData: chapterDataMap) {
            int chapterId = Integer.parseInt(chapterData.get("index"));
            String chapterName = chapterData.get("tname");
            int verseCount = Integer.parseInt(chapterData.get("ayas"));
            int startingAyatId = Integer.parseInt(chapterData.get("start"));

            Chapter chapter = new Chapter(chapterId, chapterName, verseCount, startingAyatId);
            chapters.add(chapter);
        }

        return chapters;
    }

    public QuranObject populateVerseText(QuranObject quranObject, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            while (line != null) {
                final String[] parts = line.split("\\|");

                if (parts.length == 3 && !parts[0].isEmpty() && !parts[1].isEmpty() && !parts[2].isEmpty()) {
                    int chapterId = Integer.parseInt(parts[0]);
                    int verseId = Integer.parseInt(parts[1]);
                    String verseText = parts[2];

                    Optional<Chapter> chapterRef = quranObject.getChapter(chapterId);

                    if (chapterRef.isPresent()) {
                        Chapter chapter = chapterRef.get();

                        chapter.getVerses().add(new Verse(chapterId, verseId, verseText));
                    }
                }

                // read next line
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return quranObject;
    }

    public QuranObject populateTranslationText(QuranObject quranObject, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            while (line != null) {
                final String[] parts = line.split("\\|");

                if (parts.length == 3 && !parts[0].isEmpty() && !parts[1].isEmpty() && !parts[2].isEmpty()) {
                    int chapterId = Integer.parseInt(parts[0]);
                    int verseId = Integer.parseInt(parts[1]);
                    String verseText = parts[2];

                    Optional<Chapter> chapterRef = quranObject.getTranslatedChapter(chapterId);

                    if (chapterRef.isPresent()) {
                        Chapter chapter = chapterRef.get();

                        chapter.getVerses().add(new Verse(chapterId, verseId, verseText));
                    }
                }

                // read next line
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return quranObject;
    }

    public List<Chapter> loadTafsir(QuranObject quranObject, String file) {
        return mdFileLoader.loadTafsir(quranObject, file);
    }
}
