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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.layout.renderer.LineRenderer.RendererGlyph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class TypographyUtilsTest extends ExtendedITextTest {

    @Test
    public void verifyPdfCalligraphIsNotAvailable() {
        Assert.assertFalse(TypographyUtils.isPdfCalligraphAvailable());
    }

    @Test
    public void updateAnchorDeltaMarkNotReorderedTest() {
        // original line 'abm', and 'm' is a mark based on 'b'
        Glyph mGlyph = new Glyph(100, 'm');
        mGlyph.setAnchorDelta((short) -1);
        mGlyph.setXAdvance((short) 15);
        mGlyph.setYAdvance((short) 25);

        RendererGlyph b = new RendererGlyph(new Glyph(100, 'b'), null);
        RendererGlyph m = new RendererGlyph(mGlyph, null);
        RendererGlyph a = new RendererGlyph(new Glyph(100, 'a'), null);
        List<RendererGlyph> reorderedLine = Arrays.asList(b, m, a);

        int[] reorder = new int[] {1, 2, 0};
        int[] inverseReorder = new int[] {2, 0, 1};

        TypographyUtils.updateAnchorDeltaForReorderedLineGlyphs(reorder, inverseReorder, reorderedLine);

        Assert.assertSame(mGlyph, m.glyph);
        Assert.assertEquals(-1, m.glyph.getAnchorDelta());
    }

    @Test
    public void updateAnchorDeltaMarkReorderedTest() {
        // original line 'abm', and 'm' is a mark based on 'b'
        Glyph mGlyph = new Glyph(100, 'm');
        mGlyph.setAnchorDelta((short) -1);
        mGlyph.setXAdvance((short) 15);
        mGlyph.setYAdvance((short) 25);

        RendererGlyph m = new RendererGlyph(mGlyph, null);
        RendererGlyph b = new RendererGlyph(new Glyph(100, 'b'), null);
        RendererGlyph a = new RendererGlyph(new Glyph(100, 'a'), null);
        List<RendererGlyph> reorderedLine = Arrays.asList(m, b, a);

        int[] reorder = new int[] {2, 1, 0};
        int[] inverseReorder = new int[] {2, 1, 0};

        TypographyUtils.updateAnchorDeltaForReorderedLineGlyphs(reorder, inverseReorder, reorderedLine);

        Assert.assertNotSame(mGlyph, m.glyph);
        Assert.assertEquals(1, m.glyph.getAnchorDelta());
    }

}
