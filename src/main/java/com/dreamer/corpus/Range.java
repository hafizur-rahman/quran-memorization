package com.dreamer.corpus;

public class Range {
    private Verse startVerse;

    private Verse endVerse;

    public Range(Verse startVerse, Verse endVerse) {
        this.startVerse = startVerse;
        this.endVerse = endVerse;
    }

    public Verse getStartVerse() {
        return startVerse;
    }

    public Verse getEndVerse() {
        return endVerse;
    }
}
