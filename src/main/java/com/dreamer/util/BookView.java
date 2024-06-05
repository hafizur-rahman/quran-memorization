package com.dreamer.util;

import javafx.scene.image.ImageView;

public class BookView {
    public static final int QURAN_PAGE_COUNT = 604;

    private PdfModel model;

    private ImageView pageImage;

    private int currentPageId = -1;

    public BookView(PdfModel model, ImageView pageImage) {
        this.model = model;
        this.pageImage = pageImage;

        setCurrentPageIndex(0);
    }

    public void setCurrentPageIndex(int newPageId) {
        if (pageImage != null && newPageId != currentPageId) {
            if (newPageId >= 0 && newPageId <= model.numPages()) {
                this.currentPageId = newPageId;

                pageImage.setImage(model.getImage(newPageId));
            }
        }
    }
}