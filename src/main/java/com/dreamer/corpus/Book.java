package com.dreamer.corpus;

import java.util.List;
import java.util.Optional;

public class Book {
    private String translationId;

    private String lang;

    private List<Chapter> chapters;

    public Book(String translationId, String lang, List<Chapter> chapters) {
        this.translationId = translationId;
        this.lang = lang;
        this.chapters = chapters;
    }

    public String getTranslationId() {
        return translationId;
    }

    public String getLang() {
        return lang;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public Optional<Chapter> getChapter(int chapterId) {
        if (chapterId < 1 || chapterId > chapters.size()) {
            return Optional.empty();
        }

        return Optional.of(chapters.get(chapterId-1));
    }

}
