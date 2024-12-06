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
package com.itextpdf.svg.converter;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.Image;
import com.itextpdf.styledxmlparser.IXmlParser;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgProcessorResult;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.PdfRootSvgNodeRenderer;
import com.itextpdf.svg.utils.SvgCssUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main container class for static methods that do high-level
 * conversion operations from input to PDF, either by drawing on a canvas or by
 * returning an XObject, which can then be used by the calling class for further
 * processing and drawing operations.
 */
public final class SvgConverter {

    private SvgConverter() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SvgConverter.class);


    private static void checkNull(Object o) {
        if (o == null) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.PARAMETER_CANNOT_BE_NULL);
        }
    }

    /**
     * Draws a String containing valid SVG to a document, on a given page
     * number at the origin of the page.
     *
     * @param content  the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     */
    public static void drawOnDocument(String content, PdfDocument document, int pageNo) {
        drawOnDocument(content, document, pageNo, 0, 0);
    }

    /**
     * Draws a String containing valid SVG to a document, on a given page
     * number on the provided x and y coordinate.
     *
     * @param content  the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     * @param x        x-coordinate of the location to draw at
     * @param y        y-coordinate of the location to draw at
     */
    public static void drawOnDocument(String content, PdfDocument document, int pageNo, float x, float y) {
        checkNull(document);
        drawOnPage(content, document.getPage(pageNo), x, y);
    }

    /**
     * Draws a String containing valid SVG to a document, on a given page
     * number on the provided x and y coordinate.
     *
     * @param content  the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     * @param props    a container for extra properties that customize the behavior
     */
    public static void drawOnDocument(String content, PdfDocument document, int pageNo, ISvgConverterProperties props) {
        drawOnDocument(content, document, pageNo, 0, 0, props);
    }

    /**
     * Draws a String containing valid SVG to a document, on a given page
     * number on the provided x and y coordinate.
     *
     * @param content  the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     * @param x        x-coordinate of the location to draw at
     * @param y        y-coordinate of the location to draw at
     * @param props    a container for extra properties that customize the behavior
     */
    public static void drawOnDocument(String content, PdfDocument document, int pageNo, float x, float y, ISvgConverterProperties props) {
        checkNull(document);
        drawOnPage(content, document.getPage(pageNo), x, y, props);
    }

    /**
     * Draws a Stream containing valid SVG to a document, on a given page
     * number ate the origni of the page.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnDocument(InputStream stream, PdfDocument document, int pageNo) throws IOException {
        drawOnDocument(stream, document, pageNo, 0, 0);
    }

    /**
     * Draws a Stream containing valid SVG to a document, on a given page
     * number on the provided x and y coordinate.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     * @param x        x-coordinate of the location to draw at
     * @param y        y-coordinate of the location to draw at
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnDocument(InputStream stream, PdfDocument document, int pageNo, float x, float y) throws IOException {
        checkNull(document);
        drawOnPage(stream, document.getPage(pageNo), x, y);
    }

    /**
     * Draws a Stream containing valid SVG to a document, on a given page
     * number on the provided x and y coordinate.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     * @param props    a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnDocument(InputStream stream, PdfDocument document, int pageNo, ISvgConverterProperties props) throws IOException {
        drawOnDocument(stream, document, pageNo, 0, 0, props);
    }

    /**
     * Draws a Stream containing valid SVG to a document, on a given page
     * number on the provided x and y coordinate.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo   the page to draw on
     * @param x        x-coordinate of the location to draw at
     * @param y        y-coordinate of the location to draw at
     * @param props    a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnDocument(InputStream stream, PdfDocument document, int pageNo, float x, float y, ISvgConverterProperties props) throws IOException {
        checkNull(document);
        drawOnPage(stream, document.getPage(pageNo), x, y, props);
    }

    /**
     * Draws a String containing valid SVG to a given page at the origin of the page.
     *
     * @param content the String value containing valid SVG content
     * @param page    the {@link PdfPage} instance to draw on
     */
    public static void drawOnPage(String content, PdfPage page) {
        drawOnPage(content, page, 0, 0);
    }

    /**
     * Draws a String containing valid SVG to a given page on the provided x and y coordinate.
     *
     * @param content the String value containing valid SVG content
     * @param page    the {@link PdfPage} instance to draw on
     * @param x       x-coordinate of the location to draw at
     * @param y       y-coordinate of the location to draw at
     */
    public static void drawOnPage(String content, PdfPage page, float x, float y) {
        checkNull(page);
        drawOnCanvas(content, new PdfCanvas(page), x, y);
    }


    /**
     * Draws a String containing valid SVG to a given page on the provided x and y coordinate.
     *
     * @param content the String value containing valid SVG content
     * @param page    the {@link PdfPage} instance to draw on
     * @param props   a container for extra properties that customize the behavior
     */
    public static void drawOnPage(String content, PdfPage page, ISvgConverterProperties props) {
        drawOnPage(content, page, 0, 0, props);
    }

    /**
     * Draws a String containing valid SVG to a given page on the provided x and y coordinate.
     *
     * @param content the String value containing valid SVG content
     * @param page    the {@link PdfPage} instance to draw on
     * @param x       x-coordinate of the location to draw at
     * @param y       y-coordinate of the location to draw at
     * @param props   a container for extra properties that customize the behavior
     */
    public static void drawOnPage(String content, PdfPage page, float x, float y, ISvgConverterProperties props) {
        checkNull(page);
        drawOnCanvas(content, new PdfCanvas(page), x, y, props);
    }

    /**
     * Draws a Stream containing valid SVG to a given page at coordinate 0,0.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param page   the {@link PdfPage} instance to draw on
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnPage(InputStream stream, PdfPage page) throws IOException {
        drawOnPage(stream, page, 0, 0);
    }

    /**
     * Draws a Stream containing valid SVG to a given page, at a given location.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param page   the {@link PdfPage} instance to draw on
     * @param x      x-coordinate of the location to draw at
     * @param y      y-coordinate of the location to draw at
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnPage(InputStream stream, PdfPage page, float x, float y) throws IOException {
        checkNull(page);
        drawOnCanvas(stream, new PdfCanvas(page), x, y);
    }

    /**
     * Draws a Stream containing valid SVG to a given page at a given location.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param page   the {@link PdfPage} instance to draw on
     * @param props  a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnPage(InputStream stream, PdfPage page, ISvgConverterProperties props) throws IOException {
        drawOnPage(stream, page, 0, 0, props);
    }

    /**
     * Draws a Stream containing valid SVG to a given page at a given location.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param page   the {@link PdfPage} instance to draw on
     * @param x      x-coordinate of the location to draw at
     * @param y      y-coordinate of the location to draw at
     * @param props  a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnPage(InputStream stream, PdfPage page, float x, float y, ISvgConverterProperties props) throws IOException {
        checkNull(page);
        if (props instanceof SvgConverterProperties && ((SvgConverterProperties) props).getCustomViewport() == null) {
            ((SvgConverterProperties) props).setCustomViewport(page.getMediaBox());
        }
        drawOnCanvas(stream, new PdfCanvas(page), x, y, props);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object.
     *
     * @param content the String value containing valid SVG content
     * @param canvas  the {@link PdfCanvas} instance to draw on
     */
    public static void drawOnCanvas(String content, PdfCanvas canvas) {
        drawOnCanvas(content, canvas, 0, 0);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object.
     *
     * @param content the String value containing valid SVG content
     * @param canvas  the {@link PdfCanvas} instance to draw on
     * @param x       x-coordinate of the location to draw at
     * @param y       y-coordinate of the location to draw at
     */
    public static void drawOnCanvas(String content, PdfCanvas canvas, float x, float y) {
        checkNull(canvas);
        draw(convertToXObject(content, canvas.getDocument()), canvas, x, y);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object.
     *
     * @param content the String value containing valid SVG content
     * @param canvas  the {@link PdfCanvas} instance to draw on
     * @param props   a container for extra properties that customize the behavior
     */
    public static void drawOnCanvas(String content, PdfCanvas canvas, ISvgConverterProperties props) {
        drawOnCanvas(content, canvas, 0, 0, props);
    }

    /**
     * draws a String containing valid SVG to a pre-made canvas object, at a specified location.
     *
     * @param content the String value containing valid SVG content
     * @param canvas  the {@link PdfCanvas} instance to draw on
     * @param x       x-coordinate of the location to draw at
     * @param y       y-coordinate of the location to draw at
     * @param props   a container for extra properties that customize the behavior
     */
    public static void drawOnCanvas(String content, PdfCanvas canvas, float x, float y, ISvgConverterProperties props) {
        checkNull(canvas);
        draw(convertToXObject(content, canvas.getDocument(), props), canvas, x, y);
    }

    /**
     * Draws a Stream containing valid SVG to a pre-made canvas object.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnCanvas(InputStream stream, PdfCanvas canvas) throws IOException {
        drawOnCanvas(stream, canvas, 0, 0);
    }

    /**
     * Draws a Stream containing valid SVG to a pre-made canvas object, to a specified location.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     * @param x      x-coordinate of the location to draw at
     * @param y      y-coordinate of the location to draw at
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnCanvas(InputStream stream, PdfCanvas canvas, float x, float y) throws IOException {
        checkNull(canvas);
        draw(convertToXObject(stream, canvas.getDocument()), canvas, x, y);
    }

    /**
     * Draws a Stream containing valid SVG to a pre-made canvas object.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     * @param props  a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnCanvas(InputStream stream, PdfCanvas canvas, ISvgConverterProperties props) throws IOException {
        drawOnCanvas(stream, canvas, 0, 0, props);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object, at a specified position on the canvas.
     *
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     * @param x      x-coordinate of the location to draw at
     * @param y      y-coordinate of the location to draw at
     * @param props  a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnCanvas(InputStream stream, PdfCanvas canvas, float x, float y, ISvgConverterProperties props) throws IOException {
        checkNull(canvas);
        draw(convertToXObject(stream, canvas.getDocument(), props), canvas, x, y);
    }

    /**
     * Converts SVG stored in a {@link File} to a PDF {@link File}.
     *
     * @param svgFile the {@link File} containing the source SVG
     * @param pdfFile the {@link File} containing the resulting PDF
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void createPdf(File svgFile, File pdfFile) throws IOException {
        createPdf(svgFile, pdfFile, null, null);
    }

    /**
     * Converts SVG stored in a {@link File} to a PDF {@link File},
     * using specific {@link ISvgConverterProperties}.
     *
     * @param svgFile the {@link File} containing the source SVG
     * @param pdfFile the {@link File} containing the resulting PDF
     * @param props   a {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void createPdf(File svgFile, File pdfFile, ISvgConverterProperties props) throws IOException {
        createPdf(svgFile, pdfFile, props, null);
    }

    /**
     * Converts SVG stored in a {@link File} to a PDF {@link File},
     * using {@link WriterProperties}
     *
     * @param svgFile     the {@link File} containing the source SVG
     * @param pdfFile     the {@link File} containing the resulting PDF
     * @param writerProps the{@link WriterProperties} for the pdf document
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void createPdf(File svgFile, File pdfFile, WriterProperties writerProps) throws IOException {
        createPdf(svgFile, pdfFile, null, writerProps);
    }

    /**
     * Converts SVG stored in a {@link File} to a PDF {@link File},
     * using specific {@link ISvgConverterProperties} and {@link WriterProperties}.
     *
     * @param svgFile     the {@link File} containing the source SVG
     * @param pdfFile     the {@link File} containing the resulting PDF
     * @param props       a {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @param writerProps a {@link WriterProperties} for the pdf document
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void createPdf(File svgFile, File pdfFile, ISvgConverterProperties props, WriterProperties writerProps) throws IOException {
        if (props == null) {
            props = new SvgConverterProperties().setBaseUri(FileUtil.getParentDirectoryUri(svgFile));
        } else if (props.getBaseUri() == null || props.getBaseUri().isEmpty()) {
            String baseUri = FileUtil.getParentDirectoryUri(svgFile);
            props = convertToSvgConverterProps(props, baseUri);
        }
        try (InputStream fileInputStream = FileUtil.getInputStreamForFile(svgFile.getAbsolutePath());
                OutputStream fileOutputStream = FileUtil.getFileOutputStream(pdfFile.getAbsolutePath())) {
            createPdf(fileInputStream, fileOutputStream, props, writerProps);
        }
    }

    /**
     * Copies properties from custom ISvgConverterProperties into new SvgConverterProperties.
     * Since ISvgConverterProperties itself is immutable we have to do it.
     *
     * @param props {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @param baseUri the directory of new SvgConverterProperties
     * @return new SvgConverterProperties.
     */
    private static SvgConverterProperties convertToSvgConverterProps(ISvgConverterProperties props, String baseUri) {
        return new SvgConverterProperties().setBaseUri(baseUri)
                .setMediaDeviceDescription(props.getMediaDeviceDescription())
                .setFontProvider(props.getFontProvider())
                .setCharset(props.getCharset())
                .setRendererFactory(props.getRendererFactory());
    }

    /**
     * Create a single page pdf containing the SVG on its page using the default processing and drawing logic
     *
     * @param svgStream {@link InputStream Stream} containing the SVG
     * @param pdfDest   PDF destination outputStream
     * @throws IOException when the one of the streams cannot be read correctly
     */
    public static void createPdf(InputStream svgStream, OutputStream pdfDest) throws IOException {
        createPdf(svgStream, pdfDest, null, null);
    }

    /**
     * Create a single page pdf containing the SVG on its page using the default processing and drawing logic
     *
     * @param svgStream   {@link InputStream Stream} containing the SVG
     * @param pdfDest     PDF destination outputStream
     * @param writerProps writer properties for the pdf document
     * @throws IOException when the one of the streams cannot be read correctly
     */
    public static void createPdf(InputStream svgStream, OutputStream pdfDest, WriterProperties writerProps) throws IOException {
        createPdf(svgStream, pdfDest, null, writerProps);
    }

    /**
     * Create a single page pdf containing the SVG on its page using the default processing and drawing logic
     *
     * @param svgStream {@link InputStream Stream} containing the SVG
     * @param pdfDest   PDF destination outputStream
     * @param props     {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @throws IOException when the one of the streams cannot be read correctly
     */
    public static void createPdf(InputStream svgStream, OutputStream pdfDest, ISvgConverterProperties props) throws IOException {
        createPdf(svgStream, pdfDest, props, null);
    }

    /**
     * Create a single page pdf containing the SVG on its page using the default processing and drawing logic
     *
     * @param svgStream   {@link InputStream Stream} containing the SVG
     * @param pdfDest     PDF destination outputStream
     * @param props       {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @param writerProps {@link WriterProperties}  for the pdf document
     * @throws IOException when the one of the streams cannot be read correctly
     */
    public static void createPdf(InputStream svgStream, OutputStream pdfDest, ISvgConverterProperties props, WriterProperties writerProps) throws IOException {
        // Create doc
        if (writerProps == null) {
            writerProps = new WriterProperties();
        }
        try (PdfWriter writer = new PdfWriter(pdfDest, writerProps);
                PdfDocument pdfDocument = new PdfDocument(writer)) {
            // Process
            ISvgProcessorResult processorResult = process(parse(svgStream, props), props);

            ResourceResolver resourceResolver = SvgConverter.getResourceResolver(processorResult, props);
            final SvgDrawContext drawContext = new SvgDrawContext(resourceResolver, processorResult.getFontProvider());
            if (processorResult instanceof SvgProcessorResult) {
                drawContext.setCssContext(((SvgProcessorResult) processorResult).getContext().getCssContext());
            }

            drawContext.addNamedObjects(processorResult.getNamedObjects());
            // Add temp fonts
            drawContext.setTempFonts(processorResult.getTempFonts());

            ISvgNodeRenderer topSvgRenderer = processorResult.getRootRenderer();
            // Extract topmost dimensions
            checkNull(topSvgRenderer);
            checkNull(pdfDocument);

            // Since svg is a single object in the document, em = rem
            float em = drawContext.getCssContext().getRootFontSize();
            Rectangle wh = SvgCssUtils.extractWidthAndHeight(topSvgRenderer, em, drawContext);

            // Adjust pagesize and create new page
            pdfDocument.setDefaultPageSize(new PageSize(wh.getWidth(), wh.getHeight()));
            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas pageCanvas = new PdfCanvas(page);
            // Add to the first page
            PdfFormXObject xObject = convertToXObject(topSvgRenderer, pdfDocument, drawContext);
            // Draw
            draw(xObject, pageCanvas);
        }
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param content  the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     *         corresponding to the passed SVG content
     */
    public static PdfFormXObject convertToXObject(String content, PdfDocument document) {
        return convertToXObject(content, document, null);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param content  the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param props    {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     *         corresponding to the passed SVG content
     */
    public static PdfFormXObject convertToXObject(String content, PdfDocument document, ISvgConverterProperties props) {
        checkNull(content);
        checkNull(document);

        return convertToXObject(process(parse(content), props), document, props);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param props    {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     *         corresponding to the passed SVG content
     * @throws IOException when the stream cannot be read correctly
     */
    public static PdfFormXObject convertToXObject(InputStream stream, PdfDocument document, ISvgConverterProperties props) throws IOException {
        checkNull(stream);
        checkNull(document);

        return convertToXObject(process(parse(stream, props), props), document, props);
    }

    //Private converter for unification
    private static PdfFormXObject convertToXObject(ISvgProcessorResult processorResult, PdfDocument document,
            ISvgConverterProperties props) {
        ResourceResolver resourceResolver = SvgConverter.getResourceResolver(processorResult, props);
        final SvgDrawContext drawContext = new SvgDrawContext(resourceResolver, processorResult.getFontProvider());
        if (processorResult instanceof SvgProcessorResult) {
            drawContext.setCssContext(((SvgProcessorResult) processorResult).getContext().getCssContext());
        }
        if (props instanceof SvgConverterProperties) {
            drawContext.setCustomViewport(((SvgConverterProperties) props).getCustomViewport());
        }
        drawContext.setTempFonts(processorResult.getTempFonts());
        drawContext.addNamedObjects(processorResult.getNamedObjects());
        return convertToXObject(processorResult.getRootRenderer(), document, drawContext);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     *         corresponding to the passed SVG content
     * @throws IOException when the Stream cannot be read correctly
     */
    public static PdfFormXObject convertToXObject(InputStream stream, PdfDocument document) throws IOException {
        return convertToXObject(stream, document, null);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @return a {@link Image Image} containing the PDF instructions corresponding to the passed SVG content
     * @throws IOException when the Stream cannot be read correctly
     */
    public static Image convertToImage(InputStream stream, PdfDocument document) throws IOException {
        return new Image(convertToXObject(stream, document));
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link Image image} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param stream   the {@link InputStream Stream} containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param props    {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @return a {@link Image Image} containing the PDF instructions corresponding to the passed SVG content
     * @throws IOException when the Stream cannot be read correctly
     */
    public static Image convertToImage(InputStream stream, PdfDocument document, ISvgConverterProperties props) throws IOException {
        return new Image(convertToXObject(stream, document, props));
    }

    /*
     * This method is kept private, because there is little purpose in exposing it.
     */
    private static void draw(PdfFormXObject pdfForm, PdfCanvas canvas) {
        draw(pdfForm, canvas, 0, 0);
    }

    /*
     * This method is kept private, because there is little purpose in exposing it.
     */
    static void draw(PdfFormXObject pdfForm, PdfCanvas canvas, float x, float y) {
        canvas.addXObjectAt(
                pdfForm,
                x + (pdfForm.getBBox() == null ? 0 : pdfForm.getBBox().getAsNumber(0).floatValue()),
                y + (pdfForm.getBBox() == null ? 0 :pdfForm.getBBox().getAsNumber(1).floatValue()));
    }

    /**
     * This method draws a NodeRenderer tree to a canvas that is tied to the
     * passed document.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * this method, or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param topSvgRenderer the {@link ISvgNodeRenderer} instance that contains
     *                       the renderer tree
     * @param document       the document that the returned
     *                       {@link PdfFormXObject XObject} can be drawn on (on any given page
     *                       coordinates)
     * @return an {@link PdfFormXObject XObject}containing the PDF instructions
     *         corresponding to the passed node renderer tree.
     */
    public static PdfFormXObject convertToXObject(ISvgNodeRenderer topSvgRenderer, PdfDocument document) {
        return convertToXObject(topSvgRenderer, document, new SvgDrawContext(null, null));
    }

    /**
     * This method draws a NodeRenderer tree to a canvas that is tied to the
     * passed document.
     * <p>
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     * <p>
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * this method, or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param topSvgRenderer the {@link ISvgNodeRenderer} instance that contains
     *                       the renderer tree
     * @param document       the document that the returned
     * @param context        the SvgDrawContext
     * @return an {@link PdfFormXObject XObject}containing the PDF instructions
     *         corresponding to the passed node renderer tree.
     */
    private static PdfFormXObject convertToXObject(ISvgNodeRenderer topSvgRenderer, PdfDocument document, SvgDrawContext context) {
        checkNull(topSvgRenderer);
        checkNull(document);
        checkNull(context);

        // Can't determine em value here, so em=rem
        float em = context.getCssContext().getRootFontSize();
        Rectangle bbox = SvgCssUtils.extractWidthAndHeight(topSvgRenderer, em, context);

        PdfFormXObject pdfForm = new PdfFormXObject(bbox);
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        context.pushCanvas(canvas);
        ISvgNodeRenderer root = new PdfRootSvgNodeRenderer(topSvgRenderer);
        root.draw(context);
        return pdfForm;
    }

    /**
     * Parse and process an Inputstream containing an SVG, using the default Svg processor ({@link DefaultSvgProcessor})
     * The parsing of the stream is done using UTF-8 as the default charset.
     * The properties used by the processor are the {@link SvgConverterProperties}
     *
     * @param svgStream {@link InputStream Stream} containing the SVG to parse and process
     * @return {@link ISvgProcessorResult} containing the root renderer and metadata of the svg
     */
    public static ISvgProcessorResult parseAndProcess(InputStream svgStream) {
        return parseAndProcess(svgStream, null);
    }

    /**
     * Parse and process an Inputstream containing an SVG, using the default Svg processor ({@link DefaultSvgProcessor})
     *
     * @param svgStream {@link InputStream Stream} containing the SVG to parse and process
     * @param props     {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @return {@link ISvgProcessorResult} containing the root renderer and metadata of the svg
     */
    public static ISvgProcessorResult parseAndProcess(InputStream svgStream, ISvgConverterProperties props) {
        IXmlParser parser = new JsoupXmlParser();
        String charset = SvgConverter.tryToExtractCharset(props);
        INode nodeTree;
        try {
            nodeTree = parser.parse(svgStream, charset);
        } catch (Exception e) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.FAILED_TO_PARSE_INPUTSTREAM, e);
        }
        return new DefaultSvgProcessor().process(nodeTree, props);
    }

    /**
     * Use the default implementation of {@link ISvgProcessor} to convert an XML
     * DOM tree to a node renderer tree. The passed properties can modify the default behaviour
     *
     * @param root  the XML DOM tree
     * @param props {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @return a node renderer tree corresponding to the passed XML DOM tree
     */
    public static ISvgProcessorResult process(INode root, ISvgConverterProperties props) {
        checkNull(root);
        return new DefaultSvgProcessor().process(root, props);
    }

    /**
     * Parse a String containing valid SVG into an XML DOM node, using the
     * default JSoup XML parser.
     *
     * @param content the String value containing valid SVG content
     * @return an XML DOM tree corresponding to the passed String input
     */
    public static INode parse(String content) {
        checkNull(content);
        return new JsoupXmlParser().parse(content);
    }

    /**
     * Parse a Stream containing valid SVG into an XML DOM node, using the
     * default JSoup XML parser. This method will assume that the encoding of
     * the Stream is {@code UTF-8}.
     *
     * @param stream the {@link InputStream Stream} containing valid SVG content
     * @return an XML DOM tree corresponding to the passed String input
     * @throws IOException when the Stream cannot be read correctly
     */
    public static INode parse(InputStream stream) throws IOException {
        checkNull(stream);
        return parse(stream, null);
    }

    /**
     * Parse a Stream containing valid SVG into an XML DOM node, using the
     * default JSoup XML parser. This method will assume that the encoding of
     * the Stream is {@code UTF-8}, unless specified otherwise by the method
     * {@link ISvgConverterProperties#getCharset()} of the {@code props}
     * parameter.
     *
     * @param stream the {@link InputStream Stream} containing valid SVG content
     * @param props  {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @return an XML DOM tree corresponding to the passed String input
     * @throws IOException when the Stream cannot be read correctly
     */
    public static INode parse(InputStream stream, ISvgConverterProperties props) throws IOException {
        checkNull(stream); // props is allowed to be null
        IXmlParser xmlParser = new JsoupXmlParser();
        return xmlParser.parse(stream, SvgConverter.tryToExtractCharset(props));
    }

    /**
     * Extract width and height of the passed SVGNodeRenderer,
     * defaulting to respective viewbox values if either one is not present or
     * to browser default if viewbox is missing as well
     * <p>
     * Deprecated in favour of {@link SvgCssUtils#extractWidthAndHeight(ISvgNodeRenderer, float, SvgDrawContext)}
     *
     * @param topSvgRenderer the {@link ISvgNodeRenderer} instance that contains
     *                       the renderer tree
     * @return float[2], width is in position 0, height in position 1
     */
    @Deprecated
    public static float[] extractWidthAndHeight(ISvgNodeRenderer topSvgRenderer) {
        SvgDrawContext context = new SvgDrawContext(null, null);
        float em = context.getCssContext().getRootFontSize();
        Rectangle rectangle = SvgCssUtils.extractWidthAndHeight(topSvgRenderer, em, context);
        return new float[] {rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()};
    }

    static ResourceResolver getResourceResolver(ISvgProcessorResult processorResult, ISvgConverterProperties props) {
        if (processorResult instanceof SvgProcessorResult) {
            return ((SvgProcessorResult) processorResult).getContext().getResourceResolver();
        }
        return createResourceResolver(props);
    }

    /**
     * Tries to extract charset from {@link ISvgConverterProperties}.
     *
     * @param props {@link ISvgConverterProperties} an instance for extra properties to customize the behavior
     * @return charset | null
     */
    private static String tryToExtractCharset(final ISvgConverterProperties props) {
        return props != null ? props.getCharset() : null;
    }

    private static ResourceResolver createResourceResolver(final ISvgConverterProperties props) {
        if (props == null) {
            return new ResourceResolver(null);
        }
        return new ResourceResolver(props.getBaseUri(), props.getResourceRetriever());
    }
}
