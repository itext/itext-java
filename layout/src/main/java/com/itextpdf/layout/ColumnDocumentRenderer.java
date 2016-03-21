package com.itextpdf.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.DocumentRenderer;

/**
 * This class is used for convenient multi-column Document Layouting
 */
public class ColumnDocumentRenderer extends DocumentRenderer {

    private Rectangle[] columns;
    private int nextAreaNumber;

    public ColumnDocumentRenderer(Document document, Rectangle[] columns) {
        super(document);
        this.columns = columns;
    }

    public ColumnDocumentRenderer(Document document, boolean immediateFlush, Rectangle[] columns) {
        super(document, immediateFlush);
        this.columns = columns;
    }

    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        if (overflowResult != null && overflowResult.getAreaBreak() != null && overflowResult.getAreaBreak().getType() != Property.AreaBreakType.NEW_AREA) {
            nextAreaNumber = 0;
        }
        if (nextAreaNumber % columns.length == 0) {
            super.updateCurrentArea(overflowResult);
        }
        return (currentArea = new LayoutArea(currentPageNumber, columns[nextAreaNumber++ % columns.length].clone()));
    }
}
