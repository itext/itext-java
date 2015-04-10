package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.element.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutRect;
import com.itextpdf.model.layout.LayoutResult;

public class InlineRenderer extends AbstractRenderer {

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(area.getBBox().getX(), area.getBBox().getY() + area.getBBox().getHeight(), area.getBBox().getWidth(), 0));
        int childPos = 0;
        boolean anythingPlaced = false;
        while (childPos < childRenderers.size()) {
            IRenderer childRenderer = childRenderers.get(childPos);
            float maxHeight = 0;
            float curWidth = 0;
            int lineInitialChildPos = childPos;
            while (childPos < childRenderers.size()) {
                LayoutRect childSize=  getElementSize(childRenderer);
                if (childSize.getWidth() != null && curWidth + childSize.getWidth() < area.getBBox().getWidth()) {
                    Rectangle bbox = new Rectangle(area.getBBox().getX() + curWidth, area.getBBox().getY(), childSize.getWidth(), area.getBBox().getHeight() - occupiedArea.getBBox().getHeight());
                    LayoutResult childResult = childRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), bbox)));
                    curWidth += childSize.getWidth();
                    maxHeight = Math.max(maxHeight, childResult.getOccupiedArea().getBBox().getHeight());
                } else if (childSize.getWidth() == null) {
                    Rectangle bbox = new Rectangle(area.getBBox().getX() + curWidth, area.getBBox().getY(), area.getBBox().getWidth() - curWidth, area.getBBox().getHeight() - occupiedArea.getBBox().getHeight());
                    LayoutResult childResult = childRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), bbox)));
                    curWidth = area.getBBox().getWidth();
                    maxHeight = Math.max(maxHeight, childResult.getOccupiedArea().getBBox().getHeight());
                }
                childPos++;
            }

            if (maxHeight > area.getBBox().getHeight()) {
                // the line does not fit because of height - full overflow
                // TODO set parent, occupied area, params
                InlineRenderer splitRenderer = new InlineRenderer();
                splitRenderer.childRenderers = childRenderers.subList(0, lineInitialChildPos);
                InlineRenderer overflowRenderer = new InlineRenderer();
                overflowRenderer.childRenderers = childRenderers.subList(lineInitialChildPos, childRenderers.size());
                return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, splitRenderer, overflowRenderer);
            } else {
                occupiedArea.getBBox().moveDown(maxHeight);
                occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + maxHeight);
                anythingPlaced = true;
            }
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public LayoutArea getNextArea() {
        throw new RuntimeException();
    }

    protected LayoutRect getElementSize(IRenderer renderer) {
        Float width = renderer.getProperty(Property.WIDTH);
        Float height = renderer.getProperty(Property.HEIGHT);
        return new LayoutRect(width, height);
    }

}
