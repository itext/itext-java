/*

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
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class BlockRenderer extends AbstractRenderer {

    protected BlockRenderer(IElement modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        overrideHeightProperties();
        boolean wasHeightClipped = false;
        int pageNumber = layoutContext.getArea().getPageNumber();

        boolean isPositioned = isPositioned();

        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();
        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null || isPositioned) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }

        float minWidth = 0;
        float additionalWidth = 0;
        float maxFullWidth = parentBBox.getWidth();

        MarginsCollapseHandler marginsCollapseHandler = new MarginsCollapseHandler(this, layoutContext.getMarginsCollapseInfo());
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.startMarginsCollapse(parentBBox);
        }
        float marginsBordersWidth = parentBBox.getWidth();
        applyMargins(parentBBox, false);
        Border[] borders = getBorders();
        applyBorderBox(parentBBox, borders, false);
        marginsBordersWidth -= parentBBox.getWidth();
        additionalWidth += marginsBordersWidth;

        if (isPositioned) {
            float x = (float) this.getPropertyAsFloat(Property.X);
            float relativeX = isFixedLayout() ? 0 : parentBBox.getX();
            parentBBox.setX(relativeX + x);
        }

        Float blockWidth = retrieveWidth(parentBBox.getWidth());
        if (blockWidth != null && (blockWidth < parentBBox.getWidth() || isPositioned)) {
            parentBBox.setWidth((float) blockWidth);
        }
        float[] paddings = getPaddings();
        float paddingsWidth = parentBBox.getWidth();
        applyPaddings(parentBBox, paddings, false);
        paddingsWidth -= parentBBox.getWidth();
        additionalWidth += paddingsWidth;

        Float blockMaxHeight = retrieveMaxHeight();
        if (!isFixedLayout() && null != blockMaxHeight && blockMaxHeight < parentBBox.getHeight()
                && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
            float heightDelta = parentBBox.getHeight() - (float) blockMaxHeight;
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler.processFixedHeightAdjustment(heightDelta);
            }
            parentBBox.moveUp(heightDelta).setHeight((float) blockMaxHeight);
            wasHeightClipped = true;
        }

        List<Rectangle> areas;
        if (isPositioned) {
            areas = Collections.singletonList(parentBBox);
        } else {
            areas = initElementAreas(new LayoutArea(pageNumber, parentBBox));
        }

        occupiedArea = new LayoutArea(pageNumber, new Rectangle(parentBBox.getX(), parentBBox.getY() + parentBBox.getHeight(), parentBBox.getWidth(), 0));
        int currentAreaPos = 0;

        Rectangle layoutBox = areas.get(0).clone();

        // the first renderer (one of childRenderers or their children) to produce LayoutResult.NOTHING
        IRenderer causeOfNothing = null;
        boolean anythingPlaced = false;
        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult result;
            childRenderer.setParent(this);
            MarginsCollapseInfo childMarginsInfo = null;
            if (marginsCollapsingEnabled) {
                childMarginsInfo = marginsCollapseHandler.startChildMarginsHandling(childRenderer, layoutBox);
            }
            while ((result = childRenderer.setParent(this).layout(new LayoutContext(new LayoutArea(pageNumber, layoutBox), childMarginsInfo))).getStatus() != LayoutResult.FULL) {
                minWidth = Math.max(minWidth, result.getMinFullWidth());
                if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA_ON_SPLIT))
                        || Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) {
                    occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), layoutBox));
                } else if (result.getOccupiedArea() != null && result.getStatus() != LayoutResult.NOTHING) {
                    occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                }

                if (childRenderer.getOccupiedArea() != null) {
                    alignChildHorizontally(childRenderer, layoutBox.getWidth());
                }

                // Save the first renderer to produce LayoutResult.NOTHING
                if (null == causeOfNothing && null != result.getCauseOfNothing()) {
                    causeOfNothing = result.getCauseOfNothing();
                }

                // have more areas
                if (currentAreaPos + 1 < areas.size()) {
                    if (result.getStatus() == LayoutResult.PARTIAL) {
                        childRenderers.set(childPos, result.getSplitRenderer());
                        // TODO linkedList would make it faster
                        childRenderers.add(childPos + 1, result.getOverflowRenderer());
                    } else {
                        childRenderers.set(childPos, result.getOverflowRenderer());
                        childPos--;
                    }
                    layoutBox = areas.get(++currentAreaPos).clone();
                    break;
                } else {
                    if (result.getStatus() == LayoutResult.PARTIAL) {

                        if (currentAreaPos + 1 == areas.size()) {
                            if (marginsCollapsingEnabled) {
                                marginsCollapseHandler.endChildMarginsHandling();
                                marginsCollapseHandler.endMarginsCollapse();
                            }

                            AbstractRenderer splitRenderer = createSplitRenderer(LayoutResult.PARTIAL);
                            splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                            splitRenderer.childRenderers.add(result.getSplitRenderer());
                            splitRenderer.occupiedArea = occupiedArea;

                            AbstractRenderer overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
                            // Apply forced placement only on split renderer
                            overflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);
                            List<IRenderer> overflowRendererChildren = new ArrayList<>();
                            overflowRendererChildren.add(result.getOverflowRenderer());
                            overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                            overflowRenderer.childRenderers = overflowRendererChildren;

                            applyPaddings(occupiedArea.getBBox(), paddings, true);
                            applyBorderBox(occupiedArea.getBBox(), borders, true);
                            applyMargins(occupiedArea.getBBox(), true);

                            if (hasProperty(Property.MAX_HEIGHT)) {
                                if (wasHeightClipped) {
                                    Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                                    logger.warn(LogMessageConstant.CLIP_ELEMENT);
                                    return new LayoutResult(LayoutResult.FULL, occupiedArea, splitRenderer, null);
                                }
                                overflowRenderer.setProperty(Property.MAX_HEIGHT, retrieveMaxHeight() - occupiedArea.getBBox().getHeight());
                            }
                            if (hasProperty(Property.MIN_HEIGHT)) {
                                overflowRenderer.setProperty(Property.MIN_HEIGHT, retrieveMinHeight() - occupiedArea.getBBox().getHeight());
                            }
                            if (hasProperty(Property.HEIGHT)) {
                                overflowRenderer.setProperty(Property.HEIGHT, retrieveHeight() - occupiedArea.getBBox().getHeight());
                            }
                            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer, causeOfNothing);
                        } else {
                            childRenderers.set(childPos, result.getSplitRenderer());
                            childRenderers.add(childPos + 1, result.getOverflowRenderer());
                            layoutBox = areas.get(++currentAreaPos).clone();
                            break;
                        }
                    } else if (result.getStatus() == LayoutResult.NOTHING) {
                        boolean keepTogether = isKeepTogether();
                        int layoutResult = anythingPlaced && !keepTogether ? LayoutResult.PARTIAL : LayoutResult.NOTHING;

                        if (marginsCollapsingEnabled) {
                            marginsCollapseHandler.endMarginsCollapse();
                        }
                        AbstractRenderer splitRenderer = createSplitRenderer(layoutResult);
                        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                        for (IRenderer renderer : splitRenderer.childRenderers) {
                            renderer.setParent(splitRenderer);
                        }

                        AbstractRenderer overflowRenderer = createOverflowRenderer(layoutResult);
                        List<IRenderer> overflowRendererChildren = new ArrayList<>();
                        overflowRendererChildren.add(result.getOverflowRenderer());
                        overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));

                        overflowRenderer.childRenderers = overflowRendererChildren;
                        if (keepTogether) {
                            splitRenderer = null;
                            overflowRenderer.childRenderers.clear();
                            overflowRenderer.childRenderers = new ArrayList<>(childRenderers);
                        }

                        applyPaddings(occupiedArea.getBBox(), paddings, true);
                        applyBorderBox(occupiedArea.getBBox(), borders, true);
                        applyMargins(occupiedArea.getBBox(), true);

                        if (hasProperty(Property.MAX_HEIGHT)) {
                            if (isPositioned) {
                                correctPositionedLayout(layoutBox);
                            }
                            if (wasHeightClipped) {
                                occupiedArea.getBBox()
                                        .moveDown((float) blockMaxHeight - occupiedArea.getBBox().getHeight())
                                        .setHeight((float) blockMaxHeight);
                                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                                logger.warn(LogMessageConstant.CLIP_ELEMENT);
                                return new LayoutResult(LayoutResult.FULL, occupiedArea, splitRenderer, null, minWidth + additionalWidth, maxFullWidth);
                            }
                            //overflowRenderer.setProperty(Property.MAX_HEIGHT, retrieveMaxHeight() - occupiedArea.getBBox().getHeight());
                        }
//                        if (hasProperty(Property.MIN_HEIGHT)) {
//                            overflowRenderer.setProperty(Property.MIN_HEIGHT, retrieveMinHeight() - occupiedArea.getBBox().getHeight());
//                        }
//                        if (hasProperty(Property.HEIGHT)) {
//                            overflowRenderer.setProperty(Property.HEIGHT, retrieveHeight() - occupiedArea.getBBox().getHeight());
//                        }

                        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null, minWidth + additionalWidth, maxFullWidth);
                        } else {
                            if (layoutResult != LayoutResult.NOTHING) {
                                return new LayoutResult(layoutResult, occupiedArea, splitRenderer, overflowRenderer, null, minWidth + additionalWidth, maxFullWidth);
                            } else {
                                return new LayoutResult(layoutResult, null, splitRenderer, overflowRenderer, result.getCauseOfNothing(), 0, maxFullWidth);
                            }
                        }
                    }
                }
            }
            minWidth = Math.max(minWidth, result.getMinFullWidth());
            anythingPlaced = true;

            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler.endChildMarginsHandling();
            }
            if (result.getStatus() == LayoutResult.FULL) {
                layoutBox.setHeight(result.getOccupiedArea().getBBox().getY() - layoutBox.getY());
                if (childRenderer.getOccupiedArea() != null) {
                    alignChildHorizontally(childRenderer, layoutBox.getWidth());
                }
            }

            // Save the first renderer to produce LayoutResult.NOTHING
            if (null == causeOfNothing && null != result.getCauseOfNothing()) {
                causeOfNothing = result.getCauseOfNothing();
            }
        }

        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) {
            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), layoutBox));
        }

        applyPaddings(occupiedArea.getBBox(), paddings, true);
        IRenderer overflowRenderer = null;
        Float blockMinHeight = retrieveMinHeight();
        if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) && null != blockMinHeight && blockMinHeight > occupiedArea.getBBox().getHeight()) {
            float blockBottom = occupiedArea.getBBox().getBottom() - ((float) blockMinHeight - occupiedArea.getBBox().getHeight());
            if (blockBottom >= layoutContext.getArea().getBBox().getBottom()) {
                occupiedArea.getBBox().setY(blockBottom).setHeight((float) blockMinHeight);
            } else if (!isFixedLayout()) {
                occupiedArea.getBBox()
                        .increaseHeight(occupiedArea.getBBox().getBottom() - layoutContext.getArea().getBBox().getBottom())
                        .setY(layoutContext.getArea().getBBox().getBottom());
                overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
                overflowRenderer.setProperty(Property.MIN_HEIGHT, (float) blockMinHeight - occupiedArea.getBBox().getHeight());
                if (hasProperty(Property.HEIGHT)) {
                    overflowRenderer.setProperty(Property.HEIGHT, retrieveHeight() - occupiedArea.getBBox().getHeight());
                }
            } else {
                occupiedArea.getBBox().moveDown((float) blockMinHeight - occupiedArea.getBBox().getHeight()).setHeight((float) blockMinHeight);
            }
        }

        if (isPositioned) {
            correctPositionedLayout(layoutBox);
        }

        applyBorderBox(occupiedArea.getBBox(), borders, true);
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.endMarginsCollapse();
        }
        applyMargins(occupiedArea.getBBox(), true);
        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            minWidth = occupiedArea.getBBox().getWidth();
            additionalWidth = 0;
            if (isNotFittingLayoutArea(layoutContext.getArea())) {
                if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                    return new LayoutResult(LayoutResult.NOTHING, null, null, this, this, 0, maxFullWidth);
                }
            }
        }
        applyVerticalAlignment();
        if (null == overflowRenderer) {
            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null, causeOfNothing, minWidth + additionalWidth, maxFullWidth);
        } else {
            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, this, overflowRenderer, causeOfNothing, minWidth + additionalWidth, maxFullWidth);
        }
    }

    protected AbstractRenderer createSplitRenderer(int layoutResult) {
        AbstractRenderer splitRenderer = (AbstractRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.properties = new HashMap<>(properties);
        return splitRenderer;
    }

    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
        AbstractRenderer overflowRenderer = (AbstractRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.properties = new HashMap<>(properties);
        return overflowRenderer;
    }

    @Override
    public void draw(DrawContext drawContext) {
        if (occupiedArea == null) {
            Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
            logger.error(LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED);
            return;
        }
        applyDestinationsAndAnnotation(drawContext);

        PdfDocument document = drawContext.getDocument();
        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        TagTreePointer tagPointer = null;
        IAccessibleElement accessibleElement = null;
        if (isTagged) {
            accessibleElement = (IAccessibleElement) getModelElement();
            PdfName role = accessibleElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                tagPointer = document.getTagStructureContext().getAutoTaggingPointer();
                if (!tagPointer.isElementConnectedToTag(accessibleElement)) {
                    AccessibleAttributesApplier.applyLayoutAttributes(role, this, document);

                    if (role.equals(PdfName.TD)) {
                        AccessibleAttributesApplier.applyTableAttributes(this);
                    }

                    if (role.equals(PdfName.List)) {
                        AccessibleAttributesApplier.applyListAttributes(this);
                    }

                }
                tagPointer.addTag(accessibleElement, true);
            } else {
                isTagged = false;
            }
        }

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(false);
        }

        beginRotationIfApplied(drawContext.getCanvas());

        drawBackground(drawContext);
        drawBorder(drawContext);
        drawChildren(drawContext);

        endRotationIfApplied(drawContext.getCanvas());

        if (isRelativePosition) {
            applyAbsolutePositioningTranslation(true);
        }

        if (isTagged) {
            tagPointer.moveToParent();
            if (isLastRendererForModelElement) {
                document.getTagStructureContext().removeElementConnectionToTag(accessibleElement);
            }
        }

        flushed = true;
    }

    @Override
    public Rectangle getOccupiedAreaBBox() {
        Rectangle bBox = occupiedArea.getBBox().clone();
        Float rotationAngle = this.<Float>getProperty(Property.ROTATION_ANGLE);
        if (rotationAngle != null) {
            if (!hasOwnProperty(Property.ROTATION_INITIAL_WIDTH) || !hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
                Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
                logger.error(MessageFormat.format(LogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER, getClass().getSimpleName()));
            } else {
                bBox.setWidth((float) this.getPropertyAsFloat(Property.ROTATION_INITIAL_WIDTH));
                bBox.setHeight((float) this.getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT));
            }
        }
        return bBox;
    }

    protected void applyVerticalAlignment() {
        VerticalAlignment verticalAlignment = this.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT);
        if (verticalAlignment != null && verticalAlignment != VerticalAlignment.TOP && childRenderers.size() > 0) {
            float deltaY = childRenderers.get(childRenderers.size() - 1).getOccupiedArea().getBBox().getY() - getInnerAreaBBox().getY();
            switch (verticalAlignment) {
                case BOTTOM:
                    for (IRenderer child : childRenderers) {
                        child.move(0, -deltaY);
                    }
                    break;
                case MIDDLE:
                    for (IRenderer child : childRenderers) {
                        child.move(0, -deltaY / 2);
                    }
                    break;
            }
        }
    }

    protected void applyRotationLayout(Rectangle layoutBox) {
        float angle = (float) this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        float x = occupiedArea.getBBox().getX();
        float y = occupiedArea.getBBox().getY();
        float height = occupiedArea.getBBox().getHeight();
        float width = occupiedArea.getBBox().getWidth();

        setProperty(Property.ROTATION_INITIAL_WIDTH, width);
        setProperty(Property.ROTATION_INITIAL_HEIGHT, height);

        AffineTransform rotationTransform = new AffineTransform();

        // here we calculate and set the actual occupied area of the rotated content
        if (isPositioned()) {
            Float rotationPointX = this.getPropertyAsFloat(Property.ROTATION_POINT_X);
            Float rotationPointY = this.getPropertyAsFloat(Property.ROTATION_POINT_Y);

            if (rotationPointX == null || rotationPointY == null) {
                // if rotation point was not specified, the most bottom-left point is used
                rotationPointX = x;
                rotationPointY = y;
            }

            // transforms apply from bottom to top
            rotationTransform.translate((float) rotationPointX, (float) rotationPointY); // move point back at place
            rotationTransform.rotate(angle); // rotate
            rotationTransform.translate((float) -rotationPointX, (float) -rotationPointY); // move rotation point to origin

            List<Point> rotatedPoints = transformPoints(rectangleToPointsList(occupiedArea.getBBox()), rotationTransform);
            Rectangle newBBox = calculateBBox(rotatedPoints);

            // make occupied area be of size and position of actual content
            occupiedArea.getBBox().setWidth(newBBox.getWidth());
            occupiedArea.getBBox().setHeight(newBBox.getHeight());
            float occupiedAreaShiftX = newBBox.getX() - x;
            float occupiedAreaShiftY = newBBox.getY() - y;
            move(occupiedAreaShiftX, occupiedAreaShiftY);
        } else {
            rotationTransform = AffineTransform.getRotateInstance(angle);
            List<Point> rotatedPoints = transformPoints(rectangleToPointsList(occupiedArea.getBBox()), rotationTransform);
            float[] shift = calculateShiftToPositionBBoxOfPointsAt(x, y + height, rotatedPoints);

            for (Point point : rotatedPoints) {
                point.setLocation(point.getX() + shift[0], point.getY() + shift[1]);
            }

            Rectangle newBBox = calculateBBox(rotatedPoints);

            occupiedArea.getBBox().setWidth(newBBox.getWidth());
            occupiedArea.getBBox().setHeight(newBBox.getHeight());

            float heightDiff = height - newBBox.getHeight();
            move(0, heightDiff);
        }
    }

    /**
     * @deprecated Will be removed in iText 7.1
     */
    @Deprecated
    protected float[] applyRotation() {
        float[] ctm = new float[6];
        createRotationTransformInsideOccupiedArea().getMatrix(ctm);
        return ctm;
    }

    /**
     * This method creates {@link AffineTransform} instance that could be used
     * to rotate content inside the occupied area. Be aware that it should be used only after
     * layout rendering is finished and correct occupied area for the rotated element is calculated.
     *
     * @return {@link AffineTransform} that rotates the content and places it inside occupied area.
     */
    protected AffineTransform createRotationTransformInsideOccupiedArea() {
        Float angle = this.<Float>getProperty(Property.ROTATION_ANGLE);
        AffineTransform rotationTransform = AffineTransform.getRotateInstance((float) angle);

        Rectangle contentBox = this.getOccupiedAreaBBox();
        List<Point> rotatedContentBoxPoints = transformPoints(rectangleToPointsList(contentBox), rotationTransform);
        // Occupied area for rotated elements is already calculated on layout in such way to enclose rotated content;
        // therefore we can simply rotate content as is and then shift it to the occupied area.
        float[] shift = calculateShiftToPositionBBoxOfPointsAt(occupiedArea.getBBox().getLeft(), occupiedArea.getBBox().getTop(), rotatedContentBoxPoints);
        rotationTransform.preConcatenate(AffineTransform.getTranslateInstance(shift[0], shift[1]));

        return rotationTransform;
    }

    protected void beginRotationIfApplied(PdfCanvas canvas) {
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            if (!hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
                Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
                logger.error(MessageFormat.format(LogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER, getClass().getSimpleName()));
            } else {
                AffineTransform transform = createRotationTransformInsideOccupiedArea();
                canvas.saveState().concatMatrix(transform);
            }
        }
    }

    protected void endRotationIfApplied(PdfCanvas canvas) {
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            canvas.restoreState();
        }
    }

    protected void correctPositionedLayout(Rectangle layoutBox) {
        float y = (float) this.getPropertyAsFloat(Property.Y);
        float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
        move(0, relativeY + y - occupiedArea.getBBox().getY());
    }

    private List<Point> clipPolygon(List<Point> points, Point clipLineBeg, Point clipLineEnd) {
        List<Point> filteredPoints = new ArrayList<>();

        boolean prevOnRightSide = false;
        Point filteringPoint = points.get(0);
        if (checkPointSide(filteringPoint, clipLineBeg, clipLineEnd) >= 0) {
            filteredPoints.add(filteringPoint);
            prevOnRightSide = true;
        }

        Point prevPoint = filteringPoint;
        for (int i = 1; i < points.size() + 1; ++i) {
            filteringPoint = points.get(i % points.size());
            if (checkPointSide(filteringPoint, clipLineBeg, clipLineEnd) >= 0) {
                if (!prevOnRightSide) {
                    filteredPoints.add(getIntersectionPoint(prevPoint, filteringPoint, clipLineBeg, clipLineEnd));
                }
                filteredPoints.add(filteringPoint);
                prevOnRightSide = true;
            } else if (prevOnRightSide) {
                filteredPoints.add(getIntersectionPoint(prevPoint, filteringPoint, clipLineBeg, clipLineEnd));
            }

            prevPoint = filteringPoint;
        }

        return filteredPoints;
    }

    private int checkPointSide(Point filteredPoint, Point clipLineBeg, Point clipLineEnd) {
        double x1, x2, y1, y2;
        x1 = filteredPoint.getX() - clipLineBeg.getX();
        y2 = clipLineEnd.getY() - clipLineBeg.getY();

        x2 = clipLineEnd.getX() - clipLineBeg.getX();
        y1 = filteredPoint.getY() - clipLineBeg.getY();

        double sgn = x1 * y2 - x2 * y1;

        if (Math.abs(sgn) < 0.001) return 0;
        if (sgn > 0) return 1;
        if (sgn < 0) return -1;

        return 0;
    }

    private Point getIntersectionPoint(Point lineBeg, Point lineEnd, Point clipLineBeg, Point clipLineEnd) {
        double A1 = lineBeg.getY() - lineEnd.getY(), A2 = clipLineBeg.getY() - clipLineEnd.getY();
        double B1 = lineEnd.getX() - lineBeg.getX(), B2 = clipLineEnd.getX() - clipLineBeg.getX();
        double C1 = lineBeg.getX() * lineEnd.getY() - lineBeg.getY() * lineEnd.getX();
        double C2 = clipLineBeg.getX() * clipLineEnd.getY() - clipLineBeg.getY() * clipLineEnd.getX();

        double M = B1 * A2 - B2 * A1;

        return new Point((B2 * C1 - B1 * C2) / M, (C2 * A1 - C1 * A2) / M);
    }
}
