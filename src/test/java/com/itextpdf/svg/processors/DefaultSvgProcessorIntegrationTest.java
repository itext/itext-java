package com.itextpdf.svg.processors;

import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgSvgNodeRenderer;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Category(IntegrationTest.class)
public class DefaultSvgProcessorIntegrationTest {


    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/processors/impl/DefaultSvgProcessorIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/processors/impl/DefaultSvgProcessorIntegrationTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void DefaultBehaviourTest() throws IOException {
        String svgFile = sourceFolder + "RedCircle.svg";
        InputStream svg = new FileInputStream(svgFile);
        ISvgProcessor processor = new DefaultSvgProcessor();
        JsoupXmlParser xmlParser = new JsoupXmlParser();
        IDocumentNode root = xmlParser.parse(svg,null);
        ISvgNodeRenderer actual = processor.process(root);

        ISvgNodeRenderer expected = new SvgSvgNodeRenderer();
        ISvgNodeRenderer expectedEllipse = new EllipseSvgNodeRenderer();
        Map<String, String> expectedEllipseAttributes = new HashMap<>();
        expectedEllipse.setAttributesAndStyles(expectedEllipseAttributes);
        expected.addChild(expectedEllipse);

        //1 child
        Assert.assertEquals(expected.getChildren().size(),actual.getChildren().size());
        //Attribute comparison
        //TODO(RND-868) : Replace above check with the following
        //Assert.assertEquals(expected,actual);
    }


}
