package com.dreamer.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Chapter {
    private int id;
    private String name;

    private int verseCount;
    private int startingVerseId;
    private List<Verse> verses = new ArrayList<>();

    public Chapter(int id, String name, int verseCount, int startingVerseId) {
        this.id = id;
        this.name = name;
        this.verseCount = verseCount;
        this.startingVerseId = startingVerseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartingVerseId() {
        return startingVerseId;
    }

    public void setStartingVerseId(int startingVerseId) {
        this.startingVerseId = startingVerseId;
    }

    public int getEndingVerseId() {
        return startingVerseId + verseCount;
    }

    public List<Verse> getVerses() {
        return verses;
    }

    public Optional<Verse> getVerse(int verseId) {
        if (verseId < 1 || verseId > verses.size()) {
            return Optional.empty();
        }

        return Optional.of(verses.get(verseId-1));
    }

    public int getVerseCount() {
        return verseCount;
    }

    @Override
    public String toString() {
        return "Chapter " + id + ": " + name;
    }
}
