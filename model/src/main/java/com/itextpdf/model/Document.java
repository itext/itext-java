package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.AreaBreak;
import com.itextpdf.model.element.BlockElement;
import com.itextpdf.model.element.IElement;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.renderer.DocumentRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Document implements IPropertyContainer {

    protected PdfDocument pdfDocument;
    protected DocumentRenderer documentRenderer;
    protected boolean immediateFlush = true;
    protected List<IElement> childElements = new ArrayList<>();
    protected Map<Integer, Object> properties = new HashMap<>();

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
    public void close() throws PdfException {
        if (documentRenderer != null && !immediateFlush)
            documentRenderer.flush();
        pdfDocument.close();
    }

    /**
     * Adds an element to the document. The element is immediately placed with the layout manager.
     *
     * @param element
     * @return
     */
    public Document add(BlockElement element) throws PdfException {
        childElements.add(element);
        ensureDocumentRendererNotNull().addChild(element.createRendererSubTree());
        return this;
    }

    public Document add(Image image) {
        childElements.add(image);
        ensureDocumentRendererNotNull().addChild(image.createRendererSubTree());
        return this;
    }

    public Document add(AreaBreak areaBreak) {
        childElements.add(areaBreak);
        ensureDocumentRendererNotNull().addChild(areaBreak.createRendererSubTree());
        return this;
    }

    /**
     * Gets PDF document.
     *
     * @return
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public DocumentRenderer getRenderer() {
        return documentRenderer;
    }

    public void setRenderer(DocumentRenderer documentRenderer) {
        this.documentRenderer = documentRenderer;
    }

    public void flush() {
        documentRenderer.flush();
    }

    public void relayout() {
        try {
            while (pdfDocument.getNumOfPages() > 0)
                pdfDocument.removePage(pdfDocument.getNumOfPages());
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        documentRenderer = new DocumentRenderer(this, immediateFlush);
        for (IElement element : childElements) {
            documentRenderer.addChild(element.createRendererSubTree());
        }
    }

    @Override
    public <T> T getProperty(Integer propertyKey) {
        return (T) properties.get(propertyKey);
    }

    @Override
    public <T> T getDefaultProperty(Integer propertyKey) {
        try {
            switch (propertyKey) {
                case Property.FONT:
                    return (T) new PdfType1Font(pdfDocument, new Type1Font(FontConstants.HELVETICA, ""));
                case Property.FONT_SIZE:
                    return (T) new Integer(12);
                case Property.TEXT_RENDERING_MODE:
                    return (T) Integer.valueOf(Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL);
                case Property.TEXT_RISE:
                    return (T) new Float(0);
                default:
                    return null;
            }
        } catch (PdfException | IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public Document setProperty(Integer propertyKey, Object value) {
        properties.put(propertyKey, value);
        return this;
    }

    private DocumentRenderer ensureDocumentRendererNotNull() {
        if (documentRenderer == null)
            documentRenderer = new DocumentRenderer(this, immediateFlush);
        return documentRenderer;
    }
}
