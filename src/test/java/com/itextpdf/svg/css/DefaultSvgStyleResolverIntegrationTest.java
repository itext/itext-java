package com.itextpdf.svg.css;


import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

@Category(IntegrationTest.class)
public class DefaultSvgStyleResolverIntegrationTest {

    @Test
    public void RedCirleTest() {
        String svg = "<svg\n" +
                "   width=\"210mm\"\n" +
                "   height=\"297mm\"\n" +
                "   viewBox=\"0 0 210 297\"\n" +
                "   version=\"1.1\"\n" +
                "  <title id=\"title4508\">Red Circle</title>\n" +
                "    <ellipse\n" +
                "       id=\"path3699\"\n" +
                "       cx=\"96.005951\"\n" +
                "       cy=\"110.65774\"\n" +
                "       rx=\"53.672619\"\n" +
                "       ry=\"53.294643\"\n" +
                "       style=\"stroke-width:1.76388889;stroke:#da0000;stroke-opacity:1;fill:none;stroke-miterlimit:4;stroke-dasharray:none\" />\n" +
                "</svg>\n";
        ISvgProcessor processor = new DefaultSvgProcessor();
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg);
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) processor.process(root);

        Map<String, String> actual = new HashMap<>();
        //Traverse to ellipse
        ISvgNodeRenderer ellipse = nodeRenderer.getChildren().get(0);
        actual.put("stroke",ellipse.getAttribute("stroke"));
        actual.put("stroke-width",ellipse.getAttribute("stroke-width"));
        actual.put("stroke-opacity",ellipse.getAttribute("stroke-opacity"));

        Map<String,String> expected = new HashMap<>();
        expected.put("stroke-width", "1.76388889");
        expected.put("stroke","#da0000");
        expected.put("stroke-opacity","1");

        Assert.assertEquals(expected,actual);
    }

    @Test
    public void styleTagProcessingTest(){
        String svg = "<svg\n" +
                "   width=\"210mm\"\n" +
                "   height=\"297mm\"\n" +
                "   viewBox=\"0 0 210 297\"\n" +
                "   version=\"1.1\"\n" +
                "   id=\"svg8\"\n" +
                "   >\n" +
                "  <style>\n" +
                "\tellipse{\n" +
                "\t\tstroke-width:1.76388889;\n" +
                "\t\tstroke:#da0000;\n" +
                "\t\tstroke-opacity:1;\n" +
                "\t}\n" +
                "  </style>\n" +
                "    <ellipse\n" +
                "       id=\"path3699\"\n" +
                "       cx=\"96.005951\"\n" +
                "       cy=\"110.65774\"\n" +
                "       rx=\"53.672619\"\n" +
                "       ry=\"53.294643\"\n" +
                "       style=\"fill:none;stroke-miterlimit:4;stroke-dasharray:none\" />\n" +
                "</svg>\n";
        ISvgProcessor processor = new DefaultSvgProcessor();
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg);
        IBranchSvgNodeRenderer nodeRenderer = (IBranchSvgNodeRenderer) processor.process(root);

        Map<String, String> actual = new HashMap<>();
        //Traverse to ellipse
        ISvgNodeRenderer ellipse = nodeRenderer.getChildren().get(0);
        actual.put("stroke",ellipse.getAttribute("stroke"));
        actual.put("stroke-width",ellipse.getAttribute("stroke-width"));
        actual.put("stroke-opacity",ellipse.getAttribute("stroke-opacity"));

        Map<String,String> expected = new HashMap<>();
        expected.put("stroke-width", "1.76388889");
        expected.put("stroke","#da0000");
        expected.put("stroke-opacity","1");

        Assert.assertEquals(expected,actual);
    }
}
