package com.itextpdf.core.font;

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;

public class PdfType1Font extends PdfSimpleFont<Type1Font> {

    PdfType1Font(Type1Font type1Font, String encoding, boolean embedded) {
        super();
        setFontProgram(type1Font);
        this.embedded = embedded && !type1Font.isBuiltInFont();
        if ((encoding == null || encoding.length() == 0) && type1Font.isFontSpecific()) {
            encoding = FontEncoding.FontSpecific;
        }
        if (encoding != null && FontEncoding.FontSpecific.toLowerCase().equals(encoding.toLowerCase())) {
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
        checkFontDictionary(fontDictionary, PdfName.Type1);
        CMapToUnicode toUni = FontUtils.processToUnicode(fontDictionary.get(PdfName.ToUnicode));
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), toUni);
        fontProgram = DocType1Font.createFontProgram(fontDictionary, fontEncoding);

        if (fontProgram instanceof DocFontProgram) {
            embedded = ((DocFontProgram) fontProgram).getFontFile() != null;
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
    protected boolean isBuiltInFont() {
        return fontProgram.isBuiltInFont();
    }

    /**
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     */
    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
        if (embedded) {
            if (fontProgram instanceof DocFontProgram) {
                DocFontProgram docType1Font = (DocFontProgram)fontProgram;
                fontDescriptor.put(docType1Font.getFontFileName(),
                        docType1Font.getFontFile());
                if (docType1Font.getSubtype() != null) {
                    fontDescriptor.put(PdfName.Subtype, docType1Font.getSubtype());
                }
            } else {
                byte[] fontStreamBytes = getFontProgram().getFontStreamBytes();
                if (fontStreamBytes != null) {
                    PdfStream fontStream = new PdfStream(fontStreamBytes);
                    int[] fontStreamLengths = getFontProgram().getFontStreamLengths();
                    for (int k = 0; k < fontStreamLengths.length; ++k) {
                        fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
                    }
                    fontDescriptor.put(PdfName.FontFile, fontStream);
                }
            }
        }
    }
}
