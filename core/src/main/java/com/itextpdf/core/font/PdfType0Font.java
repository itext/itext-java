package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.font.CMapEncoding;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfLiteral;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfStream;

import java.util.HashMap;

public class PdfType0Font extends PdfFont {

    CMapEncoding cmap;

    public PdfType0Font(PdfDocument document, TrueTypeFont font, String cmap, String embed) throws PdfException {
        super(document);
        if (!cmap.equals(PdfEncodings.IDENTITY_H) && !cmap.equals(PdfEncodings.IDENTITY_V)) {
            throw new PdfRuntimeException("");
        }
        throw new UnsupportedOperationException();
    }

    public PdfType0Font(PdfDocument document, String fontName, String cmap, String embed) throws PdfException {
        super(document);
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] convertToBytes(String text) {
        throw new UnsupportedOperationException();
    }

    public boolean isIdentity() {
        return cmap.isDirect();
    }

    @Override
    public void flush() throws PdfException {
        //generate Base font
        throw new UnsupportedOperationException();
    }

    private PdfDictionary getFontDescriptor(PdfStream fontStream, String subsetPrefix) throws PdfException {
        PdfDictionary fontDescriptor = new PdfDictionary();
        fontDescriptor.makeIndirect(getDocument());
        fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);
//        fontDescriptor.put(PdfName.ASCENT, new PdfLiteral((String)fontDesc.get("Ascent")));
//        fontDescriptor.put(PdfName.CAPHEIGHT, new PdfLiteral((String)fontDesc.get("CapHeight")));
//        fontDescriptor.put(PdfName.DESCENT, new PdfLiteral((String)fontDesc.get("Descent")));
//        fontDescriptor.put(PdfName.FLAGS, new PdfLiteral((String)fontDesc.get("Flags")));
//        fontDescriptor.put(PdfName.FONTBBOX, new PdfLiteral((String)fontDesc.get("FontBBox")));
//        fontDescriptor.put(PdfName.FONTNAME, new PdfName(fontName + style));
//        fontDescriptor.put(PdfName.ITALICANGLE, new PdfLiteral((String)fontDesc.get("ItalicAngle")));
//        fontDescriptor.put(PdfName.STEMV, new PdfLiteral((String)fontDesc.get("StemV")));
//        PdfDictionary pdic = new PdfDictionary();
//        pdic.put(PdfName.PANOSE, new PdfString((String)fontDesc.get("Panose"), null));
//        fontDescriptor.put(PdfName.STYLE, pdic);
        return fontDescriptor;
    }

    private PdfDictionary getCidFont(PdfDictionary fontDescriptor, HashMap<Integer, int[]> longTag) throws PdfException {
        PdfDictionary cidFont = new PdfDictionary();
        cidFont.makeIndirect(getDocument());
        cidFont.put(PdfName.Type, PdfName.Font);
//        cidFont.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
//        cidFont.put(PdfName.BASEFONT, new PdfName(fontName + style));
//        cidFont.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
//        int[] keys = cjkTag.toOrderedKeys();
//        String w = convertToHCIDMetrics(keys, hMetrics);
//        if (w != null)
//            dic.put(PdfName.W, new PdfLiteral(w));
//        if (vertical) {
//            w = convertToVCIDMetrics(keys, vMetrics, hMetrics);
//            if (w != null)
//                dic.put(PdfName.W2, new PdfLiteral(w));
//        } else {
//            dic.put(PdfName.DW, new PdfNumber(1000));
//        }
//        PdfDictionary cdic = new PdfDictionary();
//        if (cmap.isDirect()) {
//            cdic.put(PdfName.REGISTRY, new PdfString(cidUni.getRegistry(), null));
//            cdic.put(PdfName.ORDERING, new PdfString(cidUni.getOrdering(), null));
//            cdic.put(PdfName.SUPPLEMENT, new PdfNumber(cidUni.getSupplement()));
//        }
//        else {
//            cdic.put(PdfName.REGISTRY, new PdfString(cidByte.getRegistry(), null));
//            cdic.put(PdfName.ORDERING, new PdfString(cidByte.getOrdering(), null));
//            cdic.put(PdfName.SUPPLEMENT, new PdfNumber(cidByte.getSupplement()));
//        }
//        cidFont.put(PdfName.CIDSYSTEMINFO, cdic);
        return cidFont;
    }

    /** Creates a ToUnicode CMap to allow copy and paste from Acrobat.
     * @param metrics metrics[0] contains the glyph index and metrics[2]
     * contains the Unicode code
     * @return the stream representing this CMap or <CODE>null</CODE>
     */
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

}
