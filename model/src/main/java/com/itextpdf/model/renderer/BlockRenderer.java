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

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea layoutArea = layoutContext.getArea();
        occupiedArea = new LayoutArea(layoutArea.getPageNumber(), new Rectangle(layoutArea.getBBox().getX(), layoutArea.getBBox().getY() + layoutArea.getBBox().getHeight(), layoutArea.getBBox().getWidth(), 0));
        for (IRenderer childRenderer : childRenderers) {
            List<IRenderer> resultRenderers = new ArrayList<IRenderer>();
            LayoutResult result;
            LayoutArea currentArea = layoutArea.clone();
            while ((result = childRenderer.layout(new LayoutContext(currentArea))).getStatus() != LayoutResult.FULL) {
                if (result.getStatus() == LayoutResult.PARTIAL) {
                    resultRenderers.add(childRenderer);
                }
            }
            occupiedArea.getBBox().moveDown(result.getOccupiedArea().getBBox().getHeight()).
                    setHeight(occupiedArea.getBBox().getHeight() + result.getOccupiedArea().getBBox().getHeight());
            resultRenderers.add(childRenderer);
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null);
    }

    @Override
    public IRenderer split() {
        throw new RuntimeException();
    }

}
