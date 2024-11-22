/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;

import java.util.Arrays;

/**
 * This class represents {@code text} and {@code tspan} SVG elements properties identifying their graphics state.
 * Created for internal usage.
 */
public class SvgTextProperties {
    private Color fillColor = DeviceGray.BLACK;
    private Color strokeColor = DeviceGray.BLACK;
    private float fillOpacity = 1f;
    private float strokeOpacity = 1f;
    private PdfArray dashPattern = new PdfArray(Arrays.asList(new PdfObject[]{new PdfArray(), new PdfNumber(0)}));
    private float lineWidth = 1f;

    /**
     * Creates new {@link SvgTextProperties} instance.
     */
    public SvgTextProperties() {
        // Empty constructor in order for default one to not be removed if another one is added.
    }

    /**
     * Creates copy of the provided {@link SvgTextProperties} instance.
     *
     * @param textProperties {@link SvgTextProperties} instance to copy
     */
    public SvgTextProperties(SvgTextProperties textProperties) {
        this.fillColor = textProperties.getFillColor();
        this.strokeColor = textProperties.getStrokeColor();
        this.fillOpacity = textProperties.getFillOpacity();
        this.strokeOpacity = textProperties.getStrokeOpacity();
        this.dashPattern = textProperties.getDashPattern();
        this.lineWidth = textProperties.getLineWidth();
    }

    /**
     * Gets text stroke color.
     *
     * @return stroke color
     */
    public Color getStrokeColor() {
        return strokeColor;
    }

    /**
     * Sets text stroke color.
     *
     * @param strokeColor stroke color to set
     *
     * @return this same {@link SvgTextProperties} instance
     */
    public SvgTextProperties setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
        return this;
    }

    /**
     * Gets text fill color.
     *
     * @return fill color
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Sets text fill color.
     *
     * @param fillColor fill color to set
     *
     * @return this same {@link SvgTextProperties} instance
     */
    public SvgTextProperties setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    /**
     * Gets text line (or stroke) width.
     *
     * @return text line width
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets text line (or stroke) width.
     *
     * @param lineWidth text line width
     *
     * @return this same {@link SvgTextProperties} instance
     */
    public SvgTextProperties setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    /**
     * Gets text stroke opacity.
     *
     * @return stroke opacity
     */
    // TODO DEVSIX-8774 support stroke-opacity for text at layout level
    public float getStrokeOpacity() {
        return strokeOpacity;
    }

    /**
     * Sets text stroke opacity.
     *
     * @param strokeOpacity stroke opacity to set
     *
     * @return this same {@link SvgTextProperties} instance
     */
    public SvgTextProperties setStrokeOpacity(float strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
        return this;
    }

    /**
     * Gets text fill opacity.
     *
     * @return fill opacity
     */
    public float getFillOpacity() {
        return fillOpacity;
    }

    /**
     * Sets text fill opacity.
     *
     * @param fillOpacity fill opacity to set
     *
     * @return this same {@link SvgTextProperties} instance
     */
    public SvgTextProperties setFillOpacity(float fillOpacity) {
        this.fillOpacity = fillOpacity;
        return this;
    }

    /**
     * Gets a description of the dash pattern to be used when paths are stroked. Default value is solid line.
     *
     * <p>
     * The line dash pattern is expressed as an array of the form [ dashArray dashPhase ],
     * where dashArray is itself an array and dashPhase is an integer.
     *
     * <p>
     * An empty dash array (first element in the array) and zero phase (second element in the array)
     * can be used to restore the dash pattern to a solid line.
     *
     * @return dash pattern array
     */
    // TODO DEVSIX-8776 support dash-pattern in layout
    public PdfArray getDashPattern() {
        return dashPattern;
    }

    /**
     * Sets a description of the dash pattern to be used when paths are stroked. Default value is solid line.
     *
     * <p>
     * The line dash pattern is expressed as an array of the form [ dashArray dashPhase ],
     * where dashArray is itself an array and dashPhase is a number.
     *
     * <p>
     * An empty dash array (first element in the array) and zero phase (second element in the array)
     * can be used to restore the dash pattern to a solid line.
     *
     * @param dashArray dash array
     * @param dashPhase dash phase value
     *
     * @return this same {@link SvgTextProperties} instance
     */
    public SvgTextProperties setDashPattern(float[] dashArray, float dashPhase) {
        this.dashPattern = getDashPatternArray(dashArray, dashPhase);
        return this;
    }

    private static PdfArray getDashPatternArray(float[] dashArray, float phase) {
        PdfArray dashPatternArray = new PdfArray();
        PdfArray dArray = new PdfArray();
        if (dashArray != null) {
            for (float fl : dashArray) {
                dArray.add(new PdfNumber(fl));
            }
        }
        dashPatternArray.add(dArray);
        dashPatternArray.add(new PdfNumber(phase));
        return dashPatternArray;
    }
}
