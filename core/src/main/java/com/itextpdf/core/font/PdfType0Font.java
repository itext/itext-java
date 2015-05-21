package com.itextpdf.core.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.font.*;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

import java.util.Arrays;
import java.util.HashMap;

public class PdfType0Font extends PdfFont {

    private static final int[] Empty = {};

    private static final int First = 0;
    private static final int Bracket = 1;
    private static final int Serial = 2;
    private static final int V1y = 880;

    private boolean vertical;
    protected FontProgram font;
    protected CMapEncoding cmapEncoding;
    protected HashMap<Integer, int[]> longTag;

    public PdfType0Font(PdfDocument document, TrueTypeFont font, String cmap, String embed) throws PdfException {
        super(document);
        if (!cmap.equals(PdfEncodings.IDENTITY_H) && !cmap.equals(PdfEncodings.IDENTITY_V)) {
            throw new PdfRuntimeException("");
        }
        throw new UnsupportedOperationException();
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
    }

    @Override
    public byte[] convertToBytes(String text) {
        int len = text.length();
        if (isIdentity()) {
            for (int k = 0; k < len; ++k) {
                longTag.put((int)text.charAt(k), Empty);
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
    }

    public boolean isIdentity() {
        return cmapEncoding.isDirect();
    }

    @Override
    public void flush() throws PdfException {
        getPdfObject().put(PdfName.Type, PdfName.Font);
        getPdfObject().put(PdfName.Subtype, PdfName.Type0);
        String name = font.getFontName();
        if (font.getStyle().length() > 0) {
            name += "-" + font.getStyle().substring(1);
        }
        name += "-" + cmapEncoding.getCmapName();
        getPdfObject().put(PdfName.BaseFont, new PdfName(name));
        getPdfObject().put(PdfName.Encoding, new PdfName(cmapEncoding.getCmapName()));
        PdfDictionary cidFont = getCidFont();
        getPdfObject().put(PdfName.DescendantFonts, new PdfArray(cidFont));
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

    private PdfDictionary getCidFont() throws PdfException {
        PdfDictionary cidFont = new PdfDictionary();
        cidFont.makeIndirect(getDocument());
        cidFont.put(PdfName.Type, PdfName.Font);
        cidFont.put(PdfName.Subtype, PdfName.CIDFontType0);
        cidFont.put(PdfName.BaseFont, new PdfName(font.getFontName() + font.getStyle()));
        cidFont.put(PdfName.FontDescriptor, getFontDescriptor());
        int[] keys = Utilities.toArray(longTag.keySet());
        Arrays.sort(keys);
        String w = convertToHCIDMetrics(keys, ((CidFont)font).getHMetrics());
        if (w != null) {
            cidFont.put(PdfName.W, new PdfLiteral(w));
        }
        if (vertical) {
            w = convertToVCIDMetrics(keys, ((CidFont)font).getVMetrics(), ((CidFont)font).getHMetrics());
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

    /** Creates a ToUnicode CMap to allow copy and paste from Acrobat.
     * @param metrics metrics[0] contains the glyph index and metrics[2]
     * contains the Unicode code
     * @return the stream representing this CMap or <CODE>null</CODE>
     */
    //TODO TTFUNI method
    public PdfStream getToUnicode(Object metrics[]) {
//        if (metrics.length == 0)
//            return null;
//        StringBuffer buf = new StringBuffer(
//                "/CIDInit /ProcSet findresource begin\n" +
//                        "12 dict begin\n" +
//                        "begincmap\n" +
//                        "/CIDSystemInfo\n" +
//                        "<< /Registry (TTX+0)\n" +
//                        "/Ordering (T42UV)\n" +
//                        "/Supplement 0\n" +
//                        ">> def\n" +
//                        "/CMapName /TTX+0 def\n" +
//                        "/CMapType 2 def\n" +
//                        "1 begincodespacerange\n" +
//                        "<0000><FFFF>\n" +
//                        "endcodespacerange\n");
//        int size = 0;
//        for (int k = 0; k < metrics.length; ++k) {
//            if (size == 0) {
//                if (k != 0) {
//                    buf.append("endbfrange\n");
//                }
//                size = Math.min(100, metrics.length - k);
//                buf.append(size).append(" beginbfrange\n");
//            }
//            --size;
//            int metric[] = (int[])metrics[k];
//            String fromTo = toHex(metric[0]);
//            buf.append(fromTo).append(fromTo).append(toHex(metric[2])).append('\n');
//        }
//        buf.append(
//                "endbfrange\n" +
//                        "endcmap\n" +
//                        "CMapName currentdict /CMap defineresource pop\n" +
//                        "end end\n");
//        String s = buf.toString();
//        PdfStream stream = new PdfStream(PdfEncodings.convertToBytes(s, null));
//        stream.flateCompress(compressionLevel);
//        return stream;
        return null;
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

}
