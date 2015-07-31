package com.itextpdf.model;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontFactory;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.*;
import com.itextpdf.model.renderer.DocumentRenderer;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class Document implements IPropertyContainer<Document> {

    protected PdfDocument pdfDocument;
    protected DocumentRenderer documentRenderer;
    protected boolean immediateFlush = true;
    protected List<IElement> childElements = new ArrayList<>();
    protected Map<Property, Object> properties = new EnumMap<>(Property.class);
    protected PdfFont defaultFont;

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
    public Document add(BlockElement element) {
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
        if (immediateFlush) {
            throw new IllegalStateException("Operation not supported with immediate flush");
        }

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
    public <T> T getProperty(Property property) {
        return (T) properties.get(property);
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        try {
            switch (property) {
                case FONT:
                    if (defaultFont == null) {
                        defaultFont = new PdfType1Font(pdfDocument, (Type1Font) FontFactory.createFont(FontConstants.HELVETICA, ""));
                    }
                    return (T) defaultFont;
                case FONT_SIZE:
                    return (T) new Integer(12);
                case TEXT_RENDERING_MODE:
                    return (T) Integer.valueOf(Property.TextRenderingMode.TEXT_RENDERING_MODE_FILL);
                case TEXT_RISE:
                    return (T) new Float(0);
                case SPACING_RATIO:
                    return (T) new Float(0.75f);
                case FONT_KERNING:
                    return (T) Property.FontKerning.NO;
                default:
                    return null;
            }
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public Document setProperty(Property property, Object value) {
        properties.put(property, value);
        return this;
    }

    private DocumentRenderer ensureDocumentRendererNotNull() {
        if (documentRenderer == null)
            documentRenderer = new DocumentRenderer(this, immediateFlush);
        return documentRenderer;
    }
}
