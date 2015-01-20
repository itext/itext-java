package com.itextpdf.core.crypto;

public class StandardDecryption {

    protected ARCFOUREncryption arcfour;
    protected AESCipher cipher;
    private byte[] key;
    private static final int AES_128 = 4;
    private static final int AES_256 = 5;
    private boolean aes;
    private boolean initiated;
    private byte[] iv = new byte[16];
    private int ivptr;

    /**
     * Creates a new instance of StandardDecryption
     */
    public StandardDecryption(byte key[], int off, int len, int revision) {
        aes = (revision == AES_128 || revision == AES_256);
        if (aes) {
            this.key = new byte[len];
            System.arraycopy(key, off, this.key, 0, len);
        } else {
            arcfour = new ARCFOUREncryption();
            arcfour.prepareARCFOURKey(key, off, len);
        }
    }

    public byte[] update(byte[] b, int off, int len) {
        if (aes) {
            if (initiated) {
                return cipher.update(b, off, len);
            } else {
                int left = Math.min(iv.length - ivptr, len);
                System.arraycopy(b, off, iv, ivptr, left);
                off += left;
                len -= left;
                ivptr += left;
                if (ivptr == iv.length) {
                    cipher = new AESCipher(false, key, iv);
                    initiated = true;
                    if (len > 0)
                        return cipher.update(b, off, len);
                }
                return null;
            }
        } else {
            byte[] b2 = new byte[len];
            arcfour.encryptARCFOUR(b, off, len, b2, 0);
            return b2;
        }
    }

    public byte[] finish() {
        if (aes) {
            return cipher.doFinal();
        } else {
            return null;
        }
    }
}