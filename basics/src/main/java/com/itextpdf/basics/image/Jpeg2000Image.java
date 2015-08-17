package com.itextpdf.basics.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Jpeg2000Image extends Image {

    protected Jpeg2000Image(URL url) {
        super(url, JPEG2000);
    }

    protected Jpeg2000Image(byte[] bytes) {
        super(bytes, JPEG2000);
    }

    @Override
    public boolean canImageBeInline() {
        Logger logger = LoggerFactory.getLogger(Image.class);
        logger.warn("Image cannot be inline if it has JPXDecode filter. It will be added as an ImageXObject");
        return false;
    }
}
