package com.itextpdf.basics.image;

import java.net.URL;

public class JpegImage extends Image {

    protected JpegImage(URL url) {
        super(url, JPEG);
    }

    protected JpegImage(byte[] bytes) {
        super(bytes, JPEG);
    }
}
