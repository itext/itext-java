package com.itextpdf.layout.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import java.util.List;

/**
 * Represents the context for content of a line {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
 */
public class LineLayoutContext extends LayoutContext {
    private boolean floatOverflowedToNextPageWithNothing = false;

    public LineLayoutContext(LayoutArea area, MarginsCollapseInfo marginsCollapseInfo, List<Rectangle> floatedRendererAreas, boolean clippedHeight) {
        super(area, marginsCollapseInfo, floatedRendererAreas, clippedHeight);
    }

    public LineLayoutContext(LayoutContext layoutContext) {
        super(layoutContext.area, layoutContext.marginsCollapseInfo, layoutContext.floatRendererAreas, layoutContext.clippedHeight);
    }

    /**
     * Specifies whether some floating element within the same paragraph has already completely overflowed to the next
     * page.
     * @return true if floating element has already overflowed to the next page, false otherwise.
     */
    public boolean isFloatOverflowedToNextPageWithNothing() {
        return floatOverflowedToNextPageWithNothing;
    }

    /**
     * Changes the value of property specified by {@link #isFloatOverflowedToNextPageWithNothing()}.
     * @param floatOverflowedToNextPageWithNothing true if some floating element already completely overflowed.
     * @return this {@link LineLayoutContext} instance.
     */
    public LineLayoutContext setFloatOverflowedToNextPageWithNothing(boolean floatOverflowedToNextPageWithNothing) {
        this.floatOverflowedToNextPageWithNothing = floatOverflowedToNextPageWithNothing;
        return this;
    }
}
