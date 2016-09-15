/*
    $Id: d358ee4cb87c8f576c0babb72d280efaac05e27b $

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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the most common properties and behavior that are shared by most
 * {@link IRenderer} implementations. All default Renderers are subclasses of
 * this default implementation.
 */
public abstract class AbstractRenderer implements IRenderer {

    public static final float EPS = 1e-4f;
    public static final float INF = 1e6f;

    // TODO linkedList?
    protected List<IRenderer> childRenderers = new ArrayList<>();
    protected List<IRenderer> positionedRenderers = new ArrayList<>();
    protected IPropertyContainer modelElement;
    protected boolean flushed = false;
    protected LayoutArea occupiedArea;
    protected IRenderer parent;
    protected Map<Integer, Object> properties = new HashMap<>();
    protected boolean isLastRendererForModelElement = true;

    /**
     * Creates a renderer.
     */
    protected AbstractRenderer() {
    }

    /**
     * Creates a renderer for the specified layout element.
     *
     * @param modelElement the layout element that will be drawn by this renderer
     */
    protected AbstractRenderer(IElement modelElement) {
        this.modelElement = modelElement;
    }

    protected AbstractRenderer(AbstractRenderer other) {
        this.childRenderers = other.childRenderers;
        this.positionedRenderers = other.positionedRenderers;
        this.modelElement = other.modelElement;
        this.flushed = other.flushed;
        this.occupiedArea = other.occupiedArea.clone();
        this.parent = other.parent;
        this.properties.putAll(other.properties);
        this.isLastRendererForModelElement = other.isLastRendererForModelElement;
    }

    @Override
    public void addChild(IRenderer renderer) {
        // https://www.webkit.org/blog/116/webcore-rendering-iii-layout-basics
        // "The rules can be summarized as follows:"...
        Integer positioning = renderer.getProperty(Property.POSITION);
        if (positioning == null || positioning == LayoutPosition.RELATIVE || positioning == LayoutPosition.STATIC) {
            childRenderers.add(renderer);
            renderer.setParent(this);
        } else if (positioning == LayoutPosition.FIXED) {
            AbstractRenderer root = this;
            while (root.parent instanceof AbstractRenderer) {
                root = (AbstractRenderer) root.parent;
            }
            if (root == this) {
                positionedRenderers.add(renderer);
                renderer.setParent(this);
            } else {
                root.addChild(renderer);
            }
        }
    }

    @Override
    public IPropertyContainer getModelElement() {
        return modelElement;
    }

    @Override
    public List<IRenderer> getChildRenderers() {
        return childRenderers;
    }

    @Override
    public boolean hasProperty(int property) {
        return hasOwnProperty(property)
                || (modelElement != null && modelElement.hasProperty(property))
                || (parent != null && Property.isPropertyInherited(property) && parent.hasProperty(property));
    }

    @Override
    public boolean hasOwnProperty(int property) {
        return properties.containsKey(property);
    }

    @Override
    public void deleteOwnProperty(int property) {
        properties.remove(property);
    }

    /**
     * Deletes property from this very renderer, or in case the property is specified on its model element, the
     * property of the model element is deleted
     * @param property the property key to be deleted
     */
    public void deleteProperty(int property) {
        if (properties.containsKey(property)) {
            properties.remove(property);
        } else {
            if (modelElement != null) {
                modelElement.deleteOwnProperty(property);
            }
        }
    }

    @Override
    public <T1> T1 getProperty(int key) {
        Object property;
        if ((property = properties.get(key)) != null || properties.containsKey(key)) {
            return (T1) property;
        }
        if (modelElement != null && ((property = modelElement.getProperty(key)) != null || modelElement.hasProperty(key))) {
            return (T1) property;
        }
        // TODO in some situations we will want to check inheritance with additional info, such as parent and descendant.
        if (parent != null && Property.isPropertyInherited(key) && (property = parent.getProperty(key)) != null) {
            return (T1) property;
        }
        property = getDefaultProperty(key);
        if (property != null) {
            return (T1) property;
        }
        return modelElement != null ? (T1) modelElement.getDefaultProperty(key) : null;
    }

    @Override
    public <T1> T1 getOwnProperty(int property) {
        return (T1) properties.get(property);
    }

    @Override
    public <T1> T1 getProperty(int property, T1 defaultValue) {
        T1 result = getProperty(property);
        return result != null ? result : defaultValue;
    }

