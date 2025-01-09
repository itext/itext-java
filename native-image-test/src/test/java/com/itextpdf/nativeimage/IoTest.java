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
package com.itextpdf.nativeimage;

import com.itextpdf.io.codec.brotli.dec.Dictionary;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.font.CjkResourceLoader;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.cmap.AbstractCMap;
import com.itextpdf.io.font.constants.StandardFonts;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

class IoTest {
    @Test
    void adobeGlyphList() {
        Assertions.assertEquals(97, AdobeGlyphList.nameToUnicode("a"));
    }

    @Test
    void standardFonts() throws IOException {
        checkType1Font(StandardFonts.COURIER);
        checkType1Font(StandardFonts.COURIER_BOLD);
        checkType1Font(StandardFonts.COURIER_BOLDOBLIQUE);
        checkType1Font(StandardFonts.COURIER_OBLIQUE);

        checkType1Font(StandardFonts.HELVETICA);
        checkType1Font(StandardFonts.HELVETICA_BOLD);
        checkType1Font(StandardFonts.HELVETICA_BOLDOBLIQUE);
        checkType1Font(StandardFonts.HELVETICA_OBLIQUE);

        checkType1Font(StandardFonts.SYMBOL);

        checkType1Font(StandardFonts.TIMES_BOLD);
        checkType1Font(StandardFonts.TIMES_BOLDITALIC);
        checkType1Font(StandardFonts.TIMES_ITALIC);
        checkType1Font(StandardFonts.TIMES_ROMAN);

        checkType1Font(StandardFonts.ZAPFDINGBATS);
    }

