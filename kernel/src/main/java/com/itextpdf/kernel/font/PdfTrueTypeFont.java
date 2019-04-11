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
import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Note. For TrueType FontNames.getStyle() is the same to Subfamily(). So, we shouldn't add style to /BaseFont.
 */
public class PdfTrueTypeFont extends PdfSimpleFont<TrueTypeFont> {

    private static final long serialVersionUID = -8152778382960290571L;

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
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), toUnicode);
        fontProgram = DocTrueTypeFont.createFontProgram(fontDictionary, fontEncoding, toUnicode);
        embedded = ((IDocFontProgram) fontProgram).getFontFile() != null;
        subset = false;
    }

    @Override
    public Glyph getGlyph(int unicode) {
        if (fontEncoding.canEncode(unicode)) {
            Glyph glyph = getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode));
            //TODO TrueType what if font is specific?
            if (glyph == null && (glyph = notdefGlyphs.get(unicode)) == null) {
                Glyph notdef = getFontProgram().getGlyphByCode(0);
                if (notdef != null) {
                    glyph = new Glyph(getFontProgram().getGlyphByCode(0), unicode);
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
        if (isFlushed()) return;
        ensureUnderlyingObjectHasIndirectReference();
        //TODO make subtype class member and simplify this method
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

    /**
     * @deprecated use {@link TrueTypeFont#updateUsedGlyphs(SortedSet, boolean, List)}
     */
    @Deprecated
    protected void addRangeUni(Set<Integer> longTag) {
        ((TrueTypeFont) getFontProgram()).updateUsedGlyphs((SortedSet<Integer>)longTag, subset, subsetRanges);
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
                for (int k = 0; k < shortTag.length; k++) {
                    if (shortTag[k] != 0) {
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
}
