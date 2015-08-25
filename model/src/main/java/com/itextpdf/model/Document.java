package com.itextpdf.model;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontFactory;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.*;
import com.itextpdf.model.renderer.AbstractRenderer;
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
        if (element instanceof ILargeElement) {
            ((ILargeElement) element).setDocument(this);
            ((ILargeElement) element).flushContent();
        }
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
     * Convenience method to write a text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param horAlign horizontal alignment about the specified point
     */
    public Document showTextAligned(String text, float x, float y, Property.HorizontalAlignment horAlign) {
        return showTextAligned(text, x, y, horAlign, 0);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param horAlign horizontal alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public Document showTextAligned(String text, float x, float y, Property.HorizontalAlignment horAlign, float angle) {
        return showTextAligned(text, x, y, horAlign, Property.VerticalAlignment.BOTTOM, angle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param horAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public Document showTextAligned(String text, float x, float y, Property.HorizontalAlignment horAlign, Property.VerticalAlignment vertAlign, float angle) {
        Paragraph p = new Paragraph(text);
        return showTextAligned(p, x, y, pdfDocument.getNumOfPages(), horAlign, vertAlign, angle);
    }

    /**
     * Convenience method to write a kerned text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param horAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public Document showTextAlignedKerned(String text, float x, float y, Property.HorizontalAlignment horAlign, Property.VerticalAlignment vertAlign, float angle) {
        Paragraph p = new Paragraph(text).setFontKerning(Property.FontKerning.YES);
        return showTextAligned(p, x, y, pdfDocument.getNumOfPages(), horAlign, vertAlign, angle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param p paragraph of text to be placed to the page. By default it has no leading and is written in single line.
     *          Set width to write multiline text.
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param pageNumber the page number to write the text
     * @param horAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public Document showTextAligned(Paragraph p, float x, float y, int pageNumber, Property.HorizontalAlignment horAlign, Property.VerticalAlignment vertAlign, float angle) {
        Div div = new Div();
        div.setHorizontalAlignment(horAlign).setVerticalAlignment(vertAlign);
        if (angle != 0) {
            div.setRotationAngle(angle);
        }
        div.setProperty(Property.ROTATION_POINT_X, x);
        div.setProperty(Property.ROTATION_POINT_Y, y);

        float divWidth = AbstractRenderer.INF;
        float divHeight = AbstractRenderer.INF;
        float divX = x, divY = y;
        if (horAlign == Property.HorizontalAlignment.CENTER) {
            divX = x - divWidth / 2;
        } else if (horAlign == Property.HorizontalAlignment.RIGHT) {
            divX = x - divWidth;
        }

        if (vertAlign == Property.VerticalAlignment.MIDDLE) {
            divY = y - divHeight / 2;
        } else if (vertAlign == Property.VerticalAlignment.TOP) {
            divY = y - divHeight;
        }

        if (pageNumber == 0)
            pageNumber = 1;
        div.setFixedPosition(pageNumber, divX, divY, divWidth).setHeight(divHeight);
        if (p.getProperty(Property.LEADING) == null) {
            p.setMultipliedLeading(1);
        }
        div.add(p.setMargins(0, 0, 0, 0));
        this.add(div);

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
    public boolean hasProperty(Property property) {
        return properties.containsKey(property);
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
    public void deleteProperty(Property property) {
        properties.remove(property);
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
