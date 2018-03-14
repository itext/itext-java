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
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgSvgNodeRenderer;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SvgConverterUnitTest {

    // we cannot easily mock the PdfDocument, so we make do with as close to unit testing as we can
    private PdfDocument doc;
    private final String content = "<svg width=\"10\" height=\"10\"/>";
    private InputStream is;

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
        ISvgNodeRenderer node = SvgConverter.process(svg, props);
        Assert.assertTrue(node instanceof DummySvgNodeRenderer);
        Assert.assertEquals(0, node.getChildren().size());
        Assert.assertNull(node.getParent());
    }

    @Test
    public void processNode() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        ISvgNodeRenderer node = SvgConverter.process(svg);
        Assert.assertTrue(node instanceof SvgSvgNodeRenderer);
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
}
