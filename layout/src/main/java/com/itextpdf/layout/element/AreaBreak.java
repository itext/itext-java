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
package com.itextpdf.layout.element;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.AreaBreakRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * A layout object that terminates the current content area and creates a new
 * one. If no {@link PageSize} is given, the new content area will have the same
 * size as the current one.
 */
public class AreaBreak extends AbstractElement<AreaBreak> {

    protected PageSize pageSize;

    /**
     * Creates an AreaBreak. The new content area will have the same size as the
     * current one.
     */
    public AreaBreak() {
        this(AreaBreakType.NEXT_AREA);
    }

    /**
     * Creates an AreaBreak that terminates a specified area type.
     * @param areaBreakType an {@link AreaBreakType area break type}
     */
    public AreaBreak(AreaBreakType areaBreakType) {
        setProperty(Property.AREA_BREAK_TYPE, areaBreakType);
    }
    
    /**
     * Creates an AreaBreak. The new content area will have the specified page
     * size.
     * @param pageSize the size of the new content area
     */
    public AreaBreak(PageSize pageSize) {
        this(AreaBreakType.NEXT_PAGE);
        this.pageSize = pageSize;
    }

    /**
     * Gets the page size.
     * @return the {@link PageSize page size} of the next content area.
     */
    public PageSize getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size.
     * @param pageSize the new {@link PageSize page size} of the next content area.
     */
    public void setPageSize(PageSize pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets the type of area that this AreaBreak will terminate.
     * @return the current {@link AreaBreakType area break type}
     */
    public AreaBreakType getType() {
        return this.<AreaBreakType>getProperty(Property.AREA_BREAK_TYPE);
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new AreaBreakRenderer(this);
    }
}
