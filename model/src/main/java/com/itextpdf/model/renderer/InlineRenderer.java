package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.element.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutRect;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;

public class InlineRenderer extends AbstractRenderer {

    public InlineRenderer (IPropertyContainer modelElement) {
        super(modelElement);
    }

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

                    if (childResult.getStatus() != LayoutResult.FULL) {
                        occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), childResult.getOccupiedArea().getBBox()));

                        InlineRenderer splitRenderer = new InlineRenderer(modelElement);
                        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                        splitRenderer.childRenderers.add(childResult.getSplitRenderer());
                        splitRenderer.occupiedArea = occupiedArea.clone();
                        splitRenderer.parent = parent;
                        splitRenderer.modelElement = modelElement;

                        InlineRenderer overflowRenderer = new InlineRenderer(modelElement);
                        overflowRenderer.childRenderers = new ArrayList<>();
                        overflowRenderer.childRenderers.add(childResult.getOverflowRenderer());
                        overflowRenderer.childRenderers.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));

                        overflowRenderer.parent = parent;
                        overflowRenderer.modelElement = modelElement;

                        return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer);
                    }
                }
                childPos++;
            }

            if (maxHeight > area.getBBox().getHeight()) {
                // the line does not fit because of height - full overflow
                // TODO set parent, occupied area, params. A separate method should be created for that.
                InlineRenderer splitRenderer = new InlineRenderer(modelElement);
                splitRenderer.childRenderers = childRenderers.subList(0, lineInitialChildPos);
                splitRenderer.occupiedArea = occupiedArea.clone();
                splitRenderer.parent = parent;
                splitRenderer.modelElement = modelElement;

                InlineRenderer overflowRenderer = new InlineRenderer(modelElement);
                overflowRenderer.childRenderers = childRenderers.subList(lineInitialChildPos, childRenderers.size());
                overflowRenderer.parent = parent;
                overflowRenderer.modelElement = modelElement;

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
    protected InlineRenderer createOverflowRenderer() {
        return new InlineRenderer(modelElement);
    }

    @Override
    protected InlineRenderer createSplitRenderer() {
        return new InlineRenderer(modelElement);
    }

    protected LayoutRect getElementSize(IRenderer renderer) {
        Float width = renderer.getProperty(Property.WIDTH);
        Float height = renderer.getProperty(Property.HEIGHT);
        return new LayoutRect(width, height);
    }

}
