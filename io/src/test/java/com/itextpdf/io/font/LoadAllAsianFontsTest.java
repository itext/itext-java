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
package com.itextpdf.io.font;

import com.itextpdf.io.font.cmap.AbstractCMap;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Tag("IntegrationTest")
public class LoadAllAsianFontsTest extends ExtendedITextTest {

    @ParameterizedTest(name = "{index}: {0} {1}")
    @MethodSource("data")
    // TODO DEVSIX-8619 All cmap parsing errors should be fixed and this logging should then be removed
    @LogMessages(messages = {
            @com.itextpdf.test.annotations.LogMessage(messageTemplate = IoLogMessageConstant.UNKNOWN_ERROR_WHILE_PROCESSING_CMAP, ignore = true),

    })
    public void testAsianFonts(String cmapName, String ordering) {
        checkFontAsianCmap(cmapName, ordering);
    }

    public static Collection<Object[]> data() {
        List<Object[]> result = new ArrayList<>();
        result.add(new Object[]{"78-EUC-H", "Japan1"});
        result.add(new Object[]{"78-EUC-V", "Japan1"});
        result.add(new Object[]{"78-H", "Japan1"});
        result.add(new Object[]{"78-RKSJ-H", "Japan1"});
        result.add(new Object[]{"78-RKSJ-V", "Japan1"});
        result.add(new Object[]{"78-V", "Japan1"});
        result.add(new Object[]{"78ms-RKSJ-H", "Japan1"});
        result.add(new Object[]{"78ms-RKSJ-V", "Japan1"});
        result.add(new Object[]{"83pv-RKSJ-H", "Japan1"});
        result.add(new Object[]{"90ms-RKSJ-H", "Japan1"});
        result.add(new Object[]{"90ms-RKSJ-V", "Japan1"});
        result.add(new Object[]{"90msp-RKSJ-H", "Japan1"});
        result.add(new Object[]{"90msp-RKSJ-V", "Japan1"});
        result.add(new Object[]{"90pv-RKSJ-H", "Japan1"});
        result.add(new Object[]{"90pv-RKSJ-V", "Japan1"});
        result.add(new Object[]{"Add-H", "Japan1"});
        result.add(new Object[]{"Add-RKSJ-H", "Japan1"});
        result.add(new Object[]{"Add-RKSJ-V", "Japan1"});
        result.add(new Object[]{"Add-V", "Japan1"});

        result.add(new Object[]{"Adobe-CNS1-0", "CNS1"});
        result.add(new Object[]{"Adobe-CNS1-1", "CNS1"});
        result.add(new Object[]{"Adobe-CNS1-2", "CNS1"});
        result.add(new Object[]{"Adobe-CNS1-3", "CNS1"});
        result.add(new Object[]{"Adobe-CNS1-4", "CNS1"});
        result.add(new Object[]{"Adobe-CNS1-5", "CNS1"});
        result.add(new Object[]{"Adobe-CNS1-6", "CNS1"});
        result.add(new Object[]{"Adobe-CNS1-7", "CNS1"});

        result.add(new Object[]{"Adobe-GB1-0", "GB1"});
        result.add(new Object[]{"Adobe-GB1-1", "GB1"});
        result.add(new Object[]{"Adobe-GB1-2", "GB1"});
        result.add(new Object[]{"Adobe-GB1-3", "GB1"});
        result.add(new Object[]{"Adobe-GB1-4", "GB1"});
        result.add(new Object[]{"Adobe-GB1-5", "GB1"});

        result.add(new Object[]{"Adobe-Japan1-0", "Japan1"});
        result.add(new Object[]{"Adobe-Japan1-1", "Japan1"});
        result.add(new Object[]{"Adobe-Japan1-2", "Japan1"});
        result.add(new Object[]{"Adobe-Japan1-3", "Japan1"});
        result.add(new Object[]{"Adobe-Japan1-4", "Japan1"});
        result.add(new Object[]{"Adobe-Japan1-5", "Japan1"});
        result.add(new Object[]{"Adobe-Japan1-6", "Japan1"});
        result.add(new Object[]{"Adobe-Japan1-7", "Japan1"});

        result.add(new Object[]{"Adobe-Korea1-0", "Korea1"});
        result.add(new Object[]{"Adobe-Korea1-1", "Korea1"});
        result.add(new Object[]{"Adobe-Korea1-2", "Korea1"});

        result.add(new Object[]{"Adobe-KR-0", "KR"});
        result.add(new Object[]{"Adobe-KR-1", "KR"});
        result.add(new Object[]{"Adobe-KR-2", "KR"});
        result.add(new Object[]{"Adobe-KR-3", "KR"});
        result.add(new Object[]{"Adobe-KR-4", "KR"});
        result.add(new Object[]{"Adobe-KR-5", "KR"});
        result.add(new Object[]{"Adobe-KR-6", "KR"});
        result.add(new Object[]{"Adobe-KR-7", "KR"});
        result.add(new Object[]{"Adobe-KR-8", "KR"});
        result.add(new Object[]{"Adobe-KR-9", "KR"});

        result.add(new Object[]{"B5-H", "CNS1"});
        result.add(new Object[]{"B5-V", "CNS1"});
        result.add(new Object[]{"B5pc-H", "CNS1"});
        result.add(new Object[]{"B5pc-V", "CNS1"});
        result.add(new Object[]{"CNS1-H", "CNS1"});
        result.add(new Object[]{"CNS1-V", "CNS1"});
        result.add(new Object[]{"CNS2-H", "CNS1"});
        result.add(new Object[]{"CNS2-V", "CNS1"});
        result.add(new Object[]{"CNS-EUC-H", "CNS1"});
        result.add(new Object[]{"CNS-EUC-V", "CNS1"});
        result.add(new Object[]{"ETen-B5-H", "CNS1"});
        result.add(new Object[]{"ETen-B5-V", "CNS1"});
        result.add(new Object[]{"ETenms-B5-H", "CNS1"});
        result.add(new Object[]{"ETenms-B5-V", "CNS1"});
        result.add(new Object[]{"ETHK-B5-H", "CNS1"});
        result.add(new Object[]{"ETHK-B5-V", "CNS1"});

        result.add(new Object[]{"EUC-H", "Japan1"});
        result.add(new Object[]{"EUC-V", "Japan1"});
        result.add(new Object[]{"Ext-H", "Japan1"});
        result.add(new Object[]{"Ext-RKSJ-H", "Japan1"});
        result.add(new Object[]{"Ext-RKSJ-V", "Japan1"});
        result.add(new Object[]{"Ext-V", "Japan1"});

        result.add(new Object[]{"GB-EUC-H", "GB1"});
        result.add(new Object[]{"GB-EUC-V", "GB1"});
        result.add(new Object[]{"GBK2K-H", "GB1"});
        result.add(new Object[]{"GBK2K-V", "GB1"});
        result.add(new Object[]{"GBK-EUC-H", "GB1"});
        result.add(new Object[]{"GBK-EUC-V", "GB1"});
        result.add(new Object[]{"GBKp-EUC-H", "GB1"});
        result.add(new Object[]{"GBKp-EUC-V", "GB1"});
        result.add(new Object[]{"GBpc-EUC-H", "GB1"});
        result.add(new Object[]{"GBpc-EUC-V", "GB1"});
        result.add(new Object[]{"GBT-EUC-H", "GB1"});
        result.add(new Object[]{"GBT-EUC-V", "GB1"});
        result.add(new Object[]{"GBT-H", "GB1"});
        result.add(new Object[]{"GBT-V", "GB1"});
        result.add(new Object[]{"GBTpc-EUC-H", "GB1"});
        result.add(new Object[]{"GBTpc-EUC-V", "GB1"});

        result.add(new Object[]{"H", "Japan1"});
        result.add(new Object[]{"Hankaku", "Japan1"});
        result.add(new Object[]{"Hiragana", "Japan1"});

        result.add(new Object[]{"HKdla-B5-H", "CNS1"});
        result.add(new Object[]{"HKdla-B5-V", "CNS1"});
        result.add(new Object[]{"HKdlb-B5-H", "CNS1"});
        result.add(new Object[]{"HKdlb-B5-V", "CNS1"});
        result.add(new Object[]{"HKgccs-B5-H", "CNS1"});
        result.add(new Object[]{"HKgccs-B5-V", "CNS1"});
        result.add(new Object[]{"HKm314-B5-H", "CNS1"});
        result.add(new Object[]{"HKm314-B5-V", "CNS1"});
        result.add(new Object[]{"HKm471-B5-H", "CNS1"});
        result.add(new Object[]{"HKm471-B5-V", "CNS1"});
        result.add(new Object[]{"HKscs-B5-H", "CNS1"});
        result.add(new Object[]{"HKscs-B5-V", "CNS1"});

        result.add(new Object[]{"Identity-H", "Identity"});
        result.add(new Object[]{"Identity-V", "Identity"});

        result.add(new Object[]{"Katakana", "Japan1"});

        result.add(new Object[]{"KSC-EUC-H", "Korea1"});
        result.add(new Object[]{"KSC-EUC-V", "Korea1"});
        result.add(new Object[]{"KSC-H", "Korea1"});
        result.add(new Object[]{"KSC-Johab-H", "Korea1"});
        result.add(new Object[]{"KSC-Johab-V", "Korea1"});
        result.add(new Object[]{"KSC-V", "Korea1"});
        result.add(new Object[]{"KSCms-UHC-H", "Korea1"});
        result.add(new Object[]{"KSCms-UHC-HW-H", "Korea1"});
        result.add(new Object[]{"KSCms-UHC-HW-V", "Korea1"});
        result.add(new Object[]{"KSCms-UHC-V", "Korea1"});
        result.add(new Object[]{"KSCpc-EUC-H", "Korea1"});
        result.add(new Object[]{"KSCpc-EUC-V", "Korea1"});

        result.add(new Object[]{"NWP-H", "Japan1"});
        result.add(new Object[]{"NWP-V", "Japan1"});
        result.add(new Object[]{"RKSJ-H", "Japan1"});
        result.add(new Object[]{"RKSJ-V", "Japan1"});
        result.add(new Object[]{"Roman", "Japan1"});

        result.add(new Object[]{"UniAKR-UTF8-H", "KR"});
        result.add(new Object[]{"UniAKR-UTF16-H", "KR"});
        result.add(new Object[]{"UniAKR-UTF32-H", "KR"});

        result.add(new Object[]{"UniCNS-UCS2-H", "CNS1"});
        result.add(new Object[]{"UniCNS-UCS2-V", "CNS1"});
        result.add(new Object[]{"UniCNS-UTF8-H", "CNS1"});
        result.add(new Object[]{"UniCNS-UTF8-V", "CNS1"});
        result.add(new Object[]{"UniCNS-UTF16-H", "CNS1"});
        result.add(new Object[]{"UniCNS-UTF16-V", "CNS1"});
        result.add(new Object[]{"UniCNS-UTF32-H", "CNS1"});
        result.add(new Object[]{"UniCNS-UTF32-V", "CNS1"});

        result.add(new Object[]{"UniGB-UCS2-H", "GB1"});
        result.add(new Object[]{"UniGB-UCS2-V", "GB1"});
        result.add(new Object[]{"UniGB-UTF8-H", "GB1"});
        result.add(new Object[]{"UniGB-UTF8-V", "GB1"});
        result.add(new Object[]{"UniGB-UTF16-H", "GB1"});
        result.add(new Object[]{"UniGB-UTF16-V", "GB1"});
        result.add(new Object[]{"UniGB-UTF32-H", "GB1"});
        result.add(new Object[]{"UniGB-UTF32-V", "GB1"});

        result.add(new Object[]{"UniJIS2004-UTF8-H", "Japan1"});
        result.add(new Object[]{"UniJIS2004-UTF8-V", "Japan1"});
        result.add(new Object[]{"UniJIS2004-UTF16-H", "Japan1"});
        result.add(new Object[]{"UniJIS2004-UTF16-V", "Japan1"});
        result.add(new Object[]{"UniJIS2004-UTF32-H", "Japan1"});
        result.add(new Object[]{"UniJIS2004-UTF32-V", "Japan1"});
        result.add(new Object[]{"UniJIS-UCS2-H", "Japan1"});
        result.add(new Object[]{"UniJIS-UCS2-HW-H", "Japan1"});
        result.add(new Object[]{"UniJIS-UCS2-HW-V", "Japan1"});
        result.add(new Object[]{"UniJIS-UCS2-V", "Japan1"});
        result.add(new Object[]{"UniJIS-UTF8-H", "Japan1"});
        result.add(new Object[]{"UniJIS-UTF8-V", "Japan1"});
        result.add(new Object[]{"UniJIS-UTF16-H", "Japan1"});
        result.add(new Object[]{"UniJIS-UTF16-V", "Japan1"});
        result.add(new Object[]{"UniJIS-UTF32-H", "Japan1"});
        result.add(new Object[]{"UniJIS-UTF32-V", "Japan1"});
        result.add(new Object[]{"UniJISPro-UCS2-HW-V", "Japan1"});
        result.add(new Object[]{"UniJISPro-UCS2-V", "Japan1"});
        result.add(new Object[]{"UniJISPro-UTF8-V", "Japan1"});
        result.add(new Object[]{"UniJISX0213-UTF32-H", "Japan1"});
        result.add(new Object[]{"UniJISX0213-UTF32-V", "Japan1"});
        result.add(new Object[]{"UniJISX02132004-UTF32-H", "Japan1"});
        result.add(new Object[]{"UniJISX02132004-UTF32-V", "Japan1"});

        result.add(new Object[]{"UniKS-UCS2-H", "Korea1"});
        result.add(new Object[]{"UniKS-UCS2-V", "Korea1"});
        result.add(new Object[]{"UniKS-UTF8-H", "Korea1"});
        result.add(new Object[]{"UniKS-UTF8-V", "Korea1"});
        result.add(new Object[]{"UniKS-UTF16-H", "Korea1"});
        result.add(new Object[]{"UniKS-UTF16-V", "Korea1"});
        result.add(new Object[]{"UniKS-UTF32-H", "Korea1"});
        result.add(new Object[]{"UniKS-UTF32-V", "Korea1"});

        result.add(new Object[]{"V", "Japan1"});
        result.add(new Object[]{"WP-Symbol", "Japan1"});

        result.add(new Object[]{ResourceTestUtil.normalizeResourceName("toUnicode/Adobe-CNS1-UCS2"), "Adobe_CNS1_UCS2"});
        result.add(new Object[]{ResourceTestUtil.normalizeResourceName("toUnicode/Adobe-GB1-UCS2"), "Adobe_GB1_UCS2"});
        result.add(new Object[]{ResourceTestUtil.normalizeResourceName("toUnicode/Adobe-Japan1-UCS2"), "Adobe_Japan1_UCS2"});
        result.add(new Object[]{ResourceTestUtil.normalizeResourceName("toUnicode/Adobe-Korea1-UCS2"), "Adobe_Korea1_UCS2"});
        result.add(new Object[]{ResourceTestUtil.normalizeResourceName("toUnicode/Adobe-KR-UCS2"), "Adobe_KR_UCS2"});

        return result;
    }

    private void checkFontAsianCmap(String cmapName, String ordering) {
        AbstractCMap cmap = CjkResourceLoader.getUni2CidCmap(cmapName);
        Assertions.assertTrue(cmapName.endsWith(cmap.getName()));
        Assertions.assertEquals(ordering, cmap.getOrdering());
    }


}
