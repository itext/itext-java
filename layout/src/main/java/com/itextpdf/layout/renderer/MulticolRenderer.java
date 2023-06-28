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

    private static final int MAX_RELAYOUT_COUNT = 4;
    private static final float ZERO_DELTA = 0.0001F;

    private BlockRenderer elementRenderer;
    private final HeightEnhancer heightCalculator = new HeightEnhancer();
    private int columnCount;
    private float columnWidth;
    private float approximateHeight;

    /**
     * Creates a DivRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link MulticolContainer} which this object should manage
     */
    public MulticolRenderer(MulticolContainer modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        this.setProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER, Boolean.TRUE);
        final Rectangle actualBBox = layoutContext.getArea().getBBox().clone();
        applyPaddings(actualBBox, false);
        applyBorderBox(actualBBox, false);
        applyMargins(actualBBox, false);

        columnCount = (int) this.<Integer>getProperty(Property.COLUMN_COUNT);
        columnWidth = actualBBox.getWidth() / columnCount;
        if (this.elementRenderer == null) {
            // initialize elementRenderer on first layout when first child represents renderer of element which
            // should be layouted in multicol, because on the next layouts this can have multiple children
            elementRenderer = getElementsRenderer();
        }
        //It is necessary to set parent, because during relayout elementRenderer's parent gets cleaned up
        elementRenderer.setParent(this);
        LayoutResult prelayoutResult = elementRenderer.layout(
                new LayoutContext(new LayoutArea(1, new Rectangle(columnWidth, INF))));
        if (prelayoutResult.getStatus() != LayoutResult.FULL) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, prelayoutResult.getCauseOfNothing());
        }

        approximateHeight = prelayoutResult.getOccupiedArea().getBBox().getHeight() / columnCount;

        MulticolLayoutResult layoutResult = balanceContentAndLayoutColumns(layoutContext, actualBBox);

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

    private MulticolLayoutResult balanceContentAndLayoutColumns(LayoutContext prelayoutContext, Rectangle actualBbox) {
        float additionalHeightPerIteration;
        MulticolLayoutResult result = new MulticolLayoutResult();
        int counter = MAX_RELAYOUT_COUNT + 1;
        float maxHeight = actualBbox.getHeight();
        boolean isLastLayout = false;
        while (counter-- > 0) {
            if (approximateHeight > maxHeight) {
                isLastLayout = true;
                approximateHeight = maxHeight;
            }
            result = layoutColumnsAndReturnOverflowRenderer(prelayoutContext, actualBbox);
            if (result.getOverflowRenderer() == null || isLastLayout) {
                return result;
            }
            additionalHeightPerIteration = heightCalculator.apply(this, result).floatValue();
            if (Math.abs(additionalHeightPerIteration) <= ZERO_DELTA) {
                return result;
            }
            approximateHeight += additionalHeightPerIteration;
        }
        return result;
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
        return area;
    }

    private BlockRenderer getElementsRenderer() {
        if (!(getChildRenderers().size() == 1 && getChildRenderers().get(0) instanceof BlockRenderer)) {
            throw new IllegalStateException("Invalid child renderers, it should be one and be a block element");
        }
        return (BlockRenderer) getChildRenderers().get(0);
    }

    private MulticolLayoutResult layoutColumnsAndReturnOverflowRenderer(LayoutContext preLayoutContext, Rectangle actualBBox) {
        MulticolLayoutResult result = new MulticolLayoutResult();
        IRenderer renderer = elementRenderer;
        for (int i = 0; i < columnCount && renderer != null; i++) {
            LayoutArea tempArea = preLayoutContext.getArea().clone();
            tempArea.getBBox().setWidth(columnWidth);
            tempArea.getBBox().setHeight(approximateHeight);
            tempArea.getBBox().setX(actualBBox.getX() + columnWidth * i);
            tempArea.getBBox().setY(actualBBox.getY() + actualBBox.getHeight() - tempArea.getBBox()
                    .getHeight());

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
        result.setOverflowRenderer((AbstractRenderer)renderer);
        return result;
    }


    /**
     * Represents result of one iteration of MulticolRenderer layouting
     * It contains split renderers which were lauded on a given height and overflow renderer
     * for which height should be increased, so it can be lauded.
     */
    private static class MulticolLayoutResult {
        private List<IRenderer> splitRenderers = new ArrayList<>();
        private AbstractRenderer overflowRenderer;
        private IRenderer causeOfNothing;

        public List<IRenderer> getSplitRenderers() {
            return splitRenderers;
        }

        public AbstractRenderer getOverflowRenderer() {
            return overflowRenderer;
        }

        public IRenderer getCauseOfNothing() {
            return causeOfNothing;
        }

        public void setOverflowRenderer(AbstractRenderer overflowRenderer) {
            this.overflowRenderer = overflowRenderer;
        }

        public void setCauseOfNothing(IRenderer causeOfNothing) {
            this.causeOfNothing = causeOfNothing;
        }
    }

    /**
     * Class which used for additional height calculation
     */
    private static class HeightEnhancer {
        private Float height = null;

        /**
         * Calculate height, by which current height of given {@code MulticolRenderer} should be increased so
         * {@code MulticolLayoutResult#getOverflowRenderer} could be lauded
         *
         * @param renderer multicol renderer for which height needs to be increased
         * @param result result of one iteration of {@code MulticolRenderer} layouting
         * @return height by which current height of given multicol renderer should be increased
         */
        public Float apply(MulticolRenderer renderer, MulticolLayoutResult result) {
            if (height != null) {
                return height;
            }
            if (result.getOverflowRenderer() == null) {
                return 0.0f;
            }
            LayoutResult overflowResult = result.getOverflowRenderer().layout(
                    new LayoutContext(new LayoutArea(1, new Rectangle(renderer.columnWidth, INF))));
            height = overflowResult.getOccupiedArea().getBBox().getHeight() / MAX_RELAYOUT_COUNT;
            return height;
        }
    }
}
