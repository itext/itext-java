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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Draws a border with a specific dashes around the element it's been set to.
 */
public class FixedDashedBorder extends Border {

    /**
     * Default dash unitsOn and unitsOff value.
     */
    public static final float DEFAULT_UNITS_VALUE = 3;

    private final float unitsOn;
    private final float unitsOff;
    private final float phase;

    /**
     * Creates a FixedDashedBorder with the specified width.
     *
     * @param width width of the border
     */
    public FixedDashedBorder(float width) {
        this(ColorConstants.BLACK, width);
    }

    /**
     * Creates a FixedDashedBorder with the specified width and the specified color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public FixedDashedBorder(Color color, float width) {
        this(color, width, 1f);
    }

    /**
     * Creates a FixedDashedBorder with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity the opacity which border should have
     */
    public FixedDashedBorder(Color color, float width, float opacity) {
        this(color, width, opacity, DEFAULT_UNITS_VALUE, DEFAULT_UNITS_VALUE, 0);
    }

    /**
     * Creates a FixedDashedBorder with the specified width, color, unitsOn, unitsOff and phase.
     *
     * @param color    color of the border
     * @param width    width of the border
     * @param unitsOn  the number of units that must be 'on'
     * @param unitsOff the number of units that must be 'off'
     * @param phase    the value of the phase
     */
    public FixedDashedBorder(Color color, float width, float unitsOn, float unitsOff, float phase) {
        this(color, width, 1f, unitsOn, unitsOff, phase);
    }

    /**
     * Creates a FixedDashedBorder with the specified width, color, opacity, unitsOn, unitsOff and phase.
     *
     * @param color    color of the border
     * @param width    width of the border
     * @param opacity  the opacity which border should have
     * @param unitsOn  the number of units that must be 'on'
     * @param unitsOff the number of units that must be 'off'
     * @param phase    the value of the phase
     */
    public FixedDashedBorder(Color color, float width, float opacity, float unitsOn, float unitsOff, float phase) {
        super(color, width, opacity);
        this.unitsOn = unitsOn;
        this.unitsOff = unitsOff;
        this.phase = phase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide,
            float borderWidthBefore, float borderWidthAfter) {
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
                .setLineDash(unitsOn, unitsOff, phase)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke()
                .restoreState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2,
            float horizontalRadius1, float verticalRadius1, float horizontalRadius2, float verticalRadius2,
            Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        canvas
                .saveState()
                .setLineWidth(width)
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.setLineDash(unitsOn, unitsOff, phase);

        Rectangle boundingRectangle = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        float[] horizontalRadii = new float[]{horizontalRadius1, horizontalRadius2};
        float[] verticalRadii = new float[]{verticalRadius1, verticalRadius2};

        drawDiscontinuousBorders(canvas, boundingRectangle, horizontalRadii, verticalRadii,
                defaultSide, borderWidthBefore, borderWidthAfter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        canvas
                .saveState()
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas
                .setLineDash(unitsOn, unitsOff, phase)
                .setLineWidth(width)
                .moveTo(x1, y1)
                .lineTo(x2, y2)
                .stroke()
                .restoreState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return Border.DASHED_FIXED;
    }
}
