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
