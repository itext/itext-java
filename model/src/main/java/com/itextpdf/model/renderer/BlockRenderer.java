package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.BlockElement;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockRenderer extends AbstractRenderer {

    public BlockRenderer(BlockElement modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        List<LayoutArea> areas;
        if (isPositioned()) {
            float x = getPropertyAsFloat(Property.X);
            Rectangle parentBBox = layoutContext.getArea().getBBox();
            float relativeX = isFixedLayout() ? 0 : parentBBox.getX();
            areas = Collections.singletonList(new LayoutArea(layoutContext.getArea().getPageNumber(), new Rectangle(relativeX + x, parentBBox.getY(), parentBBox.getWidth() - x, parentBBox.getHeight())));
        }
        else {
            areas = initElementAreas(layoutContext);
        }
        int currentAreaPos = 0;

        int pageNumber = areas.get(0).getPageNumber();
        Rectangle layoutBox = applyMargins(areas.get(0).getBBox().clone(), false);
        Float blockWidth = getPropertyAsFloat(Property.WIDTH);
        if (blockWidth != null && blockWidth < layoutBox.getWidth()) {
            layoutBox.setWidth(blockWidth);
        }
        applyPaddings(layoutBox, false);
        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), null);

        boolean anythingPlaced = false;

        for (int childPos = 0; childPos < childRenderers.size(); childPos++) {
            IRenderer childRenderer = childRenderers.get(childPos);
            LayoutResult result;
            while ((result = childRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, layoutBox)))).getStatus() != LayoutResult.FULL) {
                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
                layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());

                if (childRenderer.getProperty(Property.WIDTH) != null) {
                    alignChildHorizontally(childRenderer, layoutBox.getWidth());
                }

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
                            BlockRenderer splitRenderer = createSplitRenderer(LayoutResult.PARTIAL);
                            splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));
                            splitRenderer.childRenderers.add(result.getSplitRenderer());
                            splitRenderer.occupiedArea = occupiedArea.clone();

                            BlockRenderer overflowRenderer = createOverflowRenderer(LayoutResult.PARTIAL);
                            List<IRenderer> overflowRendererChildren = new ArrayList<>();
                            overflowRendererChildren.add(result.getOverflowRenderer());
                            overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                            overflowRenderer.childRenderers = overflowRendererChildren;

                            applyPaddings(occupiedArea.getBBox(), false);
                            applyMargins(occupiedArea.getBBox(), true);
                            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, overflowRenderer);
                        } else {
                            childRenderers.set(childPos, result.getSplitRenderer());
                            childRenderers.add(childPos + 1, result.getOverflowRenderer());
                            layoutBox = areas.get(++currentAreaPos).getBBox().clone();
                            break;
                        }
                    } else if (result.getStatus() == LayoutResult.NOTHING) {
                        boolean keepTogether = Boolean.valueOf(true).equals(getProperty(Property.KEEP_TOGETHER));
                        int layoutResult = anythingPlaced && !keepTogether ? LayoutResult.PARTIAL : LayoutResult.NOTHING;

                        BlockRenderer splitRenderer = createSplitRenderer(layoutResult);
                        splitRenderer.childRenderers = new ArrayList<>(childRenderers.subList(0, childPos));

                        BlockRenderer overflowRenderer = createOverflowRenderer(layoutResult);
                        List<IRenderer> overflowRendererChildren = new ArrayList<>();
                        overflowRendererChildren.add(result.getOverflowRenderer());
                        overflowRendererChildren.addAll(childRenderers.subList(childPos + 1, childRenderers.size()));
                        overflowRenderer.childRenderers = overflowRendererChildren;
                        if (getProperty(Property.KEEP_TOGETHER)) {
                            splitRenderer = null;
                            overflowRenderer.childRenderers.clear();
                            overflowRenderer.childRenderers = new ArrayList<>(childRenderers);
                        }

                        applyPaddings(occupiedArea.getBBox(), false);
                        applyMargins(occupiedArea.getBBox(), true);
                        return new LayoutResult(layoutResult, occupiedArea, splitRenderer, overflowRenderer);
                    }
                }
            }

            anythingPlaced = true;

            occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), result.getOccupiedArea().getBBox()));
            if (result.getStatus() == LayoutResult.FULL) {
                layoutBox.setHeight(layoutBox.getHeight() - result.getOccupiedArea().getBBox().getHeight());
                if (childRenderer.getProperty(Property.WIDTH) != null) {
                    alignChildHorizontally(childRenderer, layoutBox.getWidth());
                }
            }

        }

        ensureOccupiedAreaNotNull(layoutContext.getArea());
        Float blockHeight = getPropertyAsFloat(Property.HEIGHT);
        applyPaddings(occupiedArea.getBBox(), true);
        if (blockHeight != null && blockHeight > occupiedArea.getBBox().getHeight()) {
            occupiedArea.getBBox().moveDown(blockHeight - occupiedArea.getBBox().getHeight()).setHeight(blockHeight);
        }
        if (isPositioned()) {
            float y = getPropertyAsFloat(Property.Y);
            float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
            move(0, relativeY + y - occupiedArea.getBBox().getY());
        }

        applyMargins(occupiedArea.getBBox(), true);
        if (getProperty(Property.ANGLE) != null) {
            applyRotationLayout();
            if (isNotFittingHeight(layoutContext.getArea())) {
                return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
            }
        }
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    protected BlockRenderer createSplitRenderer(int layoutResult) {
        BlockRenderer splitRenderer = new BlockRenderer((BlockElement) modelElement);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        return splitRenderer;
    }

    protected BlockRenderer createOverflowRenderer(int layoutResult) {
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
