package com.itextpdf.svg.processors.impl;

import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupTextNode;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.FileNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)

public class SvgFontProcessorTest {
    @Test
    public void addFontFaceFontsTest() throws FileNotFoundException {
        Element styleTag = new Element(Tag.valueOf("style"), "");
        TextNode styleContents = new TextNode("\n" +
                "\t@font-face{\n" +
                "\t\tfont-family:Courier;\n" +
                "\t\tsrc:local(Courier);\n" +
                "\t}\n" +
                "  ", "");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));
        ISvgConverterProperties properties = new DefaultSvgConverterProperties(jSoupStyle);
        ProcessorContext context = new ProcessorContext(new DefaultSvgConverterProperties());
        ICssResolver cssResolver = properties.getCssResolver();
        SvgFontProcessor svgFontProcessor = new SvgFontProcessor(context);
        svgFontProcessor.addFontFaceFonts(cssResolver);
        FontInfo info = (FontInfo) context.getTempFonts().getFonts().toArray()[0];
        Assert.assertEquals("Courier", info.getFontName());
    }
}
