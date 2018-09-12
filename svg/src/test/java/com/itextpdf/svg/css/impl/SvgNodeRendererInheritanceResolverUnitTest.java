package com.itextpdf.svg.css.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.GroupSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.UseSvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgNodeRendererInheritanceResolverUnitTest {

    @Test
    public void applyInheritanceToSubTreeFillTest(){

        String expectedFillAttribute = "blue";

        UseSvgNodeRenderer newRoot = new UseSvgNodeRenderer();
        newRoot.setAttribute(SvgConstants.Attributes.FILL,expectedFillAttribute);

        GroupSvgNodeRenderer subTree = new GroupSvgNodeRenderer();
        RectangleSvgNodeRenderer rect =new RectangleSvgNodeRenderer();
        CircleSvgNodeRenderer circle = new CircleSvgNodeRenderer();

        subTree.addChild(rect);
        subTree.addChild(circle);

        SvgNodeRendererInheritanceResolver sru = new SvgNodeRendererInheritanceResolver();

        sru.applyInheritanceToSubTree(newRoot,subTree);

        Assert.assertEquals(expectedFillAttribute,subTree.getAttribute(SvgConstants.Attributes.FILL));
        Assert.assertEquals(expectedFillAttribute,rect.getAttribute(SvgConstants.Attributes.FILL));
        Assert.assertEquals(expectedFillAttribute,circle.getAttribute(SvgConstants.Attributes.FILL));
    }

    @Test
    public void applyInheritanceToSubTreeFillDoNotOverwriteTest(){

    }
}
