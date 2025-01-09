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

import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors.IInputConversionFunction;
import com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors.IOutputConversionFunction;

import java.io.IOException;

public interface IPdfFunction {

    int getFunctionType();

    boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace);

    int getInputSize();

    int getOutputSize();

    double[] getDomain();

    void setDomain(double[] value);

    double[] getRange();

    void setRange(double[] value);

    /**
     * Calculates one set of input components to one set of output components.
     *
     * @param input The input values size must contain {@link #getInputSize()}  items
     *
     * @return an array the size of {@link  #getOutputSize()} items containing the result
     */
    double[] calculate(double[] input);

    byte[] calculateFromByteArray(byte[] bytes, int offset, int length, int wordSizeInputLength,
            int wordSizeOutputLength) throws IOException;

    byte[] calculateFromByteArray(byte[] bytes, int offset, int length, int wordSizeInputLength,
            int wordSizeOutputLength, IInputConversionFunction inputConvertor,
            IOutputConversionFunction outputConvertor) throws IOException;

    double[] clipInput(double[] input);

    double[] clipOutput(double[] input);

    PdfObject getAsPdfObject();
}
