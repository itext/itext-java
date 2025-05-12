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
package com.itextpdf.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class StringNormalizerTest {

    @Test
    public void toLowerCaseTest() {
        Assertions.assertNull(StringNormalizer.toLowerCase(null));
        Assertions.assertEquals("some string", StringNormalizer.toLowerCase("SoMe StRiNg"));
        Assertions.assertEquals("some string", StringNormalizer.toLowerCase("SOME STRING"));
    }

    @Test
    public void toUpperCaseTest() {
        Assertions.assertNull(StringNormalizer.toUpperCase(null));
        Assertions.assertEquals("SOME STRING", StringNormalizer.toUpperCase("SoMe StRiNg"));
        Assertions.assertEquals("SOME STRING", StringNormalizer.toUpperCase("some string"));
    }

    @Test
    public void normalizeTest() {
        Assertions.assertNull(StringNormalizer.normalize(null));
        Assertions.assertEquals("some   string", StringNormalizer.normalize(" \t\nSoMe   StRiNg  "));
        Assertions.assertEquals("some   string", StringNormalizer.normalize(" \t\nSOME   STRING  "));
    }
}
