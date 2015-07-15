package com.itextpdf.model.renderer;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;
import com.itextpdf.model.layout.LineLayoutResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParagraphRenderer extends AbstractRenderer {

    protected float previousDescent = 0;

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
            float relativeX = isFixedLayout() ? 0 : parentBBox.getX();
            areas = Collections.singletonList(new LayoutArea(layoutContext.getArea().getPageNumber(), new Rectangle(relativeX + x, parentBBox.getY(), parentBBox.getWidth() - x, parentBBox.getHeight())));
        }
        else {
            areas = initElementAreas(layoutContext);
        }
        int currentAreaPos = 0;

        int pageNumber = areas.get(0).getPageNumber();
        Rectangle layoutBox = applyMargins(areas.get(0).getBBox().clone(), false);
        applyBorderBox(layoutBox, false);
        Float blockWidth = getPropertyAsFloat(Property.WIDTH);
        if (blockWidth != null && blockWidth < layoutBox.getWidth()) {
            layoutBox.setWidth(blockWidth);
        }
        applyPaddings(layoutBox, false);
        occupiedArea = new LayoutArea(pageNumber, new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), layoutBox.getWidth(), 0));

        boolean anythingPlaced = false;
        boolean firstLineInBox = true;

        LineRenderer currentRenderer = (LineRenderer) childRenderers.get(0);
        LineRenderer initialRenderer = (LineRenderer) childRenderers.get(0);
        childRenderers.clear();

        float lastYLine = layoutBox.getY() + layoutBox.getHeight();
        Property.Leading leading = getProperty(Property.LEADING);
        float leadingValue = 0;

        float lastLineHeight = 0;
        float maxLineWidth = 0;

        while (currentRenderer != null) {
            currentRenderer.setProperty(Property.TAB_DEFAULT, getPropertyAsFloat(Property.TAB_DEFAULT));
            currentRenderer.setProperty(Property.TAB_STOPS, getProperty(Property.TAB_STOPS));

            float lineIndent = anythingPlaced ? 0 : getPropertyAsFloat(Property.FIRST_LINE_INDENT);
            float availableWidth = layoutBox.getWidth() - lineIndent;
            Rectangle childLayoutBox = new Rectangle(layoutBox.getX() + lineIndent, layoutBox.getY(), availableWidth, layoutBox.getHeight());
            LineLayoutResult result = currentRenderer.layout(new LayoutContext(new LayoutArea(pageNumber, childLayoutBox)));

            LineRenderer processedRenderer = null;
            if (result.getStatus() == LayoutResult.FULL) {
                processedRenderer = currentRenderer;
            } else if (result.getStatus() == LayoutResult.PARTIAL) {
                processedRenderer = (LineRenderer) result.getSplitRenderer();
            }

            Property.HorizontalAlignment horizontalAlignment = getProperty(Property.HORIZONTAL_ALIGNMENT);
            if (result.getStatus() == LayoutResult.PARTIAL && horizontalAlignment == Property.HorizontalAlignment.JUSTIFIED && !result.isSplitForcedByNewline() ||
                    horizontalAlignment == Property.HorizontalAlignment.JUSTIFIED_ALL) {
                if (processedRenderer != null) {
                    processedRenderer.justify(layoutBox.getWidth() - lineIndent);
                }
            } else if (horizontalAlignment != null && horizontalAlignment != Property.HorizontalAlignment.LEFT && processedRenderer != null) {
                float deltaX = availableWidth - processedRenderer.getOccupiedArea().getBBox().getWidth();
                switch (horizontalAlignment) {
                    case RIGHT:
                        processedRenderer.move(deltaX, 0);
                        break;
                    case CENTER:
                        processedRenderer.move(deltaX / 2, 0);
                        break;
                }
            }

            leadingValue = processedRenderer != null && leading != null ? processedRenderer.getLeadingValue(leading) : 0;
            if (processedRenderer != null && processedRenderer.containsImage()){
                leadingValue -= previousDescent;
            }
            boolean doesNotFit = result.getStatus() == LayoutResult.NOTHING ||
                    processedRenderer != null && leading != null && processedRenderer.getOccupiedArea().getBBox().getHeight() + processedRenderer.getLeadingValue(leading) - processedRenderer.getMaxAscent() > layoutBox.getHeight();

            if (doesNotFit) {
                // TODO avoid infinite loop
                if (currentAreaPos + 1 < areas.size()) {
                    layoutBox = applyMargins(areas.get(++currentAreaPos).getBBox().clone(), false);
                    layoutBox = applyBorderBox(layoutBox, false);
                    layoutBox = applyPaddings(layoutBox, false);
                    lastYLine = layoutBox.getY() + layoutBox.getHeight();
                    firstLineInBox = true;
                    continue;
                } else {
                    applyPaddings(occupiedArea.getBBox(), true);
                    applyBorderBox(occupiedArea.getBBox(), true);
                    applyMargins(occupiedArea.getBBox(), true);
                    ParagraphRenderer[] split = split();
                    split[0].childRenderers = new ArrayList<>(childRenderers);
                    split[1].childRenderers.add(currentRenderer);
                    boolean keepTogether = getProperty(Property.KEEP_TOGETHER);
                    if (keepTogether) {
                        split[0] = null;
                        childRenderers.clear();
                        childRenderers.add(initialRenderer);
                        split[1] = this;
                        anythingPlaced = false;
                    }
                    return new LayoutResult(anythingPlaced ? LayoutResult.PARTIAL : LayoutResult.NOTHING, occupiedArea, split[0], split[1]);
                }
            } else {
                lastLineHeight = processedRenderer.getOccupiedArea().getBBox().getHeight();
                if (leading != null) {
                    float deltaY = lastYLine - leadingValue - processedRenderer.getYLine();
                    // for the first and last line in a paragraph, leading is smaller
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
                previousDescent = processedRenderer.getMaxDescent();
                maxLineWidth = Math.max(processedRenderer.getOccupiedArea().getBBox().getWidth(), maxLineWidth);
            }
        }

        if (!isPositioned()) {
            occupiedArea.getBBox().moveDown((leadingValue - lastLineHeight) / 2);
            occupiedArea.getBBox().setHeight(occupiedArea.getBBox().getHeight() + (leadingValue - lastLineHeight) / 2);
        }
        Float blockHeight = getPropertyAsFloat(Property.HEIGHT);
        applyPaddings(occupiedArea.getBBox(), true);
        if (blockHeight != null && blockHeight > occupiedArea.getBBox().getHeight()) {
            occupiedArea.getBBox().moveDown(blockHeight - occupiedArea.getBBox().getHeight()).setHeight(blockHeight);
            //applyVerticalAlignment();
        }
        if (isPositioned()) {
            float y = getPropertyAsFloat(Property.Y);
            float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
            move(0, relativeY + y - occupiedArea.getBBox().getY());
        }

        applyBorderBox(occupiedArea.getBBox(), true);
        applyMargins(occupiedArea.getBBox(), true);
        if (getProperty(Property.ROTATION_ANGLE) != null) {
            calculateRotationPointAndRotate(maxLineWidth);

            if (isNotFittingHeight(layoutContext.getArea())) {
                childRenderers.clear();
                childRenderers.add(initialRenderer);
                return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, this);
            }
        }
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

