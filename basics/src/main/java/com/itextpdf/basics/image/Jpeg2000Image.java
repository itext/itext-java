package com.itextpdf.basics.image;

import com.itextpdf.basics.LogMessageConstant;
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
        logger.warn(LogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER);
        return false;
    }
}
