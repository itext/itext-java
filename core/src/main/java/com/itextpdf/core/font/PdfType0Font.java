package com.itextpdf.core.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.font.*;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PdfType0Font extends PdfFont {

    private static class MetricComparator implements Comparator<int[]> {
        /**
         * The method used to sort the metrics array.
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

    private static final int[] Empty = {};

    private static final int First = 0;
    private static final int Bracket = 1;
    private static final int Serial = 2;
    private static final int V1y = 880;
    protected static final int CidFontType0 = 0;
    protected static final int CidFontType2 = 2;

    private boolean vertical;
    protected FontProgram font;
    protected CMapEncoding cmapEncoding;
    protected HashMap<Integer, int[]> longTag;
    protected char[] specificUnicodeDifferences;
    protected int cidFontType;


    public PdfType0Font(PdfDocument document, TrueTypeFont ttf, String cmap) throws PdfException {
        super(document);
        if (!cmap.equals(PdfEncodings.IDENTITY_H) && !cmap.equals(PdfEncodings.IDENTITY_V)) {
            throw new PdfRuntimeException("only.identity.cmaps.supports.with.truetype");
        }
        if (!ttf.allowEmbedding()) {
            throw new PdfException("1.cannot.be.embedded.due.to.licensing.restrictions").setMessageParams(ttf.getFontName() + ttf.getStyle());
        }
        this.font = ttf;
        this.embedded = true;
        vertical = cmap.endsWith("V");
        cmapEncoding = new CMapEncoding(cmap);
        longTag = new HashMap<>();
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
    public PdfType0Font(PdfDocument document, CidFont font, String cmap) throws PdfException {
        super(document);
        if (!CidFontProperties.isCidFont(font.getFontName(), cmap)) {
            throw new PdfException("font.1.with.2.encoding.is.not.a.cjk.font").setMessageParams(font.getFontName(), cmap);
        }
        this.font = font;
        vertical = cmap.endsWith("V");
        String uniMap = "";
        for (String name : CidFontProperties.getRegistryNames().get(font.getRegistry() + "_Uni")) {
            uniMap = name;
            if (name.endsWith("V") && vertical) {
                break;
            } else if (!name.endsWith("V") && !vertical) {
                break;
            }
        }
        cmapEncoding = new CMapEncoding(cmap, uniMap);
        longTag = new HashMap<>();
        cidFontType = CidFontType0;
    }

    @Override
    public FontProgram getFontProgram() {
        return font;
    }

    @Override
    public byte[] convertToBytes(String text) {
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
            TrueTypeFont ttf = (TrueTypeFont) font;
            int len = text.length();
            char glyph[] = new char[len];
            int i = 0;
            if (ttf.isFontSpecific()) {
                byte[] b = PdfEncodings.convertToBytes(text, "symboltt");
                len = b.length;
                for (int k = 0; k < len; ++k) {
                    int[] metrics = ttf.getMetrics(b[k] & 0xff);
                    if (metrics == null) {
                        continue;
                    } else if (!longTag.containsKey(metrics[0])) {
                        longTag.put(metrics[0], new int[]{metrics[0], metrics[1], specificUnicodeDifferences[b[k] & 0xff]});
                    }
                    glyph[i++] = (char) metrics[0];
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
                    int[] metrics = ttf.getMetrics(val);
                    if (metrics == null) {
                        continue;
                    } else if (!longTag.containsKey(metrics[0])) {
                        longTag.put(metrics[0], new int[]{metrics[0], metrics[1], val});
                    }
                    glyph[i++] = (char) metrics[0];
                }
            }
            String s = new String(glyph, 0, i);
            try {
                return s.getBytes(PdfEncodings.UnicodeBigUnmarked);
            } catch (UnsupportedEncodingException e) {
                throw new PdfRuntimeException("TrueTypeFont", e);
            }
        } else {
            throw new PdfRuntimeException("font.has.no.suitable.cmap");
        }

    }

    public boolean isIdentity() {
        return cmapEncoding.isDirect();
    }

    @Override
    public void flush() throws PdfException {
        if (cidFontType == CidFontType0) {
            getPdfObject().put(PdfName.Type, PdfName.Font);
            getPdfObject().put(PdfName.Subtype, PdfName.Type0);
            String name = font.getFontName();
            if (font.getStyle().length() > 0) {
                name += "-" + font.getStyle().substring(1);
            }
            name += "-" + cmapEncoding.getCmapName();
            getPdfObject().put(PdfName.BaseFont, new PdfName(name));
            getPdfObject().put(PdfName.Encoding, new PdfName(cmapEncoding.getCmapName()));
            PdfDictionary fontDescriptor = getFontDescriptor();
            PdfDictionary cidFont = getCidFontType0(fontDescriptor);
            getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));
            fontDescriptor.flush();
            cidFont.flush();
        } else if (cidFontType == CidFontType2) {
            TrueTypeFont ttf = (TrueTypeFont)getFontProgram();
            addRangeUni(ttf, longTag, true);
            int[][] metrics = longTag.values().toArray(new int[0][]);
            Arrays.sort(metrics, new MetricComparator());
            PdfStream fontStream;
            // sivan: cff
            if (ttf.isCff()) {
                byte[] cffBytes = ttf.getFontStreamBytes();
                if (subset || subsetRanges != null) {
                    CFFFontSubset cff = new CFFFontSubset(ttf.getFontStreamBytes(), longTag);
                    cffBytes = cff.Process(cff.getNames()[0]);
                }
                fontStream = getFontStream(cffBytes, new int[] {cffBytes.length});
                fontStream.put(PdfName.Subtype, new PdfName("CIDFontType0C"));
            } else {
                byte[] ttfBytes;
                if (subset || ttf.getDirectoryOffset() != 0) {
                    ttfBytes = ttf.getSubset(new HashSet<Integer>(longTag.keySet()), true);
                } else {
                    ttfBytes = ttf.getFontStreamBytes();
                }
                fontStream = getFontStream(ttfBytes, new int[] {ttfBytes.length});
            }
            String subsetPrefix = "";
            if (subset) {
                subsetPrefix = createSubsetPrefix();
            }
            PdfDictionary fontDescriptor = PdfTrueTypeFont.getFontDescriptor(getDocument(), ttf, fontStream, subsetPrefix);
            PdfDictionary cidFont = getCidFontType2(ttf, fontDescriptor, subsetPrefix, metrics);

            getPdfObject().put(PdfName.Type, PdfName.Font);
            getPdfObject().put(PdfName.Subtype, PdfName.Type0);
            // The PDF Reference manual advises to add -encoding to CID font names
            if (ttf.isCff()) {
                String fontName = String.format("%s%s-%s", subsetPrefix, ttf.getFontName(), cmapEncoding.getCmapName());
                getPdfObject().put(PdfName.BaseFont, new PdfName(fontName));
            } else {
                getPdfObject().put(PdfName.BaseFont, new PdfName(subsetPrefix + ttf.getFontName()));
            }
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

    private PdfDictionary getFontDescriptor() throws PdfException {
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(getDocument());
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
        fontDescriptor.put(PdfName.FontName, new PdfName(font.getFontName() + font.getStyle()));
        Rectangle fontBBox = new Rectangle(font.getLlx(), font.getLly(), font.getUrx(), font.getUry());
        fontDescriptor.put(PdfName.FontBBox, new PdfArray(fontBBox));
        fontDescriptor.put(PdfName.Ascent, new PdfNumber(font.getAscent()));
        fontDescriptor.put(PdfName.Descent, new PdfNumber(font.getDescent()));
        fontDescriptor.put(PdfName.CapHeight, new PdfNumber(font.getCapHeight()));
        fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(font.getItalicAngle()));
        fontDescriptor.put(PdfName.Flags, new PdfNumber(font.getFlags()));
        fontDescriptor.put(PdfName.StemV, new PdfNumber(font.getStemV()));
        PdfDictionary styleDictionary = new PdfDictionary();
        styleDictionary.put(PdfName.Panose, new PdfString(font.getPanose()));
        fontDescriptor.put(PdfName.Style, styleDictionary);
        return fontDescriptor;
    }

    private PdfDictionary getCidFontType0(PdfDictionary fontDescriptor) throws PdfException {
        PdfDictionary cidFont = new PdfDictionary();
        cidFont.makeIndirect(getDocument());
        cidFont.put(PdfName.Type, PdfName.Font);
        cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        cidFont.put(PdfName.BaseFont, new PdfName(font.getFontName() + font.getStyle()));
        cidFont.put(PdfName.FontDescriptor, fontDescriptor);
        int[] keys = Utilities.toArray(longTag.keySet());
        Arrays.sort(keys);
        String w = convertToHCIDMetrics(keys, ((CidFont) font).getHMetrics());
        if (w != null) {
            cidFont.put(PdfName.W, new PdfLiteral(w));
        }
        if (vertical) {
            w = convertToVCIDMetrics(keys, ((CidFont) font).getVMetrics(), ((CidFont) font).getHMetrics());
            if (w != null) {
                cidFont.put(PdfName.W2, new PdfLiteral(w));
            }
        } else {
            cidFont.put(PdfName.DW, new PdfNumber(1000));
        }
        PdfDictionary cidInfo = new PdfDictionary();
        cidInfo.put(PdfName.Registry, new PdfString(cmapEncoding.getRegistry()));
        cidInfo.put(PdfName.Ordering, new PdfString(cmapEncoding.getOrdering()));
        cidInfo.put(PdfName.Supplement, new PdfNumber(cmapEncoding.getSupplement()));
        cidFont.put(PdfName.CIDSystemInfo, cidInfo);
        return cidFont;
    }

    /** Generates the CIDFontTyte2 dictionary.
     * @param fontDescriptor the indirect reference to the font descriptor
     * @param subsetPrefix the subset prefix
     * @param metrics the horizontal width metrics
     * @return a stream
     */
    public PdfDictionary getCidFontType2(TrueTypeFont ttf, PdfDictionary fontDescriptor, String subsetPrefix, int[][] metrics) throws PdfException {
        PdfDictionary cidFont = new PdfDictionary();
        cidFont.makeIndirect(getDocument());
        cidFont.put(PdfName.Type, PdfName.Font);
        // sivan; cff
        cidFont.put(PdfName.FontDescriptor, fontDescriptor);
        if (ttf.isCff()) {
            String fontName = String.format("%s%s-%s", subsetPrefix, ttf.getFontName(), cmapEncoding.getCmapName());
            cidFont.put(PdfName.BaseFont, new PdfName(fontName));
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        }
        else {
            cidFont.put(PdfName.BaseFont, new PdfName(subsetPrefix + ttf.getFontName()));
            cidFont.put(PdfName.Subtype, PdfName.CIDFontType2);
            cidFont.put(PdfName.CIDToGIDMap, PdfName.Identity);
        }
        PdfDictionary cidInfo = new PdfDictionary();
        cidInfo.put(PdfName.Registry, new PdfString("Adobe"));
        cidInfo.put(PdfName.Ordering, new PdfString("Identity"));
        cidInfo.put(PdfName.Supplement, new PdfNumber(0));
        cidFont.put(PdfName.CIDSystemInfo, cidInfo);
        if (!vertical) {
            cidFont.put(PdfName.DW, new PdfNumber(1000));
            StringBuilder buf = new StringBuilder("[");
            int lastNumber = -10;
            boolean firstTime = true;
            for (int[] metric : metrics) {
                if (metric[1] == 1000) {
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
    public PdfStream getToUnicode(Object metrics[]) throws PdfException {
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
            int metric[] = (int[]) metrics[k];
            String fromTo = toHex(metric[0]);
            buf.append(fromTo).append(fromTo).append(toHex(metric[2])).append('\n');
        }
        buf.append("endbfrange\n" +
                "endcmap\n" +
                "CMapName currentdict /CMap defineresource pop\n" +
                "end end\n");
        return new PdfStream(getDocument(), PdfEncodings.convertToBytes(buf.toString(), null));
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
            lastHValue = 1000;
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
                hValue = 1000;
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
            for (Map.Entry<Integer, int[]> e: usemap.entrySet()) {
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

    private static String toHex4(int n) {
        String s = "0000" + Integer.toHexString(n);
        return s.substring(s.length() - 4);
    }

    /** Gets an hex string in the format "&lt;HHHH&gt;".
     * @param n the number
     * @return the hex string
     */
    private static String toHex(int n) {
        if (n < 0x10000)
            return "<" + toHex4(n) + ">";
        n -= 0x10000;
        int high = n / 0x400 + 0xd800;
        int low = n % 0x400 + 0xdc00;
        return "[<" + toHex4(high) + toHex4(low) + ">]";
    }
}
