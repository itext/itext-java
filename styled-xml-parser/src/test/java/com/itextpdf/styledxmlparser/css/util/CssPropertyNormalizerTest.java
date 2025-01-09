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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssPropertyNormalizerTest extends ExtendedITextTest {

    @Test
    public void checkUrlNormalizationSimpleTest() {
        test("url('data:image/png;base64,iVBORw0K')", "url('data:image/png;base64,iVBORw0K')");
    }

    @Test
    public void checkUrlNormalizationUppercaseTest() {
        test("URL('data:image/png;base64,iVBORw0K')", "url('data:image/png;base64,iVBORw0K')");
        test("uRL('data:image/png;base64,iVBORw0K')", "url('data:image/png;base64,iVBORw0K')");
        test("urL('data:image/png;base64,iVBORw0K')", "url('data:image/png;base64,iVBORw0K')");
    }

    @Test
    public void checkUrlNormalizationWhitespacesTest() {
        test("  url(  'data:image/png;base64,iVBORw0K' )", "url('data:image/png;base64,iVBORw0K')");
    }

    @Test
    // Test is initially added to ensure equal behavior between Java and C#.
    // The behavior itself might be reconsidered in the future. Browsers do not forgive newlines in base64 expressions
    public void checkUrlNormalizationLineTerminatorsTest() {
        test("url(data:image/png;base64,iVBOR\nw0K)", "url(data:image/png;base64,iVBOR\nw0K)");
        test("url(data:image/png;base64,iVBOR\rw0K)", "url(data:image/png;base64,iVBOR\rw0K)");
        test("url(data:image/png;base64,iVBOR\r\nw0K)", "url(data:image/png;base64,iVBOR\r\nw0K)");
    }

    private void test(String input, String expectedOutput) {
        String result = CssPropertyNormalizer.normalize(input);
        Assertions.assertEquals(expectedOutput, result);
    }
}
