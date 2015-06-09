package com.itextpdf.core.font;


import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.CMapEncoding;
import com.itextpdf.basics.font.CidFont;
import com.itextpdf.basics.font.CidFontProperties;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.font.cmap.CMapContentParser;
import com.itextpdf.basics.font.cmap.CMapObject;
import com.itextpdf.basics.io.PdfTokeniser;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.PdfString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class PdfCompositeFont extends PdfSimpleFont<FontProgram> {

    protected boolean vertical;
    protected CMapEncoding cmapEncoding;
    protected HashMap<Integer, int[]> longTag;
    protected int cidFontType;
    protected static final int CidFontType0 = 0;
    protected static final int CidFontType2 = 2;

    public PdfCompositeFont(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    public PdfCompositeFont(PdfDocument pdfDocument) throws PdfException {
        super(pdfDocument);
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
            fontProgram.setFontName(cidBaseFont.getValue());
            PdfDictionary fromDescriptorDictionary = fromCidFont.getAsDictionary(PdfName.FontDescriptor);
            if (fromDescriptorDictionary != null) {
                PdfDictionary toDescriptorDictionary = getNewFontDescriptor(fromDescriptorDictionary);
                toCidFont.put(PdfName.FontDescriptor, toDescriptorDictionary);
                toDescriptorDictionary.flush();
            }

            if (w != null) {
                toCidFont.put(PdfName.W, w);
                if (fontProgram instanceof CidFont) {
                    ((CidFont) fontProgram).sethMetrics(readWidths(w));
                }
            }

            if (w2 != null) {
                toCidFont.put(PdfName.W2, w2);
                if (fontProgram instanceof CidFont) {
                    ((CidFont) fontProgram).setvMetrics(readWidths(w2));
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
                    dwVal = dw.intValue();
                }
                IntHashtable widths = readWidths(w);
                if (toUnicode instanceof PdfStream) {
                    PdfStream newStream = copyFontFileStream((PdfStream) toUnicode);
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

    private IntHashtable readWidths(PdfArray ws) throws PdfException {
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
        longTag = new HashMap<Integer, int[]>();
        String encoding = fontDictionary.getAsName(PdfName.Encoding).getValue();
        String fontName = fontDictionary.getAsArray(PdfName.DescendantFonts).getAsDictionary(0).getAsName(PdfName.BaseFont).getValue();
        if (CidFontProperties.isCidFont(fontName, encoding)) {
            fontProgram = new CidFont(fontName);
            vertical = encoding.endsWith(FontConstants.V_SYMBOL);
            String uniMap = getUniMapName(fontProgram.getRegistry());
            cmapEncoding = new CMapEncoding(encoding, uniMap);
            cidFontType = CidFontType0;
        } else {
            cmapEncoding = new CMapEncoding(encoding);
            cidFontType = CidFontType2;
            fontProgram = new TrueTypeFont(encoding);
        }
    }

    private void fillMetrics(byte[] touni, IntHashtable widths, int dw) {
        try {
            CMapContentParser ps = new CMapContentParser(new PdfTokeniser(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(touni))));
            CMapObject ob = null;
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
                                longTag.put(Integer.valueOf(unic), new int[]{cidc, w});
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
                                        longTag.put(Integer.valueOf(unic), new int[]{cid1c, w});
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
                                        longTag.put(Integer.valueOf(unic), new int[]{cid1c, w});
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

}
