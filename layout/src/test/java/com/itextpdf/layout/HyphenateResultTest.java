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
package com.itextpdf.layout;

import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class HyphenateResultTest extends ExtendedITextTest {

    @Test
    public void ukraineHyphenTest() {
        //здравствуйте
        testHyphenateResult("uk", "\u0437\u0434\u0440\u0430\u0432\u0441\u0442\u0432\u0443\u0439", new int[]{5});
    }

    @Test
    public void ukraineNoneHyphenTest() {
        //день
        testHyphenateResult("uk", "\u0434\u0435\u043D\u044C", null);
    }

    @Test
    public void parenthesisTest01() {
        //Annuitätendarlehen
        testHyphenateResult("de", "((:::(\"|;Annuitätendarlehen|\")))", new int[]{5, 7, 10, 13, 15});
    }

    @Test
    public void hindiHyphResult() {
        //लाभहानि
        testHyphenateResult("hi", "लाभहानि", new int[]{3});
    }

    @Test
    public void spacesTest01() {
        //Annuitätendarlehen
        testHyphenateResult("de", "    Annuitätendarlehen", new int[]{5, 7, 10, 13, 15});
    }

    @Test
    public void softHyphenTest01() {
        //Ann\u00ADuit\u00ADätendarl\u00ADehen
        testHyphenateResult("de", "Ann\u00ADuit\u00ADätendarl\u00ADehen", new int[]{3, 7, 16});
    }

    @Test
    public void stackoverflowTestDe() {
        //https://stackoverflow.com/
        testHyphenateResult("de", "https://stackoverflow.com/", new int[]{3, 14, 17});
    }

    @Test
    public void stackoverflowTestEn() {
        //https://stackoverflow.com/
        testHyphenateResult("en", "https://stackoverflow.com/", new int[]{13, 17});
    }

    @Test
    public void nonBreakingHyphenTest01() {
        //99\u2011verheiratet
        testHyphenateResult("de", "999\u2011verheiratet", new int[]{3, 6, 8});
    }
    @Test
    public void nonBreakingHyphenTest02() {
        //honorificabilitudinitatibus
        testHyphenateResult("en", "honorificabilitudinitatibus", new int[] {3, 5, 6, 9, 11, 13, 15, 19, 21, 22, 24});
    }

    @Test
    public void nonBreakingHyphenTest02A() {
        //honorificabil\u2011itudinitatibus
        testHyphenateResult("en", "honorificabil\u2011itudinitatibus", new int[] {3, 5, 6, 9, 11, 20, 22, 23, 25});
    }

    @Test
    public void numberTest01() {
        //123456789
        testHyphenateResult("en", "123456789", null);
    }

    private void testHyphenateResult(String lang, String testWorld, int[] expectedHyphenatePoints) {
        String[] parts = lang.split("_");
        lang = parts[0];
        String country = (parts.length == 2) ? parts[1] : null;
        HyphenationConfig config = new HyphenationConfig(lang, country, 3, 3);
        Hyphenation result = config.hyphenate(testWorld);
        if (result != null) {
            Assertions.assertArrayEquals(expectedHyphenatePoints, result.getHyphenationPoints());
        } else {
            Assertions.assertNull(expectedHyphenatePoints);
        }
    }
}
