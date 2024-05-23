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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.GridContainer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.ContinuousContainer;
import com.itextpdf.layout.properties.GridFlow;
import com.itextpdf.layout.properties.GridValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a renderer for a grid.
 */
public class GridContainerRenderer extends DivRenderer {
    private boolean isFirstLayout = true;

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

        //TODO DEVSIX-8331 enable continuous container, right now its not working properly out of the box because
        // we don't need to enable it for every element in a grid, probably only to those which get
        // split by a page
        //this.setProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER, Boolean.TRUE);

        Rectangle actualBBox = layoutContext.getArea().getBBox().clone();
        Float blockWidth = retrieveWidth(actualBBox.getWidth());
        if (blockWidth != null) {
            actualBBox.setWidth((float) blockWidth);
        }

        ContinuousContainer.setupContinuousContainerIfNeeded(this);
        applyPaddings(actualBBox, false);
        applyBorderBox(actualBBox, false);
        applyMargins(actualBBox, false);

        Grid grid = constructGrid(this, actualBBox);
        GridLayoutResult layoutResult = layoutGrid(layoutContext, actualBBox, grid);

        if (layoutResult.getOverflowRenderers().isEmpty()) {
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, grid, true);
            return new LayoutResult(LayoutResult.FULL, this.occupiedArea, null, null);
        } else if (layoutResult.getSplitRenderers().isEmpty()) {
            IRenderer cause = this;
            if (!layoutResult.getCauseOfNothing().isEmpty()) {
                cause = layoutResult.getCauseOfNothing().get(0);
            }
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, cause);
        } else {
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, grid, false);
            return new LayoutResult(LayoutResult.PARTIAL, this.occupiedArea,
                    createSplitRenderer(layoutResult.getSplitRenderers()),
                    createOverflowRenderer(layoutResult.getOverflowRenderers()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        renderer.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        renderer.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        super.addChild(renderer);
    }

    private AbstractRenderer createSplitRenderer(List<IRenderer> children) {
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

    private AbstractRenderer createOverflowRenderer(List<IRenderer> children) {
        // TODO DEVSIX-8340 - We put the original amount of rows into overflow container.
        GridContainerRenderer overflowRenderer = (GridContainerRenderer) getNextRenderer();
        overflowRenderer.isFirstLayout = false;
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        overflowRenderer.setChildRenderers(children);
        ContinuousContainer.clearPropertiesFromOverFlowRenderer(overflowRenderer);
        return overflowRenderer;
    }

    //Process cells by doing actual layout on the calculated layout area
    private GridLayoutResult layoutGrid(LayoutContext layoutContext, Rectangle actualBBox, Grid grid) {
        GridLayoutResult layoutResult = new GridLayoutResult();

        for (GridCell cell : grid.getUniqueGridCells(Grid.GridOrder.ROW)) {
            // Calculate cell layout context by getting actual x and y on parent layout area for it
            LayoutContext cellContext = getCellLayoutContext(layoutContext, actualBBox, cell);

            IRenderer cellToRender = cell.getValue();
            cellToRender.setProperty(Property.COLLAPSING_MARGINS, Boolean.FALSE);

            // Now set the height for the individual items
            // We know cell height upfront and this way we tell the element what it can occupy
            Rectangle cellBBox = cellContext.getArea().getBBox();
            if (!cellToRender.hasProperty(Property.HEIGHT)) {
                final Rectangle rectangleWithoutBordersMarginsPaddings = cellBBox.clone();
                if (cellToRender instanceof AbstractRenderer) {
                    final AbstractRenderer abstractCellRenderer = ((AbstractRenderer) cellToRender);
                    // We subtract margins/borders/paddings because we should take into account that
                    // borders/paddings/margins should also fit into a cell.
                    if (AbstractRenderer.isBorderBoxSizing(cellToRender)) {
                        abstractCellRenderer.applyMargins(rectangleWithoutBordersMarginsPaddings, false);
                    } else {
                        abstractCellRenderer.applyMarginsBordersPaddings(rectangleWithoutBordersMarginsPaddings, false);
                    }
                }

                cellToRender.setProperty(Property.HEIGHT,
                        UnitValue.createPointValue(rectangleWithoutBordersMarginsPaddings.getHeight()));
            }

            // Adjust cell BBox to the remaining part of the layout bbox
            // This way we can layout elements partially
            cellBBox.setHeight(cellBBox.getTop() - actualBBox.getBottom())
                    .setY(actualBBox.getY());

            LayoutResult cellResult = cellToRender.layout(cellContext);

            if (cellResult.getStatus() == LayoutResult.NOTHING) {
                layoutResult.getOverflowRenderers().add(cellToRender);
                layoutResult.getCauseOfNothing().add(cellResult.getCauseOfNothing());
            } else {
                // PARTIAL + FULL result handling
                layoutResult.getSplitRenderers().add(cellToRender);
                if (cellResult.getStatus() == LayoutResult.PARTIAL) {
                    layoutResult.getOverflowRenderers().add(cellResult.getOverflowRenderer());
                }
            }
        }
        return layoutResult;
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

    //calculate grid container occupied area based on its width/height properties and cell layout areas
    private LayoutArea calculateContainerOccupiedArea(LayoutContext layoutContext, Grid grid, boolean isFull) {
        LayoutArea area = layoutContext.getArea().clone();
        final float totalHeight = updateOccupiedHeight(grid.getHeight(), isFull);
        area.getBBox().setHeight(totalHeight);
        final Rectangle initialBBox = layoutContext.getArea().getBBox();
        area.getBBox().setY(initialBBox.getY() + initialBBox.getHeight() - area.getBBox().getHeight());
        recalculateHeightAndWidthAfterLayout(area.getBBox(), isFull);
        return area;
    }

    private void recalculateHeightAndWidthAfterLayout(Rectangle bBox, boolean isFull) {
        Float height = retrieveHeight();
        if (height != null) {
            height = updateOccupiedHeight((float) height, isFull);
            float heightDelta = bBox.getHeight() - (float) height;
            bBox.moveUp(heightDelta);
            bBox.setHeight((float) height);
        }
        Float blockWidth = retrieveWidth(bBox.getWidth());
        if (blockWidth != null) {
            bBox.setWidth((float) blockWidth);
        }
    }

    private float updateOccupiedHeight(float initialHeight, boolean isFull) {
        if (isFull) {
            initialHeight += safelyRetrieveFloatProperty(Property.PADDING_BOTTOM);
            initialHeight += safelyRetrieveFloatProperty(Property.MARGIN_BOTTOM);
            if (!this.hasOwnProperty(Property.BORDER) || this.<Border>getProperty(Property.BORDER) == null) {
                initialHeight += safelyRetrieveFloatProperty(Property.BORDER_BOTTOM);
            }
        }
        initialHeight += safelyRetrieveFloatProperty(Property.PADDING_TOP);

        initialHeight += safelyRetrieveFloatProperty(Property.MARGIN_TOP);

        if (!this.hasOwnProperty(Property.BORDER) || this.<Border>getProperty(Property.BORDER) == null) {
            initialHeight += safelyRetrieveFloatProperty(Property.BORDER_TOP);
        }

        // isFirstLayout is necessary to handle the case when grid container laid out on more
        // than 2 pages, and on the last page layout result is full, but there is no bottom border
        float TOP_AND_BOTTOM = isFull && isFirstLayout ? 2 : 1;
        //If container laid out on more than 3 pages, then it is a page where there are no bottom and top borders
        if (!isFull && !isFirstLayout) {
            TOP_AND_BOTTOM = 0;
        }
        initialHeight += safelyRetrieveFloatProperty(Property.BORDER) * TOP_AND_BOTTOM;
        return initialHeight;
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

    //Grid layout algorithm is based on a https://drafts.csswg.org/css-grid/#layout-algorithm
    private static Grid constructGrid(GridContainerRenderer renderer, Rectangle actualBBox) {
        List<GridValue> templateColumns = renderer.<List<GridValue>>getProperty(Property.GRID_TEMPLATE_COLUMNS);
        List<GridValue> templateRows = renderer.<List<GridValue>>getProperty(Property.GRID_TEMPLATE_ROWS);
        final GridFlow flow = renderer.<GridFlow>getProperty(Property.GRID_FLOW) == null ?
                GridFlow.ROW : (GridFlow) (renderer.<GridFlow>getProperty(Property.GRID_FLOW));

        for (IRenderer child : renderer.getChildRenderers()) {
            child.setParent(renderer);
        }

        // 8. Placing Grid Items
        Grid grid = Grid.Builder.forItems(renderer.getChildRenderers())
                        .columns(templateColumns == null ? 1 : templateColumns.size())
                        .rows(templateRows == null ? 1 : templateRows.size())
                        .flow(flow).build();


        GridValue columnAutoWidth = renderer.<GridValue>getProperty(Property.GRID_AUTO_COLUMNS);
        GridValue rowAutoHeight = renderer.<GridValue>getProperty(Property.GRID_AUTO_ROWS);
        Float columnGapProp = renderer.<Float>getProperty(Property.COLUMN_GAP);
        Float rowGapProp = renderer.<Float>getProperty(Property.ROW_GAP);
        float columnGap = columnGapProp == null ? 0f : (float) columnGapProp;
        float rowGap = rowGapProp == null ? 0f : (float) rowGapProp;

        // 12. Grid Layout Algorithm
        GridSizer gridSizer = new GridSizer(grid, templateColumns, templateRows, columnAutoWidth, rowAutoHeight,
                columnGap, rowGap, actualBBox);
        gridSizer.sizeGrid();
        return grid;
    }


    private final static class GridLayoutResult {
        private final List<IRenderer> splitRenderers = new ArrayList<>();
        private final List<IRenderer> overflowRenderers = new ArrayList<>();
        private final List<IRenderer> causeOfNothing = new ArrayList<>();

        public GridLayoutResult() {
            //default constructor
        }

        public List<IRenderer> getSplitRenderers() {
            return splitRenderers;
        }

        public List<IRenderer> getOverflowRenderers() {
            return overflowRenderers;
        }

        public List<IRenderer> getCauseOfNothing() {
            return causeOfNothing;
        }
    }
}
