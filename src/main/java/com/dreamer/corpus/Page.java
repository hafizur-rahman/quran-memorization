package com.dreamer.corpus;

public class Page {
    int index;

    int chapterId;

    int startVerse;

    public Page(int index, int chapterId, int startVerse) {
        this.index = index;
        this.chapterId = chapterId;
        this.startVerse = startVerse;
    }

    public int getIndex() {
        return index;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getStartVerse() {
        return startVerse;
    }
}
