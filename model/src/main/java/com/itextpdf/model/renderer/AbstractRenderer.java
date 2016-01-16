package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.Border;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRenderer implements IRenderer {

    public static final float EPS = 1e-4f;
    public static final float INF = 1e6f;

    // TODO linkedList?
    protected List<IRenderer> childRenderers = new ArrayList<>();
    protected List<IRenderer> positionedRenderers = new ArrayList<>();
    protected IPropertyContainer modelElement;
    protected boolean flushed = false;
    protected LayoutArea occupiedArea;
    protected IRenderer parent;
    protected Map<Property, Object> properties = new EnumMap<>(Property.class);
    protected boolean isLastRendererForModelElement = true;

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
                root = (AbstractRenderer) root.parent;
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
    public boolean hasProperty(Property property) {
        return hasOwnProperty(property)
                || (modelElement != null && modelElement.hasProperty(property))
                || (parent != null && property.isInherited() && parent.hasProperty(property));
    }

    @Override
    public boolean hasOwnProperty(Property property) {
        return properties.containsKey(property);
    }

    @Override
    public void deleteProperty(Property property) {
        properties.remove(property);
    }

    @Override
    public <T> T getProperty(Property key) {
        Object property;
        if ((property = properties.get(key)) != null || properties.containsKey(key)) {
            return (T) property;
        }
        if (modelElement != null && ((property = modelElement.getProperty(key)) != null || modelElement.hasProperty(key))) {
            return (T) property;
        }
        // TODO in some situations we will want to check inheritance with additional info, such as parent and descendant.
        if (parent != null && key.isInherited() && (property = parent.getProperty(key)) != null) {
            return (T) property;
        }
        return modelElement != null ? (T) modelElement.getDefaultProperty(key) : (T) getDefaultProperty(key);
    }

    @Override
    public <T> T getOwnProperty(Property property) {
        return (T) properties.get(property);
    }

    @Override
    public <T> T getProperty(Property property, T defaultValue) {
        T result = getProperty(property);
        return result != null ? result : defaultValue;
    }

    @Override
    public <T extends IRenderer> T setProperty(Property property, Object value) {
        properties.put(property, value);
        return (T) this;
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        switch (property) {
            case POSITION:
                return (T) Integer.valueOf(LayoutPosition.STATIC);
            default:
                return null;
        }
    }

    public PdfFont getPropertyAsFont(Property property) {
        return getProperty(property);
    }

    public Color getPropertyAsColor(Property property) {
        return getProperty(property);
    }

    public Float getPropertyAsFloat(Property property) {
        Number value = getProperty(property);
        return value != null ? value.floatValue() : null;
    }

    public Boolean getPropertyAsBoolean(Property property) {
        return getProperty(property);
    }

    public Integer getPropertyAsInteger(Property property) {
        Number value = getProperty(property);
        return value != null ? value.intValue() : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IRenderer renderer : childRenderers) {
            sb.append(renderer.toString());
        }
        return sb.toString();
    }

    public LayoutArea getOccupiedArea() {
        return occupiedArea;
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        applyDestination(document);
        applyAction(document);

        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        drawBackground(document, canvas);
        drawBorder(document, canvas);
        drawChildren(document, canvas);

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }

        flushed = true;
    }

    public void drawBackground(PdfDocument document, PdfCanvas canvas) {
        Property.Background background = getProperty(Property.BACKGROUND);
        if (background != null) {

            Rectangle bBox = getOccupiedAreaBBox();

            Rectangle backgroundArea = applyMargins(bBox, false);
            canvas.saveState().setFillColor(background.getColor()).
                    rectangle(backgroundArea.getX() - background.getExtraLeft(), backgroundArea.getY() - background.getExtraBottom(),
                            backgroundArea.getWidth() + background.getExtraLeft() + background.getExtraRight(),
                            backgroundArea.getHeight() + background.getExtraTop() + background.getExtraBottom()).
                    fill().restoreState();
        }
    }

    public void drawChildren(PdfDocument document, PdfCanvas canvas) {
        for (IRenderer child : childRenderers) {
            child.draw(document, canvas);
        }
    }

    public void drawBorder(PdfDocument document, PdfCanvas canvas) {
        Border[] borders = getBorders();
        boolean gotBorders = false;

        for (Border border : borders)
            gotBorders = gotBorders || border != null;

        if (gotBorders) {
            float topWidth = borders[0] != null ? borders[0].getWidth() : 0;
            float rightWidth = borders[1] != null ? borders[1].getWidth() : 0;
            float bottomWidth = borders[2] != null ? borders[2].getWidth() : 0;
            float leftWidth = borders[3] != null ? borders[3].getWidth() : 0;

            Rectangle bBox = getOccupiedAreaBBox();

            applyMargins(bBox, false);
            applyBorderBox(bBox, false);
            float x1 = bBox.getX();
            float y1 = bBox.getY();
            float x2 = bBox.getX() + bBox.getWidth();
            float y2 = bBox.getY() + bBox.getHeight();

            if (borders[0] != null) {
                canvas.saveState();
                borders[0].draw(canvas, x1, y2, x2, y2, leftWidth, rightWidth);
                canvas.restoreState();
            }
            if (borders[1] != null) {
                canvas.saveState();
                borders[1].draw(canvas, x2, y2, x2, y1, topWidth, bottomWidth);
                canvas.restoreState();
            }
            if (borders[2] != null) {
                canvas.saveState();
                borders[2].draw(canvas, x2, y1, x1, y1, rightWidth, leftWidth);
                canvas.restoreState();
            }
            if (borders[3] != null) {
                canvas.saveState();
                borders[3].draw(canvas, x1, y1, x1, y2, bottomWidth, topWidth);
                canvas.restoreState();
            }
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

    public List<Rectangle> initElementAreas(LayoutArea area) {
        return Collections.singletonList(area.getBBox());
    }

    protected Rectangle getOccupiedAreaBBox() {
        return occupiedArea.getBBox().clone();
    }

    protected Rectangle getBorderBBox() {
        Rectangle rect = getOccupiedAreaBBox();
        applyMargins(rect, false);
        applyBorderBox(rect, false);
        return rect;
    }

    protected Rectangle getInnerBBox() {
        Rectangle rect = getOccupiedAreaBBox();
        applyPaddings(rect, false);
        return rect;
    }


    protected Float retrieveWidth(float parentBoxWidth) {
        Property.UnitValue width = getProperty(Property.WIDTH);
        if (width != null) {
            if (width.getUnitType() == Property.UnitValue.POINT) {
                return width.getValue();
            } else if (width.getUnitType() == Property.UnitValue.PERCENT) {
                return width.getValue() * parentBoxWidth / 100;
            } else {
                throw new IllegalStateException("invalid unit type");
            }
        } else {
            return null;
        }
    }

    //TODO is behavior of copying all properties in split case common to all renderers?
    protected Map<Property, Object> getOwnProperties() {
        return properties;
    }

    protected void addAllProperties(Map<Property, Object> properties) {
        this.properties.putAll(properties);
    }

    /**
     * Gets the first yLine of the nested children recursively. E.g. for a list, this will be the yLine of the
     * first item (if the first item is indeed a paragraph).
     * NOTE: this method will no go further than the first child.
     * Returns null if there is no text found.
     */
    protected Float getFirstYLineRecursively() {
        if (childRenderers.size() == 0) {
            return null;
        }
        return ((AbstractRenderer) childRenderers.get(0)).getFirstYLineRecursively();
    }

    protected Rectangle applyMargins(Rectangle rect, boolean reverse) {
        if (isPositioned())
            return rect;

        return rect.applyMargins(getPropertyAsFloat(Property.MARGIN_TOP), getPropertyAsFloat(Property.MARGIN_RIGHT),
                getPropertyAsFloat(Property.MARGIN_BOTTOM), getPropertyAsFloat(Property.MARGIN_LEFT), reverse);
    }

    protected Rectangle applyPaddings(Rectangle rect, boolean reverse) {
        return rect.applyMargins(getPropertyAsFloat(Property.PADDING_TOP), getPropertyAsFloat(Property.PADDING_RIGHT),
                getPropertyAsFloat(Property.PADDING_BOTTOM), getPropertyAsFloat(Property.PADDING_LEFT), reverse);
    }

    protected Rectangle applyBorderBox(Rectangle rect, boolean reverse) {
        Border[] borders = getBorders();
        float topWidth = borders[0] != null ? borders[0].getWidth() : 0;
        float rightWidth = borders[1] != null ? borders[1].getWidth() : 0;
        float bottomWidth = borders[2] != null ? borders[2].getWidth() : 0;
        float leftWidth = borders[3] != null ? borders[3].getWidth() : 0;
        return rect.applyMargins(topWidth, rightWidth, bottomWidth, leftWidth, reverse);
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

    protected void applyDestination(PdfDocument document){
        String destination = getProperty(Property.DESTINATION);
        if (destination != null) {
            PdfArray array = new PdfArray();
            array.add(document.getPage(occupiedArea.getPageNumber()).getPdfObject());
            array.add(PdfName.XYZ);
            array.add(new PdfNumber(occupiedArea.getBBox().getX()));
            array.add(new PdfNumber(occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight()));
            array.add(new PdfNumber(1));
            document.addNewName(new PdfString(destination), array);
        }
    }

    protected void applyAction(PdfDocument document){
        PdfAction action = getProperty(Property.ACTION);
        if (action != null) {
            PdfLinkAnnotation link = new PdfLinkAnnotation(document, getOccupiedArea().getBBox());
            link.setAction(action);
            Border border = getProperty(Property.BORDER);
            if (border != null) {
                link.setBorder(new PdfArray(new float[]{0, 0, border.getWidth()}));
            } else {
                link.setBorder(new PdfArray(new float[]{0, 0, 0}));
            }
            document.getPage(getOccupiedArea().getPageNumber()).addAnnotation(link);
        }
    }

    protected boolean isNotFittingHeight(LayoutArea layoutArea) {
        Rectangle area = applyMargins(layoutArea.getBBox().clone(), false);
        area = applyPaddings(area, false);
        return !isPositioned() && occupiedArea.getBBox().getHeight() > area.getHeight();
    }

    protected boolean isPositioned() {
        Object positioning = getProperty(Property.POSITION);
        return Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    protected boolean isFixedLayout() {
        Object positioning = getProperty(Property.POSITION);
        return Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    protected void alignChildHorizontally(IRenderer childRenderer, float availableWidth) {
        Property.HorizontalAlignment horizontalAlignment = childRenderer.getProperty(Property.HORIZONTAL_ALIGNMENT);
        if (horizontalAlignment != null && horizontalAlignment != Property.HorizontalAlignment.LEFT) {
            float freeSpace = availableWidth - childRenderer.getOccupiedArea().getBBox().getWidth();
            switch (horizontalAlignment) {
                case RIGHT:
                    childRenderer.move(freeSpace, 0);
                    break;
                case CENTER:
                    childRenderer.move(freeSpace / 2, 0);
                    break;
            }
        }
    }

    /**
     * Gets borders of the element in the specified order: top, right, bottom, left.
     *
     * @return an array of BorderDrawer objects.
     * In case when certain border isn't set <code>Property.BORDER</code> is used,
     * and if <code>Property.BORDER</code> is also not set then <code>null<code/> is returned
     * on position of this border
     */
    protected Border[] getBorders() {
        Border border = getProperty(Property.BORDER);
        Border topBorder = getProperty(Property.BORDER_TOP);
        Border rightBorder = getProperty(Property.BORDER_RIGHT);
        Border bottomBorder = getProperty(Property.BORDER_BOTTOM);
        Border leftBorder = getProperty(Property.BORDER_LEFT);

        Border[] borders = {topBorder, rightBorder, bottomBorder, leftBorder};

        for (int i = 0; i < borders.length; ++i) {
            if (borders[i] == null)
                borders[i] = border;
        }

        return borders;
    }

    protected AbstractRenderer setBorders(Border border, int borderNumber) {
        switch (borderNumber) {
            case 0 :
                setProperty(Property.BORDER_TOP, border);
                break;
            case 1 :
                setProperty(Property.BORDER_RIGHT, border);
                break;
            case 2 :
                setProperty(Property.BORDER_BOTTOM, border);
                break;
            case 3 :
                setProperty(Property.BORDER_LEFT, border);
                break;
        }

        return this;
    }
}
