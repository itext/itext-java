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

import com.itextpdf.io.font.constants.FontStyles;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class FontProgramFactoryTest extends ExtendedITextTest {

    @Test
    public void createRegisteredFontTest() throws IOException {
        Assertions.assertNull(FontProgramFactory.createRegisteredFont(null, FontStyles.NORMAL));
        Assertions.assertNotNull(FontProgramFactory.createRegisteredFont("helvetica", FontStyles.UNDEFINED));
        Assertions.assertNotNull(FontProgramFactory.createRegisteredFont("helvetica", FontStyles.BOLD));
        Assertions.assertNotNull(FontProgramFactory.createRegisteredFont("helvetica", FontStyles.ITALIC));
    }

    @Test
    public void registerFontFamilyTest() throws IOException {
        FontProgramFactory.registerFontFamily("somefont", "somefont", null);
        Assertions.assertNull(FontProgramFactory.createRegisteredFont("somefont", FontStyles.UNDEFINED));

        FontProgramFactory.registerFontFamily("somefont", "somefont regular", null);
        Assertions.assertNull(FontProgramFactory.createRegisteredFont("somefont", FontStyles.UNDEFINED));

    }

}
