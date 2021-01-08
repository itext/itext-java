/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTest.class)
public class OpenTypeFontTableReaderTest extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/OpenTypeFontTableReaderTest/";

    @Test
    public void testFetchLangSysByTag() throws IOException {

        TrueTypeFont fontProgram = (TrueTypeFont) FontProgramFactory.createFont(RESOURCE_FOLDER + "NotoSansMyanmar-Regular.ttf");

        GlyphSubstitutionTableReader gsub = fontProgram.getGsubTable();
        ScriptRecord mym2 = gsub.getScriptRecords().get(0);

        Assert.assertEquals("mym2", mym2.tag);
        // default LangSys has no tag
        Assert.assertEquals("", gsub.getLanguageRecord("mym2").tag);
        Assert.assertEquals(LanguageTags.SGAW_KAREN, gsub.getLanguageRecord("mym2", LanguageTags.SGAW_KAREN).tag);
        Assert.assertEquals(LanguageTags.MON, gsub.getLanguageRecord("mym2", LanguageTags.MON).tag);

        Assert.assertNull(gsub.getLanguageRecord(null));
        Assert.assertNull(gsub.getLanguageRecord("mym3"));
        Assert.assertNull(gsub.getLanguageRecord("mym3", LanguageTags.SGAW_KAREN));
    }
}
