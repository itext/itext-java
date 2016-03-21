package com.itextpdf.kernel.font;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.kernel.pdf.*;

/**
 * This class allow to parse document font's encoding.
 */
class DocFontEncoding extends FontEncoding {

    protected DocFontEncoding() {
    }

    public static FontEncoding createDocFontEncoding(PdfObject encoding, CMapToUnicode toUnicode, boolean fillBaseEncoding) {
        if (encoding != null) {
            if (encoding.isName()) {
                return FontEncoding.createFontEncoding(((PdfName) encoding).getValue());
            } else if (encoding.isDictionary()) {
                DocFontEncoding fontEncoding = new DocFontEncoding();
                fontEncoding.differences = new String[256];
                if (fillBaseEncoding) {
                    fillBaseEncoding(fontEncoding, ((PdfDictionary) encoding).getAsName(PdfName.BaseEncoding));
                }
                fillDifferences(fontEncoding, ((PdfDictionary) encoding).getAsArray(PdfName.Differences), toUnicode);
                return fontEncoding;
            }
        }
        if (toUnicode != null) {
            DocFontEncoding fontEncoding = new DocFontEncoding();
            fontEncoding.differences = new String[256];
            fillDifferences(fontEncoding, toUnicode);
            return fontEncoding;
        } else {
            return FontEncoding.createFontSpecificEncoding();
        }
    }

    public static FontEncoding createDocFontEncoding(PdfObject encoding, CMapToUnicode toUnicode) {
        return createDocFontEncoding(encoding, toUnicode, true);
    }

    private static void fillBaseEncoding(DocFontEncoding fontEncoding, PdfName baseEncodingName) {
        if (baseEncodingName != null) {
            fontEncoding.baseEncoding = baseEncodingName.getValue();
        }
        if (PdfName.MacRomanEncoding.equals(baseEncodingName) || PdfName.WinAnsiEncoding.equals(baseEncodingName)
                || PdfName.Symbol.equals(baseEncodingName) || PdfName.ZapfDingbats.equals(baseEncodingName)) {
            String enc = PdfEncodings.WINANSI;
            if (PdfName.MacRomanEncoding.equals(baseEncodingName)) {
                enc = PdfEncodings.MACROMAN;
            } else if (PdfName.Symbol.equals(baseEncodingName)) {
                enc = PdfEncodings.SYMBOL;
            } else if (PdfName.ZapfDingbats.equals(baseEncodingName)) {
                enc = PdfEncodings.ZAPFDINGBATS;
            }
            fontEncoding.baseEncoding = enc;
            fontEncoding.fillNamedEncoding();
        } else {
            fontEncoding.fillStandardEncoding();
        }
    }

    private static void fillDifferences(DocFontEncoding fontEncoding, PdfArray diffs, CMapToUnicode toUnicode) {
        IntHashtable byte2uni = toUnicode != null ? toUnicode.createDirectMapping() : new IntHashtable();
        if (diffs != null) {
            int currentNumber = 0;
            for (int k = 0; k < diffs.size(); ++k) {
                PdfObject obj = diffs.get(k);
                if (obj.isNumber()) {
                    currentNumber = ((PdfNumber) obj).getIntValue();
                } else {
                    String glyphName = ((PdfName) obj).getValue();
                    Integer unicode = AdobeGlyphList.nameToUnicode(glyphName);
                    if (unicode != null) {
                        fontEncoding.codeToUnicode[currentNumber] = unicode;
                        fontEncoding.unicodeToCode.put(unicode, currentNumber);
                        fontEncoding.differences[currentNumber] = glyphName;
                        fontEncoding.unicodeDifferences.put(unicode, unicode);
                    } else {
                        if (byte2uni.contains(currentNumber)) {
                            unicode = byte2uni.get(currentNumber);
                            fontEncoding.codeToUnicode[currentNumber] = unicode;
                            fontEncoding.unicodeToCode.put(unicode, currentNumber);
                            fontEncoding.differences[currentNumber] = glyphName;
                            fontEncoding.unicodeDifferences.put(unicode, unicode);
                        }
                    }
                    currentNumber++;
                }
            }
        }
    }

    private static void fillDifferences(DocFontEncoding fontEncoding, CMapToUnicode toUnicode) {
        IntHashtable byte2uni = toUnicode.createDirectMapping();
        for(Integer code : byte2uni.getKeys()) {
            int unicode = byte2uni.get(code);
            String glyphName = AdobeGlyphList.unicodeToName(unicode);
            fontEncoding.codeToUnicode[code] = unicode;
            fontEncoding.unicodeToCode.put(unicode, code);
            fontEncoding.differences[code] = glyphName;
            fontEncoding.unicodeDifferences.put(unicode, unicode);
        }
    }
}
