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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.function.IPdfFunction;

import java.util.Arrays;
import java.util.List;

/**
 * Representation of a DeviceN color space.
 */
public class DeviceN extends Color {


    /**
     * Creates a DeviceN color using the given {@link PdfSpecialCs} color space.
     *
     * @param cs Color space
     */
    public DeviceN(PdfSpecialCs.DeviceN cs) {
        this(cs, getDefaultColorants(cs.getNumberOfComponents()));
    }

    /**
     * Creates a DeviceN color using the given {@link PdfSpecialCs} color space and color values.
     *
     * @param cs    Color space
     * @param value Color component values
     */
    public DeviceN(PdfSpecialCs.DeviceN cs, float[] value) {
        super(cs, value);
    }

    /**
     * Creates a color in a new DeviceN color space.
     *
     * @param names         the names oif the components
     * @param alternateCs   the alternate color space
     * @param tintTransform the function to transform color to the alternate color space
     * @param value         the values for the components of this color
     */
    public DeviceN(List<String> names, PdfColorSpace alternateCs, IPdfFunction tintTransform, float[] value) {
        this(new PdfSpecialCs.DeviceN(names, alternateCs, tintTransform), value);
    }

    private static float[] getDefaultColorants(int numOfColorants) {
        float[] colorants = new float[numOfColorants];
        Arrays.fill(colorants, 1f);
        return colorants;
    }
}
