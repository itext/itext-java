package com.itextpdf.core.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.font.CFFFontSubset;
import com.itextpdf.basics.font.CMapEncoding;
import com.itextpdf.basics.font.CidFont;
import com.itextpdf.basics.font.CidFontProperties;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.font.cmap.CMapContentParser;
import com.itextpdf.basics.font.cmap.CMapObject;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.io.PdfTokenizer;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfLiteral;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class PdfType0Font extends PdfSimpleFont<FontProgram> {

    private static final int[] Empty = {};
    private static final byte[] rotbits = {(byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10, (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01};

    private static final int First = 0;
    private static final int Bracket = 1;
    private static final int Serial = 2;
    private static final int V1y = 880;

    protected static final int CidFontType0 = 0;
    protected static final int CidFontType2 = 2;

    protected boolean vertical;
    protected CMapEncoding cmapEncoding;
    // TODO HashSet will be enough
    protected LinkedHashMap<Integer, int[]> longTag;
    protected int cidFontType;
    protected char[] specificUnicodeDifferences;

    public PdfType0Font(PdfDocument pdfDocument, PdfDictionary fontDictionary) throws IOException {
        super(pdfDocument, fontDictionary, true);
        checkFontDictionary(fontDictionary, PdfName.Type0);
        init();
    }

    public PdfType0Font(PdfDocument document, TrueTypeFont ttf, String cmap) {
        super(document,new PdfDictionary());
        if (!cmap.equals(PdfEncodings.IDENTITY_H) && !cmap.equals(PdfEncodings.IDENTITY_V)) {
            throw new PdfException("only.identity.cmaps.supports.with.truetype");
        }

        if (!ttf.getFontNames().allowEmbedding()) {
            throw new PdfException("1.cannot.be.embedded.due.to.licensing.restrictions")
                    .setMessageParams(ttf.getFontNames().getFontName() + ttf.getFontNames().getStyle());
        }
        this.fontProgram = ttf;
        this.embedded = true;
        vertical = cmap.endsWith(FontConstants.V_SYMBOL);
        cmapEncoding = new CMapEncoding(cmap);
        longTag = new LinkedHashMap<>();
        cidFontType = CidFontType2;
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

    //note Make this constructor protected. Only FontFactory (core level) will
    // be able to create Type0 font based on predefined font.
    // Or not? Possible it will be convenient construct PdfType0Font based on custom CidFont.
    // There is no typography features in CJK fonts.
    public PdfType0Font(PdfDocument document, CidFont font, String cmap) {
        super(document,new PdfDictionary());
        if (!CidFontProperties.isCidFont(font.getFontNames().getFontName(), cmap)) {
            throw new PdfException("font.1.with.2.encoding.is.not.a.cjk.font")
                    .setMessageParams(font.getFontNames().getFontName(), cmap);
        }
        this.fontProgram = font;
        vertical = cmap.endsWith("V");
        String uniMap = getUniMapName(fontProgram.getRegistry());
        cmapEncoding = new CMapEncoding(cmap, uniMap);
        longTag = new LinkedHashMap<>();
        cidFontType = CidFontType0;
    }

    @Override
    protected FontProgram initializeTypeFontForCopy(String encodingName)  {
        return null;
    }

    @Override
    protected FontProgram initializeTypeFont(String fontName, String encodingName)  {
        return null;
    }

    protected String getUniMapName(String registry) {
        String uniMap = "";
        for (String name : CidFontProperties.getRegistryNames().get(registry + "_Uni")) {
            uniMap = name;
            if (name.endsWith(FontConstants.V_SYMBOL) && vertical) {
                break;
            } else if (!name.endsWith(FontConstants.V_SYMBOL) && !vertical) {
                break;
            }
        }
        return uniMap;
    }

    @Override
    public Glyph getGlyph(int ch) {
        // TODO handle unicode value with cmap and use only glyphByCode
        Glyph glyph = getFontProgram().getGlyph(ch);
        if (glyph == null && (glyph = notdefGlyphs.get(ch)) == null) {
            // Handle special layout characters like sfthyphen (00AD).
            // This glyphs will be skipped while converting to bytes
            Glyph notdef = getFontProgram().getGlyphByCode(0);
            if (notdef != null) {
                glyph = new Glyph(notdef, ch);
            } else {
                glyph = new Glyph(-1, 0, ch);
            }
            notdefGlyphs.put(ch, glyph);
        }
        return glyph;
    }

    @Override
    public byte[] convertToBytes(String text) {
        //TODO different with type0 and type2 could be removed after simplifying longTag
        if (cidFontType == CidFontType0) {
            int len = text.length();
            if (isIdentity()) {
                for (int k = 0; k < len; ++k) {
                    longTag.put((int) text.charAt(k), Empty);
                }
            } else {
                for (int k = 0; k < len; ++k) {
                    int ch;
                    if (Utilities.isSurrogatePair(text, k)) {
                        ch = Utilities.convertToUtf32(text, k);
                        k++;
                    } else {
                        ch = text.charAt(k);
                    }
                    longTag.put(cmapEncoding.getCidCode(ch), Empty);
                }
            }
            return cmapEncoding.convertToBytes(text);
        } else if (cidFontType == CidFontType2) {
            TrueTypeFont ttf = (TrueTypeFont) fontProgram;
            int len = text.length();
            char[] glyphs = new char[len];
            int i = 0;
            if (ttf.isFontSpecific()) {
                byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
                len = b.length;
                for (int k = 0; k < len; ++k) {
                    Glyph glyph = fontProgram.getGlyph(b[k] & 0xff);
                    if (glyph != null && !longTag.containsKey(glyph.index)) {
                        longTag.put(glyph.index, new int[]{glyph.index, glyph.width,
                                glyph.unicode != null ? glyph.unicode : 0});
                        glyphs[i++] = (char) glyph.index;
                    }
                }
            } else {
                for (int k = 0; k < len; ++k) {
                    int val;
                    if (Utilities.isSurrogatePair(text, k)) {
                        val = Utilities.convertToUtf32(text, k);
                        k++;
                    } else {
                        val = text.charAt(k);
                    }
                    Glyph glyph = fontProgram.getGlyph(val);
                    if (glyph == null) {
                        glyph = fontProgram.getGlyphByCode(0);
                    }
                    if (!longTag.containsKey(glyph.index)) {
                        longTag.put(glyph.index, new int[]{glyph.index, glyph.width,
                                glyph.unicode != null ? glyph.unicode : 0});
                    }
                    glyphs[i++] = (char) glyph.index;
                }

                GlyphLine glyphLine = null;
                if (ttf.getOtfScript() != null) {
                    glyphLine = ttf.createGlyphLine(glyphs, i);
                    if (!ttf.applyOtfScript(glyphLine)) {
                       glyphLine = null;
                    }
                } else if (ttf.isApplyLigatures()) {
                    glyphLine = ttf.createGlyphLine(glyphs, i);
                    if (!ttf.applyLigaFeature(glyphLine, false)) {
                        glyphLine = null;
                    }
                }
                if (glyphLine != null) {
                    glyphs = glyphLineToChars(glyphLine);
                    i = glyphs.length;
                    for (char ch: glyphs) {
                        int code = (int)ch;
                        if (longTag.get(code) == null) {
                            Glyph glyph = ttf.getGlyphByCode(code);
                            longTag.put(code, new int[] {code, glyph.width, glyph.unicode != null ? glyph.unicode : 0});
                        }
                    }
                }
            }

            String s = new String(glyphs, 0, i);
            try {
                return s.getBytes(PdfEncodings.UnicodeBigUnmarked);
            } catch (UnsupportedEncodingException e) {
                throw new PdfException("TrueTypeFont", e);
            }
        } else {
            throw new PdfException("font.has.no.suitable.cmap");
        }
    }

    @Override
    public byte[] convertToBytes(GlyphLine glyphLine) {
        if (glyphLine != null) {
            char[] glyphs = new char[glyphLine.glyphs.size()];
            for (int i = 0; i < glyphLine.glyphs.size(); i++) {
                Glyph glyph = glyphLine.glyphs.get(i);
                glyphs[i] = (char)glyph.index;
                int code = glyph.index;
                if (longTag.get(code) == null) {
                    Integer uniChar = glyph.unicode;
                    longTag.put(code, new int[]{code, glyph.width, uniChar != null ? uniChar : 0});
                }
            }

            String s = new String(glyphs, 0, glyphs.length);
            try {
                return s.getBytes(PdfEncodings.UnicodeBigUnmarked);
            } catch (UnsupportedEncodingException e) {
                throw new PdfException("TrueTypeFont", e);
            }
        } else {
            return null;
        }
    }

    @Override
    public byte[] convertToBytes(Glyph glyph) {
        int code = glyph.index;
        if (longTag.get(code) == null) {
            longTag.put(code, new int[] {code, glyph.width, glyph.unicode !=null ? glyph.unicode : 0});
        }
        String s = new String(new char[] {(char)glyph.index}, 0, 1);
        try {
            return s.getBytes(PdfEncodings.UnicodeBigUnmarked);
        } catch (UnsupportedEncodingException e) {
            throw new PdfException("TrueTypeFont", e);
        }
    }

    @Override
    public GlyphLine createGlyphLine(String content) {
        ArrayList<Glyph> glyphs = new ArrayList<>();
        //TODO different with type0 and type2 could be removed after simplifying longTag
        if (cidFontType == CidFontType0) {
            int len = content.length();
            if (isIdentity()) {
                for (int k = 0; k < len; ++k) {
                    Glyph glyph = fontProgram.getGlyphByCode((int) content.charAt(k));
                    if (glyph != null) {
                        glyphs.add(glyph);
                    }
                }
            } else {
                for (int k = 0; k < len; ++k) {
                    int ch;
                    if (Utilities.isSurrogatePair(content, k)) {
                        ch = Utilities.convertToUtf32(content, k);
                        k++;
                    } else {
                        ch = content.charAt(k);
                    }
                    glyphs.add(getGlyph(ch));
                }
            }
        } else if (cidFontType == CidFontType2) {
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
                    if (Utilities.isSurrogatePair(content, k)) {
                        val = Utilities.convertToUtf32(content, k);
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
    public int getWidth(int unicode) {
        int width = fontProgram.getWidth(cmapEncoding.getCidCode(unicode));
        return width > 0 ? width : FontProgram.DEFAULT_WIDTH;
    }

    @Override
    public int getWidth(String text) {
        int total = 0;
        for (int k = 0; k < text.length(); ++k) {
            int ch;
            if (Utilities.isSurrogatePair(text, k)) {
                ch = Utilities.convertToUtf32(text, k);
                k++;
            } else {
                ch = text.charAt(k);
            }
            total += fontProgram.getWidth(cmapEncoding.getCidCode(ch));
        }
        return total;
    }

    @Override
    protected PdfStream getFontStream() {
        throw new RuntimeException();
    }

    @Override
    protected PdfDictionary getFontDescriptor(PdfStream fontStream, String fontName) {
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(getDocument());
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
        Rectangle fontBBox = new Rectangle(getFontProgram().getFontMetrics().getBbox().clone());
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontBBox));
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(getFontProgram().getFontMetrics().getTypoAscender()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(getFontProgram().getFontMetrics().getTypoDescender()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(getFontProgram().getFontMetrics().getCapHeight()));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(getFontProgram().getFontMetrics().getItalicAngle()));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(getFontProgram().getFontMetrics().getStemV()));
        fontDescriptor.put(PdfName.Flags, new PdfNumber(getFontProgram().getPdfFontFlags()));
        if (fontStream != null && cidFontType == CidFontType2) {
            if (((TrueTypeFont)getFontProgram()).isCff()) {
                fontDescriptor.put(PdfName.FontFile3, fontStream);
            } else {
                fontDescriptor.put(PdfName.FontFile2, fontStream);
            }
        }
        if (fontProgram.getFontIdentification().getPanose() != null) {
            PdfDictionary styleDictionary = new PdfDictionary();
            styleDictionary.put(PdfName.Panose, new PdfString(fontProgram.getFontIdentification().getPanose()));
            fontDescriptor.put(PdfName.Style, styleDictionary);
        }

        return fontDescriptor;
    }

    /**
     * Gets the descent of a {@code String} in normalized 1000 units. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
     * @param text the {@code String} to get the descent of
     * @return the descent in normalized 1000 units
     */
    public int getDescent(String text) {
        int min = 0;
        for (int k = 0; k < text.length(); ++k) {
            int ch;
            if (Utilities.isSurrogatePair(text, k)) {
                ch = Utilities.convertToUtf32(text, k);
                k++;
            } else {
                ch = text.charAt(k);
            }
            int[] bbox = getGlyph(ch).bbox;
            if (bbox != null && bbox[1] < min) {
                min = bbox[1];
            } else if (bbox == null && fontProgram.getFontMetrics().getTypoDescender() < min) {
                min = fontProgram.getFontMetrics().getTypoDescender();
            }
        }
        return min;
    }

    /**
     * Gets the descent of a char code in normalized 1000 units. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
     * @param ch the char code to get the descent of
     * @return the descent in normalized 1000 units
     */
    public int getDescent(int ch) {
        int min = 0;
        int[] bbox = getGlyph(ch).bbox;
        if (bbox != null && bbox[1] < min) {
            min = bbox[1];
        } else if (bbox ==  null && fontProgram.getFontMetrics().getTypoDescender() < min) {
            min = fontProgram.getFontMetrics().getTypoDescender();
        }

        return min;
    }

    /**
     * Gets the ascent of a {@code String} in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param text the {@code String} to get the ascent of
     * @return the ascent in normalized 1000 units
     */
    public int getAscent(String text) {
        int max = 0;
        for (int k = 0; k < text.length(); ++k) {
            int ch;
            if (Utilities.isSurrogatePair(text, k)) {
                ch = Utilities.convertToUtf32(text, k);
                k++;
            } else {
                ch = text.charAt(k);
            }
            int[] bbox = getGlyph(ch).bbox;
            if (bbox != null && bbox[3] > max) {
                max = bbox[3];
            } else if (bbox == null && fontProgram.getFontMetrics().getTypoAscender() > max) {
                max = fontProgram.getFontMetrics().getTypoAscender();
            }
        }

        return max;
    }

    /**
     * Gets the ascent of a char code in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param ch the char code to get the ascent of
     * @return the ascent in normalized 1000 units
     */
    public int getAscent(int ch) {
        int max = 0;
        int[] bbox = getGlyph(ch).bbox;
        if (bbox != null && bbox[3] > max) {
            max = bbox[3];
        } else if (bbox == null && fontProgram.getFontMetrics().getTypoAscender() > max) {
            max = fontProgram.getFontMetrics().getTypoAscender();
        }

        return max;
    }

    public boolean isIdentity() {
        //TODO strange property
        return cmapEncoding.isDirect();
    }

    @Override   
    public void flush() {
        if (isCopy) {
            flushCopyFontData();
        } else {
            flushFontData();
        }
    }

    private void flushCopyFontData() {
        getPdfObject().flush();
    }

    private void flushFontData() {
        if (cidFontType == CidFontType0) {
            getPdfObject().put(PdfName.Type, PdfName.Font);
            getPdfObject().put(PdfName.Subtype, PdfName.Type0);
            String name = fontProgram.getFontNames().getFontName();
            String style = fontProgram.getFontNames().getStyle();
            if (style.length() > 0) {
                name += "-" + style;
            }
            getPdfObject().put(PdfName.BaseFont, new PdfName(String.format("%s-%s", name, cmapEncoding.getCmapName())));
            getPdfObject().put(PdfName.Encoding, new PdfName(cmapEncoding.getCmapName()));
            PdfDictionary fontDescriptor = getFontDescriptor(null, name);
            PdfDictionary cidFont = getCidFontType0(fontDescriptor);
            getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));
            fontDescriptor.flush();
            cidFont.flush();
        } else if (cidFontType == CidFontType2) {
            TrueTypeFont ttf = (TrueTypeFont) getFontProgram();
            addRangeUni(ttf, longTag, true);
            int[][] metrics = longTag.values().toArray(new int[0][]);
            Arrays.sort(metrics, new MetricComparator());
            PdfStream fontStream;
            String fontName = ttf.getFontNames().getFontName();
            if (subset) {
                fontName = createSubsetPrefix() + fontName;
            }
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
                        new PdfName(String.format("%s-%s", fontName, cmapEncoding.getCmapName())));
            } else {
                byte[] ttfBytes;
                if (subset || ttf.getDirectoryOffset() != 0) {
                    ttfBytes = ttf.getSubset(new LinkedHashSet<>(longTag.keySet()), true);
                } else {
                    ttfBytes = ttf.getFontStreamBytes();
                }
                fontStream = getPdfFontStream(ttfBytes, new int[]{ttfBytes.length});
                getPdfObject().put(PdfName.BaseFont, new PdfName(fontName));
            }

            PdfDictionary fontDescriptor = getFontDescriptor(fontStream, fontName);

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
                toUnicode.flush();
            }
            fontDescriptor.flush();
            cidFont.flush();
        } else {
            throw new IllegalStateException("Unsupported CID Font");
        }
    }


    /** Generates the CIDFontTyte2 dictionary.
     * @param fontDescriptor the indirect reference to the font descriptor
     * @param fontName   a name of the font
     * @param metrics        the horizontal width metrics
     * @return a stream
     */
    public PdfDictionary getCidFontType2(TrueTypeFont ttf, PdfDictionary fontDescriptor, String fontName, int[][] metrics) {
        PdfDictionary cidFont = new PdfDictionary();
        cidFont.makeIndirect(getDocument());
        cidFont.put(PdfName.Type, PdfName.Font);
        // sivan; cff
        cidFont.put(PdfName.FontDescriptor, fontDescriptor);
        if (ttf.isCff()) {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        } else {
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType2);
            cidFont.put(PdfName.CIDToGIDMap, PdfName.Identity);
        }
        cidFont.put(PdfName.BaseFont, new PdfName(fontName));
        PdfDictionary cidInfo = new PdfDictionary();
        cidInfo.put(PdfName.Registry, new PdfString("Adobe"));
        cidInfo.put(PdfName.Ordering, new PdfString("Identity"));
        cidInfo.put(PdfName.Supplement, new PdfNumber(0));
        cidFont.put(PdfName.CIDSystemInfo, cidInfo);
        if (!vertical) {
            cidFont.put(PdfName.DW, new PdfNumber(FontProgram.DEFAULT_WIDTH));
            StringBuilder buf = new StringBuilder("[");
            int lastNumber = -10;
            boolean firstTime = true;
            for (int[] metric : metrics) {
                if (metric[1] == FontProgram.DEFAULT_WIDTH) {
                    continue;
                }
                if (metric[0] == lastNumber + 1) {
                    buf.append(' ').append(metric[1]);
                } else {
                    if (!firstTime) {
                        buf.append(']');
                    }
                    firstTime = false;
                    buf.append(metric[0]).append('[').append(metric[1]);
                }
                lastNumber = metric[0];
            }
            if (buf.length() > 1) {
                buf.append("]]");
                cidFont.put(PdfName.W, new PdfLiteral(buf.toString()));
            }
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
    public PdfStream getToUnicode(Object metrics[]) {
        if (metrics.length == 0)
            return null;
        StringBuilder buf = new StringBuilder(
                "/CIDInit /ProcSet findresource begin\n" +
                        "12 dict begin\n" +
                        "begincmap\n" +
                        "/CIDSystemInfo\n" +
                        "<< /Registry (TTX+0)\n" +
                        "/Ordering (T42UV)\n" +
                        "/Supplement 0\n" +
                        ">> def\n" +
                        "/CMapName /TTX+0 def\n" +
                        "/CMapType 2 def\n" +
                        "1 begincodespacerange\n" +
                        "<0000><FFFF>\n" +
                        "endcodespacerange\n");
        int size = 0;
        for (int k = 0; k < metrics.length; ++k) {
            if (size == 0) {
                if (k != 0) {
                    buf.append("endbfrange\n");
                }
                size = Math.min(100, metrics.length - k);
                buf.append(size).append(" beginbfrange\n");
            }
            --size;
            int[] metric = (int[]) metrics[k];
            String fromTo = CMapContentParser.toHex(metric[0]);
            buf.append(fromTo).append(fromTo).append(CMapContentParser.toHex(metric[2])).append('\n');
        }
        buf.append("endbfrange\n" +
                "endcmap\n" +
                "CMapName currentdict /CMap defineresource pop\n" +
                "end end\n");
        return new PdfStream(PdfEncodings.convertToBytes(buf.toString(), null)).makeIndirect(getDocument());
    }

    protected static String convertToHCIDMetrics(int keys[], IntHashtable h) {
        if (keys.length == 0)
            return null;
        int lastCid = 0;
        int lastValue = 0;
        int start;
        for (start = 0; start < keys.length; ++start) {
            lastCid = keys[start];
            lastValue = h.get(lastCid);
            if (lastValue != 0) {
                ++start;
                break;
            }
        }
        if (lastValue == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(lastCid);
        int state = First;
        for (int k = start; k < keys.length; ++k) {
            int cid = keys[k];
            int value = h.get(cid);
            if (value == 0) {
                continue;
            }
            switch (state) {
                case First: {
                    if (cid == lastCid + 1 && value == lastValue) {
                        state = Serial;
                    } else if (cid == lastCid + 1) {
                        state = Bracket;
                        buf.append('[').append(lastValue);
                    } else {
                        buf.append('[').append(lastValue).append(']').append(cid);
                    }
                    break;
                }
                case Bracket: {
                    if (cid == lastCid + 1 && value == lastValue) {
                        state = Serial;
                        buf.append(']').append(lastCid);
                    } else if (cid == lastCid + 1) {
                        buf.append(' ').append(lastValue);
                    } else {
                        state = First;
                        buf.append(' ').append(lastValue).append(']').append(cid);
                    }
                    break;
                }
                case Serial: {
                    if (cid != lastCid + 1 || value != lastValue) {
                        buf.append(' ').append(lastCid).append(' ').append(lastValue).append(' ').append(cid);
                        state = First;
                    }
                    break;
                }
            }
            lastValue = value;
            lastCid = cid;
        }
        switch (state) {
            case First: {
                buf.append('[').append(lastValue).append("]]");
                break;
            }
            case Bracket: {
                buf.append(' ').append(lastValue).append("]]");
                break;
            }
            case Serial: {
                buf.append(' ').append(lastCid).append(' ').append(lastValue).append(']');
                break;
            }
        }
        return buf.toString();
    }

    protected static String convertToVCIDMetrics(int keys[], IntHashtable v, IntHashtable h) {
        if (keys.length == 0) {
            return null;
        }
        int lastCid = 0;
        int lastValue = 0;
        int lastHValue = 0;
        int start;
        for (start = 0; start < keys.length; ++start) {
            lastCid = keys[start];
            lastValue = v.get(lastCid);
            if (lastValue != 0) {
                ++start;
                break;
            } else {
                lastHValue = h.get(lastCid);
            }
        }
        if (lastValue == 0) {
            return null;
        }
        if (lastHValue == 0) {
            lastHValue = FontProgram.DEFAULT_WIDTH;
        }
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(lastCid);
        int state = First;
        for (int k = start; k < keys.length; ++k) {
            int cid = keys[k];
            int value = v.get(cid);
            if (value == 0) {
                continue;
            }
            int hValue = h.get(lastCid);
            if (hValue == 0) {
                hValue = FontProgram.DEFAULT_WIDTH;
            }
            switch (state) {
                case First: {
                    if (cid == lastCid + 1 && value == lastValue && hValue == lastHValue) {
                        state = Serial;
                    } else {
                        buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(V1y).append(' ').append(cid);
                    }
                    break;
                }
                case Serial: {
                    if (cid != lastCid + 1 || value != lastValue || hValue != lastHValue) {
                        buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(V1y).append(' ').append(cid);
                        state = First;
                    }
                    break;
                }
            }
            lastValue = value;
            lastCid = cid;
            lastHValue = hValue;
        }
        buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(V1y).append(" ]");
        return buf.toString();
    }

    protected void addRangeUni(TrueTypeFont ttf, HashMap<Integer, int[]> longTag, boolean includeMetrics) {
        if (!subset && (subsetRanges != null || ttf.getDirectoryOffset() > 0)) {
            int[] rg = subsetRanges == null && ttf.getDirectoryOffset() > 0
                    ? new int[]{0, 0xffff} : compactRanges(subsetRanges);
            HashMap<Integer, int[]> usemap = ttf.getActiveCmap();
            assert usemap != null;
            for (Map.Entry<Integer, int[]> e : usemap.entrySet()) {
                int[] v = e.getValue();
                Integer gi = v[0];
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

    protected void init() {
        PdfName baseFont = fontDictionary.getAsName(PdfName.BaseFont);
        getPdfObject().put(PdfName.Subtype, fontDictionary.getAsName(PdfName.Subtype));
        getPdfObject().put(PdfName.BaseFont, baseFont);
        PdfName encoding = fontDictionary.getAsName(PdfName.Encoding);
        getPdfObject().put(PdfName.Encoding, encoding);

        initFontProgramData();

        PdfDictionary toCidFont = new PdfDictionary();
        PdfArray fromCidFontArray = fontDictionary.getAsArray(PdfName.DescendantFonts);
        PdfDictionary fromCidFont = fromCidFontArray.getAsDictionary(0);
        if (fromCidFont != null) {
            toCidFont.makeIndirect(getDocument());
            PdfName subType = fromCidFont.getAsName(PdfName.Subtype);
            PdfName cidBaseFont = fromCidFont.getAsName(PdfName.BaseFont);
            PdfObject cIDToGIDMap = fromCidFont.get(PdfName.CIDToGIDMap);
            PdfArray w = fromCidFont.getAsArray(PdfName.W);
            PdfArray w2 = fromCidFont.getAsArray(PdfName.W2);
            Integer dw = fromCidFont.getAsInt(PdfName.DW);

            toCidFont.put(PdfName.Type, PdfName.Font);
            toCidFont.put(PdfName.Subtype, subType);
            toCidFont.put(PdfName.BaseFont, cidBaseFont);
            fontProgram.getFontNames().setFontName(cidBaseFont.getValue());
            PdfDictionary fromDescriptorDictionary = fromCidFont.getAsDictionary(PdfName.FontDescriptor);
            if (fromDescriptorDictionary != null) {
                PdfDictionary toDescriptorDictionary = getNewFontDescriptor(fromDescriptorDictionary);
                toCidFont.put(PdfName.FontDescriptor, toDescriptorDictionary);
                toDescriptorDictionary.flush();
            }

            if (w != null) {
                toCidFont.put(PdfName.W, w);
                if (fontProgram instanceof CidFont) {
                    ((CidFont) fontProgram).setHMetrics(readWidths(w));
                }
            }

            if (w2 != null) {
                toCidFont.put(PdfName.W2, w2);
                if (fontProgram instanceof CidFont) {
                    ((CidFont) fontProgram).setVMetrics(readWidths(w2));
                }
            }

            if (dw != null) {
                toCidFont.put(PdfName.DW, new PdfNumber(dw));
            }

            if (cIDToGIDMap != null) {
                toCidFont.put(PdfName.CIDToGIDMap, cIDToGIDMap);
            }

            PdfDictionary toCidInfo = new PdfDictionary();
            PdfDictionary fromCidInfo = fromCidFont.getAsDictionary(PdfName.CIDSystemInfo);
            if (fromCidInfo != null) {
                PdfString registry = fromCidInfo.getAsString(PdfName.Registry);
                PdfString ordering = fromCidInfo.getAsString(PdfName.Ordering);
                Integer supplement = fromCidInfo.getAsInt(PdfName.Supplement);

                toCidInfo.put(PdfName.Registry, registry);
                fontProgram.setRegistry(registry.getValue());
                toCidInfo.put(PdfName.Ordering, ordering);
                toCidInfo.put(PdfName.Supplement, new PdfNumber(supplement));
            }
            toCidFont.put(PdfName.CIDSystemInfo, fromCidInfo);

            PdfObject toUnicode = fontDictionary.get(PdfName.ToUnicode);
            if (toUnicode != null) {
                int dwVal = FontProgram.DEFAULT_WIDTH;
                if (dw != null) {
                    dwVal = dw;
                }
                IntHashtable widths = readWidths(w);
                if (toUnicode instanceof PdfStream) {
                    PdfStream newStream = (PdfStream) toUnicode.clone();
                    getPdfObject().put(PdfName.ToUnicode, newStream);
                    newStream.flush();
                    fillMetrics(((PdfStream) toUnicode).getBytes(), widths, dwVal);
                } else if (toUnicode instanceof PdfString) {
                    fillMetricsIdentity(widths, dwVal);
                }
            }
        }

        getPdfObject().put(PdfName.DescendantFonts, new PdfArray(toCidFont));
        toCidFont.flush();
    }

    private IntHashtable readWidths(PdfArray ws) {
        IntHashtable hh = new IntHashtable();
        if (ws == null)
            return hh;
        for (int k = 0; k < ws.size(); ++k) {
            int c1 = ws.getAsInt(k);
            PdfObject obj = ws.get(++k);
            if (obj.isArray()) {
                PdfArray a2 = (PdfArray) obj;
                for (int j = 0; j < a2.size(); ++j) {
                    int c2 = a2.getAsInt(j);
                    hh.put(c1++, c2);
                }
            } else {
                int c2 = ((PdfNumber) obj).getIntValue();
                int w = ws.getAsInt(++k);
                for (; c1 <= c2; ++c1)
                    hh.put(c1, w);
            }
        }
        return hh;
    }

    private void initFontProgramData()  {
        longTag = new LinkedHashMap<>();
        String encoding = fontDictionary.getAsName(PdfName.Encoding).getValue();
        String fontName = fontDictionary.getAsArray(PdfName.DescendantFonts).getAsDictionary(0).getAsName(PdfName.BaseFont).getValue();
        if (CidFontProperties.isCidFont(fontName, encoding)) {
            fontProgram = new CidFont(fontName, null);
            vertical = encoding.endsWith(FontConstants.V_SYMBOL);
            String uniMap = getUniMapName(fontProgram.getRegistry());
            cmapEncoding = new CMapEncoding(encoding, uniMap);
            cidFontType = CidFontType0;
        } else {
            cmapEncoding = new CMapEncoding(encoding);
            cidFontType = CidFontType2;
            //TODO Document font refactoring
            //fontProgram = new TrueTypeFont(encoding);
            throw new RuntimeException();
        }
    }

    private void fillMetrics(byte[] touni, IntHashtable widths, int dw) {
        try {
            CMapContentParser ps = new CMapContentParser(new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(touni))));
            CMapObject ob;
            boolean notFound = true;
            int nestLevel = 0;
            int maxExc = 50;
            while ((notFound || nestLevel > 0)) {
                try {
                    ob = ps.readObject();
                } catch (Exception ex) {
                    if (--maxExc < 0) break;
                    continue;
                }
                if (ob == null) break;
                if (ob.getType() == 5) {
                    if (ob.toString().equals("begin")) {
                        notFound = false;
                        nestLevel++;
                    } else if (ob.toString().equals("end")) {
                        nestLevel--;
                    } else if (ob.toString().equals("beginbfchar")) {
                        while (true) {
                            CMapObject nx = ps.readObject();
                            if (nx.toString().equals("endbfchar")) break;
                            String cid = CMapContentParser.decodeCMapObject(nx);
                            String uni = CMapContentParser.decodeCMapObject(ps.readObject());
                            if (uni.length() == 1) {
                                int cidc = cid.charAt(0);
                                int unic = uni.charAt(uni.length() - 1);
                                int w = dw;
                                if (widths.containsKey(cidc)) {
                                    w = widths.get(cidc);
                                }
                                longTag.put(unic, new int[]{cidc, w});
                            }
                        }
                    } else if (ob.toString().equals("beginbfrange")) {
                        while (true) {
                            CMapObject nx = ps.readObject();
                            if (nx.toString().equals("endbfrange")) break;
                            String cid1 = CMapContentParser.decodeCMapObject(nx);
                            String cid2 = CMapContentParser.decodeCMapObject(ps.readObject());
                            int cid1c = cid1.charAt(0);
                            int cid2c = cid2.charAt(0);
                            CMapObject ob2 = ps.readObject();
                            if (ob2.isString()) {
                                String uni = CMapContentParser.decodeCMapObject(ob2);
                                if (uni.length() == 1) {
                                    int unic = uni.charAt(uni.length() - 1);
                                    for (; cid1c <= cid2c; cid1c++, unic++) {
                                        int w = dw;
                                        if (widths.containsKey(cid1c)) {
                                            w = widths.get(cid1c);
                                        }
                                        longTag.put(unic, new int[]{cid1c, w});
                                    }
                                }
                            } else if (ob2.isArray()) {
                                ArrayList<CMapObject> a = (ArrayList<CMapObject>) ob2.getValue();
                                for (int j = 0; j < a.size(); ++j, ++cid1c) {
                                    String uni = CMapContentParser.decodeCMapObject(a.get(j));
                                    if (uni.length() == 1) {
                                        int unic = uni.charAt(uni.length() - 1);
                                        int w = dw;
                                        if (widths.containsKey(cid1c)) {
                                            w = widths.get(cid1c);
                                        }
                                        longTag.put(unic, new int[]{cid1c, w});
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillMetricsIdentity(IntHashtable widths, int dw) {
        for (int i = 0; i < 65536; i++) {
            int w = dw;
            if (widths.containsKey(i))
                w = widths.get(i);
            longTag.put(i, new int[]{i, w});
        }
    }

    private PdfDictionary getCidFontType0(PdfDictionary fontDescriptor)  {
        PdfDictionary cidFont = new PdfDictionary();
        cidFont.makeIndirect(getDocument());
        cidFont.put(PdfName.Type, PdfName.Font);
        cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        cidFont.put(PdfName.BaseFont, new PdfName(fontProgram.getFontNames().getFontName() + fontProgram.getFontNames().getStyle()));
        cidFont.put(PdfName.FontDescriptor, fontDescriptor);
        int[] keys = Utilities.toArray(longTag.keySet());
        Arrays.sort(keys);
        String w = convertToHCIDMetrics(keys, ((CidFont) fontProgram).getHMetrics());
        if (w != null) {
            cidFont.put(PdfName.W, new PdfLiteral(w));
        }
        if (vertical) {
            w = convertToVCIDMetrics(keys, ((CidFont) fontProgram).getVMetrics(), ((CidFont) fontProgram).getHMetrics());
            if (w != null) {
                cidFont.put(PdfName.W2, new PdfLiteral(w));
            }
        } else {
            cidFont.put(PdfName.DW, new PdfNumber(FontProgram.DEFAULT_WIDTH));
        }
        PdfDictionary cidInfo = new PdfDictionary();
        cidInfo.put(PdfName.Registry, new PdfString(cmapEncoding.getRegistry()));
        cidInfo.put(PdfName.Ordering, new PdfString(cmapEncoding.getOrdering()));
        cidInfo.put(PdfName.Supplement, new PdfNumber(cmapEncoding.getSupplement()));
        cidFont.put(PdfName.CIDSystemInfo, cidInfo);
        return cidFont;
    }

    private char[] glyphLineToChars(GlyphLine glyphLine) {
        int length = glyphLine.end - glyphLine.start;
        char[] glyphs = new char[length];
        for (int k = 0; k < length; k++) {
            glyphs[k] = (char) glyphLine.glyphs.get(glyphLine.start + k).index;
        }
        return glyphs;
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
            if (m1 < m2)
                return -1;
            if (m1 == m2)
                return 0;
            return 1;
        }
    }
}
