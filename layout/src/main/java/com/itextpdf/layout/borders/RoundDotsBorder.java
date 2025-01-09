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
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

/**
 * Draws a border with rounded dots around the element it's been set to. For square dots see {@link DottedBorder}.
 */
public class RoundDotsBorder extends Border {

    /**
     * The modifier to be applied on the width to have the initial gap size
     */
    private static final float GAP_MODIFIER = 2.5f;

    /**
     * Creates a RoundDotsBorder with the specified wit?dth and sets the color to black.
     *
     * @param width width of the border
     */
    public RoundDotsBorder(float width) {
        super(width);
    }

    /**
     * Creates a RoundDotsBorder with the specified wit?dth and the specified color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public RoundDotsBorder(Color color, float width) {
        super(color, width);
    }

    /**
     * Creates a RoundDotsBorder with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity width of the border
     */
    public RoundDotsBorder(Color color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return ROUND_DOTS;
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
        float adjustedGap = super.getDotsGap(borderLength, initialGap);

        float[] startingPoints = getStartingPointsForBorderSide(x1, y1, x2, y2, defaultSide);
        x1 = startingPoints[0];
        y1 = startingPoints[1];
        x2 = startingPoints[2];
        y2 = startingPoints[3];

        canvas.saveState()
                .setStrokeColor(transparentColor.getColor())
                .setLineWidth(width)
                .setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);
        transparentColor.applyStrokeTransparency(canvas);
        canvas.setLineDash(0, adjustedGap, adjustedGap/2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke()
                .restoreState();
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
        float adjustedGap = super.getDotsGap(borderLength, initialGap);
        boolean isHorizontal = false;
        if (Math.abs(y2 - y1) < 0.0005f) {
            isHorizontal = true;
        }

        if (isHorizontal) {
            x2 -= width;
        }
        canvas.saveState();
        canvas.setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.setLineWidth(width);
        canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);

        canvas.setLineDash(0, adjustedGap, adjustedGap / 2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke();
        canvas.restoreState();
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

        canvas
                .saveState()
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas
                .setLineWidth(width)
                .setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND)
                .setLineDash(0, adjustedGap, adjustedGap / 2);

        Rectangle boundingRectangle = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        float[] horizontalRadii = new float[]{horizontalRadius1, horizontalRadius2};
        float[] verticalRadii = new float[]{verticalRadius1, verticalRadius2};

        drawDiscontinuousBorders(canvas, boundingRectangle, horizontalRadii, verticalRadii, defaultSide, borderWidthBefore, borderWidthAfter);
    }
}
