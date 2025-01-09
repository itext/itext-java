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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Draws a dotted border around the element it has been set to. Do note that this border draw square dots,
 * if you want to draw round dots, see {@link RoundDotsBorder}.
 */
public class DottedBorder extends Border {

    /**
     * The modifier to be applied on the width to have the initial gap size
     */
    private static final float GAP_MODIFIER = 1.5f;

    /**
     * Creates a DottedBorder instance with the specified width. The color is set to the default: black.
     *
     * @param width width of the border
     */
    public DottedBorder(float width) {
        super(width);
    }

    /**
     * Creates a DottedBorder instance with the specified width and color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public DottedBorder(Color color, float width) {
        super(color, width);
    }

    /**
     * Creates a DottedBorder with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity width of the border
     */
    public DottedBorder(Color color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return Border.DOTTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * GAP_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = super.getDotsGap(borderLength, initialGap + width);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        float[] startingPoints = getStartingPointsForBorderSide(x1, y1, x2, y2, defaultSide);
        x1 = startingPoints[0];
        y1 = startingPoints[1];
        x2 = startingPoints[2];
        y2 = startingPoints[3];

        canvas
                .saveState()
                .setLineWidth(width)
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas
                .setLineDash(width, adjustedGap, width + adjustedGap / 2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke()
                .restoreState();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float horizontalRadius1, float verticalRadius1, float horizontalRadius2, float verticalRadius2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * GAP_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);
        float adjustedGap = super.getDotsGap(borderLength, initialGap);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        canvas
                .saveState()
                .setLineWidth(width)
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.setLineDash(width, adjustedGap, width + adjustedGap / 2);

        Rectangle boundingRectangle = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        float[] horizontalRadii = new float[]{horizontalRadius1, horizontalRadius2};
        float[] verticalRadii = new float[]{verticalRadius1, verticalRadius2};

        drawDiscontinuousBorders(canvas, boundingRectangle, horizontalRadii, verticalRadii, defaultSide, borderWidthBefore, borderWidthAfter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        float initialGap = width * GAP_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = super.getDotsGap(borderLength, initialGap + width);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        canvas
                .saveState()
                .setLineWidth(width)
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas
                .setLineDash(width, adjustedGap, width + adjustedGap / 2)
                .moveTo(x1, y1)
                .lineTo(x2, y2)
                .stroke()
                .restoreState();
    }
}
