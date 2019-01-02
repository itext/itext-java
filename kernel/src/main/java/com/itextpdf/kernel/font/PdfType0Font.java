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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.CFFFontSubset;
import com.itextpdf.io.font.CMapEncoding;
import com.itextpdf.io.font.CidFont;
import com.itextpdf.io.font.CidFontProperties;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.cmap.CMapContentParser;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfType0Font extends PdfFont {

    private static final long serialVersionUID = -8033620300884193397L;

    private static final byte[] rotbits = {(byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10, (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01};

    /**
     * CIDFont Type0 (Type1 outlines).
     */
    protected static final int CID_FONT_TYPE_0 = 0;
    /**
     * CIDFont Type2 (TrueType outlines).
     */
    protected static final int CID_FONT_TYPE_2 = 2;

    protected boolean vertical;
    protected CMapEncoding cmapEncoding;
    protected Set<Integer> longTag;
    protected int cidFontType;
    protected char[] specificUnicodeDifferences;

    PdfType0Font(TrueTypeFont ttf, String cmap) {
        super();
        if (!cmap.equals(PdfEncodings.IDENTITY_H) && !cmap.equals(PdfEncodings.IDENTITY_V)) {
            throw new PdfException(PdfException.OnlyIdentityCMapsSupportsWithTrueType);
        }

        if (!ttf.getFontNames().allowEmbedding()) {
            throw new PdfException(PdfException.CannotBeEmbeddedDueToLicensingRestrictions)
                    .setMessageParams(ttf.getFontNames().getFontName() + ttf.getFontNames().getStyle());
        }
        this.fontProgram = ttf;
        this.embedded = true;
        vertical = cmap.endsWith("V");
        cmapEncoding = new CMapEncoding(cmap);
        longTag = new HashSet<>();
        cidFontType = CID_FONT_TYPE_2;
        if (ttf.isFontSpecific()) {
            specificUnicodeDifferences = new char[256];
            byte[] bytes = new byte[1];
            for (int k = 0; k < 256; ++k) {
                bytes[0] = (byte) k;
                String s = PdfEncodings.convertToString(bytes, null);
                char ch = s.length() > 0 ? s.charAt(0) : '?';
                specificUnicodeDifferences[k] = ch;
            }
        }
    }

    // Note. Make this constructor protected. Only PdfFontFactory (kernel level) will
    // be able to create Type0 font based on predefined font.
    // Or not? Possible it will be convenient construct PdfType0Font based on custom CidFont.
    // There is no typography features in CJK fonts.
    PdfType0Font(CidFont font, String cmap) {
        super();
        if (!CidFontProperties.isCidFont(font.getFontNames().getFontName(), cmap)) {
            throw new PdfException("Font {0} with {1} encoding is not a cjk font.")
                    .setMessageParams(font.getFontNames().getFontName(), cmap);
        }
        this.fontProgram = font;
        vertical = cmap.endsWith("V");
        String uniMap = getCompatibleUniMap(fontProgram.getRegistry());
        cmapEncoding = new CMapEncoding(cmap, uniMap);
        longTag = new HashSet<>();
        cidFontType = CID_FONT_TYPE_0;
    }

    PdfType0Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        newFont = false;
        PdfDictionary cidFont = fontDictionary.getAsArray(PdfName.DescendantFonts).getAsDictionary(0);
        PdfObject cmap = fontDictionary.get(PdfName.Encoding);
        PdfObject toUnicode = fontDictionary.get(PdfName.ToUnicode);
        CMapToUnicode toUnicodeCMap = FontUtil.processToUnicode(toUnicode);
        if (cmap.isName() && (PdfEncodings.IDENTITY_H.equals(((PdfName) cmap).getValue()) || PdfEncodings.IDENTITY_V.equals(((PdfName) cmap).getValue()))) {
            if (toUnicodeCMap == null) {
                String uniMap = getUniMapFromOrdering(getOrdering(cidFont));
                toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(uniMap);
                if (toUnicodeCMap == null) {
                    toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(PdfEncodings.IDENTITY_H);
                    Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
                    logger.error(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_CMAP, uniMap));
                }
            }
            fontProgram = DocTrueTypeFont.createFontProgram(cidFont, toUnicodeCMap);
            cmapEncoding = createCMap(cmap, null);
            assert fontProgram instanceof IDocFontProgram;
            embedded = ((IDocFontProgram) fontProgram).getFontFile() != null;
        } else {
            String cidFontName = cidFont.getAsName(PdfName.BaseFont).getValue();
            String uniMap = getUniMapFromOrdering(getOrdering(cidFont));
            if (uniMap != null && uniMap.startsWith("Uni") && CidFontProperties.isCidFont(cidFontName, uniMap)) {
                try {
                    fontProgram = FontProgramFactory.createFont(cidFontName);
                    cmapEncoding = createCMap(cmap, uniMap);
                    embedded = false;
                } catch (IOException ignored) {
                    fontProgram = null;
                    cmapEncoding = null;
                }
            } else {
                if (toUnicodeCMap == null) {
                    toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(uniMap);
                }
                if (toUnicodeCMap != null) {
                    fontProgram = DocTrueTypeFont.createFontProgram(cidFont, toUnicodeCMap);
                    cmapEncoding = createCMap(cmap, uniMap);
                }
            }
            if (fontProgram == null) {
                throw new PdfException(MessageFormatUtil.format(PdfException.CannotRecogniseDocumentFontWithEncoding, cidFontName, cmap));
            }
        }
        // DescendantFonts is a one-element array specifying the CIDFont dictionary that is the descendant of this Type 0 font.
        PdfDictionary cidFontDictionary = fontDictionary.getAsArray(PdfName.DescendantFonts).getAsDictionary(0);
        // Required according to the spec
        PdfName subtype = cidFontDictionary.getAsName(PdfName.Subtype);
        if (PdfName.CIDFontType0.equals(subtype)) {
            cidFontType = CID_FONT_TYPE_0;
        } else if (PdfName.CIDFontType2.equals(subtype)) {
            cidFontType = CID_FONT_TYPE_2;
        } else {
            LoggerFactory.getLogger(getClass()).error(LogMessageConstant.FAILED_TO_DETERMINE_CID_FONT_SUBTYPE);
        }
        longTag = new HashSet<>();
        subset = false;
    }

    public static String getUniMapFromOrdering(String ordering) {
        switch (ordering) {
            case "CNS1":
                return "UniCNS-UTF16-H";
            case "Japan1":
                return "UniJIS-UTF16-H";
            case "Korea1":
                return "UniKS-UTF16-H";
            case "GB1":
                return "UniGB-UTF16-H";
            case "Identity":
                return "Identity-H";
            default:
                return null;
        }
    }

    @Override
    public Glyph getGlyph(int unicode) {
        // TODO handle unicode value with cmap and use only glyphByCode
        Glyph glyph = getFontProgram().getGlyph(unicode);
        if (glyph == null && (glyph = notdefGlyphs.get(unicode)) == null) {
            // Handle special layout characters like sfthyphen (00AD).
            // This glyphs will be skipped while converting to bytes
            Glyph notdef = getFontProgram().getGlyphByCode(0);
            if (notdef != null) {
                glyph = new Glyph(notdef, unicode);
            } else {
                glyph = new Glyph(-1, 0, unicode);
            }
            notdefGlyphs.put(unicode, glyph);
        }
        return glyph;
    }

    @Override
    public boolean containsGlyph(int unicode) {
        if (cidFontType == CID_FONT_TYPE_0) {
            if (cmapEncoding.isDirect()) {
                return fontProgram.getGlyphByCode(unicode) != null;
            } else {
                return getFontProgram().getGlyph(unicode) != null;
            }
        } else if (cidFontType == CID_FONT_TYPE_2) {
            if (fontProgram.isFontSpecific()) {
                byte[] b = PdfEncodings.convertToBytes((char) unicode, "symboltt");
                return b.length > 0 && fontProgram.getGlyph(b[0] & 0xff) != null;
            } else {
                return getFontProgram().getGlyph(unicode) != null;
            }
        } else {
            throw new PdfException("Invalid CID font type: " + cidFontType);
        }
    }

    @Override
    public byte[] convertToBytes(String text) {
        int len = text.length();
        ByteBuffer buffer = new ByteBuffer();
        int i = 0;
        if (fontProgram.isFontSpecific()) {
            byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
            len = b.length;
            for (int k = 0; k < len; ++k) {
                Glyph glyph = fontProgram.getGlyph(b[k] & 0xff);
                if (glyph != null) {
                    convertToBytes(glyph, buffer);
                }
            }
        } else {
            for (int k = 0; k < len; ++k) {
                int val;
                if (TextUtil.isSurrogatePair(text, k)) {
                    val = TextUtil.convertToUtf32(text, k);
                    k++;
                } else {
                    val = text.charAt(k);
                }
                Glyph glyph = getGlyph(val);
                if (glyph.getCode() > 0) {
                    convertToBytes(glyph, buffer);
                } else {
                    //getCode() could be either -1 or 0
                    int nullCode = cmapEncoding.getCmapCode(0);
                    buffer.append(nullCode >> 8);
                    buffer.append(nullCode);
                }
            }
        }
        return buffer.toByteArray();
    }

    @Override
    public byte[] convertToBytes(GlyphLine glyphLine) {
        if (glyphLine != null) {
            // prepare and count total length in bytes
            int totalByteCount = 0;
            for (int i = glyphLine.start; i < glyphLine.end; i++) {
                totalByteCount += cmapEncoding.getCmapBytesLength(glyphLine.get(i).getCode());
            }
            // perform actual conversion
            byte[] bytes = new byte[totalByteCount];
            int offset = 0;
            for (int i = glyphLine.start; i < glyphLine.end; i++) {
                longTag.add(glyphLine.get(i).getCode());
                offset = cmapEncoding.fillCmapBytes(glyphLine.get(i).getCode(), bytes, offset);
            }
            return bytes;
        } else {
            return null;
        }
    }

    @Override
    public byte[] convertToBytes(Glyph glyph) {
        longTag.add(glyph.getCode());
        return cmapEncoding.getCmapBytes(glyph.getCode());
    }

    @Override
    public void writeText(GlyphLine text, int from, int to, PdfOutputStream stream) {
        int len = to - from + 1;
        if (len > 0) {
            byte[] bytes = convertToBytes(new GlyphLine(text, from, to + 1));
            StreamUtil.writeHexedString(stream, bytes);
        }
    }

    @Override
    public void writeText(String text, PdfOutputStream stream) {
        StreamUtil.writeHexedString(stream, convertToBytes(text));
    }

    @Override
    public GlyphLine createGlyphLine(String content) {
        List<Glyph> glyphs = new ArrayList<>();
        if (cidFontType == CID_FONT_TYPE_0) {
            int len = content.length();
            if (cmapEncoding.isDirect()) {
                for (int k = 0; k < len; ++k) {
                    Glyph glyph = fontProgram.getGlyphByCode((int) content.charAt(k));
                    if (glyph != null) {
                        glyphs.add(glyph);
                    }
                }
            } else {
                for (int k = 0; k < len; ++k) {
                    int ch;
                    if (TextUtil.isSurrogatePair(content, k)) {
                        ch = TextUtil.convertToUtf32(content, k);
                        k++;
                    } else {
                        ch = content.charAt(k);
                    }
                    glyphs.add(getGlyph(ch));
                }
            }
        } else if (cidFontType == CID_FONT_TYPE_2) {
            int len = content.length();
            if (fontProgram.isFontSpecific()) {
                byte[] b = PdfEncodings.convertToBytes(content, "symboltt");
                len = b.length;
                for (int k = 0; k < len; ++k) {
                    Glyph glyph = fontProgram.getGlyph(b[k] & 0xff);
                    if (glyph != null) {
                        glyphs.add(glyph);
                    }
                }
            } else {
                for (int k = 0; k < len; ++k) {
                    int val;
                    if (TextUtil.isSurrogatePair(content, k)) {
                        val = TextUtil.convertToUtf32(content, k);
                        k++;
                    } else {
                        val = content.charAt(k);
                    }
                    glyphs.add(getGlyph(val));
                }
            }
        } else {
            throw new PdfException("Font has no suitable cmap.");
        }

        return new GlyphLine(glyphs);
    }

    @Override
    public int appendGlyphs(String text, int from, int to, List<Glyph> glyphs) {
        if (cidFontType == CID_FONT_TYPE_0) {
            if (cmapEncoding.isDirect()) {
                int processed = 0;
                for (int k = from; k <= to; k++) {
                    Glyph glyph = fontProgram.getGlyphByCode((int) text.charAt(k));
                    if (glyph != null && (isAppendableGlyph(glyph))) {
                        glyphs.add(glyph);
                        processed++;
                    } else {
                        break;
                    }
                }
                return processed;
            } else {
                return appendUniGlyphs(text, from, to, glyphs);
            }
        } else if (cidFontType == CID_FONT_TYPE_2) {
            if (fontProgram.isFontSpecific()) {
                int processed = 0;
                for (int k = from; k <= to; k++) {
                    Glyph glyph = fontProgram.getGlyph(text.charAt(k) & 0xff);
                    if (glyph != null && (isAppendableGlyph(glyph))) {
                        glyphs.add(glyph);
                        processed++;
                    } else {
                        break;
                    }
                }
                return processed;
            } else {
                return appendUniGlyphs(text, from, to, glyphs);
            }
        } else {
            throw new PdfException("Font has no suitable cmap.");
        }
    }

    private int appendUniGlyphs(String text, int from, int to, List<Glyph> glyphs) {
        int processed = 0;
        for (int k = from; k <= to; ++k) {
            int val;
            int currentlyProcessed = processed;
            if (TextUtil.isSurrogatePair(text, k)) {
                val = TextUtil.convertToUtf32(text, k);
                processed += 2;
            } else {
                val = text.charAt(k);
                processed++;
            }
            Glyph glyph = getGlyph(val);
            if (isAppendableGlyph(glyph)) {
                glyphs.add(glyph);
            } else {
                processed = currentlyProcessed;
                break;
            }
        }
        return processed;
    }

    @Override
    public int appendAnyGlyph(String text, int from, List<Glyph> glyphs) {
        int process = 1;

        if (cidFontType == CID_FONT_TYPE_0) {
            if (cmapEncoding.isDirect()) {
                Glyph glyph = fontProgram.getGlyphByCode((int) text.charAt(from));
                if (glyph != null) {
                    glyphs.add(glyph);
                }
            } else {
                int ch;
                if (TextUtil.isSurrogatePair(text, from)) {
                    ch = TextUtil.convertToUtf32(text, from);
                    process = 2;
                } else {
                    ch = text.charAt(from);
                }
                glyphs.add(getGlyph(ch));
            }
        } else if (cidFontType == CID_FONT_TYPE_2) {
            TrueTypeFont ttf = (TrueTypeFont) fontProgram;
            if (ttf.isFontSpecific()) {
                byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
                if (b.length > 0) {
                    Glyph glyph = fontProgram.getGlyph(b[0] & 0xff);
                    if (glyph != null) {
                        glyphs.add(glyph);
                    }
                }
            } else {
                int ch;
                if (TextUtil.isSurrogatePair(text, from)) {
                    ch = TextUtil.convertToUtf32(text, from);
                    process = 2;
                } else {
                    ch = text.charAt(from);
                }
                glyphs.add(getGlyph(ch));
            }
        } else {
            throw new PdfException("Font has no suitable cmap.");
        }
        return process;
    }

    //TODO what if Glyphs contains only whitespaces and ignorable identifiers?
    private boolean isAppendableGlyph(Glyph glyph) {
        // If font is specific and glyph.getCode() = 0, unicode value will be also 0.
        // Character.isIdentifierIgnorable(0) gets true.
        return glyph.getCode() > 0 || TextUtil.isWhitespaceOrNonPrintable(glyph.getUnicode());
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
        //A sequence of one or more bytes shall be extracted from the string and matched against the codespace
        //ranges in the CMap. That is, the first byte shall be matched against 1-byte codespace ranges; if no match is
        //found, a second byte shall be extracted, and the 2-byte code shall be matched against 2-byte codespace
        //ranges. This process continues for successively longer codes until a match is found or all codespace ranges
        //have been tested. There will be at most one match because codespace ranges shall not overlap.
        String cids = content.getValue();
        List<Glyph> glyphs = new ArrayList<>();
        for (int i = 0; i < cids.length(); i++) {
            //The code length shall not be greater than 4.
            int code = 0;
            Glyph glyph = null;
            int codeSpaceMatchedLength = 1;
            for (int codeLength = 1; codeLength <= 4 && i + codeLength <= cids.length(); codeLength++) {
                code = (code << 8) + cids.charAt(i + codeLength - 1);
                if (!cmapEncoding.containsCodeInCodeSpaceRange(code, codeLength)) {
                    continue;
                } else {
                    codeSpaceMatchedLength = codeLength;
                }
                int glyphCode = cmapEncoding.getCidCode(code);
                glyph = fontProgram.getGlyphByCode(glyphCode);
                if (glyph != null) {
                    i += codeLength - 1;
                    break;
                }
            }
            if (glyph == null) {
                StringBuilder failedCodes = new StringBuilder();
                for (int codeLength = 1; codeLength <= 4 && i + codeLength <= cids.length(); codeLength++) {
                    failedCodes.append((int) cids.charAt(i + codeLength - 1)).append(" ");
                }
                Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
                logger.warn(MessageFormatUtil.format(LogMessageConstant.COULD_NOT_FIND_GLYPH_WITH_CODE, failedCodes.toString()));
                i += codeSpaceMatchedLength - 1;
            }
            if (glyph != null && glyph.getChars() != null) {
                glyphs.add(glyph);
            } else {
                glyphs.add(new Glyph(0, fontProgram.getGlyphByCode(0).getWidth(), -1));
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

    @Override
    public boolean isBuiltWith(String fontProgram, String encoding) {
        return getFontProgram().isBuiltWith(fontProgram) &&
                cmapEncoding.isBuiltWith(encoding);
    }

    @Override
    public void flush() {
        if (isFlushed()) return;
        ensureUnderlyingObjectHasIndirectReference();
        if (newFont) {
            flushFontData();
        }
        super.flush();
    }

    /**
     * Gets CMAP associated with the Pdf Font.
     *
     * @return CMAP
     * @see CMapEncoding
     */
    public CMapEncoding getCmap() {
        return cmapEncoding;
    }

    /**
     * Creates a ToUnicode CMap to allow copy and paste from Acrobat.
     *
     * @param metrics metrics[0] contains the glyph index and metrics[2]
     *                contains the Unicode code
     * @return the stream representing this CMap or <CODE>null</CODE>
     * @deprecated will be removed in 7.2. Use {@link #getToUnicode(int[])} instead
     */
    @Deprecated
    public PdfStream getToUnicode(Object[] metrics) {
        ArrayList<Integer> unicodeGlyphs = new ArrayList<>(metrics.length);
        for (int i = 0; i < metrics.length; i++) {
            int[] metric = (int[]) metrics[i];
            if (fontProgram.getGlyphByCode(metric[0]).getChars() != null) {
                unicodeGlyphs.add(metric[0]);
            }
        }
        if (unicodeGlyphs.size() == 0)
            return null;
        StringBuilder buf = new StringBuilder(
                "/CIDInit /ProcSet findresource begin\n" +
                        "12 dict begin\n" +
                        "begincmap\n" +
                        "/CIDSystemInfo\n" +
                        "<< /Registry (Adobe)\n" +
                        "/Ordering (UCS)\n" +
                        "/Supplement 0\n" +
                        ">> def\n" +
                        "/CMapName /Adobe-Identity-UCS def\n" +
                        "/CMapType 2 def\n" +
                        "1 begincodespacerange\n" +
                        "<0000><FFFF>\n" +
                        "endcodespacerange\n");
        int size = 0;
        for (int k = 0; k < unicodeGlyphs.size(); ++k) {
            if (size == 0) {
                if (k != 0) {
                    buf.append("endbfrange\n");
                }
                size = Math.min(100, unicodeGlyphs.size() - k);
                buf.append(size).append(" beginbfrange\n");
            }
            --size;
            String fromTo = CMapContentParser.toHex((int) unicodeGlyphs.get(k));
            Glyph glyph = fontProgram.getGlyphByCode((int) unicodeGlyphs.get(k));
            if (glyph.getChars() != null) {
                StringBuilder uni = new StringBuilder(glyph.getChars().length);
                for (char ch : glyph.getChars()) {
                    uni.append(toHex4(ch));
                }
                buf.append(fromTo).append(fromTo).append('<').append(uni.toString()).append('>').append('\n');
            }
        }
        buf.append("endbfrange\n" +
                "endcmap\n" +
                "CMapName currentdict /CMap defineresource pop\n" +
                "end end\n");
        PdfStream toUnicode = new PdfStream(PdfEncodings.convertToBytes(buf.toString(), null));
        makeObjectIndirect(toUnicode);
        return toUnicode;
    }

    @Override
    protected PdfDictionary getFontDescriptor(String fontName) {
        PdfDictionary fontDescriptor = new PdfDictionary();
        makeObjectIndirect(fontDescriptor);
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(getFontProgram().getFontMetrics().getBbox()));
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(getFontProgram().getFontMetrics().getTypoAscender()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(getFontProgram().getFontMetrics().getTypoDescender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(getFontProgram().getFontMetrics().getCapHeight()));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(getFontProgram().getFontMetrics().getItalicAngle()));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(getFontProgram().getFontMetrics().getStemV()));
        fontDescriptor.put(PdfName.Flags, new PdfNumber(getFontProgram().getPdfFontFlags()));
        if (fontProgram.getFontIdentification().getPanose() != null) {
            PdfDictionary styleDictionary = new PdfDictionary();
            styleDictionary.put(PdfName.Panose, new PdfString(fontProgram.getFontIdentification().getPanose()).setHexWriting(true));
            fontDescriptor.put(PdfName.Style, styleDictionary);
        }
        return fontDescriptor;
    }

    /**
     * Generates the CIDFontTyte2 dictionary.
     *
     * @param fontDescriptor the indirect reference to the font descriptor
     * @param fontName       a name of the font
     * @param metrics        the horizontal width metrics
     * @return fully initialized CIDFont
     * @deprecated will be removed in 7.2
     */
    @Deprecated
    protected PdfDictionary getCidFontType2(TrueTypeFont ttf, PdfDictionary fontDescriptor, String fontName, int[][] metrics) {
        PdfDictionary cidFont = new PdfDictionary();
        makeObjectIndirect(cidFont);
        cidFont.put(PdfName.Type, PdfName.Font);
        // sivan; cff
        cidFont.put(PdfName.FontDescriptor, fontDescriptor);
        if (ttf == null || ttf.isCff()) {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        } else {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType2);
            cidFont.put(PdfName.CIDToGIDMap, PdfName.Identity);
        }
        cidFont.put(PdfName.BaseFont, new PdfName(fontName));
        PdfDictionary cidInfo = new PdfDictionary();
        cidInfo.put(PdfName.Registry, new PdfString(cmapEncoding.getRegistry()));
        cidInfo.put(PdfName.Ordering, new PdfString(cmapEncoding.getOrdering()));
        cidInfo.put(PdfName.Supplement, new PdfNumber(cmapEncoding.getSupplement()));
        cidFont.put(PdfName.CIDSystemInfo, cidInfo);
        if (!vertical) {
            cidFont.put(PdfName.DW, new PdfNumber(FontProgram.DEFAULT_WIDTH));
            StringBuilder buf = new StringBuilder("[");
            int lastNumber = -10;
            boolean firstTime = true;
            for (int[] metric : metrics) {
                Glyph glyph = fontProgram.getGlyphByCode(metric[0]);
                if (glyph.getWidth() == FontProgram.DEFAULT_WIDTH) {
                    continue;
                }
                if (glyph.getCode() == lastNumber + 1) {
                    buf.append(' ').append(glyph.getWidth());
                } else {
                    if (!firstTime) {
                        buf.append(']');
                    }
                    firstTime = false;
                    buf.append(glyph.getCode()).append('[').append(glyph.getWidth());
                }
                lastNumber = glyph.getCode();
            }
            if (buf.length() > 1) {
                buf.append("]]");
                cidFont.put(PdfName.W, new PdfLiteral(buf.toString()));
            }
        } else {
            throw new UnsupportedOperationException("Vertical writing has not implemented yet.");
        }
        return cidFont;
    }

    @Deprecated
    /**
     * @deprecated will be removed in 7.2
     */
    protected void addRangeUni(TrueTypeFont ttf, Map<Integer, int[]> longTag, boolean includeMetrics) {
        if (!subset && (subsetRanges != null || ttf.getDirectoryOffset() > 0)) {
            int[] rg = subsetRanges == null && ttf.getDirectoryOffset() > 0
                    ? new int[]{0, 0xffff} : compactRanges(subsetRanges);
            Map<Integer, int[]> usemap = ttf.getActiveCmap();
            assert usemap != null;
            for (Map.Entry<Integer, int[]> e : usemap.entrySet()) {
                int[] v = e.getValue();
                int gi = v[0];
                if (longTag.containsKey(v[0])) {
                    continue;
                }
                int c = e.getKey();
                boolean skip = true;
                for (int k = 0; k < rg.length; k += 2) {
                    if (c >= rg[k] && c <= rg[k + 1]) {
                        skip = false;
                        break;
                    }
                }
                if (!skip) {
                    longTag.put(gi, includeMetrics ? new int[]{v[0], v[1], c} : null);
                }
            }
        }
    }

    private void convertToBytes(Glyph glyph, ByteBuffer result) {
        int code = glyph.getCode();
        longTag.add(code);
        cmapEncoding.fillCmapBytes(code, result);
    }

    private static String getOrdering(PdfDictionary cidFont) {
        PdfDictionary cidinfo = cidFont.getAsDictionary(PdfName.CIDSystemInfo);
        if (cidinfo == null)
            return null;
        return cidinfo.containsKey(PdfName.Ordering) ? cidinfo.get(PdfName.Ordering).toString() : null;
    }

    private void flushFontData() {
        if (cidFontType == CID_FONT_TYPE_0) {
            getPdfObject().put(PdfName.Type, PdfName.Font);
            getPdfObject().put(PdfName.Subtype, PdfName.Type0);
            String name = fontProgram.getFontNames().getFontName();
            String style = fontProgram.getFontNames().getStyle();
            if (style.length() > 0) {
                name += "-" + style;
            }
            getPdfObject().put(PdfName.BaseFont, new PdfName(MessageFormatUtil.format("{0}-{1}", name, cmapEncoding.getCmapName())));
            getPdfObject().put(PdfName.Encoding, new PdfName(cmapEncoding.getCmapName()));
            PdfDictionary fontDescriptor = getFontDescriptor(name);
            int[] metrics = hashSetToArray(longTag);
            Arrays.sort(metrics);
            PdfDictionary cidFont = getCidFontType2(null, fontDescriptor, fontProgram.getFontNames().getFontName(), metrics);
            getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));

            // getPdfObject().getIndirectReference() != null by assertion of PdfType0Font#flush()
            //this means, that fontDescriptor and cidFont already are indirects
            fontDescriptor.flush();
            cidFont.flush();
        } else if (cidFontType == CID_FONT_TYPE_2) {
            TrueTypeFont ttf = (TrueTypeFont) getFontProgram();
            addRangeUni(ttf, longTag);
            int[] metrics = hashSetToArray(longTag);
            Arrays.sort(metrics);
            PdfStream fontStream;
            String fontName = updateSubsetPrefix(ttf.getFontNames().getFontName(), subset, embedded);
            PdfDictionary fontDescriptor = getFontDescriptor(fontName);
            if (ttf.isCff()) {
                byte[] cffBytes = ttf.getFontStreamBytes();
                if (subset || subsetRanges != null) {
                    CFFFontSubset cff = new CFFFontSubset(ttf.getFontStreamBytes(), longTag);
                    cffBytes = cff.Process(cff.getNames()[0]);
                }
                fontStream = getPdfFontStream(cffBytes, new int[]{cffBytes.length});
                fontStream.put(PdfName.Subtype, new PdfName("CIDFontType0C"));
                // The PDF Reference manual advises to add -cmap in case CIDFontType0
                getPdfObject().put(PdfName.BaseFont,
                        new PdfName(MessageFormatUtil.format("{0}-{1}", fontName, cmapEncoding.getCmapName())));
                fontDescriptor.put(PdfName.FontFile3, fontStream);
            } else {
                byte[] ttfBytes = null;
                if (subset || ttf.getDirectoryOffset() != 0) {
                    try {
                        ttfBytes = ttf.getSubset(new HashSet<>(longTag), true);
                    } catch (com.itextpdf.io.IOException e) {
                        Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
                        logger.warn(LogMessageConstant.FONT_SUBSET_ISSUE);
                        ttfBytes = null;
                    }
                }
                if (ttfBytes == null) {
                    ttfBytes = ttf.getFontStreamBytes();
                }
                fontStream = getPdfFontStream(ttfBytes, new int[]{ttfBytes.length});
                getPdfObject().put(PdfName.BaseFont, new PdfName(fontName));
                fontDescriptor.put(PdfName.FontFile2, fontStream);
            }

            // CIDSet shall be based on font.numberOfGlyphs property of the font, it is maxp.numGlyphs for ttf,
            // because technically we convert all unused glyphs to space, e.g. just remove outlines.
            int numOfGlyphs = ttf.getFontMetrics().getNumberOfGlyphs();
            byte[] cidSetBytes = new byte[ttf.getFontMetrics().getNumberOfGlyphs() / 8 + 1];
            for (int i = 0; i < numOfGlyphs / 8; i++) {
                cidSetBytes[i] |= 0xff;
            }
            for (int i = 0; i < numOfGlyphs % 8; i++) {
                cidSetBytes[cidSetBytes.length - 1] |= rotbits[i];
            }
            fontDescriptor.put(PdfName.CIDSet, new PdfStream(cidSetBytes));
            PdfDictionary cidFont = getCidFontType2(ttf, fontDescriptor, fontName, metrics);

            getPdfObject().put(PdfName.Type, PdfName.Font);
            getPdfObject().put(PdfName.Subtype, PdfName.Type0);
            getPdfObject().put(PdfName.Encoding, new PdfName(cmapEncoding.getCmapName()));
            getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));

            PdfStream toUnicode = getToUnicode(metrics);
            if (toUnicode != null) {
                getPdfObject().put(PdfName.ToUnicode, toUnicode);
                if (toUnicode.getIndirectReference() != null) {
                    toUnicode.flush();
                }
            }

            // getPdfObject().getIndirectReference() != null by assertion of PdfType0Font#flush()
            // This means, that fontDescriptor, cidFont and fontStream already are indirects
            if (getPdfObject().getIndirectReference().getDocument().getPdfVersion().compareTo(PdfVersion.PDF_2_0) >= 0) {
                // CIDSet is deprecated in PDF 2.0
                fontDescriptor.remove(PdfName.CIDSet);
            }
            fontDescriptor.flush();
            cidFont.flush();
            fontStream.flush();
        } else {
            throw new IllegalStateException("Unsupported CID Font");
        }
    }

    /**
     * Generates the CIDFontTyte2 dictionary.
     *
     * @param fontDescriptor the indirect reference to the font descriptor
     * @param fontName       a name of the font
     * @param metrics        the horizontal width metrics
     * @return fully initialized CIDFont
     */
    protected PdfDictionary getCidFontType2(TrueTypeFont ttf, PdfDictionary fontDescriptor, String fontName, int[] metrics) {
        PdfDictionary cidFont = new PdfDictionary();
        markObjectAsIndirect(cidFont);
        cidFont.put(PdfName.Type, PdfName.Font);
        // sivan; cff
        cidFont.put(PdfName.FontDescriptor, fontDescriptor);
        if (ttf == null || ttf.isCff()) {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        } else {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType2);
            cidFont.put(PdfName.CIDToGIDMap, PdfName.Identity);
        }
        cidFont.put(PdfName.BaseFont, new PdfName(fontName));
        PdfDictionary cidInfo = new PdfDictionary();
        cidInfo.put(PdfName.Registry, new PdfString(cmapEncoding.getRegistry()));
        cidInfo.put(PdfName.Ordering, new PdfString(cmapEncoding.getOrdering()));
        cidInfo.put(PdfName.Supplement, new PdfNumber(cmapEncoding.getSupplement()));
        cidFont.put(PdfName.CIDSystemInfo, cidInfo);
        if (!vertical) {
            cidFont.put(PdfName.DW, new PdfNumber(FontProgram.DEFAULT_WIDTH));
            StringBuilder buf = new StringBuilder("[");
            int lastNumber = -10;
            boolean firstTime = true;
            for (int code : metrics) {
                Glyph glyph = fontProgram.getGlyphByCode(code);
                if (glyph.getWidth() == FontProgram.DEFAULT_WIDTH) {
                    continue;
                }
                if (glyph.getCode() == lastNumber + 1) {
                    buf.append(' ').append(glyph.getWidth());
                } else {
                    if (!firstTime) {
                        buf.append(']');
                    }
                    firstTime = false;
                    buf.append(glyph.getCode()).append('[').append(glyph.getWidth());
                }
                lastNumber = glyph.getCode();
            }
            if (buf.length() > 1) {
                buf.append("]]");
                cidFont.put(PdfName.W, new PdfLiteral(buf.toString()));
            }
        } else {
            throw new UnsupportedOperationException("Vertical writing has not implemented yet.");
        }
        return cidFont;
    }

    /**
     * Creates a ToUnicode CMap to allow copy and paste from Acrobat.
     *
     * @param metrics metrics[0] contains the glyph index and metrics[2]
     *                contains the Unicode code
     * @return the stream representing this CMap or <CODE>null</CODE>
     */
    public PdfStream getToUnicode(int[] metrics) {
        ArrayList<Integer> unicodeGlyphs = new ArrayList<>(metrics.length);
        for (int i = 0; i < metrics.length; i++) {
            int code = metrics[i];
            if (fontProgram.getGlyphByCode(code).getChars() != null) {
                unicodeGlyphs.add(code);
            }
        }
        if (unicodeGlyphs.size() == 0)
            return null;
        StringBuilder buf = new StringBuilder(
                "/CIDInit /ProcSet findresource begin\n" +
                        "12 dict begin\n" +
                        "begincmap\n" +
                        "/CIDSystemInfo\n" +
                        "<< /Registry (Adobe)\n" +
                        "/Ordering (UCS)\n" +
                        "/Supplement 0\n" +
                        ">> def\n" +
                        "/CMapName /Adobe-Identity-UCS def\n" +
                        "/CMapType 2 def\n" +
                        "1 begincodespacerange\n" +
                        "<0000><FFFF>\n" +
                        "endcodespacerange\n");
        int size = 0;
        for (int k = 0; k < unicodeGlyphs.size(); ++k) {
            if (size == 0) {
                if (k != 0) {
                    buf.append("endbfrange\n");
                }
                size = Math.min(100, unicodeGlyphs.size() - k);
                buf.append(size).append(" beginbfrange\n");
            }
            --size;
            String fromTo = CMapContentParser.toHex((int) unicodeGlyphs.get(k));
            Glyph glyph = fontProgram.getGlyphByCode((int) unicodeGlyphs.get(k));
            if (glyph.getChars() != null) {
                StringBuilder uni = new StringBuilder(glyph.getChars().length);
                for (char ch : glyph.getChars()) {
                    uni.append(toHex4(ch));
                }
                buf.append(fromTo).append(fromTo).append('<').append(uni.toString()).append('>').append('\n');
            }
        }
        buf.append("endbfrange\n" +
                "endcmap\n" +
                "CMapName currentdict /CMap defineresource pop\n" +
                "end end\n");
        return new PdfStream(PdfEncodings.convertToBytes(buf.toString(), null));
    }

    //TODO optimize memory ussage
    private static String toHex4(char ch) {
        String s = "0000" + Integer.toHexString(ch);
        return s.substring(s.length() - 4);
    }

    protected void addRangeUni(TrueTypeFont ttf, Set<Integer> longTag) {
        if (!subset && (subsetRanges != null || ttf.getDirectoryOffset() > 0)) {
            int[] rg = subsetRanges == null && ttf.getDirectoryOffset() > 0
                    ? new int[]{0, 0xffff} : compactRanges(subsetRanges);
            Map<Integer, int[]> usemap = ttf.getActiveCmap();
            assert usemap != null;
            for (Map.Entry<Integer, int[]> e : usemap.entrySet()) {
                int[] v = e.getValue();
                int gi = v[0];
                if (longTag.contains(v[0])) {
                    continue;
                }
                int c = e.getKey();
                boolean skip = true;
                for (int k = 0; k < rg.length; k += 2) {
                    if (c >= rg[k] && c <= rg[k + 1]) {
                        skip = false;
                        break;
                    }
                }
                if (!skip) {
                    longTag.add(gi);
                }
            }
        }
    }

    private String getCompatibleUniMap(String registry) {
        String uniMap = "";
        for (String name : CidFontProperties.getRegistryNames().get(registry + "_Uni")) {
            uniMap = name;
            if (name.endsWith("V") && vertical) {
                break;
            } else if (!name.endsWith("V") && !vertical) {
                break;
            }
        }
        return uniMap;
    }

    private static CMapEncoding createCMap(PdfObject cmap, String uniMap) {
        if (cmap.isStream()) {
            PdfStream cmapStream = (PdfStream) cmap;
            byte[] cmapBytes = cmapStream.getBytes();
            return new CMapEncoding(cmapStream.getAsName(PdfName.CMapName).getValue(), cmapBytes);
        } else {
            String cmapName = ((PdfName) cmap).getValue();
            if (PdfEncodings.IDENTITY_H.equals(cmapName) || PdfEncodings.IDENTITY_V.equals(cmapName)) {
                return new CMapEncoding(cmapName);
            } else {
                return new CMapEncoding(cmapName, uniMap);
            }
        }
    }

    private static int[] hashSetToArray(Set<Integer> set) {
        int[] res = new int[set.size()];
        int i = 0;
        for (int n : set) {
            res[i++] = n;
        }
        return res;
    }
}
