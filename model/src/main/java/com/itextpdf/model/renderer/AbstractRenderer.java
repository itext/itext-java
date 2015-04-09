package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.model.element.IElement;
import com.itextpdf.model.layout.LayoutArea;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRenderer implements IRenderer {

    protected List<IRenderer> childRenderers = new ArrayList<IRenderer>();
    protected IElement modelElement;
    protected boolean flushed = false;
    protected LayoutArea occupiedArea;
    protected IRenderer parent;

    public AbstractRenderer() {
    }

    public AbstractRenderer(IElement modelElement) {
        this.modelElement = modelElement;
    }

    @Override
    public void addChild(IRenderer renderer) {
        childRenderers.add(renderer);
    }

    @Override
    public LayoutArea getNextArea() {
        if (parent != null)
            return parent.getNextArea();
        throw new RuntimeException("Next area is not available");
    }

    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    @Override
    public void draw(PdfCanvas canvas) {
        drawBorder(canvas);
        for (IRenderer child : childRenderers) {
            child.draw(canvas);
        }
    }

    protected void drawBorder(PdfCanvas canvas) {
        try {
            canvas.rectangle(occupiedArea.getBBox()).stroke();
        } catch (PdfException exc) {
            throw new RuntimeException(exc);
        }
    }
}
