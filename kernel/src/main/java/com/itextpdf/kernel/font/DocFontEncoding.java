/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.commons.utils.MessageFormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                fontEncoding.differences = new String[256];
                fillBaseEncoding(fontEncoding, ((PdfDictionary) encoding).getAsName(PdfName.BaseEncoding));
                fillDifferences(fontEncoding, ((PdfDictionary) encoding).getAsArray(PdfName.Differences), toUnicode);
                return fontEncoding;
            }
        }
        if (toUnicode != null) {
            DocFontEncoding fontEncoding = new DocFontEncoding();
            FontEncoding.fillFontEncoding(fontEncoding);

            fontEncoding.differences = new String[256];
            fillDifferences(fontEncoding, toUnicode);
            return fontEncoding;
        } else {
            return FontEncoding.createFontSpecificEncoding();
        }
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
            // Actually, font's built in encoding should be used if font file is embedded
            // and standard encoding should be used otherwise
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
                    currentNumber = ((PdfNumber) obj).intValue();
                } else if (currentNumber > 255) {
                    Logger LOGGER = LoggerFactory.getLogger(DocFontEncoding.class);
                    LOGGER.warn(MessageFormatUtil.format(IoLogMessageConstant.DOCFONT_HAS_ILLEGAL_DIFFERENCES,
                            ((PdfName) obj).getValue()));
                    /* don't return or break, because differences subarrays may
                     * be in any order:
                     * e.g. [255 /space /one 250 /two /three]
                     * /one is invalid but all others should be parsed
                     */
                } else {
                    String glyphName = ((PdfName) obj).getValue();
                    int unicode = AdobeGlyphList.nameToUnicode(glyphName);
                    if (unicode != -1) {
                        fontEncoding.codeToUnicode[currentNumber] = (int) unicode;
                        fontEncoding.unicodeToCode.put((int) unicode, currentNumber);
                        fontEncoding.differences[currentNumber] = glyphName;
                        fontEncoding.unicodeDifferences.put((int) unicode, (int) unicode);
                    } else {
                        if (byte2uni.containsKey(currentNumber)) {
                            unicode = byte2uni.get(currentNumber);
                            fontEncoding.codeToUnicode[currentNumber] = (int) unicode;
                            fontEncoding.unicodeToCode.put((int) unicode, currentNumber);
                            fontEncoding.differences[currentNumber] = glyphName;
                            fontEncoding.unicodeDifferences.put((int) unicode, (int) unicode);
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
            int unicode = byte2uni.get((int) code);
            String glyphName = AdobeGlyphList.unicodeToName(unicode);
            fontEncoding.codeToUnicode[(int) code] = unicode;
            fontEncoding.unicodeToCode.put(unicode, (int) code);
            fontEncoding.differences[(int) code] = glyphName;
            fontEncoding.unicodeDifferences.put(unicode, unicode);
        }
    }
}
