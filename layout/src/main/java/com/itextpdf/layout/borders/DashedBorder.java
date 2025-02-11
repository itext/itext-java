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
package com.itextpdf.layout.borders;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Draws a border with dashes around the element it's been set to.
 */
public class DashedBorder extends Border {

    /**
     * The modifier to be applied on the width to have the dash size
     */
    private static final float DASH_MODIFIER = 5f;
    /**
     * The modifier to be applied on the width to have the initial gap size
     */
    private static final float GAP_MODIFIER = 3.5f;

    /**
     * Creates a DashedBorder with the specified width and sets the color to black.
     *
     * @param width width of the border
     */
    public DashedBorder(float width) {
        super(width);
    }

    /**
     * Creates a DashedBorder with the specified width and the specified color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public DashedBorder(Color color, float width) {
        super(color, width);
    }

    /**
     * Creates a DashedBorder with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity width of the border
     */
    public DashedBorder(Color color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return Border.DASHED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide,
            float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * GAP_MODIFIER;
        float dash = width * DASH_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = super.getDotsGap(borderLength, initialGap + dash);
        if (adjustedGap > dash) {
            adjustedGap -= dash;
        }

        new FixedDashedBorder(getColor(), width, getOpacity(), dash, adjustedGap, dash + adjustedGap / 2)
                .draw(canvas, x1, y1, x2, y2, defaultSide, borderWidthBefore, borderWidthAfter);
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float horizontalRadius1,
            float verticalRadius1, float horizontalRadius2, float verticalRadius2, Side defaultSide,
            float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * GAP_MODIFIER;
        float dash = width * DASH_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);
        float adjustedGap = super.getDotsGap(borderLength, initialGap + dash);
        if (adjustedGap > dash) {
            adjustedGap -= dash;
        }
        new FixedDashedBorder(getColor(), width, getOpacity(), dash, adjustedGap, dash + adjustedGap / 2)
                .draw(canvas, x1, y1, x2, y2, horizontalRadius1, verticalRadius1, horizontalRadius2, verticalRadius2,
                        defaultSide, borderWidthBefore, borderWidthAfter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        float initialGap = width * GAP_MODIFIER;
        float dash = width * DASH_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = super.getDotsGap(borderLength, initialGap + dash);
        if (adjustedGap > dash) {
            adjustedGap -= dash;
        }

        new FixedDashedBorder(getColor(), width, getOpacity(), dash, adjustedGap, dash + adjustedGap / 2)
                .drawCellBorder(canvas, x1, y1, x2, y2, defaultSide);
    }
}
