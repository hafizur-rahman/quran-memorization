package com.dreamer.corpus;

public class QuranWord {
    public String arabic, translation, subheading, root, cpPair, remarks;

    public QuranWord(String arabic, String translation, String subheading, String root, String cpPair, String remarks) {
        this.arabic = arabic;
        this.translation = translation;
        this.subheading = subheading;
        this.root = root;
        this.cpPair = cpPair;
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Root: %s", arabic, translation, root);
    }
}
