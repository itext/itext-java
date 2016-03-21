package com.itextpdf.kernel.crypto;

public class StandardDecryptor implements Decryptor {

    protected ARCFOUREncryption arcfour;

    /**
     * Creates a new instance of StandardDecryption
     */
    public StandardDecryptor(byte key[], int off, int len) {
        arcfour = new ARCFOUREncryption();
        arcfour.prepareARCFOURKey(key, off, len);
    }

    public byte[] update(byte[] b, int off, int len) {
        byte[] b2 = new byte[len];
        arcfour.encryptARCFOUR(b, off, len, b2, 0);
        return b2;
    }

    public byte[] finish() {
        return null;
    }
}