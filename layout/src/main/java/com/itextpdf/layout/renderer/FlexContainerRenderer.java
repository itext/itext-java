/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.properties.AlignmentPropertyValue;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.FlexDirectionPropertyValue;
import com.itextpdf.layout.properties.FlexWrapPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlexContainerRenderer extends DivRenderer {

    /**
     * Used for caching purposes in FlexUtil
     * We couldn't find the real use case when this map contains more than 1 entry
     * but let it still be a map to be on a safe(r) side
     * Map mainSize (always width in our case) - hypotheticalCrossSize
     */
    private final Map<Float, Float> hypotheticalCrossSizes = new HashMap<>();

    private List<List<FlexItemInfo>> lines;

    private IFlexItemMainDirector flexItemMainDirector = null;

    /**
     * Child renderers and their heights and min heights before the layout.
     */
    private final Map<IRenderer, Tuple2<UnitValue, UnitValue>> heights = new HashMap<>();

    /**
     * Creates a FlexContainerRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link com.itextpdf.layout.element.Div} which this object should manage
     */
    public FlexContainerRenderer(Div modelElement) {
        super(modelElement);
    }

    /**
     * Gets a new instance of this class to be used as a next renderer, after this renderer is used, if
     * {@link #layout(LayoutContext)} is called more than once.
     *
     * <p>
     * If a renderer overflows to the next area, iText uses this method to create a renderer
     * for the overflow part. So if one wants to extend {@link FlexContainerRenderer}, one should override
     * this method: otherwise the default method will be used and thus the default rather than the custom
     * renderer will be created.
     *
     * @return new renderer instance
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(FlexContainerRenderer.class, this.getClass());
        return new FlexContainerRenderer((Div) modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        Rectangle layoutContextRectangle = layoutContext.getArea().getBBox();
        setThisAsParent(getChildRenderers());
        lines = FlexUtil.calculateChildrenRectangles(layoutContextRectangle, this);
        applyWrapReverse();
        List<IRenderer> renderers = getFlexItemMainDirector().applyDirection(lines);
        removeAllChildRenderers(getChildRenderers());
        addAllChildRenderers(renderers);

        List<IRenderer> renderersToOverflow = retrieveRenderersToOverflow(layoutContextRectangle);

        final List<UnitValue> previousWidths = new ArrayList<>();
        for (final List<FlexItemInfo> line : lines) {
            for (final FlexItemInfo itemInfo : line) {
                final Rectangle rectangleWithoutBordersMarginsPaddings;
                if (AbstractRenderer.isBorderBoxSizing(itemInfo.getRenderer())) {
                    rectangleWithoutBordersMarginsPaddings =
                            itemInfo.getRenderer().applyMargins(itemInfo.getRectangle().clone(), false);
                } else {
                    rectangleWithoutBordersMarginsPaddings =
                            itemInfo.getRenderer().applyMarginsBordersPaddings(itemInfo.getRectangle().clone(), false);
                }

                heights.put(itemInfo.getRenderer(), new Tuple2<UnitValue, UnitValue>(
                        itemInfo.getRenderer().<UnitValue>getProperty(Property.HEIGHT),
                        itemInfo.getRenderer().<UnitValue>getProperty(Property.MIN_HEIGHT)));
                previousWidths.add(itemInfo.getRenderer().<UnitValue>getProperty(Property.WIDTH));

                itemInfo.getRenderer().setProperty(Property.WIDTH,
                        UnitValue.createPointValue(rectangleWithoutBordersMarginsPaddings.getWidth()));
                itemInfo.getRenderer().setProperty(Property.HEIGHT,
                        UnitValue.createPointValue(rectangleWithoutBordersMarginsPaddings.getHeight()));
                // TODO DEVSIX-1895 Once the ticket is closed, there will be no need in setting min-height
                // In case element takes less vertical space than expected, we need to make sure
                // it is extended to the height predicted by the algo
                itemInfo.getRenderer().setProperty(Property.MIN_HEIGHT,
                        UnitValue.createPointValue(rectangleWithoutBordersMarginsPaddings.getHeight()));

                // Property.HORIZONTAL_ALIGNMENT mustn't play, in flex container items are aligned
                // using justify-content and align-items
                itemInfo.getRenderer().setProperty(Property.HORIZONTAL_ALIGNMENT, null);
            }
        }

        LayoutResult result = super.layout(layoutContext);
        if (!renderersToOverflow.isEmpty()) {
            adjustLayoutResultToHandleOverflowRenderers(result, renderersToOverflow);
        }

        // We must set back widths of the children because multiple layouts are possible
        // If flex-grow is less than 1, layout algorithm increases the width of the element based on the initial width
        // And if we would not set back widths, every layout flex-item width will grow.
        int counter = 0;
        for (final List<FlexItemInfo> line : lines) {
            for (final FlexItemInfo itemInfo : line) {
                itemInfo.getRenderer().setProperty(Property.WIDTH, previousWidths.get(counter));
                Tuple2<UnitValue, UnitValue> curHeights = heights.get(itemInfo.getRenderer());
                itemInfo.getRenderer().setProperty(Property.HEIGHT, curHeights.getFirst());
                itemInfo.getRenderer().setProperty(Property.MIN_HEIGHT, curHeights.getSecond());
                ++counter;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinMaxWidth getMinMaxWidth() {
        final MinMaxWidth minMaxWidth = new MinMaxWidth(calculateAdditionalWidth(this));
        final AbstractWidthHandler minMaxWidthHandler = new MaxMaxWidthHandler(minMaxWidth);
        if (!setMinMaxWidthBasedOnFixedWidth(minMaxWidth)) {
            final Float minWidth = hasAbsoluteUnitValue(Property.MIN_WIDTH) ? retrieveMinWidth(0) : null;
            final Float maxWidth = hasAbsoluteUnitValue(Property.MAX_WIDTH) ? retrieveMaxWidth(0) : null;
            if (minWidth == null || maxWidth == null) {
                findMinMaxWidthIfCorrespondingPropertiesAreNotSet(minMaxWidth, minMaxWidthHandler);
            }
            if (minWidth != null) {
                minMaxWidth.setChildrenMinWidth((float) minWidth);
            }
            // if max-width was defined explicitly, it shouldn't be overwritten
            if (maxWidth == null) {
                if (minMaxWidth.getChildrenMinWidth() > minMaxWidth.getChildrenMaxWidth()) {
                    minMaxWidth.setChildrenMaxWidth(minMaxWidth.getChildrenMinWidth());
                }
            } else {
                minMaxWidth.setChildrenMaxWidth((float) maxWidth);
            }
        }

        if (this.getPropertyAsFloat(Property.ROTATION_ANGLE) != null) {
            return RotationUtils.countRotationMinMaxWidth(minMaxWidth, this);
        }

        return minMaxWidth;
    }

    IFlexItemMainDirector getFlexItemMainDirector() {
        if (flexItemMainDirector == null) {
            flexItemMainDirector = createMainDirector();
        }

        return flexItemMainDirector;
    }

    /**
     * Check if flex container is wrapped reversely.
     *
     * @return {@code true} if flex-wrap property is set to wrap-reverse, {@code false} otherwise.
     */
    boolean isWrapReverse() {
        return FlexWrapPropertyValue.WRAP_REVERSE ==
                this.<FlexWrapPropertyValue>getProperty(Property.FLEX_WRAP, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AbstractRenderer[] createSplitAndOverflowRenderers(int childPos, int layoutStatus, LayoutResult childResult,
                                                       Map<Integer, IRenderer> waitingFloatsSplitRenderers,
                                                       List<IRenderer> waitingOverflowFloatRenderers) {
        final AbstractRenderer splitRenderer = createSplitRenderer(layoutStatus);
        final AbstractRenderer overflowRenderer = createOverflowRenderer(layoutStatus);

        final IRenderer childRenderer = getChildRenderers().get(childPos);
        final boolean forcedPlacement = Boolean.TRUE.equals(this.<Boolean>getProperty(Property.FORCED_PLACEMENT));
        boolean metChildRenderer = false;
        for (int i = 0; i < lines.size(); ++i) {
            List<FlexItemInfo> line = lines.get(i);
            final boolean isSplitLine = line.stream().anyMatch(flexItem -> flexItem.getRenderer() == childRenderer);
            metChildRenderer = metChildRenderer || isSplitLine;

            // If the renderer to split is in the current line
            if (isSplitLine && !forcedPlacement && layoutStatus == LayoutResult.PARTIAL &&
                    (!FlexUtil.isColumnDirection(this) ||
                            (i == 0 && line.get(0).getRenderer() == childRenderer))) {
                // It has sense to call it also for LayoutResult.NOTHING. And then try to layout remaining renderers
                // in line inside fillSplitOverflowRenderersForPartialResult to see if some of them can be left or
                // partially left on the first page (in split renderer). But it's not that easy.
                // So currently, if the 1st not fully layouted renderer is layouted with LayoutResult.NOTHING,
                // the whole line is moved to the next page (overflow renderer).
                fillSplitOverflowRenderersForPartialResult(splitRenderer, overflowRenderer, line, childRenderer,
                        childResult);
                getFlexItemMainDirector().applyDirectionForLine(overflowRenderer.getChildRenderers());
            } else {
                List<IRenderer> overflowRendererChildren = new ArrayList<IRenderer>();
                boolean isSingleColumn = lines.size() == 1 && FlexUtil.isColumnDirection(this);
                boolean metChildRendererInLine = false;
                for (final FlexItemInfo itemInfo : line) {
                    metChildRendererInLine = metChildRendererInLine || itemInfo.getRenderer() == childRenderer;
                    if ((!isSingleColumn && metChildRenderer || metChildRendererInLine) && !forcedPlacement) {
                        overflowRendererChildren.add(itemInfo.getRenderer());
                    } else {
                        splitRenderer.addChildRenderer(itemInfo.getRenderer());
                    }
                }
                getFlexItemMainDirector().applyDirectionForLine(overflowRendererChildren);

                // If wrapped reversely we should add a line into beginning to correctly recalculate
                // and inverse lines while layouting overflowRenderer.
                if (isWrapReverse()) {
                    overflowRenderer.addAllChildRenderers(0, overflowRendererChildren);
                } else {
                    overflowRenderer.addAllChildRenderers(overflowRendererChildren);
                }
            }
        }

        overflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);

        return new AbstractRenderer[]{splitRenderer, overflowRenderer};
    }

    @Override
    LayoutResult processNotFullChildResult(LayoutContext layoutContext,
                                           Map<Integer, IRenderer> waitingFloatsSplitRenderers,
                                           List<IRenderer> waitingOverflowFloatRenderers, boolean wasHeightClipped,
                                           List<Rectangle> floatRendererAreas, boolean marginsCollapsingEnabled,
                                           float clearHeightCorrection, Border[] borders, UnitValue[] paddings,
                                           List<Rectangle> areas, int currentAreaPos, Rectangle layoutBox,
                                           Set<Rectangle> nonChildFloatingRendererAreas, IRenderer causeOfNothing,
                                           boolean anythingPlaced, int childPos, LayoutResult result) {
        final boolean keepTogether = isKeepTogether(causeOfNothing);
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) || wasHeightClipped) {
            final AbstractRenderer splitRenderer = keepTogether ? null : createSplitRenderer(result.getStatus());
            if (splitRenderer != null) {
                splitRenderer.setChildRenderers(getChildRenderers());
            }

            return new LayoutResult(LayoutResult.FULL,
                    getOccupiedAreaInCaseNothingWasWrappedWithFull(result, splitRenderer), splitRenderer, null, null);
        }

        final AbstractRenderer[] splitAndOverflowRenderers = createSplitAndOverflowRenderers(
                childPos, result.getStatus(), result, waitingFloatsSplitRenderers, waitingOverflowFloatRenderers);

        AbstractRenderer splitRenderer = splitAndOverflowRenderers[0];
        final AbstractRenderer overflowRenderer = splitAndOverflowRenderers[1];
        overflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);
        updateHeightsOnSplit(wasHeightClipped, splitRenderer, overflowRenderer);

        if (isRelativePosition() && !positionedRenderers.isEmpty()) {
            overflowRenderer.positionedRenderers = new ArrayList<>(positionedRenderers);
        }

        if (keepTogether) {
            splitRenderer = null;
            overflowRenderer.setChildRenderers(getChildRenderers());
        }

        correctFixedLayout(layoutBox);

        applyAbsolutePositionIfNeeded(layoutContext);

        applyPaddings(occupiedArea.getBBox(), paddings, true);
        applyBorderBox(occupiedArea.getBBox(), borders, true);
        applyMargins(occupiedArea.getBBox(), true);
        if (splitRenderer == null || splitRenderer.getChildRenderers().isEmpty()) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, overflowRenderer,
                    result.getCauseOfNothing()).setAreaBreak(result.getAreaBreak());
        } else {
            return new LayoutResult(LayoutResult.PARTIAL, layoutContext.getArea(), splitRenderer,
                    overflowRenderer, null).setAreaBreak(result.getAreaBreak());
        }
    }

    // TODO DEVSIX-5238 Consider this fix (perhaps it should be improved or unified) while working on the ticket
    LayoutArea getOccupiedAreaInCaseNothingWasWrappedWithFull(LayoutResult result, IRenderer splitRenderer) {
        return null != result.getOccupiedArea() ? result.getOccupiedArea() : splitRenderer.getOccupiedArea();
    }

    @Override
    boolean stopLayoutingChildrenIfChildResultNotFull(LayoutResult returnResult) {
        return returnResult.getStatus() != LayoutResult.FULL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void recalculateOccupiedAreaAfterChildLayout(Rectangle resultBBox, Float blockMaxHeight) {
        final Rectangle oldBBox = occupiedArea.getBBox().clone();
        final Rectangle recalculatedRectangle = Rectangle.getCommonRectangle(occupiedArea.getBBox(),
                resultBBox);
        occupiedArea.getBBox().setY(recalculatedRectangle.getY());
        occupiedArea.getBBox().setHeight(recalculatedRectangle.getHeight());
        if (oldBBox.getTop() < occupiedArea.getBBox().getTop()) {
            occupiedArea.getBBox().decreaseHeight(occupiedArea.getBBox().getTop() - oldBBox.getTop());
        }
        if (null != blockMaxHeight &&
                occupiedArea.getBBox().getHeight() > ((float) blockMaxHeight)) {
            occupiedArea.getBBox()
                    .moveUp(occupiedArea.getBBox().getHeight() - ((float) blockMaxHeight));
            occupiedArea.getBBox().setHeight((float) blockMaxHeight);
        }
    }

    @Override
    MarginsCollapseInfo startChildMarginsHandling(IRenderer childRenderer,
                                                  Rectangle layoutBox, MarginsCollapseHandler marginsCollapseHandler) {
        return marginsCollapseHandler.startChildMarginsHandling(null, layoutBox);
    }

    @Override
    void decreaseLayoutBoxAfterChildPlacement(Rectangle layoutBox, LayoutResult result, IRenderer childRenderer) {
        if (FlexUtil.isColumnDirection(this)) {
            decreaseLayoutBoxAfterChildPlacementColumnLayout(layoutBox, childRenderer);
        } else {
            decreaseLayoutBoxAfterChildPlacementRowLayout(layoutBox, result, childRenderer);
        }
    }

    void decreaseLayoutBoxAfterChildPlacementRowLayout(Rectangle layoutBox, LayoutResult result,
                                                       IRenderer childRenderer) {
        layoutBox.decreaseWidth(result.getOccupiedArea().getBBox().getRight() - layoutBox.getLeft());
        layoutBox.setX(result.getOccupiedArea().getBBox().getRight());

        List<FlexItemInfo> line = findLine(childRenderer);
        final boolean isLastInLine = childRenderer.equals(line.get(line.size() - 1).getRenderer());
        // If it was the last renderer in line we have to go to the next line (row)
        if (isLastInLine) {
            float minBottom = layoutBox.getTop();
            float minLeft = layoutBox.getLeft();
            float commonWidth = 0;
            for (FlexItemInfo item : line) {
                minLeft = Math.min(minLeft,
                        item.getRenderer().getOccupiedArea().getBBox().getLeft() - item.getRectangle().getLeft());
                minBottom = Math.min(minBottom, item.getRenderer().getOccupiedArea().getBBox().getBottom());
                commonWidth += item.getRectangle().getLeft() + item.getRenderer().getOccupiedArea().getBBox().getWidth();
            }

            layoutBox.setX(minLeft);
            layoutBox.increaseWidth(commonWidth);
            layoutBox.decreaseHeight(layoutBox.getTop() - minBottom);
        }
    }

    void decreaseLayoutBoxAfterChildPlacementColumnLayout(Rectangle layoutBox, IRenderer childRenderer) {
        FlexItemInfo childFlexItemInfo = findFlexItemInfo((AbstractRenderer) childRenderer);
        layoutBox.decreaseHeight(childFlexItemInfo.getRenderer().getOccupiedArea().getBBox().getHeight() +
                childFlexItemInfo.getRectangle().getY());

        List<FlexItemInfo> line = findLine(childRenderer);
        final boolean isLastInLine = childRenderer.equals(line.get(line.size() - 1).getRenderer());
        // If it was the last renderer in line we have to go to the next line (row)
        if (isLastInLine) {
            float maxWidth = 0;
            float commonHeight = 0;
            for (FlexItemInfo item : line) {
                maxWidth = Math.max(maxWidth, item.getRenderer().getOccupiedArea().getBBox().getWidth()
                        + item.getRectangle().getX());
                commonHeight += item.getRectangle().getY() + item.getRenderer().getOccupiedArea().getBBox().getHeight();
            }
            layoutBox.increaseHeight(commonHeight);
            layoutBox.decreaseWidth(maxWidth);
            layoutBox.moveRight(maxWidth);
        }
    }

    @Override
    Rectangle recalculateLayoutBoxBeforeChildLayout(Rectangle layoutBox,
                                                    IRenderer childRenderer, Rectangle initialLayoutBox) {
        Rectangle layoutBoxCopy = layoutBox.clone();
        if (childRenderer instanceof AbstractRenderer) {
            FlexItemInfo childFlexItemInfo = findFlexItemInfo((AbstractRenderer) childRenderer);
            if (childFlexItemInfo != null) {
                layoutBoxCopy.decreaseWidth(childFlexItemInfo.getRectangle().getX());
                layoutBoxCopy.moveRight(childFlexItemInfo.getRectangle().getX());

                layoutBoxCopy.decreaseHeight(childFlexItemInfo.getRectangle().getY());
            }
        }
        return layoutBoxCopy;
    }

    @Override
    void handleForcedPlacement(boolean anythingPlaced) {
        // In (horizontal) FlexContainerRenderer Property.FORCED_PLACEMENT is still valid for other children
        // so do nothing
    }

    void setHypotheticalCrossSize(Float mainSize, Float hypotheticalCrossSize) {
        hypotheticalCrossSizes.put(mainSize.floatValue(), hypotheticalCrossSize);
    }

    Float getHypotheticalCrossSize(Float mainSize) {
        return hypotheticalCrossSizes.get(mainSize.floatValue());
    }

    /**
     * Apply wrap-reverse property.
     */
    private void applyWrapReverse() {
        if (!isWrapReverse()) {
            return;
        }

        Collections.reverse(lines);
        List<IRenderer> reorderedRendererList = new ArrayList<>();
        for (List<FlexItemInfo> line : lines) {
            for (FlexItemInfo itemInfo : line) {
                reorderedRendererList.add(itemInfo.getRenderer());
            }
        }

        removeAllChildRenderers(getChildRenderers());
        addAllChildRenderers(reorderedRendererList);
    }

    private FlexItemInfo findFlexItemInfo(AbstractRenderer renderer) {
        for (List<FlexItemInfo> line : lines) {
            for (FlexItemInfo itemInfo : line) {
                if (itemInfo.getRenderer().equals(renderer)) {
                    return itemInfo;
                }
            }
        }
        return null;
    }

    private List<FlexItemInfo> findLine(IRenderer renderer) {
        for (List<FlexItemInfo> line : lines) {
            for (FlexItemInfo itemInfo : line) {
                if (itemInfo.getRenderer().equals(renderer)) {
                    return line;
                }
            }
        }
        return null;
    }

    @Override
    void fixOccupiedAreaIfOverflowedX(OverflowPropertyValue overflowX, Rectangle layoutBox) {
        // TODO DEVSIX-5087 Support overflow visible/hidden property correctly
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        // TODO DEVSIX-5087 Since overflow-fit is an internal iText overflow value, we do not need to support if
        // for html/css objects, such as flex. As for now we will set VISIBLE by default, however, while working
        // on the ticket one may come to some more satifactory approach
        renderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        super.addChild(renderer);
    }

    private static void addSimulateDiv(AbstractRenderer overflowRenderer, float width) {
        final IRenderer fakeOverflowRenderer = new DivRenderer(
                new Div().setMinWidth(width).setMaxWidth(width));
        overflowRenderer.addChildRenderer(fakeOverflowRenderer);
    }

    private void fillSplitOverflowRenderersForPartialResult(AbstractRenderer splitRenderer,
                                                            AbstractRenderer overflowRenderer, List<FlexItemInfo> line, IRenderer childRenderer,
                                                            LayoutResult childResult) {
        restoreHeightForOverflowRenderer(childRenderer, childResult.getOverflowRenderer());

        float occupiedSpace = 0;
        float maxHeightInLine = 0;
        boolean metChildRendererInLine = false;
        for (final FlexItemInfo itemInfo : line) {
            // Split the line
            if (itemInfo.getRenderer() == childRenderer) {
                metChildRendererInLine = true;
                if (childResult.getSplitRenderer() != null) {
                    splitRenderer.addChildRenderer(childResult.getSplitRenderer());
                }

                if (childResult.getOverflowRenderer() != null) {
                    // Get rid of vertical alignment for item with partial result. For column direction, justify-content
                    // is applied to the entire line, not the single item, so there is no point in getting rid of it
                    if (!FlexUtil.isColumnDirection(this)) {
                        setAlignSelfIfNotStretch(childResult.getOverflowRenderer());
                    }
                    overflowRenderer.addChildRenderer(childResult.getOverflowRenderer());
                }

                // Count the height allowed for the items after the one which was partially layouted
                maxHeightInLine = Math.max(maxHeightInLine,
                        itemInfo.getRectangle().getY() + childResult.getOccupiedArea().getBBox().getHeight());
            } else if (metChildRendererInLine) {
                if (FlexUtil.isColumnDirection(this)) {
                    overflowRenderer.addChildRenderer(itemInfo.getRenderer());
                    continue;
                }
                // Process all following renderers in the current line
                // We have to layout them to understand what goes where
                // x - space occupied by all preceding items
                // y - y of current occupied area
                // width - item width
                // height - allowed height for the item
                final Rectangle neighbourBbox = new Rectangle(getOccupiedAreaBBox().getX() + occupiedSpace,
                        getOccupiedAreaBBox().getY(),
                        itemInfo.getRectangle().getWidth(),
                        maxHeightInLine - itemInfo.getRectangle().getY());
                final LayoutResult neighbourLayoutResult = itemInfo.getRenderer().layout(new LayoutContext(
                        new LayoutArea(childResult.getOccupiedArea().getPageNumber(), neighbourBbox)));
                restoreHeightForOverflowRenderer(itemInfo.getRenderer(), neighbourLayoutResult.getOverflowRenderer());

                // Handle result
                if (neighbourLayoutResult.getStatus() == LayoutResult.PARTIAL &&
                        neighbourLayoutResult.getSplitRenderer() != null) {
                    splitRenderer.addChildRenderer(neighbourLayoutResult.getSplitRenderer());
                } else if (neighbourLayoutResult.getStatus() == LayoutResult.FULL) {
                    splitRenderer.addChildRenderer(itemInfo.getRenderer());
                } else {
                    // LayoutResult.NOTHING
                }

                if (neighbourLayoutResult.getOverflowRenderer() != null) {
                    if (neighbourLayoutResult.getStatus() == LayoutResult.PARTIAL) {
                        // Get rid of cross alignment for item with partial result
                        setAlignSelfIfNotStretch(neighbourLayoutResult.getOverflowRenderer());
                    }
                    overflowRenderer.addChildRenderer(neighbourLayoutResult.getOverflowRenderer());
                } else {
                    // Here we might need to still occupy the space on overflow renderer
                    addSimulateDiv(overflowRenderer, itemInfo.getRectangle().getWidth());
                }
            } else {
                // Process all preceeding renderers in the current line
                // They all were layouted as FULL so add them into split renderer
                splitRenderer.addChildRenderer(itemInfo.getRenderer());

                // But we also need to occupy the space on overflow renderer
                addSimulateDiv(overflowRenderer, itemInfo.getRectangle().getWidth());

                // Count the height allowed for the items after the one which was partially layouted
                maxHeightInLine = Math.max(maxHeightInLine,
                        itemInfo.getRectangle().getY() + itemInfo.getRenderer().getOccupiedAreaBBox().getHeight());
            }

            // X is nonzero only for the 1st renderer in line serving for alignment adjustments
            occupiedSpace += itemInfo.getRectangle().getX() + itemInfo.getRectangle().getWidth();
        }
    }

    private void setAlignSelfIfNotStretch(IRenderer overflowRenderer) {
        AlignmentPropertyValue alignItems =
                (AlignmentPropertyValue) this.<AlignmentPropertyValue>getProperty(
                        Property.ALIGN_ITEMS, AlignmentPropertyValue.STRETCH);
        AlignmentPropertyValue alignSelf =
                (AlignmentPropertyValue) overflowRenderer.<AlignmentPropertyValue>getProperty(
                        Property.ALIGN_SELF, alignItems);
        if (alignSelf != AlignmentPropertyValue.STRETCH) {
            overflowRenderer.setProperty(Property.ALIGN_SELF, AlignmentPropertyValue.START);
        }
    }

    private void restoreHeightForOverflowRenderer(IRenderer childRenderer, IRenderer overflowRenderer) {
        if (overflowRenderer == null) {
            return;
        }

        // childRenderer is the original renderer we set the height for before the layout
        // And we need to remove the height from the corresponding overflow renderer
        Tuple2<UnitValue, UnitValue> curHeights = heights.get(childRenderer);
        if (curHeights.getFirst() == null) {
            overflowRenderer.deleteOwnProperty(Property.HEIGHT);
        }
        if (curHeights.getSecond() == null) {
            overflowRenderer.deleteOwnProperty(Property.MIN_HEIGHT);
        }
    }

    private void findMinMaxWidthIfCorrespondingPropertiesAreNotSet(MinMaxWidth minMaxWidth,
                                                                   AbstractWidthHandler minMaxWidthHandler) {
        float initialMinWidth = minMaxWidth.getChildrenMinWidth();
        float initialMaxWidth = minMaxWidth.getChildrenMaxWidth();
        if (lines == null || lines.size() == 1) {
            findMinMaxWidth(initialMinWidth, initialMaxWidth, minMaxWidthHandler, getChildRenderers());
        } else {
            for (List<FlexItemInfo> line : lines) {
                List<IRenderer> childRenderers = new ArrayList<>();
                for (FlexItemInfo itemInfo : line) {
                    childRenderers.add(itemInfo.getRenderer());
                }
                findMinMaxWidth(initialMinWidth, initialMaxWidth, minMaxWidthHandler, childRenderers);
            }
        }
    }

    private void findMinMaxWidth(float initialMinWidth, float initialMaxWidth, AbstractWidthHandler minMaxWidthHandler,
                                 List<IRenderer> childRenderers) {
        float maxWidth = initialMaxWidth;
        float minWidth = initialMinWidth;
        for (final IRenderer childRenderer : childRenderers) {
            MinMaxWidth childMinMaxWidth;
            childRenderer.setParent(this);
            if (childRenderer instanceof AbstractRenderer) {
                childMinMaxWidth = ((AbstractRenderer) childRenderer).getMinMaxWidth();
            } else {
                childMinMaxWidth = MinMaxWidthUtils.countDefaultMinMaxWidth(childRenderer);
            }
            if (FlexUtil.isColumnDirection(this)) {
                maxWidth = Math.max(maxWidth, childMinMaxWidth.getMaxWidth());
                minWidth = Math.max(minWidth, childMinMaxWidth.getMinWidth());
            } else {
                maxWidth += childMinMaxWidth.getMaxWidth();
                minWidth += childMinMaxWidth.getMinWidth();
            }
        }
        minMaxWidthHandler.updateMaxChildWidth(maxWidth);
        minMaxWidthHandler.updateMinChildWidth(minWidth);
    }

    /**
     * Check if flex container direction is row reverse.
     *
     * @return {@code true} if flex-direction property is set to row-reverse, {@code false} otherwise.
     */
    private boolean isRowReverse() {
        return FlexDirectionPropertyValue.ROW_REVERSE ==
                this.<FlexDirectionPropertyValue>getProperty(Property.FLEX_DIRECTION, null);
    }

    private boolean isColumnReverse() {
        return FlexDirectionPropertyValue.COLUMN_REVERSE ==
                this.<FlexDirectionPropertyValue>getProperty(Property.FLEX_DIRECTION, null);
    }

    private IFlexItemMainDirector createMainDirector() {
        if (FlexUtil.isColumnDirection(this)) {
            return isColumnReverse()
                    ? (IFlexItemMainDirector) new BottomToTopFlexItemMainDirector() :
                    new TopToBottomFlexItemMainDirector();
        } else {
            final boolean isRtlDirection = BaseDirection.RIGHT_TO_LEFT ==
                    this.<BaseDirection>getProperty(Property.BASE_DIRECTION, null);
            flexItemMainDirector = isRowReverse() ^ isRtlDirection
                    ? (IFlexItemMainDirector) new RtlFlexItemMainDirector() : new LtrFlexItemMainDirector();
            return flexItemMainDirector;
        }
    }

    private List<IRenderer> retrieveRenderersToOverflow(Rectangle flexContainerBBox) {
        List<IRenderer> renderersToOverflow = new ArrayList<>();
        Rectangle layoutContextRectangle = flexContainerBBox.clone();
        applyMarginsBordersPaddings(layoutContextRectangle, false);
        if (FlexUtil.isColumnDirection(this) &&
                FlexUtil.getMainSize(this, layoutContextRectangle) >= layoutContextRectangle.getHeight()) {
            float commonLineCrossSize = 0;
            List<Float> lineCrossSizes = FlexUtil.calculateColumnDirectionCrossSizes(lines);
            for (int i = 0; i < lines.size(); ++i) {
                commonLineCrossSize += lineCrossSizes.get(i);
                if (i > 0 && commonLineCrossSize > layoutContextRectangle.getWidth()) {
                    List<IRenderer> lineRenderersToOverflow = new ArrayList<>();
                    for (final FlexItemInfo itemInfo : lines.get(i)) {
                        lineRenderersToOverflow.add(itemInfo.getRenderer());
                    }
                    getFlexItemMainDirector().applyDirectionForLine(lineRenderersToOverflow);
                    if (isWrapReverse()) {
                        renderersToOverflow.addAll(0, lineRenderersToOverflow);
                    } else {
                        renderersToOverflow.addAll(lineRenderersToOverflow);
                    }
                    // Those renderers will be handled in adjustLayoutResultToHandleOverflowRenderers method.
                    // If we leave these children in multi-page fixed-height flex container, renderers
                    // will be drawn to the right outside the container's bounds on the first page
                    // (this logic is expected, but needed for the last page only)
                    for (IRenderer renderer : renderersToOverflow) {
                        childRenderers.remove(renderer);
                    }
                }
            }
        }
        return renderersToOverflow;
    }

    private void adjustLayoutResultToHandleOverflowRenderers(LayoutResult result, List<IRenderer> renderersToOverflow) {
        if (LayoutResult.FULL == result.getStatus()) {
            IRenderer splitRenderer = createSplitRenderer(LayoutResult.PARTIAL);
            IRenderer overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
            for (IRenderer childRenderer : renderersToOverflow) {
                overflowRenderer.addChild(childRenderer);
            }
            for (IRenderer childRenderer : getChildRenderers()) {
                splitRenderer.addChild(childRenderer);
            }

            result.setStatus(LayoutResult.PARTIAL);
            result.setSplitRenderer(splitRenderer);
            result.setOverflowRenderer(overflowRenderer);
        }
        if (LayoutResult.PARTIAL == result.getStatus()) {
            IRenderer overflowRenderer = result.getOverflowRenderer();
            for (IRenderer childRenderer : renderersToOverflow) {
                if (!overflowRenderer.getChildRenderers().contains(childRenderer)) {
                    overflowRenderer.addChild(childRenderer);
                }
            }
        }
    }
}
