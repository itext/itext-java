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

public class TableRenderer extends AbstractRenderer {

    ArrayList<BlockRenderer[]> rows = new ArrayList<>();

    public TableRenderer(Table modelElement) {
        super(modelElement);

        for (int row = 0; row < modelElement.getNumberOfRows(); row++) {
            rows.add(new BlockRenderer[modelElement.getNumberOfColumns()]);
        }
    }

    @Override
    public void addChild(IRenderer renderer) {
        if (renderer instanceof BlockRenderer && renderer.getModelElement() instanceof Cell) {
            childRenderers.add(renderer);
            renderer.setParent(this);
            Cell cell = (Cell)renderer.getModelElement();
            rows.get(cell.getRow() + cell.getRowspan() - 1)[cell.getCol()] = (BlockRenderer)renderer;
        } else {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.error("Only BlockRenderer with Cell model element could be added");
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox();
        Table tableModel = (Table)getModelElement();
        if (tableModel.getTotalWidth() == 0) {
            // In this case get available occupiedArea, but don't save to model element.
            // Also recalculate column widths, and also don't save it to the model element.
        }
        float rowOffset = 0;
        ArrayList<Float> heights = new ArrayList<>();
        for (int row = 0; row < rows.size(); row++) {
            BlockRenderer[] currentRow = rows.get(row);
            float rowHeight = 0;
            for (int col = 0; col < currentRow.length; col++) {
                BlockRenderer cell = currentRow[col];
                if (cell == null) continue;
                int colspan = cell.getPropertyAsInteger(Property.COLSPAN);
                int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                float cellWidth = 0, colOffset = 0;
                for (int i = col; i < col + colspan; i++) {
                    cellWidth += tableModel.getColumnWidth(i);
                }
                for (int i = 0; i < col; i++) {
                    colOffset += tableModel.getColumnWidth(i);
                }
                float currRowOffset = rowOffset;
                for (int i = row - 1; i > row - rowspan; i--) {
                    currRowOffset += heights.get(i);
                }
                Rectangle cellLayoutBox = new Rectangle(layoutBox.getX() + colOffset, layoutBox.getY() + currRowOffset,
                        cellWidth, layoutBox.getHeight());
                LayoutArea cellArea = new LayoutArea(layoutContext.getArea().getPageNumber(),cellLayoutBox);
                cell.layout(new LayoutContext(cellArea));
                rowHeight = Math.max(rowHeight, cell.getOccupiedArea().getBBox().getHeight() - currRowOffset + rowOffset);
            }
            rowOffset -= rowHeight;
            heights.add(rowHeight);

            for (BlockRenderer cell : currentRow) {
                if (cell == null) continue;
                float height = 0;
                int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                for (int i = row; i > row - rowspan; i--) {
                    height += heights.get(i);
                }
                float shift = height - cell.getOccupiedArea().getBBox().getHeight();
                cell.getOccupiedArea().getBBox().moveDown(shift);
                cell.getOccupiedArea().getBBox().setHeight(height);
            }
        }

        occupiedArea = new LayoutArea(area.getPageNumber(),
                new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), tableModel.getTotalWidth(), rowOffset));
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }
}
