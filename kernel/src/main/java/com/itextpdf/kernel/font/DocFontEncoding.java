/*
    $Id$

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

    private static final long serialVersionUID = -4248206280861742148L;

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
                    currentNumber = ((PdfNumber) obj).intValue();
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
