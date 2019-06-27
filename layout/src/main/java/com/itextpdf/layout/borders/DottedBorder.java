/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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

    /**
     * Adjusts the size of the gap between dots
     *
     * @param distance   the {@link Border border} length
     * @param initialGap the initial size of the gap
     * @return the adjusted size of the gap
     * @deprecated logic moved to super-class
     */
    @Deprecated
    protected float getDotsGap(double distance, float initialGap) {
        return super.getDotsGap(distance, initialGap);
    }
}
