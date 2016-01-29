package com.itextpdf.io.image;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GifImage {

    private float logicalHeight;
    private float logicalWidth;
    private List<Image> frames = new ArrayList<>();
    private byte[] bytes;
    private URL url;

    protected GifImage(URL url) {
        this.url = url;
    }

    protected GifImage(byte[] bytes) {
        this.bytes = bytes;
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

    public List<Image> getFrames() {
        return frames;
    }

    protected byte[] getBytes() {
        return bytes;
    }

    protected URL getUrl() {
        return url;
    }

    protected void addFrame(Image frame) {
        frames.add(frame);
    }
}
