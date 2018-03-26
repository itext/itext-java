package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.css.ICssContext;
import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.DataNode;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.TextNode;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDataNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupTextNode;
import com.itextpdf.svg.css.impl.DefaultSvgStyleResolver;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

@Category(UnitTest.class)
public class DefaultSvgStyleResolverTest {
    //Single element test
    //Inherits values from parent?
    //Calculates values from parent
    @Test
    public void DefaultSvgCssResolverBasicAttributeTest(){

        Element jsoupCircle = new Element(Tag.valueOf("circle"),"");
        Attributes circleAttributes  = jsoupCircle.attributes();
        circleAttributes.put(new Attribute("id","circle1"));
        circleAttributes.put(new Attribute("cx","95"));
        circleAttributes.put(new Attribute("cy","95"));
        circleAttributes.put(new Attribute("rx","53"));
        circleAttributes.put(new Attribute("ry","53"));
        circleAttributes.put(new Attribute("style","stroke-width:1.5;stroke:#da0000;"));

        ICssContext cssContext = new SvgCssContext();
        INode circle = new JsoupElementNode(jsoupCircle);
        ICssResolver resolver = new DefaultSvgStyleResolver(circle);
        Map<String, String> actual = resolver.resolveStyles(circle,cssContext);
        Map<String,String> expected = new HashMap<>();
        expected.put("id","circle1");
        expected.put("cx","95");
        expected.put("cy","95");
        expected.put("rx","53");
        expected.put("ry","53");
        expected.put("stroke-width","1.5");
        expected.put("stroke","#da0000");


        Assert.assertEquals(expected,actual);
    }

    @Test
    public void DefaultSvgCssResolverStyleTagTest(){
        Element styleTag = new Element(Tag.valueOf("style"),"");
        TextNode styleContents = new TextNode("\n" +
                "\tellipse{\n" +
                "\t\tstroke-width:1.76388889;\n" +
                "\t\tstroke:#da0000;\n" +
                "\t\tstroke-opacity:1;\n" +
                "\t}\n" +
                "  ","");
        JsoupElementNode jSoupStyle = new JsoupElementNode(styleTag);
        jSoupStyle.addChild(new JsoupTextNode(styleContents));
        Element ellipse = new Element(Tag.valueOf("ellipse"),"");
        JsoupElementNode jSoupEllipse = new JsoupElementNode(ellipse);

        DefaultSvgStyleResolver resolver = new DefaultSvgStyleResolver(jSoupStyle);
        ICssContext svgContext = new SvgCssContext();
        Map<String,String> actual = resolver.resolveStyles(jSoupEllipse,svgContext);

        Map<String,String> expected = new HashMap<>();
        expected.put("stroke-width", "1.76388889");
        expected.put("stroke","#da0000");
        expected.put("stroke-opacity","1");

        Assert.assertEquals(expected,actual);
    }
}