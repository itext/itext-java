/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.Collection;
import java.util.Map;

/**
 * Interface that will provide a mapping from SVG tag names to Renderers that
 * will be able to draw them. It's used in {@link DefaultSvgNodeRendererFactory}
 * to allow customizability in client code, and dependency injection in tests.
 *
 * @deprecated The interface will be removed in 7.2, while its implementation
 * ({@link DefaultSvgNodeRendererMapper}) will be used as our internal class. Users should
 * override {@link ISvgNodeRendererFactory} (or at least {@link DefaultSvgNodeRendererFactory})
 * and should not deal with the mapping class as it's more of an implementation detail.
 */
@Deprecated
public interface ISvgNodeRendererMapper {

    /**
     * Gets the map from tag names to Renderer classes.
     *
     * @return a {@link Map} with Strings as keys and {link @ISvgNodeRenderer}
     * implementations as values
     */
    Map<String, Class<? extends ISvgNodeRenderer>> getMapping();

    /**
     * Get the list of tags that do not map to any Renderer and should be ignored
     * @return a collection of ignored tags
     */
    Collection<String> getIgnoredTags();
}
