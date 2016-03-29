package com.itextpdf.io.image;

import java.net.URL;

public class BmpImage extends RawImage {

    private int size;
    private boolean noHeader;

    protected BmpImage(URL url, boolean noHeader, int size) {
        super(url, ImageType.BMP);
        this.noHeader = noHeader;
        this.size = size;
    }

    protected BmpImage(byte[] bytes, boolean noHeader, int size) {
        super(bytes, ImageType.BMP);
        this.noHeader = noHeader;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public boolean isNoHeader() {
        return noHeader;
    }
}