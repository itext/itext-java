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

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("UnitTest")
public class OpenTypeFontTableReaderTest extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/OpenTypeFontTableReaderTest/";

    private static final String FONTS_FOLDER = "./src/test/resources/com/itextpdf/io/font/sharedFontsResourceFiles/";



    private static final String CJK_JP_BOLD_PATH = FONTS_FOLDER + "NotoSansCJKjp-Bold.otf";

    private static final String SANS_MYANMAR_REGULAR_PATH = RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf";


    @Test
    public void testFetchLangSysByTag() throws IOException {

        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(SANS_MYANMAR_REGULAR_PATH);

        GlyphSubstitutionTableReader gsub = fontProgram.getGsubTable();
        ScriptRecord mym2 = gsub.getScriptRecords().get(0);

        Assertions.assertEquals("mym2", mym2.getTag());
        // default LangSys has no tag
        Assertions.assertEquals("", gsub.getLanguageRecord("mym2").getTag());
        Assertions.assertEquals(LanguageTags.SGAW_KAREN, gsub.getLanguageRecord("mym2", LanguageTags.SGAW_KAREN).getTag());
        Assertions.assertEquals(LanguageTags.MON, gsub.getLanguageRecord("mym2", LanguageTags.MON).getTag());

        Assertions.assertNull(gsub.getLanguageRecord(null));
        Assertions.assertNull(gsub.getLanguageRecord("mym3"));
        Assertions.assertNull(gsub.getLanguageRecord("mym3", LanguageTags.SGAW_KAREN));
    }


    @Test
    public void testGetLookupsArray() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(SANS_MYANMAR_REGULAR_PATH);
        GlyphSubstitutionTableReader gsub = fontProgram.getGsubTable();
        FeatureRecord firstRecord = new FeatureRecord();
        firstRecord.setLookups(new int[]{5, 2});
        firstRecord.setTag("1");
        FeatureRecord secondRecord = new FeatureRecord();
        secondRecord.setLookups(new int[]{4, 10});
        secondRecord.setTag("2");
        FeatureRecord[] records = new FeatureRecord[]{firstRecord, secondRecord};

        int[] lookupsLocations = gsub.getLookups(firstRecord).stream().mapToInt(record -> record.subTableLocations[0]).toArray();
        int[] expected = new int[]{142610, 142436};
        Assertions.assertArrayEquals(expected, lookupsLocations);

        lookupsLocations = gsub.getLookups(records).stream().mapToInt(record -> record.subTableLocations[0]).toArray();
        expected = new int[]{142436, 142538, 142610, 143908};
        Assertions.assertArrayEquals(expected, lookupsLocations);
    }

    @Test
    public void getNegativeIdxTest() throws IOException {
        GlyphPositioningTableReader gposTableReader = getGPosTableReader(SANS_MYANMAR_REGULAR_PATH);
        GposLookupType1 lookup = (GposLookupType1) gposTableReader.getLookupTable(-1);
        Assertions.assertNull(lookup);
    }

    @Test
    public void getFeatureRecordsTest() throws IOException {
        GlyphPositioningTableReader gposTableReader = getGPosTableReader(SANS_MYANMAR_REGULAR_PATH);
        List<FeatureRecord> lookup = gposTableReader.getFeatureRecords();
        Assertions.assertEquals(3, lookup.size());
    }

    @Test
    public void getFeaturesNullTest() throws IOException {
        GlyphPositioningTableReader gposTableReader = getGPosTableReader(SANS_MYANMAR_REGULAR_PATH);
        String[] scripts = new String[0];
        List<FeatureRecord> lookup = gposTableReader.getFeatures(scripts, "null");
        Assertions.assertNull(lookup);
    }

    @Test
    public void getRequiredFeaturesNullTest() throws IOException {
        GlyphPositioningTableReader gposTableReader = getGPosTableReader(SANS_MYANMAR_REGULAR_PATH);
        String[] scripts = new String[1];
        scripts[0] = "test";
        FeatureRecord requiredFeature = gposTableReader.getRequiredFeature(scripts, "null");
        Assertions.assertNull(requiredFeature);
    }

    @Test
    public void defaultLangTest() throws IOException {
        GlyphPositioningTableReader gposTableReader = getGPosTableReader(CJK_JP_BOLD_PATH);
        String[] scripts = new String[7];
        scripts[0] = "DFLT";

        List<FeatureRecord> featureRecords = gposTableReader.getFeatures(scripts, "");

        Assertions.assertEquals(8, featureRecords.size());
    }

    @Test
    public void nullStringArrayScriptsTest() throws IOException {
        GlyphPositioningTableReader gposTableReader = getGPosTableReader(CJK_JP_BOLD_PATH);
        String[] scripts = new String[7];
        List<FeatureRecord> featureRecords = gposTableReader.getFeatures(scripts, "");

        Assertions.assertEquals(8, featureRecords.size());
    }

    @Test
    public void nonDefTest() throws IOException {
        GlyphPositioningTableReader gposTableReader = getGPosTableReader(SANS_MYANMAR_REGULAR_PATH);
        String[] scripts = new String[7];
        scripts[2] = "DFLT";
        List<FeatureRecord> featureRecords = gposTableReader.getFeatures(scripts, "");

        Assertions.assertEquals(3, featureRecords.size());
    }

    @Test
    public void testFetchLangSysByTag2() throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(SANS_MYANMAR_REGULAR_PATH);
        GlyphSubstitutionTableReader gsub = fontProgram.getGsubTable();
        Assertions.assertNull(gsub.getLanguageRecord("mym2", LanguageTags.ARABIC));
    }

    @Test
    public void specificEqualsNullTest() throws IOException {
        GlyphPositioningTableReader gPosTableReader = getGPosTableReader(CJK_JP_BOLD_PATH);
        List<FeatureRecord> test = new ArrayList<>();
        test.add(new FeatureRecord());
        List<FeatureRecord> specificFeatures = gPosTableReader.getSpecificFeatures(test, null);
        Assertions.assertEquals(test, specificFeatures);
    }

    @Test
    public void specificFeaturesTest() throws IOException {
        GlyphPositioningTableReader gPosTableReader = getGPosTableReader(CJK_JP_BOLD_PATH);
        String[] specific = new String[1];
        List<FeatureRecord> test = new ArrayList<>();
        test.add(new FeatureRecord());
        List<FeatureRecord> specificFeatures = gPosTableReader.getSpecificFeatures(test, specific);
        Assertions.assertEquals(test, specificFeatures);
    }

    private GlyphPositioningTableReader getGPosTableReader(String fontPath) throws IOException {
        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(fontPath);
        return fontProgram.getGposTable();
    }
}
