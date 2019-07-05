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

import com.itextpdf.io.util.FileUtil;
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
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.impl.PdfRootSvgNodeRenderer;
import com.itextpdf.svg.utils.SvgCssUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
            throw new SvgProcessingException(SvgLogMessageConstant.PARAMETER_CANNOT_BE_NULL);
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
     * @param content  the Stream object containing valid SVG content
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
     * @param content  the Stream object containing valid SVG content
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
     * @param stream   the Stream object containing valid SVG content
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
     * @param stream   the Stream object containing valid SVG content
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
     * @param stream   the Stream object containing valid SVG content
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
     * @param stream   the Stream object containing valid SVG content
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
     * @param props   a {@link ISvgConverterProperties} instance
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
     * @param props       a {@link ISvgConverterProperties} instance
     * @param writerProps a {@link WriterProperties} for the pdf document
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void createPdf(File svgFile, File pdfFile, ISvgConverterProperties props, WriterProperties writerProps) throws IOException {
        if (props == null) {
            props = new SvgConverterProperties().setBaseUri(FileUtil.getParentDirectory(svgFile));
        } else if (props.getBaseUri() == null || props.getBaseUri().isEmpty()) {
            String baseUri = FileUtil.getParentDirectory(svgFile);
            props = convertToSvgConverterProps(props, baseUri);
        }
        try (FileInputStream fileInputStream = new FileInputStream(svgFile.getAbsolutePath());
             FileOutputStream fileOutputStream = new FileOutputStream(pdfFile.getAbsolutePath())) {
            createPdf(fileInputStream, fileOutputStream, props, writerProps);
        }
    }

    /**
     * Copies properties from custom ISvgConverterProperties into new SvgConverterProperties.
     * Since ISvgConverterProperties itself is immutable we have to do it.
     *
     * @param props
     * @param baseUri
     * @return
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
     * @param writerprops writerproperties for the pdf document
     * @throws IOException when the one of the streams cannot be read correctly
     */
    public static void createPdf(InputStream svgStream, OutputStream pdfDest, WriterProperties writerprops) throws IOException {
        createPdf(svgStream, pdfDest, null, writerprops);
    }

    /**
     * Create a single page pdf containing the SVG on its page using the default processing and drawing logic
     *
     * @param svgStream {@link InputStream Stream} containing the SVG
     * @param pdfDest   PDF destination outputStream
     * @param props     Svg {@link ISvgConverterProperties} to change default behaviour
     * @throws IOException when the one of the streams cannot be read correctly
     */
    public static void createPdf(InputStream svgStream, OutputStream pdfDest, ISvgConverterProperties props) throws IOException {
        createPdf(svgStream, pdfDest, props, null);
    }

    /**
     * Create a single page pdf containing the SVG on its page using the default processing and drawing logic
     *
     * @param svgStream   {@link InputStream Stream} containing the SVG
     * @param props       {@link ISvgConverterProperties} to change default behaviour
     * @param pdfDest     PDF destination outputStream
     * @param writerProps {@link WriterProperties}  for the pdf document
     * @throws IOException when the one of the streams cannot be read correctly
     *                     public static void createPdf(InputStream svgStream,ISvgConverterProperties props, OutputStream pdfDest) throws IOException {
     *                     createPdf(svgStream,props,pdfDest,null);
     *                     }
     *                     <p>
     *                     /**
     *                     Create a single page pdf containing the SVG on its page using the default processing and drawing logic
     * @throws IOException when the one of the streams cannot be read correctly
     */
    public static void createPdf(InputStream svgStream, OutputStream pdfDest, ISvgConverterProperties props, WriterProperties writerProps) throws IOException {

        //create doc
        if (writerProps == null) {
            writerProps = new WriterProperties();
        }
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfDest, writerProps));
        //TODO DEVSIX-2095
        //process
        ISvgProcessorResult processorResult = process(parse(svgStream, props), props);
        ISvgNodeRenderer topSvgRenderer = processorResult.getRootRenderer();

        String baseUri = tryToExtractBaseUri(props);
        SvgDrawContext drawContext =
                new SvgDrawContext(new ResourceResolver(baseUri), processorResult.getFontProvider());

        drawContext.addNamedObjects(processorResult.getNamedObjects());
        //Add temp fonts
        drawContext.setTempFonts(processorResult.getTempFonts());
        //Extract topmost dimensions
        checkNull(topSvgRenderer);
        checkNull(pdfDocument);
        float width, height;

        float[] wh = extractWidthAndHeight(topSvgRenderer);
        width = wh[0];
        height = wh[1];

        //adjust pagesize and create new page
        pdfDocument.setDefaultPageSize(new PageSize(width, height));
        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas pageCanvas = new PdfCanvas(page);
        //Add to the first page
        PdfFormXObject xObject = convertToXObject(topSvgRenderer, pdfDocument, drawContext);
        //Draw
        draw(xObject, pageCanvas);
        pdfDocument.close();
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
     * corresponding to the passed SVG content
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
     * @param props    {@link ISvgConverterProperties} to customize the behavior
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     * corresponding to the passed SVG content
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
     * @param stream   the {@link InputStream Stream} object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param props    {@link ISvgConverterProperties} to customize the behavior
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     * corresponding to the passed SVG content
     * @throws IOException when the Stream cannot be read correctly
     */
    public static PdfFormXObject convertToXObject(InputStream stream, PdfDocument document, ISvgConverterProperties props) throws IOException {
        checkNull(stream);
        checkNull(document);

        return convertToXObject(process(parse(stream, props), props), document, props);
    }

    //Private converter for unification
    private static PdfFormXObject convertToXObject(ISvgProcessorResult processorResult, PdfDocument document, ISvgConverterProperties props) {
        String baseUri = "";
        if (props != null) baseUri = props.getBaseUri();
        SvgDrawContext drawContext = new SvgDrawContext(new ResourceResolver(baseUri), processorResult.getFontProvider());
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
     * @param stream   the {@link InputStream Stream} object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     * corresponding to the passed SVG content
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
     * @param stream   the Stream object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @return a {@link Image Image} containing the PDF instructions
     * corresponding to the passed SVG content
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
     * @param stream   the {@link InputStream Stream} object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param props    {@link ISvgConverterProperties} to customize the behavior
     * @return a {@link Image Image} containing the PDF instructions
     * corresponding to the passed SVG content
     * @throws IOException when the Stream cannot be read correctly
     */
    public static Image convertToImage(InputStream stream, PdfDocument document, ISvgConverterProperties props) throws IOException {
        return new Image(convertToXObject(stream, document, props));
    }

    /*
     * This method is kept private, because there is little purpose in exposing it.
     */
    private static void draw(PdfFormXObject pdfForm, PdfCanvas canvas) {
        canvas.addXObject(pdfForm, 0, 0);
    }

    /*
     * This method is kept private, because there is little purpose in exposing it.
     */
    private static void draw(PdfFormXObject pdfForm, PdfCanvas canvas, float x, float y) {
        canvas.addXObject(pdfForm, x, y);
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
     * corresponding to the passed node renderer tree.
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
     * corresponding to the passed node renderer tree.
     */
    private static PdfFormXObject convertToXObject(ISvgNodeRenderer topSvgRenderer, PdfDocument document, SvgDrawContext context) {
        checkNull(topSvgRenderer);
        checkNull(document);
        checkNull(context);
        float width, height;

        float[] wh = extractWidthAndHeight(topSvgRenderer);
        width = wh[0];
        height = wh[1];

        PdfFormXObject pdfForm = new PdfFormXObject(new Rectangle(0, 0, width, height));
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
     * @param svgStream Input stream containing the SVG to parse and process
     * @return {@link ISvgProcessorResult} containing the root renderer and metadata of the svg
     * @throws IOException when the Stream cannot be read correctly
     */
    public static ISvgProcessorResult parseAndProcess(InputStream svgStream) throws IOException {
        return parseAndProcess(svgStream, null);
    }

    /**
     * Parse and process an Inputstream containing an SVG, using the default Svg processor ({@link DefaultSvgProcessor})
     *
     * @param svgStream {@link InputStream Stream} containing the SVG to parse and process
     * @param props     {@link ISvgConverterProperties} used by the processor
     * @return {@link ISvgProcessorResult} containing the root renderer and metadata of the svg
     * @throws IOException when the Stream cannot be read correctly
     */
    public static ISvgProcessorResult parseAndProcess(InputStream svgStream, ISvgConverterProperties props) throws IOException {
        IXmlParser parser = new JsoupXmlParser();
        String charset = tryToExtractCharset(props);
        INode nodeTree = parser.parse(svgStream, charset);
        return new DefaultSvgProcessor().process(nodeTree, props);
    }

    /**
     * Use the default implementation of {@link ISvgProcessor} to convert an XML
     * DOM tree to a node renderer tree.
     *
     * @param root the XML DOM tree
     * @return a node renderer tree corresponding to the passed XML DOM tree
     */
    public static ISvgProcessorResult process(INode root) {
        checkNull(root);
        return new DefaultSvgProcessor().process(root);
    }

    /**
     * Use the default implementation of {@link ISvgProcessor} to convert an XML
     * DOM tree to a node renderer tree. The passed properties can modify the default behaviour
     *
     * @param root  the XML DOM tree
     * @param props {@link ISvgConverterProperties} to customize the behavior
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
     * @param stream the {@link InputStream Stream} object containing valid SVG content
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
     * @param stream the {@link InputStream Stream} object containing valid SVG content
     * @param props  {@link ISvgConverterProperties} to customize the behavior
     * @return an XML DOM tree corresponding to the passed String input
     * @throws IOException when the Stream cannot be read correctly
     */
    public static INode parse(InputStream stream, ISvgConverterProperties props) throws IOException {
        checkNull(stream); // props is allowed to be null
        IXmlParser xmlParser = new JsoupXmlParser();
        return xmlParser.parse(stream, tryToExtractCharset(props));
    }

    /**
     * Extract width and height of the passed SVGNodeRenderer,
     * defaulting to respective viewbox values if either one is not present or
     * to browser default if viewbox is missing as well
     *
     * @param topSvgRenderer
     * @return float[2], width is in position 0, height in position 1
     */
    public static float[] extractWidthAndHeight(ISvgNodeRenderer topSvgRenderer) {
        float[] res = new float[2];
        boolean viewBoxPresent = false;

        //Parse viewbox
        String vbString = topSvgRenderer.getAttribute(SvgConstants.Attributes.VIEWBOX);
        float[] values = {0, 0, 0, 0};
        if (vbString != null) {
            List<String> valueStrings = SvgCssUtils.splitValueList(vbString);
            values = new float[valueStrings.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = CssUtils.parseAbsoluteLength(valueStrings.get(i));
            }
            viewBoxPresent = true;
        }
        float width, height;
        String wString, hString;
        wString = topSvgRenderer.getAttribute(SvgConstants.Attributes.WIDTH);
        if (wString == null) {
            if (viewBoxPresent) {
                width = values[2];
            } else {
                //Log Warning
                LOGGER.warn(SvgLogMessageConstant.MISSING_WIDTH);
                //Set to browser default
                width = CssUtils.parseAbsoluteLength("300px");
            }
        } else {
            width = CssUtils.parseAbsoluteLength(wString);
        }
        hString = topSvgRenderer.getAttribute(SvgConstants.Attributes.HEIGHT);
        if (hString == null) {
            if (viewBoxPresent) {
                height = values[3];
            } else {
                //Log Warning
                LOGGER.warn(SvgLogMessageConstant.MISSING_HEIGHT);
                //Set to browser default
                height = CssUtils.parseAbsoluteLength("150px");
            }
        } else {
            height = CssUtils.parseAbsoluteLength(hString);
        }

        res[0] = width;
        res[1] = height;
        return res;


    }

    /**
     * Tries to extract charset from {@see ISvgConverterProperties}.
     *
     * @param props converter properties
     * @return charset  | null
     */
    private static String tryToExtractCharset(final ISvgConverterProperties props) {
        return props != null ? props.getCharset() : null;
    }

    /**
     * Tries to extract baseUri from {@see ISvgConverterProperties}.
     *
     * @param props converter properties
     * @return baseUrl  | null
     */
    private static String tryToExtractBaseUri(final ISvgConverterProperties props) {
        if (props == null || props.getBaseUri() == null) {
            return null;
        }
        String baseUrl = props.getBaseUri().trim();
        return baseUrl.isEmpty() ? null : baseUrl;
    }

}
