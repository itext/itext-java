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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class OpenTypeParserTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/OpenTypeParserTest/";

    @Test
    public void tryToReadFontSubsetWithoutGlyfTableTest() throws IOException {
        byte[] fontBytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "subsetWithoutGlyfTable.ttf"));
        OpenTypeParser parser = new OpenTypeParser(fontBytes);
        Set<Integer> usedGlyphs = new HashSet<Integer>();
        // these GIDs correspond to ABC
        usedGlyphs.add(36);
        usedGlyphs.add(37);
        usedGlyphs.add(38);

        Exception e = Assertions.assertThrows(com.itextpdf.io.exceptions.IOException.class, () ->
                parser.getSubset(usedGlyphs, true));
        String exp = MessageFormatUtil.format(IoExceptionMessageConstant.TABLE_DOES_NOT_EXISTS_IN, "glyf", null);
        Assertions.assertEquals(exp, e.getMessage());
    }
}
