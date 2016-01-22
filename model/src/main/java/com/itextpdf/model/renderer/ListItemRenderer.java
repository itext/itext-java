package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.tagutils.IAccessibleElement;
import com.itextpdf.core.pdf.tagutils.PdfTagStructure;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.ListItem;
import com.itextpdf.model.layout.LayoutContext;
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
    public LayoutResult layout(LayoutContext layoutContext) {
        if (symbolRenderer != null && getProperty(Property.HEIGHT) == null) {
            // TODO this is actually MinHeight.
            setProperty(Property.HEIGHT, symbolRenderer.getOccupiedArea().getBBox().getHeight());
        }
        return super.layout(layoutContext);
    }

    @Override
    public void draw(DrawContext drawContext) {
        boolean isTagged = drawContext.getDocument().isTagged() && getModelElement() instanceof IAccessibleElement;
        PdfTagStructure tagStructure = null;
        if (isTagged) {
            tagStructure = drawContext.getDocument().getTagStructure();
            IAccessibleElement modelElement = (IAccessibleElement) getModelElement();
            boolean lBodyTagIsCreated = tagStructure.isConnectedToTag(modelElement);
            if (!lBodyTagIsCreated) {
                tagStructure.addTag(PdfName.LI);
            } else {
                tagStructure.moveToTag(modelElement).moveToParent();
            }
        }

        super.draw(drawContext);

        // It will be null in case of overflow (only the "split" part will contain symbol renderer.
        if (symbolRenderer != null) {
            float x = occupiedArea.getBBox().getX();
            if (childRenderers.size() > 0) {
                Float yLine = ((AbstractRenderer) childRenderers.get(0)).getFirstYLineRecursively();
                if (yLine != null) {
                    if (symbolRenderer instanceof TextRenderer) {
                        ((TextRenderer) symbolRenderer).moveYLineTo(yLine);
                    } else {
                        symbolRenderer.move(0, yLine - symbolRenderer.getOccupiedArea().getBBox().getY());
                    }
                } else {
                    symbolRenderer.move(0, occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() -
                            (symbolRenderer.getOccupiedArea().getBBox().getY() + symbolRenderer.getOccupiedArea().getBBox().getHeight()));
                }
            } else {
                symbolRenderer.move(0, occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() -
                        symbolRenderer.getOccupiedArea().getBBox().getHeight() - symbolRenderer.getOccupiedArea().getBBox().getY());
            }

            symbolRenderer.move(x + symbolAreaWidth - symbolRenderer.getOccupiedArea().getBBox().getWidth() - symbolRenderer.getOccupiedArea().getBBox().getX(), 0);

            if (isTagged) {
                tagStructure.addTag(0, PdfName.Lbl);
            }
            symbolRenderer.draw(drawContext);
            if (isTagged) {
                tagStructure.moveToParent();
            }
        }

        if (isTagged) {
            tagStructure.moveToParent();
        }
    }

    @Override
    public ListItemRenderer getNextRenderer() {
        return new ListItemRenderer((ListItem) modelElement);
    }

    @Override
    protected BlockRenderer createSplitRenderer(int layoutResult) {
        ListItemRenderer splitRenderer = getNextRenderer();
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
        ListItemRenderer overflowRenderer = getNextRenderer();
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
