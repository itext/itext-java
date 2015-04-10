package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.fonts.PdfFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.IElement;
import com.itextpdf.model.layout.LayoutArea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRenderer implements IRenderer {

    protected List<IRenderer> childRenderers = new ArrayList<IRenderer>();
    protected IElement modelElement;
    // TODO
    protected boolean flushed = false;
    protected LayoutArea occupiedArea;
    protected IRenderer parent;
    protected Map<Integer, Object> properties = new HashMap<Integer, Object>();

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

    @Override
    public <T> T getProperty(int key) {
        Object ownProperty = getOwnProperty(key);
        if (ownProperty != null)
            return (T) ownProperty;
        Object modelProperty = modelElement.getProperty(key);
        if (modelProperty != null)
            return (T) modelProperty;
        Object baseProperty = parent != null ? parent.getProperty(key) : null;
        if (baseProperty != null)
            return (T) baseProperty;
        return modelElement.getDefaultProperty(key);
    }

    public <T> T getOwnProperty(int key) {
        return (T) properties.get(key);
    }

    public PdfFont getPropertyAsFont(int key) {
        return getProperty(key);
    }

    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        drawBorder(canvas);
        for (IRenderer child : childRenderers) {
            child.draw(document, canvas);
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
