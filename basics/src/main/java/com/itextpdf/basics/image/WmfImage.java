package com.itextpdf.basics.image;

import java.net.URL;

public class WmfImage extends Image {

    protected WmfImage(URL url) {
        super(url, WMF);
    }

    protected WmfImage(byte[] bytes) {
        super(bytes, WMF);
    }
}
