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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TextRectangle;

/**
 * Interface for &lt;text&gt; and &lt;tspan&gt; related renderers.
 */
public interface ISvgTextNodeRenderer extends ISvgNodeRenderer {

    /**
     * Gets text content length.
     *
     * @param parentFontSize parent font size
     * @param font current font
     *
     * @return text content length
     */
    @Deprecated
    float getTextContentLength(float parentFontSize, PdfFont font);

    /**
     * This method is deprecated and will be replaced with new signature {@code getRelativeTranslation(SvgDrawContext)}.
     * This is needed because xMove/yMove can contain relative values, so SvgDrawContext is needed to resolve them.
     *
     * @return text relative translation
     */
    @Deprecated
    float[] getRelativeTranslation();

    /**
     * This method is deprecated and will be replaced with new signature {@code containsRelativeMove(SvgDrawContext)}.
     * This is needed because xMove/yMove can contain relative values, so SvgDrawContext is needed to resolve them.
     *
     * @return {@code true} if there is a relative move, {@code false} otherwise
     */
    @Deprecated
    boolean containsRelativeMove();

    /**
     * This method is deprecated and will be replaced with new signature {@code containsAbsolutePositionChange(SvgDrawContext)}.
     * This is needed because x/y can contain relative values, so SvgDrawContext is needed to resolve them.
     *
     * @return {@code true} if an absolute position is specified via x/y attributes, {@code false} otherwise
     */
    @Deprecated
    boolean containsAbsolutePositionChange();

    /**
     * This method is deprecated and will be replaced with new signature {@code getAbsolutePositionChanges(SvgDrawContext)}.
     * This is needed because x/y can contain relative values, so SvgDrawContext is needed to resolve them.
     *
     * @return text absolute position
     */
    @Deprecated
    float[][] getAbsolutePositionChanges();

    /**
     * Return the bounding rectangle of the text element.
     *
     * @param context current {@link SvgDrawContext}
     * @param basePoint end point of previous text element
     * @return created instance of {@link TextRectangle}
     */
    TextRectangle getTextRectangle(SvgDrawContext context, Point basePoint);
}
