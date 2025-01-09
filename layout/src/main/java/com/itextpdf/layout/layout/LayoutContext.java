/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.layout.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the context for content {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
 */
public class LayoutContext {

    /**
     * The {@link LayoutArea} for the content to be placed on.
     */
    protected LayoutArea area;

    /**
     * The info about margins collapsing.
     */
    protected MarginsCollapseInfo marginsCollapseInfo;

    /**
     * The list of {@link Rectangle} objects.
     */
    protected List<Rectangle> floatRendererAreas = new ArrayList<>();

    /**
     * Indicates whether the height is clipped or not.
     */
    protected boolean clippedHeight = false;

    /**
     * Creates the layout context.
     *
     * @param area for the content to be placed on
     */
    public LayoutContext(LayoutArea area) {
        this.area = area;
    }

    /**
     * Creates the layout context.
     *
     * @param area for the content to be placed on
     * @param marginsCollapseInfo the info about margins collapsing
     */
    public LayoutContext(LayoutArea area, MarginsCollapseInfo marginsCollapseInfo) {
        this.area = area;
        this.marginsCollapseInfo = marginsCollapseInfo;
    }

    /**
     * Creates the layout context.
     *
     * @param area for the content to be placed on
     * @param marginsCollapseInfo the info about margins collapsing
     * @param floatedRendererAreas list of {@link Rectangle} objects
     */
    public LayoutContext(LayoutArea area, MarginsCollapseInfo marginsCollapseInfo, List<Rectangle> floatedRendererAreas) {
        this(area, marginsCollapseInfo);
        if (floatedRendererAreas != null) {
            this.floatRendererAreas = floatedRendererAreas;
        }
    }

    /**
     * Creates the layout context.
     *
     * @param area for the content to be placed on
     * @param clippedHeight indicates whether the height is clipped or not
     */
    public LayoutContext(LayoutArea area, boolean clippedHeight) {
        this(area);
        this.clippedHeight = clippedHeight;
    }

    /**
     * Creates the layout context.
     *
     * @param area for the content to be placed on
     * @param marginsCollapseInfo the info about margins collapsing
     * @param floatedRendererAreas list of {@link Rectangle} objects
     * @param clippedHeight indicates whether the height is clipped or not
     */
    public LayoutContext(LayoutArea area, MarginsCollapseInfo marginsCollapseInfo, List<Rectangle> floatedRendererAreas, boolean clippedHeight) {
        this(area, marginsCollapseInfo);
        if (floatedRendererAreas != null) {
            this.floatRendererAreas = floatedRendererAreas;
        }
        this.clippedHeight = clippedHeight;
    }

    /**
     * Gets the {@link LayoutArea area} the content to be placed on.
     *
     * @return the area for content layouting.
     */
    public LayoutArea getArea() {
        return area;
    }

    /**
     * Gets info about margins collapsing.
     *
     * @return the info about margins collapsing
     */
    public MarginsCollapseInfo getMarginsCollapseInfo() {
        return marginsCollapseInfo;
    }

    /**
     * Gets list of {@link Rectangle} objects.
     *
     * @return list of {@link Rectangle} objects
     */
    public List<Rectangle> getFloatRendererAreas() {
        return floatRendererAreas;
    }

    /**
     * Indicates whether the layout area's height is clipped or not.
     *
     * @return whether the layout area's height is clipped or not.
     */
    public boolean isClippedHeight() {
        return clippedHeight;
    }

    /**
     * Defines whether the layout area's height is clipped or not.
     *
     * @param clippedHeight indicates whether the height is clipped or not.
     */
    public void setClippedHeight(boolean clippedHeight) {
        this.clippedHeight = clippedHeight;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return area.toString();
    }
}
