package com.itextpdf.layout;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.ILargeElement;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.RootRenderer;

/**
 * Document is the default root element when creating a self-sufficient PDF. It
 * mainly operates high-level operations e.g. setting page size and rotation,
 * adding elements, and writing text at specific coordinates. It has no
 * knowledge of the actual PDF concepts and syntax.
 * 
 * A {@link Document}'s rendering behavior can be modified by extending
 * {@link DocumentRenderer} and setting an instance of this newly created with
 * {@link #setRenderer(com.itextpdf.layout.renderer.DocumentRenderer) }.
 */
public class Document extends RootElement<Document> {

    protected float leftMargin = 36;
    protected float rightMargin = 36;
    protected float topMargin = 36;
    protected float bottomMargin = 36;

    /**
     * Creates a document from a {@link PdfDocument}. Initializes the first page
     * with the {@link PdfDocument}'s current default {@link PageSize}.
     * @param pdfDoc the in-memory representation of the PDF document
     */
    public Document(PdfDocument pdfDoc) {
        this(pdfDoc, pdfDoc.getDefaultPageSize());
    }
    
    /**
     * Creates a document from a {@link PdfDocument} with a manually set {@link
     * PageSize}.
     * @param pdfDoc the in-memory representation of the PDF document
     * @param pageSize the page size
     */
    public Document(PdfDocument pdfDoc, PageSize pageSize) {
        this(pdfDoc, pageSize, true);
    }

    /**
     * Creates a document from a {@link PdfDocument} with a manually set {@link
     * PageSize}. 
     * @param pdfDoc the in-memory representation of the PDF document
     * @param pageSize the page size
     * @param immediateFlush if true, write pages and page-related instructions
     * to the {@link PdfDocument} as soon as possible.
     */
    public Document(PdfDocument pdfDoc, PageSize pageSize, boolean immediateFlush) {
        this.pdfDocument = pdfDoc;
        this.pdfDocument.setDefaultPageSize(pageSize);
        this.immediateFlush = immediateFlush;
    }

    /**
     * Closes the document and associated PdfDocument.
     */
    public void close() {
        if (rootRenderer != null && !immediateFlush)
            rootRenderer.flush();
        pdfDocument.close();
    }

    /**
     * Terminates the current element, usually a page. Sets the next element
     * to be the size specified in the argument.
     * @param areaBreak an {@link AreaBreak}, optionally with a specified size
     * @return this element
     */
    public Document add(AreaBreak areaBreak) {
        childElements.add(areaBreak);
        ensureRootRendererNotNull().addChild(areaBreak.createRendererSubTree());
        return this;
    }

    @Override
    public Document add(BlockElement element) {
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
     * @return the in-memory representation of the PDF document
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Changes the {@link DocumentRenderer} at runtime. Use this to customize
     * the Document's {@link IRenderer} behavior.
     * 
     * @param documentRenderer
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
     * 
     * Do not use when you have set {@link #immediateFlush} to <code>true</code>.
     */
    public void relayout() {

        if (immediateFlush) {
            throw new IllegalStateException("Operation not supported with immediate flush");
        }

        try {
            while (pdfDocument.getNumberOfPages() > 0)
                pdfDocument.removePage(pdfDocument.getNumberOfPages());
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        rootRenderer = new DocumentRenderer(this, immediateFlush);
        for (IElement element : childElements) {
            rootRenderer.addChild(element.createRendererSubTree());
        }
    }

    @Override
    protected RootRenderer ensureRootRendererNotNull() {
        if (rootRenderer == null)
            rootRenderer = new DocumentRenderer(this, immediateFlush);
        return rootRenderer;
    }

    public float getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(float leftMargin) {
        this.leftMargin = leftMargin;
    }

    public float getRightMargin() {
        return rightMargin;
    }

    public void  setRightMargin(float rightMargin) {
        this.rightMargin = rightMargin;
    }

    public float getTopMargin() {
        return topMargin;
    }

    public void  setTopMargin(float topMargin) {
        this.topMargin = topMargin;
    }

    public float getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(float bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    /**
     * Convenience method to set all margins with one method.
     * @param topMargin the upper margin
     * @param rightMargin the right margin
     * @param leftMargin the left margin
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
        return new Rectangle(leftMargin, bottomMargin, pageSize.getWidth() - leftMargin - rightMargin, pageSize.getHeight() - bottomMargin - topMargin);
    }

    /**
     * Rotates PageSize clockwise with all the margins, i.e. the margins are rotated as well.
     * @param pageSize the current page size
     * @return the same page size, after clockwise rotation
     */
    public PageSize rotatePage(PageSize pageSize) {
        setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        return pageSize.rotate();
    }

    /**
     * checks whether a method is invoked at the closed document
     * @throws PdfException
     */
    protected void checkClosingStatus(){
        if(getPdfDocument().isClosed()){
            throw  new PdfException(PdfException.DocumentClosedImpossibleExecuteAction);
        }
    }
}
