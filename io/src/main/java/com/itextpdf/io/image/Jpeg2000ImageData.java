/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.image;

import com.itextpdf.commons.logs.LazyLogger;
import com.itextpdf.io.logs.IoLogMessageConstant;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Image data and parsed metadata for a JPEG 2000 image.
 */
public class Jpeg2000ImageData extends ImageData {

    /**
     * Holds metadata parsed from a JPEG 2000 codestream or JP2 container.
     */
    public static class Parameters {
        private int numOfComps;
        private List<ColorSpecBox> colorSpecBoxes = null;
        private boolean isJp2 = false;
        private boolean isJpxBaseline = false;
        private byte[] bpcBoxData;

        /**
         * Retrieves number of components of the object.
         *
         * @return number of components
         */
        public int getNumOfComps() {
            return numOfComps;
        }

        /**
         * Sets number of components of the object.
         *
         * @param numOfComps number of components
         */
        public void setNumOfComps(int numOfComps) {
            this.numOfComps = numOfComps;
        }

        /**
         * Retrieves the color spec boxes of the object.
         *
         * @return color spec boxes
         */
        public List<ColorSpecBox> getColorSpecBoxes() {
            return colorSpecBoxes;
        }

        /**
         * Sets the color spec boxes of the object.
         *
         * @param colorSpecBoxes color spec boxes
         */
        public void setColorSpecBoxes(List<ColorSpecBox> colorSpecBoxes) {
            this.colorSpecBoxes = colorSpecBoxes;
        }

        /**
         * Retrieves whether the object is a Jp2.
         *
         * @return true if it is a jp2, otherwise false
         */
        public boolean isJp2() {
            return isJp2;
        }

        /**
         * Sets whether the object is a jp2.
         *
         * @param jp2 true is it is a jp2, otherwise false
         */
        public void setJp2(boolean jp2) {
            isJp2 = jp2;
        }

        /**
         * Retrieves whether jpx is baseline.
         *
         * @return true if jpx is baseline, false otherwise
         */
        public boolean isJpxBaseline() {
            return isJpxBaseline;
        }

        /**
         * Sets whether jpx is baseline.
         *
         * @param jpxBaseline true if jpx is baseline, false otherwise
         */
        public void setJpxBaseline(boolean jpxBaseline) {
            isJpxBaseline = jpxBaseline;
        }

        /**
         * Retrieves the bits per component of the box data.
         *
         * @return bits per component
         */
        public byte[] getBpcBoxData() {
            return bpcBoxData;
        }

        /**
         * Sets the bits per component of the box data.
         *
         * @param bpcBoxData bits per component
         */
        public void setBpcBoxData(byte[] bpcBoxData) {
            this.bpcBoxData = bpcBoxData;
        }
    }

    /**
     * Represents a JPEG 2000 color specification box.
     *
     * <p>
     * The first four list values are the method, precedence, approximation, and enumerated color space.
     */
    public static class ColorSpecBox extends ArrayList<Integer> {
        
		
		private byte[] colorProfile;

        /**
         * Gets the color-specification method.
         *
         * @return method value stored at index {@code 0}
         */
        public int getMeth() {
            return (int) get(0);
        }

        /**
         * Gets the color-specification precedence.
         *
         * @return precedence value stored at index {@code 1}
         */
        public int getPrec() {
            return (int) get(1);
        }

        /**
         * Gets the color-specification approximation.
         *
         * @return approximation value stored at index {@code 2}
         */
        public int getApprox() {
            return (int) get(2);
        }

        /**
         * Gets the enumerated color space.
         *
         * @return color-space value stored at index {@code 3}
         */
        public int getEnumCs() {
            return (int) get(3);
        }

        /**
         * Gets the embedded color profile.
         *
         * @return retained profile bytes, or {@code null}
         */
        public byte[] getColorProfile() {
            return colorProfile;
        }

        void setColorProfile(byte[] colorProfile) {
            this.colorProfile = colorProfile;
        }
    }

    /** Parsed JPEG 2000 parameters, or {@code null} before processing. */
    protected Parameters parameters;

    /**
     * Creates JPEG 2000 image data to be loaded from a URL.
     *
     * @param url source URL, not {@code null}
     */
    protected Jpeg2000ImageData(URL url) {
        super(url, ImageType.JPEG2000);
    }

    /**
     * Creates JPEG 2000 image data from encoded bytes.
     *
     * @param bytes encoded JPEG 2000 bytes; the array is retained
     */
    protected Jpeg2000ImageData(byte[] bytes) {
        super(bytes, ImageType.JPEG2000);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code false}, because JPEG 2000 images require a JPXDecode filter
     */
    @Override
    public boolean canImageBeInline() {
        LazyLogger logger = new LazyLogger(ImageData.class);
        logger.warn(() -> IoLogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER);
        return false;
    }

    /**
     * Gets metadata parsed from the JPEG 2000 image.
     *
     * @return parsed parameters, or {@code null} before processing
     */
    public Jpeg2000ImageData.Parameters getParameters() {
        return parameters;
    }
}
