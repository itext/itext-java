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

        int pageNumber = areas.get(0).getPageNumber();
        Rectangle layoutBox = applyMargins(areas.get(0).getBBox().clone(), false);
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), null);

        boolean anythingPlaced = false;

        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult result;
            while ((result = childRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, layoutBox)))).getStatus() != LayoutResult.FULL) {
                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());

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
                    layoutBox = areas.get(++currentAreaPos).getBBox().clone();
                    break;
                } else {
                    if (result.getStatus() == LayoutResult.PARTIAL) {

                       layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());

                        if (currentAreaPos + 1 == areas.size()) {
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

                            applyMargins(occupiedArea.getBBox(), true);
                            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer);
                        } else {
                            childRenderers.set(childPos, result.getSplitRenderer());
                            childRenderers.add(childPos + 1, result.getOverflowRenderer());
                            layoutBox = areas.get(++currentAreaPos).getBBox().clone();
                            break;
                        }
                    } else if (result.getStatus() == LayoutResult.NOTHING) {
                        BlockRenderer splitRenderer = createSplitRenderer();
                        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));

                        BlockRenderer overflowRenderer = createOverflowRenderer();
                        List<IRenderer> overflowRendererChildren = new ArrayList<IRenderer>();
                        overflowRendererChildren.add(result.getOverflowRenderer());
                        overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                        overflowRenderer.childRenderers = overflowRendererChildren;

                        applyMargins(occupiedArea.getBBox(), true);
                        return new LayoutResult(LayoutResult.NOTHING, occupiedArea, splitRenderer, overflowRenderer);
                    }
                }
            }

            anythingPlaced = true;

            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
            if (result.getStatus() == LayoutResult.FULL)
                layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());

        }

        ensureOccupiedAreaNotNull(layoutContext.getArea());
        applyMargins(occupiedArea.getBBox(), true);
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    protected BlockRenderer createSplitRenderer() {
        BlockRenderer splitRenderer = new BlockRenderer((BlockElement) modelElement);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        return splitRenderer;
    }

    @Override
    protected BlockRenderer createOverflowRenderer() {
        BlockRenderer overflowRenderer = new BlockRenderer((BlockElement) modelElement);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        return overflowRenderer;
    }

    private void ensureOccupiedAreaNotNull(LayoutArea area) {
        if (occupiedArea.getBBox() == null)
            occupiedArea.setBBox(area.getBBox().clone().moveDown(-area.getBBox().getHeight()).setHeight(0));
    }


}
