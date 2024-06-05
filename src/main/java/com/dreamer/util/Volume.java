package com.dreamer.util;

import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class Volume {
    private int id;
    private String pdfPath;

    private PdfModel pdfModel;

    private Map<Integer, Integer> pageIndex;

    private int pageNumberOffset;

    public Volume(int id, String pdfPath, Map<Integer, Integer> pageIndex, int pageNumerOffset) {
        this.id = id;
        this.pdfPath = pdfPath;
        this.pageIndex = pageIndex;
        this.pageNumberOffset = pageNumerOffset;
    }

    public int getId() {
        return id;
    }

    public boolean isMushafPageInRange(int muashafPageId) {
        if (pageIndex != null) {
            int start = Collections.min(pageIndex.keySet());
            int end = Collections.max(pageIndex.keySet());

            return muashafPageId >= start && muashafPageId <= end;
        }

        return false;
    }

    public Optional<Integer> getPageIndex(int muashafPageId) {
        if (pageIndex.containsKey(muashafPageId)) {
            return Optional.of(pageIndex.get(muashafPageId) + pageNumberOffset);
        }

        return Optional.empty();
    }

    public Image getPageImage(int pageNumber) {
        if (pdfModel == null) {
            pdfModel = new PdfModel(Path.of(pdfPath));
        }

        if (pageNumber >= 0 && pageNumber <= pdfModel.numPages()) {
            return pdfModel.getImage(pageNumber);
        }

        return null;
    }

    public PdfModel getPdfModel() {
        return pdfModel;
    }
}
