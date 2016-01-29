package com.itextpdf.io.image;

import java.net.URL;

public class PngImage extends RawImage {

    protected PngImage(byte[] bytes) {
        super(bytes, PNG);
    }

    protected PngImage(URL url) {
        super(url, PNG);
    }
}
