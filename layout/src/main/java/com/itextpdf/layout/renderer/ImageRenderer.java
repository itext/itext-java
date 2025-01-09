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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.ObjectFit;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.objectfit.ObjectFitApplyingResult;
import com.itextpdf.layout.renderer.objectfit.ObjectFitCalculator;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageRenderer extends AbstractRenderer implements ILeafElementRenderer {

    protected Float fixedXPosition;
    protected Float fixedYPosition;
    protected float pivotY;
    protected float deltaX;
    protected float imageWidth;
    protected float imageHeight;
    float[] matrix = new float[6];
    private Float height;
    private Float width;
    private float renderedImageHeight;
    private float renderedImageWidth;
    private boolean doesObjectFitRequireCutting;
    private Rectangle initialOccupiedAreaBBox;
    private float rotatedDeltaX;
    private float rotatedDeltaY;

    /**
     * Creates an ImageRenderer from its corresponding layout object.
     *
     * @param image the {@link com.itextpdf.layout.element.Image} which this object should manage
     */
    public ImageRenderer(Image image) {
        super(image);
        imageWidth = image.getImageWidth();
        imageHeight = image.getImageHeight();
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea().clone();
        Rectangle layoutBox = area.getBBox().clone();

        AffineTransform t = new AffineTransform();
        Image modelElement = (Image) (getModelElement());
        PdfXObject xObject = modelElement.getXObject();

        calculateImageDimensions(layoutBox, t, xObject);

        OverflowPropertyValue overflowX = null != parent
                ? parent.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X)
                : OverflowPropertyValue.FIT;

        boolean nowrap = false;
        if (parent instanceof LineRenderer) {
            nowrap = Boolean.TRUE.equals(this.parent.<Boolean>getOwnProperty(Property.NO_SOFT_WRAP_INLINE));
        }

        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();
        float clearHeightCorrection = FloatingHelper.calculateClearHeightCorrection(this, floatRendererAreas, layoutBox);
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);
        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            layoutBox.decreaseHeight(clearHeightCorrection);
            FloatingHelper.adjustFloatedBlockLayoutBox(this, layoutBox, width, floatRendererAreas, floatPropertyValue, overflowX);
        } else {
            clearHeightCorrection = FloatingHelper.adjustLayoutBoxAccordingToFloats(floatRendererAreas, layoutBox, width, clearHeightCorrection, null);
        }

        applyMargins(layoutBox, false);
        Border[] borders = getBorders();
        applyBorderBox(layoutBox, borders, false);

        Float declaredMaxHeight = retrieveMaxHeight();
        OverflowPropertyValue overflowY = null == parent
                || ((null == declaredMaxHeight || declaredMaxHeight > layoutBox.getHeight())
                && !layoutContext.isClippedHeight())
                ? OverflowPropertyValue.FIT
                : parent.<OverflowPropertyValue>getProperty(Property.OVERFLOW_Y);
        boolean processOverflowX = !isOverflowFit(overflowX) || nowrap;
        boolean processOverflowY = !isOverflowFit(overflowY);
        if (isAbsolutePosition()) {
            applyAbsolutePosition(layoutBox);
        }
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), 0, 0));

        TargetCounterHandler.addPageByID(this);

        float imageContainerWidth = (float) width;
        float imageContainerHeight = (float) height;

        if (isFixedLayout()) {
            fixedXPosition = this.getPropertyAsFloat(Property.LEFT);
            fixedYPosition = this.getPropertyAsFloat(Property.BOTTOM);
        }

        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        // See in adjustPositionAfterRotation why angle = 0 is necessary
        if (null == angle) {
            angle = 0f;
        }
        t.rotate((float) angle);
        initialOccupiedAreaBBox = getOccupiedAreaBBox().clone();
        float scaleCoef = adjustPositionAfterRotation((float) angle, layoutBox.getWidth(), layoutBox.getHeight());

        imageContainerHeight *= scaleCoef;
        imageContainerWidth *= scaleCoef;

        initialOccupiedAreaBBox.moveDown(imageContainerHeight);
        initialOccupiedAreaBBox.setHeight(imageContainerHeight);
        initialOccupiedAreaBBox.setWidth(imageContainerWidth);
        if (xObject instanceof PdfFormXObject) {
            t.scale(scaleCoef, scaleCoef);
        }

        float imageItselfWidth;
        float imageItselfHeight;

        applyObjectFit(modelElement.getObjectFit(), imageWidth, imageHeight);
        if (modelElement.getObjectFit() == ObjectFit.FILL) {
            imageItselfWidth = imageContainerWidth;
            imageItselfHeight = imageContainerHeight;
        } else {
            imageItselfWidth = renderedImageWidth;
            imageItselfHeight = renderedImageHeight;
        }
        getMatrix(t, imageItselfWidth, imageItselfHeight);

        // indicates whether the placement is forced
        boolean isPlacingForced = false;
        if (width > layoutBox.getWidth() + EPS || height > layoutBox.getHeight() + EPS) {
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                isPlacingForced = true;
            } else {
                isPlacingForced = true;
                if (width > layoutBox.getWidth() + EPS) {
                    isPlacingForced &= processOverflowX;
                }
                if (height > layoutBox.getHeight() + EPS) {
                    isPlacingForced &= processOverflowY;
                }
            }

            if (!isPlacingForced) {
                applyMargins(initialOccupiedAreaBBox, true);
                applyBorderBox(initialOccupiedAreaBBox, true);
                occupiedArea.getBBox().setHeight(initialOccupiedAreaBBox.getHeight());
                return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this, this);
            }
        }

        occupiedArea.getBBox().moveDown((float) height);
        if (borders[3] != null) {
            final float delta = (float) Math.sin((float) angle) * borders[3].getWidth();
            final float renderScaling = renderedImageHeight / (float) height;
            height += delta;
            renderedImageHeight += delta * renderScaling;
        }
        occupiedArea.getBBox().setHeight((float) height);
        occupiedArea.getBBox().setWidth((float) width);

        UnitValue leftMargin = this.getPropertyAsUnitValue(Property.MARGIN_LEFT);
        if (!leftMargin.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(ImageRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_LEFT));
        }
        UnitValue topMargin = this.getPropertyAsUnitValue(Property.MARGIN_TOP);
        if (!topMargin.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(ImageRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED,
                    Property.MARGIN_TOP));
        }

        if (0 != leftMargin.getValue() || 0 != topMargin.getValue()) {
            translateImage(leftMargin.getValue(), topMargin.getValue(), t);
            getMatrix(t, imageContainerWidth, imageContainerHeight);
        }

        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), true);

        if (angle != 0) {
            applyRotationLayout((float) angle);
        }

        float unscaledWidth = occupiedArea.getBBox().getWidth() / scaleCoef;
        MinMaxWidth minMaxWidth = new MinMaxWidth(unscaledWidth, unscaledWidth, 0);
        UnitValue rendererWidth = this.<UnitValue>getProperty(Property.WIDTH);

        if (rendererWidth != null && rendererWidth.isPercentValue()) {
            minMaxWidth.setChildrenMinWidth(0);
            float coeff = imageWidth / (float) retrieveWidth(area.getBBox().getWidth());
            minMaxWidth.setChildrenMaxWidth(unscaledWidth * coeff);
        } else {
            boolean autoScale = hasProperty(Property.AUTO_SCALE) && (boolean) this.<Boolean>getProperty(Property.AUTO_SCALE);
            boolean autoScaleWidth = hasProperty(Property.AUTO_SCALE_WIDTH) && (boolean) this.<Boolean>getProperty(Property.AUTO_SCALE_WIDTH);
            if (autoScale || autoScaleWidth) {
                minMaxWidth.setChildrenMinWidth(0);
            }
        }

        FloatingHelper.removeFloatsAboveRendererBottom(floatRendererAreas, this);
        LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, floatRendererAreas, layoutContext.getArea().getBBox(), clearHeightCorrection, false);

        applyAbsolutePositionIfNeeded(layoutContext);

        return new MinMaxWidthLayoutResult(LayoutResult.FULL, editedArea, null, null, isPlacingForced ? this : null)
                .setMinMaxWidth(minMaxWidth);
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(ImageRenderer.class);
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Drawing won't be performed."));
            return;
        }

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        boolean isTagged = drawContext.isTaggingEnabled();
        LayoutTaggingHelper taggingHelper = null;
        boolean isArtifact = false;
        TagTreePointer tagPointer = null;
        if (isTagged) {
            taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper == null) {
                isArtifact = true;
            } else {
                isArtifact = taggingHelper.isArtifact(this);
                if (!isArtifact) {
                    tagPointer = taggingHelper.useAutoTaggingPointerAndRememberItsPosition(this);
                    if (taggingHelper.createTag(this, tagPointer)) {
                        tagPointer.getProperties().addAttributes(0, AccessibleAttributesApplier.getLayoutAttributes(this, tagPointer));
                    }
                }
            }
        }

        beginTransformationIfApplied(drawContext.getCanvas());

        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            drawContext.getCanvas().saveState();
            applyConcatMatrix(drawContext, angle);
        }

        super.draw(drawContext);

        boolean clipImageInAViewOfBorderRadius = clipBackgroundArea(drawContext, applyMargins(getOccupiedAreaBBox(), false), true);
        applyMargins(occupiedArea.getBBox(), false);
        applyBorderBox(occupiedArea.getBBox(), getBorders(), false);

        if (fixedYPosition == null) {
            fixedYPosition = occupiedArea.getBBox().getY() + pivotY;
        }
        if (fixedXPosition == null) {
            fixedXPosition = occupiedArea.getBBox().getX();
        }

        if (angle != null) {
            fixedXPosition += rotatedDeltaX;
            fixedYPosition -= rotatedDeltaY;
            drawContext.getCanvas().restoreState();
        }
        PdfCanvas canvas = drawContext.getCanvas();
        if (isTagged) {
            if (isArtifact) {
                canvas.openTag(new CanvasArtifact());
            } else {
                canvas.openTag(tagPointer.getTagReference());

            }
        }

        beginObjectFitImageClipping(canvas);

        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        beginElementOpacityApplying(drawContext);

        final float renderedImageShiftX = ((float) width - renderedImageWidth) / 2;
        final float renderedImageShiftY = ((float) height - renderedImageHeight) / 2;
        canvas.addXObjectWithTransformationMatrix(xObject, matrix[0], matrix[1], matrix[2], matrix[3], (float) fixedXPosition +
                deltaX + renderedImageShiftX, (float) fixedYPosition + renderedImageShiftY);

        endElementOpacityApplying(drawContext);
        endObjectFitImageClipping(canvas);
        endTransformationIfApplied(drawContext.getCanvas());

        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FLUSH_ON_DRAW))) {
            xObject.flush();
        }

        if (isTagged) {
            canvas.closeTag();
        }

        if (clipImageInAViewOfBorderRadius) {
            canvas.restoreState();
        }

        if (isRelativePosition) {
            applyRelativePositioningTranslation(true);
        }
        applyBorderBox(occupiedArea.getBBox(), getBorders(), true);
        applyMargins(occupiedArea.getBBox(), true);

        if (isTagged && !isArtifact) {
            taggingHelper.finishTaggingHint(this);
            taggingHelper.restoreAutoTaggingPointerPosition(this);
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return null;
    }

    @Override
    public Rectangle getBorderAreaBBox() {
        applyMargins(initialOccupiedAreaBBox, false);
        applyBorderBox(initialOccupiedAreaBBox, getBorders(), false);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }
        applyMargins(initialOccupiedAreaBBox, true);
        applyBorderBox(initialOccupiedAreaBBox, true);
        return initialOccupiedAreaBBox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean hasAspectRatio() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Float getAspectRatio() {
        return imageWidth / imageHeight;
    }

    /**
     * Gets original width of the image, not the width set by {@link Image#setWidth} method.
     *
     * @return original image width
     */
    public float getImageWidth() {
        return imageWidth;
    }

    /**
     * Gets original height of the image, not the height set by {@link Image#setHeight} method.
     *
     * @return original image height
     */
    public float getImageHeight() {
        return imageHeight;
    }

    @Override
    protected Rectangle applyPaddings(Rectangle rect, UnitValue[] paddings, boolean reverse) {
        return rect;
    }

    @Override
    public void move(float dxRight, float dyUp) {
        super.move(dxRight, dyUp);
        if (initialOccupiedAreaBBox != null) {
            initialOccupiedAreaBBox.moveRight(dxRight);
            initialOccupiedAreaBBox.moveUp(dyUp);
        }
        if (fixedXPosition != null) {
            fixedXPosition += dxRight;
        }
        if (fixedYPosition != null) {
            fixedYPosition += dyUp;
        }
    }

    @Override
    public MinMaxWidth getMinMaxWidth() {
        return ((MinMaxWidthLayoutResult) layout(new LayoutContext(new LayoutArea(1, new Rectangle(MinMaxWidthUtils.getInfWidth(), AbstractRenderer.INF))))).getMinMaxWidth();
    }

    protected ImageRenderer autoScale(LayoutArea layoutArea) {
        Rectangle area = layoutArea.getBBox().clone();
        applyMargins(area, false);
        applyBorderBox(area, false);
        // if rotation was applied, width would be equal to the width of rectangle bounding the rotated image
        float angleScaleCoef = imageWidth / (float) width;
        if (width > angleScaleCoef * area.getWidth()) {
            updateHeight(UnitValue.createPointValue(area.getWidth() / (float)width * imageHeight));
            updateWidth(UnitValue.createPointValue(angleScaleCoef * area.getWidth()));
        }

        return this;
    }

    private void applyObjectFit(ObjectFit objectFit, float imageWidth, float imageHeight) {
        final ObjectFitApplyingResult result = ObjectFitCalculator.calculateRenderedImageSize(objectFit,
                imageWidth, imageHeight, (float) width, (float) height);
        renderedImageWidth = (float) result.getRenderedImageWidth();
        renderedImageHeight = (float) result.getRenderedImageHeight();
        doesObjectFitRequireCutting = result.isImageCuttingRequired();
    }

    private void beginObjectFitImageClipping(PdfCanvas canvas) {
        if (doesObjectFitRequireCutting) {
            canvas.saveState();
            final Rectangle clippedArea = new Rectangle((float) fixedXPosition,
                    (float) fixedYPosition, (float) width, (float) height);
            canvas.rectangle(clippedArea).clip().endPath();
        }
    }

    private void endObjectFitImageClipping(PdfCanvas canvas) {
        if (doesObjectFitRequireCutting) {
            canvas.restoreState();
        }
    }

    private void calculateImageDimensions(Rectangle layoutBox, AffineTransform t, PdfXObject xObject) {
        width = this.<UnitValue>getProperty(Property.WIDTH) != null ? retrieveWidth(layoutBox.getWidth()) : null;
        Float declaredHeight = retrieveHeight();
        height = declaredHeight;
        if (width == null && height == null) {
            width = imageWidth;
            height = (float) width / imageWidth * imageHeight;
        } else if (width == null) {
            width = (float) height / imageHeight * imageWidth;
        } else if (height == null) {
            height = (float) width / imageWidth * imageHeight;
        }

        Float horizontalScaling = this.getPropertyAsFloat(Property.HORIZONTAL_SCALING, 1f);
        Float verticalScaling = this.getPropertyAsFloat(Property.VERTICAL_SCALING, 1f);


        if (xObject instanceof PdfFormXObject && width != imageWidth) {
            horizontalScaling *= width / imageWidth;
            verticalScaling *= height / imageHeight;
        }

        if (horizontalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale((float) horizontalScaling, 1);
                width = imageWidth * (float) horizontalScaling;
            } else {
                width *= (float) horizontalScaling;
            }
        }
        if (verticalScaling != 1) {
            if (xObject instanceof PdfFormXObject) {
                t.scale(1, (float) verticalScaling);
                height = imageHeight * (float) verticalScaling;
            } else {
                height *= (float) verticalScaling;
            }
        }

        // Constrain width and height according to min/max width
        Float minWidth = retrieveMinWidth(layoutBox.getWidth());
        Float maxWidth = retrieveMaxWidth(layoutBox.getWidth());
        if (null != minWidth && width < minWidth) {
            height *= minWidth / width;
            width = minWidth;
        } else if (null != maxWidth && width > maxWidth) {
            height *= maxWidth / width;
            width = maxWidth;
        }

        // Constrain width and height according to min/max height, which has precedence over width settings
        Float minHeight = retrieveMinHeight();
        Float maxHeight = retrieveMaxHeight();
        if (null != minHeight && height < minHeight) {
            width *= minHeight / height;
            height = minHeight;
        } else if (null != maxHeight && height > maxHeight) {
            width *= maxHeight / height;
            this.height = maxHeight;
        } else if (null != declaredHeight && !height.equals(declaredHeight)) {
            width *= declaredHeight / height;
            height = declaredHeight;
        }
    }

    private void getMatrix(AffineTransform t, float imageItselfScaledWidth, float imageItselfScaledHeight) {
        t.getMatrix(matrix);
        PdfXObject xObject = ((Image) (getModelElement())).getXObject();
        if (xObject instanceof PdfImageXObject) {
            matrix[0] *= imageItselfScaledWidth;
            matrix[1] *= imageItselfScaledWidth;
            matrix[2] *= imageItselfScaledHeight;
            matrix[3] *= imageItselfScaledHeight;
        }
    }

    private float adjustPositionAfterRotation(float angle, float maxWidth, float maxHeight) {
        if (angle != 0) {
            AffineTransform t = AffineTransform.getRotateInstance(angle);
            Point p00 = t.transform(new Point(0, 0), new Point());
            Point p01 = t.transform(new Point(0, (float) height), new Point());
            Point p10 = t.transform(new Point((float) width, 0), new Point());
            Point p11 = t.transform(new Point((float) width, (float) height), new Point());

            double[] xValues = {p01.getX(), p10.getX(), p11.getX()};
            double[] yValues = {p01.getY(), p10.getY(), p11.getY()};

            double minX = p00.getX();
            double minY = p00.getY();
            double maxX = minX;
            double maxY = minY;

            for (double x : xValues) {
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
            }
            for (double y : yValues) {
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }

            height = (float) (maxY - minY);
            width = (float) (maxX - minX);
            pivotY = (float) (p00.getY() - minY);

            deltaX = -(float) minX;
        }
        // Rotating image can cause fitting into area problems.
        // So let's find scaling coefficient
        float scaleCoeff = 1;
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.AUTO_SCALE))) {
            if (maxWidth / (float) width <  maxHeight / (float) height) {
                scaleCoeff = maxWidth / (float) width;
                height *= maxWidth / (float) width;
                width = maxWidth;
            } else {
                scaleCoeff = maxHeight / (float) height;
                width *= maxHeight / (float) height;
                height = maxHeight;
            }
        } else if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.AUTO_SCALE_WIDTH))) {
            scaleCoeff = maxWidth / (float) width;
            height *= scaleCoeff;
            width = maxWidth;
        } else if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.AUTO_SCALE_HEIGHT))) {
            scaleCoeff = maxHeight / (float) height;
            height = maxHeight;
            width *= scaleCoeff;
        }
        pivotY *= scaleCoeff;
        deltaX *= scaleCoeff;
        return scaleCoeff;
    }

    private void translateImage(float xDistance, float yDistance, AffineTransform t) {
        t.translate(xDistance, yDistance);
        t.getMatrix(matrix);
        if (fixedXPosition != null) {
            fixedXPosition += (float) t.getTranslateX();
        }
        if (fixedYPosition != null) {
            fixedYPosition += (float) t.getTranslateY();
        }
    }

    private void applyConcatMatrix(DrawContext drawContext, Float angle) {
        AffineTransform rotationTransform = AffineTransform.getRotateInstance((float) angle);
        Rectangle rect = getBorderAreaBBox();

        List<Point> rotatedPoints = transformPoints(rectangleToPointsList(rect), rotationTransform);

        float[] shift = calculateShiftToPositionBBoxOfPointsAt(rect.getX(), rect.getY() + rect.getHeight(), rotatedPoints);

        double[] matrix = new double[6];
        rotationTransform.getMatrix(matrix);

        drawContext.getCanvas().concatMatrix(matrix[0], matrix[1], matrix[2], matrix[3], shift[0], shift[1]);
    }

    private void applyRotationLayout(float angle) {
        Border[] borders = getBorders();
        Rectangle rect = getBorderAreaBBox();

        float leftBorderWidth = borders[3] == null ? 0 : borders[3].getWidth();
        float rightBorderWidth = borders[1] == null ? 0 : borders[1].getWidth();
        float topBorderWidth = borders[0] == null ? 0 : borders[0].getWidth();
        if (leftBorderWidth != 0) {
            float gip = (float) Math.sqrt(Math.pow(topBorderWidth, 2) + Math.pow(leftBorderWidth, 2));
            double atan = Math.atan(topBorderWidth / leftBorderWidth);
            if (angle < 0) {
                atan = -atan;
            }
            rotatedDeltaX = Math.abs((float) (gip * Math.cos(angle - atan) - leftBorderWidth));
        } else {
            rotatedDeltaX = 0;
        }

        rect.moveRight(rotatedDeltaX);
        occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() + rotatedDeltaX);

        if (rightBorderWidth != 0) {
            float gip = (float) Math.sqrt(Math.pow(topBorderWidth, 2) + Math.pow(leftBorderWidth, 2));
            double atan = Math.atan(rightBorderWidth / topBorderWidth);
            if (angle < 0) {
                atan = -atan;
            }
            rotatedDeltaY = Math.abs((float) (gip * Math.cos(angle - atan) - topBorderWidth));
        } else {
            rotatedDeltaY = 0;
        }

        rect.moveDown(rotatedDeltaY);
        if (angle < 0) {
            rotatedDeltaY += rightBorderWidth;
        }
        occupiedArea.getBBox().increaseHeight(rotatedDeltaY);
    }

    @Override
    public float getAscent() {
        return occupiedArea.getBBox().getHeight();
    }

    @Override
    public float getDescent() {
        return 0;
    }
}
