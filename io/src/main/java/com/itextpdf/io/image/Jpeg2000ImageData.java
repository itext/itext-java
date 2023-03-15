/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.logs.IoLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Jpeg2000ImageData extends ImageData {

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
            return (int) get(0);
        }

        public int getPrec() {
            return (int) get(1);
        }

        public int getApprox() {
            return (int) get(2);
        }

        public int getEnumCs() {
            return (int) get(3);
        }

        public byte[] getColorProfile() {
            return colorProfile;
        }

        void setColorProfile(byte[] colorProfile) {
            this.colorProfile = colorProfile;
        }
    }

    protected Parameters parameters;

    protected Jpeg2000ImageData(URL url) {
        super(url, ImageType.JPEG2000);
    }

    protected Jpeg2000ImageData(byte[] bytes) {
        super(bytes, ImageType.JPEG2000);
    }

    @Override
    public boolean canImageBeInline() {
        Logger logger = LoggerFactory.getLogger(ImageData.class);
        logger.warn(IoLogMessageConstant.IMAGE_HAS_JPXDECODE_FILTER);
        return false;
    }

    public Jpeg2000ImageData.Parameters getParameters() {
        return parameters;
    }
}
