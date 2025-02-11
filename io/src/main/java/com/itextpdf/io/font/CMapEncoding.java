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
package com.itextpdf.io.font;

import com.itextpdf.io.font.cmap.CMapCidToCodepoint;
import com.itextpdf.io.font.cmap.CMapCodepointToCid;
import com.itextpdf.io.font.cmap.CMapLocationFromBytes;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteBuffer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.slf4j.LoggerFactory;

public class CMapEncoding {

    private static final List<byte[]> IDENTITY_H_V_CODESPACE_RANGES = Arrays.asList(new byte[] {0, 0}, new byte[] {(byte)0xff, (byte)0xff});

    private final String cmap;
    private String uniMap;

    // true if CMap is Identity-H/V
    private boolean isDirect;

    private CMapCidToCodepoint cid2Code;

    private CMapCodepointToCid code2Cid;

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
            isDirect = true;
            this.codeSpaceRanges = IDENTITY_H_V_CODESPACE_RANGES;
        } else {
            cid2Code = CjkResourceLoader.getCidToCodepointCmap(cmap);
            code2Cid = CMapEncoding.getCodeToCidCmap(cmap, cid2Code);
            this.codeSpaceRanges = cid2Code.getCodeSpaceRanges();
        }
    }

    public CMapEncoding(String cmap, byte[] cmapBytes) {
        this.cmap = cmap;
        cid2Code = new CMapCidToCodepoint();
        try {
            CMapParser.parseCid(cmap, cid2Code, new CMapLocationFromBytes(cmapBytes));
            code2Cid = CMapEncoding.getCodeToCidCmap(cmap, cid2Code);
            this.codeSpaceRanges = cid2Code.getCodeSpaceRanges();
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error(IoLogMessageConstant.FAILED_TO_PARSE_ENCODING_STREAM);
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
            return code2Cid.lookup(cmapCode);
        }
    }

    public List<byte[]> getCodeSpaceRanges() {
        return codeSpaceRanges;
    }

    private static CMapCodepointToCid getCodeToCidCmap(String cmap, CMapCidToCodepoint cid2Code) {
        try {
            return CjkResourceLoader.getCodepointToCidCmap(cmap);
        } catch (com.itextpdf.io.exceptions.IOException ex) {
            // if not found, fall back to reversing
            return new CMapCodepointToCid(cid2Code);
        }
    }
}
