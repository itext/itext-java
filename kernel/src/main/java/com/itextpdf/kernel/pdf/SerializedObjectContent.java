package com.itextpdf.kernel.pdf;

import java.util.Arrays;

class SerializedObjectContent {
    private final byte[] serializedContent;
    private final int hash;

    SerializedObjectContent(byte[] serializedContent) {
        this.serializedContent = serializedContent;
        this.hash = calculateHash(serializedContent);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SerializedObjectContent
                && hashCode() == obj.hashCode()
                && Arrays.equals(serializedContent, ((SerializedObjectContent) obj).serializedContent);
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
