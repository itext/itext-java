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
import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note. For TrueType FontNames.getStyle() is the same to Subfamily(). So, we shouldn't add style to /BaseFont.
 */
public class PdfTrueTypeFont extends PdfSimpleFont<TrueTypeFont> {


    PdfTrueTypeFont(TrueTypeFont ttf, String encoding, boolean embedded) {
        super();
        setFontProgram(ttf);
        this.embedded = embedded;
        FontNames fontNames = ttf.getFontNames();
        if (embedded && !fontNames.allowEmbedding()) {
            throw new PdfException("{0} cannot be embedded due to licensing restrictions.")
                    .setMessageParams(fontNames.getFontName());
        }
        if ((encoding == null || encoding.length() == 0) && ttf.isFontSpecific()) {
            encoding = FontEncoding.FONT_SPECIFIC;
        }
        if (encoding != null && FontEncoding.FONT_SPECIFIC.toLowerCase().equals(encoding.toLowerCase())) {
            fontEncoding = FontEncoding.createFontSpecificEncoding();
        } else {
            fontEncoding = FontEncoding.createFontEncoding(encoding);
        }
    }

    PdfTrueTypeFont(PdfDictionary fontDictionary) {
        super(fontDictionary);
        newFont = false;
        subset = false;
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), toUnicode);

        PdfName baseFontName = fontDictionary.getAsName(PdfName.BaseFont);
        // Section 9.6.3 (ISO-32000-1): A TrueType font dictionary may contain the same entries as a Type 1 font
        // dictionary (see Table 111), with these differences...
        // Section 9.6.2.2. (ISO-32000-1) associate standard fonts with Type1 fonts but there does not
        // seem to be a strict requirement on the subtype
        // Cases when a font with /TrueType subtype has base font which is one of the Standard 14 fonts
        // does not seem to be forbidden and it's handled by many PDF tools, so we handle it here as well
        if (baseFontName != null && StandardFonts.isStandardFont(baseFontName.getValue())
                && !fontDictionary.containsKey(PdfName.FontDescriptor) && !fontDictionary.containsKey(PdfName.Widths)) {
            try {
                fontProgram = FontProgramFactory.createFont(baseFontName.getValue(), true);
            } catch (IOException e) {
                throw new PdfException(KernelExceptionMessageConstant.IO_EXCEPTION_WHILE_CREATING_FONT, e);
            }
        } else {
            fontProgram = DocTrueTypeFont.createFontProgram(fontDictionary, fontEncoding, toUnicode);
        }

        embedded = fontProgram instanceof IDocFontProgram && ((IDocFontProgram) fontProgram).getFontFile() != null;
    }

    @Override
    public Glyph getGlyph(int unicode) {
        if (fontEncoding.canEncode(unicode)) {
            Glyph glyph = getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode));
            if (glyph == null && (glyph = notdefGlyphs.get(unicode)) == null) {
                final Glyph notdef = getFontProgram().getGlyphByCode(0);
                if (notdef != null) {
                    glyph = new Glyph(notdef, unicode);
                    notdefGlyphs.put(unicode, glyph);
                }
            }
            return glyph;
        }
        return null;
    }

    @Override
    public boolean containsGlyph(int unicode) {
        if (fontEncoding.isFontSpecific()) {
            return fontProgram.getGlyphByCode(unicode) != null;
        } else {
            return fontEncoding.canEncode(unicode)
                    && getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode)) != null;
        }
    }

    @Override
    public void flush() {
        if (isFlushed()) {
            return;
        }
        ensureUnderlyingObjectHasIndirectReference();
        if (newFont) {
            PdfName subtype;
            String fontName;
            if (((TrueTypeFont) getFontProgram()).isCff()) {
                subtype = PdfName.Type1;
                fontName = fontProgram.getFontNames().getFontName();
            } else {
                subtype = PdfName.TrueType;
                fontName = updateSubsetPrefix(fontProgram.getFontNames().getFontName(), subset, embedded);
            }
            flushFontData(fontName, subtype);
        }
        super.flush();
    }

    @Override
    public boolean isBuiltWith(String fontProgram, String encoding) {
        // Now Identity-H is default for true type fonts. However, in case of Identity-H the method from
        // PdfType0Font would be triggered, hence we need to return false there.
        return null != encoding && !"".equals(encoding) && super.isBuiltWith(fontProgram, encoding);
    }

    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
        if (embedded) {
            PdfName fontFileName;
            PdfStream fontStream;
            if (fontProgram instanceof IDocFontProgram) {
                fontFileName = ((IDocFontProgram) fontProgram).getFontFileName();
                fontStream = ((IDocFontProgram) fontProgram).getFontFile();
            } else if (((TrueTypeFont) getFontProgram()).isCff()) {
                fontFileName = PdfName.FontFile3;
                try {
                    byte[] fontStreamBytes = ((TrueTypeFont) getFontProgram()).getFontStreamBytes();
                    fontStream = getPdfFontStream(fontStreamBytes, new int[]{fontStreamBytes.length});
                    fontStream.put(PdfName.Subtype, new PdfName("Type1C"));
                } catch (PdfException e) {
                    Logger logger = LoggerFactory.getLogger(PdfTrueTypeFont.class);
                    logger.error(e.getMessage());
                    fontStream = null;
                }
            } else {
                fontFileName = PdfName.FontFile2;
                SortedSet<Integer> glyphs = new TreeSet<>();
                for (int k = 0; k < usedGlyphs.length; k++) {
                    if (usedGlyphs[k] != 0) {
                        int uni = fontEncoding.getUnicode(k);
                        Glyph glyph = uni > -1 ? fontProgram.getGlyph(uni) : fontProgram.getGlyphByCode(k);
                        if (glyph != null) {
                            glyphs.add(glyph.getCode());
                        }
                    }
                }
                ((TrueTypeFont) getFontProgram()).updateUsedGlyphs(glyphs, subset, subsetRanges);
                try {
                    byte[] fontStreamBytes;
                    //getDirectoryOffset() > 0 means ttc, which shall be subset anyway.
                    if (subset || ((TrueTypeFont) getFontProgram()).getDirectoryOffset() > 0) {
                        fontStreamBytes = ((TrueTypeFont) getFontProgram()).getSubset(glyphs, subset);
                    } else {
                        fontStreamBytes = ((TrueTypeFont) getFontProgram()).getFontStreamBytes();
                    }
                    fontStream = getPdfFontStream(fontStreamBytes, new int[]{fontStreamBytes.length});
                } catch (PdfException e) {
                    Logger logger = LoggerFactory.getLogger(PdfTrueTypeFont.class);
                    logger.error(e.getMessage());
                    fontStream = null;
                }
            }
            if (fontStream != null) {
                fontDescriptor.put(fontFileName, fontStream);
                if (fontStream.getIndirectReference() != null) {
                    fontStream.flush();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isBuiltInFont() {
        return fontProgram instanceof Type1Font && ((Type1Font) fontProgram).isBuiltInFont();
    }
}
