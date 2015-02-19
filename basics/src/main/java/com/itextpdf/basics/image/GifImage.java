package com.itextpdf.basics.image;

import java.net.URL;

public class GifImage extends RawImage {

    private int frame;
    private float logicalHeight;
    private float logicalWidth;

    protected GifImage(URL url, int frame) {
        super(url, GIF);
        this.frame = frame;
    }

    protected GifImage(byte[] bytes, int frame) {
        super(bytes, GIF);
        this.frame = frame;
    }

    public int getFrame() {
        return frame;
    }

    public float getLogicalHeight() {
        return logicalHeight;
    }

    public void setLogicalHeight(float logicalHeight) {
        this.logicalHeight = logicalHeight;
    }

    public float getLogicalWidth() {
        return logicalWidth;
    }

    public void setLogicalWidth(float logicalWidth) {
        this.logicalWidth = logicalWidth;
    }
}
