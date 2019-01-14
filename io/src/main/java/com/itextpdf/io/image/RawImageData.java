/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
