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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

public final class PdfFunctionFactory {


    public static final int FUNCTION_TYPE_0 = 0;
    public static final int FUNCTION_TYPE_2 = 2;
    public static final int FUNCTION_TYPE_3 = 3;
    public static final int FUNCTION_TYPE_4 = 4;

    private PdfFunctionFactory() {}

    /**
     * Factory method to create a function instance based on an existing {@link PdfObject}.
     *
     * @param pdfObject Either a {@link PdfDictionary} or a {@link PdfStream} representing
     *                  a function
     *
     * @return Depending on the type, a {@link  PdfType0Function}, a {@link  PdfType2Function},
     * a {@link  PdfType3Function} or a {@link  PdfType4Function}
     */
    public static IPdfFunction create(PdfObject pdfObject) {
        if (pdfObject.isDictionary() || pdfObject.isStream()) {
            final PdfDictionary dict = (PdfDictionary) pdfObject;

            switch (dict.getAsNumber(PdfName.FunctionType).intValue()) {
                case FUNCTION_TYPE_0:
                    if (pdfObject.getType() != PdfObject.STREAM) {
                        throw new PdfException(KernelExceptionMessageConstant.FUCTIONFACTORY_INVALID_OBJECT_TYPE_TYPE0);
                    }
                    return new PdfType0Function((PdfStream) pdfObject);
                case FUNCTION_TYPE_2:
                    return new PdfType2Function(dict);
                case FUNCTION_TYPE_3:
                    return new PdfType3Function(dict);
                case FUNCTION_TYPE_4:
                    if (pdfObject.getType() != PdfObject.STREAM) {
                        throw new PdfException(KernelExceptionMessageConstant.FUCTIONFACTORY_INVALID_OBJECT_TYPE_TYPE4);
                    }
                    return new PdfType4Function((PdfStream) pdfObject);
                default:
                    throw new PdfException(MessageFormatUtil.format(
                            KernelExceptionMessageConstant.FUCTIONFACTORY_INVALID_FUNCTION_TYPE ,
                            dict.getAsNumber(PdfName.FunctionType).intValue()));
            }
        }
        throw new PdfException(KernelExceptionMessageConstant.FUCTIONFACTORY_INVALID_OBJECT_TYPE);
    }
}
