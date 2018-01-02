/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.renderer.CanvasRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;

/**
 * This class is used for adding content directly onto a specified {@link PdfCanvas}.
 * {@link Canvas} does not know the concept of a page, so it can't reflow to a 'next' {@link Canvas}.
 *
 * This class effectively acts as a bridge between the high-level <em>layout</em>
 * API and the low-level <em>kernel</em> API.
 */
public class Canvas extends RootElement<Canvas> {

    protected PdfCanvas pdfCanvas;
    protected Rectangle rootArea;

    /**
     * Is initialized and used only when Canvas element autotagging is enabled, see {@link #enableAutoTagging(PdfPage)}.
     * It is also used to determine if autotagging is enabled.
     */
    protected PdfPage page;

    /**
     * Creates a new Canvas to manipulate a specific document and page.
     *
     * @param pdfCanvas the low-level content stream writer
     * @param pdfDocument the document that the resulting content stream will be written to
     * @param rootArea the maximum area that the Canvas may write upon
     */
    public Canvas(PdfCanvas pdfCanvas, PdfDocument pdfDocument, Rectangle rootArea) {
        super();
        this.pdfDocument = pdfDocument;
        this.pdfCanvas = pdfCanvas;
        this.rootArea = rootArea;
    }

    /**
     * Creates a new Canvas to manipulate a specific document and page.
     *
     * @param pdfCanvas         The low-level content stream writer
     * @param pdfDocument       The document that the resulting content stream will be written to
     * @param rootArea          The maximum area that the Canvas may write upon
     * @param immediateFlush    Whether to flush the canvas immediately after operations, false otherwise
     */
    public Canvas(PdfCanvas pdfCanvas, PdfDocument pdfDocument, Rectangle rootArea, boolean immediateFlush) {
        this(pdfCanvas, pdfDocument, rootArea);
        this.immediateFlush = immediateFlush;
    }

    /**
     * Creates a new Canvas to manipulate a specific {@link PdfFormXObject}.
     *
     * @param formXObject the form
     * @param pdfDocument the document that the resulting content stream will be written to
     */
    public Canvas(PdfFormXObject formXObject, PdfDocument pdfDocument) {
        this(new PdfCanvas(formXObject, pdfDocument), pdfDocument, formXObject.getBBox().toRectangle());
    }

    /**
     * Gets the {@link PdfDocument} for this canvas.
     * @return the document that the resulting content stream will be written to
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Gets the root area rectangle.
     * @return the maximum area that the Canvas may write upon
     */
    public Rectangle getRootArea() {
        return rootArea;
    }

    /**
     * Gets the {@link PdfCanvas}.
     * @return the low-level content stream writer
     */
    public PdfCanvas getPdfCanvas() {
        return pdfCanvas;
    }

    /**
     * Sets the {@link IRenderer} for this Canvas.
     *
     * @param canvasRenderer a renderer specific for canvas operations
     */
    public void setRenderer(CanvasRenderer canvasRenderer) {
        this.rootRenderer = canvasRenderer;
    }

    /**
     * Returned value is not null only in case when autotagging is enabled.
     * @return the page, on which this canvas will be rendered, or null if autotagging is not enabled.
     */
    public PdfPage getPage() {
        return page;
    }

    /**
     * Enables canvas content autotagging. By default it is disabled.
     * @param page the page, on which this canvas will be rendered.
     */
    public void enableAutoTagging(PdfPage page) {
        this.page = page;
    }

    /**
     * @return true if autotagging of canvas content is enabled. Default value - false.
     */
    public boolean isAutoTaggingEnabled() {
        return page != null;
    }

    /**
     * Performs an entire recalculation of the element flow on the canvas,
     * taking into account all its current child elements. May become very
     * resource-intensive for large documents.
     *
     * Do not use when you have set {@link #immediateFlush} to <code>true</code>.
     */
    public void relayout() {
        if (immediateFlush) {
            throw new IllegalStateException("Operation not supported with immediate flush");
        }

        IRenderer nextRelayoutRenderer = rootRenderer != null ? rootRenderer.getNextRenderer() : null;
        if (nextRelayoutRenderer == null || !(nextRelayoutRenderer instanceof RootRenderer)) {
            nextRelayoutRenderer = new CanvasRenderer(this, immediateFlush);
        }
        rootRenderer = (RootRenderer) nextRelayoutRenderer;
        for (IElement element : childElements) {
            createAndAddRendererSubTree(element);
        }
    }

    /**
     * Forces all registered renderers (including child element renderers) to
     * flush their contents to the content stream.
     */
    public void flush() {
        rootRenderer.flush();
    }

    /**
     * Closes the {@link Canvas}. Although not completely necessary in all cases, it is still recommended to call this
     * method when you are done working with {@link Canvas} object, as due to some properties set there might be some
     * 'hanging' elements, which are waiting other elements to be added and processed. {@link #close()} tells the
     * {@link Canvas} that no more elements will be added and it is time to finish processing all the elements.
     */
    @Override
    public void close() {
        if (rootRenderer != null) {
            rootRenderer.close();
        }
    }

    @Override
    protected RootRenderer ensureRootRendererNotNull() {
        if (rootRenderer == null)
            rootRenderer = new CanvasRenderer(this, immediateFlush);
        return rootRenderer;
    }

}
