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
package com.itextpdf.layout.testutil;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.IRenderer;

public final class LayoutResultTestUtil {

    private LayoutResultTestUtil() {
    }

    /**
     * Gets the layout status from an element.
     *
     * @param element the to-check element
     * @param document document
     * @param area bounding box
     * @return layout status
     */
    public static int getLayoutStatus(IBlockElement element, Document document, Rectangle area) {
        IRenderer renderer = element.createRendererSubTree().setParent(document.getRenderer());
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, area)));
        return result.getStatus();
    }

    /**
     * Gets the layout status from an image.
     *
     * @param image the to-check image
     * @param document document
     * @param area bounding box
     * @return layout status
     */
    public static int getLayoutStatusForImage(Image image, Document document, Rectangle area) {
        IRenderer renderer = image.createRendererSubTree().setParent(document.getRenderer());
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, area)));
        return result.getStatus();
    }
}