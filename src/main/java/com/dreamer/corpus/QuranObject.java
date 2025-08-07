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

    public Optional<Page> locatePageByVerse(Verse verse) {
        for (int i = 0; i < pages.size()-1; i++) {
            Page currentPage = pages.get(i);
            Page nextPage = pages.get(i+1);

            if (verse.getChapterId() == currentPage.chapterId) {
                if (currentPage.chapterId == nextPage.chapterId) {
                    if (verse.getVerseId() >= currentPage.startVerse
                        && verse.getVerseId() < nextPage.startVerse) {
                        return Optional.of(currentPage);
                    }
                } else {
                    if (verse.getVerseId() >= currentPage.startVerse) {
                        return Optional.of(currentPage);
                    }
                }
            } else if (verse.getChapterId() == nextPage.chapterId) {
                if (verse.getVerseId() < nextPage.startVerse) {
                    return Optional.of(currentPage);
                }
            } else if (verse.getChapterId() > currentPage.chapterId
                    && verse.getChapterId() < nextPage.chapterId) {
                return Optional.of(nextPage);
            }
        }

        return Optional.of(pages.getLast());
    }
}