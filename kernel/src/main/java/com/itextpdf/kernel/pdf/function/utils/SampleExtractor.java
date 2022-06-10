package com.itextpdf.kernel.pdf.function.utils;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

public abstract class SampleExtractor {

    public abstract long extract(byte[] samples, int pos);

    public static SampleExtractor createExtractor(int bitsPerSample) {
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

    private final static class SampleBitsExtractor extends SampleExtractor {

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

    private final static class SampleBytesExtractor extends SampleExtractor {

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

    private final static class Sample12BitsExtractor extends SampleExtractor {

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
