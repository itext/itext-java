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
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renderer for the {@link AreaBreak} layout element. Will terminate the
 * current content area and initialize a new one.
 */
public class AreaBreakRenderer extends AbstractBreakRenderer {

    protected AreaBreak areaBreak;
    protected LayoutArea occupiedArea;

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaBreakRenderer.class);

    /**
     * Creates an AreaBreakRenderer.
     *
     * @param areaBreak the {@link AreaBreak} that will be rendered by this object
     */
    public AreaBreakRenderer(AreaBreak areaBreak) {
        this.areaBreak = areaBreak;
    }

    /**
     * Logs a warning about unexpected use of {@link AreaBreakRenderer} if not ignored,
     * because instances of this class are only used for terminating the current content area.
     *
     * @param renderer {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        if (this.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS) == null) {
            LOGGER.warn(LayoutLogMessageConstant.AREA_BREAK_UNEXPECTED);
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        if (Boolean.TRUE.equals(this.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS))) {
            if (occupiedArea == null) {
                LOGGER.warn(LayoutLogMessageConstant.AREA_BREAK_IGNORED);
            }
            Rectangle layoutContextAreaBbox = layoutContext.getArea().getBBox();
            Rectangle occupiedAreaBbox =
                    new Rectangle(layoutContextAreaBbox.getLeft(), layoutContextAreaBbox.getTop(), 0, 0);
            occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), occupiedAreaBbox);
            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null, this);
        }

        return new LayoutResult(LayoutResult.NOTHING, null, null, null, this).setAreaBreak(areaBreak);
    }

    /**
     * Logs a warning about unexpected use of {@link AreaBreakRenderer} if not ignored,
     * because instances of this class are only used for terminating the current content area.
     *
     * @param drawContext {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        if (this.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS) == null) {
            LOGGER.warn(LayoutLogMessageConstant.AREA_BREAK_UNEXPECTED);
        }
    }

    /**
     * Throws an UnsupportedOperationException if not ignored, because instances of this
     * class are only used for terminating the current content area.
     *
     * @return {@inheritDoc}
     */
    @Override
    public LayoutArea getOccupiedArea() {
        if (Boolean.TRUE.equals(this.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS))) {
            return occupiedArea;
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public IPropertyContainer getModelElement() {
        return null;
    }

    /**
     * Logs a warning about unexpected use of {@link AreaBreakRenderer} if not ignored,
     * because instances of this class are only used for terminating the current content area.
     *
     * @param dx {@inheritDoc}
     * @param dy {@inheritDoc}
     */
    @Override
    public void move(float dx, float dy) {
        if (this.<Boolean>getProperty(Property.IGNORE_AREA_AND_SECTION_BREAKS) == null) {
            LOGGER.warn(LayoutLogMessageConstant.AREA_BREAK_UNEXPECTED);
        }
    }

    @Override
    public IRenderer getNextRenderer() {
        return null;
    }
}
