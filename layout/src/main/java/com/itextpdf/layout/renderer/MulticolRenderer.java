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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.Border.Side;
import com.itextpdf.layout.element.MulticolContainer;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.ContinuousContainer;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    private float containerWidth;

    private boolean isFirstLayout = true;

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
        setOverflowForAllChildren(this);
        Rectangle actualBBox = layoutContext.getArea().getBBox().clone();
        float originalWidth = actualBBox.getWidth();

        ContinuousContainer.setupContinuousContainerIfNeeded(this);
        applyPaddings(actualBBox, false);
        applyBorderBox(actualBBox, false);
        applyMargins(actualBBox, false);
        applyWidth(actualBBox, originalWidth);
        containerWidth = actualBBox.getWidth();

        calculateColumnCountAndWidth(containerWidth);

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
            for (IRenderer child : elementRenderer.getChildRenderers()) {
                child.setParent(elementRenderer);
            }
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, layoutResult.getCauseOfNothing());
        } else if (layoutResult.getOverflowRenderer() == null) {
            final ContinuousContainer continuousContainer = this.<ContinuousContainer>getProperty(
                    Property.TREAT_AS_CONTINUOUS_CONTAINER_RESULT);
            if (continuousContainer != null) {
                continuousContainer.reApplyProperties(this);
            }

            this.childRenderers.clear();
            addAllChildRenderers(layoutResult.getSplitRenderers());
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, true);
            return new LayoutResult(LayoutResult.FULL, this.occupiedArea, this, null);
        } else {
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, false);
            return new LayoutResult(LayoutResult.PARTIAL, this.occupiedArea,
                    GridMulticolUtil.createSplitRenderer(layoutResult.getSplitRenderers(), this),
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
     * Performs the drawing operation for the border of this renderer, if
     * defined by any of the {@link Property#BORDER} values in either the layout
     * element or this {@link IRenderer} itself.
     *
     * @param drawContext the context (canvas, document, etc) of this drawing operation.
     */
    @Override
    public void drawBorder(DrawContext drawContext) {
        super.drawBorder(drawContext);

        Rectangle borderRect = applyMargins(occupiedArea.getBBox().clone(), getMargins(), false);
        boolean isAreaClipped = clipBorderArea(drawContext, borderRect);
        Border gap = this.<Border>getProperty(Property.COLUMN_GAP_BORDER);
        if (getChildRenderers().isEmpty() || gap == null || gap.getWidth() <= ZERO_DELTA) {
            return;
        }

        drawTaggedWhenNeeded(drawContext, canvas -> {
            for (int i = 0; i < getChildRenderers().size() - 1; ++i) {
                Rectangle columnBBox = getChildRenderers().get(i).getOccupiedArea().getBBox();
                Rectangle columnSpaceBBox = new Rectangle(columnBBox.getX() + columnBBox.getWidth(), columnBBox.getY(),
                        columnGap, columnBBox.getHeight());
                float x1 = columnSpaceBBox.getX() + columnSpaceBBox.getWidth() / 2 + gap.getWidth() / 2;
                float y1 = columnSpaceBBox.getY();
                float y2 = columnSpaceBBox.getY() + columnSpaceBBox.getHeight();
                gap.draw(canvas, x1, y1, x1, y2, Side.RIGHT, 0, 0);
            }
            if (isAreaClipped) {
                drawContext.getCanvas().restoreState();
            }
        });
    }

    /**
     * Layouts multicol in the passed area.
     *
     * @param layoutContext the layout context
     * @param actualBBox the area to layout multicol on
     *
     * @return the {@link MulticolLayoutResult} instance
     */
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
     * Creates an overflow renderer.
     *
     * @param overflowedContentRenderer an overflowed content renderer
     *
     * @return a new {@link AbstractRenderer} instance
     */
    protected AbstractRenderer createOverflowRenderer(IRenderer overflowedContentRenderer) {
        MulticolRenderer overflowRenderer = (MulticolRenderer) getNextRenderer();
        overflowRenderer.isFirstLayout = false;
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        List<IRenderer> children = new ArrayList<>(1);
        children.add(overflowedContentRenderer);
        overflowRenderer.setChildRenderers(children);
        ContinuousContainer.clearPropertiesFromOverFlowRenderer(overflowRenderer);
        return overflowRenderer;
    }

    private void setOverflowForAllChildren(IRenderer renderer) {
        if (renderer == null || renderer instanceof AreaBreakRenderer) {
            return;
        }
        renderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        for (IRenderer child : renderer.getChildRenderers()) {
            setOverflowForAllChildren(child);
        }
    }
    private void drawTaggedWhenNeeded(DrawContext drawContext, Consumer<PdfCanvas> action) {
        PdfCanvas canvas = drawContext.getCanvas();
        if (drawContext.isTaggingEnabled()) {
            canvas.openTag(new CanvasArtifact());
        }
        action.accept(canvas);
        if (drawContext.isTaggingEnabled()) {
            canvas.closeTag();
        }
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

    // Algorithm is based on pseudo algorithm from https://www.w3.org/TR/css-multicol-1/#propdef-column-span
    private void calculateColumnCountAndWidth(float initialWidth) {
        final Integer columnCountTemp = (Integer)this.<Integer>getProperty(Property.COLUMN_COUNT);
        final Float columnWidthTemp = (Float)this.<Float>getProperty(Property.COLUMN_WIDTH);

        final Float columnGapTemp = (Float)this.<Float>getProperty(Property.COLUMN_GAP);
        this.columnGap = columnGapTemp == null ? 0f : columnGapTemp.floatValue();
        if ((columnCountTemp == null && columnWidthTemp == null)
                || (columnCountTemp != null && columnCountTemp.intValue() < 0)
                || (columnWidthTemp != null && columnWidthTemp.floatValue() < 0)
                || (this.columnGap < 0)) {

            throw new IllegalStateException(LayoutExceptionMessageConstant.INVALID_COLUMN_PROPERTIES);
        }

        if (columnWidthTemp == null) {
            this.columnCount = columnCountTemp.intValue();
        } else if (columnCountTemp == null) {
            final float columnWidthPlusGap = columnWidthTemp.floatValue() + this.columnGap;
            if (columnWidthPlusGap > ZERO_DELTA) {
                this.columnCount = Math.max(1,
                        (int) Math.floor((double) ((initialWidth + this.columnGap) / columnWidthPlusGap)));
            } else {
                this.columnCount = 1;
            }
        } else {
            final float columnWidthPlusGap = columnWidthTemp.floatValue() + this.columnGap;
            if (columnWidthPlusGap > ZERO_DELTA) {
                this.columnCount = Math.min((int) columnCountTemp,
                        Math.max(1, (int) Math.floor((double) ((initialWidth + this.columnGap) / columnWidthPlusGap))));
            } else {
                this.columnCount = 1;
            }
        }
        this.columnWidth = Math.max(0.0f, ((initialWidth + this.columnGap) / this.columnCount - this.columnGap));
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
        final Rectangle areaBBox = area.getBBox();

        final float totalContainerHeight = GridMulticolUtil.updateOccupiedHeight(approximateHeight, isFull, isFirstLayout, this);
        if (totalContainerHeight < areaBBox.getHeight() || isFull) {
            areaBBox.setHeight(totalContainerHeight);
            Float height = determineHeight(areaBBox);
            if (height != null) {
                height = GridMulticolUtil.updateOccupiedHeight((float) height, isFull, isFirstLayout, this);
                areaBBox.setHeight((float) height);
            }
        }

        final Rectangle initialBBox = layoutContext.getArea().getBBox();
        areaBBox.setY(initialBBox.getY() + initialBBox.getHeight() - areaBBox.getHeight());

        final float totalContainerWidth = GridMulticolUtil.updateOccupiedWidth(containerWidth, this);
        areaBBox.setWidth(totalContainerWidth);

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
            float overflowHeight = overflowResult.getOccupiedArea().getBBox().getHeight();
            if (result.getSplitRenderers().isEmpty()) {
                // In case when first child of content bigger or wider than column and in first layout NOTHING is
                // returned. In that case content again layouted in infinity area without keeping in mind that some
                // approximateHeight already exist.
                overflowHeight -= renderer.approximateHeight;
            }
            height = overflowHeight / maxRelayoutCount;
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
