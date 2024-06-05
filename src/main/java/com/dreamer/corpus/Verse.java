package com.dreamer.corpus;

public class Verse {
    private int chapterId;
    private int verseId;
    private String text;

    public Verse(int chapterId, int verseId, String text) {
        this.chapterId = chapterId;
        this.verseId = verseId;
        this.text = text;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getVerseId() {
        return verseId;
    }

    public String getText() {
        return text;
    }

    public void setText(String s) {
        this.text = text;
    }
}
