/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.ILargeElement;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;

/**
 * Document is the default root element when creating a self-sufficient PDF. It
 * mainly operates high-level operations e.g. setting page size and rotation,
 * adding elements, and writing text at specific coordinates. It has no
 * knowledge of the actual PDF concepts and syntax.
 * <p>
 * A {@link Document}'s rendering behavior can be modified by extending
 * {@link DocumentRenderer} and setting an instance of this newly created with
 * {@link #setRenderer(com.itextpdf.layout.renderer.DocumentRenderer) }.
 */
public class Document extends RootElement<Document> {

    /**
     * @deprecated To be removed in 7.2. Use {@link com.itextpdf.layout.property.Property#MARGIN_LEFT} instead.
     */
    @Deprecated
    protected float leftMargin = 36;
    /**
     * @deprecated To be removed in 7.2. Use {@link com.itextpdf.layout.property.Property#MARGIN_RIGHT} instead.
     */
    @Deprecated
    protected float rightMargin = 36;
    /**
     * @deprecated To be removed in 7.2. Use {@link com.itextpdf.layout.property.Property#MARGIN_TOP} instead.
     */
    @Deprecated
    protected float topMargin = 36;
    /**
     * @deprecated To be removed in 7.2. Use {@link com.itextpdf.layout.property.Property#MARGIN_BOTTOM} instead.
     */
    @Deprecated
    protected float bottomMargin = 36;

    /**
     * Creates a document from a {@link PdfDocument}. Initializes the first page
     * with the {@link PdfDocument}'s current default {@link PageSize}.
     *
     * @param pdfDoc the in-memory representation of the PDF document
     */
    public Document(PdfDocument pdfDoc) {
        this(pdfDoc, pdfDoc.getDefaultPageSize());
    }

    /**
     * Creates a document from a {@link PdfDocument} with a manually set {@link
     * PageSize}.
     *
     * @param pdfDoc   the in-memory representation of the PDF document
     * @param pageSize the page size
     */
    public Document(PdfDocument pdfDoc, PageSize pageSize) {
        this(pdfDoc, pageSize, true);
    }

    /**
     * Creates a document from a {@link PdfDocument} with a manually set {@link
     * PageSize}.
     *
     * @param pdfDoc         the in-memory representation of the PDF document
     * @param pageSize       the page size
     * @param immediateFlush if true, write pages and page-related instructions
     *                       to the {@link PdfDocument} as soon as possible.
     */
    public Document(PdfDocument pdfDoc, PageSize pageSize, boolean immediateFlush) {
        super();
        this.pdfDocument = pdfDoc;
        this.pdfDocument.setDefaultPageSize(pageSize);
        this.immediateFlush = immediateFlush;
    }

    /**
     * Closes the document and associated PdfDocument.
     */
    @Override
    public void close() {
        if (rootRenderer != null) {
            rootRenderer.close();
        }
        pdfDocument.close();
    }

    /**
     * Terminates the current element, usually a page. Sets the next element
     * to be the size specified in the argument.
     *
     * @param areaBreak an {@link AreaBreak}, optionally with a specified size
     * @return this element
     */
    public Document add(AreaBreak areaBreak) {
        checkClosingStatus();
        childElements.add(areaBreak);
        ensureRootRendererNotNull().addChild(areaBreak.createRendererSubTree());
        if (immediateFlush) {
            childElements.remove(childElements.size() - 1);
        }
        return this;
    }

    @Override
    public Document add(IBlockElement element) {
        checkClosingStatus();
        super.add(element);
        if (element instanceof ILargeElement) {
            ((ILargeElement) element).setDocument(this);
            ((ILargeElement) element).flushContent();
        }
        return this;
    }

    /**
     * Gets PDF document.
     *
     * @return the in-memory representation of the PDF document
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Changes the {@link DocumentRenderer} at runtime. Use this to customize
     * the Document's {@link IRenderer} behavior.
     *
     * @param documentRenderer the DocumentRenderer to set
     */
    public void setRenderer(DocumentRenderer documentRenderer) {
        this.rootRenderer = documentRenderer;
    }

    /**
     * Forces all registered renderers (including child element renderers) to
     * flush their contents to the content stream.
     */
    public void flush() {
        rootRenderer.flush();
    }

