package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

public class CellRenderer extends BlockRenderer {

    public CellRenderer(Cell modelElement) {
        super(modelElement);
        setProperty(Property.ROWSPAN, modelElement.getRowspan());
        setProperty(Property.COLSPAN, modelElement.getColspan());
    }

    @Override
    public Cell getModelElement() {
        return (Cell) super.getModelElement();
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        return super.layout(layoutContext);
    }

    @Override
    protected CellRenderer createSplitRenderer(int layoutResult) {
        CellRenderer splitRenderer = getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.addAllProperties(getOwnProperties());
        return splitRenderer;
    }

    @Override
    protected CellRenderer createOverflowRenderer(int layoutResult) {
        CellRenderer overflowRenderer = getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        return overflowRenderer;
    }

    @Override
    public void drawBorder(DrawContext drawContext) {
        // Do nothing here. Border drawing for tables is done on TableRenderer.
    }

    @Override
    public CellRenderer getNextRenderer() {
        return new CellRenderer(getModelElement());
    }
}
