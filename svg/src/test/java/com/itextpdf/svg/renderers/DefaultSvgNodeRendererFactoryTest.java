/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.renderers;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.dummy.factories.DummySvgNodeMapper;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererMapper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultSvgNodeRendererFactoryTest extends ExtendedITextTest {

    private ISvgNodeRendererFactory fact;

    @Before
    public void setUp() {
        fact = new DefaultSvgNodeRendererFactory(new DummySvgNodeMapper());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG)
    })
    public void nonExistingTagTest() {
        Element nonExistingElement = new Element(Tag.valueOf("notAnExistingTag"), "");
        IElementNode tag = new JsoupElementNode(nonExistingElement);
        fact.createSvgNodeRendererForTag(tag, null);
    }

    @Test
    public void protectedConstructorTest() {
        Element protectedElement = new Element(Tag.valueOf("protected"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);

        Assert.assertThrows(SvgProcessingException.class, () -> fact.createSvgNodeRendererForTag(tag, null));
    }

    @Test
    public void protectedConstructorInnerTest() throws ReflectiveOperationException {
        Element protectedElement = new Element(Tag.valueOf("protected"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);

        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> fact.createSvgNodeRendererForTag(tag, null)
        );
        Assert.assertTrue(e.getCause() instanceof ReflectiveOperationException);
    }

    @Test
    public void argumentedConstructorTest() {
        Element protectedElement = new Element(Tag.valueOf("argumented"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);
        Assert.assertThrows(SvgProcessingException.class, () -> Assert.assertNull(fact.createSvgNodeRendererForTag(tag, null)));
    }

    @Test
    public void argumentedConstructorInnerTest() throws ReflectiveOperationException {
        Element protectedElement = new Element(Tag.valueOf("argumented"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);

        Exception e = Assert.assertThrows(SvgProcessingException.class,
                () -> fact.createSvgNodeRendererForTag(tag, null)
        );
        Assert.assertTrue(e.getCause() instanceof ReflectiveOperationException);
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
        Assert.assertThrows(RuntimeException.class, () -> new DefaultSvgNodeRendererFactory(new FaultyTestMapper()));
    }

}
