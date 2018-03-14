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

    /**
     * Checks whether the provided tag is an ignored tag of this factory or not. If ignored, the factory won't process this IElementNode into an ISvgNodeRenderer.
     *
     * @param tag the IElementNode
     * @return true if ignored
     */
    boolean isTagIgnored(IElementNode tag);
}
