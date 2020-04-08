/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class BlockRendererTest extends ExtendedITextTest {

    @Test
    public void applyMinHeightForSpecificDimensionsCausingFloatPrecisionError () {
        float divHeight = 42.55f;

        Div div = new Div();
        div.setHeight(UnitValue.createPointValue(divHeight));

        float occupiedHeight = 17.981995f;
        float leftHeight = 24.567993f;

        Assert.assertTrue(occupiedHeight + leftHeight < divHeight);

        BlockRenderer blockRenderer = (BlockRenderer) div.createRendererSubTree();
        blockRenderer.occupiedArea = new LayoutArea(1, new Rectangle(0, 267.9681f, 0, occupiedHeight));
        AbstractRenderer renderer = blockRenderer.applyMinHeight(OverflowPropertyValue.FIT,
                new Rectangle(0, 243.40012f, 0, leftHeight));
        Assert.assertNull(renderer);
    }
}
