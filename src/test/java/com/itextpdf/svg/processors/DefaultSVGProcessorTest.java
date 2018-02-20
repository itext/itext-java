package com.itextpdf.svg.processors;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.TestUtil;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.processors.impl.DummySvgConverterProperties;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.DummySvgNodeRenderer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DefaultSVGProcessorTest {

    //Main success scenario
    @Test
    /**
     * Simple correct example
     */
    public void dummyProcessingTestCorrect(){
        //Setup nodes
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"),"");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"),"");
        Element jsoupSVGPath = new Element(Tag.valueOf("path"),"");
        INode root = null;
        root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props= new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root,props);
        //setup expected
        ISvgNodeRenderer rootExpected = new DummySvgNodeRenderer("svg",null);
        rootExpected.addChild(new DummySvgNodeRenderer("circle",rootExpected));
        rootExpected.addChild(new DummySvgNodeRenderer("path",rootExpected));
        //Compare
        Assert.assertTrue(TestUtil.compareDummyRendererTrees(rootActual,rootExpected));
    }

    //Edge cases
    @Test
    /**
     * Invalid input: null
     */
    public void dummyProcessingTestNodeHasNullChild(){
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"),"");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"),"");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(null);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props= new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root,props);
        //setup expected
        ISvgNodeRenderer rootExpected = new DummySvgNodeRenderer("svg",null);
    }

    @Ignore
    @Test(expected = SvgProcessingException.class)
    /**
     * Invalid input: loop in tree
     */
    public void dummyProcessingTestLoop(){
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"),null);
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"),null);
        Element jsoupSVGPath = new Element(Tag.valueOf("path"),null);
        Element jsoupSVG2 = new Element(Tag.valueOf("svg"),null);
        INode root = null;
        root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        JsoupElementNode loopNode = new JsoupElementNode(jsoupSVG2);
        root.addChild(loopNode);
        loopNode.addChild(root);
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props= new DummySvgConverterProperties();
        //Expect an exception
        ISvgNodeRenderer rootActual = processor.process(root,props);

    }

    @Test(expected = SvgProcessingException.class)
    public void dummyProcessingTestNullInput(){
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        processor.process(null);
    }

}
