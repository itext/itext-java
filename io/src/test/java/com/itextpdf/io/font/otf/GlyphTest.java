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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GlyphTest extends ExtendedITextTest {

    @Test
    public void hasPlacementIfAnchorDeltaNonZeroTest() {
        Glyph glyph = createDummyGlyph();

        Assert.assertEquals(0, glyph.getXPlacement());
        Assert.assertEquals(0, glyph.getYPlacement());
        Assert.assertEquals(0, glyph.getAnchorDelta());
        Assert.assertFalse(glyph.hasPlacement());

        glyph.setAnchorDelta((short) 10);

        Assert.assertTrue(glyph.hasPlacement());
    }

    @Test
    public void hasOffsetsIfAnchorDeltaNonZeroTest() {
        Glyph glyph = createDummyGlyph();

        Assert.assertEquals(0, glyph.getXPlacement());
        Assert.assertEquals(0, glyph.getYPlacement());
        Assert.assertEquals(0, glyph.getAnchorDelta());
        Assert.assertFalse(glyph.hasOffsets());

        glyph.setAnchorDelta((short) 10);

        Assert.assertTrue(glyph.hasOffsets());
    }

    private static Glyph createDummyGlyph() {
        return new Glyph(0, 0, 0);
    }
}
