/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfType0Font extends PdfFont {

    private static final long serialVersionUID = -8033620300884193397L;

    private static final byte[] rotbits = {(byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10, (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01};

    protected static final int CID_FONT_TYPE_0 = 0;
    protected static final int CID_FONT_TYPE_2 = 2;

    protected boolean vertical;
    protected CMapEncoding cmapEncoding;
    protected Map<Integer, int[]> longTag;
    protected int cidFontType;
    protected char[] specificUnicodeDifferences;

    PdfType0Font(TrueTypeFont ttf, String cmap) {
        super();
        if (!cmap.equals(PdfEncodings.IDENTITY_H) && !cmap.equals(PdfEncodings.IDENTITY_V)) {
            throw new PdfException("only.identity.cmaps.supports.with.truetype");
        }

        if (!ttf.getFontNames().allowEmbedding()) {
            throw new PdfException("1.cannot.be.embedded.due.to.licensing.restrictions")
                    .setMessageParams(ttf.getFontNames().getFontName() + ttf.getFontNames().getStyle());
        }
        this.fontProgram = ttf;
        this.embedded = true;
        vertical = cmap.endsWith("V");
        cmapEncoding = new CMapEncoding(cmap);
        longTag = new LinkedHashMap<>();
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
            throw new PdfException("font.1.with.2.encoding.is.not.a.cjk.font")
                    .setMessageParams(font.getFontNames().getFontName(), cmap);
        }
        this.fontProgram = font;
        vertical = cmap.endsWith("V");
        String uniMap = getCompatibleUniMap(fontProgram.getRegistry());
        cmapEncoding = new CMapEncoding(cmap, uniMap);
        longTag = new LinkedHashMap<>();
        cidFontType = CID_FONT_TYPE_0;
    }

    PdfType0Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        checkFontDictionary(fontDictionary, PdfName.Type0);
        newFont = false;
        PdfDictionary cidFont = fontDictionary.getAsArray(PdfName.DescendantFonts).getAsDictionary(0);
        String cmap = fontDictionary.getAsName(PdfName.Encoding).getValue();
        if (PdfEncodings.IDENTITY_H.equals(cmap) || PdfEncodings.IDENTITY_V.equals(cmap)) {
            PdfObject toUnicode = fontDictionary.get(PdfName.ToUnicode);
            CMapToUnicode toUnicodeCMap = FontUtil.processToUnicode(toUnicode);
            if (toUnicodeCMap == null) {
                String uniMap = getUniMapFromOrdering(getOrdering(cidFont));
                toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(uniMap);
                if (toUnicodeCMap == null) {
                    toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(PdfEncodings.IDENTITY_H);
                    Logger logger = LoggerFactory.getLogger(PdfType0Font.class);
                    logger.error(MessageFormat.format(LogMessageConstant.UNKNOWN_CMAP, uniMap));
                }
            }
            fontProgram = DocTrueTypeFont.createFontProgram(cidFont, toUnicodeCMap);
            cmapEncoding = new CMapEncoding(cmap);
            assert fontProgram instanceof IDocFontProgram;
            embedded = ((IDocFontProgram) fontProgram).getFontFile() != null;
            cidFontType = CID_FONT_TYPE_2;
        } else {
            String cidFontName = cidFont.getAsName(PdfName.BaseFont).getValue();
            String uniMap = getUniMapFromOrdering(getOrdering(cidFont));
            if (uniMap != null && uniMap.startsWith("Uni")
                    && CidFontProperties.isCidFont(cidFontName, uniMap)) {
                try {
                    fontProgram = FontProgramFactory.createFont(cidFontName);
                    cmapEncoding = new CMapEncoding(cmap, uniMap);
                    embedded = false;
                } catch (IOException ignored) {
                    fontProgram = null;
                    cmapEncoding = null;
                }
            } else {
                CMapToUnicode toUnicodeCMap = FontUtil.getToUnicodeFromUniMap(uniMap);
                if (toUnicodeCMap != null) {
                    fontProgram = DocTrueTypeFont.createFontProgram(cidFont, toUnicodeCMap);
                    cmapEncoding = new CMapEncoding(cmap, uniMap);
                }
            }
            if (fontProgram == null) {
                throw new PdfException(MessageFormat.format("Cannot recognise document font {0} with {1} encoding", cidFontName, cmap));
            }
            cidFontType = CID_FONT_TYPE_0;
        }
        longTag = new LinkedHashMap<>();
        subset = false;
    }

    private static String getOrdering(PdfDictionary cidFont) {
        PdfDictionary cidinfo = cidFont.getAsDictionary(PdfName.CIDSystemInfo);
        if (cidinfo == null)
            return null;
        return cidinfo.containsKey(PdfName.Ordering) ? cidinfo.get(PdfName.Ordering).toString() : null;
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
    public byte[] convertToBytes(String text) {
        int len = text.length();
        char[] glyphs = new char[len];
        int i = 0;
        if (fontProgram.isFontSpecific()) {
            byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
            len = b.length;
            for (int k = 0; k < len; ++k) {
                Glyph glyph = fontProgram.getGlyph(b[k] & 0xff);
                if (glyph != null && !longTag.containsKey(glyph.getCode())) {
                    longTag.put(glyph.getCode(), new int[]{glyph.getCode(), glyph.getWidth(),
                            glyph.hasValidUnicode() ? glyph.getUnicode() : 0});
                    glyphs[i++] = (char) cmapEncoding.getCmapCode(glyph.getCode());
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
                Glyph glyph = fontProgram.getGlyph(val);
                if (glyph == null) {
                    glyph = fontProgram.getGlyphByCode(0);
                }
                if (!longTag.containsKey(glyph.getCode())) {
                    longTag.put(glyph.getCode(), new int[]{glyph.getCode(), glyph.getWidth(),
                            glyph.hasValidUnicode() ? glyph.getUnicode() : 0});
                }
                glyphs[i++] = (char) cmapEncoding.getCmapCode(glyph.getCode());
            }
        }
        return PdfEncodings.convertToBytes(new String(glyphs, 0, i), PdfEncodings.UNICODE_BIG_UNMARKED);
    }

    @Override
    public byte[] convertToBytes(GlyphLine glyphLine) {
        if (glyphLine != null) {
            char[] glyphs = new char[glyphLine.size()];
            for (int i = 0; i < glyphLine.size(); i++) {
                Glyph glyph = glyphLine.get(i);
                glyphs[i] = (char) cmapEncoding.getCmapCode(glyph.getCode());
                int code = glyph.getCode();
                if (longTag.get(code) == null) {
                    longTag.put(code, new int[]{code, glyph.getWidth(), glyph.hasValidUnicode() ? glyph.getUnicode() : 0});
                }
            }
            return PdfEncodings.convertToBytes(new String(glyphs, 0, glyphs.length), PdfEncodings.UNICODE_BIG_UNMARKED);
        } else {
            return null;
        }
    }

    @Override
    public byte[] convertToBytes(Glyph glyph) {
        int code = glyph.getCode();
        if (longTag.get(code) == null) {
            longTag.put(code, new int[]{code, glyph.getWidth(), glyph.hasValidUnicode() ? glyph.getUnicode() : 0});
        }
        return PdfEncodings.convertToBytes(new String(new char[]{(char) glyph.getCode()}, 0, 1), PdfEncodings.UNICODE_BIG_UNMARKED);
    }

    @Override
    public void writeText(GlyphLine text, int from, int to, PdfOutputStream stream) {
        StringBuilder bytes = new StringBuilder();
        for (int i = from; i <= to; i++) {
            Glyph glyph = text.get(i);
            int code = glyph.getCode();
            bytes.append((char) cmapEncoding.getCmapCode(glyph.getCode()));
            if (longTag.get(code) == null) {
                longTag.put(code, new int[]{code, glyph.getWidth(), glyph.hasValidUnicode() ? glyph.getUnicode() : 0});
            }
        }
        //TODO improve converting chars to hexed string
        StreamUtil.writeHexedString(stream, PdfEncodings.convertToBytes(bytes.toString(), PdfEncodings.UNICODE_BIG_UNMARKED));
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
            TrueTypeFont ttf = (TrueTypeFont) fontProgram;
            int len = content.length();

            if (ttf.isFontSpecific()) {
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
            throw new PdfException("font.has.no.suitable.cmap");
        }

        return new GlyphLine(glyphs);
    }

    @Override
    public String decode(PdfString content) {
        String cids = content.getValue();
        if (cids.length() == 1) {
            return "";
        }
        StringBuilder builder = new StringBuilder(cids.length() / 2);
        //number of cids must be even. With i < cids.length() - 1 we garantee, that we will not process the last odd index.
        for (int i = 0; i < cids.length() - 1; i += 2) {
            int code = (cids.charAt(i) << 8) + cids.charAt(i + 1);
            Glyph glyph = fontProgram.getGlyphByCode(cmapEncoding.getCidCode(code));
            if (glyph != null && glyph.getChars() != null) {
                builder.append(glyph.getChars());
            } else {
                builder.append('?');
            }
        }
        return builder.toString();
    }

    @Override
    public float getContentWidth(PdfString content) {
        String cids = content.getValue();
        Glyph notdef = fontProgram.getGlyphByCode(0);
        float width = 0;
        for (int i = 0; i < cids.length(); i++) {
            int code = cids.charAt(i++);
            if (i < cids.length()) {
                code <<= 8;
                code |= cids.charAt(i);
            }
            Glyph glyph = fontProgram.getGlyphByCode(cmapEncoding.getCidCode(code));
            if (glyph == null)
                System.err.println(code);
            width += glyph != null ? glyph.getWidth() : notdef.getWidth();
        }
        return width;
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

    public CMapEncoding getCmap() {
        return cmapEncoding;
    }

    @Override
    public void flush() {
        if (newFont) {
            flushFontData();
        }
        super.flush();
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
            getPdfObject().put(PdfName.BaseFont, new PdfName(MessageFormat.format("{0}-{1}", name, cmapEncoding.getCmapName())));
            getPdfObject().put(PdfName.Encoding, new PdfName(cmapEncoding.getCmapName()));
            PdfDictionary fontDescriptor = getFontDescriptor(name);
            int[][] metrics = longTag.values().toArray(new int[0][]);
            Arrays.sort(metrics, new MetricComparator());
            PdfDictionary cidFont = getCidFontType2(null, fontDescriptor, fontProgram.getFontNames().getFontName(), metrics);
            getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));
            if (getPdfObject().getIndirectReference() != null) {
                //this means, that fontDescriptor and cidFont already are indirects
                fontDescriptor.flush();
                cidFont.flush();
            }
        } else if (cidFontType == CID_FONT_TYPE_2) {
            TrueTypeFont ttf = (TrueTypeFont) getFontProgram();
            addRangeUni(ttf, longTag, true);
            int[][] metrics = longTag.values().toArray(new int[0][]);
            Arrays.sort(metrics, new MetricComparator());
            PdfStream fontStream;
            String fontName = ttf.getFontNames().getFontName();
            if (subset) {
                fontName = createSubsetPrefix() + fontName;
            }
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
                        new PdfName(MessageFormat.format("{0}-{1}", fontName, cmapEncoding.getCmapName())));
                fontDescriptor.put(PdfName.FontFile3, fontStream);
            } else {
                byte[] ttfBytes;
                if (subset || ttf.getDirectoryOffset() != 0) {
                    ttfBytes = ttf.getSubset(new LinkedHashSet<>(longTag.keySet()), true);
                } else {
                    ttfBytes = ttf.getFontStreamBytes();
                }
                fontStream = getPdfFontStream(ttfBytes, new int[]{ttfBytes.length});
                getPdfObject().put(PdfName.BaseFont, new PdfName(fontName));
                fontDescriptor.put(PdfName.FontFile2, fontStream);
            }

            // CIDSet shall be based on font.maxGlyphId property of the font, it is maxp.numGlyphs for ttf,
            // because technically we convert all unused glyphs to space, e.g. just remove outlines.
            int maxGlyphId = ttf.getFontMetrics().getMaxGlyphId();
            byte[] cidSetBytes = new byte[ttf.getFontMetrics().getMaxGlyphId() / 8 + 1];
            for (int i = 0; i < maxGlyphId / 8; i++) {
                cidSetBytes[i] |= 0xff;
            }
            for (int i = 0; i < maxGlyphId % 8; i++) {
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
            if (getPdfObject().getIndirectReference() != null) {
                //this means, that fontDescriptor, cidFont and fontStream already are indirects
                fontDescriptor.flush();
                cidFont.flush();
                fontStream.flush();
            }
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

    /**
     * Creates a ToUnicode CMap to allow copy and paste from Acrobat.
     *
     * @param metrics metrics[0] contains the glyph index and metrics[2]
     *                contains the Unicode code
     * @return the stream representing this CMap or <CODE>null</CODE>
     */
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

    //TODO optimize memory usage
    private static String toHex4(char ch) {
        String s = "0000" + Integer.toHexString(ch);
        return s.substring(s.length() - 4);
    }

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

    private static class MetricComparator implements Comparator<int[]> {
        /**
         * The method used to sort the metrics array.
         *
         * @param o1 the first element
         * @param o2 the second element
         * @return the comparison
         */
        public int compare(int[] o1, int[] o2) {
            int m1 = o1[0];
            int m2 = o2[0];
            return Integer.compare(m1, m2);
        }
    }


}
