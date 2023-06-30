/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.MulticolContainer;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.ContinuousContainer;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a renderer for columns.
 */
public class MulticolRenderer extends AbstractRenderer {

    private static final float ZERO_DELTA = 0.0001F;
    private ColumnHeightCalculator heightCalculator;
    private BlockRenderer elementRenderer;
    private int columnCount;
    private float columnWidth;
    private float approximateHeight;
    private Float heightFromProperties;
    private float columnGap;

    /**
     * Creates a DivRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link MulticolContainer} which this object should manage
     */
    public MulticolRenderer(MulticolContainer modelElement) {
        super(modelElement);
        setHeightCalculator(new LayoutInInfiniteHeightCalculator());
    }

    /**
     * Sets the height calculator to be used by this renderer.
     *
     * @param heightCalculator the height calculator to be used by this renderer.
     */
    public final void setHeightCalculator(ColumnHeightCalculator heightCalculator) {
        this.heightCalculator = heightCalculator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        this.setProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER, Boolean.TRUE);

        Rectangle actualBBox = layoutContext.getArea().getBBox().clone();
        float originalWidth = actualBBox.getWidth();
        applyWidth(actualBBox, originalWidth);
        applyPaddings(actualBBox, false);
        applyBorderBox(actualBBox, false);
        applyMargins(actualBBox, false);
        calculateColumnCountAndWidth(actualBBox.getWidth());

        heightFromProperties = determineHeight(actualBBox);
        if (this.elementRenderer == null) {
            // initialize elementRenderer on first layout when first child represents renderer of element which
            // should be layouted in multicol, because on the next layouts this can have multiple children
            elementRenderer = getElementsRenderer();
        }
        //It is necessary to set parent, because during relayout elementRenderer's parent gets cleaned up
        elementRenderer.setParent(this);

        final MulticolLayoutResult layoutResult = layoutInColumns(layoutContext, actualBBox);

