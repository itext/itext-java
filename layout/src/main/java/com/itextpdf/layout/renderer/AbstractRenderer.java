/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontFamilySplitter;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * Defines the most common properties and behavior that are shared by most
 * {@link IRenderer} implementations. All default Renderers are subclasses of
 * this default implementation.
 */
public abstract class AbstractRenderer implements IRenderer {

    /**
     * The maximum difference between {@link Rectangle} coordinates to consider rectangles equal
     */
    public static final float EPS = 1e-4f;

    /**
     * The infinity value which is used while layouting
     */
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
        this.occupiedArea = other.occupiedArea != null ? other.occupiedArea.clone() : null;
        this.parent = other.parent;
        this.properties.putAll(other.properties);
        this.isLastRendererForModelElement = other.isLastRendererForModelElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        // https://www.webkit.org/blog/116/webcore-rendering-iii-layout-basics
        // "The rules can be summarized as follows:"...
        Integer positioning = renderer.<Integer>getProperty(Property.POSITION);
        if (positioning == null || positioning == LayoutPosition.RELATIVE || positioning == LayoutPosition.STATIC) {
            childRenderers.add(renderer);
        } else if (positioning == LayoutPosition.FIXED) {
            AbstractRenderer root = this;
            while (root.parent instanceof AbstractRenderer) {
                root = (AbstractRenderer) root.parent;
            }
            if (root == this) {
                positionedRenderers.add(renderer);
            } else {
                root.addChild(renderer);
            }
        } else if (positioning == LayoutPosition.ABSOLUTE) {
            // For position=absolute, if none of the top, bottom, left, right properties are provided,
            // the content should be displayed in the flow of the current content, not overlapping it.
            // The behavior is just if it would be statically positioned except it does not affect other elements
            AbstractRenderer positionedParent = this;
            boolean noPositionInfo = AbstractRenderer.noAbsolutePositionInfo(renderer);
            while (!positionedParent.isPositioned() && !noPositionInfo) {
                IRenderer parent = positionedParent.parent;
                if (parent instanceof AbstractRenderer) {
                    positionedParent = (AbstractRenderer) parent;
                } else {
                    break;
                }
            }
            if (positionedParent == this) {
                positionedRenderers.add(renderer);
            } else {
                positionedParent.addChild(renderer);
            }
        }

