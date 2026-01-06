/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class TrueTypeFontIntegrationTest extends ExtendedITextTest {
    private static final String SHARED_FOLDER = "./src/test/resources/com/itextpdf/io/font/sharedFontsResourceFiles/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/TrueTypeFontIntegrationTest/";

    @Test
    public void simpleSubsetTest() throws IOException {
        byte[] fontBytes = Files.readAllBytes(Paths.get(SHARED_FOLDER + "NotoSans-Regular.ttf"));
        TrueTypeFont font = FontProgramFactory.createTrueTypeFont(fontBytes, false);

        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // these GIDs correspond to ABC
        usedGlyphs.add(36);
        usedGlyphs.add(37);
        usedGlyphs.add(38);
        byte[] subsetFontBytes = font.getSubset(usedGlyphs, true);

        TrueTypeFont subsetFont = FontProgramFactory.createTrueTypeFont(subsetFontBytes, true);
        Assertions.assertEquals(3271, font.bBoxes.length);
        Assertions.assertEquals(39, subsetFont.bBoxes.length);
        Assertions.assertNotNull(subsetFont.bBoxes[36]);
        Assertions.assertNull(subsetFont.bBoxes[35]);
    }

    @Test
    public void simpleSubsetWithoutTableSubsetTest() throws IOException {
        byte[] fontBytes = Files.readAllBytes(Paths.get(SHARED_FOLDER + "NotoSans-Regular.ttf"));
        TrueTypeFont font = FontProgramFactory.createTrueTypeFont(fontBytes, false);

        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // these GIDs correspond to ABC
        usedGlyphs.add(36);
        usedGlyphs.add(37);
        usedGlyphs.add(38);
        byte[] subsetFontBytes = font.getSubset(usedGlyphs, false);

        TrueTypeFont subsetFont = FontProgramFactory.createTrueTypeFont(subsetFontBytes, false);
        Assertions.assertEquals(3271, font.bBoxes.length);
        Assertions.assertEquals(39, subsetFont.bBoxes.length);
        Assertions.assertNotNull(subsetFont.bBoxes[36]);
        Assertions.assertNull(subsetFont.bBoxes[35]);
    }

    @Test
    public void simpleSubsetMergeTest() throws IOException {
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset1.ttf"));
        // Subset for XBC
        TrueTypeFont subset1 = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset2.ttf"));
        // Subset for ABC
        TrueTypeFont subset2 = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        Map<TrueTypeFont, Set<Integer>> toMerge = new HashMap<TrueTypeFont, Set<Integer>>();
        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // GID correspond to B
        usedGlyphs.add(37);
        toMerge.put(subset1, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to A
        usedGlyphs.add(36);
        toMerge.put(subset2, usedGlyphs);

        byte[] mergeFontBytes = TrueTypeFont.merge(toMerge, "NotoSans-Regular", false);
        TrueTypeFont mergeFont = FontProgramFactory.createTrueTypeFont(mergeFontBytes, true);

        // C glyphs wasn't used, it's why it was cut from merge font
        Assertions.assertNotNull(subset1.bBoxes[38]);
        Assertions.assertNotNull(subset2.bBoxes[38]);
        Assertions.assertEquals(38, mergeFont.bBoxes.length);
    }

    @Test
    public void noCommonCmapPdfTrueTypeMergeTest() throws IOException {
        // subsets are created using fonttools Python lib with the following command
        // fonttools subset ./NotoSans-Regular.ttf --text="ABC" --retain-gids --layout-features='*' --notdef-glyph --output-file=subset_abc.ttf
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_abc.ttf"));
        TrueTypeFont subsetAbc = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_def.ttf"));
        TrueTypeFont subsetDef = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_xyz.ttf"));
        TrueTypeFont subsetXyz = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        Map<TrueTypeFont, Set<Integer>> toMerge = new HashMap<TrueTypeFont, Set<Integer>>();
        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // GID correspond to ABC
        usedGlyphs.add(36);
        usedGlyphs.add(37);
        usedGlyphs.add(38);
        toMerge.put(subsetAbc, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to DEF
        usedGlyphs.add(39);
        usedGlyphs.add(40);
        usedGlyphs.add(41);
        toMerge.put(subsetDef, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to XYZ
        usedGlyphs.add(59);
        usedGlyphs.add(60);
        usedGlyphs.add(61);
        toMerge.put(subsetXyz, usedGlyphs);

        Exception e = Assertions.assertThrows(
                com.itextpdf.io.exceptions.IOException.class,
                () -> TrueTypeFont.merge(toMerge, "NotoSans-Regular", true));
        Assertions.assertEquals(IoExceptionMessageConstant.CMAP_TABLE_MERGING_IS_NOT_SUPPORTED, e.getMessage());
    }

    @Test
    public void commonCmapPdfTrueTypeMergeTest() throws IOException {
        // subsets are created using fonttools Python lib with the following command
        // fonttools subset ./NotoSans-Regular.ttf --text="ABC" --retain-gids --layout-features='*' --notdef-glyph --output-file=subset_abc.ttf
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_abc.ttf"));
        TrueTypeFont subsetAbc = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_abc_def_xyz.ttf"));
        TrueTypeFont subsetAbcDefXyz = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_def.ttf"));
        TrueTypeFont subsetDef = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_xyz.ttf"));
        TrueTypeFont subsetXyz = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        Map<TrueTypeFont, Set<Integer>> toMerge = new HashMap<TrueTypeFont, Set<Integer>>();
        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // GID correspond to ABC
        usedGlyphs.add(36);
        usedGlyphs.add(37);
        usedGlyphs.add(38);
        toMerge.put(subsetAbc, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to ADX
        usedGlyphs.add(36);
        usedGlyphs.add(39);
        usedGlyphs.add(59);
        toMerge.put(subsetAbcDefXyz, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to DEF
        usedGlyphs.add(39);
        usedGlyphs.add(40);
        usedGlyphs.add(41);
        toMerge.put(subsetDef, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to XYZ
        usedGlyphs.add(59);
        usedGlyphs.add(60);
        usedGlyphs.add(61);
        toMerge.put(subsetXyz, usedGlyphs);

        byte[] mergeFontBytes = TrueTypeFont.merge(toMerge, "NotoSans-Regular", true);
        TrueTypeFont mergeFont = FontProgramFactory.createTrueTypeFont(mergeFontBytes, true);

        // `cmap` table contains mapping for all used glyphs
        Assertions.assertEquals(9, mergeFont.getActiveCmap().size());
        // `glyf` table contains data for all used glyphs
        Assertions.assertEquals(62, mergeFont.bBoxes.length);
        Assertions.assertNotNull(mergeFont.bBoxes[36]);
        Assertions.assertNotNull(mergeFont.bBoxes[39]);
        Assertions.assertNotNull(mergeFont.bBoxes[59]);
    }

    @Test
    public void noCommonCmapPdfType0MergeTest() throws IOException {
        // subsets are created using fonttools Python lib with the following command
        // fonttools subset ./NotoSans-Regular.ttf --text="ABC" --retain-gids --layout-features='*' --notdef-glyph --output-file=subset_abc.ttf
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_abc.ttf"));
        TrueTypeFont subsetAbc = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_def.ttf"));
        TrueTypeFont subsetDef = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_xyz.ttf"));
        TrueTypeFont subsetXyz = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        Map<TrueTypeFont, Set<Integer>> toMerge = new HashMap<TrueTypeFont, Set<Integer>>();
        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // GID correspond to ABC
        usedGlyphs.add(36);
        usedGlyphs.add(37);
        usedGlyphs.add(38);
        toMerge.put(subsetAbc, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to DEF
        usedGlyphs.add(39);
        usedGlyphs.add(40);
        usedGlyphs.add(41);
        toMerge.put(subsetDef, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to XYZ
        usedGlyphs.add(59);
        usedGlyphs.add(60);
        usedGlyphs.add(61);
        toMerge.put(subsetXyz, usedGlyphs);

        byte[] mergeFontBytes = TrueTypeFont.merge(toMerge, "NotoSans-Regular", false);
        TrueTypeFont mergeFont = FontProgramFactory.createTrueTypeFont(mergeFontBytes, true);

        // `cmap` table doesn't contain mapping for all used glyphs, but for PDF
        // Type0 CIDFontType2 it isn't required because of CIDToGIDMap presence
        Assertions.assertEquals(3, mergeFont.getActiveCmap().size());
        // `glyf` table contains data for all used glyphs
        Assertions.assertEquals(62, mergeFont.bBoxes.length);
        Assertions.assertNotNull(mergeFont.bBoxes[36]);
        Assertions.assertNotNull(mergeFont.bBoxes[39]);
        Assertions.assertNotNull(mergeFont.bBoxes[59]);
    }

    @Test
    public void noCommonCmapUnknownPdfTypeMergeTest() throws IOException {
        // subsets are created using fonttools Python lib with the following command
        // fonttools subset ./NotoSans-Regular.ttf --text="ABC" --retain-gids --layout-features='*' --notdef-glyph --output-file=subset_abc.ttf
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_abc.ttf"));
        TrueTypeFont subsetAbc = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_def.ttf"));
        TrueTypeFont subsetDef = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subset_xyz.ttf"));
        TrueTypeFont subsetXyz = FontProgramFactory.createTrueTypeFont(fontBytes, true);

        Map<TrueTypeFont, Set<Integer>> toMerge = new HashMap<TrueTypeFont, Set<Integer>>();
        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // GID correspond to ABC
        usedGlyphs.add(36);
        usedGlyphs.add(37);
        usedGlyphs.add(38);
        toMerge.put(subsetAbc, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to DEF
        usedGlyphs.add(39);
        usedGlyphs.add(40);
        usedGlyphs.add(41);
        toMerge.put(subsetDef, usedGlyphs);
        usedGlyphs = new HashSet<>();
        // GID correspond to XYZ
        usedGlyphs.add(59);
        usedGlyphs.add(60);
        usedGlyphs.add(61);
        toMerge.put(subsetXyz, usedGlyphs);

        Exception e = Assertions.assertThrows(
                com.itextpdf.io.exceptions.IOException.class,
                () -> TrueTypeFont.merge(toMerge, "NotoSans-Regular"));
        Assertions.assertEquals(IoExceptionMessageConstant.CMAP_TABLE_MERGING_IS_NOT_SUPPORTED, e.getMessage());
    }

    @Test
    public void tryToReadFontSubsetWithoutGlyfTableTest() throws IOException {
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subsetWithoutGlyfTable.ttf"));
        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class, () ->
            FontProgramFactory.createTrueTypeFont(fontBytes, true));
        String exp = MessageFormatUtil.format(IoExceptionMessageConstant.TABLE_DOES_NOT_EXIST, "glyf");
        Assertions.assertEquals(exp, e.getMessage());
    }

    @Test
    public void readFontSubsetWithoutOs2TableTest() throws IOException {
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subsetWithoutOsTable.ttf"));
        AssertUtil.doesNotThrow(() -> FontProgramFactory.createTrueTypeFont(fontBytes, true));
    }

    @Test
    public void tryToReadFontSubsetWithoutOs2TableTest() throws IOException {
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subsetWithoutOsTable.ttf"));

        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class, () ->
                FontProgramFactory.createTrueTypeFont(fontBytes, false));
        String exp = MessageFormatUtil.format(IoExceptionMessageConstant.TABLE_DOES_NOT_EXIST, "os/2");
        Assertions.assertEquals(exp, e.getMessage());
    }
}
