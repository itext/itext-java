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
package com.itextpdf.io.font.constants;

public final class FontWeights {

    private FontWeights() {
    }

    // Font weight Thin
    public static final int THIN = 100;

    // Font weight Extra-light (Ultra-light)
    public static final int EXTRA_LIGHT = 200;

    // Font weight Light
    public static final int LIGHT = 300;

    // Font weight Normal
    public static final int NORMAL = 400;

    // Font weight Medium
    public static final int MEDIUM = 500;

    // Font weight Semi-bold
    public static final int SEMI_BOLD = 600;

    // Font weight Bold
    public static final int BOLD = 700;

    // Font weight Extra-bold (Ultra-bold)
    public static final int EXTRA_BOLD = 800;

    // Font weight Black (Heavy)
    public static final int BLACK = 900;

    public static int fromType1FontWeight(String weight) {
        int fontWeight = NORMAL;
        switch (weight.toLowerCase()) {
            case "ultralight":
                fontWeight = THIN;
                break;
            case "thin":
            case "extralight":
                fontWeight = EXTRA_LIGHT;
                break;
            case "light":
                fontWeight = LIGHT;
                break;
            case "book":
            case "regular":
            case "normal":
                fontWeight = NORMAL;
                break;
            case "medium":
                fontWeight = MEDIUM;
                break;
            case "demibold":
            case "semibold":
                fontWeight = SEMI_BOLD;
                break;
            case "bold":
                fontWeight = BOLD;
                break;
            case "extrabold":
            case "ultrabold":
                fontWeight = EXTRA_BOLD;
                break;
            case "heavy":
            case "black":
            case "ultra":
            case "ultrablack":
                fontWeight = BLACK;
                break;
            case "fat":
            case "extrablack":
                fontWeight = BLACK;
                break;
        }
        return fontWeight;
    }

    public static int normalizeFontWeight(int fontWeight) {
        fontWeight = (fontWeight/100)*100;
        if (fontWeight < FontWeights.THIN) return FontWeights.THIN;
        if (fontWeight > FontWeights.BLACK) return FontWeights.BLACK;
        return fontWeight;
    }
}
