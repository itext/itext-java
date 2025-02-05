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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.util.NumberUtil;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;

/**
 * This class helps to identify whether we are dealing with a renderer that creates
 * a new "Block formatting context" in terms of CSS. Such renderers adhere to
 * specific rules of floating elements and margins collapse handling.
 * <p>
 * @see <a href="https://www.w3.org/TR/CSS21/visuren.html#block-formatting">https://www.w3.org/TR/CSS21/visuren.html#block-formatting</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Block_formatting_context">https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Block_formatting_context</a>
 */
public class BlockFormattingContextUtil {

    /**
     * Defines whether a renderer creates a new "Block formatting context" in terms of CSS.
     * <p>
     * See {@link BlockFormattingContextUtil} class description for more info.
     * @param renderer an {@link IRenderer} to be checked.
     * @return true if given renderer creates a new "Block formatting context" in terms of CSS, false otherwise.
     */
    public static boolean isRendererCreateBfc(IRenderer renderer) {
        return (renderer instanceof RootRenderer)
                || (renderer instanceof CellRenderer)
                || isInlineBlock(renderer)
                || renderer.getParent() instanceof FlexContainerRenderer
                || FloatingHelper.isRendererFloating(renderer)
                || isAbsolutePosition(renderer)
                || isFixedPosition(renderer)
                || isCaption(renderer)
                || AbstractRenderer.isOverflowProperty(OverflowPropertyValue.HIDDEN, renderer, Property.OVERFLOW_X)
                || AbstractRenderer.isOverflowProperty(OverflowPropertyValue.HIDDEN, renderer, Property.OVERFLOW_Y);
    }

    private static boolean isInlineBlock(IRenderer renderer) {
        return renderer.getParent() instanceof LineRenderer
                && (renderer instanceof BlockRenderer || renderer instanceof TableRenderer);
    }

    private static boolean isAbsolutePosition(IRenderer renderer) {
        Integer positioning = NumberUtil.asInteger(renderer.<Object>getProperty(Property.POSITION));
        return Integer.valueOf(LayoutPosition.ABSOLUTE).equals(positioning);
    }

    private static boolean isFixedPosition(IRenderer renderer) {
        Integer positioning = NumberUtil.asInteger(renderer.<Object>getProperty(Property.POSITION));
        return Integer.valueOf(LayoutPosition.FIXED).equals(positioning);
    }

    private static boolean isCaption(IRenderer renderer) {
        return renderer.getParent() instanceof TableRenderer
                && (renderer instanceof DivRenderer);
    }
}
