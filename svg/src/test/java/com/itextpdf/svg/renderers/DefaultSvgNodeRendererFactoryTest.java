/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.svg.dummy.factories.DummySvgNodeFactory;
import com.itextpdf.svg.dummy.renderers.impl.DummyArgumentedConstructorSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")
public class DefaultSvgNodeRendererFactoryTest extends ExtendedITextTest {

    private final ISvgNodeRendererFactory fact = new DummySvgNodeFactory();

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG)
    })
    public void nonExistingTagTest() {
        ISvgNodeRendererFactory factory = new DefaultSvgNodeRendererFactory();
        Element nonExistingElement = new Element(Tag.valueOf("notAnExistingTag"), "");
        IElementNode tag = new JsoupElementNode(nonExistingElement);
        factory.createSvgNodeRendererForTag(tag, null);
    }

    @Test
    public void argumentedConstructorTest() {
        Element protectedElement = new Element(Tag.valueOf("argumented"), "");
        IElementNode tag = new JsoupElementNode(protectedElement);
        ISvgNodeRenderer renderer = fact.createSvgNodeRendererForTag(tag, null);
        Assertions.assertTrue(renderer instanceof DummyArgumentedConstructorSvgNodeRenderer);
        Assertions.assertEquals(15, ((DummyArgumentedConstructorSvgNodeRenderer) renderer).number);
    }

    @Test
    public void rootTagTest() {
        Element element = new Element(Tag.valueOf("dummy"), "");
        IElementNode tag = new JsoupElementNode(element);
        ISvgNodeRenderer childRenderer = fact.createSvgNodeRendererForTag(tag, null);
        Assertions.assertTrue(childRenderer instanceof DummySvgNodeRenderer);
    }

    private static class LocalSvgNodeRendererFactory extends DefaultSvgNodeRendererFactory {
        @Override
        public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
            ISvgNodeRenderer result;
            if ("test".equals(tag.name())) {
                result = new DummyProcessableSvgNodeRenderer();
                result.setParent(parent);
                return result;
            } else {
                return null;
            }
        }
    }

    @Test
    public void customMapperTest() {
        ISvgNodeRendererFactory factory = new LocalSvgNodeRendererFactory();
        Element element = new Element(Tag.valueOf("test"), "");
        IElementNode tag = new JsoupElementNode(element);
        ISvgNodeRenderer rend = factory.createSvgNodeRendererForTag(tag, null);
        Assertions.assertTrue(rend instanceof DummyProcessableSvgNodeRenderer);
    }

    @Test
    public void hierarchyTagTest() {
        Element parentEl = new Element(Tag.valueOf("dummy"), "");
        IElementNode parentTag = new JsoupElementNode(parentEl);
        Element childEl = new Element(Tag.valueOf("dummy"), "");
        IElementNode childTag = new JsoupElementNode(childEl);
        ISvgNodeRenderer parentRenderer = fact.createSvgNodeRendererForTag(parentTag, null);
        ISvgNodeRenderer childRenderer = fact.createSvgNodeRendererForTag(childTag, parentRenderer);

        Assertions.assertEquals(parentRenderer, childRenderer.getParent());
    }
}
