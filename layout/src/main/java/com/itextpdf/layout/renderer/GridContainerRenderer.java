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
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        //TODO DEVSIX-8329 improve nothing processing, consider checking for cause of nothing here?
        //TODO DEVSIX-8329 improve forced placement logic
        if (layoutResult.getSplitRenderers().isEmpty()) {
            if (Boolean.TRUE.equals(this.<Boolean>getProperty(Property.FORCED_PLACEMENT))) {
                this.occupiedArea = calculateContainerOccupiedArea(layoutContext, grid, true);
                return new LayoutResult(LayoutResult.FULL,  this.occupiedArea, this, null);
            }
            IRenderer cause = this;
            if (!layoutResult.getCauseOfNothing().isEmpty()) {
                cause = layoutResult.getCauseOfNothing().get(0);
            }
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, cause);
        } else if (layoutResult.getOverflowRenderers().isEmpty()) {
            final ContinuousContainer continuousContainer = this.<ContinuousContainer>getProperty(
                    Property.TREAT_AS_CONTINUOUS_CONTAINER_RESULT);
            if (continuousContainer != null) {
                continuousContainer.reApplyProperties(this);
            }
            this.childRenderers.clear();
            this.addAllChildRenderers(layoutResult.getSplitRenderers());
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, grid, true);
            return new LayoutResult(LayoutResult.FULL, this.occupiedArea, this, null);
        } else {
            this.occupiedArea = calculateContainerOccupiedArea(layoutContext, grid, false);
            return new LayoutResult(LayoutResult.PARTIAL, this.occupiedArea,
                    createSplitRenderer(layoutResult.getSplitRenderers()),
                    createOverflowRenderer(layoutResult.getOverflowRenderers()));
        }
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
        ensureTemplateValuesFit(grid, actualBBox);
        //TODO DEVSIX-8329 improve forced placement logic, right now if we have a cell which could not be fitted on its
        // area or returns nothing as layout result RootRenderer sets FORCED_PLACEMENT on this class instance.
        // And basically every cell inherits this value and force placed, but we only need to force place cells
        // which were not fitted originally.
        for (GridCell cell : grid.getUniqueGridCells(Grid.ROW_ORDER)) {
            //If cell couldn't fit during cell layout area calculation than we need to put such cell straight to
            //nothing result list
            if (!cell.isValueFitOnCellArea()) {
                layoutResult.getOverflowRenderers().add(cell.getValue());
                layoutResult.getCauseOfNothing().add(cell.getValue());
                continue;
            }
            //Calculate cell layout context by getting actual x and y on parent layout area for it
            LayoutContext cellContext = getCellLayoutContext(layoutContext, actualBBox, cell);
            //We need to check for forced placement here, because otherwise we would infinitely return partial result.
            if (!Boolean.TRUE.equals(this.<Boolean>getProperty(Property.FORCED_PLACEMENT))
                    && !actualBBox.contains(cellContext.getArea().getBBox())) {
                layoutResult.getOverflowRenderers().add(cell.getValue());
                continue;
            }

            IRenderer cellToRender = cell.getValue();
            cellToRender.setProperty(Property.FILL_AVAILABLE_AREA, true);
            cellToRender.setProperty(Property.COLLAPSING_MARGINS, Boolean.FALSE);
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

    private void ensureTemplateValuesFit(Grid grid, Rectangle actualBBox) {
        if (grid.getMinWidth() > actualBBox.getWidth()) {
            actualBBox.setWidth(grid.getMinWidth());
        }
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

    //TODO DEVSIX-8330: Probably height also has to be calculated before layout and set into actual bbox
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

    //TODO DEVSIX-8330: Consider extracting this method and same from MulticolRenderer to a separate class
    // or derive GridRenderer and MulticolRenderer from one class which will manage this and isFirstLayout field
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
    //It's not a 1 to 1 implementation and Grid Sizing Algorithm differs a little bit
    //TODO DEVSIX-8324 Left actualBBox parameter since it will be needed for fr and % calculations
    // if it is inline-grid, than it won't be needed.
    private static Grid constructGrid(GridContainerRenderer renderer, Rectangle actualBBox) {
        //TODO DEVSIX-8324 create a new class GridTemplateValue, which will store fr, pt, %, minmax, etc. values
        //TODO DEVSIX-8324 Use this new class instead of Float and use it inside Grid Sizing Algorithm
        //TODO DEVSIX-8324 Right now we're assuming that all template values are points, and there is no % and fr in this list
        List<Float> templateColumns = processTemplateValues(renderer.<List<UnitValue>>getProperty(Property.GRID_TEMPLATE_COLUMNS));
        List<Float> templateRows = processTemplateValues(renderer.<List<UnitValue>>getProperty(Property.GRID_TEMPLATE_ROWS));

        Float columnAutoWidth = renderer.<UnitValue>getProperty(Property.GRID_AUTO_COLUMNS) == null ?
                null : (Float) ((UnitValue)renderer.<UnitValue>getProperty(Property.GRID_AUTO_COLUMNS)).getValue();
        Float rowAutoHeight = renderer.<UnitValue>getProperty(Property.GRID_AUTO_ROWS) == null ?
                null : (Float) ((UnitValue)renderer.<UnitValue>getProperty(Property.GRID_AUTO_ROWS)).getValue();

        //Grid Item Placement Algorithm
        int initialRowsCount = templateRows == null ? 1 : templateRows.size();
        int initialColumnsCount = templateColumns == null ? 1 : templateColumns.size();

        //Sort cells to first position anything that’s not auto-positioned, then process the items locked to a given row.
        //It would be better to use tree set here, but not using it due to .NET porting issues
        List<GridCell> sortedCells = new ArrayList<>();
        for (IRenderer child : renderer.getChildRenderers()) {
            sortedCells.add(new GridCell(child));
        }
        Collections.sort(sortedCells, new CellComparator());

        //Find a cell with a max column end to ensure it will fit on the grid
        for (GridCell cell : sortedCells) {
            if (cell != null) {
                initialColumnsCount = Math.max(initialColumnsCount, cell.getColumnEnd());
            }
        }

        final Grid grid = new Grid(initialRowsCount, initialColumnsCount, false);

        for (GridCell cell : sortedCells) {
            cell.getValue().setParent(renderer);
            grid.addCell(cell);
        }
        //TODO DEVSIX-8325 eliminate null rows/columns
        // for rows it's easy: grid.getCellsRows().removeIf(row -> row.stream().allMatch(cell -> cell == null));
        // shrinkNullAxis(grid);
        GridSizer gridSizer = new GridSizer(grid, templateRows, templateColumns, rowAutoHeight, columnAutoWidth);
        gridSizer.sizeCells();
        //calculating explicit height to ensure that even empty rows which covered by template would be considered
        //TODO DEVSIX-8324 improve those methods in future for working correctly with minmax/repeat/etc.
        setGridContainerMinimalHeight(grid, templateRows);
        setGridContainerMinimalWidth(grid, templateColumns);
        return grid;
    }

    //TODO DEVSIX-8324 This is temporary method, we should remove it and instead of having Property.GRID_TEMPLATE_...
    // as a UnitValue and returning list of Float, we need a new class which will be passed to Grid Sizing Algorithm.
    private static List<Float> processTemplateValues(List<UnitValue> template) {
        if (template == null) {
            return null;
        }
        return template.stream().map(value -> value.getValue()).collect(Collectors.toList());
    }

    //This method calculates container minimal height, because if number of cells is not enough to fill all specified
    //rows by template than we need to set the height of the container higher than it's actual occupied height.
    private static void setGridContainerMinimalHeight(Grid grid, List<Float> templateRows) {
        float explicitContainerHeight = 0.0f;
        if (templateRows != null) {
            for (Float template : templateRows) {
                explicitContainerHeight += (float) template;
            }
        }
        grid.setMinHeight(explicitContainerHeight);
    }

    private static void setGridContainerMinimalWidth(Grid grid, List<Float> templateColumns) {
        float explicitContainerWidth = 0.0f;
        if (templateColumns != null) {
            for (Float template : templateColumns) {
                explicitContainerWidth += (float) template;
            }
        }
        grid.setMinWidth(explicitContainerWidth);
    }

    /**
     * This comparator sorts cells so ones with both fixed row and column positions would go first,
     * then cells with fixed row and then cells without such properties.
     */
    private final static class CellComparator implements Comparator<GridCell> {

        @Override
        public int compare(GridCell lhs, GridCell rhs) {
            int lhsModifiers = 0;
            if (lhs.getColumnStart() != -1 && lhs.getRowStart() != -1) {
                lhsModifiers = 2;
            } else if (lhs.getRowStart() != -1) {
                lhsModifiers = 1;
            }

            int rhsModifiers = 0;
            if (rhs.getColumnStart() != -1 && rhs.getRowStart() != -1) {
                rhsModifiers = 2;
            } else if (rhs.getRowStart() != -1) {
                rhsModifiers = 1;
            }
            //passing parameters in reversed order so ones with properties would come first
            return Integer.compare(rhsModifiers, lhsModifiers);
        }
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
