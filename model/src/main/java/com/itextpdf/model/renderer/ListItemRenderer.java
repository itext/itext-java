package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.ListItem;

public class ListItemRenderer extends BlockRenderer {

    protected IRenderer symbolRenderer;
    protected float symbolAreaWidth;

    public ListItemRenderer(ListItem modelElement) {
        super(modelElement);
    }

    public void addSymbolRenderer(IRenderer symbolRenderer, float symbolAreaWidth) {
        this.symbolRenderer = symbolRenderer;
        this.symbolAreaWidth = symbolAreaWidth;
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        super.draw(document, canvas);

        float x = occupiedArea.getBBox().getX();
        if (childRenderers.size() > 0) {
            float yLine = ((AbstractRenderer)childRenderers.get(0)).getFirstYLineRecursively();
            if (symbolRenderer instanceof TextRenderer) {
                ((TextRenderer)symbolRenderer).moveYLineTo(yLine);
            } else {
                symbolRenderer.move(0, yLine - symbolRenderer.getOccupiedArea().getBBox().getY());
            }
        } else {
            symbolRenderer.move(0, occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() -
                    symbolRenderer.getOccupiedArea().getBBox().getHeight() - symbolRenderer.getOccupiedArea().getBBox().getY());
        }

        symbolRenderer.move(x + symbolAreaWidth - symbolRenderer.getOccupiedArea().getBBox().getWidth() - symbolRenderer.getOccupiedArea().getBBox().getX(), 0);
        symbolRenderer.draw(document, canvas);
    }
}
