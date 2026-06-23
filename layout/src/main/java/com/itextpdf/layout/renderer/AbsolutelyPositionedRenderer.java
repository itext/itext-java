/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for absolutely positioned elements which lack coordinates in a certain axis.
 * This wrapper performs fake static layout for such elements to determine it's coordinates.
 */
class AbsolutelyPositionedRenderer implements IRenderer {
    private final IRenderer wrappedRenderer;
    private final boolean verticalCoordinateMissing;
    private final boolean horizontalCoordinateMissing;
    private final DivRenderer dummyRenderer = new DivRenderer(new Div().setWidth(0).setHeight(0));

    public AbsolutelyPositionedRenderer(IRenderer wrappedRenderer, boolean verticalCoordinateMissing,
                                        boolean horizontalCoordinateMissing) {
        this.wrappedRenderer = wrappedRenderer;
        this.wrappedRenderer.setProperty(Property.POSITIONED_ELEMENT_WRAPPED, new Object());
        this.verticalCoordinateMissing = verticalCoordinateMissing;
        this.horizontalCoordinateMissing = horizontalCoordinateMissing;
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutContext copiedContext = copyContext(layoutContext);
        Object positioning = wrappedRenderer.<Integer>getOwnProperty(Property.POSITION);
        wrappedRenderer.setProperty(Property.POSITION, LayoutPosition.STATIC);
        wrappedRenderer.setProperty(Property.FLOAT, FloatPropertyValue.NONE);
        LayoutResult result = wrappedRenderer.layout(copiedContext);
        if (result.getStatus() == LayoutResult.NOTHING) {
            wrappedRenderer.setProperty(Property.FORCED_PLACEMENT, true);
            result = wrappedRenderer.layout(copiedContext);
            wrappedRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);
        }
        if (positioning == null) {
            wrappedRenderer.deleteOwnProperty(Property.POSITION);
        } else {
            wrappedRenderer.setProperty(Property.POSITION, positioning);
        }
        if (verticalCoordinateMissing) {
            wrappedRenderer.setProperty(Property.TOP_CALCULATED, result.getOccupiedArea().getBBox().getTop());
        }
        if (horizontalCoordinateMissing) {
            wrappedRenderer.setProperty(Property.LEFT_CALCULATED, result.getOccupiedArea().getBBox().getLeft());
        }
        if (wrappedRenderer instanceof AbstractRenderer) {
            ((AbstractRenderer) wrappedRenderer).occupiedArea = null;
        }
        return dummyRenderer.layout(copiedContext);
    }

    public IRenderer getWrappedRenderer() {
        return wrappedRenderer;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new AbsolutelyPositionedRenderer(wrappedRenderer.getNextRenderer(),
                verticalCoordinateMissing, horizontalCoordinateMissing);
    }

    @Override
    public void draw(DrawContext drawContext) {
        // We never need to draw wrapper renderer.
    }

    @Override
    public LayoutArea getOccupiedArea() {
        return dummyRenderer.getOccupiedArea();
    }

    @Override
    public void move(float dx, float dy) {
        // We don't need to move wrapper renderer.
    }

    @Override
    public boolean hasProperty(int property) {
        return wrappedRenderer.hasProperty(property);
    }

    @Override
    public boolean hasOwnProperty(int property) {
        return wrappedRenderer.hasOwnProperty(property);
    }

    @Override
    public <T1> T1 getProperty(int property) {
        if (Property.POSITION == property) {
            // This absolutely positioned renderer wrapper is never supposed to be treated as absolutely positioned.
            // The whole idea of this wrapper is to calculate it's potential static coordinates.
            return (T1) (Object) LayoutPosition.STATIC;
        }
        return wrappedRenderer.<T1>getProperty(property);
    }

    @Override
    public <T1> T1 getProperty(int property, T1 defaultValue) {
        if (Property.POSITION == property) {
            // This absolutely positioned renderer wrapper is never supposed to be treated as absolutely positioned.
            // The whole idea of this wrapper is to calculate it's potential static coordinates.
            return (T1) (Object) LayoutPosition.STATIC;
        }
        return wrappedRenderer.<T1>getProperty(property, defaultValue);
    }

    @Override
    public <T1> T1 getOwnProperty(int property) {
        return wrappedRenderer.<T1>getOwnProperty(property);
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        return wrappedRenderer.<T1>getDefaultProperty(property);
    }

    @Override
    public void setProperty(int property, Object value) {
        wrappedRenderer.setProperty(property, value);
    }

    @Override
    public void deleteOwnProperty(int property) {
        wrappedRenderer.deleteOwnProperty(property);
    }

    @Override
    public void addChild(IRenderer renderer) {
        wrappedRenderer.addChild(renderer);
    }

    @Override
    public IRenderer setParent(IRenderer parent) {
        wrappedRenderer.setParent(parent);
        return this;
    }

    @Override
    public IRenderer getParent() {
        return wrappedRenderer.getParent();
    }

    @Override
    public IPropertyContainer getModelElement() {
        return wrappedRenderer.getModelElement();
    }

    @Override
    public List<IRenderer> getChildRenderers() {
        return wrappedRenderer.getChildRenderers();
    }

    @Override
    public boolean isFlushed() {
        return wrappedRenderer.isFlushed();
    }

    private static LayoutContext copyContext(LayoutContext originalContext) {
        MarginsCollapseInfo copiedMarginsCollapseInfo = null;
        if (originalContext.getMarginsCollapseInfo() != null) {
            copiedMarginsCollapseInfo = MarginsCollapseInfo.createDeepCopy(originalContext.getMarginsCollapseInfo());
        }
        ArrayList<Rectangle> attemptFloatRectsList = new ArrayList<>(originalContext.getFloatRendererAreas());
        return new LayoutContext(originalContext.getArea().clone(),
                copiedMarginsCollapseInfo, attemptFloatRectsList, originalContext.isClippedHeight());
    }
}
