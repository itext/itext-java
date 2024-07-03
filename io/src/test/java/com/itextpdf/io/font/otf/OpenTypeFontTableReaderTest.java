/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("UnitTest")
public class OpenTypeFontTableReaderTest extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/OpenTypeFontTableReaderTest/";

    @Test
    public void testFetchLangSysByTag() throws IOException {

        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");

        GlyphSubstitutionTableReader gsub = fontProgram.getGsubTable();
        ScriptRecord mym2 = gsub.getScriptRecords().get(0);

        Assertions.assertEquals("mym2", mym2.tag);
        // default LangSys has no tag
        Assertions.assertEquals("", gsub.getLanguageRecord("mym2").tag);
        Assertions.assertEquals(LanguageTags.SGAW_KAREN, gsub.getLanguageRecord("mym2", LanguageTags.SGAW_KAREN).tag);
        Assertions.assertEquals(LanguageTags.MON, gsub.getLanguageRecord("mym2", LanguageTags.MON).tag);

        Assertions.assertNull(gsub.getLanguageRecord(null));
        Assertions.assertNull(gsub.getLanguageRecord("mym3"));
        Assertions.assertNull(gsub.getLanguageRecord("mym3", LanguageTags.SGAW_KAREN));
    }


    @Test
    public void testGetLookupsArray() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");
        GlyphSubstitutionTableReader gsub = fontProgram.getGsubTable();
        FeatureRecord firstRecord = new FeatureRecord();
        firstRecord.lookups = new int[]{5, 2};
        firstRecord.tag = "1";
        FeatureRecord secondRecord = new FeatureRecord();
        secondRecord.lookups = new int[]{4, 10};
        secondRecord.tag = "2";
        FeatureRecord[] records = new FeatureRecord[]{firstRecord, secondRecord};

        int[] lookupsLocations = gsub.getLookups(firstRecord).stream().mapToInt(record -> record.subTableLocations[0]).toArray();
        int[] expected = new int[]{142610, 142436};
        Assertions.assertArrayEquals(expected, lookupsLocations);

        lookupsLocations = gsub.getLookups(records).stream().mapToInt(record -> record.subTableLocations[0]).toArray();
        expected = new int[]{142436, 142538, 142610, 143908};
        Assertions.assertArrayEquals(expected, lookupsLocations);
    }
}
