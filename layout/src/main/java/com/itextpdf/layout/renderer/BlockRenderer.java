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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.MinMaxWidthLayoutResult;
import com.itextpdf.layout.layout.PositionedLayoutContext;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.ClearPropertyValue;
import com.itextpdf.layout.properties.ContinuousContainer;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a renderer for block elements.
 */
public abstract class BlockRenderer extends AbstractRenderer {

    /**
     * Creates a BlockRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link IElement} which this object should manage
     */
    protected BlockRenderer(IElement modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        this.isLastRendererForModelElement = true;
        Map<Integer, IRenderer> waitingFloatsSplitRenderers = new LinkedHashMap<>();
        List<IRenderer> waitingOverflowFloatRenderers = new ArrayList<>();
        boolean floatOverflowedCompletely = false;
        boolean wasHeightClipped = false;
        boolean wasParentsHeightClipped = layoutContext.isClippedHeight();
        int pageNumber = layoutContext.getArea().getPageNumber();

        boolean isPositioned = isPositioned();

        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();

        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);
        Float rotation = this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        OverflowPropertyValue overflowX = this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);

        MarginsCollapseHandler marginsCollapseHandler = null;
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler = new MarginsCollapseHandler(this, layoutContext.getMarginsCollapseInfo());
        }
        Float blockWidth = retrieveWidth(parentBBox.getWidth());
        if (rotation != null || isFixedLayout()) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }
        if (rotation != null && !FloatingHelper.isRendererFloating(this, floatPropertyValue) &&
                !(this instanceof FlexContainerRenderer)) {
            blockWidth = RotationUtils.retrieveRotatedLayoutWidth(parentBBox.getWidth(), this);
        }
        boolean includeFloatsInOccupiedArea = BlockFormattingContextUtil.isRendererCreateBfc(this);
        float clearHeightCorrection = FloatingHelper.calculateClearHeightCorrection(this, floatRendererAreas,
                parentBBox);
        FloatingHelper.applyClearance(parentBBox, marginsCollapseHandler, clearHeightCorrection,
                FloatingHelper.isRendererFloating(this));
        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            blockWidth = FloatingHelper.adjustFloatedBlockLayoutBox(this, parentBBox, blockWidth, floatRendererAreas,
                    floatPropertyValue, overflowX);
            floatRendererAreas = new ArrayList<>();
        }
        boolean wasHeightDecreased = clearHeightCorrection > 0 &&
                (marginsCollapseHandler == null || FloatingHelper.isRendererFloating(this));
        float bfcHeightCorrection = FloatingHelper.adjustBlockFormattingContextLayoutBox(this, floatRendererAreas,
                parentBBox,
                blockWidth == null ? 0 : (float) blockWidth, wasHeightDecreased ? 0 : clearHeightCorrection);
        boolean isCellRenderer = this instanceof CellRenderer;
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.startMarginsCollapse(parentBBox);
        }

        ContinuousContainer.setupContinuousContainerIfNeeded(this);

        Border[] borders = getBorders();
        UnitValue[] paddings = getPaddings();

        applyMargins(parentBBox, false);
        applyBorderBox(parentBBox, borders, false);
        if (isFixedLayout()) {
            parentBBox.setX((float) this.getPropertyAsFloat(Property.LEFT));
        }
        applyPaddings(parentBBox, paddings, false);
        Float blockMaxHeight = retrieveMaxHeight();
        OverflowPropertyValue overflowY = (null == blockMaxHeight || blockMaxHeight > parentBBox.getHeight())
                && !wasParentsHeightClipped
                ? OverflowPropertyValue.FIT
                : this.<OverflowPropertyValue>getProperty(Property.OVERFLOW_Y);
        applyWidth(parentBBox, blockWidth, overflowX);
        wasHeightClipped = applyMaxHeight(parentBBox, blockMaxHeight, marginsCollapseHandler, isCellRenderer, wasParentsHeightClipped, overflowY);

        List<Rectangle> areas;
        if (isPositioned) {
            areas = Collections.singletonList(parentBBox);
        } else {
            areas = initElementAreas(new LayoutArea(pageNumber, parentBBox));
        }

        occupiedArea = new LayoutArea(pageNumber, new Rectangle(parentBBox.getX(), parentBBox.getY() + parentBBox.getHeight(), parentBBox.getWidth(), 0));
        shrinkOccupiedAreaForAbsolutePosition();

        TargetCounterHandler.addPageByID(this);

        int currentAreaPos = 0;

        Rectangle layoutBox = areas.get(0).clone();

        // rectangles are compared by instances
        Set<Rectangle> nonChildFloatingRendererAreas = new HashSet<>(floatRendererAreas);

        // the first renderer (one of childRenderers or their children) to produce LayoutResult.NOTHING
        IRenderer causeOfNothing = null;
        boolean anythingPlaced = false;
        // We have to remember initial FORCED_PLACEMENT property of this renderer to use it later
        // to define if rotated content should be placed or not
        final boolean initialForcePlacementForRotationAdjustments =
                Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));
        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult result;
            childRenderer.setParent(this);
            MarginsCollapseInfo childMarginsInfo = null;

            if (floatOverflowedCompletely && FloatingHelper.isRendererFloating(childRenderer)) {
                waitingFloatsSplitRenderers.put(childPos, null);
                waitingOverflowFloatRenderers.add(childRenderer);
                continue;
            }

            if (!waitingOverflowFloatRenderers.isEmpty() && FloatingHelper.isClearanceApplied(waitingOverflowFloatRenderers, childRenderer.<ClearPropertyValue>getProperty(Property.CLEAR))) {
                if (FloatingHelper.isRendererFloating(childRenderer)) {
                    waitingFloatsSplitRenderers.put(childPos, null);
                    waitingOverflowFloatRenderers.add(childRenderer);
                    floatOverflowedCompletely = true;
                    continue;
                }
                if (marginsCollapsingEnabled && !isCellRenderer) {
                    marginsCollapseHandler.endMarginsCollapse(layoutBox);
                }

                FloatingHelper.includeChildFloatsInOccupiedArea(floatRendererAreas, this, nonChildFloatingRendererAreas);
                fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);

                result = new LayoutResult(LayoutResult.NOTHING, null, null, childRenderer);
                boolean isKeepTogether = isKeepTogether(childRenderer);
                int layoutResult = anythingPlaced && !isKeepTogether ? LayoutResult.PARTIAL : LayoutResult.NOTHING;
                AbstractRenderer[] splitAndOverflowRenderers = createSplitAndOverflowRenderers(childPos, layoutResult, result, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers);

                AbstractRenderer splitRenderer = splitAndOverflowRenderers[0];
                AbstractRenderer overflowRenderer = splitAndOverflowRenderers[1];

                if (isKeepTogether) {
                    splitRenderer = null;
                    overflowRenderer.childRenderers.clear();
                    overflowRenderer.childRenderers = new ArrayList<>(childRenderers);
                }

                updateHeightsOnSplit(wasHeightClipped, splitRenderer, overflowRenderer);
                applyPaddings(occupiedArea.getBBox(), paddings, true);
                applyBorderBox(occupiedArea.getBBox(), borders, true);
                applyMargins(occupiedArea.getBBox(), true);

                if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) || wasHeightClipped) {
                    LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, bfcHeightCorrection, marginsCollapsingEnabled);
                    return new LayoutResult(LayoutResult.FULL, editedArea, splitRenderer, null, null);
                } else {
                    if (layoutResult != LayoutResult.NOTHING) {
                        LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, bfcHeightCorrection, marginsCollapsingEnabled);
                        return new LayoutResult(layoutResult, editedArea, splitRenderer, overflowRenderer, null).setAreaBreak(result.getAreaBreak());
                    } else {
                        floatRendererAreas.retainAll(nonChildFloatingRendererAreas);
                        return new LayoutResult(layoutResult, null, null, overflowRenderer, result.getCauseOfNothing()).setAreaBreak(result.getAreaBreak());
                    }
                }
            }

            if (marginsCollapsingEnabled) {
                childMarginsInfo = startChildMarginsHandling(childRenderer, layoutBox, marginsCollapseHandler);
            }
            Rectangle changedLayoutBox =
                    recalculateLayoutBoxBeforeChildLayout(layoutBox, childRenderer, areas.get(0).clone());
            while ((result = childRenderer.setParent(this).layout(new LayoutContext(
                    new LayoutArea(pageNumber, changedLayoutBox),
                    childMarginsInfo,
                    floatRendererAreas,
                    wasHeightClipped || wasParentsHeightClipped)))
                    .getStatus() != LayoutResult.FULL) {

                if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA_ON_SPLIT))
                        || Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) {
                    occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), layoutBox));
                } else if (result.getOccupiedArea() != null && result.getStatus() != LayoutResult.NOTHING) {
                    recalculateOccupiedAreaAfterChildLayout(result.getOccupiedArea().getBBox(), blockMaxHeight);
                    fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);
                }

                if (marginsCollapsingEnabled && result.getStatus() != LayoutResult.NOTHING) {
                    marginsCollapseHandler.endChildMarginsHandling(layoutBox);
                }

                if (FloatingHelper.isRendererFloating(childRenderer)) {
                    // Check if current block is empty, kid returns nothing and neither floats nor content
                    // were met on root area (e.g. page area) - return NOTHING, don't layout other kids,
                    // expect FORCED_PLACEMENT to be set.
                    boolean immediatelyReturnNothing = result.getStatus() == LayoutResult.NOTHING
                            && !anythingPlaced
                            && floatRendererAreas.isEmpty()
                            && isFirstOnRootArea();
                    if (!immediatelyReturnNothing) {
                        waitingFloatsSplitRenderers.put(childPos, result.getStatus() == LayoutResult.PARTIAL ? result.getSplitRenderer() : null);
                        waitingOverflowFloatRenderers.add(result.getOverflowRenderer());
                        floatOverflowedCompletely = result.getStatus() == LayoutResult.NOTHING;
                        break;
                    }
                }

                if (marginsCollapsingEnabled) {
                    marginsCollapseHandler.endMarginsCollapse(layoutBox);
                }

                // On page split, content will be drawn on next page, i.e. under all floats on this page
                FloatingHelper.includeChildFloatsInOccupiedArea(floatRendererAreas, this, nonChildFloatingRendererAreas);
                fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);

                if (result.getSplitRenderer() != null) {
                    // TODO DEVSIX-6488 all elements should be layouted first in case when parent box should wrap around child boxes
                    alignChildHorizontally(result.getSplitRenderer(), occupiedArea.getBBox());
                }

                // Save the first renderer to produce LayoutResult.NOTHING
                if (null == causeOfNothing && null != result.getCauseOfNothing()) {
                    causeOfNothing = result.getCauseOfNothing();
                }

                // have more areas
                if (currentAreaPos + 1 < areas.size() && !(result.getAreaBreak() != null && result.getAreaBreak().getType() == AreaBreakType.NEXT_PAGE)) {
                    if (result.getStatus() == LayoutResult.PARTIAL) {
                        childRenderers.set(childPos, result.getSplitRenderer());
                        childRenderers.add(childPos + 1, result.getOverflowRenderer());
                    } else {
                        if (result.getOverflowRenderer() != null) {
                            childRenderers.set(childPos, result.getOverflowRenderer());
                        } else {
                            childRenderers.remove(childPos);
                        }
                        childPos--;
                    }
                    layoutBox = areas.get(++currentAreaPos).clone();
                    break;
                } else {
                    final LayoutResult layoutResult = processNotFullChildResult(
                            layoutContext, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers, wasHeightClipped,
                            floatRendererAreas, marginsCollapsingEnabled, clearHeightCorrection, borders, paddings,
                            areas, currentAreaPos, layoutBox, nonChildFloatingRendererAreas, causeOfNothing,
                            anythingPlaced, childPos, result);
                    if (layoutResult == null) {
                        layoutBox = areas.get(++currentAreaPos).clone();
                        break;
                    }
                    if (stopLayoutingChildrenIfChildResultNotFull(layoutResult)) {
                        return layoutResult;
                    }
                    result = layoutResult;
                    break;
                }
            }
            anythingPlaced = anythingPlaced || result.getStatus() != LayoutResult.NOTHING;
            handleForcedPlacement(anythingPlaced);

            // The second condition check (after &&) is needed only if margins collapsing is enabled
            if (result.getOccupiedArea() != null && (!FloatingHelper.isRendererFloating(childRenderer) || includeFloatsInOccupiedArea)) {
                recalculateOccupiedAreaAfterChildLayout(result.getOccupiedArea().getBBox(), blockMaxHeight);
                fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);
            }
            if (marginsCollapsingEnabled) {
                marginsCollapseHandler.endChildMarginsHandling(layoutBox);
            }
            if (result.getStatus() == LayoutResult.FULL) {
                decreaseLayoutBoxAfterChildPlacement(layoutBox, result, childRenderer);
                if (childRenderer.getOccupiedArea() != null) {
                    // TODO DEVSIX-6488 all elements should be layouted first in case when parent box should wrap around child boxes
                    alignChildHorizontally(childRenderer, occupiedArea.getBBox());
                }
            }

            // Save the first renderer to produce LayoutResult.NOTHING
            if (null == causeOfNothing && null != result.getCauseOfNothing()) {
                causeOfNothing = result.getCauseOfNothing();
            }
        }



        if (includeFloatsInOccupiedArea) {
            FloatingHelper.includeChildFloatsInOccupiedArea(floatRendererAreas, this, nonChildFloatingRendererAreas);
            fixOccupiedAreaIfOverflowedX(overflowX, layoutBox);
        }
        if (wasHeightClipped) {
            fixOccupiedAreaIfOverflowedY(overflowY, layoutBox);
        }
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.endMarginsCollapse(layoutBox);
        }

        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) {
            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), layoutBox));
        }

        int layoutResult = LayoutResult.FULL;
        boolean processOverflowedFloats = !waitingOverflowFloatRenderers.isEmpty() && !wasHeightClipped &&
                !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));

        AbstractRenderer overflowRenderer = null;
        if (!includeFloatsInOccupiedArea || !processOverflowedFloats) {
            overflowRenderer = applyMinHeight(overflowY, layoutBox);
        }

        boolean minHeightOverflow = overflowRenderer != null;
        if (minHeightOverflow && isKeepTogether()) {
            floatRendererAreas.retainAll(nonChildFloatingRendererAreas);
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
        }

        // in this case layout result need to be changed
        if (overflowRenderer != null || processOverflowedFloats) {
            layoutResult = !anythingPlaced && (!waitingOverflowFloatRenderers.isEmpty()
                    || !isAnythingOccupied())
                    // nothing was placed and there are some overflowed floats
                    ? LayoutResult.NOTHING
                    // either something was placed or (since there are no overflowed floats) there is overflow renderer
                    // that indicates overflowed min_height
                    : LayoutResult.PARTIAL;
        }
        if (processOverflowedFloats) {
            if (overflowRenderer == null || layoutResult == LayoutResult.NOTHING) {
                // if layout result is NOTHING - avoid possible usage of the overflowRenderer created
                // for overflow of min_height with adjusted height properties
                overflowRenderer = createOverflowRenderer(layoutResult);
            }
            overflowRenderer.getChildRenderers().addAll(waitingOverflowFloatRenderers);
            if (layoutResult == LayoutResult.PARTIAL && !minHeightOverflow && !includeFloatsInOccupiedArea) {
                FloatingHelper.removeParentArtifactsOnPageSplitIfOnlyFloatsOverflow(overflowRenderer);
            }
        }
        AbstractRenderer splitRenderer = this;
        if (waitingFloatsSplitRenderers.size() > 0 && layoutResult != LayoutResult.NOTHING) {
            splitRenderer = createSplitRenderer(layoutResult);
            splitRenderer.childRenderers = new ArrayList<>(childRenderers);
            replaceSplitRendererKidFloats(waitingFloatsSplitRenderers, splitRenderer);

            float usedHeight = occupiedArea.getBBox().getHeight();
            if (!includeFloatsInOccupiedArea) {
                Rectangle commonRectangle = Rectangle.getCommonRectangle(layoutBox, occupiedArea.getBBox());
                usedHeight = commonRectangle.getHeight();
            }
            // this must be processed before margin/border/padding
            updateHeightsOnSplit(usedHeight, wasHeightClipped, splitRenderer, overflowRenderer, includeFloatsInOccupiedArea);
        }

        if (positionedRenderers.size() > 0) {
            for (IRenderer childPositionedRenderer : positionedRenderers) {
                Rectangle fullBbox = occupiedArea.getBBox().clone();

                // Use that value so that layout is independent of whether we are in the bottom of the page or in the
                // top of the page
                float layoutMinHeight = 1000;
                fullBbox.moveDown(layoutMinHeight).setHeight(layoutMinHeight + fullBbox.getHeight());
                LayoutArea parentArea = new LayoutArea(occupiedArea.getPageNumber(), occupiedArea.getBBox().clone());
                applyPaddings(parentArea.getBBox(), paddings, true);

                preparePositionedRendererAndAreaForLayout(childPositionedRenderer, fullBbox, parentArea.getBBox());
                childPositionedRenderer.layout(
                        new PositionedLayoutContext(new LayoutArea(occupiedArea.getPageNumber(), fullBbox),
                                parentArea));
            }
        }

        if (isPositioned) {
            correctFixedLayout(layoutBox);
        }
        final ContinuousContainer continuousContainer = this.<ContinuousContainer>getProperty(
                Property.TREAT_AS_CONTINUOUS_CONTAINER_RESULT);
        if (continuousContainer != null && overflowRenderer == null) {
            continuousContainer.reApplyProperties(this);
            paddings = getPaddings();
            borders = getBorders();
        }

        applyPaddings(occupiedArea.getBBox(), paddings, true);
        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), true);

        applyAbsolutePositionIfNeeded(layoutContext);

        if (rotation != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            if (isNotFittingLayoutArea(layoutContext.getArea())) {
                if (isNotFittingWidth(layoutContext.getArea()) && !isNotFittingHeight(layoutContext.getArea())) {
                    LoggerFactory.getLogger(getClass())
                            .warn(MessageFormatUtil.format(LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA,
                                    "It fits by height so it will be forced placed"));
                } else if (!initialForcePlacementForRotationAdjustments) {
                    floatRendererAreas.retainAll(nonChildFloatingRendererAreas);
                    return new MinMaxWidthLayoutResult(LayoutResult.NOTHING, null, null, this, this);
                }
            }
        }
        applyVerticalAlignment();

        FloatingHelper.removeFloatsAboveRendererBottom(floatRendererAreas, this);

        if (layoutResult != LayoutResult.NOTHING) {
            ContinuousContainer.clearPropertiesFromOverFlowRenderer(overflowRenderer);
            LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this,
                    layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection,
                    bfcHeightCorrection, marginsCollapsingEnabled);
            return new LayoutResult(layoutResult, editedArea, splitRenderer, overflowRenderer, causeOfNothing);
        } else {
            if (positionedRenderers.size() > 0) {
                overflowRenderer.positionedRenderers = new ArrayList<>(positionedRenderers);
            }
            floatRendererAreas.retainAll(nonChildFloatingRendererAreas);

            return new LayoutResult(LayoutResult.NOTHING, null, null, overflowRenderer, causeOfNothing);
        }
    }

    @Override
    public void draw(DrawContext drawContext) {
        Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
        if (occupiedArea == null) {
            logger.error(MessageFormatUtil.format(IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED,
                    "Drawing won't be performed."));
            return;
        }

        boolean isTagged = drawContext.isTaggingEnabled();
        LayoutTaggingHelper taggingHelper = null;
        if (isTagged) {
            taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper == null) {
                isTagged = false;
            } else {
                TagTreePointer tagPointer = taggingHelper.useAutoTaggingPointerAndRememberItsPosition(this);
                if (taggingHelper.createTag(this, tagPointer)) {
                    tagPointer.getProperties()
                            .addAttributes(0, AccessibleAttributesApplier.getListAttributes(this, tagPointer))
                            .addAttributes(0, AccessibleAttributesApplier.getTableAttributes(this, tagPointer))
                            .addAttributes(0, AccessibleAttributesApplier.getLayoutAttributes(this, tagPointer));
                }
            }
        }

        beginTransformationIfApplied(drawContext.getCanvas());
        applyDestinationsAndAnnotation(drawContext);

        boolean isRelativePosition = isRelativePosition();
        if (isRelativePosition) {
            applyRelativePositioningTranslation(false);
        }

        beginElementOpacityApplying(drawContext);
        beginRotationIfApplied(drawContext.getCanvas());

        boolean overflowXHidden = isOverflowProperty(OverflowPropertyValue.HIDDEN, Property.OVERFLOW_X);
        boolean overflowYHidden = isOverflowProperty(OverflowPropertyValue.HIDDEN, Property.OVERFLOW_Y);
        boolean processOverflow = overflowXHidden || overflowYHidden;

        drawBackground(drawContext);
        drawBorder(drawContext);

        addMarkedContent(drawContext, true);
        if (processOverflow) {
            drawContext.getCanvas().saveState();
            int pageNumber = occupiedArea.getPageNumber();
            Rectangle clippedArea;
            if (pageNumber < 1 || pageNumber > drawContext.getDocument().getNumberOfPages()) {
                clippedArea = new Rectangle(-INF / 2 , -INF / 2, INF, INF);
            } else {
                PdfPage page = drawContext.getDocument().getPage(pageNumber);
                // TODO DEVSIX-1655 This check is necessary because, in some cases, our renderer's hierarchy may contain
                //  a renderer from the different page that was already flushed
                if (page.isFlushed()) {
                    logger.error(MessageFormatUtil.format(
                            IoLogMessageConstant.PAGE_WAS_FLUSHED_ACTION_WILL_NOT_BE_PERFORMED,
                            "area clipping"));
                    clippedArea = new Rectangle(-INF / 2 , -INF / 2, INF, INF);
                } else {
                    clippedArea = page.getPageSize();
                }
            }
            Rectangle area = getBorderAreaBBox();
            if (overflowXHidden) {
                clippedArea.setX(area.getX()).setWidth(area.getWidth());
            }
            if (overflowYHidden) {
                clippedArea.setY(area.getY()).setHeight(area.getHeight());
            }
            drawContext.getCanvas().rectangle(clippedArea).clip().endPath();
        }

        drawChildren(drawContext);
        addMarkedContent(drawContext, false);
        drawPositionedChildren(drawContext);

        if (processOverflow) {
            drawContext.getCanvas().restoreState();
        }

        endRotationIfApplied(drawContext.getCanvas());
        endElementOpacityApplying(drawContext);

        if (isRelativePosition) {
            applyRelativePositioningTranslation(true);
        }

        if (isTagged) {
            if (isLastRendererForModelElement) {
                taggingHelper.finishTaggingHint(this);
            }
            taggingHelper.restoreAutoTaggingPointerPosition(this);
        }

        flushed = true;
        endTransformationIfApplied(drawContext.getCanvas());
    }

    @Override
    public Rectangle getOccupiedAreaBBox() {
        Rectangle bBox = occupiedArea.getBBox().clone();
        Float rotationAngle = this.<Float>getProperty(Property.ROTATION_ANGLE);
        if (rotationAngle != null) {
            if (!hasOwnProperty(Property.ROTATION_INITIAL_WIDTH) || !hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
                Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
                logger.error(
                        MessageFormatUtil.format(IoLogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER,
                                getClass().getSimpleName()));
            } else {
                bBox.setWidth((float) this.getPropertyAsFloat(Property.ROTATION_INITIAL_WIDTH));
                bBox.setHeight((float) this.getPropertyAsFloat(Property.ROTATION_INITIAL_HEIGHT));
            }
        }
        return bBox;
    }

    /**
     * Creates a split renderer.
     *
     * @param layoutResult the result of content layouting
     *
     * @return a new {@link AbstractRenderer} instance
     */
    protected AbstractRenderer createSplitRenderer(int layoutResult) {
        AbstractRenderer splitRenderer = (AbstractRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.addAllProperties(getOwnProperties());
        return splitRenderer;
    }

    /**
     * Creates an overflow renderer.
     *
     * @param layoutResult the result of content layouting
     *
     * @return a new {@link AbstractRenderer} instance
     */
    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
        AbstractRenderer overflowRenderer = (AbstractRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        return overflowRenderer;
    }

    void recalculateOccupiedAreaAfterChildLayout(Rectangle resultBBox, Float blockMaxHeight) {
        occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), resultBBox));
    }
    
    MarginsCollapseInfo startChildMarginsHandling(IRenderer childRenderer,
                                                  Rectangle layoutBox, MarginsCollapseHandler marginsCollapseHandler) {
        return marginsCollapseHandler.startChildMarginsHandling(childRenderer, layoutBox);
    }

    Rectangle recalculateLayoutBoxBeforeChildLayout(Rectangle layoutBox,
                                                    IRenderer childRenderer, Rectangle initialLayoutBox) {
        return layoutBox;
    }

    AbstractRenderer[] createSplitAndOverflowRenderers(int childPos, int layoutStatus, LayoutResult childResult,
                                                       Map<Integer, IRenderer> waitingFloatsSplitRenderers,
                                                       List<IRenderer> waitingOverflowFloatRenderers) {
        AbstractRenderer splitRenderer = createSplitRenderer(layoutStatus);
        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
        if (childResult.getStatus() == LayoutResult.PARTIAL && childResult.getSplitRenderer() != null) {
            splitRenderer.childRenderers.add(childResult.getSplitRenderer());
        }

        replaceSplitRendererKidFloats(waitingFloatsSplitRenderers, splitRenderer);
        for (IRenderer renderer : splitRenderer.childRenderers) {
            renderer.setParent(splitRenderer);
        }

        AbstractRenderer overflowRenderer = createOverflowRenderer(layoutStatus);
        overflowRenderer.childRenderers.addAll(waitingOverflowFloatRenderers);
        if (childResult.getOverflowRenderer() != null) {
            overflowRenderer.addChildRenderer(childResult.getOverflowRenderer());
        }
        overflowRenderer.childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));

        if (layoutStatus != LayoutResult.NOTHING) {
            ContinuousContainer.clearPropertiesFromOverFlowRenderer(overflowRenderer);
        }

        if (childResult.getStatus() == LayoutResult.PARTIAL) {
            // Apply forced placement only on split renderer
            overflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);
        }

        return new AbstractRenderer[] {splitRenderer, overflowRenderer};
    }

    /**
     * This method applies vertical alignment for the occupied area
     * of the renderer and its children renderers.
     */
    protected void applyVerticalAlignment() {
        VerticalAlignment verticalAlignment = this.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT);
        if (verticalAlignment == null || verticalAlignment == VerticalAlignment.TOP || childRenderers.isEmpty()) {
            return;
        }

        float lowestChildBottom = Float.MAX_VALUE;
        if (FloatingHelper.isRendererFloating(this) || this instanceof CellRenderer) {
            // include floats in vertical alignment
            for (IRenderer child : childRenderers) {
                if (child.getOccupiedArea() != null &&
                        child.getOccupiedArea().getBBox().getBottom() < lowestChildBottom) {
                    lowestChildBottom = child.getOccupiedArea().getBBox().getBottom();
                }
            }
        } else {
            int lastChildIndex = childRenderers.size() - 1;
            while (lastChildIndex >= 0) {
                IRenderer child = childRenderers.get(lastChildIndex--);
                if (!FloatingHelper.isRendererFloating(child) && child.getOccupiedArea() != null) {
                    lowestChildBottom = child.getOccupiedArea().getBBox().getBottom();
                    break;
                }
            }
        }

        if (lowestChildBottom == Float.MAX_VALUE) {
            return;
        }

        float deltaY = lowestChildBottom - getInnerAreaBBox().getY();
        if (deltaY < 0) {
            return;
        }
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

    /**
     * This method rotates content of the renderer and
     * calculates correct occupied area for the rotated element.
     *
     * @param layoutBox a {@link Rectangle}
     */
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
            // move point back at place
            rotationTransform.translate((float) rotationPointX, (float) rotationPointY);

            // rotate
            rotationTransform.rotate(angle);

            // move rotation point to origin
            rotationTransform.translate((float) -rotationPointX, (float) -rotationPointY);

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

    /**
     * This method starts rotation for the renderer if rotation angle property is specified.
     *
     * @param canvas the {@link PdfCanvas} to draw on
     */
    protected void beginRotationIfApplied(PdfCanvas canvas) {
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null) {
            if (!hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
                Logger logger = LoggerFactory.getLogger(BlockRenderer.class);
                logger.error(
                        MessageFormatUtil.format(IoLogMessageConstant.ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER,
                                getClass().getSimpleName()));
            } else {
                AffineTransform transform = createRotationTransformInsideOccupiedArea();
                canvas.saveState().concatMatrix(transform);
            }
        }
    }

    /**
     * This method ends rotation for the renderer if applied.
     *
     * @param canvas the {@link PdfCanvas} to draw on
     */
    protected void endRotationIfApplied(PdfCanvas canvas) {
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        if (angle != null && hasOwnProperty(Property.ROTATION_INITIAL_HEIGHT)) {
            canvas.restoreState();
        }
    }

    /**
     * Get the font set in properties, if it is not set, then resolves the first {@link PdfFont} from
     * {@link FontProvider}.
     * If {@link FontProvider} is not set, then returns null.
     *
     * @param pdfDocument the {@link PdfDocument} to get default font from.
     *
     * @return the font or null if it is not set and {@link FontProvider} is not set.
     */
    protected PdfFont getResolvedFont(PdfDocument pdfDocument) {
        final Object retrievedFont = this.<Object>getProperty(Property.FONT);
        if (retrievedFont instanceof PdfFont) {
            return (PdfFont) retrievedFont;
        }
        if (this.<FontProvider>getProperty(Property.FONT_PROVIDER) != null && retrievedFont != null) {
            return resolveFirstPdfFont();
        }
        if (pdfDocument != null) {
            return pdfDocument.getDefaultFont();
        }
        return null;
    }

    boolean stopLayoutingChildrenIfChildResultNotFull(LayoutResult returnResult) {
        return true;
    }

    LayoutResult processNotFullChildResult(LayoutContext layoutContext,
                                           Map<Integer, IRenderer> waitingFloatsSplitRenderers,
                                           List<IRenderer> waitingOverflowFloatRenderers, boolean wasHeightClipped,
                                           List<Rectangle> floatRendererAreas, boolean marginsCollapsingEnabled,
                                           float clearHeightCorrection, Border[] borders, UnitValue[] paddings,
                                           List<Rectangle> areas, int currentAreaPos, Rectangle layoutBox,
                                           Set<Rectangle> nonChildFloatingRendererAreas, IRenderer causeOfNothing,
                                           boolean anythingPlaced, int childPos, LayoutResult result) {
        if (result.getStatus() == LayoutResult.PARTIAL) {
            if (currentAreaPos + 1 == areas.size()) {
                AbstractRenderer[] splitAndOverflowRenderers = createSplitAndOverflowRenderers(childPos,
                        LayoutResult.PARTIAL, result, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers);

                AbstractRenderer splitRenderer = splitAndOverflowRenderers[0];
                AbstractRenderer overflowRenderer = splitAndOverflowRenderers[1];
                overflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);

                updateHeightsOnSplit(wasHeightClipped, splitRenderer, overflowRenderer);
                applyPaddings(occupiedArea.getBBox(), paddings, true);
                applyBorderBox(occupiedArea.getBBox(), borders, true);
                applyMargins(occupiedArea.getBBox(), true);

                correctFixedLayout(layoutBox);

                LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                if (wasHeightClipped) {
                    return new LayoutResult(LayoutResult.FULL, editedArea, splitRenderer, null);
                } else {
                    return new LayoutResult(LayoutResult.PARTIAL, editedArea, splitRenderer, overflowRenderer, causeOfNothing);
                }
            } else {
                childRenderers.set(childPos, result.getSplitRenderer());
                childRenderers.add(childPos + 1, result.getOverflowRenderer());
                return null;
            }
        } else if (result.getStatus() == LayoutResult.NOTHING) {
            boolean keepTogether = isKeepTogether(causeOfNothing);
            int layoutResult = anythingPlaced && !keepTogether ? LayoutResult.PARTIAL : LayoutResult.NOTHING;

            AbstractRenderer[] splitAndOverflowRenderers = createSplitAndOverflowRenderers(childPos, layoutResult,
                    result, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers);

            AbstractRenderer splitRenderer = splitAndOverflowRenderers[0];
            AbstractRenderer overflowRenderer = splitAndOverflowRenderers[1];

            if (isRelativePosition() && positionedRenderers.size() > 0) {
                overflowRenderer.positionedRenderers = new ArrayList<>(positionedRenderers);
            }

            updateHeightsOnSplit(wasHeightClipped, splitRenderer, overflowRenderer);

            if (keepTogether) {
                splitRenderer = null;
                overflowRenderer.childRenderers.clear();
                overflowRenderer.addAllChildRenderers(childRenderers);
            }

            correctFixedLayout(layoutBox);

            applyPaddings(occupiedArea.getBBox(), paddings, true);
            applyBorderBox(occupiedArea.getBBox(), borders, true);
            applyMargins(occupiedArea.getBBox(), true);

            applyAbsolutePositionIfNeeded(layoutContext);

            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) || wasHeightClipped) {
                LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                return new LayoutResult(LayoutResult.FULL, editedArea, splitRenderer, null, null);
            } else {
                if (layoutResult != LayoutResult.NOTHING) {
                    LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, layoutContext.getFloatRendererAreas(), layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                    return new LayoutResult(layoutResult, editedArea, splitRenderer, overflowRenderer, null).setAreaBreak(result.getAreaBreak());
                } else {
                    floatRendererAreas.retainAll(nonChildFloatingRendererAreas);
                    return new LayoutResult(layoutResult, null, null, overflowRenderer, result.getCauseOfNothing()).setAreaBreak(result.getAreaBreak());
                }
            }
        }
        return null;
    }

    void decreaseLayoutBoxAfterChildPlacement(Rectangle layoutBox, LayoutResult result, IRenderer childRenderer) {
        layoutBox.setHeight(result.getOccupiedArea().getBBox().getY() - layoutBox.getY());
    }

    void correctFixedLayout(Rectangle layoutBox) {
        if (isFixedLayout()) {
            float y = (float) this.getPropertyAsFloat(Property.BOTTOM);
            move(0, y - occupiedArea.getBBox().getY());
        }
    }

    void applyWidth(Rectangle parentBBox, Float blockWidth, OverflowPropertyValue overflowX) {
        // maxWidth has already taken in attention in blockWidth,
        // therefore only `parentBBox > minWidth` needs to be checked.
        Float rotation = this.getPropertyAsFloat(Property.ROTATION_ANGLE);

        if (blockWidth != null && (
                        blockWidth < parentBBox.getWidth() ||
                        isPositioned() ||
                        rotation != null ||
                        (!isOverflowFit(overflowX)))) {
            parentBBox.setWidth((float) blockWidth);
        } else {
            Float minWidth = retrieveMinWidth(parentBBox.getWidth());
            //Shall we check overflow-x here?
            if (minWidth != null && minWidth > parentBBox.getWidth()) {
                parentBBox.setWidth((float) minWidth);
            }
        }
    }

    boolean applyMaxHeight(Rectangle parentBBox, Float blockMaxHeight, MarginsCollapseHandler marginsCollapseHandler,
                           boolean isCellRenderer, boolean wasParentsHeightClipped, OverflowPropertyValue overflowY) {
        if (null == blockMaxHeight || (blockMaxHeight >= parentBBox.getHeight() && (isOverflowFit(overflowY)))) {
            return false;
        }
        boolean wasHeightClipped = false;
        if (blockMaxHeight <= parentBBox.getHeight()) {
            wasHeightClipped = true;
        }
        float heightDelta = parentBBox.getHeight() - (float) blockMaxHeight;
        if (marginsCollapseHandler != null && !isCellRenderer) {
            marginsCollapseHandler.processFixedHeightAdjustment(heightDelta);
        }
        parentBBox.moveUp(heightDelta).setHeight((float) blockMaxHeight);
        return wasHeightClipped;
    }

    AbstractRenderer applyMinHeight(OverflowPropertyValue overflowY, Rectangle layoutBox) {
        AbstractRenderer overflowRenderer = null;
        Float blockMinHeight = retrieveMinHeight();
        if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) && null != blockMinHeight && blockMinHeight > occupiedArea.getBBox().getHeight()) {
            float blockBottom = occupiedArea.getBBox().getBottom() - ((float) blockMinHeight - occupiedArea.getBBox().getHeight());
            if (isFixedLayout()) {
                occupiedArea.getBBox().setY(blockBottom).setHeight((float) blockMinHeight);
            } else {
                // Because of float precision inaccuracy, iText can incorrectly calculate that the block of fixed height
                // needs to be split. As a result, an empty block with a height equal to sum of paddings
                // may appear on the next area. To prevent such situations epsilon is used.
                if (isOverflowFit(overflowY) && blockBottom + EPS < layoutBox.getBottom()) {
                    float hDelta = occupiedArea.getBBox().getBottom() - layoutBox.getBottom();
                    occupiedArea.getBBox()
                            .increaseHeight(hDelta)
                            .setY(layoutBox.getBottom());

                    if (occupiedArea.getBBox().getHeight() < 0) {
                        occupiedArea.getBBox().setHeight(0);
                    }

                    this.isLastRendererForModelElement = false;
                    overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
                    overflowRenderer.updateMinHeight(UnitValue.createPointValue((float) blockMinHeight - occupiedArea.getBBox().getHeight()));
                    if (hasProperty(Property.HEIGHT)) {
                        overflowRenderer.updateHeight(UnitValue.createPointValue((float) retrieveHeight() - occupiedArea.getBBox().getHeight()));
                    }
                } else {
                    occupiedArea.getBBox().setY(blockBottom).setHeight((float) blockMinHeight);
                }
            }
        }
        return overflowRenderer;
    }

    void fixOccupiedAreaIfOverflowedX(OverflowPropertyValue overflowX, Rectangle layoutBox) {
        if (isOverflowFit(overflowX)) {
            return;
        }

        if ((occupiedArea.getBBox().getWidth() > layoutBox.getWidth() || occupiedArea.getBBox().getLeft() < layoutBox.getLeft())) {
            occupiedArea.getBBox().setX(layoutBox.getX()).setWidth(layoutBox.getWidth());
        }
    }

    void fixOccupiedAreaIfOverflowedY(OverflowPropertyValue overflowY, Rectangle layoutBox) {
        if (isOverflowFit(overflowY)) {
            return;
        }
        if (occupiedArea.getBBox().getBottom() < layoutBox.getBottom()) {
            float difference = layoutBox.getBottom() - occupiedArea.getBBox().getBottom();
            occupiedArea.getBBox().moveUp(difference).decreaseHeight(difference);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinMaxWidth getMinMaxWidth() {
        MinMaxWidth minMaxWidth = new MinMaxWidth(calculateAdditionalWidth(this));
        if (!setMinMaxWidthBasedOnFixedWidth(minMaxWidth)) {
            Float minWidth = hasAbsoluteUnitValue(Property.MIN_WIDTH) ? retrieveMinWidth(0) : null;
            Float maxWidth = hasAbsoluteUnitValue(Property.MAX_WIDTH) ? retrieveMaxWidth(0) : null;
            if (minWidth == null || maxWidth == null) {
                AbstractWidthHandler handler = new MaxMaxWidthHandler(minMaxWidth);
                int epsilonNum = 0;
                int curEpsNum = 0;
                float previousFloatingChildWidth = 0;
                for (IRenderer childRenderer : childRenderers) {
                    MinMaxWidth childMinMaxWidth;
                    childRenderer.setParent(this);
                    if (childRenderer instanceof AbstractRenderer) {
                        childMinMaxWidth = ((AbstractRenderer) childRenderer).getMinMaxWidth();
                    } else {
                        childMinMaxWidth = MinMaxWidthUtils.countDefaultMinMaxWidth(childRenderer);
                    }
                    handler.updateMaxChildWidth(childMinMaxWidth.getMaxWidth() + (FloatingHelper.isRendererFloating(childRenderer) ? previousFloatingChildWidth : 0));
                    handler.updateMinChildWidth(childMinMaxWidth.getMinWidth());
                    previousFloatingChildWidth = FloatingHelper.isRendererFloating(childRenderer) ? previousFloatingChildWidth + childMinMaxWidth.getMaxWidth() : 0;
                    if (FloatingHelper.isRendererFloating(childRenderer)) {
                        curEpsNum++;
                    } else {
                        epsilonNum = Math.max(epsilonNum, curEpsNum);
                        curEpsNum = 0;
                    }
                }
                epsilonNum = Math.max(epsilonNum, curEpsNum);
                handler.minMaxWidth.setChildrenMaxWidth(handler.minMaxWidth.getChildrenMaxWidth() + epsilonNum * AbstractRenderer.EPS);
                handler.minMaxWidth.setChildrenMinWidth(handler.minMaxWidth.getChildrenMinWidth() + epsilonNum * AbstractRenderer.EPS);
            }
            if (minWidth != null) {
                minMaxWidth.setChildrenMinWidth((float) minWidth);

            }
            // if max-width was defined explicitly, it shouldn't be overwritten
            if (maxWidth != null) {
                minMaxWidth.setChildrenMaxWidth((float) maxWidth);
            } else {
                if (minMaxWidth.getChildrenMinWidth() > minMaxWidth.getChildrenMaxWidth()) {
                    minMaxWidth.setChildrenMaxWidth(minMaxWidth.getChildrenMinWidth());
                }
            }
        }

        if (this.getPropertyAsFloat(Property.ROTATION_ANGLE) != null) {
            return RotationUtils.countRotationMinMaxWidth(minMaxWidth, this);
        }

        return minMaxWidth;
    }

    void handleForcedPlacement(boolean anythingPlaced) {
        // We placed something meaning that we don't need this property anymore while processing other children
        // to do not force place them
        if (anythingPlaced && hasOwnProperty(Property.FORCED_PLACEMENT)) {
            deleteOwnProperty(Property.FORCED_PLACEMENT);
        }
    }

    private boolean isAnythingOccupied() {
        return !(occupiedArea.getBBox().getHeight() < EPS);
    }

    private void replaceSplitRendererKidFloats(Map<Integer, IRenderer> waitingFloatsSplitRenderers, IRenderer splitRenderer) {
        for (Map.Entry<Integer, IRenderer> waitingSplitRenderer : waitingFloatsSplitRenderers.entrySet()) {
            if (waitingSplitRenderer.getValue() != null) {
                splitRenderer.getChildRenderers().set(waitingSplitRenderer.getKey(), waitingSplitRenderer.getValue());
            } else {
                splitRenderer.getChildRenderers().set((int) waitingSplitRenderer.getKey(), null);
            }
        }
        for (int i = splitRenderer.getChildRenderers().size() - 1; i >= 0; --i) {
            if (splitRenderer.getChildRenderers().get(i) == null) {
                splitRenderer.getChildRenderers().remove(i);
            }
        }
    }

    private void addMarkedContent(DrawContext drawContext, boolean isBegin) {
        if (Boolean.TRUE.equals(this.<Boolean>getProperty(Property.ADD_MARKED_CONTENT_TEXT))) {
            PdfCanvas canvas = drawContext.getCanvas();
            if (isBegin) {
                canvas.beginVariableText().saveState().endPath();
            } else {
                canvas.restoreState().endVariableText();
            }
        }
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
