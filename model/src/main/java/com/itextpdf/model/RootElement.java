package com.itextpdf.model;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontFactory;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.*;
import com.itextpdf.model.renderer.AbstractRenderer;
import com.itextpdf.model.renderer.RootRenderer;
import com.itextpdf.model.splitting.DefaultSplitCharacters;
import com.itextpdf.model.splitting.ISplitCharacters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class RootElement<Type extends RootElement> implements IPropertyContainer<Type> {

    protected boolean immediateFlush = true;
    protected PdfDocument pdfDocument;

    protected List<IElement> childElements = new ArrayList<>();
    protected Map<Property, Object> properties = new EnumMap<>(Property.class);

    protected PdfFont defaultFont;
    protected ISplitCharacters defaultSplitCharacters;

    protected RootRenderer rootRenderer;

    /**
     * Adds an element to the root. The element is immediately placed to the contents.
     */
    public Type add(BlockElement element) {
        childElements.add(element);
        ensureRootRendererNotNull().addChild(element.createRendererSubTree());
        return (Type) this;
    }

    public Type add(Image image) {
        childElements.add(image);
        ensureRootRendererNotNull().addChild(image.createRendererSubTree());
        return (Type) this;
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
                        defaultFont = new PdfType1Font(pdfDocument, (Type1Font) FontFactory.createFont(FontConstants.HELVETICA));
                    }
                    return (T) defaultFont;
                case SPLIT_CHARACTERS:
                    if (defaultSplitCharacters == null) {
                        defaultSplitCharacters = new DefaultSplitCharacters();
                    }
                    return (T) defaultSplitCharacters;
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
                case BASE_DIRECTION:
                    return (T) Property.BaseDirection.NO_BIDI;
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
    public Type setProperty(Property property, Object value) {
        properties.put(property, value);
        return (Type) this;
    }

    public <T extends RootRenderer> T getRenderer() {
        return (T) rootRenderer;
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     */
    public <T extends RootElement> T showTextAligned(String text, float x, float y, Property.TextAlignment textAlign) {
        return showTextAligned(text, x, y, textAlign, 0);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public <T extends RootElement> T showTextAligned(String text, float x, float y, Property.TextAlignment textAlign, float angle) {
        return showTextAligned(text, x, y, textAlign, Property.VerticalAlignment.BOTTOM, angle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public <T extends RootElement> T showTextAligned(String text, float x, float y, Property.TextAlignment textAlign, Property.VerticalAlignment vertAlign, float angle) {
        Paragraph p = new Paragraph(text);
        return showTextAligned(p, x, y, pdfDocument.getNumOfPages(), textAlign, vertAlign, angle);
    }

    /**
     * Convenience method to write a kerned text aligned about the specified point
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public <T extends RootElement> T showTextAlignedKerned(String text, float x, float y, Property.TextAlignment textAlign, Property.VerticalAlignment vertAlign, float angle) {
        Paragraph p = new Paragraph(text).setFontKerning(Property.FontKerning.YES);
        return showTextAligned(p, x, y, pdfDocument.getNumOfPages(), textAlign, vertAlign, angle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param p paragraph of text to be placed to the page. By default it has no leading and is written in single line.
     *          Set width to write multiline text.
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param pageNumber the page number to write the text
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     */
    public <T extends RootElement> T showTextAligned(Paragraph p, float x, float y, int pageNumber, Property.TextAlignment textAlign, Property.VerticalAlignment vertAlign, float angle) {
        Div div = new Div();
        div.setTextAlignment(textAlign).setVerticalAlignment(vertAlign);
        if (angle != 0) {
            div.setRotationAngle(angle);
        }
        div.setProperty(Property.ROTATION_POINT_X, x);
        div.setProperty(Property.ROTATION_POINT_Y, y);

        float divWidth = AbstractRenderer.INF;
        float divHeight = AbstractRenderer.INF;
        float divX = x, divY = y;
        if (textAlign == Property.TextAlignment.CENTER) {
            divX = x - divWidth / 2;
            p.setHorizontalAlignment(Property.HorizontalAlignment.CENTER);
        } else if (textAlign == Property.TextAlignment.RIGHT) {
            divX = x - divWidth;
            p.setHorizontalAlignment(Property.HorizontalAlignment.RIGHT);
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

        return (T) this;
    }

    protected abstract RootRenderer ensureRootRendererNotNull();

}
