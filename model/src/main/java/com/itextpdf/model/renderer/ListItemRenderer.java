package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.ListItem;
import com.itextpdf.model.layout.LayoutResult;

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

        // It will be null in case of overflow (only the "split" part will contain symbol renderer.
        if (symbolRenderer != null) {
            float x = occupiedArea.getBBox().getX();
            if (childRenderers.size() > 0) {
                float yLine = ((AbstractRenderer) childRenderers.get(0)).getFirstYLineRecursively();
                if (symbolRenderer instanceof TextRenderer) {
                    ((TextRenderer) symbolRenderer).moveYLineTo(yLine);
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

    @Override
    protected BlockRenderer createSplitRenderer(int layoutResult) {
        ListItemRenderer splitRenderer = new ListItemRenderer((ListItem) modelElement);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        if (layoutResult == LayoutResult.PARTIAL) {
            splitRenderer.symbolRenderer = symbolRenderer;
            splitRenderer.symbolAreaWidth = symbolAreaWidth;
        }
        // TODO retain all the properties ?
        splitRenderer.setProperty(Property.MARGIN_LEFT, getProperty(Property.MARGIN_LEFT));
        return splitRenderer;
    }

    @Override
    protected BlockRenderer createOverflowRenderer(int layoutResult) {
        ListItemRenderer overflowRenderer = new ListItemRenderer((ListItem) modelElement);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        if (layoutResult == LayoutResult.NOTHING) {
            overflowRenderer.symbolRenderer = symbolRenderer;
            overflowRenderer.symbolAreaWidth = symbolAreaWidth;
        }
        // TODO retain all the properties ?
        overflowRenderer.setProperty(Property.MARGIN_LEFT, getProperty(Property.MARGIN_LEFT));
        return overflowRenderer;
    }
}
