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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class OpenTypeGposTableReaderTest extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/";

    @Test
    public void nullLookupsInGposTableTest() throws IOException {
        String fontName = "NotoSansSinhala-Regular.ttf";
        TrueTypeFont fontProgram = (TrueTypeFont)FontProgramFactory.createFont(RESOURCE_FOLDER + fontName);
        GlyphPositioningTableReader gpos = fontProgram.getGposTable();

        Assertions.assertEquals(3, gpos.lookupList.size());
        Assertions.assertNotNull(gpos.lookupList.get(0));
        Assertions.assertEquals(0, gpos.lookupList.get(0).getIndexInLookupList());
        Assertions.assertNull(gpos.lookupList.get(1));
        Assertions.assertNotNull(gpos.lookupList.get(2));
        Assertions.assertEquals(2, gpos.lookupList.get(2).getIndexInLookupList());
    }
}
