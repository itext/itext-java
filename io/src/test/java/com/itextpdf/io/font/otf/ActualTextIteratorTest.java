/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;

@Category(UnitTest.class)
public class ActualTextIteratorTest extends ExtendedITextTest {

    @Test
    public void testActualTestParts() {
        Glyph glyph = new Glyph(200, 200, '\u002d');
        GlyphLine glyphLine = new GlyphLine(Arrays.asList(glyph));
        glyphLine.setActualText(0, 1, "\u002d");
        ActualTextIterator actualTextIterator = new ActualTextIterator(glyphLine);
        GlyphLine.GlyphLinePart part = actualTextIterator.next();
        // When actual text is the same as the result by text extraction, we should omit redundant actual text in the content stream
        Assert.assertNull(part.actualText);
    }

}
