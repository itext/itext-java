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
 * A Brush bject that holds information about the style, the hatch and the color of
 * the brush.
 */
public class MetaBrush extends MetaObject {

    public static final int BS_SOLID = 0;
    public static final int BS_NULL = 1;
    public static final int BS_HATCHED = 2;
    public static final int BS_PATTERN = 3;
    public static final int BS_DIBPATTERN = 5;
    public static final int HS_HORIZONTAL = 0;
    public static final int HS_VERTICAL = 1;
    public static final int HS_FDIAGONAL = 2;
    public static final int HS_BDIAGONAL = 3;
    public static final int HS_CROSS = 4;
    public static final int HS_DIAGCROSS = 5;

    int style = BS_SOLID;
    int hatch;
    Color color = ColorConstants.WHITE;

    /**
     * Creates a MetaBrush object.
     */
    public MetaBrush() {
        super(META_BRUSH);
    }

    /**
     * Initializes this MetaBrush object.
     *
     * @param in the InputMeta
     * @throws IOException an {@link IOException}
     */
    public void init(InputMeta in) throws IOException {
        style = in.readWord();
        color = in.readColor();
        hatch = in.readWord();
    }

    /**
     * Get the style of the MetaBrush.
     *
     * @return style of the brush
     */
    public int getStyle() {
        return style;
    }

    /**
     * Get the hatch pattern of the MetaBrush
     * @return hatch of the brush
     */
    public int getHatch() {
        return hatch;
    }

    /**
     * Get the color of the MetaBrush.
     * @return color of the brush
     */
    public Color getColor() {
        return color;
    }
}
