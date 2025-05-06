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
package com.itextpdf.io.font.constants;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class FontWeightsTest extends ExtendedITextTest {

    @Test
    public void fromType1FontWeightTest() {
        Assertions.assertEquals(FontWeights.THIN, FontWeights.fromType1FontWeight("ultralight"));
        Assertions.assertEquals(FontWeights.EXTRA_LIGHT, FontWeights.fromType1FontWeight("thin"));
        Assertions.assertEquals(FontWeights.EXTRA_LIGHT, FontWeights.fromType1FontWeight("extralight"));
        Assertions.assertEquals(FontWeights.LIGHT, FontWeights.fromType1FontWeight("light"));
        Assertions.assertEquals(FontWeights.NORMAL, FontWeights.fromType1FontWeight("book"));
        Assertions.assertEquals(FontWeights.NORMAL, FontWeights.fromType1FontWeight("regular"));
        Assertions.assertEquals(FontWeights.NORMAL, FontWeights.fromType1FontWeight("normal"));
        Assertions.assertEquals(FontWeights.MEDIUM, FontWeights.fromType1FontWeight("medium"));
        Assertions.assertEquals(FontWeights.SEMI_BOLD, FontWeights.fromType1FontWeight("demibold"));
        Assertions.assertEquals(FontWeights.SEMI_BOLD, FontWeights.fromType1FontWeight("semibold"));
        Assertions.assertEquals(FontWeights.BOLD, FontWeights.fromType1FontWeight("bold"));
        Assertions.assertEquals(FontWeights.EXTRA_BOLD, FontWeights.fromType1FontWeight("extrabold"));
        Assertions.assertEquals(FontWeights.EXTRA_BOLD, FontWeights.fromType1FontWeight("ultrabold"));
        Assertions.assertEquals(FontWeights.BLACK, FontWeights.fromType1FontWeight("heavy"));
        Assertions.assertEquals(FontWeights.BLACK, FontWeights.fromType1FontWeight("black"));
        Assertions.assertEquals(FontWeights.BLACK, FontWeights.fromType1FontWeight("ultra"));
        Assertions.assertEquals(FontWeights.BLACK, FontWeights.fromType1FontWeight("ultrablack"));
        Assertions.assertEquals(FontWeights.BLACK, FontWeights.fromType1FontWeight("fat"));
        Assertions.assertEquals(FontWeights.BLACK, FontWeights.fromType1FontWeight("extrablack"));
    }

    @Test
    public void normalizeFontWeightTest() {
        Assertions.assertEquals(FontWeights.THIN, FontWeights.normalizeFontWeight(99));
        Assertions.assertEquals(FontWeights.BLACK, FontWeights.normalizeFontWeight(1000));
        Assertions.assertEquals(FontWeights.MEDIUM, FontWeights.normalizeFontWeight(505));
    }
}
