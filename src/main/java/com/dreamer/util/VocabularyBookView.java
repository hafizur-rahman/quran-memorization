package com.dreamer.util;

import javafx.scene.image.ImageView;

import java.util.Map;

import static java.util.Map.entry;

public class VocabularyBookView {
    private PdfModel vol1Model;

    private PdfModel vol2Model;

    private ImageView pageImage;

    private int currentPageInQuran = -1;

    private int currentPage = 0;

    private PdfModel currentModel;

    private Map<Integer, Integer> vol1Index = Map.ofEntries(
            entry(1, 1),
            entry(2, 10),
            entry(3, 13),
            entry(4, 17),
            entry(5, 23),
            entry(6, 26),
            entry(7, 34),
            entry(8, 40),
            entry(9, 45),
            entry(10, 49),
            entry(11, 53),
            entry(12, 56),
            entry(13, 58),
            entry(14, 62),
            entry(15, 49),
            entry(16, 49),
            entry(17, 49),
            entry(18, 49),
            entry(19, 49),
            entry(20, 49)
            );

    private Map<Integer, Integer> vol2Index = Map.ofEntries(
            entry(402, 221),
            entry(403, 222),
            entry(404, 223),
            entry(405, 226),
            entry(406, 231),
            entry(407, 234),
            entry(408, 237),
            entry(409, 240),
            entry(410, 242),
            entry(411, 245),
            entry(412, 248),
            entry(413, 252),
            entry(414, 255),
            entry(415, 258),
            entry(416, 260),
            entry(417, 261),
            entry(418, 264),
            entry(419, 266),
            entry(420, 270),
            entry(421, 272),
            entry(422, 276),
            entry(423, 279),
            entry(424, 282),
            entry(425, 286),

            entry(477, 443),
            entry(478, 445),
            entry(479, 447),
            entry(480, 449),
            entry(481, 451),

            entry(590, 691),
            entry(591, 692),
            entry(592, 696),
            entry(593, 699),
            entry(594, 702),
            entry(595, 692),
            entry(596, 692),
            entry(597, 692),
            entry(598, 692),
            entry(599, 692),
            entry(600, 692),
            entry(601, 692),
            entry(602, 692),
            entry(603, 692)

    );

    private final int VOL1_PAGE_NO_OFFSET = 7;
    private final int VOL2_PAGE_NO_OFFSET = 7;

    public VocabularyBookView(PdfModel vol1Model, PdfModel vol2Model, ImageView pageImage) {
        this.vol1Model = vol1Model;
        this.vol2Model = vol2Model;
        this.pageImage = pageImage;

        setCurrentPageIndex(0);
    }

    public void setCurrentPageIndex(int requestedPageInQuran) {
        if (pageImage != null && requestedPageInQuran != currentPageInQuran) {
            if (requestedPageInQuran >= 0 && requestedPageInQuran <= BookView.QURAN_PAGE_COUNT) {
                currentPageInQuran = requestedPageInQuran;

                boolean found = false;

                if (vol1Index.containsKey(requestedPageInQuran)) {
                    found = true;

                    currentModel = vol1Model;
                    currentPage = vol1Index.get(requestedPageInQuran) + VOL1_PAGE_NO_OFFSET;
                } else if (vol2Index.containsKey(requestedPageInQuran)) {
                    found = true;

                    currentModel = vol2Model;
                    currentPage = vol2Index.get(requestedPageInQuran) + VOL2_PAGE_NO_OFFSET;
                }

                if (found) {
                    pageImage.setImage(currentModel.getImage(this.currentPage));
                }
            }
        }
    }

    public void showNextPage() {
        int requestedPageId = currentPage + 1;
        if (requestedPageId <= currentModel.numPages()) {
            currentPage = requestedPageId;

            pageImage.setImage(currentModel.getImage(this.currentPage));
        }
    }

    public void showPrevPage() {
        int requestedPageId = currentPage - 1;
        if (requestedPageId >= 0) {
            currentPage = requestedPageId;

            pageImage.setImage(currentModel.getImage(this.currentPage));
        }
    }
}
