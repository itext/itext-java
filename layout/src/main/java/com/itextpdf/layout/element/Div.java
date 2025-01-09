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

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.DivRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * A {@link Div} is a container object that defines a section in a document,
 * which will have some shared layout properties. Like all {@link BlockElement}
 * types, it will try to take up as much horizontal space as possible.
 * <p>
 * The concept is very similar to that of the div tag in HTML.
 */
public class Div extends BlockElement<Div> {

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Adds any block element to the div's contents.
     *
     * @param element a {@link BlockElement}
     * @return this Element
     */
    public Div add(IBlockElement element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds an image to the div's contents.
     *
     * @param element an {@link Image}
     * @return this Element
     */
    public Div add(Image element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds an area break to the div's contents.
     *
     * @param areaBreak an {@link AreaBreak}
     * @return this Element
     */
    public Div add(AreaBreak areaBreak) {
        childElements.add(areaBreak);
        return this;
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.DIV);
        }
        return tagProperties;
    }

    /**
     * Defines whether the {@link Div} should occupy all the space left in the available area
     * in case it is the last element in this area.
     *
     * @param fillArea defines whether the available area should be filled
     * @return this {@link Div}
     */
    public Div setFillAvailableArea(boolean fillArea) {
        setProperty(Property.FILL_AVAILABLE_AREA, fillArea);
        return this;
    }

    /**
     * Defines whether the {@link Div} should occupy all the space left in the available area
     * in case the area has been split and it is the last element in the split part of this area.
     *
     * @param fillAreaOnSplit defines whether the available area should be filled
     * @return this {@link Div}
     */
    public Div setFillAvailableAreaOnSplit(boolean fillAreaOnSplit) {
        setProperty(Property.FILL_AVAILABLE_AREA_ON_SPLIT, fillAreaOnSplit);
        return this;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new DivRenderer(this);
    }
}
