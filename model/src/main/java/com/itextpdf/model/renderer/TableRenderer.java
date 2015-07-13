package com.itextpdf.model.renderer;

import com.itextpdf.canvas.color.DeviceRgb;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.SolidBorder;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Table;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TableRenderer extends AbstractRenderer {

    List<BlockRenderer[]> rows = new ArrayList<>();

    public TableRenderer(Table modelElement) {
        super(modelElement);
        for (int row = 0; row < modelElement.getNumberOfRows(); row++) {
            rows.add(new BlockRenderer[modelElement.getNumberOfColumns()]);
        }
    }

    @Override
    public void addChild(IRenderer renderer) {
        if (renderer instanceof BlockRenderer && renderer.getModelElement() instanceof Cell) {
            renderer.setParent(this);
            // In case rowspan or colspan save cell into bottom left corner.
            // In in this case it will be easier handle row heights in case rowspan.
            Cell cell = (Cell) renderer.getModelElement();
            rows.get(cell.getRow() + cell.getRowspan() - 1)[cell.getCol()] = (BlockRenderer) renderer;
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
        if (tableModel.getTotalWidth() == 0) {
            // In this case get available occupiedArea, but don't save to model element.
            // Also recalculate column widths, and also don't save it to the model element.
        }
        ArrayList<Float> heights = new ArrayList<>();
        occupiedArea = new LayoutArea(area.getPageNumber(), layoutBox.clone());
        occupiedArea.getBBox().moveUp(occupiedArea.getBBox().getHeight());
        occupiedArea.getBBox().setHeight(0);
        occupiedArea.getBBox().setWidth(tableModel.getTotalWidth());
        LayoutResult[] splits = new LayoutResult[tableModel.getNumberOfColumns()];

        for (int row = 0; row < rows.size(); row++) {
            BlockRenderer[] currentRow = rows.get(row);
            float rowHeight = 0;
            boolean split = false;
            boolean hasContent = true;
            ArrayList<BlockRenderer> currChildRenderers = new ArrayList<>();
            for (int col = 0; col < currentRow.length; col++) {
                BlockRenderer cell = currentRow[col];
                if (cell == null) {
                    continue;
                }
                currChildRenderers.add(cell);
                int colspan = cell.getPropertyAsInteger(Property.COLSPAN);
                int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                float cellWidth = 0, colOffset = 0;
                for (int i = col; i < col + colspan; i++) {
                    cellWidth += tableModel.getColumnWidth(i);
                }
                for (int i = 0; i < col; i++) {
                    colOffset += tableModel.getColumnWidth(i);
                }
                float rowspanOffset = 0;
                for (int i = row - 1; i > row - rowspan; i--) {
                    rowspanOffset += heights.get(i);
                }
                Rectangle cellLayoutBox = new Rectangle(layoutBox.getX() + colOffset, layoutBox.getY(),
                        cellWidth, layoutBox.getHeight() + rowspanOffset);
                LayoutArea cellArea = new LayoutArea(layoutContext.getArea().getPageNumber(), cellLayoutBox);
                //cell.setProperty(Property.KEEP_TOGETHER, true);
                LayoutResult cellResult = cell.layout(new LayoutContext(cellArea));
                //width of BlockRenderer depends from child areas, while in cell case it is hardly define.
                cell.getOccupiedArea().getBBox().setWidth(cellWidth);

                if (cellResult.getStatus() != LayoutResult.FULL) {
                    split = true;
                    if (cellResult.getStatus() == LayoutResult.NOTHING) {
                        hasContent = false;
                    }
                    splits[col] = cellResult;
                }
                if (cellResult.getStatus() != LayoutResult.NOTHING) {
                    rowHeight = Math.max(rowHeight, cell.getOccupiedArea().getBBox().getHeight() - rowspanOffset);
                }
            }
            if (hasContent) {
                heights.add(rowHeight);
                for (int col = 0; col < currentRow.length; col++) {
                    if (currentRow[col] == null) continue;
                    float height = 0;
                    int rowspan = currentRow[col].getPropertyAsInteger(Property.ROWSPAN);
                    for (int i = row; i > row - rowspan; i--) {
                        height += heights.get(i);
                    }
                    float shift = height - currentRow[col].getOccupiedArea().getBBox().getHeight();
                    currentRow[col].getOccupiedArea().getBBox().moveDown(shift);
                    currentRow[col].getOccupiedArea().getBBox().setHeight(height);
                }

                occupiedArea.getBBox().moveDown(rowHeight);
                occupiedArea.getBBox().incrementHeight(rowHeight);
            }

            if (split) {
                TableRenderer splitRenderer = createSplitRenderer();
                splitRenderer.rows = rows.subList(0, row);
                TableRenderer overflowRenderer = createOverflowRenderer();
                overflowRenderer.rows = rows.subList(row, rows.size());
                splitRenderer.occupiedArea = occupiedArea;
                for (int col = 0; col < currentRow.length; col++) {
                    if (splits[col] != null) {
                        BlockRenderer cellSplit = currentRow[col];
                        if (splits[col].getStatus() != LayoutResult.NOTHING) {
                            cellSplit.getOccupiedArea().getBBox().setHeight(rowHeight);
                            childRenderers.add(cellSplit);
                        }
                        currentRow[col] = (BlockRenderer) splits[col].getOverflowRenderer();
                        //TODO to handle cell-renderer specific properties makes sense to add CellRenderer.
                        currentRow[col].setProperty(Property.ROWSPAN, cellSplit.getPropertyAsInteger(Property.ROWSPAN));
                        currentRow[col].setProperty(Property.COLSPAN, cellSplit.getPropertyAsInteger(Property.COLSPAN));
                        SolidBorder borders = new SolidBorder(new DeviceRgb(160, 160, 160), 0.5f);
                        currentRow[col].setProperty(Property.BORDER, borders);
                    } else if (hasContent) {
                        Cell splitCell = (Cell)currentRow[col].getModelElement();
                        Cell overflowCell = splitCell.clone(false);
                        childRenderers.add(currentRow[col]);
                        currentRow[col] = overflowCell.makeRenderer();
                    }
                }
                return new LayoutResult(row == 0 ? LayoutResult.NOTHING : LayoutResult.PARTIAL,
                        occupiedArea, splitRenderer, overflowRenderer);
            } else {
                childRenderers.addAll(currChildRenderers);
                currChildRenderers.clear();
            }

            layoutBox.decrementHeight(rowHeight);
        }

        if (getProperty(Property.ANGLE) != null) {
            applyRotationLayout();
            if (isNotFittingHeight(layoutContext.getArea())) {
                new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
            }
        }
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    protected TableRenderer createSplitRenderer() {
        TableRenderer splitRenderer = new TableRenderer((Table) modelElement);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.childRenderers = childRenderers;
        return splitRenderer;
    }

    @Override
    protected TableRenderer createOverflowRenderer() {
        TableRenderer overflowRenderer = new TableRenderer((Table) modelElement);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        return overflowRenderer;
    }

//    @Override
//    public void drawBorder(com.itextpdf.core.pdf.PdfDocument document, com.itextpdf.canvas.PdfCanvas canvas) {
//        drawRectangle(occupiedArea.getBBox(), canvas, DeviceRgb.Red);
//    }
//
//    private void drawRectangle(Rectangle bbox, com.itextpdf.canvas.PdfCanvas canvas, com.itextpdf.canvas.color.Color color) {
//        canvas.saveState();
//        canvas.setStrokeColor(color);
//        canvas.setLineWidth(3f);
//        canvas.rectangle(bbox).stroke();
//        canvas.restoreState();
//    }
}
