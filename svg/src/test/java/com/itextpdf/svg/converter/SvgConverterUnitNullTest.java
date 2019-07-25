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
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

/**
 * These tests will make sure that a NullPointerException is never thrown: if a
 * null check is made, an SVG-specific exception should tell the user where the
 * null check failed.
 *
 * If the (optional) {@link ISvgConverterProperties} parameter is null, this
 * should NOT throw an exception as this is caught in the library.
 */
@Category(UnitTest.class)
public class SvgConverterUnitNullTest extends ExtendedITextTest {

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

    @Test
    public void drawOnDocumentStringNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnDocument((String) null, doc, 1);
    }

    @Test
    public void drawOnDocumentInputStreamNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnDocument((InputStream) null, doc, 1);
    }

    @Test
    public void drawOnDocumentDocNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnDocument(is, null, 1);
    }

    @Test
    public void drawOnDocumentAllNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnDocument((String) null, null, 1);
    }

    @Test
    public void drawOnDocumentAllNullTest2() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnDocument((InputStream) null, null, 1);
    }

    @Test
    public void drawOnDocumentStringPropsNullTest() {
        SvgConverter.drawOnDocument(content, doc, 1, null);
    }

    @Test
    public void drawOnDocumentInputStreamPropsNullTest() throws IOException {
        SvgConverter.drawOnDocument(is, doc, 1, null);
    }

    @Test
    public void drawOnPageStringNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        PdfPage page = doc.getFirstPage();
        SvgConverter.drawOnPage((String) null, page);
    }

    @Test
    public void drawOnPageInputStreamNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        PdfPage page = doc.getFirstPage();
        SvgConverter.drawOnPage((InputStream) null, page);
    }

    @Test
    public void drawOnPageDocNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnPage(is, null);
    }

    @Test
    public void drawOnPageAllNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnPage((String) null, null);
    }

    @Test
    public void drawOnPageAllNullTest2() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnPage((InputStream) null, null);
    }

    @Test
    public void drawOnPageStringPropsNullTest() {
        PdfPage page = doc.getFirstPage();
        SvgConverter.drawOnPage(content, page, null);
    }

    @Test
    public void drawOnPageInputStreamPropsNullTest() throws IOException {
        PdfPage page = doc.getFirstPage();
        SvgConverter.drawOnPage(is, page, null);
    }

    @Test
    public void drawOnCanvasStringNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
        SvgConverter.drawOnCanvas((String) null, canvas);
    }

    @Test
    public void drawOnCanvasInputStreamNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
        SvgConverter.drawOnCanvas((InputStream) null, canvas);
    }

    @Test
    public void drawOnCanvasDocNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnCanvas(is, null);
    }

    @Test
    public void drawOnCanvasAllNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnCanvas((String) null, null);
    }

    @Test
    public void drawOnCanvasAllNullTest2() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.drawOnCanvas((InputStream) null, null);
    }

    @Test
    public void drawOnCanvasStringPropsNullTest() {
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
        SvgConverter.drawOnCanvas(content, canvas, null);
    }

    @Test
    public void drawOnCanvasInputStreamPropsNullTest() throws IOException {
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
        SvgConverter.drawOnCanvas(is, canvas, null);
    }

    @Test
    public void convertToXObjectStringNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject((String) null, doc);
    }

    @Test
    public void convertToXObjectInputStreamNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject((InputStream) null, doc);
    }

    @Test
    public void convertToXObjectRendererNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject((ISvgNodeRenderer) null, doc);
    }

    @Test
    public void convertToXObjectDocWithStringNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject(is, null);
    }

    @Test
    public void convertToXObjectDocWithStreamNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject(is, null);
    }

    @Test
    public void convertToXObjectDocWithRendererNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        ISvgNodeRenderer renderer = SvgConverter.process(SvgConverter.parse(is)).getRootRenderer();
        SvgConverter.convertToXObject(renderer, null);
    }

    @Test
    public void convertToXObjectAllWithStringNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject((String) null, null);
    }

    @Test
    public void convertToXObjectAllWithStreamNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject((InputStream) null, null);
    }

    @Test
    public void convertToXObjectAllWithRendererNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.convertToXObject((ISvgNodeRenderer) null, null);
    }

    @Test
    public void convertToXObjectStringPropsNullTest() {
        SvgConverter.convertToXObject(content, doc, null);
    }

    @Test
    public void convertToXObjectInputStreamPropsNullTest() throws IOException {
        SvgConverter.convertToXObject(is, doc, null);
    }

    @Test
    public void parseStringNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.parse((String) null);
    }

    @Test
    public void parseStreamNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.parse((InputStream) null);
    }

    @Test
    public void parseStreamPropsNullTest() throws IOException {
        SvgConverter.parse(is, null);
    }

    @Test
    public void parseStringPropsNullTest() throws IOException {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.parse(null, null);
    }

    @Test
    public void processNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.process(null);
    }

    @Test
    public void processAllNullTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        SvgConverter.process(null, null);
    }

    @Test
    public void processPropsNullTest() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        SvgConverter.process(svg, null);
    }
}
