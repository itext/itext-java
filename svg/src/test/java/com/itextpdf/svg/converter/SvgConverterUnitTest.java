/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.svg.converter;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.dummy.processors.impl.DummySvgConverterProperties;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorContext;
import com.itextpdf.svg.processors.impl.SvgProcessorResult;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@org.junit.jupiter.api.Tag("UnitTest")
public class SvgConverterUnitTest extends ExtendedITextTest {

    // we cannot easily mock the PdfDocument, so we make do with as close to unit testing as we can
    private PdfDocument doc;
    private final String content = "<svg width=\"10\" height=\"10\"/>";
    private InputStream is;

    @BeforeEach
    public void setup() {
        doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    public void teardown() {
        doc.close();
    }

    private void testResourceCreated(PdfDocument doc, int pageNo) {
        PdfResources res = doc.getPage(pageNo).getResources();
        Assertions.assertEquals(1, res.getPdfObject().size());
        for (PdfName name : res.getResourceNames()) {
            PdfObject obj = res.getResourceObject(PdfName.XObject, name);
            Assertions.assertTrue(obj.isStream());
        }
    }

    @Test
    public void drawStringOnDocumentCreatesResourceTest() {
        SvgConverter.drawOnDocument(content, doc, 1);
        testResourceCreated(doc, 1);
    }

    @Test
    public void drawStringOnDocumentWithPropsCreatesResourceTest() {
        SvgConverter.drawOnDocument(content, doc, 1, new DummySvgConverterProperties());
        testResourceCreated(doc, 1);
    }

    @Test
    public void drawStreamOnDocumentCreatesResourceTest() throws IOException {
        SvgConverter.drawOnDocument(is, doc, 1);
        testResourceCreated(doc, 1);
    }

    @Test
    public void drawStreamOnDocumentWithPropsCreatesResourceTest() throws IOException {
        SvgConverter.drawOnDocument(is, doc, 1, new DummySvgConverterProperties());
        testResourceCreated(doc, 1);
    }

    @Test
    public void drawStringOnPageCreatesResourceTest() {
        PdfPage page = doc.addNewPage();
        SvgConverter.drawOnPage(content, page);
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStringOnPageWithPropsCreatesResourceTest() {
        PdfPage page = doc.addNewPage();
        SvgConverter.drawOnPage(content, page, new DummySvgConverterProperties());
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnPageCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        SvgConverter.drawOnPage(is, page);
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnPageWithPropsCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        SvgConverter.drawOnPage(is, page, new DummySvgConverterProperties());
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStringOnCanvasCreatesResourceTest() {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(content, canvas);
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStringOnCanvasWithPropsCreatesResourceTest() {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(content, canvas, new DummySvgConverterProperties());
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnCanvasCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(is, canvas);
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnCanvasWithPropsCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(is, canvas, new DummySvgConverterProperties());
        Assertions.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void convertStringToXObjectCreatesNoResourceTest() {
        SvgConverter.convertToXObject(content, doc);
        Assertions.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }

    @Test
    public void convertStringToXObjectWithPropsCreatesNoResourceTest() {
        SvgConverter.convertToXObject(content, doc, new DummySvgConverterProperties());
        Assertions.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }

    @Test
    public void convertStreamToXObjectCreatesNoResourceTest() throws IOException {
        SvgConverter.convertToXObject(is, doc);
        Assertions.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }

    @Test
    public void convertStreamToXObjectWithPropsCreatesNoResourceTest() throws IOException {
        SvgConverter.convertToXObject(is, doc, new DummySvgConverterProperties());
        Assertions.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }
    
    @Test
    public void processNodeWithCustomFactory() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        DummySvgConverterProperties props = new DummySvgConverterProperties();
        IBranchSvgNodeRenderer node = (IBranchSvgNodeRenderer) SvgConverter.process(svg, props).getRootRenderer();
        Assertions.assertTrue(node instanceof DummySvgNodeRenderer);
        Assertions.assertEquals(0, node.getChildren().size());
        Assertions.assertNull(node.getParent());
    }

    @Test
    public void processNode() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        IBranchSvgNodeRenderer node = (IBranchSvgNodeRenderer) SvgConverter.process(svg, null).getRootRenderer();
        Assertions.assertTrue(node instanceof SvgTagSvgNodeRenderer);
        Assertions.assertEquals(0, node.getChildren().size());
        Assertions.assertNull(node.getParent());
    }

    @Test
    public void parseString() {
        INode actual = SvgConverter.parse(content);

        Assertions.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assertions.assertEquals("svg", node.name());
        Assertions.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStream() throws IOException {
        INode actual = SvgConverter.parse(is);

        Assertions.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assertions.assertEquals("svg", node.name());
        Assertions.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStreamWithProps() throws IOException {
        INode actual = SvgConverter.parse(is, new DummySvgConverterProperties());

        Assertions.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assertions.assertEquals("svg", node.name());
        Assertions.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStreamErrorEncodingTooBig() throws IOException {
        is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_16LE));
        INode actual = SvgConverter.parse(is, new DummySvgConverterProperties()); // defaults to UTF-8

        Assertions.assertEquals(1, actual.childNodes().size());
        // Does not throw an exception, but produces gibberish output that gets fed into a Text element, which is not a JsoupElementNode
        Assertions.assertFalse(actual.childNodes().get(0) instanceof JsoupElementNode);
    }

    private static class OtherCharsetDummySvgConverterProperties extends DummySvgConverterProperties {

        @Override
        public String getCharset() {
            return "UTF-16LE";
        }
    }

    @Test
    public void parseStreamWithOtherEncoding() throws IOException {
        is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_16LE));
        INode actual = SvgConverter.parse(is, new OtherCharsetDummySvgConverterProperties());

        Assertions.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assertions.assertEquals("svg", node.name());
        Assertions.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStreamErrorOtherCharset() throws IOException {
        INode actual = SvgConverter.parse(is, new OtherCharsetDummySvgConverterProperties());

        Assertions.assertEquals(1, actual.childNodes().size());
        // Does not throw an exception, but produces gibberish output that gets fed into a Text element, which is not a JsoupElementNode
        Assertions.assertFalse(actual.childNodes().get(0) instanceof JsoupElementNode);
    }

    @Test
    public void checkNullTest(){
        Exception e = Assertions.assertThrows(SvgProcessingException.class,
                () -> SvgConverter.drawOnDocument("test",null,1)
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.PARAMETER_CANNOT_BE_NULL, e.getMessage());
    }

    @Test
    public void resourceResolverInstanceTest() {
        DummySvgConverterProperties properties = new DummySvgConverterProperties();
        SvgProcessorContext context = new SvgProcessorContext(properties);
        ResourceResolver initialResolver = context.getResourceResolver();
        SvgProcessorResult svgProcessorResult = new SvgProcessorResult(new HashMap<String, ISvgNodeRenderer>(),
                new SvgTagSvgNodeRenderer(), context);

        ResourceResolver currentResolver = SvgConverter.getResourceResolver(svgProcessorResult, properties);
        Assertions.assertEquals(initialResolver, currentResolver);
    }

    @Test
    public void createResourceResolverWithoutProcessorResultTest() {
        ISvgConverterProperties props = new SvgConverterProperties();
        Assertions.assertNotNull(SvgConverter.getResourceResolver(null, props));
    }

    @Test
    public void resourceResolverInstanceCustomResolverTest() {
        DummySvgConverterProperties properties = new DummySvgConverterProperties();
        TestSvgProcessorResult testSvgProcessorResult = new TestSvgProcessorResult();

        ResourceResolver currentResolver = SvgConverter.getResourceResolver(testSvgProcessorResult, properties);
        Assertions.assertNotNull(currentResolver);
    }

    @Test
    public void resourceResolverInstanceCustomResolverNullPropsTest() {
        TestSvgProcessorResult testSvgProcessorResult = new TestSvgProcessorResult();

        ResourceResolver currentResolver = SvgConverter.getResourceResolver(testSvgProcessorResult, null);
        Assertions.assertNotNull(currentResolver);
    }

    @Test
    public void nullBBoxInDrawTest() throws Exception {
        Assertions.assertThrows(PdfException.class, () -> {
            PdfFormXObject object = SvgConverter.convertToXObject(content, doc);
            ((PdfDictionary)object.getPdfObject()).remove(PdfName.BBox);
            SvgConverter.draw(object, new PdfCanvas(doc, 1), 0, 0);
        });
    }

    private static class TestSvgProcessorResult implements ISvgProcessorResult {

        public TestSvgProcessorResult() {
        }

        @Override
        public Map<String, ISvgNodeRenderer> getNamedObjects() {
            return null;
        }

        @Override
        public ISvgNodeRenderer getRootRenderer() {
            return null;
        }

        @Override
        public FontProvider getFontProvider() {
            return null;
        }

        @Override
        public FontSet getTempFonts() {
            return null;
        }
    }
}
