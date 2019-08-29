/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout;

import com.itextpdf.layout.hyphenation.Hyphenation;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
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
            Assert.assertArrayEquals(expectedHyphenatePoints, result.getHyphenationPoints());
        } else {
            Assert.assertNull(expectedHyphenatePoints);
        }
    }
}
