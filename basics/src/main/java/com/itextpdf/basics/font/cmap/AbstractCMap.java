package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.font.PdfEncodings;

import java.util.ArrayList;

/**
 * @author psoares
 */
public abstract class AbstractCMap {

    private String cmapName;
    private String registry;
    private String ordering;
    private int supplement;
    
    public String getName() {
        return cmapName;
    }

    void setName(String cmapName) {
        this.cmapName = cmapName;
    }
    
    public String getOrdering() {
        return ordering;
    }

    void setOrdering(String ordering) {
        this.ordering = ordering;
    }
    
    public String getRegistry() {
        return registry;
    }

    void setRegistry(String registry) {
        this.registry = registry;
    }
    
    public int getSupplement() {
        return supplement;
    }
    
    void setSupplement(int supplement) {
        this.supplement = supplement;
    }

    abstract void addChar(String mark, CMapObject code);
    
    void addRange(String from, String to, CMapObject code) {
        byte[] a1 = toByteArray(from);
        byte[] a2 = toByteArray(to);
        if (a1.length != a2.length || a1.length == 0)
            throw new IllegalArgumentException("Invalid map.");
        byte[] sout = null;
        if (code.isString()) {
            sout = toByteArray(code.toString());
        }
        int start = a1[a1.length - 1] & 0xff;
        int end = a2[a2.length - 1] & 0xff;
        for (int k = start; k <= end; ++k) {
            a1[a1.length - 1] = (byte)k;
            String mark = PdfEncodings.convertToString(a1, null);
            if (code.isArray()) {
                ArrayList<CMapObject> codes = (ArrayList<CMapObject>) code.getValue();
                addChar(mark, codes.get(k - start));
            } else if (code.isNumber()) {
                int nn = (int)code.getValue() + k - start;
                addChar(mark, new CMapObject(CMapObject.Number, nn));
            } else if (code.isString()) {
                CMapObject s1 = new CMapObject(CMapObject.HexString, sout);
                assert sout != null;
                ++sout[sout.length - 1];
                addChar(mark, s1);
            }
        }
    }
    
    protected static byte[] toByteArray(String value) {
        if (PdfEncodings.isPdfDocEncoding(value)) {
            return PdfEncodings.convertToBytes(value, PdfEncodings.PdfDocEncoding);
        } else {
            return PdfEncodings.convertToBytes(value, null);
        }
    }

    protected String toUnicodeString(String value, boolean isHexWriting) {
        byte[] bytes = toByteArray(value);
        if (isHexWriting) {
            return PdfEncodings.convertToString(bytes, "UnicodeBigUnmarked");
        } else {
            if (bytes.length >= 2 && bytes[0] == (byte)254 && bytes[1] == (byte)255) {
                return PdfEncodings.convertToString(bytes, PdfEncodings.UnicodeBig);
            } else {
                return PdfEncodings.convertToString(bytes, PdfEncodings.PdfDocEncoding);
            }
        }
    }
}