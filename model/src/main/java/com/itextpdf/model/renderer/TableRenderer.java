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
            rows.get(cell.getRow())[cell.getCol()] = (BlockRenderer)renderer;
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
        for (int row = 0; row < rows.size(); row++) {
            BlockRenderer[] currentRow = rows.get(row);
            float rowHeight = 0;
            for (int col = 0; col < currentRow.length; col++) {
                BlockRenderer cell = currentRow[col];
                if (cell == null) continue;
                float cellOffset = 0;
                for (int i = 0; i < col; i++) {
                    cellOffset += tableModel.getColumnWidth(i);
                }
                float cellWidth = 0;
                int colspan = cell.getPropertyAsInteger(Property.COLSPAN);
                for (int i = col; i < col + colspan; i++) {
                    cellWidth += tableModel.getColumnWidth(i);
                }
                Rectangle cellLayoutBox = new Rectangle(layoutBox.getX() + cellOffset, layoutBox.getY() + rowOffset,
                        cellWidth, layoutBox.getHeight());
                LayoutArea cellArea = new LayoutArea(layoutContext.getArea().getPageNumber(),cellLayoutBox);
                LayoutContext cellContext = new LayoutContext(cellArea);
                cell.layout(cellContext);
                rowHeight = Math.max(rowHeight, cell.getOccupiedArea().getBBox().getHeight());
            }
            rowOffset -= rowHeight;
        }
        occupiedArea = new LayoutArea(area.getPageNumber(),
                new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), tableModel.getTotalWidth(), rowOffset));
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }
}
