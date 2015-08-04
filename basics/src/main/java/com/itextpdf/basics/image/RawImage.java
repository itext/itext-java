package com.itextpdf.basics.image;

import java.net.URL;

public class RawImage extends Image {

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

    private byte[] rawData;

    protected RawImage(URL url, int type) {
        super(url, type);
    }

    protected RawImage(byte[] bytes, int type) {
        super(bytes, type);
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
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
