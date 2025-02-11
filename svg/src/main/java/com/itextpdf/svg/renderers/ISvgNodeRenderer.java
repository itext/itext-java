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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.geom.Rectangle;

import java.util.Map;

/**
 * Interface for SvgNodeRenderer, the renderer draws the SVG to its Pdf-canvas
 * passed in {@link SvgDrawContext}, applying styling
 * (CSS and attributes).
 */
public interface ISvgNodeRenderer {

    /**
     * Sets the parent of this renderer. The parent may be the source of
     * inherited properties and default values.
     *
     * @param parent the parent renderer
     */
    void setParent(ISvgNodeRenderer parent);

    /**
     * Gets the parent of this renderer. The parent may be the source of
     * inherited properties and default values.
     *
     * @return the parent renderer; null in case of a root node
     */
    ISvgNodeRenderer getParent();

    /**
     * Draws this element to a canvas-like object maintained in the context.
     *
     * @param context the object that knows the place to draw this element and
     *                maintains its state
     */
    void draw(SvgDrawContext context);

    /**
     * Sets the map of XML node attributes and CSS style properties that this
     * renderer needs.
     *
     * @param attributesAndStyles the mapping from key names to values
     */
    void setAttributesAndStyles(Map<String, String> attributesAndStyles);

    /**
     * Retrieves the property value for a given key name.
     *
     * @param key the name of the property to search for
     *
     * @return the value for this key, or {@code null}
     */
    String getAttribute(String key);

    /**
     * Sets a property key and value pairs for a given attribute
     *
     * @param key   the name of the attribute
     * @param value the value of the attribute
     */
    void setAttribute(String key, String value);

    /**
     * Get a modifiable copy of the style and attribute map
     *
     * @return copy of the attributes and styles-map
     */
    Map<String, String> getAttributeMapCopy();

    /**
     * Creates a deep copy of this renderer, including it's subtree of children
     *
     * @return deep copy of this renderer
     */
    ISvgNodeRenderer createDeepCopy();

    /**
     * Calculates the current object bounding box.
     *
     * @param context the current context, for instance it contains current viewport and available
     *                font data
     *
     * @return the {@link Rectangle} representing the current object's bounding box, or null
     * if bounding box is undefined
     */
    Rectangle getObjectBoundingBox(SvgDrawContext context);
}
