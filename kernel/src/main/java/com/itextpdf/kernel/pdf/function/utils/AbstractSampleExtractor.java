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
