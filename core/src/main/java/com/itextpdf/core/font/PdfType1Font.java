package com.itextpdf.core.font;


import com.itextpdf.basics.font.FontEncoding;
import com.itextpdf.basics.font.FontMetrics;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;

import java.io.IOException;


public class PdfType1Font extends PdfSimpleFont<Type1Font> {

    public PdfType1Font(PdfDocument pdfDocument, PdfDictionary fontDictionary) throws IOException {
        super(pdfDocument,fontDictionary,true);
        checkFontDictionary(fontDictionary,PdfName.Type1);
        init();
    }

    public PdfType1Font(PdfDocument pdfDocument, PdfIndirectReference indirectReference) throws IOException {
        this(pdfDocument, (PdfDictionary) indirectReference.getRefersTo());
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, String encoding, boolean embedded) {
        super(pdfDocument, new PdfDictionary());
        setFontProgram(type1Font);
        this.embedded = embedded && !type1Font.isBuiltInFont();
        this.fontEncoding = new FontEncoding(encoding, type1Font.isFontSpecific());
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, boolean embedded) {
        this(pdfDocument, type1Font, PdfEncodings.WINANSI, embedded);
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, String encoding) {
        this(pdfDocument, type1Font, encoding, false);
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font) {
        this(pdfDocument, type1Font, PdfEncodings.WINANSI, false);
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
        if (isCopy) {
            flushCopyFontData();
        } else {
            flushFontData(fontProgram.getFontNames().getFontName(), PdfName.Type1);
        }
    }

    @Override
    protected Type1Font initializeTypeFontForCopy(String encodingName) throws IOException {
        return new Type1Font(encodingName);
    }

    @Override
    protected Type1Font initializeTypeFont(String fontName, String encodingName) throws IOException{
        return Type1Font.createFont(fontName, encodingName);
    }

    public Glyph getGlyph(int ch) {
        if (fontEncoding.canEncode(ch)) {
            Glyph glyph;
            if (fontEncoding.isFontSpecific()) {
                glyph = getFontProgram().getGlyphByCode(ch);
            } else {
                glyph = getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(ch));
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
    protected PdfStream getFontStream() {
        if (embedded) {
            byte[] fontStreamBytes = getFontProgram().getFontStreamBytes();
            if (fontStreamBytes == null) {
                return null;
            }
            PdfStream fontStream = new PdfStream(fontStreamBytes).makeIndirect(getDocument());
            int[] fontStreamLengths = getFontProgram().getFontStreamLengths();
            for (int k = 0; k < fontStreamLengths.length; ++k) {
                fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
            }
            return fontStream;
        } else {
            return null;
        }
    }

    /**
     * Generates the font descriptor for this font or {@code null} if it is one of the 14 built in fonts.
     *
     * @param fontStream the PdfStream containing the font or {@code null}.
     * @return the PdfDictionary containing the font descriptor or {@code null}.
     */
    protected PdfDictionary getFontDescriptor(PdfStream fontStream, String fontName) {
        if (getFontProgram().isBuiltInFont()) {
            return null;
        }
        FontMetrics fontMetrics = fontProgram.getFontMetrics();
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
        if (fontStream != null) {
            fontDescriptor.put(PdfName.FontFile, fontStream);
            fontStream.flush();
        }
        fontDescriptor.put(PdfName.Flags, new PdfNumber(fontProgram.getPdfFontFlags()));
        return fontDescriptor;
    }



}