    /**
     * Performs an entire recalculation of the document flow, taking into
     * account all its current child elements. May become very
     * resource-intensive for large documents.
     * <p>
     * Do not use when you have set {@link #immediateFlush} to <code>true</code>.
     */
    public void relayout() {
        if (immediateFlush) {
            throw new IllegalStateException("Operation not supported with immediate flush");
        }

        IRenderer nextRelayoutRenderer = rootRenderer != null ? rootRenderer.getNextRenderer() : null;
        if (nextRelayoutRenderer == null || !(nextRelayoutRenderer instanceof RootRenderer)) {
            nextRelayoutRenderer = new DocumentRenderer(this, immediateFlush);
        }

        while (pdfDocument.getNumberOfPages() > 0) {
            pdfDocument.removePage(pdfDocument.getNumberOfPages());
        }

        rootRenderer = (RootRenderer) nextRelayoutRenderer;
        for (IElement element : childElements) {
            createAndAddRendererSubTree(element);
        }
    }

    /**
     * Gets the left margin, measured in points
     *
     * @return a <code>float</code> containing the left margin value
     */
    public float getLeftMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_LEFT);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_LEFT));
    }

    /**
     * Sets the left margin, measured in points
     *
     * @param leftMargin a <code>float</code> containing the new left margin value
     */
    public void setLeftMargin(float leftMargin) {
        setProperty(Property.MARGIN_LEFT, leftMargin);
        this.leftMargin = leftMargin;
    }

    /**
     * Gets the right margin, measured in points
     *
     * @return a <code>float</code> containing the right margin value
     */
    public float getRightMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_RIGHT);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_RIGHT));
    }

    /**
     * Sets the right margin, measured in points
     *
     * @param rightMargin a <code>float</code> containing the new right margin value
     */
    public void setRightMargin(float rightMargin) {
        setProperty(Property.MARGIN_RIGHT, rightMargin);
        this.rightMargin = rightMargin;
    }

    /**
     * Gets the top margin, measured in points
     *
     * @return a <code>float</code> containing the top margin value
     */
    public float getTopMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_TOP);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_TOP));
    }

    /**
     * Sets the top margin, measured in points
     *
     * @param topMargin a <code>float</code> containing the new top margin value
     */
    public void setTopMargin(float topMargin) {
        setProperty(Property.MARGIN_TOP, topMargin);
        this.topMargin = topMargin;
    }

    /**
     * Gets the bottom margin, measured in points
     *
     * @return a <code>float</code> containing the bottom margin value
     */
    public float getBottomMargin() {
        Float property = this.<Float>getProperty(Property.MARGIN_BOTTOM);
        return (float) (property != null ? property : this.<Float>getDefaultProperty(Property.MARGIN_BOTTOM));
    }

    /**
     * Sets the bottom margin, measured in points
     *
     * @param bottomMargin a <code>float</code> containing the new bottom margin value
     */
    public void setBottomMargin(float bottomMargin) {
        setProperty(Property.MARGIN_BOTTOM, bottomMargin);
        this.bottomMargin = bottomMargin;
    }

    /**
     * Convenience method to set all margins with one method.
     *
     * @param topMargin    the upper margin
     * @param rightMargin  the right margin
     * @param leftMargin   the left margin
     * @param bottomMargin the lower margin
     */
    public void setMargins(float topMargin, float rightMargin, float bottomMargin, float leftMargin) {
        setTopMargin(topMargin);
        setRightMargin(rightMargin);
        setBottomMargin(bottomMargin);
        setLeftMargin(leftMargin);
    }

    /**
     * Returns the area that will actually be used to write on the page, given
     * the current margins. Does not have any side effects on the document.
     *
     * @param pageSize the size of the page to
     * @return a {@link Rectangle} with the required dimensions and origin point
     */
    public Rectangle getPageEffectiveArea(PageSize pageSize) {
        float x = pageSize.getLeft() + getLeftMargin();
        float y = pageSize.getBottom() + getBottomMargin();
        float width = pageSize.getWidth() - getLeftMargin() - getRightMargin();
        float height = pageSize.getHeight() - getBottomMargin() - getTopMargin();
        return new Rectangle(x, y, width, height);
    }


    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.MARGIN_BOTTOM:
            case Property.MARGIN_LEFT:
            case Property.MARGIN_RIGHT:
            case Property.MARGIN_TOP:
                return (T1) (Object) 36f;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }


    @Override
    protected RootRenderer ensureRootRendererNotNull() {
        if (rootRenderer == null)
            rootRenderer = new DocumentRenderer(this, immediateFlush);
        return rootRenderer;
    }

    /**
     * Checks whether a method is invoked at the closed document
     */
    protected void checkClosingStatus() {
        if (getPdfDocument().isClosed()) {
            throw new PdfException(PdfException.DocumentClosedItIsImpossibleToExecuteAction);
        }
    }
}
