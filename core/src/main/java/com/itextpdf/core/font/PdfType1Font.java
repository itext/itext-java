package com.itextpdf.core.font;


import com.itextpdf.basics.font.*;
import com.itextpdf.core.pdf.*;

import java.io.IOException;


public class PdfType1Font extends PdfSimpleFont<Type1Font> {

    /**
     * Forces the output of the width array. Only matters for the 14 built-in fonts.
     */
    protected boolean forceWidthsOutput = false;
    /**
     * Indicates if all the glyphs and widths for that particular encoding should be included in the document.
     */
    private boolean subset = false;
    /**
     * The array used with single byte encodings.
     */
    private byte[] shortTag = new byte[256];


    public PdfType1Font(PdfDocument pdfDocument, PdfDictionary fontDictionary) throws IOException {
        super(pdfDocument,fontDictionary,true);
        checkFontDictionary(fontDictionary,PdfName.Type1);
        init();
    }

    public PdfType1Font(PdfDocument pdfDocument, PdfIndirectReference indirectReference) throws IOException {
        this(pdfDocument, (PdfDictionary) indirectReference.getRefersTo());
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, boolean embedded) {
        super(pdfDocument,new PdfDictionary());
        setFontProgram(type1Font);
        this.embedded = embedded;
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font) {
        this(pdfDocument, type1Font, false);
    }

    /**
     * Returns the width of a certain character of this font.
     *
     * @param ch a certain character.
     * @return a width in Text Space.
     */
    public float getWidth(int ch) {
        return getFontProgram().getWidth(ch);
    }

    /**
     * Returns the width of a string of this font.
     *
     * @param s a string content.
     * @return a width of string in Text Space.
     */
    public float getWidth(String s) {
        return getFontProgram().getWidth(s);
    }

    /**
     * Gets the state of the property.
     *
     * @return value of property forceWidthsOutput
     */
    public boolean isForceWidthsOutput() {
        return forceWidthsOutput;
    }

    /**
     * Set to {@code true} to force the generation of the widths array.
     *
     * @param forceWidthsOutput {@code true} to force the generation of the widths array
     */
    public void setForceWidthsOutput(boolean forceWidthsOutput) {
        this.forceWidthsOutput = forceWidthsOutput;
    }

    public boolean isSubset() {
        return subset;
    }

    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    @Override
    public byte[] convertToBytes(String text) {
        byte[] content = getFontProgram().convertToBytes(text);
        for (byte b : content) {
            shortTag[b & 0xff] = 1;
        }
        return content;
    }

    @Override
    public void flush() {
        if (isCopy) {
            flushCopyFontData();
        } else {
            flushFontData();
        }
    }

    /**
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     *
     * @return the PdfStream containing the font or {@code null}.
     * @if there is an error reading the font.
     */
    protected PdfStream getFullFontStream() {
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
    }

   
    @Override
    protected Type1Font initializeTypeFontForCopy(String encodingName) throws IOException {
        return new Type1Font(encodingName);
    }

    @Override
    protected Type1Font initializeTypeFont(String fontName, String encodingName) throws IOException{
        return new Type1Font(fontName, encodingName);
    }

    private void flushCopyFontData() {
        super.flush();
    }

    private void flushFontData() {
        getPdfObject().put(PdfName.Subtype, PdfName.Type1);
        getPdfObject().put(PdfName.BaseFont, new PdfName(fontProgram.getFontNames().getFontName()));
        int firstChar;
        int lastChar;
        for (firstChar = 0; firstChar < 256; ++firstChar) {
            if (shortTag[firstChar] != 0) break;
        }
        for (lastChar = 255; lastChar >= firstChar; --lastChar) {
            if (shortTag[lastChar] != 0) break;
        }
        if (firstChar > 255) {
            firstChar = 255;
            lastChar = 255;
        }
        if (!(subset && embedded)) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; ++k) {
                shortTag[k] = 1;
            }
        }
        FontEncoding encoding = getFontProgram().getEncoding();
        boolean stdEncoding = encoding.getBaseEncoding().equals(PdfEncodings.WINANSI)
                || encoding.getBaseEncoding().equals(PdfEncodings.MACROMAN);
        if (!encoding.isFontSpecific() || encoding.hasSpecialEncoding()) {
            for (int k = firstChar; k <= lastChar; ++k) {
                if (!encoding.getDifferences(k).equals(FontConstants.notdef)) {
                    firstChar = k;
                    break;
                }
            }
            if (stdEncoding) {
                getPdfObject().put(PdfName.Encoding,
                        encoding.getBaseEncoding().equals(PdfEncodings.WINANSI) ? PdfName.WinAnsiEncoding : PdfName.MacRomanEncoding);
            } else {
                PdfDictionary enc = new PdfDictionary();
                enc.put(PdfName.Type, PdfName.Encoding);
                PdfArray dif = new PdfArray();
                boolean gap = true;
                for (int k = firstChar; k <= lastChar; ++k) {
                    if (shortTag[k] != 0) {
                        if (gap) {
                            dif.add(new PdfNumber(k));
                            gap = false;
                        }
                        dif.add(new PdfName(encoding.getDifferences(k)));
                    } else {
                        gap = true;
                    }
                }
                enc.put(PdfName.Differences, dif);
                getPdfObject().put(PdfName.Encoding, enc);
            }
        }
        if (encoding.hasSpecialEncoding() || forceWidthsOutput
                || !(getFontProgram().isBuiltInFont() && (encoding.isFontSpecific() || stdEncoding))) {
            getPdfObject().put(PdfName.FirstChar, new PdfNumber(firstChar));
            getPdfObject().put(PdfName.LastChar, new PdfNumber(lastChar));
            PdfArray wd = new PdfArray();
            int[] widths = fontProgram.getWidths();
            for (int k = firstChar; k <= lastChar; ++k) {
                if (shortTag[k] == 0) {
                    wd.add(new PdfNumber(0));
                } else {
                    wd.add(new PdfNumber(widths[k]));
                }
            }
            getPdfObject().put(PdfName.Widths, wd);
        }
        PdfDictionary fontDescriptor = getFontDescriptor(getFullFontStream());
        if (!getFontProgram().isBuiltInFont() && fontDescriptor != null) {
            getPdfObject().put(PdfName.FontDescriptor, fontDescriptor);
            fontDescriptor.flush();
        }
        super.flush();
    }

    /**
     * Generates the font descriptor for this font or {@code null} if it is one of the 14 built in fonts.
     *
     * @param fontStream the PdfStream containing the font or {@code null}.
     * @return the PdfDictionary containing the font descriptor or {@code null}.
     */


    private PdfDictionary getFontDescriptor(PdfStream fontStream) {
        if (getFontProgram().isBuiltInFont())
            return null;
        FontMetrics fontMetrics = fontProgram.getFontMetrics();
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(getDocument());
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(fontMetrics.getTypoAscender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(fontMetrics.getCapHeight()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(fontMetrics.getTypoDescender()));
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontMetrics.getBbox().clone()));
        fontDescriptor.put(PdfName.FontName, new PdfName(fontProgram.getFontNames().getFontName()));
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
