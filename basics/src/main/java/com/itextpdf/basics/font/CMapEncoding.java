package com.itextpdf.basics.font;

import com.itextpdf.basics.IOException;
import com.itextpdf.basics.util.Utilities;
import com.itextpdf.basics.font.cmap.CMapByteCid;
import com.itextpdf.basics.font.cmap.CMapCidByte;
import com.itextpdf.basics.font.cmap.CMapCidUni;
import com.itextpdf.basics.font.cmap.CMapUniCid;

import java.io.ByteArrayOutputStream;


public class CMapEncoding {

    private String cmap;
    private String uniMap;

    // true if CMap is Identity-H/V
    private boolean isDirect;

    private boolean directTextToBytes;

    private CMapCidUni cid2Uni;
    private CMapUniCid uni2Cid;
    private CMapCidByte cid2Byte;

    private CMapByteCid byte2Cid;

    private final byte[] EMPTY = {};

    /**
     *
     * @param cmap CMap name.
     */
    public CMapEncoding(String cmap) {
        this.cmap = cmap;
        if (cmap.equals(PdfEncodings.IDENTITY_H) || cmap.equals(PdfEncodings.IDENTITY_V)) {
            isDirect = true;
        }
    }

    /**
     *
     * @param cmap CMap name.
     * @param uniMap CMap to convert Unicode value to CID.
     */
    public CMapEncoding(String cmap, String uniMap) {
        this.cmap = cmap;
        this.uniMap = uniMap;
        if (cmap.equals(PdfEncodings.IDENTITY_H) || cmap.equals(PdfEncodings.IDENTITY_V)) {
            cid2Uni = FontCache.getCid2UniCmap(uniMap);
            isDirect = true;
        } else {
            cid2Byte = FontCache.getCid2Byte(cmap);
            uni2Cid = FontCache.getUni2CidCmap(uniMap);
        }
    }

    public CMapEncoding(byte[] cmap) {
        throw new UnsupportedOperationException("Reserved for document font.");
    }

    public boolean isDirect() {
        return isDirect;
    }

    public boolean hasUniMap() {
        return uniMap != null && uniMap.length() > 0;
    }

    public String getRegistry() {
        if (isDirect()) {
            return  cid2Uni.getRegistry();
        } else {
            return cid2Byte.getRegistry();
        }
    }

    public String getOrdering() {
        if (isDirect()) {
            return  cid2Uni.getOrdering();
        } else {
            return cid2Byte.getOrdering();
        }
    }

    public int getSupplement() {
        if (isDirect()) {
            return  cid2Uni.getSupplement();
        } else {
            return cid2Byte.getSupplement();
        }
    }

    public String getUniMapName() {
        return uniMap;
    }

    public String getCmapName() {
        return cmap;
    }

    public int getCidCode(int c) {
        if (isDirect) {
            return c;
        } else {
            return uni2Cid.lookup(c);
        }
    }

    public byte[] convertToBytes(String text) {
        if (isDirect) {
            if (directTextToBytes) {
                return PdfEncodings.convertToBytes(text, null);
            } else {
                return PdfEncodings.convertToBytes(text, PdfEncodings.UnicodeBigUnmarked);
            }
        }
        try {
            if (text.length() == 1)
                return convertToBytes((int)text.charAt(0));
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            for (int k = 0; k < text.length(); ++k) {
                int val;
                if (Utilities.isSurrogatePair(text, k)) {
                    val = Utilities.convertToUtf32(text, k);
                    k++;
                } else {
                    val = text.charAt(k);
                }
                bout.write(convertToBytes(val));
            }
            return bout.toByteArray();
        }
        catch (Exception ex) {
            throw new IOException("CMapEncoding", ex);
        }
    }

    /**
     * Converts a <CODE>char</CODE> to a </CODE>byte</CODE> array according
     * to the font's encoding.
     * @param ch the <CODE>char</CODE> to be converted
     * @return an array of <CODE>byte</CODE> representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(int ch) {
        if (isDirect) {
            if (directTextToBytes) {
                return PdfEncodings.convertToBytes((char)ch, null);
            } else {
                return PdfEncodings.convertToBytes((char)ch, PdfEncodings.UnicodeBigUnmarked);
            }
        }
        return cid2Byte.lookup(uni2Cid.lookup(ch));
    }

    public CMapCidUni getCid2Uni() {
        return cid2Uni;
    }

    public CMapUniCid getUni2Cid() {
        return uni2Cid;
    }

    public CMapCidByte getCid2Byte() {
        return cid2Byte;
    }

    public CMapByteCid getByte2Cid() {
        return byte2Cid;
    }
}
