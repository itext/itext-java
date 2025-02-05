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
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;

public class PdfType1Font extends PdfSimpleFont<Type1Font> {


    PdfType1Font(Type1Font type1Font, String encoding, boolean embedded) {
        super();
        setFontProgram(type1Font);
        this.embedded = embedded && !type1Font.isBuiltInFont();
        if ((encoding == null || encoding.length() == 0) && type1Font.isFontSpecific()) {
            encoding = FontEncoding.FONT_SPECIFIC;
        }
        if (encoding != null && FontEncoding.FONT_SPECIFIC.toLowerCase().equals(encoding.toLowerCase())) {
            fontEncoding = FontEncoding.createFontSpecificEncoding();
        } else {
            fontEncoding = FontEncoding.createFontEncoding(encoding);
        }
    }

    PdfType1Font(Type1Font type1Font, String encoding) {
        this(type1Font, encoding, false);
    }

    PdfType1Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        newFont = false;
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), toUnicode);
        fontProgram = DocType1Font.createFontProgram(fontDictionary, fontEncoding, toUnicode);

        if (fontProgram instanceof IDocFontProgram) {
            embedded = ((IDocFontProgram) fontProgram).getFontFile() != null;
        }
        subset = false;
    }

    @Override
    public boolean isSubset() {
        return subset;
    }

    @Override
    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    @Override
    public void flush() {
        if (isFlushed()) return;
        ensureUnderlyingObjectHasIndirectReference();
        if (newFont) {
            flushFontData(fontProgram.getFontNames().getFontName(), PdfName.Type1);
        }
        super.flush();
    }

    @Override
    public Glyph getGlyph(int unicode) {
        if (fontEncoding.canEncode(unicode)) {
            Glyph glyph;
            if (fontEncoding.isFontSpecific()) {
                glyph = getFontProgram().getGlyphByCode(unicode);
            } else {
                glyph = getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode));
                if (glyph == null && (glyph = notdefGlyphs.get(unicode)) == null) {
                    // Handle special layout characters like sfthyphen (00AD).
                    // This glyphs will be skipped while converting to bytes
                    glyph = new Glyph(-1, 0, unicode);
                    notdefGlyphs.put(unicode, glyph);
                }
            }
            return glyph;
        }
        return null;
    }

    @Override
    public boolean containsGlyph(int unicode) {
        if (fontEncoding.canEncode(unicode)) {
            if (fontEncoding.isFontSpecific()) {
                return getFontProgram().getGlyphByCode(unicode) != null;
            } else {
                return getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode)) != null;
            }
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isBuiltInFont() {
        return ((Type1Font) getFontProgram()).isBuiltInFont();
    }

    /**
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     */
    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
        if (embedded) {
            if (fontProgram instanceof IDocFontProgram) {
                IDocFontProgram docType1Font = (IDocFontProgram) fontProgram;
                fontDescriptor.put(docType1Font.getFontFileName(),
                        docType1Font.getFontFile());
                docType1Font.getFontFile().flush();
                if (docType1Font.getSubtype() != null) {
                    fontDescriptor.put(PdfName.Subtype, docType1Font.getSubtype());
                }
            } else {
                byte[] fontStreamBytes = ((Type1Font) getFontProgram()).getFontStreamBytes();
                if (fontStreamBytes != null) {
                    PdfStream fontStream = new PdfStream(fontStreamBytes);
                    int[] fontStreamLengths = ((Type1Font) getFontProgram()).getFontStreamLengths();
                    for (int k = 0; k < fontStreamLengths.length; ++k) {
                        fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
                    }
                    fontDescriptor.put(PdfName.FontFile, fontStream);
                    if (makeObjectIndirect(fontStream)) {
                        fontStream.flush();
                    }
                }
            }
        }
    }
}
