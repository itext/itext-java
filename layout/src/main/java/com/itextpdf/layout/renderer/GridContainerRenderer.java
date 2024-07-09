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
import com.itextpdf.layout.element.GridContainer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.ContinuousContainer;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.grid.GridFlow;
import com.itextpdf.layout.properties.grid.GridValue;
import com.itextpdf.layout.properties.grid.TemplateValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a renderer for a grid.
 */
public class GridContainerRenderer extends BlockRenderer {
    private boolean isFirstLayout = true;
    private float containerHeight = 0.0f;
    private float containerWidth = 0.0f;
    /**
     * Creates a Grid renderer from its corresponding layout object.
     * @param modelElement the {@link GridContainer} which this object should manage
     */
    public GridContainerRenderer(GridContainer modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(GridContainerRenderer.class, this.getClass());
        return new GridContainerRenderer((GridContainer) modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        this.setProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER, Boolean.TRUE);

        Rectangle actualBBox = layoutContext.getArea().getBBox().clone();
        Float blockWidth = retrieveWidth(actualBBox.getWidth());

        ContinuousContainer.setupContinuousContainerIfNeeded(this);
        applyPaddings(actualBBox, false);
        applyBorderBox(actualBBox, false);
        applyMargins(actualBBox, false);
        applyWidth(actualBBox, blockWidth, OverflowPropertyValue.VISIBLE);
        containerWidth = actualBBox.getWidth();

        Float blockHeight = retrieveHeight();
        if (blockHeight != null && (float) blockHeight < actualBBox.getHeight()) {
            actualBBox.setY(actualBBox.getY() + actualBBox.getHeight() - (float) blockHeight);
            actualBBox.setHeight((float) blockHeight);
        }

        Grid grid = constructGrid(this,
                new Rectangle(actualBBox.getWidth(), blockHeight == null ? -1 : actualBBox.getHeight()));
        GridLayoutResult layoutResult = layoutGrid(layoutContext, actualBBox, grid);

        if (layoutResult.getOverflowRenderers().isEmpty()) {
            final ContinuousContainer continuousContainer = this.<ContinuousContainer>getProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER_RESULT);
            continuousContainer.reApplyProperties(this);

            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, true);
            return new LayoutResult(LayoutResult.FULL, this.occupiedArea, null, null);
        } else if (layoutResult.getSplitRenderers().isEmpty()) {
            IRenderer cause = layoutResult.getCauseOfNothing() == null ? this : layoutResult.getCauseOfNothing();
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, cause);
        } else {
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, false);
            return new LayoutResult(LayoutResult.PARTIAL, this.occupiedArea,
                    GridMulticolUtil.createSplitRenderer(layoutResult.getSplitRenderers(), this),
                    createOverflowRenderer(layoutResult.getOverflowRenderers()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        // The grid's items are not affected by the 'float' and 'clear' properties.
        // Still let clear them on renderer level not model element
        renderer.setProperty(Property.FLOAT, null);

        renderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        renderer.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        renderer.setProperty(Property.COLLAPSING_MARGINS, determineCollapsingMargins(renderer));

        GridItemRenderer itemRenderer = new GridItemRenderer();
        itemRenderer.setProperty(Property.COLLAPSING_MARGINS, Boolean.FALSE);
        itemRenderer.addChild(renderer);

        super.addChild(itemRenderer);
    }

    /**
     * Calculates collapsing margins value. It's based on browser behavior.
     * Always returning true somehow also almost works.
     */
    private static Boolean determineCollapsingMargins(IRenderer renderer) {
        IRenderer currentRenderer = renderer;
        while (!currentRenderer.getChildRenderers().isEmpty()) {
            if (currentRenderer.getChildRenderers().size() > 1) {
                return Boolean.TRUE;
            } else {
                currentRenderer = currentRenderer.getChildRenderers().get(0);
            }
        }
        if (currentRenderer instanceof TableRenderer) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private AbstractRenderer createOverflowRenderer(List<IRenderer> children) {
        GridContainerRenderer overflowRenderer = (GridContainerRenderer) getNextRenderer();
        overflowRenderer.isFirstLayout = false;
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        overflowRenderer.setProperty(Property.GRID_TEMPLATE_ROWS, null);
        overflowRenderer.setProperty(Property.GRID_AUTO_ROWS, null);
        overflowRenderer.setChildRenderers(children);
        ContinuousContainer.clearPropertiesFromOverFlowRenderer(overflowRenderer);
        return overflowRenderer;
    }

    //Process cells by doing actual layout on the calculated layout area
    private GridLayoutResult layoutGrid(LayoutContext layoutContext, Rectangle actualBBox, Grid grid) {
        GridLayoutResult layoutResult = new GridLayoutResult();

        int notLayoutedRow = grid.getNumberOfRows();
        for (GridCell cell : grid.getUniqueGridCells(Grid.GridOrder.ROW)) {
            // Calculate cell layout context by getting actual x and y on parent layout area for it
            LayoutContext cellContext = getCellLayoutContext(layoutContext, actualBBox, cell);
            Rectangle cellBBox = cellContext.getArea().getBBox();
            IRenderer cellToRender = cell.getValue();

            // Now set the height for the individual items
            // We know cell height upfront and this way we tell the element what it can occupy
            final float itemHeight = ((GridItemRenderer) cellToRender).calculateHeight(cellBBox.getHeight());

            cellToRender.setProperty(Property.HEIGHT, UnitValue.createPointValue(itemHeight));

            // Adjust cell BBox to the remaining part of the layout bbox
            // This way we can lay out elements partially
            cellBBox.setHeight(cellBBox.getTop() - actualBBox.getBottom())
                    .setY(actualBBox.getY());

            cellToRender.setProperty(Property.FILL_AVAILABLE_AREA, Boolean.TRUE);
            LayoutResult cellResult = cellToRender.layout(cellContext);
            notLayoutedRow = Math.min(notLayoutedRow, processLayoutResult(layoutResult, cell, cellResult));
        }

        for (IRenderer overflowRenderer : layoutResult.getOverflowRenderers()) {
            if (overflowRenderer.<Integer>getProperty(Property.GRID_ROW_START) != null) {
                overflowRenderer.setProperty(Property.GRID_ROW_START,
                        (int) overflowRenderer.<Integer>getProperty(Property.GRID_ROW_START) - notLayoutedRow);
                overflowRenderer.setProperty(Property.GRID_ROW_END,
                        (int) overflowRenderer.<Integer>getProperty(Property.GRID_ROW_END) - notLayoutedRow);
            }
        }

        return layoutResult;
    }

    private static int processLayoutResult(GridLayoutResult layoutResult, GridCell cell, LayoutResult cellResult) {
        IRenderer overflowRenderer = cellResult.getOverflowRenderer();
        if (cellResult.getStatus() == LayoutResult.NOTHING) {
            overflowRenderer.setProperty(Property.GRID_COLUMN_START, cell.getColumnStart() + 1);
            overflowRenderer.setProperty(Property.GRID_COLUMN_END, cell.getColumnEnd() + 1);
            overflowRenderer.setProperty(Property.GRID_ROW_START, cell.getRowStart() + 1);
            overflowRenderer.setProperty(Property.GRID_ROW_END, cell.getRowEnd() + 1);
            layoutResult.getOverflowRenderers().add(overflowRenderer);
            layoutResult.setCauseOfNothing(cellResult.getCauseOfNothing());
            return cell.getRowStart();
        }

        // PARTIAL + FULL result handling
        layoutResult.getSplitRenderers().add(cell.getValue());
        if (cellResult.getStatus() == LayoutResult.PARTIAL) {
            overflowRenderer.setProperty(Property.GRID_COLUMN_START, cell.getColumnStart() + 1);
            overflowRenderer.setProperty(Property.GRID_COLUMN_END, cell.getColumnEnd() + 1);
            int rowStart = cell.getRowStart() + 1;
            int rowEnd = cell.getRowEnd() + 1;
            layoutResult.getOverflowRenderers().add(overflowRenderer);
            // Now let's find out where we split exactly
            float accumulatedRowSize = 0;
            final float layoutedHeight = cellResult.getOccupiedArea().getBBox().getHeight();
            int notLayoutedRow = rowStart - 1;
            for (int i = 0; i < cell.getRowSizes().length; ++i) {
                accumulatedRowSize += cell.getRowSizes()[i];
                if (accumulatedRowSize < layoutedHeight) {
                    ++rowStart;
                    ++notLayoutedRow;
                } else {
                    break;
                }
            }

            // We don't know what to do if rowStart is equal or more than rowEnd
            // Let's not try to guess by just take the 1st available space in a column
            // by leaving nulls for grid-row-start/end
            if (rowEnd > rowStart) {
                overflowRenderer.setProperty(Property.GRID_ROW_START, rowStart);
                overflowRenderer.setProperty(Property.GRID_ROW_END, rowEnd);
            }

            return notLayoutedRow;
        }

        return Integer.MAX_VALUE;
    }

    //Init cell layout context based on a parent context and calculated cell layout area from grid sizing algorithm.
    private static LayoutContext getCellLayoutContext(LayoutContext layoutContext, Rectangle actualBBox, GridCell cell) {
        LayoutArea tempArea = layoutContext.getArea().clone();
        Rectangle cellLayoutArea = cell.getLayoutArea();
        tempArea.getBBox().setX(actualBBox.getX() + cellLayoutArea.getX());
        tempArea.getBBox().setY(actualBBox.getY() + actualBBox.getHeight() - cellLayoutArea.getHeight() - cellLayoutArea.getY());
        tempArea.getBBox().setWidth(actualBBox.getWidth());
        if (cellLayoutArea.getWidth() > 0) {
            tempArea.getBBox().setWidth(cellLayoutArea.getWidth());
        }
        tempArea.getBBox().setHeight(cellLayoutArea.getHeight());

        return new LayoutContext(tempArea, layoutContext.getMarginsCollapseInfo(),
                layoutContext.getFloatRendererAreas(), layoutContext.isClippedHeight());
    }

    // Calculate grid container occupied area based on its width/height properties and cell layout areas
    private LayoutArea calculateContainerOccupiedArea(LayoutContext layoutContext, boolean isFull) {
        LayoutArea area = layoutContext.getArea().clone();
        final Rectangle areaBBox = area.getBBox();

        final float totalContainerHeight = GridMulticolUtil.updateOccupiedHeight(containerHeight, isFull, isFirstLayout, this);
        if (totalContainerHeight < areaBBox.getHeight() || isFull) {
            Float height = retrieveHeight();
            if (height == null) {
                areaBBox.setHeight(totalContainerHeight);
            } else {
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

    // Grid layout algorithm is based on a https://drafts.csswg.org/css-grid/#layout-algorithm
    // This method creates grid, positions items on it and sizes grid tracks
    private static Grid constructGrid(GridContainerRenderer renderer, Rectangle actualBBox) {
        Float columnGapProp = renderer.<Float>getProperty(Property.COLUMN_GAP);
        Float rowGapProp = renderer.<Float>getProperty(Property.ROW_GAP);
        float columnGap = columnGapProp == null ? 0f : (float) columnGapProp;
        float rowGap = rowGapProp == null ? 0f : (float) rowGapProp;

        // Resolving repeats
        GridTemplateResolver rowRepeatResolver = new GridTemplateResolver(actualBBox.getHeight(), rowGap);
        GridTemplateResolver columnRepeatResolver = new GridTemplateResolver(actualBBox.getWidth(), columnGap);
        List<GridValue> templateRows = rowRepeatResolver.resolveTemplate(
                renderer.<List<TemplateValue>>getProperty(Property.GRID_TEMPLATE_ROWS));
        List<GridValue> templateColumns = columnRepeatResolver.resolveTemplate(
                renderer.<List<TemplateValue>>getProperty(Property.GRID_TEMPLATE_COLUMNS));

        final GridFlow flow = renderer.<GridFlow>getProperty(Property.GRID_FLOW) == null ?
                GridFlow.ROW : (GridFlow) (renderer.<GridFlow>getProperty(Property.GRID_FLOW));

        for (IRenderer child : renderer.getChildRenderers()) {
            child.setParent(renderer);
        }

        // 8. Placing Grid Items
        Grid grid = Grid.Builder.forItems(renderer.getChildRenderers())
                        .columns(templateColumns == null ? 0 : templateColumns.size())
                        .rows(templateRows == null ? 0 : templateRows.size())
                        .flow(flow).build();

        // Collapse any empty repeated tracks if auto-fit was used
        if (rowRepeatResolver.isCollapseNullLines()) {
            templateRows = rowRepeatResolver.shrinkTemplatesToFitSize(grid.collapseNullLines(Grid.GridOrder.ROW,
                    rowRepeatResolver.getFixedValuesCount()));
        }
        if (columnRepeatResolver.isCollapseNullLines()) {
            templateColumns = columnRepeatResolver.shrinkTemplatesToFitSize(grid.collapseNullLines(Grid.GridOrder.COLUMN,
                    columnRepeatResolver.getFixedValuesCount()));
        }

        // 12. Grid Layout Algorithm
        GridValue columnAutoWidth = renderer.<GridValue>getProperty(Property.GRID_AUTO_COLUMNS);
        GridValue rowAutoHeight = renderer.<GridValue>getProperty(Property.GRID_AUTO_ROWS);
        GridSizer gridSizer = new GridSizer(grid, templateColumns, templateRows, columnAutoWidth, rowAutoHeight,
                columnGap, rowGap, actualBBox);
        gridSizer.sizeGrid();
        renderer.containerHeight = gridSizer.getContainerHeight();
        return grid;
    }

    private final static class GridLayoutResult {
        private final List<IRenderer> splitRenderers = new ArrayList<>();
        private final List<IRenderer> overflowRenderers = new ArrayList<>();
        private IRenderer causeOfNothing;

        public GridLayoutResult() {
            //default constructor
        }

        public List<IRenderer> getSplitRenderers() {
            return splitRenderers;
        }

        public List<IRenderer> getOverflowRenderers() {
            return overflowRenderers;
        }

        public void setCauseOfNothing(IRenderer causeOfNothing) {
            this.causeOfNothing = causeOfNothing;
        }

        public IRenderer getCauseOfNothing() {
            return causeOfNothing;
        }
    }
}
