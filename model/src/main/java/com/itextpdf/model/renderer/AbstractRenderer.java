package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutPosition;

import java.util.*;

public abstract class AbstractRenderer implements IRenderer {

    // TODO linkedList?
    protected List<IRenderer> childRenderers = new ArrayList<>();
    protected List<IRenderer> positionedRenderers = new ArrayList<>();
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
        // https://www.webkit.org/blog/116/webcore-rendering-iii-layout-basics
        // "The rules can be summarized as follows:"...
        Integer positioning = renderer.getProperty(Property.POSITION);
        if (positioning == null || positioning == LayoutPosition.RELATIVE || positioning == LayoutPosition.STATIC) {
            childRenderers.add(renderer);
            renderer.setParent(this);
        } else if (positioning == LayoutPosition.FIXED) {
            AbstractRenderer root = this;
            while (root.parent instanceof AbstractRenderer) {
                root = (AbstractRenderer)root.parent;
            }
            if (root == this) {
                positionedRenderers.add(renderer);
                renderer.setParent(this);
            } else {
                root.addChild(renderer);
            }
        } else if (positioning == LayoutPosition.ABSOLUTE) {
            AbstractRenderer root = this;
            while (root.getPropertyAsInteger(Property.POSITION) == LayoutPosition.STATIC && root.parent instanceof AbstractRenderer) {
                root = (AbstractRenderer)root.parent;
            }
            if (root == this) {
                positionedRenderers.add(renderer);
                renderer.setParent(this);
            } else {
                root.addChild(renderer);
            }
        }
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
        return modelElement != null ? (T) modelElement.getDefaultProperty(key) : (T) getDefaultProperty(key);
    }

    @Override
    public <T> T getProperty(int key, T defaultValue) {
        T result = getProperty(key);
        return result != null ? result : defaultValue;
    }

    public <T> T getOwnProperty(int key) {
        return (T) properties.get(key);
    }

    @Override
    public <T extends IRenderer> T setProperty(int propertyKey, Object value) {
        properties.put(propertyKey, value);
        return (T) this;
    }

    @Override
    public <T> T getDefaultProperty(int propertyKey) {
        switch (propertyKey) {
            case Property.POSITION:
                return (T) Integer.valueOf(LayoutPosition.STATIC);
            default:
                return null;
        }
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

    public Integer getPropertyAsInteger(int key) {
        Number value = getProperty(key);
        return value != null ? value.intValue() : null;
    }

    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        drawBackground(document, canvas);
        drawBorder(document, canvas);
        for (IRenderer child : childRenderers) {
            child.draw(document, canvas);
        }

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }

        flushed = true;
    }

    public void drawBackground(PdfDocument document, PdfCanvas canvas) {
        Property.Background background = getProperty(Property.BACKGROUND);
        if (background != null) {
            Rectangle backgroundArea = applyMargins(occupiedArea.getBBox().clone(), false);
            canvas.saveState().setFillColor(background.getColor()).
                    rectangle(backgroundArea.getX() - background.getExtraLeft(), backgroundArea.getY() - background.getExtraBottom(),
                            backgroundArea.getWidth() + background.getExtraLeft() + background.getExtraRight(),
                            backgroundArea.getHeight() + background.getExtraTop() + background.getExtraBottom()).
                    fill().restoreState();
        }
    }

    public void drawBorder(PdfDocument document, PdfCanvas canvas) {
        // TODO implement complete functionality with all settings. Take into account separate border sides configuration.
        Property.BorderConfig borderConfig = getProperty(Property.BORDER);
        if (borderConfig != null) {
            canvas.saveState();
            canvas.setStrokeColor(borderConfig.getColor());
            canvas.setLineWidth(borderConfig.getWidth());
            canvas.rectangle(occupiedArea.getBBox()).stroke();
            canvas.restoreState();
        }
    }

    public boolean isFlushed() {
        return flushed;
    }

    public IRenderer setParent(IRenderer parent) {
        this.parent = parent;
        return this;
    }

    public void move(float dxRight, float dyUp) {
        occupiedArea.getBBox().moveRight(dxRight);
        occupiedArea.getBBox().moveUp(dyUp);
        for (IRenderer childRenderer : childRenderers) {
            childRenderer.move(dxRight, dyUp);
        }
    }

    public List<LayoutArea> initElementAreas(LayoutContext context) {
        return Collections.singletonList(context.getArea());
    }

    /**
     * Gets the first yLine of the nested children recursively. E.g. for a list, this will be the yLine of the
     * first item (if the first item is indeed a paragraph).
     * NOTE: this method will no go further than the first child.
     */
    protected float getFirstYLineRecursively() {
        if (childRenderers.size() == 0) {
            throw new RuntimeException("Cannot get yLine of empty paragraph");
        }
        return ((AbstractRenderer)childRenderers.get(0)).getFirstYLineRecursively();
    }

    protected <T extends AbstractRenderer> T createSplitRenderer() {
        return null;
    }

    protected <T extends AbstractRenderer> T createOverflowRenderer() {
        return null;
    }

    protected Rectangle applyMargins(Rectangle rect, boolean reverse) {
        return rect.applyMargins(getPropertyAsFloat(Property.MARGIN_TOP), getPropertyAsFloat(Property.MARGIN_RIGHT),
                getPropertyAsFloat(Property.MARGIN_BOTTOM), getPropertyAsFloat(Property.MARGIN_LEFT), reverse);
    }

    protected Rectangle applyPaddings(Rectangle rect, boolean reverse) {
        return rect.applyMargins(getPropertyAsFloat(Property.PADDING_TOP), getPropertyAsFloat(Property.PADDING_RIGHT),
                getPropertyAsFloat(Property.PADDING_BOTTOM), getPropertyAsFloat(Property.PADDING_LEFT), reverse);
    }

    protected void applyAbsolutePositioningTranslation(boolean reverse) {
        float top = getPropertyAsFloat(Property.TOP);
        float bottom = getPropertyAsFloat(Property.BOTTOM);
        float left = getPropertyAsFloat(Property.LEFT);
        float right = getPropertyAsFloat(Property.RIGHT);

        int reverseMultiplier = reverse ? -1 : 1;

        float dxRight = left != 0 ? left * reverseMultiplier : -right * reverseMultiplier;
        float dyUp = top != 0 ? -top * reverseMultiplier : bottom * reverseMultiplier;

        if (dxRight != 0 || dyUp != 0)
            move(dxRight, dyUp);
    }

    protected boolean isPositioned() {
        Object positioning = getProperty(Property.POSITION);
        return Integer.valueOf(LayoutPosition.ABSOLUTE).equals(positioning) || Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    protected boolean isFixedLayout() {
        Object positioning = getProperty(Property.POSITION);
        return Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    protected void alignChildHorizontally(IRenderer childRenderer, float availableWidth) {
        Property.HorizontalAlignment horizontalAlignment = childRenderer.getProperty(Property.HORIZONTAL_ALIGNMENT);
        if (horizontalAlignment != null && horizontalAlignment != Property.HorizontalAlignment.LEFT) {
            float deltaX = availableWidth - childRenderer.getOccupiedArea().getBBox().getWidth();
            switch (horizontalAlignment) {
                case RIGHT:
                    childRenderer.move(deltaX, 0);
                    break;
                case CENTER:
                    childRenderer.move(deltaX / 2, 0);
                    break;
            }
        }
    }
}
