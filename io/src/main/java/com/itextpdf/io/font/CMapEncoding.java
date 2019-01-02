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
package com.itextpdf.io.font;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.cmap.CMapCidByte;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.cmap.CMapLocationFromBytes;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.util.IntHashtable;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CMapEncoding implements Serializable {

    private static final List<byte[]> IDENTITY_H_V_CODESPACE_RANGES = Arrays.asList(new byte[] {0, 0}, new byte[] {(byte)0xff, (byte)0xff});

    private static final long serialVersionUID = 2418291066110642993L;
    private String cmap;
    private String uniMap;

    // true if CMap is Identity-H/V
    private boolean isDirect;

    private CMapCidUni cid2Uni;
    private CMapCidByte cid2Code;

    private IntHashtable code2Cid;

    private List<byte[]> codeSpaceRanges;

    /**
     *
     * @param cmap CMap name.
     */
    public CMapEncoding(String cmap) {
        this.cmap = cmap;
        if (cmap.equals(PdfEncodings.IDENTITY_H) || cmap.equals(PdfEncodings.IDENTITY_V)) {
            isDirect = true;
        }
        // Actually this constructor is only called for Identity-H/V cmaps currently.
        // Even for hypothetical case of non-Identity-H/V, let's use Identity-H/V ranges (two byte ranges) for compatibility with previous behavior
        this.codeSpaceRanges = IDENTITY_H_V_CODESPACE_RANGES;
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
            this.codeSpaceRanges = IDENTITY_H_V_CODESPACE_RANGES;
        } else {
            cid2Code = FontCache.getCid2Byte(cmap);
            code2Cid = cid2Code.getReversMap();
            this.codeSpaceRanges = cid2Code.getCodeSpaceRanges();
        }
    }

    public CMapEncoding(String cmap, byte[] cmapBytes) {
        this.cmap = cmap;
        cid2Code = new CMapCidByte();
        try {
            CMapParser.parseCid(cmap, cid2Code, new CMapLocationFromBytes(cmapBytes));
            code2Cid = cid2Code.getReversMap();
            this.codeSpaceRanges = cid2Code.getCodeSpaceRanges();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error(LogMessageConstant.FAILED_TO_PARSE_ENCODING_STREAM);
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

    /**
     * Checks whether the {@link CMapEncoding} was built with corresponding cmap name.
     *
     * @param cmap a CMAP
     * @return true, if the CMapEncoding was built with the cmap. Otherwise false.
     */
    public boolean isBuiltWith(String cmap) {
        return Objects.equals(cmap, this.cmap);
    }

    /**
     * @deprecated Will be removed in 7.2. Use {@link #getCmapBytes(int)} instead.
     */
    @Deprecated
    public int getCmapCode(int cid) {
        if (isDirect) {
            return cid;
        } else {
            return toInteger(cid2Code.lookup(cid));
        }
    }

    public byte[] getCmapBytes(int cid) {
        int length = getCmapBytesLength(cid);
        byte[] result = new byte[length];
        fillCmapBytes(cid, result, 0);
        return result;
    }

    public int fillCmapBytes(int cid, byte[] array, int offset) {
        if (isDirect) {
            array[offset++] = (byte)((cid & 0xff00) >> 8);
            array[offset++] = (byte)(cid & 0xff);
        } else {
            byte[] bytes = cid2Code.lookup(cid);
            for (int i = 0; i < bytes.length; i++) {
                array[offset++] = bytes[i];
            }
        }
        return offset;
    }

    public void fillCmapBytes(int cid, ByteBuffer buffer) {
        if (isDirect) {
            buffer.append((byte)((cid & 0xff00) >> 8));
            buffer.append((byte)(cid & 0xff));
        } else {
            byte[] bytes = cid2Code.lookup(cid);
            buffer.append(bytes);
        }
    }

    public int getCmapBytesLength(int cid) {
        if (isDirect) {
            return 2;
        } else {
            return cid2Code.lookup(cid).length;
        }
    }

    public int getCidCode(int cmapCode) {
        if (isDirect) {
            return cmapCode;
        } else {
            return code2Cid.get(cmapCode);
        }
    }

    public boolean containsCodeInCodeSpaceRange(int code, int length) {
        for (int i = 0; i < codeSpaceRanges.size(); i += 2) {
            if (length == codeSpaceRanges.get(i).length) {
                int mask = 0xff;
                int totalShift = 0;
                byte[] low = codeSpaceRanges.get(i);
                byte[] high = codeSpaceRanges.get(i + 1);
                boolean fitsIntoRange = true;
                for (int ind = length - 1; ind >= 0; ind--, totalShift += 8, mask <<= 8) {
                    int actualByteValue = (code & mask) >> totalShift;
                    if (!(actualByteValue >= (0xff & low[ind]) && actualByteValue <= (0xff & high[ind]))) {
                        fitsIntoRange = false;
                    }
                }
                if (fitsIntoRange) {
                    return true;
                }
            }
        }
        return false;
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