    @Override
    public void setProperty(int property, Object value) {
        properties.put(property, value);
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.POSITION:
                return (T1) Integer.valueOf(LayoutPosition.STATIC);
            default:
                return null;
        }
    }

    /**
     * Returns a property with a certain key, as a font object.
     *
     * @param property an {@link Property enum value}
     * @return a {@link PdfFont}
     */
    public PdfFont getPropertyAsFont(int property) {
        return getProperty(property);
    }

    /**
     * Returns a property with a certain key, as a color.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Color}
     */
    public Color getPropertyAsColor(int property) {
        Object object = getProperty(property);

        if(object == null) {
            return null;
        }

        if(object instanceof Color) {
            return (Color) object;
        } else if (object instanceof Background) {
            return ((Background)object).getColor();
        }

        throw new IllegalStateException("The type of property is unexpected.. property: " + property + ", class: " + object.getClass());
    }

    /**
     * Returns a property with a certain key, as a floating point value.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Float}
     */
    public Float getPropertyAsFloat(int property) {
        Number value = getProperty(property);
        return value != null ? value.floatValue() : null;
    }

    /**
     * Returns a property with a certain key, as a boolean value.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Boolean}
     */
    public Boolean getPropertyAsBoolean(int property) {
        return getProperty(property);
    }

    /**
     * Returns a property with a certain key, as an integer value.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Integer}
     */
    public Integer getPropertyAsInteger(int property) {
        Number value = getProperty(property);
        return value != null ? value.intValue() : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRenderer renderer : childRenderers) {
            sb.append(renderer.toString());
        }
        return sb.toString();
    }

    @Override
    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    @Override
    public void draw(DrawContext drawContext) {
        applyDestination(drawContext.getDocument());
        applyAction(drawContext.getDocument());

        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        drawBackground(drawContext);
        drawBorder(drawContext);
        drawChildren(drawContext);

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }

        flushed = true;
    }

    /**
     * Draws a background layer if it is defined by a key {@link Property#BACKGROUND}
     * in either the layout element or this {@link IRenderer} itself.
     *
     * @param drawContext the context (canvas, document, etc) of this drawing operation.
     */
    public void drawBackground(DrawContext drawContext) {
        Background background = getProperty(Property.BACKGROUND);
        if (background != null) {

            Rectangle bBox = getOccupiedAreaBBox();

            boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
            if (isTagged) {
                drawContext.getCanvas().openTag(new CanvasArtifact());
            }
            Rectangle backgroundArea = applyMargins(bBox, false);
            if (backgroundArea.getWidth() <= 0 || backgroundArea.getHeight() <= 0) {
                Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                logger.error(MessageFormat.format(LogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES, "background"));
                return;
            }
            drawContext.getCanvas().saveState().setFillColor(background.getColor()).
                    rectangle(backgroundArea.getX() - background.getExtraLeft(), backgroundArea.getY() - background.getExtraBottom(),
                            backgroundArea.getWidth() + background.getExtraLeft() + background.getExtraRight(),
                            backgroundArea.getHeight() + background.getExtraTop() + background.getExtraBottom()).
                    fill().restoreState();

            if (isTagged) {
                drawContext.getCanvas().closeTag();
            }
        }
    }

    /**
     * Performs the drawing operation for all {@link IRenderer children}
     * of this renderer.
     *
     * @param drawContext the context (canvas, document, etc) of this drawing operation.
     */
    public void drawChildren(DrawContext drawContext) {
        for (IRenderer child : childRenderers) {
            child.draw(drawContext);
        }
    }

    /**
     * Performs the drawing operation for the border of this renderer, if
     * defined by any of the {@link Property#BORDER} values in either the layout
     * element or this {@link IRenderer} itself.
     *
     * @param drawContext the context (canvas, document, etc) of this drawing operation.
     */
    public void drawBorder(DrawContext drawContext) {
        Border[] borders = getBorders();
        boolean gotBorders = false;

        for (Border border : borders)
            gotBorders = gotBorders || border != null;

        if (gotBorders) {
            float topWidth = borders[0] != null ? borders[0].getWidth() : 0;
            float rightWidth = borders[1] != null ? borders[1].getWidth() : 0;
            float bottomWidth = borders[2] != null ? borders[2].getWidth() : 0;
            float leftWidth = borders[3] != null ? borders[3].getWidth() : 0;

            Rectangle bBox = getBorderAreaBBox();
            if (bBox.getWidth() <= 0 || bBox.getHeight() <= 0) {
                Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                logger.error(MessageFormat.format(LogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES, "border"));
                return;
            }
            float x1 = bBox.getX();
            float y1 = bBox.getY();
            float x2 = bBox.getX() + bBox.getWidth();
            float y2 = bBox.getY() + bBox.getHeight();

            boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(new CanvasArtifact());
            }

            if (borders[0] != null) {
                canvas.saveState();
                borders[0].draw(canvas, x1, y2, x2, y2, leftWidth, rightWidth);
                canvas.restoreState();
            }
            if (borders[1] != null) {
                canvas.saveState();
                borders[1].draw(canvas, x2, y2, x2, y1, topWidth, bottomWidth);
                canvas.restoreState();
            }
            if (borders[2] != null) {
                canvas.saveState();
                borders[2].draw(canvas, x2, y1, x1, y1, rightWidth, leftWidth);
                canvas.restoreState();
            }
            if (borders[3] != null) {
                canvas.saveState();
                borders[3].draw(canvas, x1, y1, x1, y2, bottomWidth, topWidth);
                canvas.restoreState();
            }

            if (isTagged) {
                canvas.closeTag();
            }
        }
    }

    @Override
    public boolean isFlushed() {
        return flushed;
    }

    @Override
    public IRenderer setParent(IRenderer parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public void move(float dxRight, float dyUp) {
        occupiedArea.getBBox().moveRight(dxRight);
        occupiedArea.getBBox().moveUp(dyUp);
        for (IRenderer childRenderer : childRenderers) {
            childRenderer.move(dxRight, dyUp);
        }
    }

    /**
     * Gets all rectangles that this {@link IRenderer} can draw upon in the given area.
     *
     * @param area a physical area on the {@link DrawContext}
     * @return a list of {@link Rectangle rectangles}
     */
    public List<Rectangle> initElementAreas(LayoutArea area) {
        return Collections.singletonList(area.getBBox());
    }

    /**
     * Gets the bounding box that contains all content written to the
     * {@link DrawContext} by this {@link IRenderer}.
     *
     * @return the smallest {@link Rectangle} that surrounds the content
     */
    public Rectangle getOccupiedAreaBBox() {
        return occupiedArea.getBBox().clone();
    }

    /**
     * Gets the border box of a renderer.
     * This is a box used to draw borders.
     *
     * @return border box of a renderer
     */
    public Rectangle getBorderAreaBBox() {
        Rectangle rect = getOccupiedAreaBBox();
        applyMargins(rect, false);
        applyBorderBox(rect, false);
        return rect;
    }

    public Rectangle getInnerAreaBBox() {
        Rectangle rect = getOccupiedAreaBBox();
        applyMargins(rect, false);
        applyBorderBox(rect, false);
        applyPaddings(rect, false);
        return rect;
    }

    protected Float retrieveWidth(float parentBoxWidth) {
        return retrieveUnitValue(parentBoxWidth, Property.WIDTH);
    }

    protected Float retrieveHeight() {
        return getProperty(Property.HEIGHT);
    }

    protected Float retrieveUnitValue(float basePercentValue, int property) {
        UnitValue value = getProperty(property);
        if (value != null) {
            if (value.getUnitType() == UnitValue.POINT) {
                return value.getValue();
            } else if (value.getUnitType() == UnitValue.PERCENT) {
                return value.getValue() * basePercentValue / 100;
            } else {
                throw new IllegalStateException("invalid unit type");
            }
        } else {
            return null;
        }
    }

    //TODO is behavior of copying all properties in split case common to all renderers?
    protected Map<Integer, Object> getOwnProperties() {
        return properties;
    }

    protected void addAllProperties(Map<Integer, Object> properties) {
        this.properties.putAll(properties);
    }

    /**
     * Gets the first yLine of the nested children recursively. E.g. for a list, this will be the yLine of the
     * first item (if the first item is indeed a paragraph).
     * NOTE: this method will no go further than the first child.
     * Returns null if there is no text found.
     */
    protected Float getFirstYLineRecursively() {
        if (childRenderers.size() == 0) {
            return null;
        }
        return ((AbstractRenderer) childRenderers.get(0)).getFirstYLineRecursively();
    }

    protected Rectangle applyMargins(Rectangle rect, boolean reverse) {
        if (isPositioned())
            return rect;

        return rect.applyMargins(getPropertyAsFloat(Property.MARGIN_TOP), getPropertyAsFloat(Property.MARGIN_RIGHT),
                getPropertyAsFloat(Property.MARGIN_BOTTOM), getPropertyAsFloat(Property.MARGIN_LEFT), reverse);
    }

    protected Rectangle applyPaddings(Rectangle rect, boolean reverse) {
        return rect.applyMargins(getPropertyAsFloat(Property.PADDING_TOP), getPropertyAsFloat(Property.PADDING_RIGHT),
                getPropertyAsFloat(Property.PADDING_BOTTOM), getPropertyAsFloat(Property.PADDING_LEFT), reverse);
    }

    protected Rectangle applyBorderBox(Rectangle rect, boolean reverse) {
        Border[] borders = getBorders();
        float topWidth = borders[0] != null ? borders[0].getWidth() : 0;
        float rightWidth = borders[1] != null ? borders[1].getWidth() : 0;
        float bottomWidth = borders[2] != null ? borders[2].getWidth() : 0;
        float leftWidth = borders[3] != null ? borders[3].getWidth() : 0;
        return rect.applyMargins(topWidth, rightWidth, bottomWidth, leftWidth, reverse);
    }

    protected void applyAbsolutePositioningTranslation(boolean reverse) {
        float top = getPropertyAsFloat(Property.TOP);
        float bottom = getPropertyAsFloat(Property.BOTTOM);
        float left = getPropertyAsFloat(Property.LEFT);
        float right = getPropertyAsFloat(Property.RIGHT);

        int reverseMultiplier = reverse ? -1 : 1;

        float dxRight = left != 0 ? left * reverseMultiplier : -right * reverseMultiplier;
        float dyUp = top != 0 ? -top * reverseMultiplier : bottom * reverseMultiplier;

        if (dxRight != 0 || dyUp != 0)
            move(dxRight, dyUp);
    }

    protected void applyDestination(PdfDocument document) {
        String destination = getProperty(Property.DESTINATION);
        if (destination != null) {
            PdfArray array = new PdfArray();
            array.add(document.getPage(occupiedArea.getPageNumber()).getPdfObject());
            array.add(PdfName.XYZ);
            array.add(new PdfNumber(occupiedArea.getBBox().getX()));
            array.add(new PdfNumber(occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight()));
            array.add(new PdfNumber(1));
            document.addNamedDestination(destination, array.makeIndirect(document));

            deleteProperty(Property.DESTINATION);
        }
    }

    protected void applyAction(PdfDocument document) {
        PdfAction action = getProperty(Property.ACTION);
        if (action != null) {
            PdfLinkAnnotation link = new PdfLinkAnnotation(getOccupiedArea().getBBox());
            link.setAction(action);
            Border border = getProperty(Property.BORDER);
            if (border != null) {
                link.setBorder(new PdfArray(new float[]{0, 0, border.getWidth()}));
            } else {
                link.setBorder(new PdfArray(new float[]{0, 0, 0}));
            }
            document.getPage(getOccupiedArea().getPageNumber()).addAnnotation(link);
        }
    }

    protected boolean isNotFittingHeight(LayoutArea layoutArea) {
        Rectangle area = applyMargins(layoutArea.getBBox().clone(), false);
        area = applyPaddings(area, false);
        return !isPositioned() && occupiedArea.getBBox().getHeight() > area.getHeight();
    }

    protected boolean isPositioned() {
        Object positioning = getProperty(Property.POSITION);
        return Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    protected boolean isFixedLayout() {
        Object positioning = getProperty(Property.POSITION);
        return Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    protected void alignChildHorizontally(IRenderer childRenderer, float availableWidth) {
        HorizontalAlignment horizontalAlignment = childRenderer.getProperty(Property.HORIZONTAL_ALIGNMENT);
        if (horizontalAlignment != null && horizontalAlignment != HorizontalAlignment.LEFT) {
            float freeSpace = availableWidth - childRenderer.getOccupiedArea().getBBox().getWidth();
            switch (horizontalAlignment) {
                case RIGHT:
                    childRenderer.move(freeSpace, 0);
                    break;
                case CENTER:
                    childRenderer.move(freeSpace / 2, 0);
                    break;
            }
        }
    }

    /**
     * Gets borders of the element in the specified order: top, right, bottom, left.
     *
     * @return an array of BorderDrawer objects.
     * In case when certain border isn't set <code>Property.BORDER</code> is used,
     * and if <code>Property.BORDER</code> is also not set then <code>null<code/> is returned
     * on position of this border
     */
    protected Border[] getBorders() {
        Border border = getProperty(Property.BORDER);
        Border topBorder = getProperty(Property.BORDER_TOP);
        Border rightBorder = getProperty(Property.BORDER_RIGHT);
        Border bottomBorder = getProperty(Property.BORDER_BOTTOM);
        Border leftBorder = getProperty(Property.BORDER_LEFT);

        Border[] borders = {topBorder, rightBorder, bottomBorder, leftBorder};

        for (int i = 0; i < borders.length; ++i) {
            if (borders[i] == null)
                borders[i] = border;
        }

        return borders;
    }

    protected AbstractRenderer setBorders(Border border, int borderNumber) {
        switch (borderNumber) {
            case 0:
                setProperty(Property.BORDER_TOP, border);
                break;
            case 1:
                setProperty(Property.BORDER_RIGHT, border);
                break;
            case 2:
                setProperty(Property.BORDER_BOTTOM, border);
                break;
            case 3:
                setProperty(Property.BORDER_LEFT, border);
                break;
        }

        return this;
    }
}
