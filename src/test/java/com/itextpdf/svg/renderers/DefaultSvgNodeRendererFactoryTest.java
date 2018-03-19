package com.itextpdf.svg.renderers;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.dummy.factories.DummySvgNodeMapper;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererMapper;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class DefaultSvgNodeRendererFactoryTest {

    private ISvgNodeRendererFactory fact;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void setUp() {
        fact = new DefaultSvgNodeRendererFactory(new DummySvgNodeMapper());
    }

    @Test
    public void nonExistingTagTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        Element nonExistingElement = new Element(Tag.valueOf("notAnExistingTag"), "");
        IElementNode tag = new JsoupElementNode(nonExistingElement);
        fact.createSvgNodeRendererForTag(tag, null);
    }

    @Test
    public void protectedConstructorTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        Element protectedElement = new Element(Tag.valueOf("protected"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);
        fact.createSvgNodeRendererForTag(tag, null);
    }

    @Test
    public void protectedConstructorInnerTest() throws ReflectiveOperationException {
        junitExpectedException.expect(ReflectiveOperationException.class);
        Element protectedElement = new Element(Tag.valueOf("protected"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);
        try {
            fact.createSvgNodeRendererForTag(tag, null);
        } catch (SvgProcessingException spe) {
            throw (ReflectiveOperationException) spe.getCause();
        }
    }

    @Test
    public void argumentedConstructorTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        Element protectedElement = new Element(Tag.valueOf("argumented"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);
        fact.createSvgNodeRendererForTag(tag, null);
    }

    @Test
    public void argumentedConstructorInnerTest() throws ReflectiveOperationException {
        junitExpectedException.expect(ReflectiveOperationException.class);
        Element protectedElement = new Element(Tag.valueOf("argumented"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);
        try {
            fact.createSvgNodeRendererForTag(tag, null);
        } catch (SvgProcessingException spe) {
            throw (ReflectiveOperationException) spe.getCause();
        }
    }

    @Test
    public void rootTagTest() {
        Element element = new Element(Tag.valueOf("dummy"), "");
        IElementNode tag = new JsoupElementNode(element);
        ISvgNodeRenderer childRenderer = fact.createSvgNodeRendererForTag(tag, null);
        Assert.assertTrue(childRenderer instanceof DummySvgNodeRenderer);
    }

    private static class LocalTestMapper implements ISvgNodeRendererMapper {

        @Override
        public Map<String, Class<? extends ISvgNodeRenderer>> getMapping() {
            Map<String, Class<? extends ISvgNodeRenderer>> result = new HashMap<>();
            result.put("test", DummyProcessableSvgNodeRenderer.class);
            return result;
        }

        @Override
        public Collection<String> getIgnoredTags() {
            return new ArrayList<>();
        }
    }

    @Test
    public void customMapperTest() {
        fact = new DefaultSvgNodeRendererFactory(new LocalTestMapper());
        Element element = new Element(Tag.valueOf("test"), "");
        IElementNode tag = new JsoupElementNode(element);
        ISvgNodeRenderer rend = fact.createSvgNodeRendererForTag(tag, null);
        Assert.assertTrue(rend instanceof DummyProcessableSvgNodeRenderer);
    }

    @Test
    public void hierarchyTagTest() {
        Element parentEl = new Element(Tag.valueOf("dummy"), "");
        IElementNode parentTag = new JsoupElementNode(parentEl);
        Element childEl = new Element(Tag.valueOf("dummy"), "");
        IElementNode childTag = new JsoupElementNode(childEl);
        ISvgNodeRenderer parentRenderer = fact.createSvgNodeRendererForTag(parentTag, null);
        ISvgNodeRenderer childRenderer = fact.createSvgNodeRendererForTag(childTag, parentRenderer);

        Assert.assertEquals(parentRenderer, childRenderer.getParent());
        Assert.assertEquals(1, parentRenderer.getChildren().size());
        Assert.assertEquals(childRenderer, parentRenderer.getChildren().get(0));
    }

    private static class FaultyTestMapper implements ISvgNodeRendererMapper {

        @Override
        public Map<String, Class<? extends ISvgNodeRenderer>> getMapping() {
            throw new RuntimeException();
        }

        @Override
        public Collection<String> getIgnoredTags() {
            return null;
        }
    }

    /**
     * Tests that exception is already thrown in constructor
     */
    @Test
    public void faultyMapperTest() {
        junitExpectedException.expect(RuntimeException.class);
        fact = new DefaultSvgNodeRendererFactory(new FaultyTestMapper());
    }

}
