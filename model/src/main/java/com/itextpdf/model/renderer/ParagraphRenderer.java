package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.List;

public class ParagraphRenderer extends AbstractRenderer {

    public ParagraphRenderer(IPropertyContainer modelElement) {
        super(modelElement);
    }

    @Override
    public void addChild(IRenderer renderer) {
        if (childRenderers.size() == 0) {
            super.addChild(new LineRenderer());
        }
        // All the children will be line renderers. Before layout there will be only one of them.
        LineRenderer lineRenderer = (LineRenderer) childRenderers.get(0);
        lineRenderer.addChild(renderer);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        List<LayoutArea> areas = initElementAreas(layoutContext);
        int currentAreaPos = 0;

        LayoutArea area = areas.get(0);
        Rectangle layoutBox = area.getBBox().clone();
        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(area.getBBox().getX(), area.getBBox().getY() + area.getBBox().getHeight(), area.getBBox().getWidth(), 0));

        boolean anythingPlaced = false;

        LineRenderer currentRenderer = (LineRenderer) childRenderers.get(0);
        childRenderers.clear();

        float lastYLine = layoutBox.getY() + layoutBox.getHeight();
        Property.Leading leading = getProperty(Property.LEADING);
        float leadingValue = 0;

        while (currentRenderer != null) {
            LayoutResult result = currentRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));

            LineRenderer processedRenderer = null;
            if (result.getStatus() == LayoutResult.FULL) {
                processedRenderer = currentRenderer;
            } else if (result.getStatus() == LayoutResult.PARTIAL) {
                processedRenderer = (LineRenderer) result.getSplitRenderer();
            }

            leadingValue = processedRenderer != null && leading != null ? processedRenderer.getLeadingValue(leading) : 0;
            boolean doesNotFit = result.getStatus() == LayoutResult.NOTHING ||
                    processedRenderer != null && leading != null && processedRenderer.getOccupiedArea().getBBox().getHeight() + processedRenderer.getLeadingValue(leading) - processedRenderer.getMaxAscent() > layoutBox.getHeight();

            if (doesNotFit) {
                // TODO avoid infinite loop
                if (currentAreaPos + 1 < areas.size()) {
                    layoutBox = areas.get(++currentAreaPos).getBBox();
                    area = areas.get(currentAreaPos);
                    lastYLine = layoutBox.getY() + layoutBox.getHeight();
                    continue;
                } else {
                    ParagraphRenderer[] split = split();
                    split[0].childRenderers = new ArrayList<>(childRenderers);
                    split[1].childRenderers.add(currentRenderer);
                    return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
                }
            } else {
                if (leading != null) {
                    processedRenderer.move(0, lastYLine - leadingValue - processedRenderer.getYLine());
                    lastYLine = processedRenderer.getYLine();
                }
                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), processedRenderer.getOccupiedArea().getBBox()));
                layoutBox.setHeight(processedRenderer.getOccupiedArea().getBBox().getY() - layoutBox.getY());
                childRenderers.add(processedRenderer);

                anythingPlaced = true;

                currentRenderer = (LineRenderer) result.getOverflowRenderer();
            }
        }

        occupiedArea.getBBox().moveDown(leadingValue / 2);
        occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + leadingValue / 2);

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    protected ParagraphRenderer createOverflowRenderer() {
        return new ParagraphRenderer(modelElement);
    }

    @Override
    protected ParagraphRenderer createSplitRenderer() {
        return new ParagraphRenderer(modelElement);
    }

    protected ParagraphRenderer[] split() {
        ParagraphRenderer splitRenderer = createSplitRenderer();
        splitRenderer.occupiedArea = occupiedArea.clone();
        splitRenderer.parent = parent;

        ParagraphRenderer overflowRenderer = createOverflowRenderer();
        overflowRenderer.parent = parent;

        return new ParagraphRenderer[] {splitRenderer, overflowRenderer};
    }
}
