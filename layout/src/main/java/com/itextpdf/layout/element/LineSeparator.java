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

import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.LineSeparatorRenderer;

/**
 * This is a line separator element which is basically just a horizontal line with
 * a style specified by {@link ILineDrawer} custom drawing interface instance.
 * This might be thought of as an HTML's &lt;hr&gt; element alternative.
 */
public class LineSeparator extends BlockElement<LineSeparator> {

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates a custom line separator with line style defined by custom {@link ILineDrawer} interface instance
     *
     * @param lineDrawer line drawer instance
     */
    public LineSeparator(ILineDrawer lineDrawer) {
        setProperty(Property.LINE_DRAWER, lineDrawer);
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.ARTIFACT);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new LineSeparatorRenderer(this);
    }
}
