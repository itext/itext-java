package com.itextpdf.svg.renderers;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.dummy.factories.DummySvgNodeMapper;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultSvgNodeRendererFactoryDrawTest {
    
    private ISvgNodeRendererFactory fact;
    
    @Before
    public void setUp() {
        fact = new DefaultSvgNodeRendererFactory(new DummySvgNodeMapper());
    }
    
    @Test
    public void basicProcessedRendererTest() {
        Element element = new Element(Tag.valueOf("processable"), "");
        IElementNode tag = new JsoupElementNode(element);
        ISvgNodeRenderer renderer = fact.createSvgNodeRendererForTag(tag, null);
        Assert.assertTrue(renderer instanceof DummyProcessableSvgNodeRenderer);
        renderer.draw(new SvgDrawContext());
        DummyProcessableSvgNodeRenderer processed = (DummyProcessableSvgNodeRenderer) renderer;
        Assert.assertTrue(processed.isProcessed());
    }
    
    @Test
    public void nestedProcessedRendererTest() {
        Element parentEl = new Element(Tag.valueOf("processable"), "");
        Element childEl = new Element(Tag.valueOf("processable"), "");
        IElementNode parentTag = new JsoupElementNode(parentEl);
        IElementNode childTag = new JsoupElementNode(childEl);
        ISvgNodeRenderer parentRenderer = fact.createSvgNodeRendererForTag(parentTag, null);
        ISvgNodeRenderer childRenderer = fact.createSvgNodeRendererForTag(childTag, parentRenderer);

        parentRenderer.draw(new SvgDrawContext());

        DummyProcessableSvgNodeRenderer parentProcessed = (DummyProcessableSvgNodeRenderer) parentRenderer;
        Assert.assertTrue(parentProcessed.isProcessed());
        DummyProcessableSvgNodeRenderer childProcessed = (DummyProcessableSvgNodeRenderer) childRenderer;
        // child is not processed unless instructed thus in its parent
        Assert.assertFalse(childProcessed.isProcessed());
    }
}
