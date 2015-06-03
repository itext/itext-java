package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.Collections;
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

    public void addChildFront(IRenderer renderer) {
        if (childRenderers.size() == 0) {
            super.addChild(new LineRenderer());
        }
        LineRenderer lineRenderer = (LineRenderer) childRenderers.get(0);
        lineRenderer.addChildFront(renderer);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        List<LayoutArea> areas;
        if (isPositioned()) {
            float x = getPropertyAsFloat(Property.X);
            Rectangle parentBBox = layoutContext.getArea().getBBox();
            areas = Collections.singletonList(new LayoutArea(layoutContext.getArea().getPageNumber(), new Rectangle(parentBBox.getX() + x, parentBBox.getY(), parentBBox.getWidth() - x, parentBBox.getHeight())));
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
        occupiedArea = new LayoutArea(pageNumber, new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), layoutBox.getWidth(), 0));

        boolean anythingPlaced = false;
        boolean firstLineInBox = true;

        LineRenderer currentRenderer = (LineRenderer) childRenderers.get(0);
        childRenderers.clear();

        float lastYLine = layoutBox.getY() + layoutBox.getHeight();
        Property.Leading leading = getProperty(Property.LEADING);
        float leadingValue = 0;

        float lastLineHeight = 0;

        while (currentRenderer != null) {
            float lineIndent = anythingPlaced ? 0 : getPropertyAsFloat(Property.FIRST_LINE_INDENT);
            Rectangle childLayoutBox = new Rectangle(layoutBox.getX() + lineIndent, layoutBox.getY(), layoutBox.getWidth() - lineIndent, layoutBox.getHeight());
            LayoutResult result = currentRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, childLayoutBox)));

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
                    layoutBox = applyMargins(areas.get(++currentAreaPos).getBBox().clone(), false);
                    layoutBox = applyPaddings(layoutBox, false);
                    lastYLine = layoutBox.getY() + layoutBox.getHeight();
                    firstLineInBox = true;
                    continue;
                } else {
                    ParagraphRenderer[] split = split();
                    split[0].childRenderers = new ArrayList<>(childRenderers);
                    split[1].childRenderers.add(currentRenderer);
                    applyPaddings(occupiedArea.getBBox(), true);
                    applyMargins(occupiedArea.getBBox(), true);
                    return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
                }
            } else {
                lastLineHeight = processedRenderer.getOccupiedArea().getBBox().getHeight();
                if (leading != null) {
                    float deltaY = lastYLine - leadingValue - processedRenderer.getYLine();
                    if (firstLineInBox)
                        deltaY = -(leadingValue - lastLineHeight) / 2;
                    processedRenderer.move(0, deltaY);
                    lastYLine = processedRenderer.getYLine();
                }
                occupiedArea.setBBox(Rectangle.getCommonRectangle(occupiedArea.getBBox(), processedRenderer.getOccupiedArea().getBBox()));
                layoutBox.setHeight(processedRenderer.getOccupiedArea().getBBox().getY() - layoutBox.getY());
                childRenderers.add(processedRenderer);

                anythingPlaced = true;
                firstLineInBox = false;

                currentRenderer = (LineRenderer) result.getOverflowRenderer();
            }
        }

        occupiedArea.getBBox().moveDown((leadingValue - lastLineHeight) / 2);
        occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + (leadingValue - lastLineHeight) / 2);
        Float blockHeight = getPropertyAsFloat(Property.HEIGHT);
        applyPaddings(occupiedArea.getBBox(), true);
        if (blockHeight != null && blockHeight > occupiedArea.getBBox().getHeight()) {
            occupiedArea.getBBox().moveDown(blockHeight - occupiedArea.getBBox().getHeight()).setHeight(blockHeight);
        }
        if (isPositioned()) {
            float y = getPropertyAsFloat(Property.Y);
            move(0, layoutBox.getY() + y - occupiedArea.getBBox().getY());
        }
        applyMargins(occupiedArea.getBBox(), true);

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    protected ParagraphRenderer createOverflowRenderer() {
        ParagraphRenderer overflowRenderer = new ParagraphRenderer(modelElement);
        // Reset first line indent in case of overflow.
        float firstLineIndent = getPropertyAsFloat(Property.FIRST_LINE_INDENT);
        if (firstLineIndent != 0) {
            overflowRenderer.setProperty(Property.FIRST_LINE_INDENT, 0);
        }
        return overflowRenderer;
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
