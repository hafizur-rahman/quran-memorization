package com.dreamer.corpus;

import java.util.List;
import java.util.Optional;

public class QuranObject extends Book {
    private List<Chapter> translatedChapters;

    private List<Page> pages;

    public QuranObject(String translationId, String lang,
                       List<Chapter> chapters, List<Chapter> translatedChapters,
                       List<Page> pages) {
        super(translationId, lang, chapters);

        this.translatedChapters = translatedChapters;
        this.pages = pages;
    }

    public List<Chapter> getTranslatedChapters() {
        return translatedChapters;
    }

    public List<Page> getPages() {
        return pages;
    }

    public Optional<Chapter> getTranslatedChapter(int chapterId) {
        if (chapterId < 1 || chapterId > translatedChapters.size()) {
            return Optional.empty();
        }

        return Optional.of(translatedChapters.get(chapterId-1));
    }

    public Optional<Page> getPage(int pageId) {
        if (pageId < 1 || pageId > pages.size()) {
            return Optional.empty();
        }

        return Optional.of(pages.get(pageId-1));
    }
}