        // Fetch positioned renderers from non-positioned child because they might be stuck there because child's parent was null previously
        if (renderer instanceof AbstractRenderer && !((AbstractRenderer) renderer).isPositioned() && ((AbstractRenderer) renderer).positionedRenderers.size() > 0) {
            // For position=absolute, if none of the top, bottom, left, right properties are provided,
            // the content should be displayed in the flow of the current content, not overlapping it.
            // The behavior is just if it would be statically positioned except it does not affect other elements
            int pos = 0;
            List<IRenderer> childPositionedRenderers = ((AbstractRenderer) renderer).positionedRenderers;
            while (pos < childPositionedRenderers.size()) {
                if (AbstractRenderer.noAbsolutePositionInfo(childPositionedRenderers.get(pos))) {
                    pos++;
                } else {
                    positionedRenderers.add(childPositionedRenderers.get(pos));
                    childPositionedRenderers.remove(pos);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPropertyContainer getModelElement() {
        return modelElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRenderer> getChildRenderers() {
        return childRenderers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasProperty(int property) {
        return hasOwnProperty(property)
                || (modelElement != null && modelElement.hasProperty(property))
                || (parent != null && Property.isPropertyInherited(property) && parent.hasProperty(property));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOwnProperty(int property) {
        return properties.containsKey(property);
    }

    /**
     * Checks if this renderer or its model element have the specified property,
     * i.e. if it was set to this very element or its very model element earlier.
     * @param property the property to be checked
     * @return {@code true} if this instance or its model element have given own property, {@code false} otherwise
     */
    public boolean hasOwnOrModelProperty(int property) {
        return properties.containsKey(property) || (null != getModelElement() && getModelElement().hasProperty(property));
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getProperty(int key) {
        Object property;
        if ((property = properties.get(key)) != null || properties.containsKey(key)) {
            return (T1) property;
        }
        if (modelElement != null && ((property = modelElement.<T1>getProperty(key)) != null || modelElement.hasProperty(key))) {
            return (T1) property;
        }
        // TODO in some situations we will want to check inheritance with additional info, such as parent and descendant.
        if (parent != null && Property.isPropertyInherited(key) && (property = parent.<T1>getProperty(key)) != null) {
            return (T1) property;
        }
        property = this.<T1>getDefaultProperty(key);
        if (property != null) {
            return (T1) property;
        }
        return modelElement != null ? modelElement.<T1>getDefaultProperty(key) : (T1) (Object) null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getOwnProperty(int property) {
        return (T1) properties.get(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getProperty(int property, T1 defaultValue) {
        T1 result = this.<T1>getProperty(property);
        return result != null ? result : defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(int property, Object value) {
        properties.put(property, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        return (T1) (Object) null;
    }

    /**
     * Returns a property with a certain key, as a font object.
     *
     * @param property an {@link Property enum value}
     * @return a {@link PdfFont}
     */
    public PdfFont getPropertyAsFont(int property) {
        return this.<PdfFont>getProperty(property);
    }

    /**
     * Returns a property with a certain key, as a color.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Color}
     */
    public Color getPropertyAsColor(int property) {
        return this.<Color>getProperty(property);
    }

    /**
     * Returns a property with a certain key, as a {@link TransparentColor}.
     *
     * @param property an {@link Property enum value}
     * @return a {@link TransparentColor}
     */
    public TransparentColor getPropertyAsTransparentColor(int property) {
        return this.<TransparentColor>getProperty(property);
    }

    /**
     * Returns a property with a certain key, as a floating point value.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Float}
     */
    public Float getPropertyAsFloat(int property) {
        Number value = this.<Number>getProperty(property);
        return value != null ? value.floatValue() : null;
    }

    /**
     * Returns a property with a certain key, as a floating point value.
     *
     * @param property an {@link Property enum value}
     * @param defaultValue default value to be returned if property is not found
     * @return a {@link Float}
     */
    public Float getPropertyAsFloat(int property, Float defaultValue) {
        Number value = this.<Number>getProperty(property, defaultValue);
        return value != null ? value.floatValue() : null;
    }

    /**
     * Returns a property with a certain key, as a boolean value.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Boolean}
     */
    public Boolean getPropertyAsBoolean(int property) {
        return this.<Boolean>getProperty(property);
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

    /**
     * Returns a string representation of the renderer.
     *
     * @return a {@link String}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRenderer renderer : childRenderers) {
            sb.append(renderer.toString());
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        applyDestinationsAndAnnotation(drawContext);

        boolean relativePosition = isRelativePosition();
        if (relativePosition) {
            applyRelativePositioningTranslation(false);
        }

        beginElementOpacityApplying(drawContext);
        drawBackground(drawContext);
        drawBorder(drawContext);
        drawChildren(drawContext);
        drawPositionedChildren(drawContext);
        endElementOpacityApplying(drawContext);

        if (relativePosition) {
            applyRelativePositioningTranslation(true);
        }

        flushed = true;
    }

    protected void beginElementOpacityApplying(DrawContext drawContext) {
        Float opacity = this.getPropertyAsFloat(Property.OPACITY);
        if (opacity != null && opacity < 1f) {
            PdfExtGState extGState = new PdfExtGState();
            extGState
                    .setStrokeOpacity((float) opacity)
                    .setFillOpacity((float) opacity);
            drawContext.getCanvas()
                    .saveState()
                    .setExtGState(extGState);
        }
    }

    protected void endElementOpacityApplying(DrawContext drawContext) {
        Float opacity = this.getPropertyAsFloat(Property.OPACITY);
        if (opacity != null && opacity < 1f) {
            drawContext.getCanvas().restoreState();
        }
    }

    /**
     * Draws a background layer if it is defined by a key {@link Property#BACKGROUND}
     * in either the layout element or this {@link IRenderer} itself.
     *
     * @param drawContext the context (canvas, document, etc) of this drawing operation.
     */
    public void drawBackground(DrawContext drawContext) {
        Background background = this.<Background>getProperty(Property.BACKGROUND);
        BackgroundImage backgroundImage = this.<BackgroundImage>getProperty(Property.BACKGROUND_IMAGE);
        if (background != null || backgroundImage != null) {
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
            if (background != null) {
                TransparentColor backgroundColor = new TransparentColor(background.getColor(), background.getOpacity());
                drawContext.getCanvas().saveState().setFillColor(backgroundColor.getColor());
                backgroundColor.applyFillTransparency(drawContext.getCanvas());
                drawContext.getCanvas()
                        .rectangle(backgroundArea.getX() - background.getExtraLeft(), backgroundArea.getY() - background.getExtraBottom(),
                                backgroundArea.getWidth() + background.getExtraLeft() + background.getExtraRight(),
                                backgroundArea.getHeight() + background.getExtraTop() + background.getExtraBottom()).
                        fill().restoreState();

            }
            if (backgroundImage != null && backgroundImage.getImage() != null) {
                applyBorderBox(backgroundArea, false);
                Rectangle imageRectangle = new Rectangle(backgroundArea.getX(), backgroundArea.getTop() - backgroundImage.getImage().getHeight(),
                        backgroundImage.getImage().getWidth(), backgroundImage.getImage().getHeight());
                if (imageRectangle.getWidth() <= 0 || imageRectangle.getHeight() <= 0) {
                    Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                    logger.error(MessageFormat.format(LogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES, "background-image"));
                    return;
                }
                applyBorderBox(backgroundArea, true);
                drawContext.getCanvas().saveState().rectangle(backgroundArea).clip().newPath();
                float initialX = backgroundImage.isRepeatX() ? imageRectangle.getX() - imageRectangle.getWidth() : imageRectangle.getX();
                float initialY = backgroundImage.isRepeatY() ? imageRectangle.getTop() : imageRectangle.getY();
                imageRectangle.setY(initialY);
                do {
                    imageRectangle.setX(initialX);
                    do {
                        drawContext.getCanvas().addXObject(backgroundImage.getImage(), imageRectangle);
                        imageRectangle.moveRight(imageRectangle.getWidth());
                    } while (backgroundImage.isRepeatX() && imageRectangle.getLeft() < backgroundArea.getRight());
                    imageRectangle.moveDown(imageRectangle.getHeight());
                } while (backgroundImage.isRepeatY() && imageRectangle.getTop() > backgroundArea.getBottom());
                drawContext.getCanvas().restoreState();
            }
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
        List<IRenderer> waitingRenderers = new ArrayList<>();
        for (IRenderer child : childRenderers) {
            if (child.hasProperty(Property.FLOAT)) {
                waitingRenderers.add(child);
            } else {
                child.draw(drawContext);
            }
        }
        for (IRenderer waitingRenderer : waitingRenderers) {
            waitingRenderer.draw(drawContext);
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
            if (bBox.getWidth() < 0 || bBox.getHeight() < 0) {
                Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                logger.error(MessageFormat.format(LogMessageConstant.RECTANGLE_HAS_NEGATIVE_SIZE, "border"));
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
                borders[0].draw(canvas, x1, y2, x2, y2, Border.Side.TOP, leftWidth, rightWidth);
            }
            if (borders[1] != null) {
                borders[1].draw(canvas, x2, y2, x2, y1, Border.Side.RIGHT, topWidth, bottomWidth);
            }
            if (borders[2] != null) {
                borders[2].draw(canvas, x2, y1, x1, y1, Border.Side.BOTTOM, rightWidth, leftWidth);
            }
            if (borders[3] != null) {
                borders[3].draw(canvas, x1, y1, x1, y2, Border.Side.LEFT, bottomWidth, topWidth);
            }

            if (isTagged) {
                canvas.closeTag();
            }
        }
    }

    /**
     * Indicates whether this renderer is flushed or not, i.e. if {@link #draw(DrawContext)} has already
     * been called.
     * @return whether the renderer has been flushed
     * @see #draw
     */
    @Override
    public boolean isFlushed() {
        return flushed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer setParent(IRenderer parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Gets the parent of this {@link IRenderer}, if previously set by {@link #setParent(IRenderer)}
     * @return parent of the renderer
     */
    public IRenderer getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move(float dxRight, float dyUp) {
        occupiedArea.getBBox().moveRight(dxRight);
        occupiedArea.getBBox().moveUp(dyUp);
        for (IRenderer childRenderer : childRenderers) {
            childRenderer.move(dxRight, dyUp);
        }
        for (IRenderer childRenderer : positionedRenderers) {
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

    protected void applyDestinationsAndAnnotation(DrawContext drawContext) {
        applyDestination(drawContext.getDocument());
        applyAction(drawContext.getDocument());
        applyLinkAnnotation(drawContext.getDocument());
    }

    protected Float retrieveWidth(float parentBoxWidth) {
        return retrieveUnitValue(parentBoxWidth, Property.WIDTH);
    }

    protected Float retrieveHeight() {
        return this.<Float>getProperty(Property.HEIGHT);
    }

    protected Float retrieveMaxHeight() {
        return this.<Float>getProperty(Property.MAX_HEIGHT);
    }

    protected Float retrieveMinHeight() {
        return this.<Float>getProperty(Property.MIN_HEIGHT);
    }

    protected Float retrieveUnitValue(float basePercentValue, int property) {
        UnitValue value = this.<UnitValue>getProperty(property);
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

    /**
     * Applies margins of the renderer on the given rectangle
     *
     * @param rect a rectangle margins will be applied on.
     * @param reverse indicates whether margins will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     * @see #getMargins
     */
    protected Rectangle applyMargins(Rectangle rect, boolean reverse) {
        return this.applyMargins(rect, getMargins(), reverse);
    }

    /**
     * Applies given margins on the given rectangle
     *
     * @param rect a rectangle margins will be applied on.
     * @param margins the margins to be applied on the given rectangle
     * @param reverse indicates whether margins will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    protected Rectangle applyMargins(Rectangle rect, float[] margins, boolean reverse) {
        return rect.<Rectangle>applyMargins(margins[0], margins[1], margins[2], margins[3], reverse);
    }

    /**
     * Returns margins of the renderer
     *
     * @return a {@code float[]} margins of the renderer
     */
    protected float[] getMargins() {
        return new float[] {(float) this.getPropertyAsFloat(Property.MARGIN_TOP), (float) this.getPropertyAsFloat(Property.MARGIN_RIGHT),
                (float) this.getPropertyAsFloat(Property.MARGIN_BOTTOM), (float) this.getPropertyAsFloat(Property.MARGIN_LEFT)};
    }

    /**
     * Returns paddings of the renderer
     *
     * @return a {@code float[]} paddings of the renderer
     */
    protected float[] getPaddings() {
        return new float[] {(float) this.getPropertyAsFloat(Property.PADDING_TOP), (float) this.getPropertyAsFloat(Property.PADDING_RIGHT),
                (float) this.getPropertyAsFloat(Property.PADDING_BOTTOM), (float) this.getPropertyAsFloat(Property.PADDING_LEFT)};
    }

    /**
     * Applies paddings of the renderer on the given rectangle
     *
     * @param rect a rectangle paddings will be applied on.
     * @param reverse indicates whether paddings will be applied
     *                inside (in case of false) or outside (in case of false) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     * @see #getPaddings
     */
    protected Rectangle applyPaddings(Rectangle rect, boolean reverse) {
        return applyPaddings(rect, getPaddings(), reverse);
    }

    /**
     * Applies given paddings on the given rectangle
     *
     * @param rect a rectangle paddings will be applied on.
     * @param paddings the paddings to be applied on the given rectangle
     * @param reverse indicates whether paddings will be applied
     *                inside (in case of false) or outside (in case of false) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    protected Rectangle applyPaddings(Rectangle rect, float[] paddings, boolean reverse) {
        return rect.<Rectangle>applyMargins(paddings[0], paddings[1], paddings[2], paddings[3], reverse);
    }

    /**
     * Applies the border box of the renderer on the given rectangle
     * If the border of a certain side is null, the side will remain as it was.
     *
     * @param rect a rectangle the border box will be applied on.
     * @param reverse indicates whether the border box will be applied
     *                inside (in case of false) or outside (in case of false) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     * @see #getBorders
     */
    protected Rectangle applyBorderBox(Rectangle rect, boolean reverse) {
        Border[] borders = getBorders();
        return applyBorderBox(rect, borders, reverse);
    }

    /**
     * Applies the given border box (borders) on the given rectangle
     *
     * @param rect a rectangle paddings will be applied on.
     * @param borders the {@link Border borders} to be applied on the given rectangle
     * @param reverse indicates whether the border box will be applied
                      * inside (in case of false) or outside (in case of false) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    protected Rectangle applyBorderBox(Rectangle rect, Border[] borders, boolean reverse) {
        float topWidth = borders[0] != null ? borders[0].getWidth() : 0;
        float rightWidth = borders[1] != null ? borders[1].getWidth() : 0;
        float bottomWidth = borders[2] != null ? borders[2].getWidth() : 0;
        float leftWidth = borders[3] != null ? borders[3].getWidth() : 0;
        return rect.<Rectangle>applyMargins(topWidth, rightWidth, bottomWidth, leftWidth, reverse);
    }

    protected void applyAbsolutePosition(Rectangle rect) {
        Float top = this.getPropertyAsFloat(Property.TOP);
        Float bottom = this.getPropertyAsFloat(Property.BOTTOM);
        Float left = this.getPropertyAsFloat(Property.LEFT);
        Float right = this.getPropertyAsFloat(Property.RIGHT);

        float initialHeight = rect.getHeight();
        float initialWidth = rect.getWidth();

        Float minHeight = this.getPropertyAsFloat(Property.MIN_HEIGHT);

        if (minHeight != null && rect.getHeight() < (float)minHeight) {
            float difference = (float)minHeight - rect.getHeight();
            rect.moveDown(difference).setHeight(rect.getHeight() + difference);
        }

        if (top != null) {
            rect.setHeight(rect.getHeight() - (float)top);
        }
        if (left != null) {
            rect.setX(rect.getX() + (float)left).setWidth(rect.getWidth() - (float)left);
        }

        if (right != null) {
            UnitValue width = this.<UnitValue>getProperty(Property.WIDTH);
            if (left == null && width != null) {
                float widthValue = width.isPointValue() ? width.getValue() : (width.getValue() * initialWidth);
                float placeLeft = rect.getWidth() - widthValue;
                if (placeLeft > 0) {
                    float computedRight = Math.min(placeLeft, (float)right);
                    rect.setX(rect.getX() + rect.getWidth() - computedRight - widthValue);
                }
            } else if (width == null) {
                rect.setWidth(rect.getWidth() - (float)right);
            }
        }

        if (bottom != null) {
            if (minHeight != null) {
                rect.setHeight((float)minHeight + (float)bottom);
            } else {
                float minHeightValue = rect.getHeight() - (float)bottom;
                Float currentMaxHeight = this.getPropertyAsFloat(Property.MAX_HEIGHT);
                if (currentMaxHeight != null) {
                    minHeightValue = Math.min(minHeightValue, (float)currentMaxHeight);
                }
                setProperty(Property.MIN_HEIGHT, minHeightValue);
            }
        }
    }

    protected void applyRelativePositioningTranslation(boolean reverse) {
        float top = (float)this.getPropertyAsFloat(Property.TOP, 0f);
        float bottom = (float)this.getPropertyAsFloat(Property.BOTTOM, 0f);
        float left = (float)this.getPropertyAsFloat(Property.LEFT, 0f);
        float right = (float)this.getPropertyAsFloat(Property.RIGHT, 0f);

        int reverseMultiplier = reverse ? -1 : 1;

        float dxRight = left != 0 ? left * reverseMultiplier : -right * reverseMultiplier;
        float dyUp = top != 0 ? -top * reverseMultiplier : bottom * reverseMultiplier;

        if (dxRight != 0 || dyUp != 0)
            move(dxRight, dyUp);
    }

    protected void applyDestination(PdfDocument document) {
        String destination = this.<String>getProperty(Property.DESTINATION);
        if (destination != null) {
            PdfArray array = new PdfArray();
            array.add(document.getPage(occupiedArea.getPageNumber()).getPdfObject());
            array.add(PdfName.XYZ);
            array.add(new PdfNumber(occupiedArea.getBBox().getX()));
            array.add(new PdfNumber(occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight()));
            array.add(new PdfNumber(0));
            document.addNamedDestination(destination, array.makeIndirect(document));

            deleteProperty(Property.DESTINATION);
        }
    }

    protected void applyAction(PdfDocument document) {
        PdfAction action = this.<PdfAction>getProperty(Property.ACTION);
        if (action != null) {
            PdfLinkAnnotation link = this.<PdfLinkAnnotation>getProperty(Property.LINK_ANNOTATION);
            if (link == null) {
                link = new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0));
                Border border = this.<Border>getProperty(Property.BORDER);
                if (border != null) {
                    link.setBorder(new PdfArray(new float[]{0, 0, border.getWidth()}));
                } else {
                    link.setBorder(new PdfArray(new float[]{0, 0, 0}));
                }
                setProperty(Property.LINK_ANNOTATION, link);
            }
            link.setAction(action);
        }
    }

    protected void applyLinkAnnotation(PdfDocument document) {
        PdfLinkAnnotation linkAnnotation = this.<PdfLinkAnnotation>getProperty(Property.LINK_ANNOTATION);
        if (linkAnnotation != null) {
            Rectangle pdfBBox = calculateAbsolutePdfBBox();
            linkAnnotation.setRectangle(new PdfArray(pdfBBox));

            PdfPage page = document.getPage(occupiedArea.getPageNumber());
            page.addAnnotation(linkAnnotation);
        }
    }

    MinMaxWidth getMinMaxWidth(float availableWidth) {
        return MinMaxWidthUtils.countDefaultMinMaxWidth(this, availableWidth);
    }

    /**
     * @deprecated Use {@link #isNotFittingLayoutArea(LayoutArea)} instead.
     */
    @Deprecated
    protected boolean isNotFittingHeight(LayoutArea layoutArea) {
        return isNotFittingLayoutArea(layoutArea);
    }

    protected boolean isNotFittingLayoutArea(LayoutArea layoutArea) {
        return !isPositioned() && (occupiedArea.getBBox().getHeight() > layoutArea.getBBox().getHeight() || occupiedArea.getBBox().getWidth() > layoutArea.getBBox().getWidth());
    }

    /**
     * Indicates whether the renderer's position is fixed or not.
     *
     * @return a {@code boolean}
     */
    protected boolean isPositioned() {
        return !isStaticLayout();
    }

    /**
     * Indicates whether the renderer's position is fixed or not.
     *
     * @return a {@code boolean}
     */
    protected boolean isFixedLayout() {
        Object positioning = this.<Object>getProperty(Property.POSITION);
        return Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    protected boolean isStaticLayout() {
        Object positioning = this.<Object>getProperty(Property.POSITION);
        return positioning == null || Integer.valueOf(LayoutPosition.STATIC).equals(positioning);
    }

    protected boolean isRelativePosition() {
        Integer positioning = this.getPropertyAsInteger(Property.POSITION);
        return Integer.valueOf(LayoutPosition.RELATIVE).equals(positioning);
    }

    protected boolean isAbsolutePosition() {
        Integer positioning = this.getPropertyAsInteger(Property.POSITION);
        return Integer.valueOf(LayoutPosition.ABSOLUTE).equals(positioning);
    }

    protected boolean isKeepTogether() {
        return Boolean.TRUE.equals(getPropertyAsBoolean(Property.KEEP_TOGETHER));
    }

    @Deprecated
    protected void alignChildHorizontally(IRenderer childRenderer, float availableWidth) {
        HorizontalAlignment horizontalAlignment = childRenderer.<HorizontalAlignment>getProperty(Property.HORIZONTAL_ALIGNMENT);
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

    // Note! The second parameter is here on purpose. Currently occupied area is passed as a value of this parameter in
    // BlockRenderer, but actually, the block can have many areas, and occupied area will be the common area of sub-areas,
    // whereas child element alignment should be performed area-wise.
    protected void alignChildHorizontally(IRenderer childRenderer, Rectangle currentArea) {
        float availableWidth = currentArea.getWidth();
        HorizontalAlignment horizontalAlignment = childRenderer.<HorizontalAlignment>getProperty(Property.HORIZONTAL_ALIGNMENT);
        if (horizontalAlignment != null && horizontalAlignment != HorizontalAlignment.LEFT) {
            float freeSpace = availableWidth - childRenderer.getOccupiedArea().getBBox().getWidth();
            FloatPropertyValue floatPropertyValue = childRenderer.<FloatPropertyValue>getProperty(Property.FLOAT);
            if (FloatPropertyValue.RIGHT.equals(floatPropertyValue)) {
                freeSpace = calculateFreeSpaceIfFloatPropertyPresent(freeSpace, childRenderer, currentArea);
            }

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
        Border border = this.<Border>getProperty(Property.BORDER);
        Border topBorder = this.<Border>getProperty(Property.BORDER_TOP);
        Border rightBorder = this.<Border>getProperty(Property.BORDER_RIGHT);
        Border bottomBorder = this.<Border>getProperty(Property.BORDER_BOTTOM);
        Border leftBorder = this.<Border>getProperty(Property.BORDER_LEFT);

        Border[] borders = {topBorder, rightBorder, bottomBorder, leftBorder};

        if (!hasOwnOrModelProperty(Property.BORDER_TOP)) {
            borders[0] = border;
        }
        if (!hasOwnOrModelProperty(Property.BORDER_RIGHT)) {
            borders[1] = border;
        }
        if (!hasOwnOrModelProperty(Property.BORDER_BOTTOM)) {
            borders[2] = border;
        }
        if (!hasOwnOrModelProperty(Property.BORDER_LEFT)) {
            borders[3] = border;
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

    /**
     * Calculates the bounding box of the content in the coordinate system of the pdf entity on which content is placed,
     * e.g. document page or form xObject. This is particularly useful for the cases when element is nested in the rotated
     * element.
     * @return a {@link Rectangle} which is a bbox of the content not relative to the parent's layout area but rather to
     * the some pdf entity coordinate system.
     */
    protected Rectangle calculateAbsolutePdfBBox() {
        Rectangle contentBox = getOccupiedAreaBBox();
        List<Point> contentBoxPoints = rectangleToPointsList(contentBox);
        AbstractRenderer renderer = this;
        while (renderer.parent != null) {
            if (renderer instanceof BlockRenderer) {
                Float angle = renderer.<Float>getProperty(Property.ROTATION_ANGLE);
                if (angle != null) {
                    BlockRenderer blockRenderer = (BlockRenderer) renderer;
                    AffineTransform rotationTransform = blockRenderer.createRotationTransformInsideOccupiedArea();
                    transformPoints(contentBoxPoints, rotationTransform);
                }
            }

            renderer = (AbstractRenderer) renderer.parent;
        }

        return calculateBBox(contentBoxPoints);
    }

    /**
     * Calculates bounding box around points.
     * @param points list of the points calculated bbox will enclose.
     * @return array of float values which denote left, bottom, right, top lines of bbox in this specific order
     */
    protected Rectangle calculateBBox(List<Point> points) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (Point p : points) {
            minX = Math.min(p.getX(), minX);
            minY = Math.min(p.getY(), minY);
            maxX = Math.max(p.getX(), maxX);
            maxY = Math.max(p.getY(), maxY);
        }
        return new Rectangle((float) minX, (float) minY, (float) (maxX - minX), (float) (maxY - minY));
    }

    protected List<Point> rectangleToPointsList(Rectangle rect) {
        List<Point> points = new ArrayList<>();
        points.addAll(Arrays.asList(new Point(rect.getLeft(), rect.getBottom()), new Point(rect.getRight(), rect.getBottom()),
                new Point(rect.getRight(), rect.getTop()), new Point(rect.getLeft(), rect.getTop())));
        return points;
    }

    protected List<Point> transformPoints(List<Point> points, AffineTransform transform) {
        for (Point point : points) {
            transform.transform(point, point);
        }

        return points;
    }

    /**
     * This method calculates the shift needed to be applied to the points in order to position
     * upper and left borders of their bounding box at the given lines.
     * @param left x coordinate at which points bbox left border is to be aligned
     * @param top y coordinate at which points bbox upper border is to be aligned
     * @param points the points, which bbox will be aligned at the given position
     * @return array of two floats, where first element denotes x-coordinate shift and the second
     * element denotes y-coordinate shift which are needed to align points bbox at the given lines.
     */
    protected float[] calculateShiftToPositionBBoxOfPointsAt(float left, float top, List<Point> points) {
        double minX = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (Point point : points) {
            minX = Math.min(point.getX(), minX);
            maxY = Math.max(point.getY(), maxY);
        }

        float dx = (float) (left - minX);
        float dy = (float) (top - maxY);
        return new float[] {dx, dy};
    }

    protected void overrideHeightProperties() {
        Float height = this.<Float>getProperty(Property.HEIGHT);
        Float maxHeight = this.<Float>getProperty(Property.MAX_HEIGHT);
        Float minHeight = this.<Float>getProperty(Property.MIN_HEIGHT);
        if (null != height) {
            if (null == maxHeight || height < maxHeight) {
                maxHeight = height;
            } else {
                height = maxHeight;
            }
            if (null == minHeight || height > minHeight) {
                minHeight = height;
            }
        }
        if (null != maxHeight && null != minHeight && minHeight > maxHeight) {
            maxHeight = minHeight;
        }
        if (null != maxHeight) {
            setProperty(Property.MAX_HEIGHT, maxHeight);
        }
        if (null != minHeight) {
            setProperty(Property.MIN_HEIGHT, minHeight);
        }
    }

    /**
     * This method removes unnecessary float renderer areas.
     * @param floatRendererAreas
     */
    void removeUnnecessaryFloatRendererAreas(List<Rectangle> floatRendererAreas) {
        // TODO floats inside floats should be supported better on different level
        if (!hasProperty(Property.FLOAT) && !parent.hasProperty(Property.FLOAT)) {
            for (int i = floatRendererAreas.size() - 1; i >= 0; i--) {
                Rectangle floatRendererArea = floatRendererAreas.get(i);
                if (floatRendererArea.getY() >= occupiedArea.getBBox().getY()) {
                    floatRendererAreas.remove(i);
                }
            }
        }
    }

    LayoutArea applyFloatPropertyOnCurrentArea(List<Rectangle> floatRendererAreas, Rectangle parentBBox, float clearHeightCorrection) {
        LayoutArea editedArea = occupiedArea;
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);
        if (floatPropertyValue != null && !FloatPropertyValue.NONE.equals(floatPropertyValue)) {
            editedArea = occupiedArea.clone();
            floatRendererAreas.add(occupiedArea.getBBox());
            editedArea.getBBox().setY(parentBBox.getTop());
            editedArea.getBBox().setHeight(0);
        } else if (clearHeightCorrection > 0) {
            editedArea = occupiedArea.clone();
            editedArea.getBBox().moveDown(clearHeightCorrection);
            editedArea.getBBox().increaseHeight(clearHeightCorrection);
        }

        return editedArea;
    }

    void adjustLineAreaAccordingToFloatRenderers(List<Rectangle> floatRendererAreas, Rectangle layoutBox) {
        List<Rectangle> boxesAtYLevel = getBoxesAtYLevel(floatRendererAreas, layoutBox.getTop());
        if (boxesAtYLevel.isEmpty()) {
            return;
        }

        Rectangle[] lastLeftAndRightBoxes = findLastLeftAndRightBoxes(layoutBox, boxesAtYLevel);
        float left = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getRight() : layoutBox.getLeft();
        float right = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getLeft() : layoutBox.getRight();

        layoutBox.setX(left);
        layoutBox.setWidth(right - left);

    }

    void adjustFloatedTableLayoutBox(Rectangle layoutBox, Float tableWidth, List<Rectangle> floatRendererAreas, FloatPropertyValue floatPropertyValue) {
        if (floatPropertyValue.equals(FloatPropertyValue.LEFT)) {
            setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.LEFT);
        } else if (floatPropertyValue.equals(FloatPropertyValue.RIGHT)) {
            setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.RIGHT);
        }

        adjustBlockAreaAccordingToFloatRenderers(floatRendererAreas, layoutBox, tableWidth);
    }

    Float adjustFloatedBlockLayoutBox(Rectangle parentBBox, Float blockWidth, List<Rectangle> floatRendererAreas, FloatPropertyValue floatPropertyValue) {
        if (floatPropertyValue.equals(FloatPropertyValue.LEFT)) {
            setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.LEFT);
        } else if (floatPropertyValue.equals(FloatPropertyValue.RIGHT)) {
            setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.RIGHT);
        }
        float floatElemWidth;
        if (blockWidth != null) {
            float[] margins = getMargins();
            Border[] borders = getBorders();
            float[] paddings = getPaddings();
            float bordersWidth = (borders[1] != null ? borders[1].getWidth() : 0) + (borders[3] != null ? borders[3].getWidth() : 0);
            float additionalWidth = margins[1] + margins[3] + bordersWidth + paddings[1] + paddings[3];
            floatElemWidth = blockWidth + additionalWidth;
        } else {
            Float minHeightProperty = this.<Float>getProperty(Property.MIN_HEIGHT);
            setProperty(Property.FLOAT, FloatPropertyValue.NONE);
            MinMaxWidth minMaxWidth = getMinMaxWidth(parentBBox.getWidth());
            setProperty(Property.FLOAT, floatPropertyValue);
            if (minHeightProperty != null) {
                setProperty(Property.MIN_HEIGHT, minHeightProperty);
            } else {
                deleteProperty(Property.MIN_HEIGHT);
            }

            floatElemWidth = minMaxWidth.getChildrenMaxWidth() + minMaxWidth.getAdditionalWidth();
            blockWidth = minMaxWidth.getChildrenMaxWidth();
        }

        adjustBlockAreaAccordingToFloatRenderers(floatRendererAreas, parentBBox, floatElemWidth);
        return blockWidth + EPS; // TODO adding eps in order to workaround precision issues
    }

    void adjustBlockAreaAccordingToFloatRenderers(List<Rectangle> floatRendererAreas, Rectangle layoutBox, float blockWidth) {
        if (floatRendererAreas.isEmpty()) {
            return;
        }

        // TODO ensure zero-width boxes are not in the list
        // TODO float boxes are ordered by addition

        float currY = floatRendererAreas.get(floatRendererAreas.size() - 1).getTop();
        Rectangle[] lastLeftAndRightBoxes = null;
        float left = 0;
        float right = 0;
        while (lastLeftAndRightBoxes == null || right - left < blockWidth) {
            if (lastLeftAndRightBoxes != null) {
                if (FloatPropertyValue.LEFT.equals(this.<FloatPropertyValue>getProperty(Property.FLOAT))) {
                    currY = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getBottom() : lastLeftAndRightBoxes[1].getBottom();
                } else {
                    currY = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getBottom() : lastLeftAndRightBoxes[0].getBottom();
                }
            }
            layoutBox.setHeight(currY - layoutBox.getY());
            List<Rectangle> yLevelBoxes = getBoxesAtYLevel(floatRendererAreas, currY);
            if (yLevelBoxes.isEmpty()) {
                return;
            }
            lastLeftAndRightBoxes = findLastLeftAndRightBoxes(layoutBox, yLevelBoxes);
            left = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getRight() : layoutBox.getLeft();
            right = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getLeft() : layoutBox.getRight();
        }

        layoutBox.setX(left);
        layoutBox.setWidth(right - left);
    }

    private Rectangle[] findLastLeftAndRightBoxes(Rectangle layoutBox, List<Rectangle> yLevelBoxes) {
        Rectangle lastLeftFloatAtY = null;
        Rectangle lastRightFloatAtY = null;
        float left = layoutBox.getX();
        for (Rectangle box : yLevelBoxes) {
            if (left >= box.getLeft() && left < box.getRight()) {
                lastLeftFloatAtY = box;
                left = box.getRight();
            } else {
                lastRightFloatAtY = box;
            }
        }

        return new Rectangle[] {lastLeftFloatAtY, lastRightFloatAtY};
    }

    private List<Rectangle> getBoxesAtYLevel(List<Rectangle> floatRendererAreas, float currY) {
        List<Rectangle> yLevelBoxes = new ArrayList<>();
        for (Rectangle box : floatRendererAreas) {
            if (box.getBottom() < currY && box.getTop() >= currY) {
                yLevelBoxes.add(box);
            }
        }
        return yLevelBoxes;
    }

    float calculateClearHeightCorrection(List<Rectangle> floatRendererAreas, Rectangle parentBBox) {
        ClearPropertyValue clearPropertyValue = this.<ClearPropertyValue>getProperty(Property.CLEAR);
        float clearHeightCorrection = 0;
        if (floatRendererAreas.size() > 0 && clearPropertyValue != null) {
            float maxFloatHeight = 0;
            Rectangle theLowestFloatRectangle = null;
            float criticalPoint = parentBBox.getX() + parentBBox.getWidth();
            for (int i = floatRendererAreas.size() - 1; i >= 0; i--) {
                Rectangle floatRenderer = floatRendererAreas.get(i);
                if (((clearPropertyValue.equals(ClearPropertyValue.LEFT) && floatRenderer.getX() < criticalPoint) ||
                        (clearPropertyValue.equals(ClearPropertyValue.RIGHT) && floatRenderer.getX() + floatRenderer.getWidth() > criticalPoint))
                        || clearPropertyValue.equals(ClearPropertyValue.BOTH)) {
                    floatRendererAreas.remove(i);
                    if (clearPropertyValue.equals(ClearPropertyValue.LEFT) || clearPropertyValue.equals(ClearPropertyValue.BOTH)) {
                        if (floatRenderer.getY() + floatRenderer.getHeight() <= parentBBox.getY() + parentBBox.getHeight() &&
                                floatRenderer.getX() < parentBBox.getX()) {
                            parentBBox.moveLeft(floatRenderer.getWidth());
                            parentBBox.setWidth(parentBBox.getWidth() + floatRenderer.getWidth());
                        }
                    }

                    if (maxFloatHeight < floatRenderer.getHeight()) {
                        theLowestFloatRectangle = floatRenderer;
                        maxFloatHeight = floatRenderer.getHeight();
                    }
                }
            }

            if (theLowestFloatRectangle != null) {
                float contentAlongFloatHeight = theLowestFloatRectangle.getHeight() + theLowestFloatRectangle.getY() - parentBBox.getY() - parentBBox.getHeight();
                clearHeightCorrection = theLowestFloatRectangle.getHeight() - contentAlongFloatHeight;
                parentBBox.decreaseHeight(clearHeightCorrection);
            }
        }

        return clearHeightCorrection;
    }

    float calculateFreeSpaceIfFloatPropertyPresent(float freeSpace, IRenderer childRenderer, Rectangle currentArea) {
        return freeSpace - (childRenderer.getOccupiedArea().getBBox().getX() - currentArea.getX());
    }

    /**
     * Tries to get document from the root renderer if there is any.
     * @return
     */
    Document getDocument() {
        IRenderer parent = getParent();
        AbstractRenderer currentRenderer = this;
        while (parent != null) {
            if (parent instanceof AbstractRenderer) {
                currentRenderer = (AbstractRenderer) parent;
                parent = currentRenderer.getParent();
            } else {
                if (currentRenderer instanceof DocumentRenderer) {
                    return ((DocumentRenderer) currentRenderer).document;
                }
            }
        }
        if (currentRenderer instanceof DocumentRenderer) {
            return ((DocumentRenderer) currentRenderer).document;
        }
        return null;
    }

    static boolean noAbsolutePositionInfo(IRenderer renderer) {
        return !renderer.hasProperty(Property.TOP) && !renderer.hasProperty(Property.BOTTOM) && !renderer.hasProperty(Property.LEFT) && !renderer.hasProperty(Property.RIGHT);
    }

    void shrinkOccupiedAreaForAbsolutePosition() {
        // In case of absolute positioning and not specified left, right, width values, the parent box is shrunk to fit
        // the children. It does not occupy all the available width if it does not need to.
        if (isAbsolutePosition()) {
            Float left = this.getPropertyAsFloat(Property.LEFT);
            Float right = this.getPropertyAsFloat(Property.RIGHT);
            UnitValue width = this.<UnitValue>getProperty(Property.WIDTH);
            if (left == null && right == null && width == null) {
                occupiedArea.getBBox().setWidth(0);
            }
        }
    }

    void drawPositionedChildren(DrawContext drawContext) {
        for (IRenderer positionedChild : positionedRenderers) {
            positionedChild.draw(drawContext);
        }
    }

    FontCharacteristics createFontCharacteristics() {
        FontCharacteristics fc = new FontCharacteristics();
        if (this.hasProperty(Property.FONT_WEIGHT)) {
            fc.setFontWeight((String) this.<Object>getProperty(Property.FONT_WEIGHT));
        }
        if (this.hasProperty(Property.FONT_STYLE)) {
            fc.setFontStyle((String) this.<Object>getProperty(Property.FONT_STYLE));
        }
        return fc;
    }

    // This method is intended to get first valid PdfFont in this renderer, based of font property.
    // It is usually done for counting some layout characteristics like ascender or descender.
    // NOTE: It neither change Font Property of renderer, nor is guarantied to contain all glyphs used in renderer.
    PdfFont resolveFirstPdfFont() {
        Object font = this.<Object>getProperty(Property.FONT);
        if (font instanceof PdfFont) {
            return (PdfFont) font;
        } else if (font instanceof String) {
            FontProvider provider = this.<FontProvider>getProperty(Property.FONT_PROVIDER);
            if (provider == null) {
                throw new IllegalStateException("Invalid font type. FontProvider expected. Cannot resolve font with string value");
            }
            FontCharacteristics fc = createFontCharacteristics();
            return resolveFirstPdfFont((String) font, provider, fc);
        } else {
            throw new IllegalStateException("String or PdfFont expected as value of FONT property");
        }
    }

    // This method is intended to get first valid PdfFont described in font string,
    // with specific FontCharacteristics with the help of specified font provider.
    // This method is intended to be called from previous method that deals with Font Property.
    // NOTE: It neither change Font Property of renderer, nor is guarantied to contain all glyphs used in renderer.
    // TODO this mechanism does not take text into account
    PdfFont resolveFirstPdfFont(String font, FontProvider provider, FontCharacteristics fc) {
        return provider.getPdfFont(provider.getFontSelector(FontFamilySplitter.splitFontFamily(font), fc).bestMatch());
    }

    static void applyGeneratedAccessibleAttributes(TagTreePointer tagPointer, PdfDictionary attributes) {
        if (attributes == null) {
            return;
        }

        // TODO if taggingPointer.getProperties will always write directly to struct elem, use it instead (add addAttributes overload with index)
        PdfStructElem structElem = tagPointer.getDocument().getTagStructureContext().getPointerStructElem(tagPointer);
        PdfObject structElemAttr = structElem.getAttributes(false);
        if (structElemAttr == null || !structElemAttr.isDictionary() && !structElemAttr.isArray()) {
            structElem.setAttributes(attributes);
        } else if (structElemAttr.isDictionary()) {
            PdfArray attrArr = new PdfArray();
            attrArr.add(attributes);
            attrArr.add(structElemAttr);
            structElem.setAttributes(attrArr);
        } else { // isArray
            PdfArray attrArr = (PdfArray) structElemAttr;
            attrArr.add(0, attributes);
        }
    }
}
