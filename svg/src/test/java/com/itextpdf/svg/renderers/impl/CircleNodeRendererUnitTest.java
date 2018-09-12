package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CircleNodeRendererUnitTest {

    @Test
    public void deepCopyTest(){
        CircleSvgNodeRenderer expected = new CircleSvgNodeRenderer();
        expected.setAttribute(SvgConstants.Attributes.FILL,"blue");
        ISvgNodeRenderer actual =expected.createDeepCopy();
        Assert.assertEquals(expected,actual);
    }
}
