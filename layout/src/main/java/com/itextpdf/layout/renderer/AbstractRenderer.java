/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.NumberUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.layout.PositionedLayoutContext;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BackgroundBox;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.BlendMode;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Transform;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    public static final float OVERLAP_EPSILON = 1e-4f;


    /**
     * The maximum difference between {@link Rectangle} coordinates to consider rectangles equal
     */
    protected static final float EPS = 1e-4f;

    /**
     * The infinity value which is used while layouting
     */
    protected static final float INF = 1e6f;

    /**
     * The common ordering index of top side in arrays of four elements which define top, right, bottom,
     * left sides values (e.g. margins, borders, paddings).
     */
    static final int TOP_SIDE = 0;

    /**
     * The common ordering index of right side in arrays of four elements which define top, right, bottom,
     * left sides values (e.g. margins, borders, paddings).
     */
    static final int RIGHT_SIDE = 1;

    /**
     * The common ordering index of bottom side in arrays of four elements which define top, right, bottom,
     * left sides values (e.g. margins, borders, paddings).
     */
    static final int BOTTOM_SIDE = 2;

    /**
     * The common ordering index of left side in arrays of four elements which define top, right, bottom,
     * left sides values (e.g. margins, borders, paddings).
     */
    static final int LEFT_SIDE = 3;

    private static final int ARC_RIGHT_DEGREE = 0;
    private static final int ARC_TOP_DEGREE = 90;
    private static final int ARC_LEFT_DEGREE = 180;
    private static final int ARC_BOTTOM_DEGREE = 270;

    private static final int ARC_QUARTER_CLOCKWISE_EXTENT = -90;

    // For autoport
    private static final Tuple2<String, PdfDictionary> CHECK_TUPLE2_TYPE =
            new Tuple2<String, PdfDictionary>("", new PdfDictionary());

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

    /**
     * Creates a new renderer based on an instance of another renderer.
     *
     * @param other renderer from which to copy essential properties
     */
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
     *
     * @param property the property to be checked
     * @return {@code true} if this instance or its model element have given own property, {@code false} otherwise
     */
    public boolean hasOwnOrModelProperty(int property) {
        return hasOwnOrModelProperty(this, property);
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
     *
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
        return NumberUtil.asFloat(this.<Object>getProperty(property));
    }

    /**
     * Returns a property with a certain key, as a floating point value.
     *
     * @param property     an {@link Property enum value}
     * @param defaultValue default value to be returned if property is not found
     * @return a {@link Float}
     */
    public Float getPropertyAsFloat(int property, Float defaultValue) {
        return NumberUtil.asFloat(this.<Object>getProperty(property, defaultValue));
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
     * Returns a property with a certain key, as a unit value.
     *
     * @param property an {@link Property enum value}
     * @return a {@link UnitValue}
     */
    public UnitValue getPropertyAsUnitValue(int property) {
        return this.<UnitValue>getProperty(property);
    }

    /**
     * Returns a property with a certain key, as an integer value.
     *
     * @param property an {@link Property enum value}
     * @return a {@link Integer}
     */
    public Integer getPropertyAsInteger(int property) {
        return NumberUtil.asInteger(this.<Object>getProperty(property));
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

    /**
     * Apply {@code Property.OPACITY} property if specified by setting corresponding values in graphic state dictionary
     * opacity will be applied to all elements drawn after calling this method and before
     * calling {@link AbstractRenderer#endElementOpacityApplying(DrawContext)} ()}.
     *
     * @param drawContext the context (canvas, document, etc) of this drawing operation.
     */
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

    /**
     * {@link AbstractRenderer#beginElementOpacityApplying(DrawContext)}.
     *
     * @param drawContext the context (canvas, document, etc) of this drawing operation.
     */
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
        final Background background = this.<Background>getProperty(Property.BACKGROUND);
        final List<BackgroundImage>  backgroundImagesList = this.<List<BackgroundImage>>getProperty(Property.BACKGROUND_IMAGE);

        if (background != null || backgroundImagesList != null) {
            Rectangle bBox = getOccupiedAreaBBox();
            boolean isTagged = drawContext.isTaggingEnabled();
            if (isTagged) {
                drawContext.getCanvas().openTag(new CanvasArtifact());
            }
            Rectangle backgroundArea = getBackgroundArea(applyMargins(bBox, false));
            if (backgroundArea.getWidth() <= 0 || backgroundArea.getHeight() <= 0) {
                Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                logger.info(MessageFormatUtil.format(
                        IoLogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES, "background"));
            } else {
                boolean backgroundAreaIsClipped = false;
                if (background != null) {
                    // TODO DEVSIX-4525 determine how background-clip affects background-radius
                    final Rectangle clippedBackgroundArea = applyBackgroundBoxProperty(backgroundArea.clone(),
                            background.getBackgroundClip());
                    backgroundAreaIsClipped = clipBackgroundArea(drawContext, clippedBackgroundArea);
                    drawColorBackground(background, drawContext, clippedBackgroundArea);
                }
                if (backgroundImagesList != null) {
                    backgroundAreaIsClipped = drawBackgroundImagesList(backgroundImagesList, backgroundAreaIsClipped,
                            drawContext, backgroundArea);
                }
                if (backgroundAreaIsClipped) {
                    drawContext.getCanvas().restoreState();
                }
            }
            if (isTagged) {
                drawContext.getCanvas().closeTag();
            }
        }
    }

    private void drawColorBackground(Background background, DrawContext drawContext, Rectangle colorBackgroundArea) {
        final TransparentColor backgroundColor = new TransparentColor(background.getColor(),
                background.getOpacity());
        drawContext.getCanvas().saveState().setFillColor(backgroundColor.getColor());
        backgroundColor.applyFillTransparency(drawContext.getCanvas());
        drawContext.getCanvas().rectangle((double) colorBackgroundArea.getX() - background.getExtraLeft(),
                (double) colorBackgroundArea.getY() - background.getExtraBottom(),
                (double) colorBackgroundArea.getWidth() +
                        background.getExtraLeft() + background.getExtraRight(),
                (double) colorBackgroundArea.getHeight() +
                        background.getExtraTop() + background.getExtraBottom()).fill().restoreState();
    }

    private Rectangle applyBackgroundBoxProperty(Rectangle rectangle, BackgroundBox clip) {
        if (BackgroundBox.PADDING_BOX == clip) {
            applyBorderBox(rectangle, false);
        } else if (BackgroundBox.CONTENT_BOX == clip) {
            applyBorderBox(rectangle, false);
            applyPaddings(rectangle, false);
        }
        return rectangle;
    }

    private boolean drawBackgroundImagesList(final List<BackgroundImage> backgroundImagesList,
                                             boolean backgroundAreaIsClipped, final DrawContext drawContext,
                                             final Rectangle backgroundArea) {
        for (int i = backgroundImagesList.size() - 1; i >= 0; i--) {
            final BackgroundImage backgroundImage = backgroundImagesList.get(i);
            if (backgroundImage != null && backgroundImage.isBackgroundSpecified()) {
                // TODO DEVSIX-4525 determine how background-clip affects background-radius
                if (!backgroundAreaIsClipped) {
                    backgroundAreaIsClipped = clipBackgroundArea(drawContext, backgroundArea);
                }
                drawBackgroundImage(backgroundImage, drawContext, backgroundArea);
            }
        }
        return backgroundAreaIsClipped;
    }

    private void drawBackgroundImage(BackgroundImage backgroundImage,
            DrawContext drawContext, Rectangle backgroundArea) {
        Rectangle originBackgroundArea = applyBackgroundBoxProperty(backgroundArea.clone(),
                backgroundImage.getBackgroundOrigin());
        float[] imageWidthAndHeight = backgroundImage.calculateBackgroundImageSize(originBackgroundArea.getWidth(),
                originBackgroundArea.getHeight());

        PdfXObject backgroundXObject = backgroundImage.getImage();
        if (backgroundXObject == null) {
            backgroundXObject = backgroundImage.getForm();
        }
        Rectangle imageRectangle;
        final UnitValue xPosition = UnitValue.createPointValue(0);
        final UnitValue yPosition = UnitValue.createPointValue(0);
        if (backgroundXObject == null) {
            final AbstractLinearGradientBuilder gradientBuilder = backgroundImage.getLinearGradientBuilder();
            if (gradientBuilder == null) {
                return;
            }
            // fullWidth and fullHeight is 0 because percentage shifts are ignored for linear-gradients
            backgroundImage.getBackgroundPosition().calculatePositionValues(0, 0, xPosition, yPosition);
            backgroundXObject = createXObject(gradientBuilder, originBackgroundArea, drawContext.getDocument());
            imageRectangle = new Rectangle(originBackgroundArea.getLeft() + xPosition.getValue(),
                    originBackgroundArea.getTop() - imageWidthAndHeight[1] - yPosition.getValue(),
                    imageWidthAndHeight[0], imageWidthAndHeight[1]);
        } else {
            backgroundImage.getBackgroundPosition().calculatePositionValues(
                    originBackgroundArea.getWidth() - imageWidthAndHeight[0],
                    originBackgroundArea.getHeight() - imageWidthAndHeight[1], xPosition, yPosition);
            imageRectangle = new Rectangle(originBackgroundArea.getLeft() + xPosition.getValue(),
                    originBackgroundArea.getTop() - imageWidthAndHeight[1] - yPosition.getValue(),
                    imageWidthAndHeight[0], imageWidthAndHeight[1]);
        }
        if (imageRectangle.getWidth() <= 0 || imageRectangle.getHeight() <= 0) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.info(MessageFormatUtil.format(
                    IoLogMessageConstant.RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES,
                    "background-image"));
        } else {
            final Rectangle clippedBackgroundArea = applyBackgroundBoxProperty(backgroundArea.clone(),
                    backgroundImage.getBackgroundClip());
            drawContext.getCanvas()
                    .saveState()
                    .rectangle(clippedBackgroundArea)
                    .clip()
                    .endPath();
            drawPdfXObject(imageRectangle, backgroundImage, drawContext, backgroundXObject, backgroundArea,
                    originBackgroundArea);
            drawContext.getCanvas().restoreState();
        }
    }

    private static void drawPdfXObject(final Rectangle imageRectangle, final BackgroundImage backgroundImage,
            final DrawContext drawContext, final PdfXObject backgroundXObject,
            final Rectangle backgroundArea, Rectangle originBackgroundArea) {
        BlendMode blendMode = backgroundImage.getBlendMode();
        if (blendMode != BlendMode.NORMAL) {
            drawContext.getCanvas().setExtGState(new PdfExtGState().setBlendMode(blendMode.getPdfRepresentation()));
        }
        final Point whitespace = backgroundImage.getRepeat()
                .prepareRectangleToDrawingAndGetWhitespace(imageRectangle, originBackgroundArea,
                        backgroundImage.getBackgroundSize());
        final float initialX = imageRectangle.getX();
        int counterY = 1;
        boolean firstDraw = true;
        boolean isCurrentOverlaps;
        boolean isNextOverlaps;
        do {
            drawPdfXObjectHorizontally(imageRectangle,
                    backgroundImage, drawContext, backgroundXObject, backgroundArea, firstDraw, (float) whitespace.getX());
            firstDraw = false;
            imageRectangle.setX(initialX);
            isCurrentOverlaps = imageRectangle.overlaps(backgroundArea, OVERLAP_EPSILON);
            if (counterY % 2 == 1) {
                isNextOverlaps =
                        imageRectangle.moveDown((imageRectangle.getHeight() + (float) whitespace.getY()) * counterY)
                                .overlaps(backgroundArea, OVERLAP_EPSILON);
            } else {
                isNextOverlaps = imageRectangle.moveUp((imageRectangle.getHeight() + (float) whitespace.getY()) * counterY)
                        .overlaps(backgroundArea, OVERLAP_EPSILON);
            }
            ++counterY;
        } while (!backgroundImage.getRepeat().isNoRepeatOnYAxis() && (isCurrentOverlaps || isNextOverlaps));
    }

    private static void drawPdfXObjectHorizontally(Rectangle imageRectangle, BackgroundImage backgroundImage,
                                                   DrawContext drawContext, PdfXObject backgroundXObject,
                                                   Rectangle backgroundArea, boolean firstDraw, final float xWhitespace) {
        boolean isItFirstDraw = firstDraw;
        int counterX = 1;
        boolean isCurrentOverlaps;
        boolean isNextOverlaps;
        do {
            if (imageRectangle.overlaps(backgroundArea, OVERLAP_EPSILON) || isItFirstDraw) {
                drawContext.getCanvas().addXObjectFittedIntoRectangle(backgroundXObject, imageRectangle);
                isItFirstDraw = false;
            }
            isCurrentOverlaps = imageRectangle.overlaps(backgroundArea, OVERLAP_EPSILON);
            if (counterX % 2 == 1) {
                isNextOverlaps =
                        imageRectangle.moveRight((imageRectangle.getWidth() + xWhitespace) * counterX)
                                .overlaps(backgroundArea, OVERLAP_EPSILON);
            } else {
                isNextOverlaps = imageRectangle.moveLeft((imageRectangle.getWidth() + xWhitespace) * counterX)
                        .overlaps(backgroundArea, OVERLAP_EPSILON);
            }
            ++counterX;
        }
        while (!backgroundImage.getRepeat().isNoRepeatOnXAxis() && (isCurrentOverlaps || isNextOverlaps));
    }

    /**
     * Create a {@link PdfFormXObject} with the given area and containing a linear gradient inside.
     *
     * @param linearGradientBuilder the linear gradient builder
     * @param xObjectArea           the result object area
     * @param document              the pdf document
     * @return the xObject with a specified area and a linear gradient
     */
    public static PdfFormXObject createXObject(AbstractLinearGradientBuilder linearGradientBuilder,
                                               Rectangle xObjectArea, PdfDocument document) {
        Rectangle formBBox = new Rectangle(0, 0, xObjectArea.getWidth(), xObjectArea.getHeight());
        PdfFormXObject xObject = new PdfFormXObject(formBBox);
        if (linearGradientBuilder != null) {
            Color gradientColor = linearGradientBuilder.buildColor(formBBox, null, document);
            if (gradientColor != null) {
                new PdfCanvas(xObject, document)
                        .setColor(gradientColor, true)
                        .rectangle(formBBox)
                        .fill();
            }
        }
        return xObject;
    }

    /**
     * Evaluate the actual background
     *
     * @param occupiedAreaWithMargins the current occupied area with applied margins
     * @return the actual background area
     */
    protected Rectangle getBackgroundArea(Rectangle occupiedAreaWithMargins) {
        return occupiedAreaWithMargins;
    }

    protected boolean clipBorderArea(DrawContext drawContext, Rectangle outerBorderBox) {
        return clipArea(drawContext, outerBorderBox, true, true, false, true);
    }

    protected boolean clipBackgroundArea(DrawContext drawContext, Rectangle outerBorderBox) {
        return clipArea(drawContext, outerBorderBox, true, false, false, false);
    }

    protected boolean clipBackgroundArea(DrawContext drawContext, Rectangle outerBorderBox, boolean considerBordersBeforeClipping) {
        return clipArea(drawContext, outerBorderBox, true, false, considerBordersBeforeClipping, false);
    }

    private boolean clipArea(DrawContext drawContext, Rectangle outerBorderBox, boolean clipOuter, boolean clipInner, boolean considerBordersBeforeOuterClipping, boolean considerBordersBeforeInnerClipping) {
        // border widths should be considered only once
        assert false == considerBordersBeforeOuterClipping || false == considerBordersBeforeInnerClipping;

        // border widths
        float[] borderWidths = {0, 0, 0, 0};
        // outer box
        float[] outerBox = {
                outerBorderBox.getTop(),
                outerBorderBox.getRight(),
                outerBorderBox.getBottom(),
                outerBorderBox.getLeft()
        };

        // radii
        boolean hasNotNullRadius = false;
        BorderRadius[] borderRadii = getBorderRadii();
        float[] verticalRadii = calculateRadii(borderRadii, outerBorderBox, false);
        float[] horizontalRadii = calculateRadii(borderRadii, outerBorderBox, true);
        for (int i = 0; i < 4; i++) {
            verticalRadii[i] = Math.min(verticalRadii[i], outerBorderBox.getHeight() / 2);
            horizontalRadii[i] = Math.min(horizontalRadii[i], outerBorderBox.getWidth() / 2);
            if (!hasNotNullRadius && (0 != verticalRadii[i] || 0 != horizontalRadii[i])) {
                hasNotNullRadius = true;
            }
        }
        if (hasNotNullRadius) {
            // coordinates of corner centers
            float[] cornersX = {outerBox[3] + horizontalRadii[0], outerBox[1] - horizontalRadii[1], outerBox[1] - horizontalRadii[2], outerBox[3] + horizontalRadii[3]};
            float[] cornersY = {outerBox[0] - verticalRadii[0], outerBox[0] - verticalRadii[1], outerBox[2] + verticalRadii[2], outerBox[2] + verticalRadii[3]};

            PdfCanvas canvas = drawContext.getCanvas();
            canvas.saveState();

            if (considerBordersBeforeOuterClipping) {
                borderWidths = decreaseBorderRadiiWithBorders(horizontalRadii, verticalRadii, outerBox, cornersX, cornersY);
            }

            // clip border area outside
            if (clipOuter) {
                clipOuterArea(canvas, horizontalRadii, verticalRadii, outerBox, cornersX, cornersY);
            }

            if (considerBordersBeforeInnerClipping) {
                borderWidths = decreaseBorderRadiiWithBorders(horizontalRadii, verticalRadii, outerBox, cornersX, cornersY);
            }

            // clip border area inside
            if (clipInner) {
                clipInnerArea(canvas, horizontalRadii, verticalRadii, outerBox, cornersX, cornersY, borderWidths);
            }
        }
        return hasNotNullRadius;
    }

    private void clipOuterArea(PdfCanvas canvas, float[] horizontalRadii, float[] verticalRadii,
            float[] outerBox, float[] cornersX, float[] cornersY) {
        final double top = outerBox[TOP_SIDE];
        final double right = outerBox[RIGHT_SIDE];
        final double bottom = outerBox[BOTTOM_SIDE];
        final double left = outerBox[LEFT_SIDE];

        // left top corner
        if (0 != horizontalRadii[0] || 0 != verticalRadii[0]) {
            double arcBottom = ((double) cornersY[TOP_SIDE]) - verticalRadii[TOP_SIDE];
            double arcRight = ((double) cornersX[TOP_SIDE]) + horizontalRadii[TOP_SIDE];
            canvas
                    .moveTo(left, bottom)
                    .arcContinuous(left, arcBottom, arcRight, top,
                            ARC_LEFT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(right, top)
                    .lineTo(right, bottom)
                    .lineTo(left, bottom);
            canvas.clip().endPath();
        }
        // right top corner
        if (0 != horizontalRadii[1] || 0 != verticalRadii[1]) {
            double arcLeft = ((double) cornersX[RIGHT_SIDE]) - horizontalRadii[RIGHT_SIDE];
            double arcBottom = ((double) cornersY[RIGHT_SIDE]) - verticalRadii[RIGHT_SIDE];
            canvas
                    .moveTo(left, top)
                    .arcContinuous(arcLeft, top, right, arcBottom,
                            ARC_TOP_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(right, bottom)
                    .lineTo(left, bottom)
                    .lineTo(left, top);
            canvas.clip().endPath();
        }
        // right bottom corner
        if (0 != horizontalRadii[2] || 0 != verticalRadii[2]) {
            double arcTop = ((double) cornersY[BOTTOM_SIDE]) + verticalRadii[BOTTOM_SIDE];
            double arcLeft = ((double) cornersX[BOTTOM_SIDE]) - horizontalRadii[BOTTOM_SIDE];
            canvas
                    .moveTo(right, top)
                    .arcContinuous(right, arcTop, arcLeft, bottom,
                            ARC_RIGHT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(left, bottom)
                    .lineTo(left, top)
                    .lineTo(right, top);
            canvas.clip().endPath();
        }
        // left bottom corner
        if (0 != horizontalRadii[3] || 0 != verticalRadii[3]) {
            double arcRight = ((double) cornersX[LEFT_SIDE]) + horizontalRadii[LEFT_SIDE];
            double arcTop = ((double) cornersY[LEFT_SIDE]) + verticalRadii[LEFT_SIDE];
            canvas
                    .moveTo(right, bottom)
                    .arcContinuous(arcRight, bottom, left, arcTop,
                            ARC_BOTTOM_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(left, top)
                    .lineTo(right, top)
                    .lineTo(right, bottom);
            canvas.clip().endPath();
        }
    }

    private void clipInnerArea(PdfCanvas canvas, float[] horizontalRadii, float[] verticalRadii,
            float[] outerBox, float[] cornersX, float[] cornersY, float[] borderWidths) {
        final double top = outerBox[TOP_SIDE];
        final double right = outerBox[RIGHT_SIDE];
        final double bottom = outerBox[BOTTOM_SIDE];
        final double left = outerBox[LEFT_SIDE];

        final double x1 = cornersX[TOP_SIDE];
        final double y1 = cornersY[TOP_SIDE];
        final double x2 = cornersX[RIGHT_SIDE];
        final double y2 = cornersY[RIGHT_SIDE];
        final double x3 = cornersX[BOTTOM_SIDE];
        final double y3 = cornersY[BOTTOM_SIDE];
        final double x4 = cornersX[LEFT_SIDE];
        final double y4 = cornersY[LEFT_SIDE];
        final double topBorderWidth = borderWidths[TOP_SIDE];
        final double rightBorderWidth = borderWidths[RIGHT_SIDE];
        final double bottomBorderWidth = borderWidths[BOTTOM_SIDE];
        final double leftBorderWidth = borderWidths[LEFT_SIDE];

        // left top corner
        if (0 != horizontalRadii[0] || 0 != verticalRadii[0]) {
            canvas
                    .arc(left, y1 - verticalRadii[TOP_SIDE],
                            x1 + horizontalRadii[TOP_SIDE], top,
                            ARC_LEFT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(x2, top)
                    .lineTo(right, y2)
                    .lineTo(right, y3)
                    .lineTo(x3, bottom)
                    .lineTo(x4, bottom)
                    .lineTo(left, y4)
                    .lineTo(left, y1)
                    .lineTo(left - leftBorderWidth, y1)
                    .lineTo(left - leftBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, y1);
            canvas.clip().endPath();
        }
        // right top corner
        if (0 != horizontalRadii[1] || 0 != verticalRadii[1]) {
            canvas
                    .arc(x2 - horizontalRadii[RIGHT_SIDE], top, right,
                            y2 - verticalRadii[RIGHT_SIDE],
                            ARC_TOP_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(right, y3)
                    .lineTo(x3, bottom)
                    .lineTo(x4, bottom)
                    .lineTo(left, y4)
                    .lineTo(left, y1)
                    .lineTo(x1, top)
                    .lineTo(x2, top)
                    .lineTo(x2, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, top + topBorderWidth)
                    .lineTo(x2, top + topBorderWidth);
            canvas.clip().endPath();
        }
        // right bottom corner
        if (0 != horizontalRadii[2] || 0 != verticalRadii[2]) {
            canvas
                    .arc(right, y3 + verticalRadii[BOTTOM_SIDE],
                            x3 - horizontalRadii[BOTTOM_SIDE], bottom,
                            ARC_RIGHT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(x4, bottom)
                    .lineTo(left, y4)
                    .lineTo(left, y1)
                    .lineTo(x1, top)
                    .lineTo(x2, top)
                    .lineTo(right, y2)
                    .lineTo(right, y3)
                    .lineTo(right + rightBorderWidth, y3)
                    .lineTo(right + rightBorderWidth, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, y3);
            canvas.clip().endPath();
        }
        // left bottom corner
        if (0 != horizontalRadii[3] || 0 != verticalRadii[3]) {
            canvas
                    .arc(x4 + horizontalRadii[LEFT_SIDE], bottom,
                            left, y4 + verticalRadii[LEFT_SIDE],
                            ARC_BOTTOM_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                    .lineTo(left, y1)
                    .lineTo(x1, top)
                    .lineTo(x2, top)
                    .lineTo(right, y2)
                    .lineTo(right, y3)
                    .lineTo(x3, bottom)
                    .lineTo(x4, bottom)
                    .lineTo(x4, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(right + rightBorderWidth, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, top + topBorderWidth)
                    .lineTo(left - leftBorderWidth, bottom - bottomBorderWidth)
                    .lineTo(x4, bottom - bottomBorderWidth);
            canvas.clip().endPath();
        }
    }

    private float[] decreaseBorderRadiiWithBorders(float[] horizontalRadii, float[] verticalRadii, float[] outerBox, float[] cornersX, float[] cornersY) {
        Border[] borders = getBorders();
        float[] borderWidths = {0, 0, 0, 0};

        if (borders[0] != null) {
            borderWidths[0] = borders[0].getWidth();
            outerBox[0] -= borders[0].getWidth();
            if (cornersY[1] > outerBox[0]) {
                cornersY[1] = outerBox[0];
            }
            if (cornersY[0] > outerBox[0]) {
                cornersY[0] = outerBox[0];
            }
            verticalRadii[0] = Math.max(0, verticalRadii[0] - borders[0].getWidth());
            verticalRadii[1] = Math.max(0, verticalRadii[1] - borders[0].getWidth());
        }
        if (borders[1] != null) {
            borderWidths[1] = borders[1].getWidth();
            outerBox[1] -= borders[1].getWidth();
            if (cornersX[1] > outerBox[1]) {
                cornersX[1] = outerBox[1];
            }
            if (cornersX[2] > outerBox[1]) {
                cornersX[2] = outerBox[1];
            }
            horizontalRadii[1] = Math.max(0, horizontalRadii[1] - borders[1].getWidth());
            horizontalRadii[2] = Math.max(0, horizontalRadii[2] - borders[1].getWidth());
        }
        if (borders[2] != null) {
            borderWidths[2] = borders[2].getWidth();
            outerBox[2] += borders[2].getWidth();
            if (cornersY[2] < outerBox[2]) {
                cornersY[2] = outerBox[2];
            }
            if (cornersY[3] < outerBox[2]) {
                cornersY[3] = outerBox[2];
            }
            verticalRadii[2] = Math.max(0, verticalRadii[2] - borders[2].getWidth());
            verticalRadii[3] = Math.max(0, verticalRadii[3] - borders[2].getWidth());
        }
        if (borders[3] != null) {
            borderWidths[3] = borders[3].getWidth();
            outerBox[3] += borders[3].getWidth();
            if (cornersX[3] < outerBox[3]) {
                cornersX[3] = outerBox[3];
            }
            if (cornersX[0] < outerBox[3]) {
                cornersX[0] = outerBox[3];
            }
            horizontalRadii[3] = Math.max(0, horizontalRadii[3] - borders[3].getWidth());
            horizontalRadii[0] = Math.max(0, horizontalRadii[0] - borders[3].getWidth());
        }
        return borderWidths;
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
            Transform transformProp = child.<Transform>getProperty(Property.TRANSFORM);
            RootRenderer rootRenderer = getRootRenderer();
            List<IRenderer> waiting = (rootRenderer != null && !rootRenderer.waitingDrawingElements.contains(child)) ? rootRenderer.waitingDrawingElements : waitingRenderers;
            processWaitingDrawing(child, transformProp, waiting);
            if (!FloatingHelper.isRendererFloating(child) && transformProp == null) {
                child.draw(drawContext);
            }
        }
        for (IRenderer waitingRenderer : waitingRenderers) {
            waitingRenderer.draw(drawContext);
        }
    }

    /**
     * Performs the drawing operation for the border of this renderer, if defined by the {@link Property#BORDER_TOP},
     * {@link Property#BORDER_RIGHT}, {@link Property#BORDER_BOTTOM} and {@link Property#BORDER_LEFT} values in either
     * the layout element or this {@link IRenderer} itself.
     *
     * @param drawContext the context (canvas, document, etc.) of this drawing operation
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
                logger.error(MessageFormatUtil.format(IoLogMessageConstant.RECTANGLE_HAS_NEGATIVE_SIZE, "border"));
                return;
            }
            float x1 = bBox.getX();
            float y1 = bBox.getY();
            float x2 = bBox.getX() + bBox.getWidth();
            float y2 = bBox.getY() + bBox.getHeight();

            boolean isTagged = drawContext.isTaggingEnabled();
            PdfCanvas canvas = drawContext.getCanvas();
            if (isTagged) {
                canvas.openTag(new CanvasArtifact());
            }

            Rectangle borderRect = applyMargins(occupiedArea.getBBox().clone(), getMargins(), false);
            boolean isAreaClipped = clipBorderArea(drawContext, borderRect);
            BorderRadius[] borderRadii = getBorderRadii();
            float[] verticalRadii = calculateRadii(borderRadii, borderRect, false);
            float[] horizontalRadii = calculateRadii(borderRadii, borderRect, true);
            for (int i = 0; i < 4; i++) {
                verticalRadii[i] = Math.min(verticalRadii[i], borderRect.getHeight() / 2);
                horizontalRadii[i] = Math.min(horizontalRadii[i], borderRect.getWidth() / 2);
            }
            if (borders[0] != null) {
                if (0 != horizontalRadii[0] || 0 != verticalRadii[0] || 0 != horizontalRadii[1] || 0 != verticalRadii[1]) {
                    borders[0].draw(canvas, x1, y2, x2, y2, horizontalRadii[0], verticalRadii[0], horizontalRadii[1], verticalRadii[1], Border.Side.TOP, leftWidth, rightWidth);
                } else {
                    borders[0].draw(canvas, x1, y2, x2, y2, Border.Side.TOP, leftWidth, rightWidth);
                }
            }
            if (borders[1] != null) {
                if (0 != horizontalRadii[1] || 0 != verticalRadii[1] || 0 != horizontalRadii[2] || 0 != verticalRadii[2]) {
                    borders[1].draw(canvas, x2, y2, x2, y1, horizontalRadii[1], verticalRadii[1], horizontalRadii[2], verticalRadii[2], Border.Side.RIGHT, topWidth, bottomWidth);
                } else {
                    borders[1].draw(canvas, x2, y2, x2, y1, Border.Side.RIGHT, topWidth, bottomWidth);
                }
            }
            if (borders[2] != null) {
                if (0 != horizontalRadii[2] || 0 != verticalRadii[2] || 0 != horizontalRadii[3] || 0 != verticalRadii[3]) {
                    borders[2].draw(canvas, x2, y1, x1, y1, horizontalRadii[2], verticalRadii[2], horizontalRadii[3], verticalRadii[3], Border.Side.BOTTOM, rightWidth, leftWidth);
                } else {
                    borders[2].draw(canvas, x2, y1, x1, y1, Border.Side.BOTTOM, rightWidth, leftWidth);
                }
            }
            if (borders[3] != null) {
                if (0 != horizontalRadii[3] || 0 != verticalRadii[3] || 0 != horizontalRadii[0] || 0 != verticalRadii[0]) {
                    borders[3].draw(canvas, x1, y1, x1, y2, horizontalRadii[3], verticalRadii[3], horizontalRadii[0], verticalRadii[0], Border.Side.LEFT, bottomWidth, topWidth);
                } else {
                    borders[3].draw(canvas, x1, y1, x1, y2, Border.Side.LEFT, bottomWidth, topWidth);
                }
            }

            if (isAreaClipped) {
                drawContext.getCanvas().restoreState();
            }

            if (isTagged) {
                canvas.closeTag();
            }
        }

    }

    /**
     * Indicates whether this renderer is flushed or not, i.e. if {@link #draw(DrawContext)} has already
     * been called.
     *
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
     * {@inheritDoc}
     */
    @Override
    public IRenderer getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move(float dxRight, float dyUp) {
        Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
        if (occupiedArea == null) {
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Moving won't be performed."));
            return;
        }
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

    /**
     * Applies margins, borders and paddings of the renderer on the given rectangle.
     *
     * @param rect    a rectangle margins, borders and paddings will be applied on.
     * @param reverse indicates whether margins, borders and paddings will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    Rectangle applyMarginsBordersPaddings(Rectangle rect, boolean reverse) {
        applyMargins(rect, reverse);
        applyBorderBox(rect, reverse);
        applyPaddings(rect, reverse);
        return rect;
    }

    /**
     * Applies margins of the renderer on the given rectangle
     *
     * @param rect    a rectangle margins will be applied on.
     * @param reverse indicates whether margins will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     * @see #getMargins
     */
    public Rectangle applyMargins(Rectangle rect, boolean reverse) {
        return this.applyMargins(rect, getMargins(), reverse);
    }

    /**
     * Applies the border box of the renderer on the given rectangle
     * If the border of a certain side is null, the side will remain as it was.
     *
     * @param rect    a rectangle the border box will be applied on.
     * @param reverse indicates whether the border box will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     * @see #getBorders
     */
    public Rectangle applyBorderBox(Rectangle rect, boolean reverse) {
        Border[] borders = getBorders();
        return applyBorderBox(rect, borders, reverse);
    }

    /**
     * Applies paddings of the renderer on the given rectangle
     *
     * @param rect    a rectangle paddings will be applied on.
     * @param reverse indicates whether paddings will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     * @see #getPaddings
     */
    public Rectangle applyPaddings(Rectangle rect, boolean reverse) {
        return applyPaddings(rect, getPaddings(), reverse);
    }

    public boolean isFirstOnRootArea() {
        return isFirstOnRootArea(false);
    }

    protected void applyDestinationsAndAnnotation(DrawContext drawContext) {
        applyDestination(drawContext.getDocument());
        applyAction(drawContext.getDocument());
        applyLinkAnnotation(drawContext.getDocument());
    }

    protected static boolean isBorderBoxSizing(IRenderer renderer) {
        BoxSizingPropertyValue boxSizing = renderer.<BoxSizingPropertyValue>getProperty(Property.BOX_SIZING);
        return boxSizing != null && boxSizing.equals(BoxSizingPropertyValue.BORDER_BOX);
    }

    protected boolean isOverflowProperty(OverflowPropertyValue equalsTo, int overflowProperty) {
        return isOverflowProperty(equalsTo, this.<OverflowPropertyValue>getProperty(overflowProperty));
    }

    protected static boolean isOverflowProperty(OverflowPropertyValue equalsTo, IRenderer renderer, int overflowProperty) {
        return isOverflowProperty(equalsTo, renderer.<OverflowPropertyValue>getProperty(overflowProperty));
    }

    protected static boolean isOverflowProperty(OverflowPropertyValue equalsTo, OverflowPropertyValue rendererOverflowProperty) {
        return equalsTo.equals(rendererOverflowProperty) || equalsTo.equals(OverflowPropertyValue.FIT) && rendererOverflowProperty == null;
    }

    protected static boolean isOverflowFit(OverflowPropertyValue rendererOverflowProperty) {
        return rendererOverflowProperty == null || OverflowPropertyValue.FIT.equals(rendererOverflowProperty);
    }

    /**
     * Replaces given property own value with the given value.
     *
     * @param property the property to be replaced
     * @param replacementValue the value with which property will be replaced
     * @param <T> the type associated with the property
     * @return previous property value
     */
    <T> T replaceOwnProperty(int property, T replacementValue) {
        T ownProperty = this.<T>getOwnProperty(property);
        setProperty(property, replacementValue);
        return ownProperty;
    }

    /**
     * Returns back own value of the given property.
     *
     * @param property the property to be returned back
     * @param prevValue the value which will be returned back
     * @param <T> the type associated with the property
     */
    <T> void returnBackOwnProperty(int property, T prevValue) {
        if (prevValue == null) {
            deleteOwnProperty(property);
        } else {
            setProperty(property, prevValue);
        }
    }

    /**
     * Checks if this renderer has intrinsic aspect ratio.
     *
     * @return true, if aspect ratio is defined for this renderer, false otherwise
     */
    boolean hasAspectRatio() {
        // TODO DEVSIX-5255 This method should be changed after we support aspect-ratio property
        return false;
    }

    /**
     * Gets intrinsic aspect ratio for this renderer.
     *
     * @return aspect ratio, if it is defined for this renderer, null otherwise
     */
    Float getAspectRatio() {
        // TODO DEVSIX-5255 This method should be changed after we support aspect-ratio property
        return null;
    }

    static void processWaitingDrawing(IRenderer child, Transform transformProp, List<IRenderer> waitingDrawing) {
        if (FloatingHelper.isRendererFloating(child) || transformProp != null) {
            waitingDrawing.add(child);
        }
        Border outlineProp = child.<Border>getProperty(Property.OUTLINE);
        if (outlineProp != null && child instanceof AbstractRenderer) {
            AbstractRenderer abstractChild = (AbstractRenderer) child;
            if (abstractChild.isRelativePosition())
                abstractChild.applyRelativePositioningTranslation(false);
            Div outlines = new Div().setNeutralRole();
            if (transformProp != null)
                outlines.setProperty(Property.TRANSFORM, transformProp);
            outlines.setBorder(outlineProp);
            float offset = outlineProp.getWidth();
            if (abstractChild.getPropertyAsFloat(Property.OUTLINE_OFFSET) != null)
                offset += (float) abstractChild.getPropertyAsFloat(Property.OUTLINE_OFFSET);
            DivRenderer div = new DivRenderer(outlines);
            div.setParent(abstractChild.getParent());
            Rectangle divOccupiedArea = abstractChild.applyMargins(abstractChild.occupiedArea.clone().getBBox(), false).moveLeft(offset).moveDown(offset);
            divOccupiedArea.setWidth(divOccupiedArea.getWidth() + 2 * offset).setHeight(divOccupiedArea.getHeight() + 2 * offset);
            div.occupiedArea = new LayoutArea(abstractChild.getOccupiedArea().getPageNumber(), divOccupiedArea);
            float outlineWidthTop = div.<Border>getProperty(Property.BORDER_TOP).getWidth();
            float outlineWidthBottom = div.<Border>getProperty(Property.BORDER_BOTTOM).getWidth();
            float outlineWidthLeft = div.<Border>getProperty(Property.BORDER_LEFT).getWidth();
            float outlineWidthRight = div.<Border>getProperty(Property.BORDER_RIGHT).getWidth();
            if (divOccupiedArea.getWidth() >= (outlineWidthLeft + outlineWidthRight) &&
                    divOccupiedArea.getHeight() >= (outlineWidthTop + outlineWidthBottom)) {
                waitingDrawing.add(div);
            }
            if (abstractChild.isRelativePosition())
                abstractChild.applyRelativePositioningTranslation(true);
        }
    }

    /**
     * Retrieves element's fixed content box width, if it's set.
     * Takes into account {@link Property#BOX_SIZING}, {@link Property#MIN_WIDTH},
     * and {@link Property#MAX_WIDTH} properties.
     *
     * @param parentBoxWidth width of the parent element content box.
     *                       If element has relative width, it will be
     *                       calculated relatively to this parameter.
     * @return element's fixed content box width or null if it's not set.
     * @see AbstractRenderer#hasAbsoluteUnitValue(int)
     */
    protected Float retrieveWidth(float parentBoxWidth) {
        Float minWidth = retrieveUnitValue(parentBoxWidth, Property.MIN_WIDTH);

        Float maxWidth = retrieveUnitValue(parentBoxWidth, Property.MAX_WIDTH);
        if (maxWidth != null && minWidth != null && minWidth > maxWidth) {
            maxWidth = minWidth;
        }

        Float width = retrieveUnitValue(parentBoxWidth, Property.WIDTH);
        if (width != null) {
            if (maxWidth != null) {
                width = width > maxWidth ? maxWidth : width;
            }
            if (minWidth != null) {
                width = width < minWidth ? minWidth : width;
            }
        } else if (maxWidth != null) {
            width = maxWidth < parentBoxWidth ? maxWidth : null;
        }

        if (width != null && isBorderBoxSizing(this)) {
            width -= calculatePaddingBorderWidth(this);
        }

        return width != null ? (Float) Math.max(0, (float) width) : null;
    }

    /**
     * Retrieves element's fixed content box max width, if it's set.
     * Takes into account {@link Property#BOX_SIZING} and {@link Property#MIN_WIDTH} properties.
     *
     * @param parentBoxWidth width of the parent element content box.
     *                       If element has relative width, it will be
     *                       calculated relatively to this parameter.
     * @return element's fixed content box max width or null if it's not set.
     * @see AbstractRenderer#hasAbsoluteUnitValue(int)
     */
    protected Float retrieveMaxWidth(float parentBoxWidth) {
        Float maxWidth = retrieveUnitValue(parentBoxWidth, Property.MAX_WIDTH);
        if (maxWidth != null) {
            Float minWidth = retrieveUnitValue(parentBoxWidth, Property.MIN_WIDTH);
            if (minWidth != null && minWidth > maxWidth) {
                maxWidth = minWidth;
            }

            if (isBorderBoxSizing(this)) {
                maxWidth -= calculatePaddingBorderWidth(this);
            }
            return maxWidth > 0 ? maxWidth : 0;
        } else {
            return null;
        }
    }

    /**
     * Retrieves element's fixed content box max width, if it's set.
     * Takes into account {@link Property#BOX_SIZING} property value.
     *
     * @param parentBoxWidth width of the parent element content box.
     *                       If element has relative width, it will be
     *                       calculated relatively to this parameter.
     * @return element's fixed content box max width or null if it's not set.
     * @see AbstractRenderer#hasAbsoluteUnitValue(int)
     */
    protected Float retrieveMinWidth(float parentBoxWidth) {
        Float minWidth = retrieveUnitValue(parentBoxWidth, Property.MIN_WIDTH);
        if (minWidth != null) {
            if (isBorderBoxSizing(this)) {
                minWidth -= calculatePaddingBorderWidth(this);
            }
            return minWidth > 0 ? minWidth : 0;
        } else {
            return null;
        }
    }

    /**
     * Updates fixed content box width value for this renderer.
     * Takes into account {@link Property#BOX_SIZING} property value.
     *
     * @param updatedWidthValue element's new fixed content box width.
     */
    protected void updateWidth(UnitValue updatedWidthValue) {
        if (updatedWidthValue.isPointValue() && isBorderBoxSizing(this)) {
            updatedWidthValue.setValue(updatedWidthValue.getValue() + calculatePaddingBorderWidth(this));
        }
        setProperty(Property.WIDTH, updatedWidthValue);
    }

    /**
     * Retrieves the element's fixed content box height, if it's set.
     * Takes into account {@link Property#BOX_SIZING}, {@link Property#MIN_HEIGHT},
     * and {@link Property#MAX_HEIGHT} properties.
     *
     * @return element's fixed content box height or null if it's not set.
     */
    protected Float retrieveHeight() {
        Float height = null;
        UnitValue heightUV = getPropertyAsUnitValue(Property.HEIGHT);
        Float parentResolvedHeight = retrieveResolvedParentDeclaredHeight();
        Float minHeight = null;
        Float maxHeight = null;
        if (heightUV != null) {
            if (parentResolvedHeight == null) {
                if (heightUV.isPercentValue()) {
                    //If the height is a relative value and no parent with a resolved height can be found, treat it as null
                    height = null;
                } else {
                    //Since no parent height is resolved, only point-value min and max should be taken into account
                    UnitValue minHeightUV = getPropertyAsUnitValue(Property.MIN_HEIGHT);
                    if (minHeightUV != null && minHeightUV.isPointValue()) {
                        minHeight = minHeightUV.getValue();
                    }
                    UnitValue maxHeightUV = getPropertyAsUnitValue(Property.MAX_HEIGHT);
                    if (maxHeightUV != null && maxHeightUV.isPointValue()) {
                        maxHeight = maxHeightUV.getValue();
                    }
                    //If the height is stored as a point value, we do not care about the parent's resolved height
                    height = heightUV.getValue();
                }
            } else {
                minHeight = retrieveUnitValue((float) parentResolvedHeight, Property.MIN_HEIGHT);
                maxHeight = retrieveUnitValue((float) parentResolvedHeight, Property.MAX_HEIGHT);
                height = retrieveUnitValue((float) parentResolvedHeight, Property.HEIGHT);
            }
            if (maxHeight != null && minHeight != null && minHeight > maxHeight) {
                maxHeight = minHeight;
            }
            if (height != null) {
                if (maxHeight != null) {
                    height = height > maxHeight ? maxHeight : height;
                }
                if (minHeight != null) {
                    height = height < minHeight ? minHeight : height;
                }
            }
            if (height != null && isBorderBoxSizing(this)) {
                height -= calculatePaddingBorderHeight(this);
            }
        }
        return height != null ? (Float) Math.max(0, (float) height) : null;

    }

    /**
     * Calculates the element corner's border radii.
     *
     * @param radii      defines border radii of the element
     * @param area       defines the area of the element
     * @param horizontal defines whether horizontal or vertical radii should be calculated
     * @return the element corner's border radii.
     */
    private float[] calculateRadii(BorderRadius[] radii, Rectangle area, boolean horizontal) {
        float[] results = new float[4];
        UnitValue value;
        for (int i = 0; i < 4; i++) {
            if (null != radii[i]) {
                value = horizontal ? radii[i].getHorizontalRadius() : radii[i].getVerticalRadius();
                if (value != null) {
                    if (value.getUnitType() == UnitValue.PERCENT) {
                        results[i] = value.getValue() * (horizontal ? area.getWidth() : area.getHeight()) / 100;
                    } else {
                        assert value.getUnitType() == UnitValue.POINT;
                        results[i] = value.getValue();
                    }
                } else {
                    results[i] = 0;
                }
            } else {
                results[i] = 0;
            }
        }
        return results;
    }

    /**
     * Updates fixed content box height value for this renderer.
     * Takes into account {@link Property#BOX_SIZING} property value.
     *
     * @param updatedHeight element's new fixed content box height, shall be not null.
     */
    protected void updateHeight(UnitValue updatedHeight) {
        if (isBorderBoxSizing(this) && updatedHeight.isPointValue()) {
            updatedHeight.setValue(updatedHeight.getValue() + calculatePaddingBorderHeight(this));

        }
        setProperty(Property.HEIGHT, updatedHeight);
    }

    /**
     * Retrieve element's content box max-ehight, if it's set.
     * Takes into account {@link Property#BOX_SIZING} property value.
     *
     * @return element's content box max-height or null if it's not set.
     */
    protected Float retrieveMaxHeight() {
        Float maxHeight = null, minHeight = null;
        Float directParentDeclaredHeight = retrieveDirectParentDeclaredHeight();
        UnitValue maxHeightAsUV = getPropertyAsUnitValue(Property.MAX_HEIGHT);
        if (maxHeightAsUV != null) {
            if (directParentDeclaredHeight == null) {
                if (maxHeightAsUV.isPercentValue()) {
                    maxHeight = null;
                } else {
                    minHeight = retrieveMinHeight();
                    //Since no parent height is resolved, only point-value min should be taken into account
                    UnitValue minHeightUV = getPropertyAsUnitValue(Property.MIN_HEIGHT);
                    if (minHeightUV != null && minHeightUV.isPointValue()) {
                        minHeight = minHeightUV.getValue();
                    }
                    //We don't care about a baseline if the max-height is explicitly defined
                    maxHeight = maxHeightAsUV.getValue();
                }
            } else {
                maxHeight = retrieveUnitValue((float) directParentDeclaredHeight, Property.MAX_HEIGHT);
            }
            if (maxHeight != null) {
                if (minHeight != null && minHeight > maxHeight) {
                    maxHeight = minHeight;
                }
                if (isBorderBoxSizing(this)) {
                    maxHeight -= calculatePaddingBorderHeight(this);
                }
                return maxHeight > 0 ? maxHeight : 0;
            }
        }
        //Max height is not set, but height might be set
        return retrieveHeight();
    }


    /**
     * Updates content box max-height value for this renderer.
     * Takes into account {@link Property#BOX_SIZING} property value.
     *
     * @param updatedMaxHeight element's new content box max-height, shall be not null.
     */
    protected void updateMaxHeight(UnitValue updatedMaxHeight) {
        if (isBorderBoxSizing(this) && updatedMaxHeight.isPointValue()) {
            updatedMaxHeight.setValue(updatedMaxHeight.getValue() + calculatePaddingBorderHeight(this));

        }
        setProperty(Property.MAX_HEIGHT, updatedMaxHeight);
    }


    /**
     * Retrieves element's content box min-height, if it's set.
     * Takes into account {@link Property#BOX_SIZING} property value.
     *
     * @return element's content box min-height or null if it's not set.
     */
    protected Float retrieveMinHeight() {
        Float minHeight = null;
        Float directParentDeclaredHeight = retrieveDirectParentDeclaredHeight();
        UnitValue minHeightUV = getPropertyAsUnitValue(this, Property.MIN_HEIGHT);
        if (minHeightUV != null) {
            if (directParentDeclaredHeight == null) {
                if (minHeightUV.isPercentValue()) {
                    //if there is no baseline to compare against, a relative value evaluates to null
                    minHeight = null;
                } else {
                    //If the min-height is stored as a point value, we do not care about a baseline.
                    minHeight = minHeightUV.getValue();
                }
            } else {
                minHeight = retrieveUnitValue((float) directParentDeclaredHeight, Property.MIN_HEIGHT);
            }
            if (minHeight != null) {
                if (isBorderBoxSizing(this)) {
                    minHeight -= calculatePaddingBorderHeight(this);
                }
                return minHeight > 0 ? minHeight : 0;
            }
        }
        //min-height might be zero, but height might be set
        return retrieveHeight();

    }

    /**
     * Updates content box min-height value for this renderer.
     * Takes into account {@link Property#BOX_SIZING} property value.
     *
     * @param updatedMinHeight element's new content box min-height, shall be not null.
     */
    protected void updateMinHeight(UnitValue updatedMinHeight) {
        if (isBorderBoxSizing(this) && updatedMinHeight.isPointValue()) {
            updatedMinHeight.setValue(updatedMinHeight.getValue() + calculatePaddingBorderHeight(this));
        }
        setProperty(Property.MIN_HEIGHT, updatedMinHeight);
    }

    protected Float retrieveUnitValue(float baseValue, int property) {
        return retrieveUnitValue(baseValue, property, false);
    }

    protected Float retrieveUnitValue(float baseValue, int property, boolean pointOnly) {
        UnitValue value = this.<UnitValue>getProperty(property);
        if (pointOnly && value.getUnitType() == UnitValue.POINT) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, property));
        }
        if (value != null) {
            if (value.getUnitType() == UnitValue.PERCENT) {
                // during mathematical operations the precision can be lost, so avoiding them if possible (100 / 100 == 1) is a good practice
                return value.getValue() != 100 ? baseValue * value.getValue() / 100 : baseValue;
            } else {
                assert value.getUnitType() == UnitValue.POINT;
                return value.getValue();
            }
        } else {
            return null;
        }
    }
    
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
     *
     * @return the first yline of the nested children, null if there is no text found
     */
    protected Float getFirstYLineRecursively() {
        if (childRenderers.size() == 0) {
            return null;
        }
        return ((AbstractRenderer) childRenderers.get(0)).getFirstYLineRecursively();
    }

    protected Float getLastYLineRecursively() {
        if (!allowLastYLineRecursiveExtraction()) {
            return null;
        }
        for (int i = childRenderers.size() - 1; i >= 0; i--) {
            IRenderer child = childRenderers.get(i);
            if (child instanceof AbstractRenderer) {
                Float lastYLine = ((AbstractRenderer) child).getLastYLineRecursively();
                if (lastYLine != null) {
                    return lastYLine;
                }
            }
        }
        return null;
    }

    protected boolean allowLastYLineRecursiveExtraction() {
        return !isOverflowProperty(OverflowPropertyValue.HIDDEN, Property.OVERFLOW_X)
                && !isOverflowProperty(OverflowPropertyValue.HIDDEN, Property.OVERFLOW_Y);
    }

    /**
     * Applies given margins on the given rectangle
     *
     * @param rect    a rectangle margins will be applied on.
     * @param margins the margins to be applied on the given rectangle
     * @param reverse indicates whether margins will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    protected Rectangle applyMargins(Rectangle rect, UnitValue[] margins, boolean reverse) {
        if (!margins[TOP_SIDE].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_TOP));
        }
        if (!margins[RIGHT_SIDE].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_RIGHT));
        }
        if (!margins[BOTTOM_SIDE].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_BOTTOM));
        }
        if (!margins[LEFT_SIDE].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_LEFT));
        }
        return rect.applyMargins(
                margins[TOP_SIDE].getValue(),
                margins[RIGHT_SIDE].getValue(),
                margins[BOTTOM_SIDE].getValue(),
                margins[LEFT_SIDE].getValue(), reverse);
    }

    /**
     * Returns margins of the renderer
     * [0] - top; [1] - right; [2] - bottom; [3] - left
     *
     * @return a {@code float[]} margins of the renderer
     */
    protected UnitValue[] getMargins() {
        return getMargins(this);
    }

    /**
     * Returns paddings of the renderer
     * [0] - top; [1] - right; [2] - bottom; [3] - left
     *
     * @return a {@code float[]} paddings of the renderer
     */
    protected UnitValue[] getPaddings() {
        return getPaddings(this);
    }

    /**
     * Applies given paddings to the given rectangle.
     *
     * @param rect     a rectangle paddings will be applied on.
     * @param paddings the paddings to be applied on the given rectangle
     * @param reverse  indicates whether paddings will be applied
     *                 inside (in case of false) or outside (in case of true) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    protected Rectangle applyPaddings(Rectangle rect, UnitValue[] paddings, boolean reverse) {
        if (paddings[0] != null && !paddings[0].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.PADDING_TOP));
        }
        if (paddings[1] != null && !paddings[1].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.PADDING_RIGHT));
        }
        if (paddings[2] != null && !paddings[2].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.PADDING_BOTTOM));
        }
        if (paddings[3] != null && !paddings[3].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.PADDING_LEFT));
        }
        return rect.applyMargins(paddings[0] != null ? paddings[0].getValue() : 0,
                paddings[1] != null ? paddings[1].getValue() : 0,
                paddings[2] != null ? paddings[2].getValue() : 0,
                paddings[3] != null ? paddings[3].getValue() : 0,
                reverse);
    }

    /**
     * Applies the given border box (borders) on the given rectangle
     *
     * @param rect    a rectangle paddings will be applied on.
     * @param borders the {@link Border borders} to be applied on the given rectangle
     * @param reverse indicates whether the border box will be applied
     *                inside (in case of false) or outside (in case of false) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    protected Rectangle applyBorderBox(Rectangle rect, Border[] borders, boolean reverse) {
        float topWidth = borders[0] != null ? borders[0].getWidth() : 0;
        float rightWidth = borders[1] != null ? borders[1].getWidth() : 0;
        float bottomWidth = borders[2] != null ? borders[2].getWidth() : 0;
        float leftWidth = borders[3] != null ? borders[3].getWidth() : 0;
        return rect.applyMargins(topWidth, rightWidth, bottomWidth, leftWidth, reverse);
    }

    protected void applyAbsolutePosition(Rectangle parentRect) {
        Float top = this.getPropertyAsFloat(Property.TOP);
        Float bottom = this.getPropertyAsFloat(Property.BOTTOM);
        Float left = this.getPropertyAsFloat(Property.LEFT);
        Float right = this.getPropertyAsFloat(Property.RIGHT);

        if (left == null && right == null && BaseDirection.RIGHT_TO_LEFT.equals(this.<BaseDirection>getProperty(Property.BASE_DIRECTION))) {
            right = 0f;
        }

        if (top == null && bottom == null) {
            top = 0f;
        }

        try {
            if (right != null) {
                move(parentRect.getRight() - (float) right - occupiedArea.getBBox().getRight(), 0);
            }

            if (left != null) {
                move(parentRect.getLeft() + (float) left - occupiedArea.getBBox().getLeft(), 0);
            }

            if (top != null) {
                move(0, parentRect.getTop() - (float) top - occupiedArea.getBBox().getTop());
            }

            if (bottom != null) {
                move(0, parentRect.getBottom() + (float) bottom - occupiedArea.getBBox().getBottom());
            }
        } catch (Exception exc) {
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Absolute positioning might be applied incorrectly."));
        }
    }

    protected void applyRelativePositioningTranslation(boolean reverse) {
        float top = (float) this.getPropertyAsFloat(Property.TOP, 0f);
        float bottom = (float) this.getPropertyAsFloat(Property.BOTTOM, 0f);
        float left = (float) this.getPropertyAsFloat(Property.LEFT, 0f);
        float right = (float) this.getPropertyAsFloat(Property.RIGHT, 0f);

        int reverseMultiplier = reverse ? -1 : 1;

        float dxRight = left != 0 ? left * reverseMultiplier : -right * reverseMultiplier;
        float dyUp = top != 0 ? -top * reverseMultiplier : bottom * reverseMultiplier;

        if (dxRight != 0 || dyUp != 0)
            move(dxRight, dyUp);
    }

    protected void applyDestination(PdfDocument document) {
        Object destination = this.<Object>getProperty(Property.DESTINATION);
        if (destination == null) {
            return;
        }
        String destinationName = null;
        PdfDictionary linkActionDict = null;
        if (destination instanceof String) {
            destinationName = (String)destination;
        } else if (CHECK_TUPLE2_TYPE.getClass().equals(destination.getClass())) {
            // 'If' above is the only autoportable way it seems
            Tuple2<String, PdfDictionary> destTuple = (Tuple2<String, PdfDictionary>)destination;
            destinationName = destTuple.getFirst();
            linkActionDict = destTuple.getSecond();
        }

        if (destinationName != null) {
            int pageNumber = occupiedArea.getPageNumber();
            if (pageNumber < 1 || pageNumber > document.getNumberOfPages()) {
                Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                String logMessageArg = "Property.DESTINATION, which specifies this element location as destination, see ElementPropertyContainer.setDestination.";
                logger.warn(MessageFormatUtil.format(
                        IoLogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN,
                        logMessageArg));
                return;
            }

            PdfArray array = new PdfArray();
            array.add(document.getPage(pageNumber).getPdfObject());
            array.add(PdfName.XYZ);
            array.add(new PdfNumber(occupiedArea.getBBox().getX()));
            array.add(new PdfNumber(occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight()));
            array.add(new PdfNumber(0));
            document.addNamedDestination(destinationName, array.makeIndirect(document));
        }

        final boolean isPdf20 = document.getPdfVersion().compareTo(PdfVersion.PDF_2_0) >= 0;
        if (linkActionDict != null && isPdf20 && document.isTagged()) {
            // Add structure destination for the action for tagged pdf 2.0
            PdfStructElem structElem = getCurrentStructElem(document);
            PdfStructureDestination dest = PdfStructureDestination.createFit(structElem);
            linkActionDict.put(PdfName.SD, dest.getPdfObject());
        }

        deleteProperty(Property.DESTINATION);
    }

    protected void applyAction(PdfDocument document) {
        PdfAction action = this.<PdfAction>getProperty(Property.ACTION);
        if (action != null) {
            PdfLinkAnnotation link = this.<PdfLinkAnnotation>getProperty(Property.LINK_ANNOTATION);
            if (link == null) {
                link = (PdfLinkAnnotation) new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)).setFlags(PdfAnnotation.PRINT);
                // For now, we set left border to an annotation, but appropriate borders for an element will be drawn.
                Border border = this.<Border>getProperty(Property.BORDER_LEFT);
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
        Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
        PdfLinkAnnotation linkAnnotation = this.<PdfLinkAnnotation>getProperty(Property.LINK_ANNOTATION);
        if (linkAnnotation == null) {
            return;
        }

        int pageNumber = occupiedArea.getPageNumber();
        if (pageNumber < 1 || pageNumber > document.getNumberOfPages()) {
            String logMessageArg = "Property.LINK_ANNOTATION, which specifies a link associated with this element content area, see com.itextpdf.layout.element.Link.";
            logger.warn(MessageFormatUtil.format(
                    IoLogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN,
                    logMessageArg));
            return;
        }

        // If an element with a link annotation occupies more than two pages,
        // then a NPE might occur, because of the annotation being partially flushed.
        // That's why we create and use an annotation's copy.
        PdfDictionary newAnnotation = (PdfDictionary) linkAnnotation.getPdfObject().clone();
        linkAnnotation = (PdfLinkAnnotation) PdfAnnotation.makeAnnotation(newAnnotation);
        Rectangle pdfBBox = calculateAbsolutePdfBBox();
        linkAnnotation.setRectangle(new PdfArray(pdfBBox));

        PdfPage page = document.getPage(pageNumber);
        // TODO DEVSIX-1655 This check is necessary because, in some cases, our renderer's hierarchy may contain
        //  a renderer from the different page that was already flushed
        if (page.isFlushed()) {
            logger.error(MessageFormatUtil.format(
                    IoLogMessageConstant.PAGE_WAS_FLUSHED_ACTION_WILL_NOT_BE_PERFORMED, "link annotation applying"));
        } else {
            page.addAnnotation(linkAnnotation);
        }
    }

    /**
     * Retrieve the parent's resolved height declaration.
     * If the parent has a relative height declaration, it will check it's parent recursively,
     *
     * @return null if no height declaration is set on the parent, or if it's own height declaration cannot be resolved
     * The float value of the resolved height otherwiser
     */
    private Float retrieveResolvedParentDeclaredHeight() {
        if (parent != null && parent.<UnitValue>getProperty(Property.HEIGHT) != null) {
            UnitValue parentHeightUV = getPropertyAsUnitValue(parent, Property.HEIGHT);
            if (parentHeightUV.isPointValue()) {
                return parentHeightUV.getValue();
            } else {
                return ((AbstractRenderer) parent).retrieveHeight();
            }
        } else {
            return null;
        }
    }

    /**
     * Retrieve the direct parent's absolute height property
     *
     * @return the direct parent's absolute height property value if it exists, null otherwise
     */
    private Float retrieveDirectParentDeclaredHeight() {
        if (parent != null && parent.<UnitValue>getProperty(Property.HEIGHT) != null) {
            UnitValue parentHeightUV = getPropertyAsUnitValue(parent, Property.HEIGHT);
            if (parentHeightUV.isPointValue()) {
                return parentHeightUV.getValue();
            }
        }
        return null;
    }

    protected void updateHeightsOnSplit(boolean wasHeightClipped, AbstractRenderer splitRenderer, AbstractRenderer overflowRenderer) {
        updateHeightsOnSplit(occupiedArea.getBBox().getHeight(), wasHeightClipped, splitRenderer, overflowRenderer, true);
    }

    void updateHeightsOnSplit(float usedHeight, boolean wasHeightClipped, AbstractRenderer splitRenderer, AbstractRenderer overflowRenderer, boolean enlargeOccupiedAreaOnHeightWasClipped) {
        if (wasHeightClipped) {
            // if height was clipped, max height exists and can be resolved
            Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
            logger.warn(IoLogMessageConstant.CLIP_ELEMENT);

            if (enlargeOccupiedAreaOnHeightWasClipped) {
                Float maxHeight = retrieveMaxHeight();
                splitRenderer.occupiedArea.getBBox()
                        .moveDown((float) maxHeight - usedHeight)
                        .setHeight((float) maxHeight);
                usedHeight = (float) maxHeight;
            }
        }

        if (overflowRenderer == null || isKeepTogether()) {
            return;
        }

        // Update height related properties on split or overflow
        // For relative heights, we need the parent's resolved height declaration
        Float parentResolvedHeightPropertyValue = retrieveResolvedParentDeclaredHeight();
        UnitValue maxHeightUV = getPropertyAsUnitValue(this, Property.MAX_HEIGHT);
        if (maxHeightUV != null) {
            if (maxHeightUV.isPointValue()) {
                Float maxHeight = retrieveMaxHeight();
                UnitValue updateMaxHeight = UnitValue.createPointValue((float) (maxHeight - usedHeight));
                overflowRenderer.updateMaxHeight(updateMaxHeight);
            } else if (parentResolvedHeightPropertyValue != null) {
                // Calculate occupied fraction and update overflow renderer
                float currentOccupiedFraction = usedHeight / (float) parentResolvedHeightPropertyValue * 100;
                // Fraction
                float newFraction = maxHeightUV.getValue() - currentOccupiedFraction;
                // Update
                overflowRenderer.updateMinHeight(UnitValue.createPercentValue(newFraction));
            }
            // If parent has no resolved height, relative height declarations can be ignored
        }
        UnitValue minHeightUV = getPropertyAsUnitValue(this, Property.MIN_HEIGHT);
        if (minHeightUV != null) {
            if (minHeightUV.isPointValue()) {
                Float minHeight = retrieveMinHeight();
                UnitValue updateminHeight = UnitValue.createPointValue((float) (minHeight - usedHeight));
                overflowRenderer.updateMinHeight(updateminHeight);
            } else if (parentResolvedHeightPropertyValue != null) {
                // Calculate occupied fraction and update overflow renderer
                float currentOccupiedFraction = usedHeight / (float) parentResolvedHeightPropertyValue * 100;
                // Fraction
                float newFraction = minHeightUV.getValue() - currentOccupiedFraction;
                // Update
                overflowRenderer.updateMinHeight(UnitValue.createPercentValue(newFraction));
            }
            // If parent has no resolved height, relative height declarations can be ignored
        }

        UnitValue heightUV = getPropertyAsUnitValue(this, Property.HEIGHT);
        if (heightUV != null) {
            if (heightUV.isPointValue()) {
                Float height = retrieveHeight();
                UnitValue updateHeight = UnitValue.createPointValue((float) (height - usedHeight));
                overflowRenderer.updateHeight(updateHeight);
            } else if (parentResolvedHeightPropertyValue != null) {
                // Calculate occupied fraction and update overflow renderer
                float currentOccupiedFraction = usedHeight / (float) parentResolvedHeightPropertyValue * 100;
                // Fraction
                float newFraction = heightUV.getValue() - currentOccupiedFraction;
                // Update
                overflowRenderer.updateMinHeight(UnitValue.createPercentValue(newFraction));
            }
            // If parent has no resolved height, relative height declarations can be ignored
        }
    }

    /**
     * Calculates min and max width values for current renderer.
     *
     * @return instance of {@link MinMaxWidth}
     */
    public MinMaxWidth getMinMaxWidth() {
        return MinMaxWidthUtils.countDefaultMinMaxWidth(this);
    }

    protected boolean setMinMaxWidthBasedOnFixedWidth(MinMaxWidth minMaxWidth) {
        // retrieve returns max width, if there is no width.
        if (hasAbsoluteUnitValue(Property.WIDTH)) {
            //Renderer may override retrieveWidth, double check is required.
            Float width = retrieveWidth(0);
            if (width != null) {
                minMaxWidth.setChildrenMaxWidth((float) width);
                minMaxWidth.setChildrenMinWidth((float) width);
                return true;
            }
        }
        return false;
    }

    protected boolean isNotFittingHeight(LayoutArea layoutArea) {
        return !isPositioned() && occupiedArea.getBBox().getHeight() > layoutArea.getBBox().getHeight();
    }

    protected boolean isNotFittingWidth(LayoutArea layoutArea) {
        return !isPositioned() && occupiedArea.getBBox().getWidth() > layoutArea.getBBox().getWidth();
    }

    protected boolean isNotFittingLayoutArea(LayoutArea layoutArea) {
        return isNotFittingHeight(layoutArea) || isNotFittingWidth(layoutArea);
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
        return isKeepTogether(null);
    }

    boolean isKeepTogether(IRenderer causeOfNothing) {
        return Boolean.TRUE.equals(getPropertyAsBoolean(Property.KEEP_TOGETHER))
                && !(causeOfNothing instanceof AreaBreakRenderer);
    }

    // Note! The second parameter is here on purpose. Currently occupied area is passed as a value of this parameter in
    // BlockRenderer, but actually, the block can have many areas, and occupied area will be the common area of sub-areas,
    // whereas child element alignment should be performed area-wise.
    protected void alignChildHorizontally(IRenderer childRenderer, Rectangle currentArea) {
        float availableWidth = currentArea.getWidth();
        HorizontalAlignment horizontalAlignment = childRenderer.<HorizontalAlignment>getProperty(Property.HORIZONTAL_ALIGNMENT);
        if (horizontalAlignment != null && horizontalAlignment != HorizontalAlignment.LEFT) {
            float freeSpace = availableWidth - childRenderer.getOccupiedArea().getBBox().getWidth();
            if (freeSpace > 0) {
                try {
                    switch (horizontalAlignment) {
                        case RIGHT:
                            childRenderer.move(freeSpace, 0);
                            break;
                        case CENTER:
                            childRenderer.move(freeSpace / 2, 0);
                            break;
                    }
                } catch (NullPointerException e) {
                    Logger logger = LoggerFactory.getLogger(AbstractRenderer.class);
                    logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                            "Some of the children might not end up aligned horizontally."));
                }
            }
        }
    }

    /**
     * Gets borders of the element in the specified order: top, right, bottom, left.
     *
     * @return an array of BorderDrawer objects.
     * In case when certain border isn't set <code>Property.BORDER</code> is used,
     * and if <code>Property.BORDER</code> is also not set then <code>null</code> is returned
     * on position of this border
     */
    protected Border[] getBorders() {
        return getBorders(this);
    }

    /**
     * Gets border radii of the element in the specified order: top-left, top-right, bottom-right, bottom-left.
     *
     * @return an array of BorderRadius objects.
     * In case when certain border radius isn't set <code>Property.BORDER_RADIUS</code> is used,
     * and if <code>Property.BORDER_RADIUS</code> is also not set then <code>null</code> is returned
     * on position of this border radius
     */
    protected BorderRadius[] getBorderRadii() {
        return getBorderRadii(this);
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
     *
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

            if (renderer.<Transform>getProperty(Property.TRANSFORM) != null) {
                if (renderer instanceof BlockRenderer || renderer instanceof ImageRenderer || renderer instanceof TableRenderer) {
                    AffineTransform rotationTransform = renderer.createTransformationInsideOccupiedArea();
                    transformPoints(contentBoxPoints, rotationTransform);
                }
            }
            renderer = (AbstractRenderer) renderer.parent;
        }

        return calculateBBox(contentBoxPoints);
    }

    /**
     * Calculates bounding box around points.
     *
     * @param points list of the points calculated bbox will enclose.
     * @return array of float values which denote left, bottom, right, top lines of bbox in this specific order
     */
    protected Rectangle calculateBBox(List<Point> points) {
        return Rectangle.calculateBBox(points);
    }

    protected List<Point> rectangleToPointsList(Rectangle rect) {
        return Arrays.asList(rect.toPointsArray());
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
     *
     * @param left   x coordinate at which points bbox left border is to be aligned
     * @param top    y coordinate at which points bbox upper border is to be aligned
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
        return new float[]{dx, dy};
    }

    /**
     * Check if corresponding property has point value.
     *
     * @param property {@link Property}
     * @return false if property value either null, or percent, otherwise true.
     */
    protected boolean hasAbsoluteUnitValue(int property) {
        UnitValue value = this.<UnitValue>getProperty(property);
        return value != null && value.isPointValue();
    }

    /**
     * Check if corresponding property has relative value.
     *
     * @param property {@link Property}
     * @return false if property value either null, or point, otherwise true.
     */
    protected boolean hasRelativeUnitValue(int property) {
        UnitValue value = this.<UnitValue>getProperty(property);
        return value != null && value.isPercentValue();
    }

    boolean isFirstOnRootArea(boolean checkRootAreaOnly) {
        boolean isFirstOnRootArea = true;
        IRenderer ancestor = this;
        while (isFirstOnRootArea && ancestor.getParent() != null) {
            IRenderer parent = ancestor.getParent();
            if (parent instanceof RootRenderer) {
                isFirstOnRootArea = ((RootRenderer) parent).currentArea.isEmptyArea();
            } else if (parent.getOccupiedArea() == null) {
                break;
            } else if (!checkRootAreaOnly) {
                isFirstOnRootArea = parent.getOccupiedArea().getBBox().getHeight() < EPS;
            }
            ancestor = parent;
        }
        return isFirstOnRootArea;
    }

    /**
     * Gets pdf document from root renderers.
     *
     * @return PdfDocument, or null if there are no document
     */
    PdfDocument getPdfDocument() {
        RootRenderer renderer = getRootRenderer();
        if (renderer instanceof DocumentRenderer) {
            final Document document = ((DocumentRenderer) renderer).document;
            return document.getPdfDocument();
        } else if (renderer instanceof CanvasRenderer) {
            return ((CanvasRenderer) renderer).canvas.getPdfDocument();
        } else {
            return null;
        }
    }

    RootRenderer getRootRenderer() {
        IRenderer currentRenderer = this;
        while (currentRenderer instanceof AbstractRenderer) {
            if (currentRenderer instanceof RootRenderer) {
                return (RootRenderer) currentRenderer;
            }
            currentRenderer = ((AbstractRenderer) currentRenderer).getParent();
        }
        return null;
    }

    static float calculateAdditionalWidth(AbstractRenderer renderer) {
        Rectangle dummy = new Rectangle(0, 0);
        renderer.applyMargins(dummy, true);
        renderer.applyBorderBox(dummy, true);
        renderer.applyPaddings(dummy, true);
        return dummy.getWidth();
    }

    static boolean noAbsolutePositionInfo(IRenderer renderer) {
        return !renderer.hasProperty(Property.TOP) && !renderer.hasProperty(Property.BOTTOM) && !renderer.hasProperty(Property.LEFT) && !renderer.hasProperty(Property.RIGHT);
    }

    static Float getPropertyAsFloat(IRenderer renderer, int property) {
        return NumberUtil.asFloat(renderer.<Object>getProperty(property));
    }

    /**
     * Returns the property of the renderer as a UnitValue if it exists and is a UnitValue, null otherwise
     *
     * @param renderer renderer to retrieve the property from
     * @param property key for the property to retrieve
     * @return A UnitValue if the property is present and is a UnitValue, null otherwise
     */
    static UnitValue getPropertyAsUnitValue(IRenderer renderer, int property) {
        UnitValue result = renderer.<UnitValue>getProperty(property);
        return result;

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

    /**
     * Gets any valid {@link PdfFont} for this renderer, based on {@link Property#FONT}, {@link Property#FONT_PROVIDER} and
     * {@link Property#FONT_SET} properties.
     * This method will not change font property of renderer. Also it is not guarantied that returned font will contain
     * all glyphs used in renderer or its children.
     * <p>
     * This method is usually needed for evaluating some layout characteristics like ascender or descender.
     *
     * @return a valid {@link PdfFont} instance based on renderer {@link Property#FONT} property.
     */
    PdfFont resolveFirstPdfFont() {
        Object font = this.<Object>getProperty(Property.FONT);
        if (font instanceof PdfFont) {
            return (PdfFont) font;
        } else if (font instanceof String[]) {
            FontProvider provider = this.<FontProvider>getProperty(Property.FONT_PROVIDER);
            if (provider == null) {
                throw new IllegalStateException(
                        LayoutExceptionMessageConstant.FONT_PROVIDER_NOT_SET_FONT_FAMILY_NOT_RESOLVED);
            }
            FontSet fontSet = this.<FontSet>getProperty(Property.FONT_SET);
            if (provider.getFontSet().isEmpty() && (fontSet == null || fontSet.isEmpty())) {
                throw new IllegalStateException(
                        LayoutExceptionMessageConstant.FONT_PROVIDER_NOT_SET_FONT_FAMILY_NOT_RESOLVED);
            }
            FontCharacteristics fc = createFontCharacteristics();
            return resolveFirstPdfFont((String[]) font, provider, fc, fontSet);
        } else {
            throw new IllegalStateException("String[] or PdfFont expected as value of FONT property");
        }
    }

    /**
     * Get first valid {@link PdfFont} for this renderer, based on given font-families, font provider and font characteristics.
     * This method will not change font property of renderer. Also it is not guarantied that returned font will contain
     * all glyphs used in renderer or its children.
     * <p>
     * This method is usually needed for evaluating some layout characteristics like ascender or descender.
     *
     * @return a valid {@link PdfFont} instance based on renderer {@link Property#FONT} property.
     */
    PdfFont resolveFirstPdfFont(String[] font, FontProvider provider, FontCharacteristics fc, FontSet additionalFonts) {
        FontSelector fontSelector = provider.getFontSelector(Arrays.asList(font), fc, additionalFonts);
        return provider.getPdfFont(fontSelector.bestMatch(), additionalFonts);
    }

    static Border[] getBorders(IRenderer renderer) {
        Border topBorder = renderer.<Border>getProperty(Property.BORDER_TOP);
        Border rightBorder = renderer.<Border>getProperty(Property.BORDER_RIGHT);
        Border bottomBorder = renderer.<Border>getProperty(Property.BORDER_BOTTOM);
        Border leftBorder = renderer.<Border>getProperty(Property.BORDER_LEFT);
        return new Border[]{topBorder, rightBorder, bottomBorder, leftBorder};
    }

    void applyAbsolutePositionIfNeeded(LayoutContext layoutContext) {
        if (isAbsolutePosition()) {
            applyAbsolutePosition(layoutContext instanceof PositionedLayoutContext ? ((PositionedLayoutContext) layoutContext).getParentOccupiedArea().getBBox() : layoutContext.getArea().getBBox());
        }
    }

    void preparePositionedRendererAndAreaForLayout(IRenderer childPositionedRenderer, Rectangle fullBbox, Rectangle parentBbox) {
        Float left = getPropertyAsFloat(childPositionedRenderer, Property.LEFT);
        Float right = getPropertyAsFloat(childPositionedRenderer, Property.RIGHT);
        Float top = getPropertyAsFloat(childPositionedRenderer, Property.TOP);
        Float bottom = getPropertyAsFloat(childPositionedRenderer, Property.BOTTOM);
        childPositionedRenderer.setParent(this);
        adjustPositionedRendererLayoutBoxWidth(childPositionedRenderer, fullBbox, left, right);

        if (Integer.valueOf(LayoutPosition.ABSOLUTE).equals(childPositionedRenderer.<Integer>getProperty(Property.POSITION))) {
            updateMinHeightForAbsolutelyPositionedRenderer(childPositionedRenderer, parentBbox, top, bottom);
        }
    }

    private void updateMinHeightForAbsolutelyPositionedRenderer(IRenderer renderer, Rectangle parentRendererBox, Float top, Float bottom) {
        if (top != null && bottom != null && !renderer.hasProperty(Property.HEIGHT)) {
            UnitValue currentMaxHeight = getPropertyAsUnitValue(renderer, Property.MAX_HEIGHT);
            UnitValue currentMinHeight = getPropertyAsUnitValue(renderer, Property.MIN_HEIGHT);
            float resolvedMinHeight = Math.max(0, parentRendererBox.getTop() - (float) top - parentRendererBox.getBottom() - (float) bottom);

            Rectangle dummy = new Rectangle(0, 0);
            if (!isBorderBoxSizing(renderer)) {
                applyPaddings(dummy, getPaddings(renderer), true);
                applyBorderBox(dummy, getBorders(renderer), true);
            }
            applyMargins(dummy, getMargins(renderer), true);
            resolvedMinHeight -= dummy.getHeight();

            if (currentMinHeight != null) {
                resolvedMinHeight = Math.max(resolvedMinHeight, currentMinHeight.getValue());
            }
            if (currentMaxHeight != null) {
                resolvedMinHeight = Math.min(resolvedMinHeight, currentMaxHeight.getValue());
            }

            renderer.setProperty(Property.MIN_HEIGHT, UnitValue.createPointValue((float) resolvedMinHeight));
            renderer.setProperty(Property.HEIGHT, UnitValue.createPointValue((float) resolvedMinHeight));
        }
    }

    private void adjustPositionedRendererLayoutBoxWidth(IRenderer renderer, Rectangle fullBbox, Float left, Float right) {
        if (left != null) {
            fullBbox.setWidth(fullBbox.getWidth() - (float) left).setX(fullBbox.getX() + (float) left);
        }
        if (right != null) {
            fullBbox.setWidth(fullBbox.getWidth() - (float) right);
        }

        if (left == null && right == null && !renderer.hasProperty(Property.WIDTH)) {
            // Other, non-block renderers won't occupy full width anyway
            MinMaxWidth minMaxWidth = renderer instanceof BlockRenderer ? ((BlockRenderer) renderer).getMinMaxWidth() : null;
            if (minMaxWidth != null && minMaxWidth.getMaxWidth() < fullBbox.getWidth()) {
                fullBbox.setWidth(minMaxWidth.getMaxWidth() + AbstractRenderer.EPS);
            }
        }
    }

    static float calculatePaddingBorderWidth(AbstractRenderer renderer) {
        Rectangle dummy = new Rectangle(0, 0);
        renderer.applyBorderBox(dummy, true);
        renderer.applyPaddings(dummy, true);
        return dummy.getWidth();
    }

    static float calculatePaddingBorderHeight(AbstractRenderer renderer) {
        Rectangle dummy = new Rectangle(0, 0);
        renderer.applyBorderBox(dummy, true);
        renderer.applyPaddings(dummy, true);
        return dummy.getHeight();
    }

    /**
     * This method creates {@link AffineTransform} instance that could be used
     * to transform content inside the occupied area,
     * considering the centre of the occupiedArea as the origin of a coordinate system for transformation.
     *
     * @return {@link AffineTransform} that transforms the content and places it inside occupied area.
     */
    private AffineTransform createTransformationInsideOccupiedArea() {
        Rectangle backgroundArea = applyMargins(occupiedArea.clone().getBBox(), false);
        float x = backgroundArea.getX();
        float y = backgroundArea.getY();
        float height = backgroundArea.getHeight();
        float width = backgroundArea.getWidth();

        AffineTransform transform = AffineTransform.getTranslateInstance(-1 * (x + width / 2), -1 * (y + height / 2));
        transform.preConcatenate(Transform.getAffineTransform(this.<Transform>getProperty(Property.TRANSFORM), width, height));
        transform.preConcatenate(AffineTransform.getTranslateInstance(x + width / 2, y + height / 2));

        return transform;
    }

    protected void beginTransformationIfApplied(PdfCanvas canvas) {
        if (this.<Transform>getProperty(Property.TRANSFORM) != null) {
            AffineTransform transform = createTransformationInsideOccupiedArea();
            canvas.saveState().concatMatrix(transform);
        }
    }

    protected void endTransformationIfApplied(PdfCanvas canvas) {
        if (this.<Transform>getProperty(Property.TRANSFORM) != null) {
            canvas.restoreState();
        }
    }

    /**
     * Add the specified {@link IRenderer renderer} to the end of children list and update its
     * parent link to {@code this}.
     *
     * @param child the {@link IRenderer child renderer} to be add
     */
    void addChildRenderer(IRenderer child) {
        child.setParent(this);
        this.childRenderers.add(child);
    }

    /**
     * Add the specified collection of {@link IRenderer renderers} to the end of children list and
     * update their parent links to {@code this}.
     *
     * @param children the collection of {@link IRenderer child renderers} to be add
     */
    void addAllChildRenderers(List<IRenderer> children) {
        if (children == null) {
            return;
        }
        setThisAsParent(children);
        this.childRenderers.addAll(children);
    }

    /**
     * Inserts the specified collection of {@link IRenderer renderers} at the specified space of
     * children list and update their parent links to {@code this}.
     *
     * @param index index at which to insert the first element from the specified collection
     * @param children the collection of {@link IRenderer child renderers} to be add
     */
    void addAllChildRenderers(int index, List<IRenderer> children) {
        setThisAsParent(children);
        this.childRenderers.addAll(index, children);
    }

    /**
     * Set the specified collection of {@link IRenderer renderers} as the children for {@code this}
     * element. That meant that the old collection would be cleaned, all parent links in old
     * children to {@code this} would be erased (i.e. set to {@code null}) and then the specified
     * list of children would be added similar to {@link AbstractRenderer#addAllChildRenderers(List)}.
     *
     *
     * @param children the collection of children {@link IRenderer renderers} to be set
     */
    void setChildRenderers(List<IRenderer> children) {
        removeThisFromParents(this.childRenderers);
        this.childRenderers.clear();
        addAllChildRenderers(children);
    }

    /**
     * Remove the child {@link IRenderer renderer} at the specified place. If the removed renderer has
     * the parent link set to {@code this} and it would not present in the children list after
     * removal, then the parent link of the removed renderer would be erased (i.e. set to {@code null}.
     *
     * @param index the index of the renderer to be removed
     * @return the removed renderer
     */
    IRenderer removeChildRenderer(int index) {
        final IRenderer removed = this.childRenderers.remove(index);
        removeThisFromParent(removed);
        return removed;
    }

    /**
     * Remove the children {@link IRenderer renderers} which are contains in the specified collection.
     * If some of the removed renderers has the parent link set to {@code this}, then
     * the parent link of the removed renderer would be erased (i.e. set to {@code null}.
     *
     * @param children the collections of renderers to be removed from children list
     * @return {@code true} if the children list has been changed
     */
    boolean removeAllChildRenderers(Collection<IRenderer> children) {
        removeThisFromParents(children);
        return this.childRenderers.removeAll(children);
    }

    /**
     * Update the child {@link IRenderer renderer} at the specified place with the specified one.
     * If the removed renderer has the parent link set to {@code this}, then it would be erased
     * (i.e. set to {@code null}).
     *
     * @param index the index of the renderer to be updated
     * @param child the renderer to be set
     * @return the removed renderer
     */
    IRenderer setChildRenderer(int index, IRenderer child) {
        if (child != null) {
            child.setParent(this);
        }
        final IRenderer removedElement = this.childRenderers.set(index, child);
        removeThisFromParent(removedElement);
        return removedElement;
    }

    /**
     * Sets current {@link AbstractRenderer} as parent to renderers in specified collection.
     * @param children the collection of renderers to set the parent renderer on
     */
    void setThisAsParent(Collection<IRenderer> children) {
        for (final IRenderer child : children) {
            child.setParent(this);
        }
    }

    boolean logWarningIfGetNextRendererNotOverridden(Class<?> baseClass, Class<?> rendererClass) {
        if (baseClass != rendererClass) {
            final Logger logger = LoggerFactory.getLogger(baseClass);
            logger.warn(MessageFormatUtil.format(IoLogMessageConstant.GET_NEXT_RENDERER_SHOULD_BE_OVERRIDDEN));
            return false;
        } else {
            return true;
        }
    }

    private void removeThisFromParent(IRenderer toRemove) {
        // we need to be sure that the removed element has no other entries in child renderers list
        if (toRemove != null && this == toRemove.getParent() && !this.childRenderers.contains(toRemove)) {
            toRemove.setParent(null);
        }
    }

    private void removeThisFromParents(Collection<IRenderer> children) {
        for (final IRenderer child : children) {
            if (child != null && this == child.getParent()) {
                child.setParent(null);
            }
        }
    }

    private static UnitValue[] getMargins(IRenderer renderer) {
        return new UnitValue[]{renderer.<UnitValue>getProperty(Property.MARGIN_TOP), renderer.<UnitValue>getProperty(Property.MARGIN_RIGHT),
                renderer.<UnitValue>getProperty(Property.MARGIN_BOTTOM), renderer.<UnitValue>getProperty(Property.MARGIN_LEFT)};
    }

    private static BorderRadius[] getBorderRadii(IRenderer renderer) {
        BorderRadius topLeftRadius = renderer.<BorderRadius>getProperty(Property.BORDER_TOP_LEFT_RADIUS);
        BorderRadius topRightRadius = renderer.<BorderRadius>getProperty(Property.BORDER_TOP_RIGHT_RADIUS);
        BorderRadius bottomRightRadius = renderer.<BorderRadius>getProperty(Property.BORDER_BOTTOM_RIGHT_RADIUS);
        BorderRadius bottomLeftRadius = renderer.<BorderRadius>getProperty(Property.BORDER_BOTTOM_LEFT_RADIUS);

        return new BorderRadius[]{topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius};
    }

    private static UnitValue[] getPaddings(IRenderer renderer) {
        return new UnitValue[]{renderer.<UnitValue>getProperty(Property.PADDING_TOP), renderer.<UnitValue>getProperty(Property.PADDING_RIGHT),
                renderer.<UnitValue>getProperty(Property.PADDING_BOTTOM), renderer.<UnitValue>getProperty(Property.PADDING_LEFT)};
    }

    private static boolean hasOwnOrModelProperty(IRenderer renderer, int property) {
        return renderer.hasOwnProperty(property) || (null != renderer.getModelElement() && renderer.getModelElement().hasProperty(property));
    }

    private static PdfStructElem getCurrentStructElem(PdfDocument document) {
        TagStructureContext context = document.getTagStructureContext();
        TagTreePointer tagPointer = context.getAutoTaggingPointer();
        return context.getPointerStructElem(tagPointer);
    }
}
