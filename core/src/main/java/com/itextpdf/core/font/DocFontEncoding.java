package com.itextpdf.core.font;

import com.itextpdf.basics.font.AdobeGlyphList;
import com.itextpdf.basics.font.FontEncoding;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.cmap.CMapToUnicode;
import com.itextpdf.core.pdf.*;

/**
 * This class allow to parse document font's encoding.
 */
class DocFontEncoding extends FontEncoding {

    protected DocFontEncoding() {
    }

    public static FontEncoding createDocFontEncoding(PdfObject encoding, CMapToUnicode toUnicode) {
        if (encoding != null) {
            if (encoding.isName()) {
                return FontEncoding.createFontEncoding(((PdfName) encoding).getValue());
            } else if (encoding.isDictionary()) {
                DocFontEncoding fontEncoding = new DocFontEncoding();
                fillBaseEncoding(fontEncoding, ((PdfDictionary) encoding).getAsName(PdfName.BaseEncoding));
                fillDifferences(fontEncoding, ((PdfDictionary) encoding).getAsArray(PdfName.Differences), toUnicode);
                return fontEncoding;
            }
        }
        return FontEncoding.createFontSpecificEncoding();
    }

    private static void fillBaseEncoding(DocFontEncoding fontEncoding, PdfName baseEncodingName) {
        if (baseEncodingName != null) {
            fontEncoding.baseEncoding = baseEncodingName.getValue();
        }
        fontEncoding.differences = new String[256];
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
        if (diffs != null) {
            int currentNumber = 0;
            for (int k = 0; k < diffs.size(); ++k) {
                PdfObject obj = diffs.get(k);
                if (obj.isNumber()) {
                    currentNumber = ((PdfNumber) obj).getIntValue();
                } else {
                    String glyphName = ((PdfName) obj).getValue();
                    Integer c = AdobeGlyphList.nameToUnicode(glyphName);
                    if (c != null) {
                        fontEncoding.codeToUnicode[currentNumber] = c;
                        fontEncoding.unicodeToCode.put(c, currentNumber);
                        fontEncoding.differences[currentNumber] = glyphName;
                        fontEncoding.unicodeDifferences.put(currentNumber, (char) (int) c);
                    } else {
                        final String unicode = toUnicode.lookup(new byte[]{(byte) currentNumber}, 0, 1);
                        if ((unicode != null) && (unicode.length() == 1)) {
                            fontEncoding.codeToUnicode[currentNumber] = (int) unicode.charAt(0);
                            fontEncoding.unicodeToCode.put(unicode.charAt(0), currentNumber);
                            fontEncoding.differences[currentNumber] = glyphName;
                            fontEncoding.unicodeDifferences.put(currentNumber, unicode.charAt(0));
                        }
                    }
                    currentNumber++;
                }
            }
        }
    }
}
