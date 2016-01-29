package com.itextpdf.core.font;

import com.itextpdf.basics.util.Utilities;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontEncoding;
import com.itextpdf.basics.font.FontMetrics;
import com.itextpdf.basics.font.FontNames;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfOutputStream;
import com.itextpdf.core.pdf.PdfString;

import java.util.ArrayList;
import java.util.List;

public abstract class PdfSimpleFont<T extends FontProgram> extends PdfFont {

    protected T fontProgram;

    protected FontEncoding fontEncoding;

    /**
     * Forces the output of the width array. Only matters for the 14 built-in fonts.
     */
    protected boolean forceWidthsOutput = false;
    /**
     * The array used with single byte encodings.
     */
    protected byte[] shortTag = new byte[256];

    protected PdfSimpleFont(PdfDictionary fontDictionary) {
        super(fontDictionary);
    }

    protected PdfSimpleFont() {
        super();
    }

    @Override
    public GlyphLine createGlyphLine(String content) {
        List<Glyph> glyphs = new ArrayList<>(content.length());
        for (int i = 0; i < content.length(); i++) {
            Glyph glyph;
            if (fontEncoding.isFontSpecific()) {
                glyph = fontProgram.getGlyphByCode(content.charAt(i));
            } else {
                glyph = getGlyph((int) content.charAt(i));
            }
            if (glyph != null) {
                glyphs.add(glyph);
            }
        }
        return new GlyphLine(glyphs);
    }

    @Override
    public T getFontProgram() {
        return fontProgram;
    }

    @Override
    public byte[] convertToBytes(String text) {
        byte[] bytes = fontEncoding.convertToBytes(text);
        for (byte b : bytes) {
            shortTag[b & 0xff] = 1;
        }
        return bytes;
    }

    @Override
    public byte[] convertToBytes(GlyphLine glyphLine) {
        if (glyphLine != null) {
            byte[] bytes = new byte[glyphLine.size()];
            int ptr = 0;
            if (fontEncoding.isFontSpecific()) {
                for (Glyph glyph : glyphLine.glyphs) {
                    bytes[ptr++] = (byte) glyph.getCode();
                }
            } else {
                for (Glyph glyph : glyphLine.glyphs) {
                    if (fontEncoding.canEncode(glyph.getUnicode())) {
                        bytes[ptr++] = (byte) fontEncoding.convertToByte(glyph.getUnicode());
                    }
                }
            }
            bytes = Utilities.shortenArray(bytes, ptr);
            for (byte b : bytes) {
                shortTag[b & 0xff] = 1;
            }
            return bytes;
        } else {
            return emptyBytes;
        }
    }

    @Override
    public byte[] convertToBytes(Glyph glyph) {
        byte[] bytes = new byte[1];
        if (fontEncoding.isFontSpecific()) {
            bytes[0] = (byte) glyph.getCode();
        } else {
            if (fontEncoding.canEncode(glyph.getUnicode())) {
                bytes[0] = (byte) fontEncoding.convertToByte(glyph.getUnicode());
            } else {
                return emptyBytes;
            }
        }
        shortTag[bytes[0] & 0xff] = 1;
        return bytes;
    }

    @Override
    public void writeText(GlyphLine text, int from, int to, PdfOutputStream stream) {
        byte[] bytes = new byte[to - from + 1];
        int ptr = 0;

        if (fontEncoding.isFontSpecific()) {
            for (int i = from; i <= to; i++) {
                bytes[ptr++] = (byte) text.get(i).getCode();
            }
        } else {
            for (int i = from; i <= to; i++) {
                if (fontEncoding.canEncode(text.get(i).getUnicode())) {
                    bytes[ptr++] = (byte) fontEncoding.convertToByte(text.get(i).getUnicode());
                }
            }
        }
        bytes = Utilities.shortenArray(bytes, ptr);
        for (byte b : bytes) {
            shortTag[b & 0xff] = 1;
        }
        Utilities.writeEscapedString(stream, bytes);
    }

    @Override
    public void writeText(String text, PdfOutputStream stream) {
        Utilities.writeEscapedString(stream, convertToBytes(text));
    }

    @Override
    public String decode(PdfString content) {
        byte[] contentBytes = content.getValueBytes();
        StringBuilder builder = new StringBuilder(contentBytes.length);
        for (byte b : contentBytes) {
            Integer uni = fontEncoding.getUnicode(b & 0xff);
            if (uni != null) {
                builder.append((char) (int) uni);
            }
        }
        return builder.toString();
    }

