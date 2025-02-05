/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import java.net.URL;

public class RawImageData extends ImageData {

    /**
     * Pure two-dimensional encoding (Group 4)
     */
    public static final int CCITTG4 = 0x100;

    /**
     * Pure one-dimensional encoding (Group 3, 1-D)
     */
    public static final int CCITTG3_1D = 0x101;

    /**
     * Mixed one- and two-dimensional encoding (Group 3, 2-D)
     */
    public static final int CCITTG3_2D = 0x102;

    /**
     * A flag indicating whether 1-bits are to be interpreted as black pixels
     * and 0-bits as white pixels,
     */
    public static final int CCITT_BLACKIS1 = 1;

    /**
     * A flag indicating whether the filter expects extra 0-bits before each
     * encoded line so that the line begins on a byte boundary.
     */
    public static final int CCITT_ENCODEDBYTEALIGN = 2;

    /**
     * A flag indicating whether end-of-line bit patterns are required to be
     * present in the encoding.
     */
    public static final int CCITT_ENDOFLINE = 4;

    /**
     * A flag indicating whether the filter expects the encoded data to be
     * terminated by an end-of-block pattern, overriding the Rows parameter. The
     * use of this flag will set the key /EndOfBlock to false.
     */
    public static final int CCITT_ENDOFBLOCK = 8;

    //NOTE in itext5 instead of typeCcitt bpc property was using for both bpc and type CCITT.
    protected int typeCcitt;


    protected RawImageData(URL url, ImageType type) {
        super(url, type);
    }

    protected RawImageData(byte[] bytes, ImageType type) {
        super(bytes, type);
    }

    @Override
    public boolean isRawImage(){
        return true;
    }

    public int getTypeCcitt() {
        return typeCcitt;
    }

    public void setTypeCcitt(int typeCcitt) {
        this.typeCcitt = typeCcitt;
    }
}
