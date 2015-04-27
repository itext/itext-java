package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutRect;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;

public class LineRenderer extends AbstractRenderer {

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        Rectangle layoutBox = layoutContext.getArea().getBBox().clone();
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), layoutBox.clone().moveDown(-layoutBox.getHeight()).setHeight(0));

        float curWidth = 0;
        float maxHeight = 0;
        int childPos = 0;

        boolean anythingPlaced = false;

        while (childPos < childRenderers.size()) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutRect childSize = getElementSize(childRenderer);
            LayoutResult childResult;
            if (childSize.getWidth() != null) {
                Rectangle bbox = new Rectangle(layoutBox.getX() + curWidth, layoutBox.getY(), childSize.getWidth(), layoutBox.getHeight());
                childResult = childRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));
                curWidth += childSize.getWidth();
                maxHeight = Math.max(maxHeight, childResult.getOccupiedArea().getBBox().getHeight());
            } else {
                Rectangle bbox = new Rectangle(layoutBox.getX() + curWidth, layoutBox.getY(), layoutBox.getWidth() - curWidth, layoutBox.getHeight());
                childResult = childRenderer.layout(new LayoutContext(new LayoutArea(layoutContext.getArea().getPageNumber(), bbox)));
                curWidth += childResult.getOccupiedArea().getBBox().getWidth();
                maxHeight = Math.max(maxHeight, childResult.getOccupiedArea().getBBox().getHeight());

                occupiedArea.setBBox(new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight() - maxHeight, curWidth, maxHeight));
            }

            if (childResult.getStatus() != LayoutResult.FULL) {
                LineRenderer[] split = split();
                split[0].childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                if (childResult.getStatus() == LayoutResult.PARTIAL) {
                    split[0].addChild(childResult.getSplitRenderer());
                    anythingPlaced = true;
                }
                split[1].childRenderers.add(childResult.getOverflowRenderer());
                split[1].childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));

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
        for (IRenderer renderer : childRenderers) {
            renderer.getOccupiedArea().getBBox().setY(occupiedArea.getBBox().getY());
        }
        return this;
    }
}
