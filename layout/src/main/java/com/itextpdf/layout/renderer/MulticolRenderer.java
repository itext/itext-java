/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.MulticolContainer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a renderer for columns.
 */
public class MulticolRenderer extends AbstractRenderer {

    private static final int MAX_RELAYOUT_COUNT = 4;
    private static final float ZERO_DELTA = 0.0001f;

    private BlockRenderer elementRenderer;
    private int columnCount;
    private float columnWidth;
    private float approximateHeight;

    /**
     * Creates a DivRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link MulticolContainer} which this object should manage
     */
    public MulticolRenderer(MulticolContainer modelElement) {
        super(modelElement);
    }

    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(MulticolRenderer.class, this.getClass());
        return new MulticolRenderer((MulticolContainer) modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        ((MulticolContainer) this.getModelElement()).copyAllPropertiesToChildren();
        this.setProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER, Boolean.TRUE);
        final Rectangle initialBBox = layoutContext.getArea().getBBox();
        columnCount = (int) this.<Integer>getProperty(Property.COLUMN_COUNT);
        columnWidth = initialBBox.getWidth() / columnCount;
        if (this.elementRenderer == null) {
            // initialize elementRenderer on first layout when first child represents renderer of element which
            // should be layouted in multicol, because on the next layouts this can have multiple children
            elementRenderer = getElementsRenderer();
        }
        //It is necessary to set parent, because during relayout elementRenderer's parent gets cleaned up
        elementRenderer.setParent(this);
        LayoutResult prelayoutResult = elementRenderer.layout(
                new LayoutContext(new LayoutArea(1, new Rectangle(columnWidth, INF))));
        if (prelayoutResult.getStatus() != LayoutResult.FULL) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, elementRenderer);
        }

        approximateHeight = prelayoutResult.getOccupiedArea().getBBox().getHeight() / columnCount;

        List<IRenderer> container = balanceContentAndLayoutColumns(layoutContext);

        this.occupiedArea = calculateContainerOccupiedArea(layoutContext);
        this.setChildRenderers(container);
        LayoutResult result = new LayoutResult(LayoutResult.FULL, this.occupiedArea, this, null);

        // process some properties (keepTogether, margin collapsing), area breaks, adding height
        // Check what we do at the end of BlockRenderer

        return result;
    }

    private List<IRenderer> balanceContentAndLayoutColumns(LayoutContext prelayoutContext) {
        Float additionalHeightPerIteration = null;
        List<IRenderer> container = new ArrayList<>();
        int counter = MAX_RELAYOUT_COUNT;
        while (counter-- > 0) {
            IRenderer overflowRenderer = layoutColumnsAndReturnOverflowRenderer(prelayoutContext, container);
            if (overflowRenderer == null) {
                return container;
            }
            if (additionalHeightPerIteration == null) {
                LayoutResult overflowResult = overflowRenderer.layout(
                        new LayoutContext(new LayoutArea(1, new Rectangle(columnWidth, INF))));
                additionalHeightPerIteration = overflowResult.getOccupiedArea().getBBox().getHeight() / MAX_RELAYOUT_COUNT;
            }
            if (Math.abs(additionalHeightPerIteration.floatValue()) <= ZERO_DELTA) {
                return container;
            }
            approximateHeight += additionalHeightPerIteration.floatValue();
        }
        return container;
    }

    private LayoutArea calculateContainerOccupiedArea(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea().clone();
        area.getBBox().setHeight(approximateHeight);
        Rectangle initialBBox = layoutContext.getArea().getBBox();
        area.getBBox().setY(initialBBox.getY() + initialBBox.getHeight() - area.getBBox().getHeight());
        return area;
    }

    private BlockRenderer getElementsRenderer() {
        if (!(getChildRenderers().size() == 1 && getChildRenderers().get(0) instanceof BlockRenderer)) {
            throw new IllegalStateException("Invalid child renderers, it should be one and be a block element");
        }
        return (BlockRenderer) getChildRenderers().get(0);
    }

    private IRenderer layoutColumnsAndReturnOverflowRenderer(LayoutContext preLayoutContext, List<IRenderer> container) {
        container.clear();
        Rectangle initialBBox = preLayoutContext.getArea().getBBox();
        IRenderer renderer = elementRenderer;
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
        }
        return renderer;
    }
}
