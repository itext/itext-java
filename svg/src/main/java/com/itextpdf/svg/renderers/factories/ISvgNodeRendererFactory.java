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
package com.itextpdf.svg.renderers.factories;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

/**
 * Interface for the factory used by {@link DefaultSvgProcessor}.
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