    @Test
    void fontAsianCmaps() {
        checkFontAsianCmap("78-EUC-H", "Japan1");
        checkFontAsianCmap("78-EUC-V", "Japan1");
        checkFontAsianCmap("78-H", "Japan1");
        checkFontAsianCmap("78-RKSJ-H", "Japan1");
        checkFontAsianCmap("78-RKSJ-V", "Japan1");
        checkFontAsianCmap("78-V", "Japan1");
        checkFontAsianCmap("78ms-RKSJ-H", "Japan1");
        checkFontAsianCmap("78ms-RKSJ-V", "Japan1");
        checkFontAsianCmap("83pv-RKSJ-H", "Japan1");
        checkFontAsianCmap("90ms-RKSJ-H", "Japan1");
        checkFontAsianCmap("90ms-RKSJ-V", "Japan1");
        checkFontAsianCmap("90msp-RKSJ-H", "Japan1");
        checkFontAsianCmap("90msp-RKSJ-V", "Japan1");
        checkFontAsianCmap("90pv-RKSJ-H", "Japan1");
        checkFontAsianCmap("90pv-RKSJ-V", "Japan1");
        checkFontAsianCmap("Add-H", "Japan1");
        checkFontAsianCmap("Add-RKSJ-H", "Japan1");
        checkFontAsianCmap("Add-RKSJ-V", "Japan1");
        checkFontAsianCmap("Add-V", "Japan1");

        checkFontAsianCmap("Adobe-CNS1-0", "CNS1");
        checkFontAsianCmap("Adobe-CNS1-1", "CNS1");
        checkFontAsianCmap("Adobe-CNS1-2", "CNS1");
        checkFontAsianCmap("Adobe-CNS1-3", "CNS1");
        checkFontAsianCmap("Adobe-CNS1-4", "CNS1");
        checkFontAsianCmap("Adobe-CNS1-5", "CNS1");
        checkFontAsianCmap("Adobe-CNS1-6", "CNS1");
        checkFontAsianCmap("Adobe-CNS1-7", "CNS1");

        checkFontAsianCmap("Adobe-GB1-0", "GB1");
        checkFontAsianCmap("Adobe-GB1-1", "GB1");
        checkFontAsianCmap("Adobe-GB1-2", "GB1");
        checkFontAsianCmap("Adobe-GB1-3", "GB1");
        checkFontAsianCmap("Adobe-GB1-4", "GB1");
        checkFontAsianCmap("Adobe-GB1-5", "GB1");

        checkFontAsianCmap("Adobe-Japan1-0", "Japan1");
        checkFontAsianCmap("Adobe-Japan1-1", "Japan1");
        checkFontAsianCmap("Adobe-Japan1-2", "Japan1");
        checkFontAsianCmap("Adobe-Japan1-3", "Japan1");
        checkFontAsianCmap("Adobe-Japan1-4", "Japan1");
        checkFontAsianCmap("Adobe-Japan1-5", "Japan1");
        checkFontAsianCmap("Adobe-Japan1-6", "Japan1");
        checkFontAsianCmap("Adobe-Japan1-7", "Japan1");

        checkFontAsianCmap("Adobe-Korea1-0", "Korea1");
        checkFontAsianCmap("Adobe-Korea1-1", "Korea1");
        checkFontAsianCmap("Adobe-Korea1-2", "Korea1");

        checkFontAsianCmap("Adobe-KR-0", "KR");
        checkFontAsianCmap("Adobe-KR-1", "KR");
        checkFontAsianCmap("Adobe-KR-2", "KR");
        checkFontAsianCmap("Adobe-KR-3", "KR");
        checkFontAsianCmap("Adobe-KR-4", "KR");
        checkFontAsianCmap("Adobe-KR-5", "KR");
        checkFontAsianCmap("Adobe-KR-6", "KR");
        checkFontAsianCmap("Adobe-KR-7", "KR");
        checkFontAsianCmap("Adobe-KR-8", "KR");
        checkFontAsianCmap("Adobe-KR-9", "KR");

        checkFontAsianCmap("B5-H", "CNS1");
        checkFontAsianCmap("B5-V", "CNS1");
        checkFontAsianCmap("B5pc-H", "CNS1");
        checkFontAsianCmap("B5pc-V", "CNS1");
        checkFontAsianCmap("CNS1-H", "CNS1");
        checkFontAsianCmap("CNS1-V", "CNS1");
        checkFontAsianCmap("CNS2-H", "CNS1");
        checkFontAsianCmap("CNS2-V", "CNS1");
        checkFontAsianCmap("CNS-EUC-H", "CNS1");
        checkFontAsianCmap("CNS-EUC-V", "CNS1");
        checkFontAsianCmap("ETen-B5-H", "CNS1");
        checkFontAsianCmap("ETen-B5-V", "CNS1");
        checkFontAsianCmap("ETenms-B5-H", "CNS1");
        checkFontAsianCmap("ETenms-B5-V", "CNS1");
        checkFontAsianCmap("ETHK-B5-H", "CNS1");
        checkFontAsianCmap("ETHK-B5-V", "CNS1");

        checkFontAsianCmap("EUC-H", "Japan1");
        checkFontAsianCmap("EUC-V", "Japan1");
        checkFontAsianCmap("Ext-H", "Japan1");
        checkFontAsianCmap("Ext-RKSJ-H", "Japan1");
        checkFontAsianCmap("Ext-RKSJ-V", "Japan1");
        checkFontAsianCmap("Ext-V", "Japan1");

        checkFontAsianCmap("GB-EUC-H", "GB1");
        checkFontAsianCmap("GB-EUC-V", "GB1");
        checkFontAsianCmap("GBK2K-H", "GB1");
        checkFontAsianCmap("GBK2K-V", "GB1");
        checkFontAsianCmap("GBK-EUC-H", "GB1");
        checkFontAsianCmap("GBK-EUC-V", "GB1");
        checkFontAsianCmap("GBKp-EUC-H", "GB1");
        checkFontAsianCmap("GBKp-EUC-V", "GB1");
        checkFontAsianCmap("GBpc-EUC-H", "GB1");
        checkFontAsianCmap("GBpc-EUC-V", "GB1");
        checkFontAsianCmap("GBT-EUC-H", "GB1");
        checkFontAsianCmap("GBT-EUC-V", "GB1");
        checkFontAsianCmap("GBT-H", "GB1");
        checkFontAsianCmap("GBT-V", "GB1");
        checkFontAsianCmap("GBTpc-EUC-H", "GB1");
        checkFontAsianCmap("GBTpc-EUC-V", "GB1");

        checkFontAsianCmap("H", "Japan1");
        checkFontAsianCmap("Hankaku", "Japan1");
        checkFontAsianCmap("Hiragana", "Japan1");

        checkFontAsianCmap("HKdla-B5-H", "CNS1");
        checkFontAsianCmap("HKdla-B5-V", "CNS1");
        checkFontAsianCmap("HKdlb-B5-H", "CNS1");
        checkFontAsianCmap("HKdlb-B5-V", "CNS1");
        checkFontAsianCmap("HKgccs-B5-H", "CNS1");
        checkFontAsianCmap("HKgccs-B5-V", "CNS1");
        checkFontAsianCmap("HKm314-B5-H", "CNS1");
        checkFontAsianCmap("HKm314-B5-V", "CNS1");
        checkFontAsianCmap("HKm471-B5-H", "CNS1");
        checkFontAsianCmap("HKm471-B5-V", "CNS1");
        checkFontAsianCmap("HKscs-B5-H", "CNS1");
        checkFontAsianCmap("HKscs-B5-V", "CNS1");

        checkFontAsianCmap("Identity-H", "Identity");
        checkFontAsianCmap("Identity-V", "Identity");

        checkFontAsianCmap("Katakana", "Japan1");

        checkFontAsianCmap("KSC-EUC-H", "Korea1");
        checkFontAsianCmap("KSC-EUC-V", "Korea1");
        checkFontAsianCmap("KSC-H", "Korea1");
        checkFontAsianCmap("KSC-Johab-H", "Korea1");
        checkFontAsianCmap("KSC-Johab-V", "Korea1");
        checkFontAsianCmap("KSC-V", "Korea1");
        checkFontAsianCmap("KSCms-UHC-H", "Korea1");
        checkFontAsianCmap("KSCms-UHC-HW-H", "Korea1");
        checkFontAsianCmap("KSCms-UHC-HW-V", "Korea1");
        checkFontAsianCmap("KSCms-UHC-V", "Korea1");
        checkFontAsianCmap("KSCpc-EUC-H", "Korea1");
        checkFontAsianCmap("KSCpc-EUC-V", "Korea1");

        checkFontAsianCmap("NWP-H", "Japan1");
        checkFontAsianCmap("NWP-V", "Japan1");
        checkFontAsianCmap("RKSJ-H", "Japan1");
        checkFontAsianCmap("RKSJ-V", "Japan1");
        checkFontAsianCmap("Roman", "Japan1");

        checkFontAsianCmap("UniAKR-UTF8-H", "KR");
        checkFontAsianCmap("UniAKR-UTF16-H", "KR");
        checkFontAsianCmap("UniAKR-UTF32-H", "KR");

        checkFontAsianCmap("UniCNS-UCS2-H", "CNS1");
        checkFontAsianCmap("UniCNS-UCS2-V", "CNS1");
        checkFontAsianCmap("UniCNS-UTF8-H", "CNS1");
        checkFontAsianCmap("UniCNS-UTF8-V", "CNS1");
        checkFontAsianCmap("UniCNS-UTF16-H", "CNS1");
        checkFontAsianCmap("UniCNS-UTF16-V", "CNS1");
        checkFontAsianCmap("UniCNS-UTF32-H", "CNS1");
        checkFontAsianCmap("UniCNS-UTF32-V", "CNS1");

        checkFontAsianCmap("UniGB-UCS2-H", "GB1");
        checkFontAsianCmap("UniGB-UCS2-V", "GB1");
        checkFontAsianCmap("UniGB-UTF8-H", "GB1");
        checkFontAsianCmap("UniGB-UTF8-V", "GB1");
        checkFontAsianCmap("UniGB-UTF16-H", "GB1");
        checkFontAsianCmap("UniGB-UTF16-V", "GB1");
        checkFontAsianCmap("UniGB-UTF32-H", "GB1");
        checkFontAsianCmap("UniGB-UTF32-V", "GB1");

        checkFontAsianCmap("UniJIS2004-UTF8-H", "Japan1");
        checkFontAsianCmap("UniJIS2004-UTF8-V", "Japan1");
        checkFontAsianCmap("UniJIS2004-UTF16-H", "Japan1");
        checkFontAsianCmap("UniJIS2004-UTF16-V", "Japan1");
        checkFontAsianCmap("UniJIS2004-UTF32-H", "Japan1");
        checkFontAsianCmap("UniJIS2004-UTF32-V", "Japan1");
        checkFontAsianCmap("UniJIS-UCS2-H", "Japan1");
        checkFontAsianCmap("UniJIS-UCS2-HW-H", "Japan1");
        checkFontAsianCmap("UniJIS-UCS2-HW-V", "Japan1");
        checkFontAsianCmap("UniJIS-UCS2-V", "Japan1");
        checkFontAsianCmap("UniJIS-UTF8-H", "Japan1");
        checkFontAsianCmap("UniJIS-UTF8-V", "Japan1");
        checkFontAsianCmap("UniJIS-UTF16-H", "Japan1");
        checkFontAsianCmap("UniJIS-UTF16-V", "Japan1");
        checkFontAsianCmap("UniJIS-UTF32-H", "Japan1");
        checkFontAsianCmap("UniJIS-UTF32-V", "Japan1");
        checkFontAsianCmap("UniJISPro-UCS2-HW-V", "Japan1");
        checkFontAsianCmap("UniJISPro-UCS2-V", "Japan1");
        checkFontAsianCmap("UniJISPro-UTF8-V", "Japan1");
        checkFontAsianCmap("UniJISX0213-UTF32-H", "Japan1");
        checkFontAsianCmap("UniJISX0213-UTF32-V", "Japan1");
        checkFontAsianCmap("UniJISX02132004-UTF32-H", "Japan1");
        checkFontAsianCmap("UniJISX02132004-UTF32-V", "Japan1");

        checkFontAsianCmap("UniKS-UCS2-H", "Korea1");
        checkFontAsianCmap("UniKS-UCS2-V", "Korea1");
        checkFontAsianCmap("UniKS-UTF8-H", "Korea1");
        checkFontAsianCmap("UniKS-UTF8-V", "Korea1");
        checkFontAsianCmap("UniKS-UTF16-H", "Korea1");
        checkFontAsianCmap("UniKS-UTF16-V", "Korea1");
        checkFontAsianCmap("UniKS-UTF32-H", "Korea1");
        checkFontAsianCmap("UniKS-UTF32-V", "Korea1");

        checkFontAsianCmap("V", "Japan1");
        checkFontAsianCmap("WP-Symbol", "Japan1");

        checkFontAsianCmap("toUnicode/Adobe-CNS1-UCS2", "Adobe_CNS1_UCS2");
        checkFontAsianCmap("toUnicode/Adobe-GB1-UCS2", "Adobe_GB1_UCS2");
        checkFontAsianCmap("toUnicode/Adobe-Japan1-UCS2", "Adobe_Japan1_UCS2");
        checkFontAsianCmap("toUnicode/Adobe-Korea1-UCS2", "Adobe_Korea1_UCS2");
        checkFontAsianCmap("toUnicode/Adobe-KR-UCS2", "Adobe_KR_UCS2");
    }

    @Test
    void predefinedCidFonts() {
        Map<String, Map<String, Object>> cidFonts = CjkResourceLoader.getAllPredefinedCidFonts();
        Assertions.assertEquals(11, cidFonts.size());
    }

    @Test
    void brotliData() {
        ByteBuffer buf = Dictionary.getData();
        Assertions.assertEquals(122784, buf.remaining());
    }

    private void checkFontAsianCmap(String cmapName, String ordering) {
        AbstractCMap cmap = CjkResourceLoader.getUni2CidCmap(cmapName);
        Assertions.assertEquals(cmapName.substring(cmapName.lastIndexOf("/") + 1), cmap.getName());
        Assertions.assertEquals(ordering, cmap.getOrdering());
    }

    private void checkType1Font(String fontName) throws IOException {
        FontProgram font = FontProgramFactory.createFont(fontName, null, false);
        Assertions.assertInstanceOf(Type1Font.class, font);
        Assertions.assertEquals(fontName, font.getFontNames().getFontName());
    }
}
