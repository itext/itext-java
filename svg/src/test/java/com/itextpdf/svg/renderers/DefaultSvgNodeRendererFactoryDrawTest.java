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
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")
public class DefaultSvgNodeRendererFactoryDrawTest extends ExtendedITextTest {
    
    private final ISvgNodeRendererFactory fact = new DummySvgNodeFactory();
    
    @Test
    public void basicProcessedRendererTest() {
        Element element = new Element(Tag.valueOf("processable"), "");
        IElementNode tag = new JsoupElementNode(element);
        ISvgNodeRenderer renderer = fact.createSvgNodeRendererForTag(tag, null);
        Assertions.assertTrue(renderer instanceof DummyProcessableSvgNodeRenderer);
        renderer.draw(new SvgDrawContext(null, null));
        DummyProcessableSvgNodeRenderer processed = (DummyProcessableSvgNodeRenderer) renderer;
        Assertions.assertTrue(processed.isProcessed());
    }
    
    @Test
    public void nestedProcessedRendererTest() {
        Element parentEl = new Element(Tag.valueOf("processable"), "");
        Element childEl = new Element(Tag.valueOf("processable"), "");
        IElementNode parentTag = new JsoupElementNode(parentEl);
        IElementNode childTag = new JsoupElementNode(childEl);
        ISvgNodeRenderer parentRenderer = fact.createSvgNodeRendererForTag(parentTag, null);
        ISvgNodeRenderer childRenderer = fact.createSvgNodeRendererForTag(childTag, parentRenderer);

        parentRenderer.draw(new SvgDrawContext(null, null));

        DummyProcessableSvgNodeRenderer parentProcessed = (DummyProcessableSvgNodeRenderer) parentRenderer;
        Assertions.assertTrue(parentProcessed.isProcessed());
        DummyProcessableSvgNodeRenderer childProcessed = (DummyProcessableSvgNodeRenderer) childRenderer;
        // child is not processed unless instructed thus in its parent
        Assertions.assertFalse(childProcessed.isProcessed());
    }
}