//    protected void applyVerticalAlignment() {
//        Property.VerticalAlignment verticalAlignment = getProperty(Property.VERTICAL_ALIGNMENT);
//        if (verticalAlignment != null && verticalAlignment != Property.VerticalAlignment.TOP && childRenderers.size() > 0) {
//            float deltaY = childRenderers.get(childRenderers.size() - 1).getOccupiedArea().getBBox().getY() - occupiedArea.getBBox().getY();
//            switch (verticalAlignment) {
//                case BOTTOM:
//                    for (IRenderer child : childRenderers) {
//                        child.move(0, -deltaY);
//                    }
//                    break;
//                case MIDDLE:
//                    for (IRenderer child : childRenderers) {
//                        child.move(0, -deltaY / 2);
//                    }
//                    break;
//            }
//        }
//    }

    private void calculateRotationPointAndRotate(float maxLineWidth) {
        float x = occupiedArea.getBBox().getX();
        float y = occupiedArea.getBBox().getY();
        Property.HorizontalAlignment rotationAlignment = getProperty(Property.ROTATION_ALIGNMENT);
        if (rotationAlignment != null) {
            if (rotationAlignment == Property.HorizontalAlignment.CENTER) {
                x += maxLineWidth / 2;
            }
            else if (rotationAlignment == Property.HorizontalAlignment.RIGHT) {
                x += maxLineWidth;
            }
        }
        applyRotationLayout(x, y);
    }
}
