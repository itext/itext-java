package com.itextpdf.layout.renderer;

import com.itextpdf.io.util.NumberUtil;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;

/**
 * This class helps to identify whether we are dealing with a renderer that creates
 * a new "Block formatting context" in terms of CSS. Such renderers adhere to
 * specific rules of floating elements and margins collapse handling.
 * <p>
 * See
 * <a href="https://www.w3.org/TR/CSS21/visuren.html#block-formatting">https://www.w3.org/TR/CSS21/visuren.html#block-formatting</a>
 * and
 * <a href="https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Block_formatting_context">https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Block_formatting_context</a>
 * for more info.
 */
public class BlockFormattingContextUtil {
    public static boolean isRendererCreateBfc(IRenderer renderer) {
        return (renderer instanceof RootRenderer)
                || (renderer instanceof CellRenderer)
                || isInlineBlock(renderer)
                || FloatingHelper.isRendererFloating(renderer)
                || isAbsolutePosition(renderer)
                || isFixedPosition(renderer)
                || isOverflowHidden(renderer);
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

    private static boolean isOverflowHidden(IRenderer renderer) {
        OverflowPropertyValue overflow = renderer.<OverflowPropertyValue>getProperty(Property.OVERFLOW);
        OverflowPropertyValue overflowX = renderer.<OverflowPropertyValue>getProperty(Property.OVERFLOW_X);
        OverflowPropertyValue overflowY = renderer.<OverflowPropertyValue>getProperty(Property.OVERFLOW_Y);

        if (overflowX == null) {
            overflowX = overflow;
        }
        if (overflowY == null) {
            overflowY = overflow;
        }

        return overflowX != null && OverflowPropertyValue.HIDDEN.equals(overflowX)
                || overflowY != null && OverflowPropertyValue.HIDDEN.equals(overflowY);
    }
}
