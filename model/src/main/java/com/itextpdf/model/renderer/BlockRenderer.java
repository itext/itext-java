package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.element.BlockElement;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.List;

public class BlockRenderer extends AbstractRenderer {

    public BlockRenderer(BlockElement modelElement) {
        super(modelElement);
    }

    // TODO All in-flow children of a block flow must be blocks, or all in-flow children of a block flow must be inlines.
    // https://www.webkit.org/blog/115/webcore-rendering-ii-blocks-and-inlines/
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea layoutArea = layoutContext.getArea();
        occupiedArea = new LayoutArea(layoutArea.getPageNumber(), new Rectangle(layoutArea.getBBox().getX(), layoutArea.getBBox().getY() + layoutArea.getBBox().getHeight(), layoutArea.getBBox().getWidth(), 0));
        boolean anythingPlaced = false;
        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            List<IRenderer> resultRenderers = new ArrayList<IRenderer>();
            LayoutResult result;
            LayoutArea currentArea = layoutArea.clone();
            while ((result = childRenderer.layout(new LayoutContext(currentArea))).getStatus() != LayoutResult.FULL) {
                if (result.getStatus() == LayoutResult.PARTIAL) {
                    resultRenderers.add(childRenderer);

                    BlockRenderer splitRenderer = new BlockRenderer((BlockElement) modelElement);
                    splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                    splitRenderer.childRenderers.add(result.getSplitRenderer());
                    splitRenderer.occupiedArea = occupiedArea.clone();
                    splitRenderer.parent = parent;

                    BlockRenderer overflowRenderer = new BlockRenderer((BlockElement) modelElement);
                    List<IRenderer> overflowRendererChildren = new ArrayList<IRenderer>();
                    overflowRendererChildren.add(result.getOverflowRenderer());
                    overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                    overflowRenderer.childRenderers = overflowRendererChildren;
                    overflowRenderer.parent = parent;

                    return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer);
                } else if (result.getStatus() == LayoutResult.NOTHING) {
                    BlockRenderer splitRenderer = new BlockRenderer((BlockElement) modelElement);
                    BlockRenderer overflowRenderer = new BlockRenderer((BlockElement) modelElement);
                    return new LayoutResult(LayoutResult.NOTHING, occupiedArea, splitRenderer, overflowRenderer);
                }
            }

            anythingPlaced = true;

            occupiedArea.getBBox().moveDown(result.getOccupiedArea().getBBox().getHeight()).
                    setHeight(occupiedArea.getBBox().getHeight() + result.getOccupiedArea().getBBox().getHeight());
            layoutArea.getBBox().setHeight(layoutArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());
            resultRenderers.add(childRenderer);
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }


}
