/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlexContainerRenderer extends DivRenderer {

    /* Used for caching purposes in FlexUtil
     * We couldn't find the real use case when this map contains more than 1 entry
     * but let it still be a map to be on a safe(r) side
     * Map mainSize (always width in our case) - hypotheticalCrossSize
     */
    private final Map<Float, Float> hypotheticalCrossSizes = new HashMap<>();

    private List<List<FlexItemInfo>> lines;

    /**
     * Creates a FlexContainerRenderer from its corresponding layout object.
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
        final List<UnitValue> previousWidths = new ArrayList<>();
        final List<UnitValue> previousHeights = new ArrayList<>();
        final List<UnitValue> previousMinHeights = new ArrayList<>();
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

                previousWidths.add(itemInfo.getRenderer().<UnitValue>getProperty(Property.WIDTH));
                previousHeights.add(itemInfo.getRenderer().<UnitValue>getProperty(Property.HEIGHT));
                previousMinHeights.add(itemInfo.getRenderer().<UnitValue>getProperty(Property.MIN_HEIGHT));

                itemInfo.getRenderer().setProperty(Property.WIDTH,
                        UnitValue.createPointValue(rectangleWithoutBordersMarginsPaddings.getWidth()));
                itemInfo.getRenderer().setProperty(Property.HEIGHT,
                        UnitValue.createPointValue(rectangleWithoutBordersMarginsPaddings.getHeight()));
                // TODO DEVSIX-1895 Once the ticket is closed, there will be no need in setting min-height
                // In case element takes less vertical space than expected, we need to make sure
                // it is extended to the height predicted by the algo
                itemInfo.getRenderer().setProperty(Property.MIN_HEIGHT,
                        UnitValue.createPointValue(rectangleWithoutBordersMarginsPaddings.getHeight()));
            }
        }

        final LayoutResult result = super.layout(layoutContext);

        // We must set back widths of the children because multiple layouts are possible
        // If flex-grow is less than 1, layout algorithm increases the width of the element based on the initial width
        // And if we would not set back widths, every layout flex-item width will grow.
        int counter = 0;
        for (final List<FlexItemInfo> line : lines) {
            for (final FlexItemInfo itemInfo : line) {
                itemInfo.getRenderer().setProperty(Property.WIDTH, previousWidths.get(counter));
                itemInfo.getRenderer().setProperty(Property.HEIGHT, previousHeights.get(counter));
                itemInfo.getRenderer().setProperty(Property.MIN_HEIGHT, previousMinHeights.get(counter));
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
        for (final List<FlexItemInfo> line : lines) {
            final boolean isSplitLine = line.stream().anyMatch(flexItem -> flexItem.getRenderer() == childRenderer);
            metChildRenderer = metChildRenderer || isSplitLine;

            // If the renderer to split is in the current line
            if (isSplitLine && !forcedPlacement && layoutStatus == LayoutResult.PARTIAL) {
                fillSplitOverflowRenderersForPartialResult(splitRenderer, overflowRenderer, line, childRenderer,
                        childResult);
            } else {
                for (final FlexItemInfo itemInfo : line) {
                    if (metChildRenderer && !forcedPlacement) {
                        overflowRenderer.addChildRenderer(itemInfo.getRenderer());
                    } else {
                        splitRenderer.addChildRenderer(itemInfo.getRenderer());
                    }
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

        // TODO DEVSIX-5086 When flex-wrap will be fully supported we'll need to update height on split
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
        // TODO DEVSIX-5086 When flex-wrap will be fully supported
        //  we'll need to decrease layout box with respect to the lines
        layoutBox.decreaseWidth(result.getOccupiedArea().getBBox().getRight() - layoutBox.getLeft());
        layoutBox.setX(result.getOccupiedArea().getBBox().getRight());
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

    private void fillSplitOverflowRenderersForPartialResult(AbstractRenderer splitRenderer,
            AbstractRenderer overflowRenderer, List<FlexItemInfo> line, IRenderer childRenderer,
            LayoutResult childResult) {
        // If we split, we remove (override) Property.ALIGN_ITEMS for the overflow renderer.
        // because we have to layout the remaining part at the top of the layout context.
        // TODO DEVSIX-5086 When flex-wrap will be fully supported we'll need to reconsider this.
        // The question is what should be set/calculated for the next line
        overflowRenderer.setProperty(Property.ALIGN_ITEMS, null);

        float occupiedSpace = 0;
        boolean metChildRendererInLine = false;
        for (final FlexItemInfo itemInfo : line) {
            // Split the line
            if (itemInfo.getRenderer() == childRenderer) {
                metChildRendererInLine = true;
                if (childResult.getSplitRenderer() != null) {
                    splitRenderer.addChildRenderer(childResult.getSplitRenderer());
                }

                if (childResult.getOverflowRenderer() != null) {
                    overflowRenderer.addChildRenderer(childResult.getOverflowRenderer());
                }
            } else if (metChildRendererInLine) {
                // Process all following renderers in the current line
                // We have to layout them to understand what goes where
                final Rectangle neighbourBbox = getOccupiedAreaBBox().clone();
                // Move bbox by occupied space
                neighbourBbox.setX(neighbourBbox.getX() + occupiedSpace);
                neighbourBbox.setWidth(itemInfo.getRectangle().getWidth());

                // Y of the renderer has been already calculated, move bbox accordingly
                neighbourBbox.setY(neighbourBbox.getY() - itemInfo.getRectangle().getY());

                final LayoutResult neighbourLayoutResult = itemInfo.getRenderer().layout(new LayoutContext(
                        new LayoutArea(childResult.getOccupiedArea().getPageNumber(), neighbourBbox)));
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
            }

            // X is nonzero only for the 1st renderer in line serving for alignment adjustments
            occupiedSpace += itemInfo.getRectangle().getX() + itemInfo.getRectangle().getWidth();
        }
    }

    private void findMinMaxWidthIfCorrespondingPropertiesAreNotSet(MinMaxWidth minMaxWidth,
                                                                   AbstractWidthHandler minMaxWidthHandler) {
        // TODO DEVSIX-5086 When flex-wrap will be fully supported we'll find min/max width with respect to the lines
        setThisAsParent(getChildRenderers());
        for (final IRenderer childRenderer : getChildRenderers()) {
            MinMaxWidth childMinMaxWidth;
            childRenderer.setParent(this);
            if (childRenderer instanceof AbstractRenderer) {
                childMinMaxWidth = ((AbstractRenderer) childRenderer).getMinMaxWidth();
            } else {
                childMinMaxWidth = MinMaxWidthUtils.countDefaultMinMaxWidth(childRenderer);
            }
            minMaxWidthHandler.updateMaxChildWidth(childMinMaxWidth.getMaxWidth() + minMaxWidth.getMaxWidth());
            minMaxWidthHandler.updateMinChildWidth(childMinMaxWidth.getMinWidth() + minMaxWidth.getMinWidth());
        }
    }

    private static void addSimulateDiv(AbstractRenderer overflowRenderer, float width) {
        final IRenderer fakeOverflowRenderer = new DivRenderer(
                new Div().setMinWidth(width).setMaxWidth(width));
        overflowRenderer.addChildRenderer(fakeOverflowRenderer);
    }
}
