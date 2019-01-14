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
package com.itextpdf.svg.converter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.dummy.processors.impl.DummySvgConverterProperties;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SvgConverterUnitTest {

    // we cannot easily mock the PdfDocument, so we make do with as close to unit testing as we can
    private PdfDocument doc;
    private final String content = "<svg width=\"10\" height=\"10\"/>";
    private InputStream is;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();


    @Before
    public void setup() {
        doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @After
    public void teardown() {
        doc.close();
    }

    private void testResourceCreated(PdfDocument doc, int pageNo) {
        PdfResources res = doc.getPage(pageNo).getResources();
        Assert.assertEquals(1, res.getPdfObject().size());
        for (PdfName name : res.getResourceNames()) {
            PdfObject obj = res.getResourceObject(PdfName.XObject, name);
            Assert.assertTrue(obj.isStream());
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
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStringOnPageWithPropsCreatesResourceTest() {
        PdfPage page = doc.addNewPage();
        SvgConverter.drawOnPage(content, page, new DummySvgConverterProperties());
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnPageCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        SvgConverter.drawOnPage(is, page);
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnPageWithPropsCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        SvgConverter.drawOnPage(is, page, new DummySvgConverterProperties());
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStringOnCanvasCreatesResourceTest() {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(content, canvas);
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStringOnCanvasWithPropsCreatesResourceTest() {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(content, canvas, new DummySvgConverterProperties());
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnCanvasCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(is, canvas);
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void drawStreamOnCanvasWithPropsCreatesResourceTest() throws IOException {
        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        SvgConverter.drawOnCanvas(is, canvas, new DummySvgConverterProperties());
        Assert.assertEquals(0, doc.getFirstPage().getResources().getPdfObject().size());
        testResourceCreated(doc, 2);
    }

    @Test
    public void convertStringToXObjectCreatesNoResourceTest() {
        SvgConverter.convertToXObject(content, doc);
        Assert.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }

    @Test
    public void convertStringToXObjectWithPropsCreatesNoResourceTest() {
        SvgConverter.convertToXObject(content, doc, new DummySvgConverterProperties());
        Assert.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }

    @Test
    public void convertStreamToXObjectCreatesNoResourceTest() throws IOException {
        SvgConverter.convertToXObject(is, doc);
        Assert.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }

    @Test
    public void convertStreamToXObjectWithPropsCreatesNoResourceTest() throws IOException {
        SvgConverter.convertToXObject(is, doc, new DummySvgConverterProperties());
        Assert.assertEquals(0, doc.getLastPage().getResources().getPdfObject().size());
    }
    
    @Test
    public void processNodeWithCustomFactory() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        DummySvgConverterProperties props = new DummySvgConverterProperties();
        IBranchSvgNodeRenderer node = (IBranchSvgNodeRenderer) SvgConverter.process(svg, props).getRootRenderer();
        Assert.assertTrue(node instanceof DummySvgNodeRenderer);
        Assert.assertEquals(0, node.getChildren().size());
        Assert.assertNull(node.getParent());
    }

    @Test
    public void processNode() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        IBranchSvgNodeRenderer node = (IBranchSvgNodeRenderer) SvgConverter.process(svg).getRootRenderer();
        Assert.assertTrue(node instanceof SvgTagSvgNodeRenderer);
        Assert.assertEquals(0, node.getChildren().size());
        Assert.assertNull(node.getParent());
    }

    @Test
    public void parseString() {
        INode actual = SvgConverter.parse(content);

        Assert.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assert.assertEquals("svg", node.name());
        Assert.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStream() throws IOException {
        INode actual = SvgConverter.parse(is);

        Assert.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assert.assertEquals("svg", node.name());
        Assert.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStreamWithProps() throws IOException {
        INode actual = SvgConverter.parse(is, new DummySvgConverterProperties());

        Assert.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assert.assertEquals("svg", node.name());
        Assert.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStreamErrorEncodingTooBig() throws IOException {
        is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_16LE));
        INode actual = SvgConverter.parse(is, new DummySvgConverterProperties()); // defaults to UTF-8

        Assert.assertEquals(1, actual.childNodes().size());
        // Does not throw an exception, but produces gibberish output that gets fed into a Text element, which is not a JsoupElementNode
        Assert.assertFalse(actual.childNodes().get(0) instanceof JsoupElementNode);
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

        Assert.assertEquals(1, actual.childNodes().size());
        JsoupElementNode node = (JsoupElementNode) actual.childNodes().get(0);
        Assert.assertEquals("svg", node.name());
        Assert.assertEquals(0, node.childNodes().size());
    }

    @Test
    public void parseStreamErrorOtherCharset() throws IOException {
        INode actual = SvgConverter.parse(is, new OtherCharsetDummySvgConverterProperties());

        Assert.assertEquals(1, actual.childNodes().size());
        // Does not throw an exception, but produces gibberish output that gets fed into a Text element, which is not a JsoupElementNode
        Assert.assertFalse(actual.childNodes().get(0) instanceof JsoupElementNode);
    }

    @Test
    public void checkNullTest(){
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.PARAMETER_CANNOT_BE_NULL);
        SvgConverter.drawOnDocument("test",null,1);

    }
}
