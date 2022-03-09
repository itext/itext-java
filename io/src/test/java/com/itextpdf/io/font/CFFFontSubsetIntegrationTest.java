/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CFFFontSubsetIntegrationTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/CFFFontSubsetIntegrationTest/";
    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/io/font/sharedFontsResourceFiles/";

    private static final String CJK_JP_BOLD_PATH = FONTS_FOLDER + "NotoSansCJKjp-Bold.otf";
    private static final int CJK_JP_BOLD_CFF_OFFSET = 259880;
    private static final int CJK_JP_BOLD_CFF_LENGTH = 16023217;

    private static final String JP_REGULAR_PATH = FONTS_FOLDER + "NotoSansJP-Regular_charsetDataFormat0.otf";
    private static final int JP_REGULAR_CFF_OFFSET = 337316;
    private static final int JP_REGULAR_CFF_LENGTH = 4210891;

    @Test
    public void subsetNotoSansCjkJpBoldNoUsedGlyphsTest() throws IOException {
        String cmpCff = SOURCE_FOLDER + "subsetNotoSansCJKjpBoldNoUsedGlyphs.cff";

        Set<Integer> glyphsUsed = Collections.<Integer>emptySet();

        byte[] cffSubsetBytes =
                subsetNotoSansCjkJpBoldCff(CJK_JP_BOLD_PATH, CJK_JP_BOLD_CFF_OFFSET, CJK_JP_BOLD_CFF_LENGTH, glyphsUsed);

        int expectedSubsetLength = 279337;
        Assert.assertEquals(expectedSubsetLength, cffSubsetBytes.length);

        byte[] cmpBytes = Files.readAllBytes(Paths.get(cmpCff));
        Assert.assertArrayEquals(cmpBytes, cffSubsetBytes);
    }

    @Test
    public void subsetNotoSansCjkJpBoldTwoUsedGlyphsTest() throws IOException {
        String cmpCff = SOURCE_FOLDER + "subsetNotoSansCJKjpBoldTwoUsedGlyphs.cff";

        // In this case cid == gid for given characters.
        // \u20eab "𠺫"
        int glyphCid1 = 59715;
        // \uff14 "４"
        int glyphCid2 = 59066;
        HashSet<Integer> glyphsUsed = new HashSet<>(Arrays.asList(glyphCid1, glyphCid2));

        byte[] cffSubsetBytes =
                subsetNotoSansCjkJpBoldCff(CJK_JP_BOLD_PATH, CJK_JP_BOLD_CFF_OFFSET, CJK_JP_BOLD_CFF_LENGTH, glyphsUsed);

        int expectedSubsetLength = 365381;
        Assert.assertEquals(expectedSubsetLength, cffSubsetBytes.length);

        byte[] cmpBytes = Files.readAllBytes(Paths.get(cmpCff));
        Assert.assertArrayEquals(cmpBytes, cffSubsetBytes);
    }

    @Test
    public void subsetNotoSansJpRegularOneUsedGlyphTest() throws IOException {
        // In this case cid != gid for given characters.
        // \u4FE1 "信"; gid: 0x0a72 / 2674
        int glyphGid1 = 2674;
        HashSet<Integer> glyphsUsed = new HashSet<>(Collections.singletonList(glyphGid1));

        byte[] cffSubsetBytes =
                subsetNotoSansCjkJpBoldCff(JP_REGULAR_PATH, JP_REGULAR_CFF_OFFSET, JP_REGULAR_CFF_LENGTH, glyphsUsed);

        int expectedSubsetLength = 121796;
        Assert.assertEquals(expectedSubsetLength, cffSubsetBytes.length);

        byte[] cmpBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subsetNotoSansJPRegularOneUsedGlyph.cff"));
        Assert.assertArrayEquals(cmpBytes, cffSubsetBytes);
    }

    private byte[] subsetNotoSansCjkJpBoldCff(String otfFile, int offsetToCff, int cffLength,
            Set<Integer> glyphsUsed) throws IOException {
        RandomAccessFileOrArray fontRaf = null;
        try {
            fontRaf = new RandomAccessFileOrArray(new RandomAccessSourceFactory()
                    .createBestSource(otfFile));

            byte[] cff = new byte[cffLength];
            try {
                fontRaf.seek(offsetToCff);
                fontRaf.readFully(cff);
            } finally {
                fontRaf.close();
            }

            CFFFontSubset cffFontSubset = new CFFFontSubset(cff, glyphsUsed);
            return cffFontSubset.Process();
        } finally {
            if (fontRaf != null) {
                fontRaf.close();
            }
        }
    }
}
