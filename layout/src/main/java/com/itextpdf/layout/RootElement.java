/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import com.itextpdf.layout.splitting.ISplitCharacters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A generic abstract root element for a PDF layout object hierarchy.
 * 
 * @param <Type> this type
 */
public abstract class RootElement<Type extends RootElement> implements IPropertyContainer<Type> {

    protected boolean immediateFlush = true;
    protected PdfDocument pdfDocument;

    protected List<IElement> childElements = new ArrayList<>();
    protected Map<Property, Object> properties = new EnumMap<>(Property.class);

    protected PdfFont defaultFont;
    protected ISplitCharacters defaultSplitCharacters;

    protected RootRenderer rootRenderer;

    /**
     * Adds an element to the root. The element is immediately placed in the contents.
     * @param element an element with spacial margins, tabbing, and alignment
     * @return this element
     * @see BlockElement
     */
    public Type add(BlockElement element) {
        childElements.add(element);
        ensureRootRendererNotNull().addChild(element.createRendererSubTree());
        return (Type) this;
    }

    /**
     * Adds an image to the root. The element is immediately placed in the contents.
     * @param image a graphical image element
     * @return this element
     * @see Image
     */
    public Type add(Image image) {
        childElements.add(image);
        ensureRootRendererNotNull().addChild(image.createRendererSubTree());
        return (Type) this;
    }

    @Override
    public boolean hasProperty(Property property) {
        return hasOwnProperty(property);
    }

    @Override
    public boolean hasOwnProperty(Property property) {
        return properties.containsKey(property);
    }

    @Override
    public <T> T getProperty(Property property) {
        return getOwnProperty(property);
    }

    @Override
    public <T> T getOwnProperty(Property property) {
        return (T) properties.get(property);
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        try {
            switch (property) {
                case FONT:
                    if (defaultFont == null) {
                        defaultFont = PdfFontFactory.createFont();
                    }
                    return (T) defaultFont;
                case SPLIT_CHARACTERS:
                    if (defaultSplitCharacters == null) {
                        defaultSplitCharacters = new DefaultSplitCharacters();
                    }
                    return (T) defaultSplitCharacters;
                case FONT_SIZE:
                    return (T) Integer.valueOf(12);
                case TEXT_RENDERING_MODE:
                    return (T) Integer.valueOf(PdfCanvasConstants.TextRenderingMode.FILL);
                case TEXT_RISE:
                    return (T) Float.valueOf(0);
                case SPACING_RATIO:
                    return (T) Float.valueOf(0.75f);
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
    public void deleteOwnProperty(Property property) {
        properties.remove(property);
    }

    @Override
    public Type setProperty(Property property, Object value) {
        properties.put(property, value);
        return (Type) this;
    }

    /**
     * Gets the rootRenderer attribute, a specialized {@link IRenderer} that
     * acts as the root object that other {@link IRenderer renderers} descend
     * from.
     * 
     * @param <T> the (sub)type of the {@link RootRenderer}
     * @return the {@link RootRenderer} attribute
     */
    public <T extends RootRenderer> T getRenderer() {
        return (T) ensureRootRendererNotNull();
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param <T> the return type
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @return this object
     */
    public <T extends RootElement> T showTextAligned(String text, float x, float y, Property.TextAlignment textAlign) {
        return showTextAligned(text, x, y, textAlign, 0);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param <T> the return type
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     * @return this object
     */
    public <T extends RootElement> T showTextAligned(String text, float x, float y, Property.TextAlignment textAlign, float angle) {
        return showTextAligned(text, x, y, textAlign, Property.VerticalAlignment.BOTTOM, angle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param <T> the return type
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     * @return this object
     */
    public <T extends RootElement> T showTextAligned(String text, float x, float y, Property.TextAlignment textAlign, Property.VerticalAlignment vertAlign, float angle) {
        Paragraph p = new Paragraph(text);
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, vertAlign, angle);
    }

    /**
     * Convenience method to write a kerned text aligned about the specified point
     * @param <T> the return type
     * @param text text to be placed to the page
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle the angle of rotation applied to the text, in radians
     * @return this object
     */
    public <T extends RootElement> T showTextAlignedKerned(String text, float x, float y, Property.TextAlignment textAlign, Property.VerticalAlignment vertAlign, float angle) {
        Paragraph p = new Paragraph(text).setFontKerning(Property.FontKerning.YES);
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, vertAlign, angle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param <T> the return type
     * @param p paragraph of text to be placed to the page. By default it has no leading and is written in single line.
     *          Set width to write multiline text.
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @return this object
     */
    public <T extends RootElement> T showTextAligned(Paragraph p, float x, float y, Property.TextAlignment textAlign) {
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, Property.VerticalAlignment.BOTTOM, 0);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     * @param <T> the return type
     * @param p paragraph of text to be placed to the page. By default it has no leading and is written in single line.
     *          Set width to write multiline text.
     * @param x the point about which the text will be aligned and rotated
     * @param y the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @return this object
     */
    public <T extends RootElement> T showTextAligned(Paragraph p, float x, float y, Property.TextAlignment textAlign, Property.VerticalAlignment vertAlign) {
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, vertAlign, 0);
    }

        /**
         * Convenience method to write a text aligned about the specified point
         * @param <T> the return type
         * @param p paragraph of text to be placed to the page. By default it has no leading and is written in single line.
         *          Set width to write multiline text.
         * @param x the point about which the text will be aligned and rotated
         * @param y the point about which the text will be aligned and rotated
         * @param pageNumber the page number to write the text
         * @param textAlign horizontal alignment about the specified point
         * @param vertAlign vertical alignment about the specified point
         * @param angle the angle of rotation applied to the text, in radians
         * @return this object
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
        div.setRole(PdfName.Artifact);
        this.add(div);

        return (T) this;
    }

    protected abstract RootRenderer ensureRootRendererNotNull();

}
