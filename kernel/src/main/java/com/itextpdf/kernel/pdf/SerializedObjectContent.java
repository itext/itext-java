package com.itextpdf.kernel.pdf;

import java.util.Arrays;

class SerializedObjectContent {
    private final byte[] serializedContent;
    private final int hash;

    SerializedObjectContent(byte[] serializedContent) {
        this.serializedContent = serializedContent;
        this.hash = calculateHash(serializedContent);
    }

    /**
     * Compares this PdfWriter to the obj.
     * Two PdfWriters are equal if their hashcodes are equal and their serialized content are equal.
     *
     * @param obj obj to compare
     * @return True if this and obj are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof SerializedObjectContent && hashCode() == obj.hashCode() && Arrays.equals(serializedContent, ((SerializedObjectContent) obj).serializedContent);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private static int calculateHash(byte[] b) {
        int hash = 0;
        int len = b.length;
        for (int k = 0; k < len; ++k) {
            hash = hash * 31 + (b[k] & 0xff);
        }
        return hash;
    }
}
