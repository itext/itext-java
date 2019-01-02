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
        Assert.assertNull(fact.createSvgNodeRendererForTag(tag, null));
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
