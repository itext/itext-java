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
package com.itextpdf.svg.processors;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.Map;

/**
 * Interface for SVG processors results.
 */
public interface ISvgProcessorResult {

    /**
     * Obtains a map of named-objects with their id's as keys and the objects as values
     *
     * @return Map of Strings as keys and {@link ISvgNodeRenderer} as values
     */
    Map<String, ISvgNodeRenderer> getNamedObjects();

    /**
     * Obtains the wrapped {@link ISvgNodeRenderer} root renderer.
     *
     * @return ISvgNodeRenderer
     */
    ISvgNodeRenderer getRootRenderer();

    /**
     * Obtains the {@link FontProvider}.
     *
     * @return FontProvider
     */
    FontProvider getFontProvider();

    /**
     * Obtains the list of temporary fonts
     *
     * @return FontSet
     */
    FontSet getTempFonts();
}
