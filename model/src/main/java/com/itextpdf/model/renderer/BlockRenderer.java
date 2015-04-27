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

        List<LayoutArea> areas = initElementAreas(layoutContext);
        int currentAreaPos = 0;

        LayoutArea layoutArea = areas.get(currentAreaPos);
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), null);
        boolean anythingPlaced = false;
        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            List<IRenderer> resultRenderers = new ArrayList<IRenderer>();
            LayoutResult result;
            LayoutArea currentArea = layoutArea.clone();
            while ((result = childRenderer.layout(new LayoutContext(currentArea))).getStatus() != LayoutResult.FULL) {
                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                layoutArea.getBBox().setHeight(layoutArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());

                // have more areas
                if (currentAreaPos + 1 < areas.size()) {
                    if (result.getStatus() == LayoutResult.PARTIAL) {
                        childRenderers.set(childPos, result.getSplitRenderer());
                        // TODO linkedList would make it faster
                        childRenderers.add(childPos + 1, result.getOverflowRenderer());
                    } else {
                        childRenderers.set(childPos, result.getOverflowRenderer());
                        childPos--;
                    }
                    layoutArea = areas.get(++currentAreaPos);
                    break;
                } else {
                    if (result.getStatus() == LayoutResult.PARTIAL) {

                        layoutArea.getBBox().setHeight(layoutArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());

                        if (currentAreaPos + 1 == areas.size()) {

                            resultRenderers.add(childRenderer);

                            BlockRenderer splitRenderer = createSplitRenderer();
                            splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                            splitRenderer.childRenderers.add(result.getSplitRenderer());
                            splitRenderer.occupiedArea = occupiedArea.clone();
                            splitRenderer.parent = parent;
                            splitRenderer.modelElement = modelElement;

                            BlockRenderer overflowRenderer = createOverflowRenderer();
                            List<IRenderer> overflowRendererChildren = new ArrayList<IRenderer>();
                            overflowRendererChildren.add(result.getOverflowRenderer());
                            overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                            overflowRenderer.childRenderers = overflowRendererChildren;
                            overflowRenderer.parent = parent;
                            overflowRenderer.modelElement = modelElement;

                            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer);
                        } else {
                            childRenderers.set(childPos, result.getSplitRenderer());
                            childRenderers.add(childPos + 1, result.getOverflowRenderer());
                            layoutArea = areas.get(++currentAreaPos);
                            break;
                        }
                    } else if (result.getStatus() == LayoutResult.NOTHING) {
                        BlockRenderer splitRenderer = createSplitRenderer();
                        splitRenderer.parent = parent;
                        splitRenderer.modelElement = modelElement;
                        splitRenderer.occupiedArea = result.getOccupiedArea();
                        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));

                        BlockRenderer overflowRenderer = createOverflowRenderer();
                        overflowRenderer.parent = parent;
                        overflowRenderer.modelElement = modelElement;
                        List<IRenderer> overflowRendererChildren = new ArrayList<IRenderer>();
                        overflowRendererChildren.add(result.getOverflowRenderer());
                        overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                        overflowRenderer.childRenderers = overflowRendererChildren;


                        return new LayoutResult(LayoutResult.NOTHING, occupiedArea, splitRenderer, overflowRenderer);
                    }
                }


            }

            anythingPlaced = true;

            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
            if (result.getStatus() == LayoutResult.FULL)
                layoutArea.getBBox().setHeight(layoutArea.getBBox().getHeight() - result.getOccupiedArea().getBBox().getHeight());

            resultRenderers.add(childRenderer);
        }

        ensureOccupiedAreaNotNull(layoutContext.getArea());
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    protected BlockRenderer createSplitRenderer() {
        return new BlockRenderer((BlockElement) modelElement);
    }

    @Override
    protected BlockRenderer createOverflowRenderer() {
        return new BlockRenderer((BlockElement) modelElement);
    }

    private void ensureOccupiedAreaNotNull(LayoutArea area) {
        if (occupiedArea.getBBox() == null)
            occupiedArea.setBBox(area.getBBox().clone().moveDown(-area.getBBox().getHeight()).setHeight(0));
    }


}
