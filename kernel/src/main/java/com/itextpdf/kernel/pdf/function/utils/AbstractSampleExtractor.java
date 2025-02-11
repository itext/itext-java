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
package com.itextpdf.kernel.pdf.function.utils;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

public abstract class AbstractSampleExtractor {

    public abstract long extract(byte[] samples, int pos);

    public static AbstractSampleExtractor createExtractor(int bitsPerSample) {
        switch (bitsPerSample) {
            case 1:
            case 2:
            case 4:
                return new SampleBitsExtractor(bitsPerSample);
            case 8:
            case 16:
            case 24:
            case 32:
                return new SampleBytesExtractor(bitsPerSample);
            case 12:
                return new Sample12BitsExtractor();
            default:
                throw new IllegalArgumentException(
                        KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_BITS_PER_SAMPLE_INVALID_VALUE);
        }
    }

    private static class SampleBitsExtractor extends AbstractSampleExtractor {

        private final int bitsPerSample;
        private final byte mask;

        public SampleBitsExtractor(int bitsPerSample) {
            this.bitsPerSample = bitsPerSample;
            this.mask = (byte) ((1 << bitsPerSample) - 1);
        }

        @Override
        public long extract(byte[] samples, int position) {
            int bitPos = position * bitsPerSample;
            int bytePos = bitPos >> 3;
            int shift = 8 - (bitPos & 7) - bitsPerSample;
            return (samples[bytePos] >> shift) & mask;
        }
    }

    private final static class SampleBytesExtractor extends AbstractSampleExtractor {

        private final int bytesPerSample;

        public SampleBytesExtractor(int bitsPerSample) {
            bytesPerSample = bitsPerSample >> 3;
        }

        @Override
        public long extract(byte[] samples, int position) {
            int bytePos = position * bytesPerSample;
            long result = 0xff & samples[bytePos++];
            for (int i = 1; i < bytesPerSample; ++i) {
                result = (result << 8) | (0xff & samples[bytePos++]);
            }
            return result;
        }
    }

    private static class Sample12BitsExtractor extends AbstractSampleExtractor {

        @Override
        public long extract(byte[] samples, int position) {
            int bitPos = position * 12;
            int bytePos = bitPos >> 3;
            if ((bitPos & 4) == 0) {
                return ((0xff & samples[bytePos]) << 4) | ((0xf0 & samples[bytePos + 1]) >> 4);
            } else {
                return ((0x0f & samples[bytePos]) << 8) | (0xff & samples[bytePos + 1]);
            }
        }
    }
}
