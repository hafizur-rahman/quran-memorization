package com.dreamer.util;

import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TafsirView {

    private ImageView pageUI;

    private Map<Integer, Volume> volumes = new HashMap<>();

    private int currentPageInQuran = -1;

    private int currentPage;

    private PdfModel currentModel;

    public TafsirView(ImageView pageUI) {
        this.pageUI = pageUI;

        setCurrentPageIndex(0);
    }

    public void setCurrentPageIndex(int requestedPageInQuran) {
        if (requestedPageInQuran != currentPageInQuran) {
            if (requestedPageInQuran >= 0 && requestedPageInQuran <= BookView.QURAN_PAGE_COUNT) {
                currentPageInQuran = requestedPageInQuran;

                for  (Volume volume: volumes.values()) {
                    if (volume.isMushafPageInRange(requestedPageInQuran)) {
                        Optional<Integer> pageIndex = volume.getPageIndex(requestedPageInQuran);
                        if (pageIndex.isPresent()) {
                            currentPage = pageIndex.get();
                            pageUI.setImage(volume.getPageImage(currentPage));

                            currentModel = volume.getPdfModel();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void addVolume(Volume volume) {
        volumes.put(volume.getId(), volume);
    }

    public void showPrevPage() {
        int requestedPageId = currentPage - 1;
        if (requestedPageId >= 0) {
            currentPage = requestedPageId;

            pageUI.setImage(currentModel.getImage(this.currentPage));
        }
    }

    public void showNextPage() {
        int requestedPageId = currentPage + 1;
        if (requestedPageId <= currentModel.numPages()) {
            currentPage = requestedPageId;

            pageUI.setImage(currentModel.getImage(this.currentPage));
        }
    }
}

