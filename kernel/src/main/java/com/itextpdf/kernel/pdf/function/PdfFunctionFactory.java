/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
