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

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;

public class PdfType1Font extends PdfSimpleFont<Type1Font> {

    private static final long serialVersionUID = 7009919945291639441L;

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
        // if there is no FontDescriptor, it is most likely one of the Standard Font with StandardEncoding as base encoding.
        // unused variable.
        // boolean fillStandardEncoding = !fontDictionary.containsKey(PdfName.FontDescriptor);
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
