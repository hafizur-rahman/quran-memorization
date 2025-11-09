package com.dreamer.util;

import java.io.Serializable;

public class AppConfig implements Serializable {
    private int pageId;

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }
}
