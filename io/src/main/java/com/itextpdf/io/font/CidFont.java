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

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.util.IntHashtable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

public class CidFont extends FontProgram {

    private static final long serialVersionUID = 5444988003799502179L;

    private String fontName;
	private int pdfFontFlags;
    private Set<String> compatibleCmaps;

    CidFont(String fontName, Set<String> cmaps) {
        this.fontName = fontName;
        compatibleCmaps = cmaps;
        fontNames = new FontNames();
        initializeCidFontNameAndStyle(fontName);
        Map<String, Object> fontDesc = CidFontProperties.getAllFonts().get(fontNames.getFontName());
        if (fontDesc == null) {
            throw new IOException("There is no such predefined font: {0}").setMessageParams(fontName);
        }
        initializeCidFontProperties(fontDesc);
    }

    CidFont(String fontName, Set<String> cmaps, Map<String, Object> fontDescription) {
        initializeCidFontNameAndStyle(fontName);
        initializeCidFontProperties(fontDescription);
        compatibleCmaps = cmaps;
    }

    public boolean compatibleWith(String cmap) {
        if (cmap.equals(PdfEncodings.IDENTITY_H) || cmap.equals(PdfEncodings.IDENTITY_V)) {
            return true;
        } else {
            return compatibleCmaps != null && compatibleCmaps.contains(cmap);
        }
    }

    @Override
    public int getKerning(Glyph glyph1, Glyph glyph2) {
        return 0;
    }

    @Override
    public int getPdfFontFlags() {
        return pdfFontFlags;
    }

    @Override
    public boolean isFontSpecific() {
        return false;
    }

    @Override
    public boolean isBuiltWith(String fontName) {
        return Objects.equals(this.fontName, fontName);
    }

    private void initializeCidFontNameAndStyle(String fontName) {
        String nameBase = trimFontStyle(fontName);
        if (nameBase.length() < fontName.length()) {
            fontNames.setFontName(fontName);
            fontNames.setStyle(fontName.substring(nameBase.length()));
        } else {
            fontNames.setFontName(fontName);
        }
        fontNames.setFullName(new String[][]{new String[]{"", "", "", fontNames.getFontName()}});
    }

    private void initializeCidFontProperties(Map<String, Object> fontDesc) {
        fontIdentification.setPanose((String) fontDesc.get("Panose"));
        fontMetrics.setItalicAngle(Integer.parseInt((String) fontDesc.get("ItalicAngle")));
        fontMetrics.setCapHeight(Integer.parseInt((String) fontDesc.get("CapHeight")));
        fontMetrics.setTypoAscender(Integer.parseInt((String) fontDesc.get("Ascent")));
        fontMetrics.setTypoDescender(Integer.parseInt((String) fontDesc.get("Descent")));
        fontMetrics.setStemV(Integer.parseInt((String) fontDesc.get("StemV")));
        pdfFontFlags = Integer.parseInt((String) fontDesc.get("Flags"));
        String fontBBox = (String) fontDesc.get("FontBBox");
        StringTokenizer tk = new StringTokenizer(fontBBox, " []\r\n\t\f");
        int llx = Integer.parseInt(tk.nextToken());
        int lly = Integer.parseInt(tk.nextToken());
        int urx = Integer.parseInt(tk.nextToken());
        int ury = Integer.parseInt(tk.nextToken());
        fontMetrics.updateBbox(llx, lly, urx, ury);
        registry = (String) fontDesc.get("Registry");
        String uniMap = getCompatibleUniMap(registry);
        if (uniMap != null) {
            IntHashtable metrics = (IntHashtable) fontDesc.get("W");
            CMapCidUni cid2Uni = FontCache.getCid2UniCmap(uniMap);
            avgWidth = 0;
            for (int cid : cid2Uni.getCids()) {
                int uni = cid2Uni.lookup(cid);
                int width = metrics.containsKey(cid) ? metrics.get(cid) : DEFAULT_WIDTH;
                Glyph glyph = new Glyph(cid, width, uni);
                avgWidth += glyph.getWidth();
                codeToGlyph.put(cid, glyph);
                unicodeToGlyph.put(uni, glyph);
            }
            fixSpaceIssue();
            if (codeToGlyph.size() != 0) {
                avgWidth /= codeToGlyph.size();
            }
        }
    }

    private static String getCompatibleUniMap(String registry) {
        String uniMap = "";
        for (String name : CidFontProperties.getRegistryNames().get(registry + "_Uni")) {
            uniMap = name;
            if (name.endsWith("H")) {
                break;
            }
        }
        return uniMap;
    }
}
