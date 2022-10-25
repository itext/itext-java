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

public final class BaseInputOutPutConvertors {

    private BaseInputOutPutConvertors(){}

    public static IInputConversionFunction getInputConvertor(int wordSize, double scaleFactor) {
        return getByteBasedInputConvertor(wordSize, scaleFactor * (1L << (wordSize * 8)) -1 );
    }

    public static IOutputConversionFunction getOutputConvertor(int wordSize, double scaleFactor) {
        return getByteBasedOutputConvertor(wordSize, scaleFactor * (1L << (wordSize * 8)) -1 );
    }

    private static IInputConversionFunction getByteBasedInputConvertor(int wordSize, double scale) {
        return (byte[] input, int o, int l) -> {
            if (o + l > input.length) {
                throw new IllegalArgumentException(KernelExceptionMessageConstant.INVALID_LENGTH);
            }
            if (l % wordSize != 0) {
                throw new IllegalArgumentException(
                        MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_LENGTH_FOR_WORDSIZE, wordSize));
            }
            double[] out = new double[l / wordSize];
            int inIndex = o;
            int outIndex = 0;
            while (inIndex < (l+o)) {
                int val = 0;
                for (int wordIndex = 0; wordIndex < wordSize; wordIndex++) {
                    val = ((val << 8) + (input[inIndex + wordIndex] & 0xff));
                    inIndex++;
                }
                out[outIndex] = val / scale;
                outIndex++;
            }
            return out;
        };
    }

    private static IOutputConversionFunction getByteBasedOutputConvertor(int wordSize, double scale) {
        return (double[] input) -> {
            byte[] out = new byte[input.length * wordSize];
            int inIndex = 0;
            int outIndex = 0;
            while (inIndex < input.length && outIndex < out.length) {
                final int val = (int) (input[inIndex] * scale);
                for (int wordIndex = 0; wordIndex < wordSize; wordIndex++) {
                    out[outIndex++] = (byte)(val >>> (wordIndex * 8));
                }
                inIndex++;
            }
            return out;
        };
    }

    /**
     * A functional interface for providing conversion of raw input to an array of double values.
     */
    @FunctionalInterface
    public interface IInputConversionFunction {
        /**
         * A method that converts a raw byte array to an array of double values.
         *
         * @param input the raw data
         * @param offset where to start processing
         * @param length how many bytes to process
         * @return an array of double values
         */
        double[] convert(byte[] input, int offset, int length);
    }


    @FunctionalInterface
    public interface IOutputConversionFunction {
        byte[] convert(double[] input);
    }
}