        if (layoutResult.getSplitRenderers().isEmpty()) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, layoutResult.getCauseOfNothing());
        } else if (layoutResult.getOverflowRenderer() == null) {
            this.childRenderers.clear();
            addAllChildRenderers(layoutResult.getSplitRenderers());
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, true);
            return new LayoutResult(LayoutResult.FULL, this.occupiedArea, this, null);
        } else {
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, false);
            return new LayoutResult(LayoutResult.PARTIAL, this.occupiedArea,
                    createSplitRenderer(layoutResult.getSplitRenderers()),
                    createOverflowRenderer(layoutResult.getOverflowRenderer()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(MulticolRenderer.class, this.getClass());
        return new MulticolRenderer((MulticolContainer) modelElement);
    }

    protected MulticolLayoutResult layoutInColumns(LayoutContext layoutContext, Rectangle actualBBox) {
        LayoutResult inifiniteHeighOneColumnLayoutResult = elementRenderer.layout(
                new LayoutContext(new LayoutArea(1, new Rectangle(columnWidth, INF))));
        if (inifiniteHeighOneColumnLayoutResult.getStatus() != LayoutResult.FULL) {
            final MulticolLayoutResult result = new MulticolLayoutResult();
            result.setCauseOfNothing(inifiniteHeighOneColumnLayoutResult.getCauseOfNothing());
            return result;
        }

        approximateHeight = inifiniteHeighOneColumnLayoutResult.getOccupiedArea().getBBox().getHeight() / columnCount;
        return balanceContentAndLayoutColumns(layoutContext, actualBBox);
    }

    /**
     * Creates a split renderer.
     *
     * @param children children of the split renderer
     *
     * @return a new {@link AbstractRenderer} instance
     */
    protected AbstractRenderer createSplitRenderer(List<IRenderer> children) {
        AbstractRenderer splitRenderer = (AbstractRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.setChildRenderers(children);
        splitRenderer.addAllProperties(getOwnProperties());
        ContinuousContainer.setupContinuousContainerIfNeeded(splitRenderer);
        return splitRenderer;
    }

    /**
     * Creates an overflow renderer.
     *
     * @param overflowedContentRenderer an overflowed content renderer
     *
     * @return a new {@link AbstractRenderer} instance
     */
    protected AbstractRenderer createOverflowRenderer(IRenderer overflowedContentRenderer) {
        AbstractRenderer overflowRenderer = (AbstractRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        List<IRenderer> children = new ArrayList<>(1);
        children.add(overflowedContentRenderer);
        overflowRenderer.setChildRenderers(children);
        ContinuousContainer.clearPropertiesFromOverFlowRenderer(overflowRenderer);
        return overflowRenderer;
    }

    private void applyWidth(Rectangle parentBbox, float originalWidth) {
        final Float blockWidth = retrieveWidth(originalWidth);
        if (blockWidth != null) {
            parentBbox.setWidth((float) blockWidth);
        } else {
            final Float minWidth = retrieveMinWidth(parentBbox.getWidth());
            if (minWidth != null && minWidth > parentBbox.getWidth()) {
                parentBbox.setWidth((float) minWidth);
            }
        }
    }

    private Float determineHeight(Rectangle parentBBox) {
        Float height = retrieveHeight();
        final Float minHeight = retrieveMinHeight();
        final Float maxHeight = retrieveMaxHeight();
        if (height == null || (minHeight != null && height < minHeight)) {
            if ((minHeight != null) && parentBBox.getHeight() < minHeight) {
                height = minHeight;
            }
        }
        if (height != null && maxHeight != null && height > maxHeight) {
            height = maxHeight;
        }
        return height;
    }


    private void recalculateHeightWidthAfterLayouting(Rectangle parentBBox) {
        Float height = determineHeight(parentBBox);
        if (height != null) {
            float heightDelta = parentBBox.getHeight() - (float) height;
            parentBBox.moveUp(heightDelta);
            parentBBox.setHeight((float) height);
        }
        applyWidth(parentBBox, parentBBox.getWidth());
    }


    private float safelyRetrieveFloatProperty(int property) {
        final Object value = this.<Object>getProperty(property);
        if (value instanceof UnitValue) {
            return ((UnitValue) value).getValue();
        }
        if (value instanceof Border) {
            return ((Border) value).getWidth();
        }
        return 0F;
    }

    private MulticolLayoutResult balanceContentAndLayoutColumns(LayoutContext prelayoutContext,
            Rectangle actualBbox) {
        float additionalHeightPerIteration;
        MulticolLayoutResult result = new MulticolLayoutResult();
        int counter = heightCalculator.maxAmountOfRelayouts() + 1;
        float maxHeight = actualBbox.getHeight();
        boolean isLastLayout = false;
        while (counter-- > 0) {
            if (approximateHeight > maxHeight) {
                isLastLayout = true;
                approximateHeight = maxHeight;
            }
            // height calcultion
            float workingHeight = approximateHeight;
            if (heightFromProperties != null) {
                workingHeight = Math.min((float) heightFromProperties, (float) approximateHeight);
                workingHeight -= safelyRetrieveFloatProperty(Property.PADDING_TOP);
                workingHeight -= safelyRetrieveFloatProperty(Property.PADDING_BOTTOM);
                workingHeight -= safelyRetrieveFloatProperty(Property.BORDER_TOP);
                workingHeight -= safelyRetrieveFloatProperty(Property.BORDER_BOTTOM);
                workingHeight -= safelyRetrieveFloatProperty(Property.BORDER) * 2;
                workingHeight -= safelyRetrieveFloatProperty(Property.MARGIN_TOP);
                workingHeight -= safelyRetrieveFloatProperty(Property.MARGIN_BOTTOM);
            }
            result = layoutColumnsAndReturnOverflowRenderer(prelayoutContext, actualBbox, workingHeight);

            if (result.getOverflowRenderer() == null || isLastLayout) {
                clearOverFlowRendererIfNeeded(result);
                return result;
            }
            additionalHeightPerIteration = heightCalculator.getAdditionalHeightOfEachColumn(this, result).floatValue();
            if (Math.abs(additionalHeightPerIteration) <= ZERO_DELTA) {
                clearOverFlowRendererIfNeeded(result);
                return result;
            }
            approximateHeight += additionalHeightPerIteration;
            clearOverFlowRendererIfNeeded(result);
        }
        return result;
    }

    //algorithm is based on pseudo algorithm from https://www.w3.org/TR/css-multicol-1/#propdef-column-span
    private void calculateColumnCountAndWidth(float initialWidth) {
        final Integer columnCount = (Integer)this.<Integer>getProperty(Property.COLUMN_COUNT);
        final Float columnWidth = (Float)this.<Float>getProperty(Property.COLUMN_WIDTH);
        final Float columnGap = (Float)this.<Float>getProperty(Property.COLUMN_GAP);
        this.columnGap = columnGap != null ? columnGap.floatValue() : 0;
        if ((columnCount == null && columnWidth == null)
            || (columnCount != null && columnCount.intValue() < 0)
            || (columnWidth != null && columnWidth.floatValue() < 0)) {
            throw new IllegalStateException(LayoutExceptionMessageConstant.INVALID_COLUMN_PROPERTIES);
        }
        if (columnWidth == null) {
            this.columnCount = columnCount.intValue();
        } else if (columnCount == null) {
            this.columnCount = Math.max(1, (int) Math.floor((double)((initialWidth + this.columnGap)
                    / (columnWidth.floatValue() + this.columnGap))));
        } else {
            this.columnCount = Math.min((int) columnCount,
                    Math.max(1, (int) Math.floor((double) ((initialWidth + this.columnGap)
                            / (columnWidth.floatValue() + this.columnGap)))));
        }
        this.columnWidth = Math.max(0.0f, ((initialWidth + this.columnGap)/this.columnCount - this.columnGap));
    }

    private void clearOverFlowRendererIfNeeded(MulticolLayoutResult result) {
        //When we have a height set on the element but the content doesn't fit in the given height
        //we don't want to render the overflow renderer as it would be rendered in the next area
        if (heightFromProperties != null && heightFromProperties < approximateHeight) {
            result.setOverflowRenderer(null);
        }
    }


    private LayoutArea calculateContainerOccupiedArea(LayoutContext layoutContext, boolean isFull) {
        LayoutArea area = layoutContext.getArea().clone();
        float totalHeight = approximateHeight;

        if (isFull) {
            totalHeight += safelyRetrieveFloatProperty(Property.PADDING_BOTTOM);
            totalHeight += safelyRetrieveFloatProperty(Property.MARGIN_BOTTOM);
            totalHeight += safelyRetrieveFloatProperty(Property.BORDER_BOTTOM);
        }
        totalHeight += safelyRetrieveFloatProperty(Property.PADDING_TOP);

        totalHeight += safelyRetrieveFloatProperty(Property.MARGIN_TOP);

        totalHeight += safelyRetrieveFloatProperty(Property.BORDER_TOP);
        final float TOP_AND_BOTTOM = isFull ? 2 : 1;

        totalHeight += safelyRetrieveFloatProperty(Property.BORDER) * TOP_AND_BOTTOM;

        area.getBBox().setHeight(totalHeight);
        final Rectangle initialBBox = layoutContext.getArea().getBBox();
        area.getBBox().setY(initialBBox.getY() + initialBBox.getHeight() - area.getBBox().getHeight());
        recalculateHeightWidthAfterLayouting(area.getBBox());
        return area;
    }

    private BlockRenderer getElementsRenderer() {
        if (!(getChildRenderers().size() == 1 && getChildRenderers().get(0) instanceof BlockRenderer)) {
            throw new IllegalStateException("Invalid child renderers, it should be one and be a block element");
        }
        return (BlockRenderer) getChildRenderers().get(0);
    }

    private MulticolLayoutResult layoutColumnsAndReturnOverflowRenderer(LayoutContext preLayoutContext,
            Rectangle actualBBox, float workingHeight) {
        MulticolLayoutResult result = new MulticolLayoutResult();
        IRenderer renderer = elementRenderer;

        for (int i = 0; i < columnCount && renderer != null; i++) {
            LayoutArea tempArea = preLayoutContext.getArea().clone();
            tempArea.getBBox().setWidth(columnWidth);
            tempArea.getBBox().setHeight(workingHeight);
            tempArea.getBBox().setX(actualBBox.getX() + (columnWidth + columnGap) * i);
            tempArea.getBBox().setY(actualBBox.getY() + actualBBox.getHeight() - tempArea.getBBox().getHeight());

            LayoutContext columnContext = new LayoutContext(tempArea, preLayoutContext.getMarginsCollapseInfo(),
                    preLayoutContext.getFloatRendererAreas(), preLayoutContext.isClippedHeight());
            renderer.setProperty(Property.COLLAPSING_MARGINS, false);
            LayoutResult tempResultColumn = renderer.layout(columnContext);
            if (tempResultColumn.getStatus() == LayoutResult.NOTHING) {
                result.setOverflowRenderer((AbstractRenderer) renderer);
                result.setCauseOfNothing(tempResultColumn.getCauseOfNothing());
                return result;
            }

            if (tempResultColumn.getSplitRenderer() == null) {
                result.getSplitRenderers().add(renderer);
            } else {
                result.getSplitRenderers().add(tempResultColumn.getSplitRenderer());
            }
            renderer = tempResultColumn.getOverflowRenderer();
        }
        result.setOverflowRenderer((AbstractRenderer) renderer);
        return result;
    }


    /**
     * Interface which used for additional height calculation
     */
    public interface ColumnHeightCalculator {


        /**
         * Calculate height, by which current height of given {@code MulticolRenderer} should be increased so
         * {@code MulticolLayoutResult#getOverflowRenderer} could be lauded
         *
         * @param renderer multicol renderer for which height needs to be increased
         * @param result   result of one iteration of {@code MulticolRenderer} layouting
         *
         * @return height by which current height of given multicol renderer should be increased
         */
        Float getAdditionalHeightOfEachColumn(MulticolRenderer renderer, MulticolLayoutResult result);

        int maxAmountOfRelayouts();
    }

    /**
     * Represents result of one iteration of MulticolRenderer layouting
     * It contains split renderers which were lauded on a given height and overflow renderer
     * for which height should be increased, so it can be lauded.
     */
    public static class MulticolLayoutResult {
        private List<IRenderer> splitRenderers = new ArrayList<>();
        private AbstractRenderer overflowRenderer;
        private IRenderer causeOfNothing;

        public List<IRenderer> getSplitRenderers() {
            return splitRenderers;
        }

        public AbstractRenderer getOverflowRenderer() {
            return overflowRenderer;
        }

        public void setOverflowRenderer(AbstractRenderer overflowRenderer) {
            this.overflowRenderer = overflowRenderer;
        }

        public IRenderer getCauseOfNothing() {
            return causeOfNothing;
        }

        public void setCauseOfNothing(IRenderer causeOfNothing) {
            this.causeOfNothing = causeOfNothing;
        }
    }

    public static class LayoutInInfiniteHeightCalculator implements ColumnHeightCalculator {

        protected int maxRelayoutCount = 4;
        private Float height = null;

        public Float getAdditionalHeightOfEachColumn(MulticolRenderer renderer, MulticolLayoutResult result) {
            if (height != null) {
                return height;
            }
            if (result.getOverflowRenderer() == null) {
                return 0.0f;
            }
            LayoutResult overflowResult = result.getOverflowRenderer().layout(
                    new LayoutContext(new LayoutArea(1, new Rectangle(renderer.columnWidth, INF))));
            height = overflowResult.getOccupiedArea().getBBox().getHeight() / maxRelayoutCount;
            return height;
        }

        /**
         * @return maximum amount of relayouts which can be done by this height enhancer
         */
        @Override
        public int maxAmountOfRelayouts() {
            return maxRelayoutCount;
        }
    }
}
