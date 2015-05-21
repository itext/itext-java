package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.io.PdfTokeniser;

import java.io.IOException;
import java.util.ArrayList;

public class CMapParser {

    public static final int Cid2UniType = 1;
    public static final int Uni2CidType = 2;
    public static final int Byte2CidType = 3;
    public static final int Cid2ByteType = 4;
    public static final int ToUnicodeType = 5;


    private static final String Def = "def";
    private static final String EndCidRange = "endcidrange";
    private static final String EndCidChar = "endcidchar";
    private static final String EndBfRange = "endbfrange";
    private static final String EndBfChar = "endbfchar";
    private static final String UseCmap = "usecmap";


    private static final String Registry = "/Registry";
    private static final String Ordering = "/Ordering";
    private static final String Supplement = "/Supplement";
    private static final String CMapName = "/CMapName";

    private static final int MaxLevel = 10;

    public static void parseCid(String cmapName, AbstractCMap cmap, CMapLocation location) throws IOException {
        parseCid(cmapName, cmap, location, 0);
    }

    private static void parseCid(String cmapName, AbstractCMap cmap, CMapLocation location, int level) throws IOException {
        if (level >= MaxLevel)
            return;
        PdfTokeniser inp = location.getLocation(cmapName);
        try {
            ArrayList<CMapObject> list = new ArrayList<CMapObject>();
            CMapContentParser cp = new CMapContentParser(inp);
            int maxExc = 50;
            while (true) {
                try {
                    cp.parse(list);
                } catch (Exception ex) {
                    if (--maxExc < 0)
                        break;
                    continue;
                }
                if (list.isEmpty())
                    break;
                String last = list.get(list.size() - 1).toString();
                if (level == 0 && list.size() == 3 && last.equals(Def)) {
                    CMapObject cmapObject = list.get(0);
                    if (Registry.equals(cmapObject.toString())) {
                        cmap.setRegistry(list.get(1).toString());
                    } else if (Ordering.equals(cmapObject.toString())) {
                        cmap.setOrdering(list.get(1).toString());
                    } else if (CMapName.equals(cmapObject.toString())) {
                        cmap.setName(list.get(1).toString());
                    } else if (Supplement.equals(cmapObject.toString())) {
                        try {
                            cmap.setSupplement((int) list.get(1).getValue());
                        } catch (Exception ignored) {
                        }
                    }
                } else if ((last.equals(EndCidChar) || last.equals(EndBfChar)) && list.size() >= 3) {
                    int lMax = list.size() - 2;
                    for (int k = 0; k < lMax; k += 2) {
                        if (list.get(k).isString()) {
                            cmap.addChar(list.get(k).toString(), list.get(k + 1));
                        }
                    }
                } else if ((last.equals(EndCidRange) || last.equals(EndBfRange)) && list.size() >= 4) {
                    int lMax = list.size() - 3;
                    for (int k = 0; k < lMax; k += 3) {
                        if (list.get(k).isString() && list.get(k + 1).isString()) {
                            cmap.addRange(list.get(k).toString(), list.get(k + 1).toString(), list.get(k + 2));
                        }
                    }
                } else if (last.equals(UseCmap) && list.size() == 2 && list.get(0).isName()) {
                    parseCid(list.get(0).toString(), cmap, location, level + 1);
                }
            }
        } finally {
            inp.close();
        }
    }

//    private static void encodeSequence(int size, byte[] seqs, char cid, ArrayList<char[]> planes) {
//        --size;
//        int nextPlane = 0;
//        for (int idx = 0; idx < size; ++idx) {
//            char plane[] = planes.get(nextPlane);
//            int one = seqs[idx] & 0xff;
//            char c = plane[one];
//            if (c != 0 && (c & 0x8000) == 0)
//                throw new PdfRuntimeException("inconsistent.mapping");
//            if (c == 0) {
//                planes.add(new char[256]);
//                c = (char) (planes.size() - 1 | 0x8000);
//                plane[one] = c;
//            }
//            nextPlane = c & 0x7fff;
//        }
//        char plane[] = planes.get(nextPlane);
//        int one = seqs[size] & 0xff;
//        char c = plane[one];
//        if ((c & 0x8000) != 0)
//            throw new PdfRuntimeException("inconsistent.mapping");
//        plane[one] = cid;
//    }
}
