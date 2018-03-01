package com.itextpdf.svg.processors;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.css.ICssResolver;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.dummy.processors.impl.DummySvgConverterProperties;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class DefaultSvgProcessorTest {


    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    //Main success scenario
    @Test
    /**
     * Simple correct example
     */
    public void dummyProcessingTestCorrectSimple(){
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
        ISvgNodeRenderer rootExpected = new DummySvgNodeRenderer("svg");
        rootExpected.addChild(new DummySvgNodeRenderer("circle"));
        rootExpected.addChild(new DummySvgNodeRenderer("path"));
        //Compare
        Assert.assertEquals(rootActual,rootExpected);
    }

    @Test()
    public void dummyProcessingTestCorrectNested(){
        //Setup nodes
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"),"");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"),"");
        Element jsoupSVGPath = new Element(Tag.valueOf("path"),"");
        INode root = null;
        root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        INode nestedSvg = new JsoupElementNode(jsoupSVGRoot);
        nestedSvg.addChild(new JsoupElementNode(jsoupSVGCircle));
        nestedSvg.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(nestedSvg);

        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props= new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root,props);
        //setup expected
        ISvgNodeRenderer rootExpected = new DummySvgNodeRenderer("svg");
        rootExpected.addChild(new DummySvgNodeRenderer("circle"));
        rootExpected.addChild(new DummySvgNodeRenderer("path"));

        ISvgNodeRenderer nestedSvgRend = new DummySvgNodeRenderer("svg");
        nestedSvgRend.addChild(new DummySvgNodeRenderer("circle"));
        nestedSvgRend.addChild(new DummySvgNodeRenderer("circle"));

        rootExpected.addChild(nestedSvgRend);
        //Compare
        Assert.assertEquals(rootActual,rootExpected);
    }

    //Edge cases
    @Test()
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
        ISvgNodeRenderer rootExpected = new DummySvgNodeRenderer("svg");
    }

    @Test
    public void dummyProcessingSvgTagIsNotRootOfInput(){
        Element jsoupRandomElement = new Element(Tag.valueOf("body"),"");
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"),"");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"),"");
        INode root = new JsoupElementNode(jsoupRandomElement);
        INode svg = new JsoupElementNode(jsoupSVGRoot);
        svg.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(svg);
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props= new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root,props);
        //setup expected
        ISvgNodeRenderer rootExpected = new DummySvgNodeRenderer("svg");
        rootExpected.addChild(new DummySvgNodeRenderer("circle"));
        Assert.assertEquals(rootActual,rootExpected);
    }

    @Test
    public void dummyProcessingNoSvgTagInInput(){
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.NOROOT);

        Element jsoupSVGRoot = new Element(Tag.valueOf("polygon"),"");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"),"");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props= new DummySvgConverterProperties();

        ISvgNodeRenderer rootActual = processor.process(root,props);
    }

    @Test
    public void dummyProcessingTestNullInput(){
        junitExpectedException.expect(SvgProcessingException.class);
        DefaultSvgProcessor processor = new DefaultSvgProcessor();

        processor.process(null);
    }

    @Ignore("TODO: Decide on default behaviour. Blocked by RND-799\n")
    @Test()
    public void defaultProcessingTestNoPassedProperties(){
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
        ISvgNodeRenderer rootActual = processor.process(root);
        //setup expected
        ISvgNodeRenderer rootExpected = null;
        //Compare
        Assert.assertEquals(rootActual,rootExpected);
    }

    @Ignore("TODO: Decide on default behaviour. Blocked by RND-799\n")
    @Test()
    public void defaultProcessingTestPassedPropertiesNull(){
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
        ISvgNodeRenderer rootActual = processor.process(root,null);
        //setup expected
        ISvgNodeRenderer rootExpected = null;
        //Compare
        Assert.assertEquals(rootActual,rootExpected);
    }

    @Ignore("TODO: Decide on default behaviour. Blocked by RND-799\n")
    @Test()
    public void defaultProcessingTestPassedPropertiesReturnNullValues(){
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
        ISvgConverterProperties convProps = new ISvgConverterProperties() {
            @Override
            public ICssResolver getCssResolver() {
                return null;
            }

            @Override
            public ISvgNodeRendererFactory getRendererFactory() {
                return null;
            }
        };
        ISvgNodeRenderer rootActual = processor.process(root,convProps);
        //setup expected
        ISvgNodeRenderer rootExpected = null;
        //Compare
        Assert.assertEquals(rootActual,rootExpected);
    }


}
