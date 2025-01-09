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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

import java.io.InputStream;

/**
 * Representation on an ICC Based color space.
 */
public class IccBased extends Color {


    /**
     * Creates an ICC Based color using the given {@link PdfCieBasedCs} color space.
     *
     * @param cs Color space
     */
    public IccBased(PdfCieBasedCs.IccBased cs) {
        this(cs, new float[cs.getNumberOfComponents()]);
    }

    /**
     * Creates an ICC Based color using the given {@link PdfCieBasedCs} color space and color values.
     *
     * @param cs    Color space
     * @param value Color values
     */
    public IccBased(PdfCieBasedCs.IccBased cs, float[] value) {
        super(cs, value);
    }

    /**
     * Creates IccBased color.
     *
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     */
    public IccBased(InputStream iccStream) {
        this(new PdfCieBasedCs.IccBased(iccStream), null);
        colorValue = new float[getNumberOfComponents()];
        for (int i = 0; i < getNumberOfComponents(); i++)
            colorValue[i] = 0f;
    }

    /**
     * Creates IccBased color.
     *
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     * @param value     color value.
     */
    public IccBased(InputStream iccStream, float[] value) {
        this(new PdfCieBasedCs.IccBased(iccStream), value);
    }

    /**
     * Creates an ICC Based color using the given ICC profile stream, range and color values.
     *
     * @param iccStream ICC profile stream. User is responsible for closing the stream.
     * @param range     Range for color
     * @param value     Color values
     */
    public IccBased(InputStream iccStream, float[] range, float[] value) {
        this(new PdfCieBasedCs.IccBased(iccStream, range), value);
        if (getNumberOfComponents() * 2 != range.length)
            throw new PdfException(KernelExceptionMessageConstant.INVALID_RANGE_ARRAY, this);
    }
}
