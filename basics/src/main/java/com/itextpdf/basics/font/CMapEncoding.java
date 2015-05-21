package com.itextpdf.basics.font;

import com.itextpdf.basics.font.cmap.CMapByteCid;
import com.itextpdf.basics.font.cmap.CMapCidByte;
import com.itextpdf.basics.font.cmap.CMapCidUni;
import com.itextpdf.basics.font.cmap.CMapUniCid;


import java.util.ArrayList;
import java.util.HashMap;

public class CMapEncoding {

    private String cmap;
    private String uniMap;


    // true if CMap is Identity-H/V
    boolean isDirect;

    private CMapCidUni cid2Uni;
    private CMapUniCid uni2Cid;
    private CMapCidByte cid2Byte;

    private CMapByteCid byte2Cid;

    private final byte[] EMPTY = {};

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

    public String getUniMapName() {
        return uniMap;
    }

    public String getCmapName() {
        return cmap;
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
