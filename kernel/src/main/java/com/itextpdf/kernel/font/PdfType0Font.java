/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.font;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.CFFFontSubset;
import com.itextpdf.io.font.CMapEncoding;
import com.itextpdf.io.font.CidFont;
import com.itextpdf.io.font.CidFontProperties;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.cmap.CMapCharsetEncoder;
import com.itextpdf.io.font.cmap.CMapContentParser;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.cmap.StandardCMapCharsets;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.OutputStream;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfType0Font extends PdfFont {

    /**
     * This is the default encoding to use.
     */
    private static final String DEFAULT_ENCODING = "";

    /**
     * The code length shall not be greater than 4.
     */
    private static final int MAX_CID_CODE_LENGTH = 4;
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
    protected Set<Integer> usedGlyphs;
    protected int cidFontType;
    protected char[] specificUnicodeDifferences;

    private final CMapToUnicode embeddedToUnicode;

    PdfType0Font(TrueTypeFont ttf, String cmap) {
        super();
        if (!PdfEncodings.IDENTITY_H.equals(cmap) && !PdfEncodings.IDENTITY_V.equals(cmap)) {
            throw new PdfException(KernelExceptionMessageConstant.ONLY_IDENTITY_CMAPS_SUPPORTS_WITH_TRUETYPE);
        }

        if (!ttf.getFontNames().allowEmbedding()) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_BE_EMBEDDED_DUE_TO_LICENSING_RESTRICTIONS)
                    .setMessageParams(ttf.getFontNames().getFontName() + ttf.getFontNames().getStyle());
        }
        this.fontProgram = ttf;
        this.embedded = true;
        vertical = cmap.endsWith("V");
        cmapEncoding = new CMapEncoding(cmap);
        usedGlyphs = new TreeSet<>();
        cidFontType = CID_FONT_TYPE_2;
        embeddedToUnicode = null;
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
        usedGlyphs = new TreeSet<>();
        cidFontType = CID_FONT_TYPE_0;
        embeddedToUnicode = null;
    }

    PdfType0Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        newFont = false;
        PdfDictionary cidFont = fontDictionary.getAsArray(PdfName.DescendantFonts).getAsDictionary(0);
        PdfObject cmap = fontDictionary.get(PdfName.Encoding);

        String ordering = getOrdering(cidFont);
        if(ordering == null) {
            throw new PdfException(KernelExceptionMessageConstant.ORDERING_SHOULD_BE_DETERMINED);
        }
        CMapToUnicode toUnicodeCMap;
        PdfObject toUnicode = fontDictionary.get(PdfName.ToUnicode);
        if (toUnicode == null) {
            toUnicodeCMap = FontUtil.parseUniversalToUnicodeCMap(ordering);
            embeddedToUnicode = null;
        } else {
            toUnicodeCMap = FontUtil.processToUnicode(toUnicode);
            embeddedToUnicode = toUnicodeCMap;
        }

        if (cmap.isName() && (PdfEncodings.IDENTITY_H.equals(((PdfName) cmap).getValue()) ||
                PdfEncodings.IDENTITY_V.equals(((PdfName) cmap).getValue()))) {

            if (toUnicodeCMap == null) {
                String uniMap = getUniMapFromOrdering(ordering, PdfEncodings.IDENTITY_H.equals(((PdfName) cmap).getValue()));
                toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(uniMap);
                if (toUnicodeCMap == null) {
                    toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(PdfEncodings.IDENTITY_H);
                    Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
                    logger.error(MessageFormatUtil.format(IoLogMessageConstant.UNKNOWN_CMAP, uniMap));
                }
            }
            fontProgram = DocTrueTypeFont.createFontProgram(cidFont, toUnicodeCMap);
            cmapEncoding = createCMap(cmap, null);
            assert fontProgram instanceof IDocFontProgram;
            embedded = ((IDocFontProgram) fontProgram).getFontFile() != null;
        } else {
            String cidFontName = cidFont.getAsName(PdfName.BaseFont).getValue();
            String uniMap = getUniMapFromOrdering(ordering, true);
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
                throw new PdfException(MessageFormatUtil.format(
                        KernelExceptionMessageConstant.CANNOT_RECOGNISE_DOCUMENT_FONT_WITH_ENCODING,
                        cidFontName, cmap));
            }
        }
        // DescendantFonts is a one-element array specifying the CIDFont dictionary
        // that is the descendant of this Type 0 font.
        PdfDictionary cidFontDictionary = fontDictionary.getAsArray(PdfName.DescendantFonts).getAsDictionary(0);
        // Required according to the spec
        PdfName subtype = cidFontDictionary.getAsName(PdfName.Subtype);
        if (PdfName.CIDFontType0.equals(subtype)) {
            cidFontType = CID_FONT_TYPE_0;
        } else if (PdfName.CIDFontType2.equals(subtype)) {
            cidFontType = CID_FONT_TYPE_2;
        } else {
            LoggerFactory.getLogger(getClass()).error(IoLogMessageConstant.FAILED_TO_DETERMINE_CID_FONT_SUBTYPE);
        }
        usedGlyphs = new TreeSet<>();
        subset = false;
    }

    /**
     * Get Unicode mapping name from ordering.
     * @param ordering the text ordering to base to unicode mapping on
     * @param horizontal identifies whether the encoding is horizontal or vertical
     *
     * @return Unicode mapping name
     */
    public static String getUniMapFromOrdering(String ordering, boolean horizontal) {
        String result = null;
        switch (ordering) {
            case "CNS1":
                result = "UniCNS-UTF16-";
                break;
            case "Japan1":
                result = "UniJIS-UTF16-";
                break;
            case "Korea1":
                result = "UniKS-UTF16-";
                break;
            case "GB1":
                result = "UniGB-UTF16-";
                break;
            case "Identity":
                result = "Identity-";
                break;
            default:
                return null;
        }
        if (horizontal) {
            return result + 'H';
        }
        return result + 'V';
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

    private byte[] convertToBytesUsingCMap(String text) {
        int len = text.length();
        ByteBuffer buffer = new ByteBuffer();
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
                    buffer.append(cmapEncoding.getCmapBytes(0));
                }
            }
        }
        return buffer.toByteArray();
    }

    @Override
    public byte[] convertToBytes(String text) {
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder(cmapEncoding.getCmapName());
        if (encoder == null) {
            return this.convertToBytesUsingCMap(text);
        } else {
            return converToBytesUsingEncoder(text, encoder);
        }
    }

    private byte[] converToBytesUsingEncoder(String text, CMapCharsetEncoder encoder) {
        java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
        int[] codePoints = TextUtil.convertToUtf32(text);
        for (int cp : codePoints) {
            try {
                stream.write(encoder.encodeUnicodeCodePoint(cp));
                Glyph glyph = getGlyph(cp);
                if (glyph.getCode() > 0) {
                    usedGlyphs.add(glyph.getCode());
                }
            } catch (IOException e) {
                // can only be thrown when stream is closed
                throw new ITextException(e);
            }
        }
        return stream.toByteArray();
    }

    @Override
    public byte[] convertToBytes(GlyphLine glyphLine) {
        if (glyphLine == null) {
            return new byte[0];
        }
        // NOTE: this isn't particularly efficient, but it demonstrates the principle behind CMap-less conversion
        // (i.e. we only use the CMap's name to derive the correct encoding)
        // Also, it will yield wrong results when used in an embedded setting where font features have been applied
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder(cmapEncoding.getCmapName());
        if (encoder == null) {
            int totalByteCount = 0;
            for (int i = glyphLine.start; i < glyphLine.end; i++) {
                totalByteCount += cmapEncoding.getCmapBytesLength(glyphLine.get(i).getCode());
            }
            // perform actual conversion
            byte[] bytes = new byte[totalByteCount];
            int offset = 0;
            for (int i = glyphLine.start; i < glyphLine.end; i++) {
                usedGlyphs.add(glyphLine.get(i).getCode());
                offset = cmapEncoding.fillCmapBytes(glyphLine.get(i).getCode(), bytes, offset);
            }
            return bytes;
        } else {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            for (int i = glyphLine.start; i < glyphLine.end; i++) {
                Glyph g = glyphLine.get(i);
                usedGlyphs.add(g.getCode());
                byte[] encodedBit = encoder.encodeUnicodeCodePoint(g.getUnicode());
                try {
                    baos.write(encodedBit);
                } catch (IOException e) {
                    // could only be thrown when the stream is closed
                    throw new PdfException(e);
                }
            }
            return baos.toByteArray();
        }
    }

    @Override
    public byte[] convertToBytes(Glyph glyph) {
        usedGlyphs.add(glyph.getCode());
        CMapCharsetEncoder encoder = StandardCMapCharsets.getEncoder(cmapEncoding.getCmapName());
        if (encoder == null) {
            return cmapEncoding.getCmapBytes(glyph.getCode());
        } else {
            int cp = glyph.getUnicode();
            return encoder.encodeUnicodeCodePoint(cp);
        }
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
                // Since a pair is processed, need to skip next char as well
                k += 1;
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
    public GlyphLine decodeIntoGlyphLine(PdfString characterCodes) {
        List<Glyph> glyphs = new ArrayList<>();
        appendDecodedCodesToGlyphsList(glyphs, characterCodes);
        return new GlyphLine(glyphs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean appendDecodedCodesToGlyphsList(List<Glyph> list, PdfString characterCodes) {
        boolean allCodesDecoded = true;

        final boolean isToUnicodeEmbedded = embeddedToUnicode != null;
        final CMapEncoding cmap = getCmap();
        final FontProgram fontProgram = getFontProgram();
        final List<byte[]> codeSpaceRanges = isToUnicodeEmbedded ? embeddedToUnicode.getCodeSpaceRanges() : cmap.getCodeSpaceRanges();

        String charCodesSequence = characterCodes.getValue();
        // A sequence of one or more bytes shall be extracted from the string and matched against the codespace
        // ranges in the CMap. That is, the first byte shall be matched against 1-byte codespace ranges; if no match is
        // found, a second byte shall be extracted, and the 2-byte code shall be matched against 2-byte codespace
        // ranges. This process continues for successively longer codes until a match is found or all codespace ranges
        // have been tested. There will be at most one match because codespace ranges shall not overlap.
        for (int i = 0; i < charCodesSequence.length(); i++) {
            int code = 0;
            Glyph glyph = null;
            int codeSpaceMatchedLength = 1;
            for (int codeLength = 1; codeLength <= MAX_CID_CODE_LENGTH && i + codeLength <= charCodesSequence.length();
                    codeLength++) {
                code = (code << 8) + charCodesSequence.charAt(i + codeLength - 1);

                if (PdfType0Font.containsCodeInCodeSpaceRange(codeSpaceRanges, code, codeLength)) {
                    codeSpaceMatchedLength = codeLength;
                } else {
                    continue;
                }

                // According to paragraph 9.10.2 of PDF Specification ISO 32000-2, if toUnicode is embedded, it is
                // necessary to use it to map directly code points to unicode. If not embedded, use CMap to map code
                // points to CIDs and then CIDFont to map CIDs to unicode.
                int glyphCode = isToUnicodeEmbedded ? code : cmap.getCidCode(code);
                glyph = fontProgram.getGlyphByCode(glyphCode);
                if (glyph != null) {
                    i += codeLength - 1;
                    break;
                }
            }
            if (glyph == null) {
                Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
                if (logger.isWarnEnabled()) {
                    StringBuilder failedCodes = new StringBuilder();
                    for (int codeLength = 1;
                            codeLength <= MAX_CID_CODE_LENGTH && i + codeLength <= charCodesSequence.length();
                            codeLength++) {
                        failedCodes.append((int) charCodesSequence.charAt(i + codeLength - 1)).append(" ");
                    }
                    logger.warn(MessageFormatUtil
                            .format(IoLogMessageConstant.COULD_NOT_FIND_GLYPH_WITH_CODE, failedCodes.toString()));
                }
                i += codeSpaceMatchedLength - 1;
            }
            if (glyph == null || glyph.getChars() == null) {
                list.add(new Glyph(0, fontProgram.getGlyphByCode(0).getWidth(), -1));
                allCodesDecoded = false;
            } else {
                list.add(glyph);
            }
        }
        return allCodesDecoded;
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
        return getFontProgram().isBuiltWith(fontProgram)
                && cmapEncoding.isBuiltWith(normalizeEncoding(encoding));
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

    private void convertToBytes(Glyph glyph, ByteBuffer result) {
        // NOTE: this should only ever be called with the identity CMap in RES-403
        int code = glyph.getCode();
        usedGlyphs.add(code);
        cmapEncoding.fillCmapBytes(code, result);
    }

    private static String getOrdering(PdfDictionary cidFont) {
        PdfDictionary cidinfo = cidFont.getAsDictionary(PdfName.CIDSystemInfo);
        if (cidinfo == null)
            return null;
        return cidinfo.containsKey(PdfName.Ordering) ? cidinfo.get(PdfName.Ordering).toString() : null;
    }

    private static boolean containsCodeInCodeSpaceRange(List<byte[]> codeSpaceRanges, int code, int length) {
        for (int i = 0; i < codeSpaceRanges.size(); i += 2) {
            if (length == codeSpaceRanges.get(i).length) {
                int mask = 0xff;
                int totalShift = 0;
                byte[] low = codeSpaceRanges.get(i);
                byte[] high = codeSpaceRanges.get(i + 1);
                boolean fitsIntoRange = true;
                for (int ind = length - 1; ind >= 0; ind--, totalShift += 8, mask <<= 8) {
                    int actualByteValue = (code & mask) >> totalShift;
                    if (!(actualByteValue >= (0xff & low[ind]) && actualByteValue <= (0xff & high[ind]))) {
                        fitsIntoRange = false;
                    }
                }
                if (fitsIntoRange) {
                    return true;
                }
            }
        }
        return false;
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
            PdfDictionary cidFont = getCidFont(fontDescriptor, fontProgram.getFontNames().getFontName(), false);
            getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));

            fontDescriptor.flush();
            cidFont.flush();
        } else if (cidFontType == CID_FONT_TYPE_2) {
            TrueTypeFont ttf = (TrueTypeFont) getFontProgram();
            String fontName = updateSubsetPrefix(ttf.getFontNames().getFontName(), subset, embedded);
            PdfDictionary fontDescriptor = getFontDescriptor(fontName);

            PdfStream fontStream;
            ttf.updateUsedGlyphs((SortedSet<Integer>) usedGlyphs, subset, subsetRanges);
            if (ttf.isCff()) {
                byte[] cffBytes;
                if (subset) {
                    byte[] bytes = ttf.getFontStreamBytes();
                    Set<Integer> usedGids = ttf.mapGlyphsCidsToGids(usedGlyphs);
                    cffBytes = new CFFFontSubset(bytes, usedGids).Process();
                } else {
                    cffBytes = ttf.getFontStreamBytes();
                }
                fontStream = getPdfFontStream(cffBytes, new int[]{cffBytes.length});
                fontStream.put(PdfName.Subtype, new PdfName("CIDFontType0C"));
                // The PDF Reference manual advises to add -cmap in case CIDFontType0
                getPdfObject().put(PdfName.BaseFont,
                        new PdfName(MessageFormatUtil.format("{0}-{1}", fontName, cmapEncoding.getCmapName())));
                fontDescriptor.put(PdfName.FontFile3, fontStream);
            } else {
                byte[] ttfBytes = null;
                //getDirectoryOffset() > 0 means ttc, which shall be subsetted anyway.
                if (subset || ttf.getDirectoryOffset() > 0) {
                    try {
                        ttfBytes = ttf.getSubset(usedGlyphs, subset);
                    } catch (com.itextpdf.io.exceptions.IOException e) {
                        Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
                        logger.warn(IoLogMessageConstant.FONT_SUBSET_ISSUE);
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
            PdfDictionary cidFont = getCidFont(fontDescriptor, fontName, !ttf.isCff());

            getPdfObject().put(PdfName.Type, PdfName.Font);
            getPdfObject().put(PdfName.Subtype, PdfName.Type0);
            getPdfObject().put(PdfName.Encoding, new PdfName(cmapEncoding.getCmapName()));
            getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));

            PdfStream toUnicode = getToUnicode();
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
     * Generates the CIDFontType2 dictionary.
     *
     * @param fontDescriptor the font descriptor dictionary
     * @param fontName       a name of the font
     * @param isType2        true, if the font is CIDFontType2 (TrueType glyphs),
     *                       otherwise false, i.e. CIDFontType0 (Type1/CFF glyphs)
     * @return fully initialized CIDFont
     */
    protected PdfDictionary getCidFont(PdfDictionary fontDescriptor, String fontName, boolean isType2) {
        PdfDictionary cidFont = new PdfDictionary();
        markObjectAsIndirect(cidFont);
        cidFont.put(PdfName.Type, PdfName.Font);
        // sivan; cff
        cidFont.put(PdfName.FontDescriptor, fontDescriptor);
        if (isType2) {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType2);
            cidFont.put(PdfName.CIDToGIDMap, PdfName.Identity);
        } else {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        }
        cidFont.put(PdfName.BaseFont, new PdfName(fontName));
        PdfDictionary cidInfo = new PdfDictionary();
        cidInfo.put(PdfName.Registry, new PdfString(cmapEncoding.getRegistry()));
        cidInfo.put(PdfName.Ordering, new PdfString(cmapEncoding.getOrdering()));
        cidInfo.put(PdfName.Supplement, new PdfNumber(cmapEncoding.getSupplement()));
        cidFont.put(PdfName.CIDSystemInfo, cidInfo);
        if (!vertical) {
            cidFont.put(PdfName.DW, new PdfNumber(FontProgram.DEFAULT_WIDTH));
            PdfObject widthsArray = generateWidthsArray();
            if (widthsArray != null) {
                cidFont.put(PdfName.W, widthsArray);
            }
        } else {
            // TODO DEVSIX-31
            Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
            logger.warn("Vertical writing has not been implemented yet.");
        }
        return cidFont;
    }

    private PdfObject generateWidthsArray() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        OutputStream<ByteArrayOutputStream> stream = new OutputStream<>(bytes);
        stream.writeByte('[');
        int lastNumber = -10;
        boolean firstTime = true;
        for (int code : usedGlyphs) {
            Glyph glyph = fontProgram.getGlyphByCode(code);
            if (glyph.getWidth() == FontProgram.DEFAULT_WIDTH) {
                continue;
            }
            if (glyph.getCode() == lastNumber + 1) {
                stream.writeByte(' ');
            } else {
                if (!firstTime) {
                    stream.writeByte(']');
                }
                firstTime = false;
                stream.writeInteger(glyph.getCode());
                stream.writeByte('[');
            }
            stream.writeInteger(glyph.getWidth());
            lastNumber = glyph.getCode();
        }
        if (stream.getCurrentPos() > 1) {
            stream.writeString("]]");
            return new PdfLiteral(bytes.toByteArray());
        }
        return null;
    }

    /**
     * Creates a ToUnicode CMap to allow copy and paste from Acrobat.
     *
     * @return the stream representing this CMap or <CODE>null</CODE>
     */
    public PdfStream getToUnicode() {
        OutputStream<ByteArrayOutputStream> stream = new OutputStream<>(new ByteArrayOutputStream());
        stream.writeString("/CIDInit /ProcSet findresource begin\n" +
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

        //accumulate long tag into a subset and write it.
        ArrayList<Glyph> glyphGroup = new ArrayList<>(100);

        int bfranges = 0;
        for (Integer glyphId : usedGlyphs) {
            Glyph glyph = fontProgram.getGlyphByCode((int) glyphId);
            if (glyph.getChars() != null) {
                glyphGroup.add(glyph);
                if (glyphGroup.size() == 100) {
                    bfranges += writeBfrange(stream, glyphGroup);
                }
            }
        }
        //flush leftovers
        bfranges += writeBfrange(stream, glyphGroup);

        if (bfranges == 0)
            return null;

        stream.writeString("endcmap\n" +
                "CMapName currentdict /CMap defineresource pop\n" +
                "end end\n");
        return new PdfStream(((ByteArrayOutputStream)stream.getOutputStream()).toByteArray());
    }

    private int writeBfrange(OutputStream<ByteArrayOutputStream> stream, List<Glyph> range) {
        if (range.isEmpty()) return 0;
        stream.writeInteger(range.size());
        stream.writeString(" beginbfrange\n");
        for (Glyph glyph: range) {
            String fromTo = CMapContentParser.toHex(glyph.getCode());
            stream.writeString(fromTo);
            stream.writeString(fromTo);
            stream.writeByte('<');
            for (char ch : glyph.getChars()) {
                stream.writeString(toHex4(ch));
            }
            stream.writeByte('>');
            stream.writeByte('\n');
        }
        stream.writeString("endbfrange\n");
        range.clear();
        return 1;
    }

    private static String toHex4(char ch) {
        String s = "0000" + Integer.toHexString(ch);
        return s.substring(s.length() - 4);
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

    private static String normalizeEncoding(String encoding) {
        return null == encoding || DEFAULT_ENCODING.equals(encoding)
                ? PdfEncodings.IDENTITY_H
                : encoding;
    }
}
