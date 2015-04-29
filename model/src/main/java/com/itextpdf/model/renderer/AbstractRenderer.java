package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;

import java.util.*;

public abstract class AbstractRenderer implements IRenderer {

    // TODO linkedList?
    protected List<IRenderer> childRenderers = new ArrayList<>();
    protected IPropertyContainer modelElement;
    protected boolean flushed = false;
    protected LayoutArea occupiedArea;
    protected IRenderer parent;
    protected Map<Integer, Object> properties = new HashMap<>();

    public AbstractRenderer() {
    }

    public AbstractRenderer(IPropertyContainer modelElement) {
        this.modelElement = modelElement;
    }

    @Override
    public void addChild(IRenderer renderer) {
        childRenderers.add(renderer);
        renderer.setParent(this);
    }

    @Override
    public IPropertyContainer getModelElement() {
        return modelElement;
    }

    @Override
    public List<IRenderer> getChildRenderers() {
        return childRenderers;
    }

    @Override
    public <T> T getProperty(int key) {
        // TODO distinguish between inherit and non-inherit properties.
        Object ownProperty = getOwnProperty(key);
        if (ownProperty != null)
            return (T) ownProperty;
        Object modelProperty = modelElement != null ? modelElement.getProperty(key) : null;
        if (modelProperty != null)
            return (T) modelProperty;
        Object baseProperty = parent != null && Property.isPropertyInherited(key, modelElement, parent.getModelElement()) ? parent.getProperty(key) : null;
        if (baseProperty != null)
            return (T) baseProperty;
        return modelElement != null ? (T) modelElement.getDefaultProperty(key) : null;
    }

    public <T> T getOwnProperty(int key) {
        return (T) properties.get(key);
    }

    public PdfFont getPropertyAsFont(int key) {
        return getProperty(key);
    }

    public Color getPropertyAsColor(int key) {
        return getProperty(key);
    }

    public Float getPropertyAsFloat(int key) {
        Number value = getProperty(key);
        return value != null ? value.floatValue() : null;
    }

    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        drawBackground(document, canvas);
        drawBorder(document, canvas);
        for (IRenderer child : childRenderers) {
            child.draw(document, canvas);
        }

        flushed = true;
    }

    public void drawBackground(PdfDocument document, PdfCanvas canvas) {
        try {
            Property.Background background = getProperty(Property.BACKGROUND);
            if (background != null) {
                canvas.saveState().setFillColor(background.getColor()).
                        rectangle(occupiedArea.getBBox().getX() - background.getExtraLeft(), occupiedArea.getBBox().getY() - background.getExtraBottom(),
                                occupiedArea.getBBox().getWidth() + background.getExtraLeft() + background.getExtraRight(),
                                occupiedArea.getBBox().getHeight() + background.getExtraTop() + background.getExtraBottom()).
                        fill().restoreState();
            }
        } catch (PdfException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void drawBorder(PdfDocument document, PdfCanvas canvas) {
//        try {
//            canvas.rectangle(occupiedArea.getBBox()).stroke();
//        } catch (PdfException exc) {
//            throw new RuntimeException(exc);
//        }
    }

    public boolean isFlushed() {
        return flushed;
    }

    public IRenderer setParent(IRenderer parent) {
        this.parent = parent;
        return this;
    }

    public List<LayoutArea> initElementAreas(LayoutContext context) {
        return Collections.singletonList(context.getArea());
    }

    protected <T extends AbstractRenderer> T createSplitRenderer() {
        return null;
    }

    protected <T extends AbstractRenderer> T createOverflowRenderer() {
        return null;
    }
}
