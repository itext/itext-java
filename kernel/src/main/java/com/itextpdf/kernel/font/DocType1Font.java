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
package com.itextpdf.kernel.font;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DocType1Font extends Type1Font implements IDocFontProgram {

    private static final long serialVersionUID = 6260280563455951912L;

    private PdfStream fontFile;
    private PdfName fontFileName;
    private PdfName subtype;
    private int missingWidth = 0;

    private DocType1Font(String fontName) {
        super(fontName);
    }

    static Type1Font createFontProgram(PdfDictionary fontDictionary, FontEncoding fontEncoding, CMapToUnicode toUnicode) {
        PdfName baseFontName = fontDictionary.getAsName(PdfName.BaseFont);
        String baseFont;
        if (baseFontName != null) {
            baseFont = baseFontName.getValue();
        } else {
            baseFont = FontUtil.createRandomFontName();
        }
        if (!fontDictionary.containsKey(PdfName.FontDescriptor)) {
            Type1Font type1StdFont;
            try {
                //if there are no font modifiers, cached font could be used,
                //otherwise a new instance should be created.
                type1StdFont = (Type1Font) FontProgramFactory.createFont(baseFont, true);
            } catch (Exception e) {
                type1StdFont = null;
            }
            if (type1StdFont != null) {
                return type1StdFont;
            }
        }
        DocType1Font fontProgram = new DocType1Font(baseFont);
        PdfDictionary fontDesc = fontDictionary.getAsDictionary(PdfName.FontDescriptor);
        fontProgram.subtype = fontDesc != null ? fontDesc.getAsName(PdfName.Subtype) : null;
        fillFontDescriptor(fontProgram, fontDesc);

        PdfNumber firstCharNumber = fontDictionary.getAsNumber(PdfName.FirstChar);
        int firstChar = firstCharNumber != null ? Math.max(firstCharNumber.intValue(), 0) : 0;
        int[] widths = FontUtil.convertSimpleWidthsArray(fontDictionary.getAsArray(PdfName.Widths), firstChar, fontProgram.getMissingWidth());
        fontProgram.avgWidth = 0;
        int glyphsWithWidths = 0;
        for (int i = 0; i < 256; i++) {
            Glyph glyph = new Glyph(i, widths[i], fontEncoding.getUnicode(i));
            fontProgram.codeToGlyph.put(i, glyph);
            if (glyph.hasValidUnicode()) {
                //FontEncoding.codeToUnicode table has higher priority
                if (fontEncoding.convertToByte(glyph.getUnicode()) == i) {
                    fontProgram.unicodeToGlyph.put(glyph.getUnicode(), glyph);
                }
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

    static void fillFontDescriptor(DocType1Font font, PdfDictionary fontDesc) {
        if (fontDesc == null) {
            Logger logger = LoggerFactory.getLogger(FontUtil.class);
            logger.warn(LogMessageConstant.FONT_DICTIONARY_WITH_NO_FONT_DESCRIPTOR);
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
                font.setTypoAscender((int) (maxAscent * 1000 / (maxAscent - minDescent)));
                font.setTypoDescender((int) (minDescent * 1000 / (maxAscent - minDescent)));
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
}
