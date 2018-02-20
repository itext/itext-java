package com.itextpdf.svg.renderers;

import java.util.List;

/**
 * Interface for SvgNodeRenderer, the renderer draws the SVG to its Pdf-canvas passed in {@link com.itextpdf.layout.renderer.DrawContext},
 * applying styling (CSS and attributes).
 */
public interface ISvgNodeRenderer {

    ISvgNodeRenderer getParent();
    void draw(SvgDrawContext context);
    void addChild(ISvgNodeRenderer child);
    List<ISvgNodeRenderer> getChildren();

}
