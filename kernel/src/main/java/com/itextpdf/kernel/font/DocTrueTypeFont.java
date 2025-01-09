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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocTrueTypeFont extends TrueTypeFont implements IDocFontProgram {


    private PdfStream fontFile;
    private PdfName fontFileName;
    private PdfName subtype;
    private int missingWidth = 0;

    private DocTrueTypeFont(PdfDictionary fontDictionary) {
        super();
        PdfName baseFontName = fontDictionary.getAsName(PdfName.BaseFont);
        if (baseFontName != null) {
            setFontName(baseFontName.getValue());
        } else {
            setFontName(FontUtil.createRandomFontName());
        }
        subtype = fontDictionary.getAsName(PdfName.Subtype);
    }

    static TrueTypeFont createFontProgram(PdfDictionary fontDictionary, FontEncoding fontEncoding, CMapToUnicode toUnicode) {
        DocTrueTypeFont fontProgram = new DocTrueTypeFont(fontDictionary);
        fillFontDescriptor(fontProgram, fontDictionary.getAsDictionary(PdfName.FontDescriptor));

        PdfNumber firstCharNumber = fontDictionary.getAsNumber(PdfName.FirstChar);
        int firstChar = firstCharNumber != null ? Math.max(firstCharNumber.intValue(), 0) : 0;
        int[] widths = FontUtil.convertSimpleWidthsArray(fontDictionary.getAsArray(PdfName.Widths), firstChar,
                fontProgram.getMissingWidth());
        fontProgram.avgWidth = 0;
        int glyphsWithWidths = 0;
        for (int i = 0; i < 256; i++) {
            Glyph glyph = new Glyph(i, widths[i], fontEncoding.getUnicode(i));
            fontProgram.codeToGlyph.put(i, glyph);
            //FontEncoding.codeToUnicode table has higher priority
            if (glyph.hasValidUnicode() && fontEncoding.convertToByte(glyph.getUnicode()) == i) {
                fontProgram.unicodeToGlyph.put(glyph.getUnicode(), glyph);
            } else if (toUnicode != null) {
                glyph.setChars(toUnicode.lookup(i));
            }
            if (widths[i] > 0) {
                glyphsWithWidths++;
                fontProgram.avgWidth += widths[i];
            }
        }
        if (glyphsWithWidths != 0) {
            fontProgram.avgWidth /= glyphsWithWidths;
        }
        return fontProgram;
    }

    static int getDefaultWithOfFont(PdfDictionary fontDictionary, PdfDictionary fontDescriptor) {
        int defaultWidth;
        if (fontDescriptor != null && fontDescriptor.containsKey(PdfName.DW)) {
            defaultWidth = (int) fontDescriptor.getAsInt(PdfName.DW);
        } else if (fontDictionary.containsKey(PdfName.DW)) {
            defaultWidth = (int) fontDictionary.getAsInt(PdfName.DW);
        } else {
            defaultWidth = DEFAULT_WIDTH;
        }
        return defaultWidth;
    }

    static TrueTypeFont createFontProgram(PdfDictionary fontDictionary, CMapToUnicode toUnicode) {
        DocTrueTypeFont fontProgram = new DocTrueTypeFont(fontDictionary);
        PdfDictionary fontDescriptor = fontDictionary.getAsDictionary(PdfName.FontDescriptor);
        fillFontDescriptor(fontProgram, fontDescriptor);
        final int defaultWidth = getDefaultWithOfFont(fontDictionary, fontDescriptor);
        IntHashtable widths = null;
        if (toUnicode != null) {
            widths = FontUtil.convertCompositeWidthsArray(fontDictionary.getAsArray(PdfName.W));
            fontProgram.avgWidth = 0;
            for (int cid : toUnicode.getCodes()) {
                final int width = widths.containsKey(cid) ? widths.get(cid) : defaultWidth;
                fontProgram.registerGlyph(cid, width, toUnicode.lookup(cid));
            }
            if (fontProgram.codeToGlyph.size() != 0) {
                fontProgram.avgWidth /= fontProgram.codeToGlyph.size();
            }
        }

        if (fontProgram.codeToGlyph.get(0) == null) {
            fontProgram.codeToGlyph.put(0,
                    new Glyph(0, widths != null && widths.containsKey(0) ? widths.get(0) : defaultWidth, -1));
        }
        return fontProgram;
    }

    @Override
    public PdfStream getFontFile() {
        return fontFile;
    }

    @Override
    public PdfName getFontFileName() {
        return fontFileName;
    }

    @Override
    public PdfName getSubtype() {
        return subtype;
    }

    /**
     * Returns false, because we cannot rely on an actual font subset and font name.
     *
     * @param fontName a font name or path to a font program
     * @return return false.
     */
    @Override
    public boolean isBuiltWith(String fontName) {
        return false;
    }

    public int getMissingWidth() {
        return missingWidth;
    }

    static void fillFontDescriptor(DocTrueTypeFont font, PdfDictionary fontDesc) {
        if (fontDesc == null) {
            Logger logger = LoggerFactory.getLogger(FontUtil.class);
            logger.warn(IoLogMessageConstant.FONT_DICTIONARY_WITH_NO_FONT_DESCRIPTOR);
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.Ascent);
        if (v != null) {
            font.setTypoAscender(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.Descent);
        if (v != null) {
            font.setTypoDescender(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.CapHeight);
        if (v != null) {
            font.setCapHeight(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.XHeight);
        if (v != null) {
            font.setXHeight(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.ItalicAngle);
        if (v != null) {
            font.setItalicAngle(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.StemV);
        if (v != null) {
            font.setStemV(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.StemH);
        if (v != null) {
            font.setStemH(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.FontWeight);
        if (v != null) {
            font.setFontWeight(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.MissingWidth);
        if (v != null) {
            font.missingWidth = v.intValue();
        }

        PdfName fontStretch = fontDesc.getAsName(PdfName.FontStretch);
        if (fontStretch != null) {
            font.setFontStretch(fontStretch.getValue());
        }

        PdfArray bboxValue = fontDesc.getAsArray(PdfName.FontBBox);
        if (bboxValue != null) {
            int[] bbox = new int[4];
            //llx
            bbox[0] = bboxValue.getAsNumber(0).intValue();
            //lly
            bbox[1] = bboxValue.getAsNumber(1).intValue();
            //urx
            bbox[2] = bboxValue.getAsNumber(2).intValue();
            //ury
            bbox[3] = bboxValue.getAsNumber(3).intValue();
            if (bbox[0] > bbox[2]) {
                int t = bbox[0];
                bbox[0] = bbox[2];
                bbox[2] = t;
            }
            if (bbox[1] > bbox[3]) {
                int t = bbox[1];
                bbox[1] = bbox[3];
                bbox[3] = t;
            }
            font.setBbox(bbox);

            // If ascender or descender in font descriptor are zero, we still want to get more or less correct valuee for
            // text extraction, stamping etc. Thus we rely on font bbox in this case
            if (font.getFontMetrics().getTypoAscender() == 0 && font.getFontMetrics().getTypoDescender() == 0) {
                float maxAscent = Math.max(bbox[3], font.getFontMetrics().getTypoAscender());
                float minDescent = Math.min(bbox[1], font.getFontMetrics().getTypoDescender());
                font.setTypoAscender(
                        (int) (FontProgram.convertGlyphSpaceToTextSpace(maxAscent) / (maxAscent - minDescent)));
                font.setTypoDescender(
                        (int) (FontProgram.convertGlyphSpaceToTextSpace(minDescent) / (maxAscent - minDescent)));
            }
        }

        PdfString fontFamily = fontDesc.getAsString(PdfName.FontFamily);
        if (fontFamily != null) {
            font.setFontFamily(fontFamily.getValue());
        }

        PdfNumber flagsValue = fontDesc.getAsNumber(PdfName.Flags);
        if (flagsValue != null) {
            int flags = flagsValue.intValue();
            if ((flags & 1) != 0) {
                font.setFixedPitch(true);
            }
            if ((flags & 262144) != 0) {
                font.setBold(true);
            }
        }

        PdfName[] fontFileNames = new PdfName[] {PdfName.FontFile, PdfName.FontFile2, PdfName.FontFile3};
        for (PdfName fontFile: fontFileNames) {
            if (fontDesc.containsKey(fontFile)) {
                font.fontFileName = fontFile;
                font.fontFile = fontDesc.getAsStream(fontFile);
                break;
            }
        }
    }

    private void registerGlyph(int cid, int width, char[] unicode) {
        Glyph glyph = new Glyph(cid, width, unicode);
        if (glyph.hasValidUnicode()) {
            this.unicodeToGlyph.put(glyph.getUnicode(), glyph);
        }
        this.codeToGlyph.put(cid, glyph);
        this.avgWidth += width;
    }
}
