package com.itextpdf.core.pdf.filters;

import com.itextpdf.core.pdf.PdfName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates filter behavior for PDF streams.  Classes generally interace with this
 * using the static getDefaultFilterHandlers() method, then obtain the desired {@link FilterHandler}
 * via a lookup.
 */
// Dev note:  we eventually want to refactor PdfReader so all of the existing filter functionality is moved into this class
// it may also be better to split the sub-classes out into a separate package
public final class FilterHandlers {

    /** The default {@link FilterHandler}s used by iText */
    private static final Map<PdfName, FilterHandler> defaults;
    static {
        HashMap<PdfName, FilterHandler> map = new HashMap<PdfName, FilterHandler>();

        map.put(PdfName.FlateDecode, new FlateDecodeFilter());
        map.put(PdfName.FL, new FlateDecodeFilter());
        map.put(PdfName.ASCIIHexDecode, new ASCIIHexDecodeFilter());
        map.put(PdfName.AHx, new ASCIIHexDecodeFilter());
        map.put(PdfName.ASCII85Decode, new ASCII85DecodeFilter());
        map.put(PdfName.A85, new ASCII85DecodeFilter());
        map.put(PdfName.LZWDecode, new LZWDecodeFilter());
        map.put(PdfName.CCITTFaxDecode, new CCITTFaxDecodeFilter());
        map.put(PdfName.Crypt, new DoNothingFilter());
        map.put(PdfName.RunLengthDecode, new RunLengthDecodeFilter());

        map.put(PdfName.JBIG2Decode, new DoNothingFilter());
        map.put(PdfName.DCTDecode, new DoNothingFilter());
        map.put(PdfName.JPXDecode, new DoNothingFilter());

        defaults = Collections.unmodifiableMap(map);
    }

    /**
     * @return the default {@link FilterHandler}s used by iText
     */
    public static Map<PdfName, FilterHandler> getDefaultFilterHandlers(){
        return defaults;
    }
}
