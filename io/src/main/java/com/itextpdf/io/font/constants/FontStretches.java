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

public final class FontStretches {

    private FontStretches() {
    }

    private static final int FWIDTH_ULTRA_CONDENSED = 1;
    private static final int FWIDTH_EXTRA_CONDENSED	= 2;
    private static final int FWIDTH_CONDENSED = 3;
    private static final int FWIDTH_SEMI_CONDENSED = 4;
    private static final int FWIDTH_NORMAL = 5;
    private static final int FWIDTH_SEMI_EXPANDED = 6;
    private static final int FWIDTH_EXPANDED = 7;
    private static final int FWIDTH_EXTRA_EXPANDED = 8;
    private static final int FWIDTH_ULTRA_EXPANDED = 9;


    public static final String ULTRA_CONDENSED = "UltraCondensed";

    public static final String EXTRA_CONDENSED = "ExtraCondensed";

    public static final String CONDENSED = "Condensed";

    public static final String SEMI_CONDENSED = "SemiCondensed";

    public static final String NORMAL = "Normal";

    public static final String SEMI_EXPANDED = "SemiExpanded";

    public static final String EXPANDED = "Expanded";

    public static final String EXTRA_EXPANDED = "ExtraExpanded";

    public static final String ULTRA_EXPANDED = "UltraExpanded";

    /**
     * Convert from Open Type font width class notation.
     * <p>
     * https://www.microsoft.com/typography/otspec/os2.htm#wdc
     *
     * @param fontWidth Open Type font width.
     * @return one of the {@link FontStretches} constants.
     */
    public static String fromOpenTypeWidthClass(int fontWidth) {
        String fontWidthValue = NORMAL;
        switch (fontWidth) {
            case FWIDTH_ULTRA_CONDENSED:
                fontWidthValue = ULTRA_CONDENSED;
                break;
            case FWIDTH_EXTRA_CONDENSED:
                fontWidthValue = EXTRA_CONDENSED;
                break;
            case FWIDTH_CONDENSED:
                fontWidthValue = CONDENSED;
                break;
            case FWIDTH_SEMI_CONDENSED:
                fontWidthValue = SEMI_CONDENSED;
                break;
            case FWIDTH_NORMAL:
                fontWidthValue = NORMAL;
                break;
            case FWIDTH_SEMI_EXPANDED:
                fontWidthValue = SEMI_EXPANDED;
                break;
            case FWIDTH_EXPANDED:
                fontWidthValue = EXPANDED;
                break;
            case FWIDTH_EXTRA_EXPANDED:
                fontWidthValue = EXTRA_EXPANDED;
                break;
            case FWIDTH_ULTRA_EXPANDED:
                fontWidthValue = ULTRA_EXPANDED;
                break;
        }
        return fontWidthValue;
    }
}
