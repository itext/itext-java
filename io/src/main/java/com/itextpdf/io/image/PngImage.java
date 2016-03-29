package com.itextpdf.io.image;

import java.net.URL;

public class PngImage extends RawImage {

    protected PngImage(byte[] bytes) {
        super(bytes, ImageType.PNG);
    }

    protected PngImage(URL url) {
        super(url, ImageType.PNG);
    }
}
