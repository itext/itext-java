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
package com.itextpdf.svg.processors.impl;

import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.dummy.processors.impl.DummySvgConverterProperties;
import com.itextpdf.svg.dummy.renderers.impl.DummyBranchSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
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
public class DefaultSvgProcessorUnitTest {


    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    //Main success scenario

    /**
     * Simple correct example
     */
    @Test
    public void dummyProcessingTestCorrectSimple() {
        //Setup nodes
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        Element jsoupSVGPath = new Element(Tag.valueOf("path"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props = new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root, props).getRootRenderer();
        //setup expected
        IBranchSvgNodeRenderer rootExpected = new DummyBranchSvgNodeRenderer("svg");
        rootExpected.addChild(new DummySvgNodeRenderer("circle"));
        rootExpected.addChild(new DummySvgNodeRenderer("path"));
        //Compare
        Assert.assertEquals(rootActual, rootExpected);
    }

    @Test()
    public void dummyProcessingTestCorrectNested() {
        //Setup nodes
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        Element jsoupSVGPath = new Element(Tag.valueOf("path"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        INode nestedSvg = new JsoupElementNode(jsoupSVGRoot);
        nestedSvg.addChild(new JsoupElementNode(jsoupSVGCircle));
        nestedSvg.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(nestedSvg);

        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props = new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root, props).getRootRenderer();
        //setup expected
        IBranchSvgNodeRenderer rootExpected = new DummyBranchSvgNodeRenderer("svg");
        rootExpected.addChild(new DummySvgNodeRenderer("circle"));
        rootExpected.addChild(new DummySvgNodeRenderer("path"));

        IBranchSvgNodeRenderer nestedSvgRend = new DummyBranchSvgNodeRenderer("svg");
        nestedSvgRend.addChild(new DummySvgNodeRenderer("circle"));
        nestedSvgRend.addChild(new DummySvgNodeRenderer("circle"));

        rootExpected.addChild(nestedSvgRend);
        //Compare
        Assert.assertEquals(rootActual, rootExpected);
    }

    //Edge cases
    @Test()
    /*
      Invalid input: null
     */
    public void dummyProcessingTestNodeHasNullChild() {
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(null);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props = new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root, props).getRootRenderer();
        //setup expected
        ISvgNodeRenderer rootExpected = new DummySvgNodeRenderer("svg");
        Assert.assertEquals(rootExpected, rootActual);
    }

    @Test
    public void dummyProcessingSvgTagIsNotRootOfInput() {
        Element jsoupRandomElement = new Element(Tag.valueOf("body"), "");
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        INode root = new JsoupElementNode(jsoupRandomElement);
        INode svg = new JsoupElementNode(jsoupSVGRoot);
        svg.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(svg);
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props = new DummySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root, props).getRootRenderer();
        //setup expected
        IBranchSvgNodeRenderer rootExpected = new DummyBranchSvgNodeRenderer("svg");
        rootExpected.addChild(new DummySvgNodeRenderer("circle"));
        Assert.assertEquals(rootActual, rootExpected);
    }

    @Test
    public void dummyProcessingNoSvgTagInInput() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.NOROOT);

        Element jsoupSVGRoot = new Element(Tag.valueOf("polygon"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties props = new DummySvgConverterProperties();

        processor.process(root, props).getRootRenderer();
    }

    @Test
    public void dummyProcessingTestNullInput() {
        junitExpectedException.expect(SvgProcessingException.class);
        DefaultSvgProcessor processor = new DefaultSvgProcessor();

        processor.process(null);
    }

    @Ignore("TODO: Implement Tree comparison. Blocked by RND-868\n")
    @Test()
    public void defaultProcessingTestNoPassedProperties() {
        //Setup nodes
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        Element jsoupSVGPath = new Element(Tag.valueOf("path"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgNodeRenderer rootActual = processor.process(root).getRootRenderer();
        //Compare
        Assert.assertNull(rootActual);
    }

    @Ignore("TODO: Implement Tree comparison. Blocked by RND-868\n")
    @Test()
    public void defaultProcessingTestPassedPropertiesNull() {
        //Setup nodes
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        Element jsoupSVGPath = new Element(Tag.valueOf("path"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgNodeRenderer rootActual = processor.process(root, null).getRootRenderer();
        //Compare
        Assert.assertNull(rootActual);
    }

    @Ignore("TODO: Implement Tree comparison. Blocked by RND-868\n")
    @Test()
    public void defaultProcessingTestPassedPropertiesReturnNullValues() {
        //Setup nodes
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        Element jsoupSVGCircle = new Element(Tag.valueOf("circle"), "");
        Element jsoupSVGPath = new Element(Tag.valueOf("path"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        root.addChild(new JsoupElementNode(jsoupSVGCircle));
        root.addChild(new JsoupElementNode(jsoupSVGPath));
        //Run
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        ISvgConverterProperties convProps = new EmptySvgConverterProperties();
        ISvgNodeRenderer rootActual = processor.process(root, convProps).getRootRenderer();
        //Compare
        Assert.assertNull(rootActual);
    }

    private static class EmptySvgConverterProperties extends SvgConverterProperties {
        @Override
        public ISvgNodeRendererFactory getRendererFactory() {
            return null;
        }

        @Override
        public String getCharset() {
            return null;
        }
    }

    @Test
    public void findFirstElementNullTest() {
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        IElementNode actual = processor.findFirstElement(null, "name");
        Assert.assertNull(actual);
    }

    @Test
    @Ignore("RND-868")
    public void processWithNullPropertiesTest() {
        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        Element jsoupSVGRoot = new Element(Tag.valueOf("svg"), "");
        INode root = new JsoupElementNode(jsoupSVGRoot);
        ISvgProcessorResult actual = processor.process(root, null);
        ISvgProcessorResult expected = processor.process(root);

        Assert.assertEquals(expected.getRootRenderer(), actual.getRootRenderer());
    }

    @Test
    public void depthFirstNullRendererTest() {
        Element jsoupNonExistingElement = new Element(Tag.valueOf("nonExisting"), "");
        INode root = new JsoupElementNode(jsoupNonExistingElement);
        DefaultSvgProcessor dsp = new DefaultSvgProcessor();
        ISvgConverterProperties scp = new SvgConverterProperties();
        dsp.performSetup(root, scp);
        // below method must not throw a NullPointerException
        dsp.executeDepthFirstTraversal(root);
    }
}
