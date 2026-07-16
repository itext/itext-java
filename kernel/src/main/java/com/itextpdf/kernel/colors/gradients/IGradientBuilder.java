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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;

/**
 * Contract for gradient builders that can produce {@link Color} instances.
 */
public interface IGradientBuilder {

    /**
     * Adds the new color stop to the end ({@link IGradientBuilder more info}).
     *
     * <p>
     * Note: if the previously added color stop's offset would have greater offset than the added
     * one, then the new offset would be normalized to be equal to the previous one. (Comparison
     * made between relative on coordinates vector offsets. If any of them has
     * the absolute offset, then the absolute value would be converted to relative first.)
     *
     * @param gradientColorStop the gradient stop color to add
     *
     * @return the current builder instance
     */
    IGradientBuilder addStopColor(GradientColorStop gradientColorStop);

    /**
     * Set the spread method to use for the gradient.
     *
     * @param gradientSpreadMethod the gradient spread method to set
     *
     * @return the current builder instance
     */
    IGradientBuilder setSpread(GradientSpreadMethod gradientSpreadMethod);

    /**
     * Builds the {@link Color} object representing the gradient with specified configuration
     * that fills the target bounding box.
     *
     * @param targetBoundingBox the bounding box to be filled in current space
     * @param contextTransform  the transformation from the base coordinates space into
     *                          the current space. The {@code null} value is valid and can be used
     *                          if there is no transformation from base coordinates to current space
     *                          specified, or it is equal to identity transformation.
     * @param document          the {@link PdfDocument} for which the linear gradient would be built.
     *
     * @return the constructed {@link Color} or {@code null} if no color to be applied
     * or base gradient vector has been specified
     */
    Color buildColor(Rectangle targetBoundingBox, AffineTransform contextTransform, PdfDocument document);
}

