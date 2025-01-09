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

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;

/**
 * Represents a {@link Border3D} with a groove effect being applied.
 */
public class GrooveBorder extends Border3D {

    /**
     * Creates a GrooveBorder instance with the specified width. The color is set to the predefined gray.
     *
     * @param width width of the border
     */
    public GrooveBorder(float width) {
        super(width);
    }

    /**
     * Creates a GrooveBorder instance with the specified width and the {@link DeviceRgb rgb color}.
     *
     * @param width width of the border
     * @param color the {@link DeviceRgb rgb color} of the border
     */
    public GrooveBorder(DeviceRgb color, float width) {
        super(color, width);
    }

    /**
     * Creates a GrooveBorder instance with the specified width and the {@link DeviceCmyk cmyk color}.
     *
     * @param width width of the border
     * @param color the {@link DeviceCmyk cmyk color} of the border
     */
    public GrooveBorder(DeviceCmyk color, float width) {
        super(color, width);
    }

    /**
     * Creates a GrooveBorder instance with the specified width and the {@link DeviceGray gray color}.
     *
     * @param width width of the border
     * @param color the {@link DeviceGray gray color} of the border
     */
    public GrooveBorder(DeviceGray color, float width) {
        super(color, width);
    }

    /**
     * Creates a GrooveBorder instance with the specified width, color and opacity.
     *
     * @param color color of the border
     * @param width width of the border
     * @param opacity opacity of the border
     */
    public GrooveBorder(DeviceRgb color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * Creates a GrooveBorder instance with the specified width, color and opacity.
     *
     * @param color color of the border
     * @param width width of the border
     * @param opacity opacity of the border
     */
    public GrooveBorder(DeviceCmyk color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * Creates a GrooveBorder instance with the specified width, color and opacity.
     *
     * @param color color of the border
     * @param width width of the border
     * @param opacity opacity of the border
     */
    public GrooveBorder(DeviceGray color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType(){
        return _3D_GROOVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInnerHalfColor(PdfCanvas canvas, Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(getColor());
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(getDarkerColor());
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setOuterHalfColor(PdfCanvas canvas, Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(getDarkerColor());
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(getColor());
                break;
        }
    }
}
