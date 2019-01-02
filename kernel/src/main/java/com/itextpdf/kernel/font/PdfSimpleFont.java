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
import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.constants.FontDescriptorFlags;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PdfSimpleFont<T extends FontProgram> extends PdfFont {

    private static final long serialVersionUID = -4942318223894676176L;

    protected FontEncoding fontEncoding;

    /**
     * Forces the output of the width array. Only matters for the 14 built-in fonts.
     */
    protected boolean forceWidthsOutput = false;
    /**
     * The array used with single byte encodings.
     */
    protected byte[] shortTag = new byte[256];

    /**
     * Currently only exists for the fonts that are parsed from the document.
     * In the future, we might provide possibility to add custom mappings after a font has been created from a font file.
     */
    protected CMapToUnicode toUnicode;

    protected PdfSimpleFont(PdfDictionary fontDictionary) {
        super(fontDictionary);
        toUnicode = FontUtil.processToUnicode(fontDictionary.get(PdfName.ToUnicode));
    }

    protected PdfSimpleFont() {
        super();
    }

    @Override
    public boolean isBuiltWith(String fontProgram, String encoding) {
        return getFontProgram().isBuiltWith(fontProgram) &&
                fontEncoding.isBuiltWith(encoding);
    }

    @Override
    public GlyphLine createGlyphLine(String content) {
        List<Glyph> glyphs = new ArrayList<>(content.length());
        if (fontEncoding.isFontSpecific()) {
            for (int i = 0; i < content.length(); i++) {
                Glyph glyph = fontProgram.getGlyphByCode(content.charAt(i));
                if (glyph != null) {
                    glyphs.add(glyph);
                }
            }
        } else {
            for (int i = 0; i < content.length(); i++) {
                Glyph glyph = getGlyph((int) content.charAt(i));
                if (glyph != null) {
                    glyphs.add(glyph);
                }
            }
        }
        return new GlyphLine(glyphs);
    }

    @Override
    public int appendGlyphs(String text, int from, int to, List<Glyph> glyphs) {
        int processed = 0;

        if (fontEncoding.isFontSpecific()) {
            for (int i = from; i <= to; i++) {
                Glyph glyph = fontProgram.getGlyphByCode(text.charAt(i) & 0xFF);
                if (glyph != null) {
                    glyphs.add(glyph);
                    processed++;
                } else {
                    break;
                }
            }
        } else {
            for (int i = from; i <= to; i++) {
                Glyph glyph = getGlyph((int) text.charAt(i));
                if (glyph != null && (containsGlyph(glyph.getUnicode()) || isAppendableGlyph(glyph))) {
                    glyphs.add(glyph);
                    processed++;
                } else if (glyph == null && TextUtil.isWhitespaceOrNonPrintable((int) text.charAt(i))) {
                    processed++;
                } else {
                    break;
                }
            }
        }

        return processed;
    }

    @Override
    public int appendAnyGlyph(String text, int from, List<Glyph> glyphs) {
        Glyph glyph;
        if (fontEncoding.isFontSpecific()) {
            glyph = fontProgram.getGlyphByCode(text.charAt(from));
        } else {
            glyph = getGlyph((int) text.charAt(from));
        }

        if (glyph != null) {
            glyphs.add(glyph);
        }
        return 1;
    }

    /**
     * Checks whether the glyph is appendable, i.e. has valid unicode and code values
     *
     * @param glyph not-null {@link Glyph}
     */
    private boolean isAppendableGlyph(Glyph glyph) {
        // If font is specific and glyph.getCode() = 0, unicode value will be also 0.
        // Character.isIdentifierIgnorable(0) gets true.
        return glyph.getCode() > 0 || TextUtil.isWhitespaceOrNonPrintable(glyph.getUnicode());
    }

    public FontEncoding getFontEncoding() {
        return fontEncoding;
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
                for (int i = 0; i < glyphLine.size(); i++) {
                    bytes[ptr++] = (byte) glyphLine.get(i).getCode();
                }
            } else {
                for (int i = 0; i < glyphLine.size(); i++) {
                    if (fontEncoding.canEncode(glyphLine.get(i).getUnicode())) {
                        bytes[ptr++] = (byte) fontEncoding.convertToByte(glyphLine.get(i).getUnicode());
                    }
                }
            }
            bytes = ArrayUtil.shortenArray(bytes, ptr);
            for (byte b : bytes) {
                shortTag[b & 0xff] = 1;
            }
            return bytes;
        } else {
            return EMPTY_BYTES;
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
                return EMPTY_BYTES;
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
        bytes = ArrayUtil.shortenArray(bytes, ptr);
        for (byte b : bytes) {
            shortTag[b & 0xff] = 1;
        }
        StreamUtil.writeEscapedString(stream, bytes);
    }

    @Override
    public void writeText(String text, PdfOutputStream stream) {
        StreamUtil.writeEscapedString(stream, convertToBytes(text));
    }

    @Override
    public String decode(PdfString content) {
        return decodeIntoGlyphLine(content).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GlyphLine decodeIntoGlyphLine(PdfString content) {
        byte[] contentBytes = content.getValueBytes();
        List<Glyph> glyphs = new ArrayList<>(contentBytes.length);
        for (byte b : contentBytes) {
            int code = b & 0xff;
            Glyph glyph = null;
            if (toUnicode != null && toUnicode.lookup(code) != null && (glyph = fontProgram.getGlyphByCode(code)) != null) {
                if (!Arrays.equals(toUnicode.lookup(code), glyph.getChars())) {
                    // Copy the glyph because the original one may be reused (e.g. standard Helvetica font program)
                    glyph = new Glyph(glyph);
                    glyph.setChars(toUnicode.lookup(code));
                }
            } else {
                int uni = fontEncoding.getUnicode(code);
                if (uni > -1) {
                    glyph = getGlyph(uni);
                } else if (fontEncoding.getBaseEncoding() == null) {
                    glyph = fontProgram.getGlyphByCode(code);
                }
            }
            if (glyph != null) {
                glyphs.add(glyph);
            }
        }
        return new GlyphLine(glyphs);
    }

    @Override
    public float getContentWidth(PdfString content) {
        float width = 0;
        GlyphLine glyphLine = decodeIntoGlyphLine(content);
        for (int i = glyphLine.start; i < glyphLine.end; i++) {
            width += glyphLine.get(i).getWidth();
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
        if (fontName != null && fontName.length() > 0) {
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
                if (fontEncoding.canDecode(k)) {
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
                if (!FontEncoding.NOTDEF.equals(fontEncoding.getDifference(k))) {
                    firstChar = k;
                    break;
                }
            }
            for (int k = lastChar; k >= firstChar; --k) {
                if (!FontEncoding.NOTDEF.equals(fontEncoding.getDifference(k))) {
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
                    int uni = fontEncoding.getUnicode(k);
                    Glyph glyph = uni > -1 ? getGlyph(uni) : fontProgram.getGlyphByCode(k);
                    wd.add(new PdfNumber(glyph != null ? glyph.getWidth() : 0));
                }
            }
            getPdfObject().put(PdfName.Widths, wd);
        }
        PdfDictionary fontDescriptor = !isBuiltInFont() ? getFontDescriptor(fontName) : null;
        if (fontDescriptor != null) {
            getPdfObject().put(PdfName.FontDescriptor, fontDescriptor);
            if (fontDescriptor.getIndirectReference() != null) {
                fontDescriptor.flush();
            }
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
    @Override
    protected PdfDictionary getFontDescriptor(String fontName) {
        assert fontName != null && fontName.length() > 0;
        FontMetrics fontMetrics = fontProgram.getFontMetrics();
        FontNames fontNames = fontProgram.getFontNames();
        PdfDictionary fontDescriptor = new PdfDictionary();
        makeObjectIndirect(fontDescriptor);
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(fontMetrics.getTypoAscender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(fontMetrics.getCapHeight()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(fontMetrics.getTypoDescender()));
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(ArrayUtil.cloneArray(fontMetrics.getBbox())));
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
        //add font stream and flush it immediately
        addFontStream(fontDescriptor);
        int flags = fontProgram.getPdfFontFlags();
        flags &= ~(FontDescriptorFlags.Symbolic | FontDescriptorFlags.Nonsymbolic); // reset both flags
        flags |= fontEncoding.isFontSpecific() ? // set based on font encoding
                FontDescriptorFlags.Symbolic : FontDescriptorFlags.Nonsymbolic;

        fontDescriptor.put(PdfName.Flags, new PdfNumber(flags));
        return fontDescriptor;
    }

    protected abstract void addFontStream(PdfDictionary fontDescriptor);

    protected void setFontProgram(T fontProgram) {
        this.fontProgram = fontProgram;
    }
}
