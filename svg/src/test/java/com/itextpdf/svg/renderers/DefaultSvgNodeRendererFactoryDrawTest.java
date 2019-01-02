/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.dummy.factories.DummySvgNodeMapper;
import com.itextpdf.svg.dummy.renderers.impl.DummyProcessableSvgNodeRenderer;
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
        renderer.draw(new SvgDrawContext(null, null));
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

        parentRenderer.draw(new SvgDrawContext(null, null));

        DummyProcessableSvgNodeRenderer parentProcessed = (DummyProcessableSvgNodeRenderer) parentRenderer;
        Assert.assertTrue(parentProcessed.isProcessed());
        DummyProcessableSvgNodeRenderer childProcessed = (DummyProcessableSvgNodeRenderer) childRenderer;
        // child is not processed unless instructed thus in its parent
        Assert.assertFalse(childProcessed.isProcessed());
    }
}
