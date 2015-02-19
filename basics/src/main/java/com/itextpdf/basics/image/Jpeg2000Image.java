package com.itextpdf.basics.image;

import java.net.URL;

public class Jpeg2000Image extends Image {

    protected Jpeg2000Image(URL url) {
        super(url, JPEG2000);
    }

    protected Jpeg2000Image(byte[] bytes) {
        super(bytes, JPEG2000);
    }
}
