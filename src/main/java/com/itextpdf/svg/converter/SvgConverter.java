package com.itextpdf.svg.converter;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.styledxmlparser.AttributeConstants;
import com.itextpdf.styledxmlparser.IHtmlParser;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.ISvgProcessor;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is the main container class for static methods that do high-level
 * conversion operations from input to PDF, either by drawing on a canvas or by
 * returning an XObject, which can then be used by the calling class for further
 * processing and drawing operations.
 */
public final class SvgConverter {

    private SvgConverter() {
    }

    private static void checkNull(Object o) {
        if (o == null) {
            throw new SvgProcessingException(SvgLogMessageConstant.PARAMETER_CANNOT_BE_NULL);
        }
    }

    /**
     * Draws a String containing valid SVG to a document, on a given page
     * number.
     *
     * @param content the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo the page to draw on
     */
    public static void drawOnDocument(String content, PdfDocument document, int pageNo) {
        checkNull(document);
        drawOnPage(content, document.getPage(pageNo));
    }

    /**
     * Draws a String containing valid SVG to a document, on a given page
     * number.
     *
     * @param content the Stream object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo the page to draw on
     * @param props a container for extra properties that customize the behavior
     */
    public static void drawOnDocument(String content, PdfDocument document, int pageNo, ISvgConverterProperties props) {
        checkNull(document);
        drawOnPage(content, document.getPage(pageNo), props);
    }

    /**
     * Draws a Stream containing valid SVG to a document, on a given page
     * number.
     *
     * @param stream the Stream object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo the page to draw on
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnDocument(InputStream stream, PdfDocument document, int pageNo) throws IOException {
        checkNull(document);
        drawOnPage(stream, document.getPage(pageNo));
    }

    /**
     * Draws a Stream containing valid SVG to a document, on a given page
     * number.
     *
     * @param stream the Stream object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param pageNo the page to draw on
     * @param props a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnDocument(InputStream stream, PdfDocument document, int pageNo, ISvgConverterProperties props) throws IOException {
        checkNull(document);
        drawOnPage(stream, document.getPage(pageNo), props);
    }

    /**
     * Draws a String containing valid SVG to a given page
     *
     * @param content the String value containing valid SVG content
     * @param page the {@link PdfPage} instance to draw on
     */
    public static void drawOnPage(String content, PdfPage page) {
        checkNull(page);
        drawOnCanvas(content, new PdfCanvas(page));
    }

    /**
     * Draws a String containing valid SVG to a given page
     *
     * @param content the String value containing valid SVG content
     * @param page the {@link PdfPage} instance to draw on
     * @param props a container for extra properties that customize the behavior
     */
    public static void drawOnPage(String content, PdfPage page, ISvgConverterProperties props) {
        checkNull(page);
        drawOnCanvas(content, new PdfCanvas(page), props);
    }

