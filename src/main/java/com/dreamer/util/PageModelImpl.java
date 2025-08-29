package com.dreamer.util;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

public class PageModelImpl implements PageModel {
    private String imageDirPath;

    public PageModelImpl(String imageDirPath) {
        this.imageDirPath = imageDirPath;
    }

    public int numPages() {
        return 611;
    }

    public Image getImage(int pageNumber) {
        Image pageImage;

        File file = new File(imageDirPath, String.format("/p%03d.gif", pageNumber));

        try {
            pageImage = new Image(new FileInputStream(file));
        } catch (Exception ex) {
            String juzId = String.format("%02d", (pageNumber / 20) + 1);

            throw new RuntimeException("Page image not found: " + file + ". You can download image from https://www.quranclassonline.com/Maktab/Para-" + juzId + ".html");
        }
        return pageImage;
    }
}
