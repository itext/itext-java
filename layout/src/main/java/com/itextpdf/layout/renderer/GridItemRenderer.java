/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

/**
 * Wrapper renderer around grid item. It's expected there is always exactly 1 child renderer.
 */
class GridItemRenderer extends BlockRenderer {

    /**
     * A renderer to wrap.
     */
    AbstractRenderer renderer;

    /**
     * Flag saying that we updated height of the renderer we wrap.
     * It allows to remove that property on split.
     */
    private boolean heightSet = false;

    GridItemRenderer() {
        super(new Div());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        this.renderer = (AbstractRenderer) renderer;
        super.addChild(renderer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(GridItemRenderer.class, this.getClass());
        return new GridItemRenderer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getProperty(int key) {
        // Handle only the props we are aware of
        switch (key) {
            case Property.GRID_COLUMN_START:
            case Property.GRID_COLUMN_END:
            case Property.GRID_COLUMN_SPAN:
            case Property.GRID_ROW_START:
            case Property.GRID_ROW_END:
            case Property.GRID_ROW_SPAN:
                T1 ownValue = this.<T1>getOwnProperty(key);
                if (ownValue != null) {
                    return ownValue;
                } else {
                    return renderer.<T1>getProperty(key);
                }

            default:
                break;
        }

        return super.<T1>getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(int property, Object value) {
        // Handle only the props we are aware of
        switch (property) {
            case Property.HEIGHT:
                if (!renderer.hasProperty(property) || heightSet) {
                    renderer.setProperty(Property.HEIGHT, value);
                    renderer.setProperty(Property.MIN_HEIGHT, value);
                    heightSet = true;
                }
                break;

            case Property.FILL_AVAILABLE_AREA_ON_SPLIT:
                renderer.setProperty(property, value);
                break;

            case Property.COLLAPSING_MARGINS:

            case Property.GRID_COLUMN_START:
            case Property.GRID_COLUMN_END:
            case Property.GRID_COLUMN_SPAN:
            case Property.GRID_ROW_START:
            case Property.GRID_ROW_END:
            case Property.GRID_ROW_SPAN:
                super.setProperty(property, value);
                break;

            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void updateHeightsOnSplit(float usedHeight, boolean wasHeightClipped, AbstractRenderer splitRenderer,
            AbstractRenderer overflowRenderer, boolean enlargeOccupiedAreaOnHeightWasClipped) {
         // If we set the height ourselves during layout, let's remove it while layouting on the next page
         // so that it is recalculated.
        if (heightSet) {
            // Always 1 child renderer
            overflowRenderer.childRenderers.get(0).deleteOwnProperty(Property.HEIGHT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void addChildRenderer(IRenderer child) {
        this.renderer = (AbstractRenderer) child;
        super.addChildRenderer(child);
    }

    float calculateHeight(float initialHeight) {
        // We subtract margins/borders/paddings because we should take into account that
        // borders/paddings/margins should also fit into a cell.
        final Rectangle rectangle = new Rectangle(0, 0, 0, initialHeight);
        if (AbstractRenderer.isBorderBoxSizing(renderer)) {
            renderer.applyMargins(rectangle, false);
            // In BlockRenderer#layout, after applying continuous container, we call AbstractRenderer#retrieveMaxHeight,
            // which calls AbstractRenderer#retrieveHeight where in case of BoxSizing we reduce the height for top
            // padding and border. So to reduce the height for top + bottom border, padding and margin here we apply
            // both top and bottom margin, but only bottom padding and border
            UnitValue paddingBottom = renderer.<UnitValue>getProperty(Property.PADDING_BOTTOM);
            if (paddingBottom.isPointValue()) {
                rectangle.decreaseHeight(paddingBottom.getValue());
            }
            Border borderBottom = renderer.getBorders()[AbstractRenderer.BOTTOM_SIDE];
            rectangle.decreaseHeight(borderBottom.getWidth());
        } else {
            renderer.applyMarginsBordersPaddings(rectangle, false);
        }

        return rectangle.getHeight();
    }
}
