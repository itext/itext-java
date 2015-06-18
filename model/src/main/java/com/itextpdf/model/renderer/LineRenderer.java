package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.*;

import java.util.ArrayList;

public class LineRenderer extends AbstractRenderer {

    protected float maxAscent;
    protected float maxDescent;

    public void addChildFront(IRenderer child) {
        childRenderers.add(0, child);
        child.setParent(this);
    }

    @Override
    public LineLayoutResult layout(LayoutContext layoutContext) {
        Rectangle layoutBox = layoutContext.getArea().getBBox().clone();
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), layoutBox.clone().moveDown(-layoutBox.getHeight()).setHeight(0));

        float curWidth = 0;
        maxAscent = 0;
        maxDescent = 0;
        int childPos = 0;

        boolean anythingPlaced = false;

        while (childPos < childRenderers.size()) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutRect childSize = getElementSize(childRenderer);
            LayoutResult childResult;

            if (!anythingPlaced && childRenderer instanceof TextRenderer) {
                ((TextRenderer) childRenderer).trimFirst();
            }

            Rectangle bbox = new Rectangle(layoutBox.getX() + curWidth, layoutBox.getY(), layoutBox.getWidth() - curWidth, layoutBox.getHeight());
            childResult = childRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));
            curWidth += childResult.getOccupiedArea().getBBox().getWidth();


            float childAscent = 0;
            float childDescent = 0;
            if (childRenderer instanceof TextRenderer) {
                childAscent = ((TextRenderer) childRenderer).getAscent();
                childDescent = ((TextRenderer) childRenderer).getDescent();
            } else if (childRenderer instanceof ImageRenderer) {
                childAscent = childRenderer.getOccupiedArea().getBBox().getHeight();
            }

            maxAscent = Math.max(maxAscent, childAscent);
            maxDescent = Math.min(maxDescent, childDescent);
            float maxHeight = maxAscent - maxDescent;
            occupiedArea.setBBox(new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight() - maxHeight, curWidth, maxHeight));

            if (childResult.getStatus() != LayoutResult.FULL) {
                LineRenderer[] split = split();
                split[0].childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                if (childResult instanceof TextLayoutResult && ((TextLayoutResult) childResult).isWordHasBeenSplit() && bbox.getWidth() < layoutBox.getWidth()) {
                    split[1].childRenderers.add(childRenderer);
                    split[1].childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                } else {
                    if (childResult.getStatus() == LayoutResult.PARTIAL) {
                        split[0].addChild(childResult.getSplitRenderer());
                        anythingPlaced = true;
                    }

                    split[1].childRenderers.add(childResult.getOverflowRenderer());
                    split[1].childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                }

                split[0].adjustChildrenYLine().trimLast();
                LineLayoutResult result = new LineLayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
                if (childResult.getStatus() == LayoutResult.PARTIAL && childResult instanceof TextLayoutResult && ((TextLayoutResult) childResult).isSplitForcedByNewline())
                    result.setSplitForcedByNewline(true);
                return result;
            } else {
                anythingPlaced = true;
                childPos++;
            }
        }

        if (anythingPlaced) {
            adjustChildrenYLine().trimLast();
            return new LineLayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        } else {
            return new LineLayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
        }
    }

    public float getMaxAscent() {
        return maxAscent;
    }

    public float getMaxDescent() {
        return maxDescent;
    }

    public float getYLine() {
        return occupiedArea.getBBox().getY() - maxDescent;
    }

    public float getLeadingValue(Property.Leading leading) {
        switch (leading.getType()) {
            case Property.Leading.FIXED:
                return leading.getValue();
            case Property.Leading.MULTIPLIED:
                return occupiedArea.getBBox().getHeight() * leading.getValue();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected float getFirstYLineRecursively() {
        return getYLine();
    }

    protected void justify(float width) {
        float ratio = getPropertyAsFloat(Property.SPACING_RATIO);
        float freeWidth = occupiedArea.getBBox().getX() + width -
                getLastChildRenderer().getOccupiedArea().getBBox().getX() - getLastChildRenderer().getOccupiedArea().getBBox().getWidth();
        int numberOfSpaces = getNumberOfSpaces();
        int lineLength = length();
        float baseFactor = freeWidth / (ratio * numberOfSpaces + (1 - ratio) * (lineLength - 1));
        float wordSpacing = ratio * baseFactor;
        float characterSpacing = (1 - ratio) * baseFactor;

        float lastRightPos = occupiedArea.getBBox().getX();
        for (IRenderer child : childRenderers) {
            float childX = child.getOccupiedArea().getBBox().getX();
            child.move(lastRightPos - childX, 0);
            childX = lastRightPos;
            if (child instanceof TextRenderer) {
                float childHSCale = child.getProperty(Property.HORIZONTAL_SCALING);
                child.setProperty(Property.CHARACTER_SPACING, characterSpacing / childHSCale);
                child.setProperty(Property.WORD_SPACING, wordSpacing / childHSCale);
                child.getOccupiedArea().getBBox().setWidth(child.getOccupiedArea().getBBox().getWidth() +
                        characterSpacing * ((TextRenderer) child).length() + wordSpacing * ((TextRenderer) child).getNumberOfSpaces());
            }
            lastRightPos = childX + child.getOccupiedArea().getBBox().getWidth();
        }

        getOccupiedArea().getBBox().setWidth(width);
    }

    protected int getNumberOfSpaces() {
        int spaces = 0;
        for (IRenderer child : childRenderers) {
            if (child instanceof TextRenderer) {
                spaces += ((TextRenderer) child).getNumberOfSpaces();
            }
        }
        return spaces;
    }

    /**
     * Gets the total lengths of characters in this line. Other elements (images, tables) are not taken
     * into account.
     */
    protected int length() {
        int length = 0;
        for (IRenderer child : childRenderers) {
            if (child instanceof TextRenderer) {
                length += ((TextRenderer) child).length();
            }
        }
        return length;
    }

    @Override
    protected LineRenderer createSplitRenderer() {
        return new LineRenderer();
    }

    @Override
    protected LineRenderer createOverflowRenderer() {
        return new LineRenderer();
    }

    protected LineRenderer[] split() {
        LineRenderer splitRenderer = new LineRenderer();
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;
        splitRenderer.maxAscent = maxAscent;
        splitRenderer.maxDescent = maxDescent;

        LineRenderer overflowRenderer = new LineRenderer();
        overflowRenderer.parent = parent;

        return new LineRenderer[] {splitRenderer, overflowRenderer};
    }

    protected LayoutRect getElementSize(IRenderer renderer) {
        Float width = renderer.getProperty(Property.WIDTH);
        Float height = renderer.getProperty(Property.HEIGHT);
        return new LayoutRect(width, height);
    }

    protected LineRenderer adjustChildrenYLine() {
        float actualYLine = occupiedArea.getBBox().getY() + occupiedArea.getBBox().getHeight() - maxAscent;
        for (IRenderer renderer : childRenderers) {
            if (renderer instanceof TextRenderer) {
                ((TextRenderer) renderer).moveYLineTo(actualYLine);
            } else {
                renderer.getOccupiedArea().getBBox().setY(occupiedArea.getBBox().getY());
            }
        }
        return this;
    }

    protected LineRenderer trimLast() {
        IRenderer lastRenderer = childRenderers.size() > 0 ? childRenderers.get(childRenderers.size() - 1) : null;
        if (lastRenderer instanceof TextRenderer) {
            float trimmedSpace = ((TextRenderer) lastRenderer).trimLast();
            occupiedArea.getBBox().setWidth(occupiedArea.getBBox().getWidth() - trimmedSpace);
        }
        return this;
    }

    private IRenderer getLastChildRenderer() {
        return childRenderers.get(childRenderers.size() - 1);
    }
}
