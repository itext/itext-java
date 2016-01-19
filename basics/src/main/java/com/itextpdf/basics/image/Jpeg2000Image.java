package com.itextpdf.basics.image;

import com.itextpdf.basics.LogMessageConstant;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jpeg2000Image extends Image {

    public static class Parameters {
        public int numOfComps;
        public List<ColorSpecBox> colorSpecBoxes = null;
        public boolean isJp2 = false;
        public boolean isJpxBaseline = false;
        public byte[] bpcBoxData;
    }

    public static class ColorSpecBox extends ArrayList<Integer> {
        private byte[] colorProfile;

        public int getMeth() {
            return get(0);
        }

        public int getPrec() {
            return get(1);
        }

        public int getApprox() {
            return get(2);
        }

        public int getEnumCs() {
            return get(3);
        }

        public byte[] getColorProfile() {
            return colorProfile;
        }

        void setColorProfile(byte[] colorProfile) {
            this.colorProfile = colorProfile;
        }
    }

    protected Parameters params;

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

    public Jpeg2000Image.Parameters getParameters() {
        return params;
    }
}
