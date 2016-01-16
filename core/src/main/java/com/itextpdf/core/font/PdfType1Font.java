package com.itextpdf.core.font;


import com.itextpdf.basics.font.FontEncoding;
import com.itextpdf.basics.font.FontMetrics;
import com.itextpdf.basics.font.FontNames;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.basics.font.cmap.CMapToUnicode;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;

public class PdfType1Font extends PdfSimpleFont<Type1Font> {

    public PdfType1Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        checkFontDictionary(fontDictionary, PdfName.Type1);

        CMapToUnicode toUni = DocFontUtils.processToUnicode(fontDictionary);
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), toUni);
        fontProgram = DocType1Font.createSimpleFontProgram(fontDictionary, fontEncoding);

        if (fontProgram instanceof DocType1Font) {
            embedded = ((DocType1Font) fontProgram).getFontFile() != null;
        }
        subset = false;
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, String encoding, boolean embedded) {
        super(pdfDocument);
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

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, boolean embedded) {
        this(pdfDocument, type1Font, null, embedded);
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, String encoding) {
        this(pdfDocument, type1Font, encoding, false);
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font) {
        this(pdfDocument, type1Font, null, false);
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
        flushFontData(fontProgram.getFontNames().getFontName(), PdfName.Type1);
    }

    @Override
    protected Type1Font initializeTypeFontForCopy(String encodingName) {
        throw new RuntimeException("Not implemented");
    }

//    @Override
//    protected Type1Font initializeTypeFont(String fontName, String encodingName) {
//        return Type1Font.createFont(fontName, encodingName);
//    }

    public Glyph getGlyph(int ch) {
        if (fontEncoding.canEncode(ch)) {
            Glyph glyph;
            if (fontEncoding.isFontSpecific()) {
                glyph = getFontProgram().getGlyphByCode(ch);
            } else {
                glyph = getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(ch));
                if (glyph == null && (glyph = notdefGlyphs.get(ch)) == null) {
                    // Handle special layout characters like sfthyphen (00AD).
                    // This glyphs will be skipped while converting to bytes
                    glyph = new Glyph(-1, 0, ch);
                    notdefGlyphs.put(ch, glyph);
                }
            }
            return glyph;
        }
        return null;
    }


    private void flushCopyFontData() {
        super.flush();
    }

    @Override
    protected boolean isBuiltInFont() {
        return fontProgram.isBuiltInFont();
    }

    /**
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     *
     * @return the PdfStream containing the font or {@code null}.
     */
    protected void addFontStream(PdfDictionary fontDescriptor) {
        if (embedded) {
            if (fontProgram instanceof DocType1Font) {
                DocType1Font docType1Font = (DocType1Font)fontProgram;
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

    /**
     * Generates the font descriptor for this font or {@code null} if it is one of the 14 built in fonts.
     *
     * @return the PdfDictionary containing the font descriptor or {@code null}.
     */
    protected PdfDictionary getFontDescriptor(String fontName) {
        if (getFontProgram().isBuiltInFont()) {
            return null;
        }
        FontMetrics fontMetrics = fontProgram.getFontMetrics();
        FontNames fontNames = fontProgram.getFontNames();
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(getDocument());
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(fontMetrics.getTypoAscender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(fontMetrics.getCapHeight()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(fontMetrics.getTypoDescender()));
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontMetrics.getBbox().clone()));
        fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(fontMetrics.getItalicAngle()));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(fontMetrics.getStemV()));
        if (fontMetrics.getXHeight() > 0) {
            fontDescriptor.put(PdfName.XHeight, new PdfNumber(fontMetrics.getXHeight()));
        }
        if (fontMetrics.getStemH() > 0) {
            fontDescriptor.put(PdfName.StemH, new PdfNumber(fontMetrics.getStemH()));
        }
        if (fontNames.getFontWeight() > 0) {
            fontDescriptor.put(PdfName.FontWeight, new PdfNumber(fontNames.getFontWeight()));
        }

        if (fontNames.getFamilyName() != null && fontNames.getFamilyName().length > 0 && fontNames.getFamilyName()[0].length >= 4) {
            fontDescriptor.put(PdfName.FontFamily, new PdfString(fontNames.getFamilyName()[0][3]));
        }
        addFontStream(fontDescriptor);
        int flags = fontProgram.getPdfFontFlags();
        if (!fontEncoding.isFontSpecific()) {
            flags &= ~64;
        }
        fontDescriptor.put(PdfName.Flags, new PdfNumber(flags));
        return fontDescriptor;
    }


}
