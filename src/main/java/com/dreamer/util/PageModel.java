package com.dreamer.util;

import javafx.scene.image.Image;

public interface PageModel {
    int numPages();

    Image getImage(int pageNumber);
}
