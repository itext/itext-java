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
package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

public class PdfType4Function extends AbstractPdfFunction<PdfStream> {
    public PdfType4Function(PdfStream dict) {
        super(dict);
    }

    public PdfType4Function(double[] domain, double[] range, byte[] code) {
        super(new PdfStream(code), PdfFunctionFactory.FUNCTION_TYPE_4, domain, range);
    }

    public PdfType4Function(float[] domain, float[] range, byte[] code) {
        this(convertFloatArrayToDoubleArray(domain), convertFloatArrayToDoubleArray(range), code);
    }

    @Override
    public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
        return getInputSize() == 1 && getOutputSize() == alternateSpace.getNumberOfComponents();
    }

    @Override
    public double[] calculate(double[] input) {
        throw new UnsupportedOperationException(KernelExceptionMessageConstant.TYPE4_EXECUTION_NOT_SUPPORTED);
    }
}
