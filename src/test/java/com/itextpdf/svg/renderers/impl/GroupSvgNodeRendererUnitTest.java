package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GroupSvgNodeRendererUnitTest {

    @Ignore("RND-880, list comparison fails")
    @Test
    public void deepCopyTest(){
        GroupSvgNodeRenderer expected = new GroupSvgNodeRenderer();
        expected.setAttribute(SvgConstants.Attributes.FILL,"blue");
        expected.addChild(new CircleSvgNodeRenderer());
        ISvgNodeRenderer actual =expected.createDeepCopy();
        Assert.assertEquals(expected,actual);
    }
}