    @Override
    public float getContentWidth(PdfString content) {
        float width = 0;
        byte[] contentBytes = content.getValueBytes();
        for (byte b : contentBytes) {
            Integer uni = fontEncoding.getUnicode(b & 0xff);
            Glyph glyph = uni != null ? getGlyph(uni) : fontProgram.getGlyphByCode(b);
            width += glyph != null ? glyph.getWidth() : 0;
        }
        return width;
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

    protected void flushFontData(String fontName, PdfName subtype) {
        getPdfObject().put(PdfName.Subtype, subtype);
        if (fontName != null) {
            getPdfObject().put(PdfName.BaseFont, new PdfName(fontName));
        }
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
        if (!isSubset() || !isEmbedded()) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; ++k) {
                // remove unsupported by encoding values in case custom encoding.
                // save widths information in case standard pdf encodings (winansi or macroman)
                if (fontEncoding.getUnicode(k) != null) {
                    shortTag[k] = 1;
                } else if (!fontEncoding.hasDifferences() && fontProgram.getGlyphByCode(k) != null) {
                    shortTag[k] = 1;
                } else {

                    shortTag[k] = 0;
                }
            }
        }
        if (fontEncoding.hasDifferences()) {
            // trim range of symbols
            for (int k = firstChar; k <= lastChar; ++k) {
                if (!FontConstants.notdef.equals(fontEncoding.getDifference(k))) {
                    firstChar = k;
                    break;
                }
            }
            for (int k = lastChar; k >= firstChar; --k) {
                if (!FontConstants.notdef.equals(fontEncoding.getDifference(k))) {
                    lastChar = k;
                    break;
                }
            }
            PdfDictionary enc = new PdfDictionary();
            enc.put(PdfName.Type, PdfName.Encoding);
            PdfArray diff = new PdfArray();
            boolean gap = true;
            for (int k = firstChar; k <= lastChar; ++k) {
                if (shortTag[k] != 0) {
                    if (gap) {
                        diff.add(new PdfNumber(k));
                        gap = false;
                    }
                    diff.add(new PdfName(fontEncoding.getDifference(k)));
                } else {
                    gap = true;
                }
            }
            enc.put(PdfName.Differences, diff);
            getPdfObject().put(PdfName.Encoding, enc);
        } else if (!fontEncoding.isFontSpecific()) {
            getPdfObject().put(PdfName.Encoding, PdfEncodings.CP1252.equals(fontEncoding.getBaseEncoding())
                    ? PdfName.WinAnsiEncoding
                    : PdfName.MacRomanEncoding);
        }

        if (isForceWidthsOutput() || !isBuiltInFont() || fontEncoding.hasDifferences()) {
            getPdfObject().put(PdfName.FirstChar, new PdfNumber(firstChar));
            getPdfObject().put(PdfName.LastChar, new PdfNumber(lastChar));
            PdfArray wd = new PdfArray();
            for (int k = firstChar; k <= lastChar; ++k) {
                if (shortTag[k] == 0) {
                    wd.add(new PdfNumber(0));
                } else {
                    //prevent lost of widths info
                    Integer uni = fontEncoding.getUnicode(k);
                    Glyph glyph = uni != null ? getGlyph(uni) : fontProgram.getGlyphByCode(k);
                    wd.add(new PdfNumber(glyph != null ? glyph.getWidth() : 0));
                }
            }
            getPdfObject().put(PdfName.Widths, wd);
        }
        PdfDictionary fontDescriptor = !isBuiltInFont() ? getFontDescriptor(fontName) : null;
        if (fontDescriptor != null) {
            getPdfObject().put(PdfName.FontDescriptor, fontDescriptor);
            fontDescriptor.flush();
        }
    }

    protected boolean isBuiltInFont() {
        return false;
    }

    /**
     * Generates the font descriptor for this font or {@code null} if it is one of the 14 built in fonts.
     *
     * @return the PdfDictionary containing the font descriptor or {@code null}.
     */
    protected PdfDictionary getFontDescriptor(String fontName) {
        FontMetrics fontMetrics = fontProgram.getFontMetrics();
        FontNames fontNames = fontProgram.getFontNames();
        PdfDictionary fontDescriptor = new PdfDictionary();
        markObjectAsIndirect(fontDescriptor);
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(fontMetrics.getTypoAscender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(fontMetrics.getCapHeight()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(fontMetrics.getTypoDescender()));
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontMetrics.getBbox().clone()));
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

    protected abstract void addFontStream(PdfDictionary fontDescriptor);

    protected void setFontProgram(T fontProgram) {
        this.fontProgram = fontProgram;
    }
}
