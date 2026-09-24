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
package com.itextpdf.kernel.colors.gradients;

/**
 * This resolver is used during the layout process to prevent infinite loops.
 */
public class GradientPropertiesResolver {

    /**
     * Default max color stops.
     */
    public static final int DEFAULT_MAX_COLOR_STOPS = 10_000;

    private final int maxColorStops;

    /**
     * Creates default instance of {@link GradientPropertiesResolver}.
     */
    public GradientPropertiesResolver() {
        maxColorStops = DEFAULT_MAX_COLOR_STOPS;
    }

    /**
     * Creates {@link GradientPropertiesResolver} instance.
     *
     * <p>
     * This resolver is used for gradient creation.
     *
     * @param maxColorStops max color stops for repeat and reflect
     *                      (see {@link GradientPropertiesResolver#getMaxColorStops()}
     */
    public GradientPropertiesResolver(int maxColorStops) {
        this.maxColorStops = maxColorStops;
    }

    /**
     * Gets maximum color stops for repeat and reflect spreading.
     * <p>
     * This property defines the maximum amount of color stops to be created for
     * repeat and reflect spreading of colors in gradients.
     *
     * @return maximum color stops for repeat and reflect
     */
    public int getMaxColorStops() {
        return maxColorStops;
    }
}
