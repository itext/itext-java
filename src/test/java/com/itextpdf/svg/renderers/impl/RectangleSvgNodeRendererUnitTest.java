package com.itextpdf.svg.renderers.impl;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class RectangleSvgNodeRendererUnitTest {

    private static final float EPSILON = 0.00001f;
    RectangleSvgNodeRenderer renderer;

    @Before
    public void setup() {
        renderer = new RectangleSvgNodeRenderer();
    }

    @Test
    public void checkRadiusTest() {
        float rad = renderer.checkRadius(0f, 20f);
        Assert.assertEquals(0f, rad, EPSILON);
    }

    @Test
    public void checkRadiusNegativeTest() {
        float rad = renderer.checkRadius(-1f, 20f);
        Assert.assertEquals(0f, rad, EPSILON);
    }

    @Test
    public void checkRadiusTooLargeTest() {
        float rad = renderer.checkRadius(30f, 20f);
        Assert.assertEquals(10f, rad, EPSILON);
    }

    @Test
    public void checkRadiusTooLargeNegativeTest() {
        float rad = renderer.checkRadius(-100f, 20f);
        Assert.assertEquals(0f, rad, EPSILON);
    }

    @Test
    public void checkRadiusHalfLengthTest() {
        float rad = renderer.checkRadius(10f, 20f);
        Assert.assertEquals(10f, rad, EPSILON);
    }

    @Test
    public void findCircularRadiusTest() {
        float rad = renderer.findCircularRadius(0f, 20f, 100f, 200f);
        Assert.assertEquals(20f, rad, EPSILON);
    }

    @Test
    public void findCircularRadiusHalfLengthTest() {
        float rad = renderer.findCircularRadius(0f, 200f, 100f, 200f);
        Assert.assertEquals(50f, rad, EPSILON);
    }

    @Test
    public void findCircularRadiusSmallWidthTest() {
        float rad = renderer.findCircularRadius(0f, 20f, 5f, 200f);
        Assert.assertEquals(2.5f, rad, EPSILON);
    }
}
