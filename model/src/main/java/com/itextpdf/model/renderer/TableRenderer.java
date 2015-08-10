package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Table;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TableRenderer extends AbstractRenderer {

    List<CellRenderer[]> rows = new ArrayList<>();
    // Row range of the current renderer. For large tables it may contain only a few rows.
    Table.RowRange rowRange;

    public TableRenderer(Table modelElement, Table.RowRange rowRange) {
        super(modelElement);
        this.rowRange = rowRange;
        for (int row = rowRange.getStartRow(); row <= rowRange.getFinishRow(); row++) {
            rows.add(new CellRenderer[modelElement.getNumberOfColumns()]);
        }
    }

    @Override
    public void addChild(IRenderer renderer) {
        if (renderer instanceof CellRenderer) {
            renderer.setParent(this);
            // In case rowspan or colspan save cell into bottom left corner.
            // In in this case it will be easier handle row heights in case rowspan.
            Cell cell = (Cell) renderer.getModelElement();
            rows.get(cell.getRow() - rowRange.getStartRow() + cell.getRowspan() - 1)[cell.getCol()] = (CellRenderer) renderer;
        } else {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.error("Only BlockRenderer with Cell model element could be added");
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox();
        Table tableModel = (Table) getModelElement();

        float[] columnWidths = calculateScaledColumnWidths(tableModel, layoutBox.getWidth());

        ArrayList<Float> heights = new ArrayList<>();
        occupiedArea = new LayoutArea(area.getPageNumber(), layoutBox.clone());
        occupiedArea.getBBox().moveUp(occupiedArea.getBBox().getHeight());
        occupiedArea.getBBox().setHeight(0);
        occupiedArea.getBBox().setWidth(tableModel.getWidth());
        LayoutResult[] splits = new LayoutResult[tableModel.getNumberOfColumns()];
        // This represents the target row index for the overflow renderer to be placed to.
        // Usually this is just the current row id of a cell, but it has valuable meaning when a cell has rowspan.
        int[] targetOverflowRowIndex = new int[tableModel.getNumberOfColumns()];

        for (int row = 0; row < rows.size(); row++) {
            CellRenderer[] currentRow = rows.get(row);
            float rowHeight = 0;
            boolean split = false;
            // Indicates that all the cells fit (at least partially after splitting if not forbidden by keepTogether) in the current row.
            boolean hasContent = true;
            // Indicates that we have added a cell from the future, i.e. a cell which has a big rowspan and we shouldn't have
            // added it yet, because we add a cell with rowspan only during the processing of the very last row this cell occupied,
            // but now we have area break and we had to force that cell addition.
            boolean cellWithBigRowspanAdded = false;
            ArrayList<CellRenderer> currChildRenderers = new ArrayList<>();
            // Process in a queue, because we might need to add a cell from the future, i.e. having big rowspan in case of split.
            Queue<CellRendererInfo> cellProcessingQueue = new LinkedList<>();
            for (int col = 0; col < currentRow.length; col++) {
                if (currentRow[col] != null) {
                    cellProcessingQueue.add(new CellRendererInfo(currentRow[col], col, row));
                }
            }
            while (!cellProcessingQueue.isEmpty()) {
                CellRendererInfo currentCellInfo = cellProcessingQueue.poll();
                int col = currentCellInfo.column;
                CellRenderer cell = currentCellInfo.cellRenderer;
                targetOverflowRowIndex[col] = currentCellInfo.finishRowInd;
                boolean currentCellHasBigRowspan = (row != currentCellInfo.finishRowInd);

                currChildRenderers.add(cell);
                int colspan = cell.getPropertyAsInteger(Property.COLSPAN);
                int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                float cellWidth = 0, colOffset = 0;
                for (int i = col; i < col + colspan; i++) {
                    cellWidth += columnWidths[i];
                }
                for (int i = 0; i < col; i++) {
                    colOffset += columnWidths[i];
                }
                float rowspanOffset = 0;
                for (int i = row - 1; i > currentCellInfo.finishRowInd - rowspan && i >= 0; i--) {
                    rowspanOffset += heights.get(i);
                }
                float cellLayoutBoxHeight = rowspanOffset + (!currentCellHasBigRowspan || hasContent ? layoutBox.getHeight() : 0);
                float cellLayoutBoxBottom = layoutBox.getY() + (!currentCellHasBigRowspan || hasContent ? 0 : layoutBox.getHeight());
                Rectangle cellLayoutBox = new Rectangle(layoutBox.getX() + colOffset, cellLayoutBoxBottom, cellWidth, cellLayoutBoxHeight);
                LayoutArea cellArea = new LayoutArea(layoutContext.getArea().getPageNumber(), cellLayoutBox);
                LayoutResult cellResult = cell.layout(new LayoutContext(cellArea));
                //width of BlockRenderer depends on child areas, while in cell case it is hardly define.
                cell.getOccupiedArea().getBBox().setWidth(cellWidth);

                if (currentCellHasBigRowspan) {
                    if (cellResult.getStatus() == LayoutResult.PARTIAL) {
                        splits[col] = cellResult;
                        currentRow[col] = (CellRenderer) cellResult.getSplitRenderer();
                    } else {
                        rows.get(currentCellInfo.finishRowInd)[col] = null;
                        currentRow[col] = cell;
                    }
                } else {
                    if (cellResult.getStatus() != LayoutResult.FULL) {
                        // first time split occurs
                        if (!split) {
                            for (int addCol = 0; addCol < currentRow.length; addCol++) {
                                if (currentRow[addCol] == null) {
                                    // Search for the next cell including rowspan.
                                    for (int addRow = row + 1; addRow < rows.size(); addRow++) {
                                        if (rows.get(addRow)[addCol] != null) {
                                            CellRenderer addRenderer = rows.get(addRow)[addCol];
                                            if (row + addRenderer.getPropertyAsInteger(Property.ROWSPAN) - 1 >= addRow) {
                                                cellProcessingQueue.add(new CellRendererInfo(addRenderer, addCol, addRow));
                                                cellWithBigRowspanAdded = true;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        split = true;
                        if (cellResult.getStatus() == LayoutResult.NOTHING) {
                            hasContent = false;
                        }

                        splits[col] = cellResult;
                        currentRow[col] = (CellRenderer) cellResult.getSplitRenderer();
                    }
                }

                if (!currentCellHasBigRowspan && cellResult.getStatus() != LayoutResult.NOTHING) {
                    rowHeight = Math.max(rowHeight, cell.getOccupiedArea().getBBox().getHeight() - rowspanOffset);
                }
            }

            if (hasContent || cellWithBigRowspanAdded) {
                heights.add(rowHeight);
                for (int col = 0; col < currentRow.length; col++) {
                    CellRenderer cell = currentRow[col];
                    if (cell == null) {
                        continue;
                    }
                    float height = 0;
                    int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                    for (int i = row; i > targetOverflowRowIndex[col] - rowspan && i >= 0; i--) {
                        height += heights.get(i);
                    }

                    // Coorrection of cell bbox only. We don't need #move() here.
                    // This is because of BlockRenderer's specificity regarding occupied area.
                    float shift = height - cell.getOccupiedArea().getBBox().getHeight();
                    cell.getOccupiedArea().getBBox().moveDown(shift);
                    cell.getOccupiedArea().getBBox().setHeight(height);
                }

                occupiedArea.getBBox().moveDown(rowHeight);
                occupiedArea.getBBox().increaseHeight(rowHeight);
            }

            if (split) {
                TableRenderer splitResult[] = split(row);
                for (int col = 0; col < currentRow.length; col++) {
                    if (splits[col] != null) {
                        BlockRenderer cellSplit = currentRow[col];
                        if (splits[col].getStatus() != LayoutResult.NOTHING) {
                            childRenderers.add(cellSplit);
                        }
                        currentRow[col] = null;
                        rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) splits[col].getOverflowRenderer();
                    } else if (hasContent && currentRow[col] != null) {
                        Cell overflowCell = currentRow[col].getModelElement().clone(false);
                        childRenderers.add(currentRow[col]);
                        currentRow[col] = null;
                        rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) overflowCell.makeRenderer().setParent(this);
                    }
                }
                return new LayoutResult(childRenderers.isEmpty() ? LayoutResult.NOTHING : LayoutResult.PARTIAL,
                        occupiedArea, splitResult[0], splitResult[1]);
            } else {
                childRenderers.addAll(currChildRenderers);
                currChildRenderers.clear();
            }

            layoutBox.decreaseHeight(rowHeight);
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    protected float[] calculateScaledColumnWidths(Table tableModel, float layoutWidth) {
        float[] columnWidths = new float[tableModel.getNumberOfColumns()];
        float widthSum = 0;
        for (int i = 0; i < tableModel.getNumberOfColumns(); i++) {
            columnWidths[i] = tableModel.getColumnWidth(i);
            widthSum += columnWidths[i];
        }
        if (tableModel.getWidth() == 0) {
            // In this case get available occupiedArea, but don't save to model element.
            // Also recalculate column widths, and also don't save it to the model element.
            for (int i = 0; i < tableModel.getNumberOfColumns(); i++) {
                columnWidths[i] *= layoutWidth / widthSum;
            }
        }
        return columnWidths;
    }

    protected TableRenderer[] split(int row) {
        TableRenderer splitRenderer = createSplitRenderer(new Table.RowRange(rowRange.getStartRow(), rowRange.getStartRow() + row));
        splitRenderer.rows = rows.subList(0, row);
        TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row,rowRange.getFinishRow()));
        overflowRenderer.rows = rows.subList(row, rows.size());
        splitRenderer.occupiedArea = occupiedArea;

        return new TableRenderer[] {splitRenderer, overflowRenderer};
    }

    protected TableRenderer createSplitRenderer(Table.RowRange rowRange) {
        TableRenderer splitRenderer = new TableRenderer((Table) modelElement, rowRange);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        // TODO childRenderers will be populated twice during the relayout.
        // We should probably clean them before #layout().
        splitRenderer.childRenderers = childRenderers;
        splitRenderer.addAllProperties(getOwnProperties());
        return splitRenderer;
    }

    protected TableRenderer createOverflowRenderer(Table.RowRange rowRange) {
        TableRenderer overflowRenderer = new TableRenderer((Table) modelElement, rowRange);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        return overflowRenderer;
    }

    /**
     * This is a struct used for convenience in layout.
     */
    private static class CellRendererInfo {
        public CellRenderer cellRenderer;
        public int column;
        public int finishRowInd;

        public CellRendererInfo(CellRenderer cellRenderer, int column, int finishRow) {
            this.cellRenderer = cellRenderer;
            this.column = column;
            // When a cell has a rowspan, this is the index of the finish row of the cell.
            // Otherwise, this is simply the index of the row of the cell in the {@link #rows} array.
            this.finishRowInd = finishRow;
        }
    }
}
