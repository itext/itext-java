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
    public LayoutResult layout(LayoutContext layoutContext) {
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

                split[0].adjustChildrenYLine();
                return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
            } else {
                anythingPlaced = true;
                childPos++;
            }
        }

        if (anythingPlaced) {
            adjustChildrenYLine();
            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        } else {
            return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
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
}
