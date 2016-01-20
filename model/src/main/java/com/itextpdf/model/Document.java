package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.AreaBreak;
import com.itextpdf.model.element.BlockElement;
import com.itextpdf.model.element.IElement;
import com.itextpdf.model.element.ILargeElement;
import com.itextpdf.model.renderer.DocumentRenderer;
import com.itextpdf.model.renderer.RootRenderer;

public class Document extends RootElement<Document> {

    protected float leftMargin = 36;
    protected float rightMargin = 36;
    protected float topMargin = 36;
    protected float bottomMargin = 36;

    public Document(PdfDocument pdfDoc) {
        this(pdfDoc, pdfDoc.getDefaultPageSize());
    }

    public Document(PdfDocument pdfDoc, PageSize pageSize) {
        this(pdfDoc, pageSize, true);
    }

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
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public void setRenderer(DocumentRenderer documentRenderer) {
        this.rootRenderer = documentRenderer;
    }

    public void flush() {
        rootRenderer.flush();
    }

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

    public void setMargins(float topMargin, float rightMargin, float bottomMargin, float leftMargin) {
        setLeftMargin(leftMargin);
        setRightMargin(rightMargin);
        setTopMargin(topMargin);
        setBottomMargin(bottomMargin);
    }

    public Rectangle getPageEffectiveArea(PageSize pageSize) {
        return new Rectangle(leftMargin, bottomMargin, pageSize.getWidth() - leftMargin - rightMargin, pageSize.getHeight() - bottomMargin - topMargin);
    }

    /**
     * Rotates PageSize clockwise with all the margins, i.e. the margins are rotated as well.
     */
    public PageSize rotatePage(PageSize pageSize) {
        setTopMargin(leftMargin);
        setRightMargin(topMargin);
        setBottomMargin(rightMargin);
        setLeftMargin(bottomMargin);
        return pageSize.rotate();
    }

    /**
     * checks whether a method is invoked at the closed document
     * @throws PdfException
     */
    protected void checkClosingStatus(){
        if(getPdfDocument().isSuccessClosing()){
            throw  new PdfException(PdfException.DocumentClosedImpossibleExecuteAction);
        }
    }
}
