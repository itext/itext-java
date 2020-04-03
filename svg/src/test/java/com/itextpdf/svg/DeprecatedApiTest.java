package com.itextpdf.svg;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
//This test class can safely be removed in 7.2
public class DeprecatedApiTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void processNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.process(null);
    }

    @Test
    public void processNode() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        IBranchSvgNodeRenderer node = (IBranchSvgNodeRenderer) SvgConverter.process(svg).getRootRenderer();
        Assert.assertTrue(node instanceof SvgTagSvgNodeRenderer);
        Assert.assertEquals(0, node.getChildren().size());
        Assert.assertNull(node.getParent());
    }
}