    /**
     * Draws a Stream containing valid SVG to a given page
     *
     * @param stream the Stream object containing valid SVG content
     * @param page the {@link PdfPage} instance to draw on
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnPage(InputStream stream, PdfPage page) throws IOException {
        checkNull(page);
        drawOnCanvas(stream, new PdfCanvas(page));
    }

    /**
     * Draws a Stream containing valid SVG to a given page
     *
     * @param stream the Stream object containing valid SVG content
     * @param page the {@link PdfPage} instance to draw on
     * @param props a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnPage(InputStream stream, PdfPage page, ISvgConverterProperties props) throws IOException {
        checkNull(page);
        drawOnCanvas(stream, new PdfCanvas(page), props);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object
     *
     * @param content the String value containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     */
    public static void drawOnCanvas(String content, PdfCanvas canvas) {
        checkNull(canvas);
        draw(convertToXObject(content, canvas.getDocument()), canvas);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object
     *
     * @param content the String value containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     * @param props a container for extra properties that customize the behavior
     */
    public static void drawOnCanvas(String content, PdfCanvas canvas, ISvgConverterProperties props) {
        checkNull(canvas);
        draw(convertToXObject(content, canvas.getDocument(), props), canvas);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object
     *
     * @param stream the Stream object containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnCanvas(InputStream stream, PdfCanvas canvas) throws IOException {
        checkNull(canvas);
        draw(convertToXObject(stream, canvas.getDocument()), canvas);
    }

    /**
     * Draws a String containing valid SVG to a pre-made canvas object
     *
     * @param stream the Stream object containing valid SVG content
     * @param canvas the {@link PdfCanvas} instance to draw on
     * @param props a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     */
    public static void drawOnCanvas(InputStream stream, PdfCanvas canvas, ISvgConverterProperties props) throws IOException {
        checkNull(canvas);
        draw(convertToXObject(stream, canvas.getDocument(), props), canvas);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     *
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     *
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param content the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     * corresponding to the passed SVG content
     */
    public static PdfFormXObject convertToXObject(String content, PdfDocument document) {
        return convertToXObject(process(parse(content)), document);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     *
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     *
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param content the String value containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param props a container for extra properties that customize the behavior
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     * corresponding to the passed SVG content
     */
    public static PdfFormXObject convertToXObject(String content, PdfDocument document, ISvgConverterProperties props) {
        return convertToXObject(process(parse(content), props), document);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     *
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     *
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param stream the Stream object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @throws IOException when the Stream cannot be read correctly
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     * corresponding to the passed SVG content
     */
    public static PdfFormXObject convertToXObject(InputStream stream, PdfDocument document) throws IOException {
        return convertToXObject(process(parse(stream)), document);
    }

    /**
     * Converts a String containing valid SVG content to an
     * {@link PdfFormXObject XObject} that can then be used on the passed
     * {@link PdfDocument}. This method does NOT manipulate the
     * {@link PdfDocument} in any way.
     *
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     *
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param stream the Stream object containing valid SVG content
     * @param document the {@link PdfDocument} instance to draw on
     * @param props a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     * @return a {@link PdfFormXObject XObject} containing the PDF instructions
     * corresponding to the passed SVG content
     */
    public static PdfFormXObject convertToXObject(InputStream stream, PdfDocument document, ISvgConverterProperties props) throws IOException {
        return convertToXObject(process(parse(stream, props), props), document);
    }

    /*
     * This method is kept private, because there is little purpose in exposing it.
     */
    private static void draw(PdfFormXObject pdfForm, PdfCanvas canvas) {
        canvas.addXObject(pdfForm, 0, 0);
    }

    /**
     * This method draws a NodeRenderer tree to a canvas that is tied to the
     * passed document.
     *
     * This method (or its overloads) is the best method to use if you want to
     * reuse the same SVG image multiple times on the same {@link PdfDocument}.
     *
     * If you want to reuse this object on other {@link PdfDocument} instances,
     * please either use any of the {@link #process} overloads in this same
     * class and convert its result to an XObject with
     * {@link #convertToXObject(ISvgNodeRenderer, PdfDocument)} , or look into
     * using {@link com.itextpdf.kernel.pdf.PdfObject#copyTo(PdfDocument)}.
     *
     * @param rootRenderer the {@link ISvgNodeRenderer} instance that contains
     * the renderer tree
     * @param document the document that the returned
     * {@link PdfFormXObject XObject} can be drawn on (on any given page
     * coordinates)
     * @return an {@link PdfFormXObject XObject}containing the PDF instructions
     * corresponding to the passed node renderer tree.
     */
    public static PdfFormXObject convertToXObject(ISvgNodeRenderer rootRenderer, PdfDocument document) {
        checkNull(rootRenderer);
        checkNull(document);
        float width = CssUtils.parseAbsoluteLength(rootRenderer.getAttribute(AttributeConstants.WIDTH));
        float height = CssUtils.parseAbsoluteLength(rootRenderer.getAttribute(AttributeConstants.HEIGHT));
        PdfFormXObject pdfForm = new PdfFormXObject(new Rectangle(0, 0, width, height));
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        SvgDrawContext context = new SvgDrawContext();
        context.pushCanvas(canvas);

        rootRenderer.draw(context);

        return pdfForm;
    }

    /**
     * Use the default implementation of {@link ISvgProcessor} to convert an XML
     * DOM tree to a node renderer tree.
     *
     * @param root the XML DOM tree
     * @return a node renderer tree corresponding to the passed XML DOM tree
     */
    public static ISvgNodeRenderer process(INode root) {
        checkNull(root);
        ISvgProcessor processor = new DefaultSvgProcessor();
        return processor.process(root);
    }

    /**
     * Use the default implementation of {@link ISvgProcessor} to convert an XML
     * DOM tree to a node renderer tree.
     *
     * @param root the XML DOM tree
     * @param props a container for extra properties that customize the behavior
     * @return a node renderer tree corresponding to the passed XML DOM tree
     */
    public static ISvgNodeRenderer process(INode root, ISvgConverterProperties props) {
        checkNull(root);
        ISvgProcessor processor = new DefaultSvgProcessor();
        return processor.process(root, props);
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
        IHtmlParser xmlParser = new JsoupXmlParser();
        return xmlParser.parse(content);
    }

    /**
     * Parse a Stream containing valid SVG into an XML DOM node, using the
     * default JSoup XML parser. This method will assume that the encoding of
     * the Stream is {@code UTF-8}.
     *
     * @param stream the Stream object containing valid SVG content
     * @throws IOException when the Stream cannot be read correctly
     * @return an XML DOM tree corresponding to the passed String input
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
     * @param stream the Stream object containing valid SVG content
     * @param props a container for extra properties that customize the behavior
     * @throws IOException when the Stream cannot be read correctly
     * @return an XML DOM tree corresponding to the passed String input
     */
    public static INode parse(InputStream stream, ISvgConverterProperties props) throws IOException {
        checkNull(stream); // props is allowed to be null
        IHtmlParser xmlParser = new JsoupXmlParser();
        return xmlParser.parse(stream, props != null ? props.getCharset() : null);
    }
}
