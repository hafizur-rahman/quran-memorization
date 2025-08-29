package com.dreamer.util;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.nio.file.Path;

public class PageModelImpl implements PageModel {
    private Path imageDirPath;

    public PageModelImpl(Path imageDirPath) {
        this.imageDirPath = imageDirPath;
    }

    public int numPages() {
        return 611;
    }

    public Image getImage(int pageNumber) {
        Image pageImage;

        Path file = Path.of(imageDirPath.toString(), String.format("/p%03d.gif", pageNumber));

        try {
            pageImage = new Image(new FileInputStream(file.toFile()));
        } catch (Exception ex) {
            throw new RuntimeException("Page image not found: " + file);
        }
        return pageImage;
    }
}
