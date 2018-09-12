package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;

@Category(UnitTest.class)
public class DefsSvgNodeRendererUnitTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/DefsSvgNodeRendererTest/";

    @Test
    public void processDefsNoChildrenTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(new FileInputStream(sourceFolder + "onlyDefsWithNoChildren.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg);

        Assert.assertTrue(result.getNamedObjects().isEmpty());
    }

    @Test
    public void processDefsOneChildTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(new FileInputStream(sourceFolder + "onlyDefsWithOneChild.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg);

        Assert.assertTrue(result.getNamedObjects().get("circle1") instanceof CircleSvgNodeRenderer);
    }

    @Test
    public void processDefsMultipleChildrenTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(new FileInputStream(sourceFolder + "onlyDefsWithMultipleChildren.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg);

        Assert.assertTrue(result.getNamedObjects().get("circle1") instanceof CircleSvgNodeRenderer);
        Assert.assertTrue(result.getNamedObjects().get("line1") instanceof LineSvgNodeRenderer);
        Assert.assertTrue(result.getNamedObjects().get("rect1") instanceof RectangleSvgNodeRenderer);
    }

    @Test
    public void processDefsParentShouldBeNullTest() throws IOException {
        INode parsedSvg = SvgConverter.parse(new FileInputStream(sourceFolder + "onlyDefsWithOneChild.svg"));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg);

        Assert.assertNull(result.getNamedObjects().get("circle1").getParent());
    }
}