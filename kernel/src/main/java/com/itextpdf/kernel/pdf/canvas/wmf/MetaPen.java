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
package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;

import java.io.IOException;

/**
 * A Pen object of the WMF format. Holds the color, style and width information of the pen.
 */
public class MetaPen extends MetaObject {

    public static final int PS_SOLID = 0;
    public static final int PS_DASH = 1;
    public static final int PS_DOT = 2;
    public static final int PS_DASHDOT = 3;
    public static final int PS_DASHDOTDOT = 4;
    public static final int PS_NULL = 5;
    public static final int PS_INSIDEFRAME = 6;

    int style = PS_SOLID;
    int penWidth = 1;
    Color color = ColorConstants.BLACK;

    /**
     * Creates a MetaPen object.
     */
    public MetaPen() {
        super(META_PEN);
    }

    /**
     * Initializes a MetaPen object.
     *
     * @param in the InputMeta object that holds the inputstream of the WMF image
     * @throws IOException an {@link IOException}
     */
    public void init(InputMeta in) throws IOException {
        style = in.readWord();
        penWidth = in.readShort();
        in.readWord();
        color = in.readColor();
    }

    /**
     * Get the style of the MetaPen.
     *
     * @return style of the pen
     */
    public int getStyle() {
        return style;
    }

    /**
     * Get the width of the MetaPen.
     *
     * @return width of the pen
     */
    public int getPenWidth() {
        return penWidth;
    }

    /**
     * Get the color of the MetaPen.
     *
     * @return color of the pen
     */
    public Color getColor() {
        return color;
    }
}
