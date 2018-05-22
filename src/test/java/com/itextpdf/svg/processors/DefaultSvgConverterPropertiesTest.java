package com.itextpdf.svg.processors;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.processors.impl.DefaultSvgConverterProperties;
import com.itextpdf.test.annotations.type.UnitTest;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultSvgConverterPropertiesTest {

    @Test
    public void getCharsetNameRegressionTest() {
        String expected = StandardCharsets.UTF_8.name();
        Element ellipse = new Element( Tag.valueOf("ellipse"),"");
        JsoupElementNode jSoupEllipse = new JsoupElementNode(ellipse);
        String actual = new DefaultSvgConverterProperties(jSoupEllipse).getCharset();

        Assert.assertEquals(expected, actual);
    }
}