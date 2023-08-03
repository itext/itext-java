/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnDocument((String) null, doc, 1));
    }

    @Test
    public void drawOnDocumentInputStreamNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class,
                () -> SvgConverter.drawOnDocument((InputStream) null, doc, 1)
        );
    }

    @Test
    public void drawOnDocumentDocNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnDocument(is, null, 1));
    }

    @Test
    public void drawOnDocumentAllNullTest() {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnDocument((String) null, null, 1));
    }

    @Test
    public void drawOnDocumentAllNullTest2() throws IOException {
        Assert.assertThrows(SvgProcessingException.class,
                () -> SvgConverter.drawOnDocument((InputStream) null, null, 1)
        );
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
        PdfPage page = doc.getFirstPage();
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnPage((String) null, page));
    }

    @Test
    public void drawOnPageInputStreamNullTest() throws IOException {
        PdfPage page = doc.getFirstPage();
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnPage((InputStream) null, page));
    }

    @Test
    public void drawOnPageDocNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnPage(is, null));
    }

    @Test
    public void drawOnPageAllNullTest() {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnPage((String) null, null));
    }

    @Test
    public void drawOnPageAllNullTest2() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnPage((InputStream) null, null));
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
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnDocument((String) null, doc, 1));
    }

    @Test
    public void drawOnCanvasInputStreamNullTest() throws IOException {
        PdfCanvas canvas = new PdfCanvas(doc.getLastPage());
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnCanvas((InputStream) null, canvas));
    }

    @Test
    public void drawOnCanvasDocNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnCanvas(is, null));
    }

    @Test
    public void drawOnCanvasAllNullTest() {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnCanvas((String) null, null));
    }

    @Test
    public void drawOnCanvasAllNullTest2() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.drawOnCanvas((InputStream) null, null));
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
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject((String) null, doc));
    }

    @Test
    public void convertToXObjectInputStreamNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject((InputStream) null, doc));
    }

    @Test
    public void convertToXObjectRendererNullTest() {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject((ISvgNodeRenderer) null, doc));
    }

    @Test
    public void convertToXObjectDocWithStringNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject(is, null));
    }

    @Test
    public void convertToXObjectDocWithStreamNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject(is, null));
    }

    @Test
    public void convertToXObjectDocWithRendererNullTest() throws IOException {
        ISvgNodeRenderer renderer = SvgConverter.process(SvgConverter.parse(is), null).getRootRenderer();
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject(renderer, null));
    }

    @Test
    public void convertToXObjectAllWithStringNullTest() {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject((String) null, null));
    }

    @Test
    public void convertToXObjectAllWithStreamNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject((InputStream) null, null));
    }

    @Test
    public void convertToXObjectAllWithRendererNullTest() {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.convertToXObject((ISvgNodeRenderer) null, null));
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
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.parse((String) null));
    }

    @Test
    public void parseStreamNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.parse((InputStream) null));
    }

    @Test
    public void parseStreamPropsNullTest() throws IOException {
        SvgConverter.parse(is, null);
    }

    @Test
    public void parseStringPropsNullTest() throws IOException {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.parse(null, null));
    }

    @Test
    public void processAllNullTest() {
        Assert.assertThrows(SvgProcessingException.class, () -> SvgConverter.process(null, null));
    }

    @Test
    public void processPropsNullTest() {
        INode svg = new JsoupElementNode(new Element(Tag.valueOf("svg"), ""));
        SvgConverter.process(svg, null);
    }
}
