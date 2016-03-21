package com.itextpdf.io.font;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.font.cmap.CMapCidByte;
import com.itextpdf.io.font.cmap.CMapCidUni;

public class CMapEncoding {

    private String cmap;
    private String uniMap;

    // true if CMap is Identity-H/V
    private boolean isDirect;

    private CMapCidUni cid2Uni;
    private CMapCidByte cid2Code;

    private IntHashtable code2Cid;

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
            cid2Code = FontCache.getCid2Byte(cmap);
            code2Cid = cid2Code.getReversMap();
        }
    }

    public boolean isDirect() {
        return isDirect;
    }

    public boolean hasUniMap() {
        return uniMap != null && uniMap.length() > 0;
    }

    public String getRegistry() {
        if (isDirect()) {
            return "Adobe";
        } else {
            return cid2Code.getRegistry();
        }
    }

    public String getOrdering() {
        if (isDirect()) {
            return "Identity";
        } else {
            return cid2Code.getOrdering();
        }
    }

    public int getSupplement() {
        if (isDirect()) {
            return 0;
        } else {
            return cid2Code.getSupplement();
        }
    }

    public String getUniMapName() {
        return uniMap;
    }

    public String getCmapName() {
        return cmap;
    }

    public int getCmapCode(int cid) {
        if (isDirect) {
            return cid;
        } else {
            return toInteger(cid2Code.lookup(cid));
        }
    }

    public int getCidCode(int cmapCode) {
        if (isDirect) {
            return cmapCode;
        } else {
            return code2Cid.get(cmapCode);
        }
    }

    private static int toInteger(byte[] bytes) {
        int result = 0;
        for (byte b : bytes) {
            result <<= 8;
            result += b & 0xff;
        }
        return result;
    }
}
