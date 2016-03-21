package com.itextpdf.kernel.crypto;

public class AesDecryptor implements Decryptor {
    private AESCipher cipher;
    private byte[] key;
    private boolean initiated;
    private byte[] iv = new byte[16];
    private int ivptr;

    /**
     * Creates a new instance of AesDecryption
     */
    public AesDecryptor(byte key[], int off, int len) {
        this.key = new byte[len];
        System.arraycopy(key, off, this.key, 0, len);
    }

    public byte[] update(byte[] b, int off, int len) {
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
    }

    public byte[] finish() {
        if (cipher != null) {
            return cipher.doFinal();
        } else {
            return null;
        }
    }
}
