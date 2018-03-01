package com.itextpdf.svg.renderers.factories;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

/**
 * Interface for the factory used by {@link com.itextpdf.svg.processors.impl.DefaultSvgProcessor}.
 * Pass along using {@link com.itextpdf.svg.processors.ISvgConverterProperties}.
 */
public interface ISvgNodeRendererFactory {

    /**
     * Create a configured renderer based on the passed Svg tag and set its parent.
     * @param tag Representation of the Svg tag, with all style attributes set
     * @param parent renderer of the parent tag
     * @return Configured ISvgNodeRenderer
     */
    ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent);
}
