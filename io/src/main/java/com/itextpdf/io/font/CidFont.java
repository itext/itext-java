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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.cmap.CMapUniCid;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.util.IntHashtable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

public class CidFont extends FontProgram {

    private String fontName;
	private int pdfFontFlags;
    private Set<String> compatibleCmaps;

    CidFont(String fontName, String cmap, Set<String> compatibleCmaps) {
        this.fontName = fontName;
        this.compatibleCmaps = compatibleCmaps;
        fontNames = new FontNames();
        initializeCidFontNameAndStyle(fontName);
        Map<String, Object> fontDesc = CidFontProperties.getAllFonts().get(fontNames.getFontName());
        if (fontDesc == null) {
            throw new IOException("There is no such predefined font: {0}").setMessageParams(fontName);
        }
        initializeCidFontProperties(fontDesc, cmap);
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

    private void initializeCidFontProperties(Map<String, Object> fontDesc, String cmap) {
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
        String uniMap = getCompatibleUniMap(registry, cmap);
        if (uniMap != null) {
            IntHashtable metrics = (IntHashtable) fontDesc.get("W");
            CMapUniCid uni2cid = CjkResourceLoader.getUni2CidCmap(uniMap);
            avgWidth = 0;
            for (int cp: uni2cid.getCodePoints()) {
                int cid = uni2cid.lookup(cp);
                int width = metrics.containsKey(cid) ? metrics.get(cid) : DEFAULT_WIDTH;
                Glyph glyph = new Glyph(cid, width, cp);
                avgWidth += glyph.getWidth();
                codeToGlyph.put(cid, glyph);
                unicodeToGlyph.put(cp, glyph);
            }
            fixSpaceIssue();
            if (codeToGlyph.size() != 0) {
                avgWidth /= codeToGlyph.size();
            }
        }
    }

    private static String getCompatibleUniMap(String registry, String cmap) {
        Set<String> compatibleUniMaps = CidFontProperties.getRegistryNames().get(registry + "_Uni");
        // 'cmap != null &&' part here is for autoport
        if (cmap != null && compatibleUniMaps.contains(cmap)) {
            return cmap;
        }

        String uniMap = "";
        for (String name : compatibleUniMaps) {
            uniMap = name;
            if (name.endsWith("H")) {
                uniMap = name;
                break;
            }
        }

        return uniMap;
    }
}
