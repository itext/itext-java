package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.ColumnContainer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a renderer for columns.
 */
public class ColumnContainerRenderer extends AbstractRenderer {

    /**
     * Creates a DivRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link ColumnContainer} which this object should manage
     */
    public ColumnContainerRenderer(ColumnContainer modelElement) {
        super(modelElement);
    }

    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(ColumnContainerRenderer.class, this.getClass());
        return new ColumnContainerRenderer((ColumnContainer) modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        ((ColumnContainer)this.getModelElement()).copyAllPropertiesToChildren();
        final Rectangle initialBBox = layoutContext.getArea().getBBox();
        final int columnCount = (int)this.<Integer>getProperty(Property.COLUMN_COUNT);
        final float columnWidth = initialBBox.getWidth() / columnCount;
        if (getChildRenderers().isEmpty() && !(getChildRenderers().get(0) instanceof BlockRenderer)) {
            throw new IllegalStateException("Invalid child renderers, it should be one, " +
                    "not empty and be a block element");
        }
        BlockRenderer blockRenderer = (BlockRenderer) getChildRenderers().get(0);
        blockRenderer.setParent(this);

        LayoutResult prelayoutResult = blockRenderer.layout(
                new LayoutContext(new LayoutArea(1, new Rectangle(columnWidth, INF))));
        if (prelayoutResult.getStatus() != LayoutResult.FULL) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, blockRenderer);
            // check if partial result is possible here
        }

        blockRenderer = prelayoutResult.getSplitRenderer() != null ?
                (BlockRenderer)prelayoutResult.getSplitRenderer() : blockRenderer;

        float approximateHeight = prelayoutResult.getOccupiedArea().getBBox().getHeight() / columnCount;

        approximateHeight = balanceContentBetweenColumns(columnCount, blockRenderer, approximateHeight);

        LayoutArea area = layoutContext.getArea().clone();
        area.getBBox().setHeight(approximateHeight);
        area.getBBox().setY(initialBBox.getY() + initialBBox.getHeight() - area.getBBox().getHeight());

        List<IRenderer> container = layoutColumns(layoutContext, columnCount, columnWidth, approximateHeight);


        this.occupiedArea = area;
        this.setChildRenderers(container);
        LayoutResult result = new LayoutResult(LayoutResult.FULL, area, this, null);

        // process some properties (keepTogether, margin collapsing), area breaks, adding height
        // Check what we do at the end of BlockRenderer

        return result;
    }

    private List<IRenderer> layoutColumns(LayoutContext preLayoutContext, int columnCount,
                                          float columnWidth, float approximateHeight) {
        List<IRenderer> container = new ArrayList<>();
        Rectangle initialBBox = preLayoutContext.getArea().getBBox();
        IRenderer renderer = getChildRenderers().get(0);
        for (int i = 0; i < columnCount && renderer != null; i++) {
            LayoutArea tempArea = preLayoutContext.getArea().clone();
            tempArea.getBBox().setWidth(columnWidth);
            tempArea.getBBox().setHeight(approximateHeight);
            tempArea.getBBox().setX(initialBBox.getX() + columnWidth * i);
            tempArea.getBBox().setY(initialBBox.getY() + initialBBox.getHeight() - tempArea.getBBox().getHeight());
            LayoutContext columnContext = new LayoutContext(tempArea, preLayoutContext.getMarginsCollapseInfo(),
                    preLayoutContext.getFloatRendererAreas(), preLayoutContext.isClippedHeight());

            LayoutResult tempResultColumn = renderer.layout(columnContext);
            if (tempResultColumn.getSplitRenderer() == null) {
                container.add(renderer);
            } else {
                container.add(tempResultColumn.getSplitRenderer());
            }
            renderer = tempResultColumn.getOverflowRenderer();
            if (i == columnCount - 1 && renderer != null) {
                throw new IllegalStateException("The last layouting should be full");
            }
        }
        return container;
    }

    private static float balanceContentBetweenColumns(int columnCount, IRenderer renderer, float approximateHeight) {
        List<List<Float>> columnsOverflowedHeights = createEmptyLists(columnCount);
        int currentColumn = 0;
        do {
            for (List<Float> overflowedHeights : columnsOverflowedHeights) {
                if (!overflowedHeights.isEmpty()) {
                    float min = (float) Collections.min(overflowedHeights);
                    approximateHeight += min;
                    break;
                }
            }
            columnsOverflowedHeights = createEmptyLists(columnCount);
            float[] columnsHeight = new float[columnCount];
            traverseRender(renderer, approximateHeight, columnsHeight, columnsOverflowedHeights, currentColumn);
        } while (!columnsOverflowedHeights.get(columnCount - 1).isEmpty());
        //TODO DEVSIX-7549: this is temporary solution, we should consider margins some other way
        approximateHeight += renderer.<UnitValue>getProperty(Property.MARGIN_TOP).getValue();
        approximateHeight += renderer.<UnitValue>getProperty(Property.MARGIN_BOTTOM).getValue();
        return approximateHeight;
    }

    private static List<List<Float>> createEmptyLists(int columnCount) {
        List<List<Float>> lists = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            lists.add(new ArrayList<>());
        }
        return lists;
    }

    private static void traverseRender(IRenderer renderer, float approximateHeight, float[] columnsHeight,
                                       List<List<Float>> columnsOverlowedHeights, int currentColumn) {
        final float height = renderer.getOccupiedArea().getBBox().getHeight();
        if (height > approximateHeight) {
            columnsOverlowedHeights.get(currentColumn).add(columnsHeight[currentColumn] + height - approximateHeight);
            traverseChildRenderers(renderer, approximateHeight, columnsHeight, columnsOverlowedHeights, currentColumn);
            return;
        }
        if (height + columnsHeight[currentColumn] > approximateHeight) {
            columnsOverlowedHeights.get(currentColumn).add(columnsHeight[currentColumn] + height - approximateHeight);
            if (currentColumn == columnsHeight.length - 1) {
                return;
            }
            if (renderer.getChildRenderers().isEmpty()) {
                traverseRender(renderer, approximateHeight, columnsHeight, columnsOverlowedHeights, ++currentColumn);
            } else {
                traverseChildRenderers(renderer, approximateHeight, columnsHeight, columnsOverlowedHeights, currentColumn);
            }
        } else {
            columnsHeight[currentColumn] += height;
        }
    }

    private static void traverseChildRenderers(IRenderer renderer, float approximateHeight, float[] columnsHeight,
                                               List<List<Float>> columnsOverlowedHeights, int currentColumn) {
        if (renderer instanceof ParagraphRenderer) {
            for (IRenderer child : ((ParagraphRenderer) renderer).getLines()) {
                traverseRender(child, approximateHeight, columnsHeight, columnsOverlowedHeights, currentColumn);
            }
        } else {
            for (IRenderer child : renderer.getChildRenderers()) {
                traverseRender(child, approximateHeight, columnsHeight, columnsOverlowedHeights, currentColumn);
            }
        }
    }
}
