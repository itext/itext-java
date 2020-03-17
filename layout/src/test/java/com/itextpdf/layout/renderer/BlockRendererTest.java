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
