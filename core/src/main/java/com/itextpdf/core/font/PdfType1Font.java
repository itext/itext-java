package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontEncoding;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfStream;

public class PdfType1Font extends PdfFont {
    /** Type1 font program. */
    private Type1Font fontProgram;
    /** Forces the output of the width array. Only matters for the 14 built-in fonts. */
    protected boolean forceWidthsOutput = false;
    /** true if the font is to be embedded in the PDF. */
    private boolean embedded = false;
    /** Indicates if all the glyphs and widths for that particular encoding should be included in the document. */
    private boolean subset = false;
    /** The array used with single byte encodings. */
    private byte[] shortTag = new byte[256];

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font, boolean embedded) throws PdfException {
        super(new PdfDictionary(), pdfDocument);
        fontProgram = type1Font;
        this.embedded = embedded;
    }

    public PdfType1Font(PdfDocument pdfDocument, Type1Font type1Font) throws PdfException {
        this(pdfDocument, type1Font, false);
    }

    /**
     * Returns the width of a certain character of this font.
     *
     * @param ch	a certain character.
     * @return a width in Text Space.
     */
    public float getWidth(int ch) {
        return fontProgram.getWidth(ch);
    }

    /**
     * Returns the width of a string of this font.
     *
     * @param s	a string content.
     * @return a width of string in Text Space.
     */
    public float getWidth(String s) {
        return fontProgram.getWidth(s);
    }

    /**
     * Gets the state of the property.
     * @return value of property forceWidthsOutput
     */
    public boolean isForceWidthsOutput() {
        return forceWidthsOutput;
    }

    /**
     * Set to {@code true} to force the generation of the widths array.
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
        byte[] content = fontProgram.convertToBytes(text);
        for (byte b : content) {
            shortTag[b & 0xff] = 1;
        }
        return content;
    }

    @Override
    public void flush() throws PdfException {
        getPdfObject().put(PdfName.Subtype, PdfName.Type1);
        getPdfObject().put(PdfName.BaseFont, new PdfName(fontProgram.getFontName()));
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
        FontEncoding encoding = fontProgram.getEncoding();
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
                || !(fontProgram.isBuiltInFont() && (encoding.isFontSpecific() || stdEncoding))) {
            getPdfObject().put(PdfName.FirstChar, new PdfNumber(firstChar));
            getPdfObject().put(PdfName.LastChar, new PdfNumber(lastChar));
            PdfArray wd = new PdfArray();
            int[] widths = fontProgram.getRawWidths();
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
        if (!fontProgram.isBuiltInFont() && fontDescriptor != null) {
            getPdfObject().put(PdfName.FontDescriptor, fontDescriptor);
            fontDescriptor.flush();
        }
        super.flush();
    }

    /**
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     * @return the PdfStream containing the font or {@code null}.
     * @throws PdfException if there is an error reading the font.
     */
    protected PdfStream getFullFontStream() throws PdfException {
        byte[] fontStreamBytes = fontProgram.getFontStreamBytes();
        if (fontStreamBytes == null) {
            return null;
        }
        PdfStream fontStream = new PdfStream(getDocument(), fontStreamBytes);
        int[] fontStreamLengths = fontProgram.getFontStreamLengths();
        for (int k = 0; k < fontStreamLengths.length; ++k) {
            fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
        }
        return fontStream;
    }

    /**
     * Generates the font descriptor for this font or {@code null} if it is one of the 14 built in fonts.
     * @param fontStream the PdfStream containing the font or {@code null}.
     * @return the PdfDictionary containing the font descriptor or {@code null}.
     */
    private PdfDictionary getFontDescriptor(PdfStream fontStream) throws PdfException {
        if (fontProgram.isBuiltInFont())
            return null;
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(getDocument());
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(fontProgram.getAscender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(fontProgram.getCapHeight()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(fontProgram.getDescender()));
        Rectangle fontBBox = new Rectangle(fontProgram.getLlx(), fontProgram.getLly(),
                fontProgram.getUrx(), fontProgram.getUry());
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontBBox));
        fontDescriptor.put(PdfName.FontName, new PdfName(fontProgram.getFontName()));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(fontProgram.getItalicAngle()));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(fontProgram.getStdVW()));
        if (fontStream != null) {
            fontDescriptor.put(PdfName.FontFile, fontStream);
            fontStream.flush();
        }
        int flags = 0;
        if (fontProgram.isFixedPitch()) {
            flags |= 1;
        }
        flags |= fontProgram.getEncoding().isFontSpecific() ? 4 : 32;
        if (fontProgram.getItalicAngle() < 0) {
            flags |= 64;
        }
        if (fontProgram.getFontName().contains("Caps") || fontProgram.getFontName().endsWith("SC")) {
            flags |= 131072;
        }
        if (fontProgram.getWeight().equals("Bold")) {
            flags |= 262144;
        }
        fontDescriptor.put(PdfName.Flags, new PdfNumber(flags));
        return fontDescriptor;
    }
}
