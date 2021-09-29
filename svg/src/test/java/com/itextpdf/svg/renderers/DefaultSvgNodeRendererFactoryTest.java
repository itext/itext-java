/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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
        Assert.assertTrue(renderer instanceof DummyArgumentedConstructorSvgNodeRenderer);
        Assert.assertEquals(15, ((DummyArgumentedConstructorSvgNodeRenderer) renderer).number);
    }

    @Test
    public void rootTagTest() {
        Element element = new Element(Tag.valueOf("dummy"), "");
        IElementNode tag = new JsoupElementNode(element);
        ISvgNodeRenderer childRenderer = fact.createSvgNodeRendererForTag(tag, null);
        Assert.assertTrue(childRenderer instanceof DummySvgNodeRenderer);
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
}
