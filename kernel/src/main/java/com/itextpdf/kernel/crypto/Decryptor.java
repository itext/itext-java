package com.itextpdf.kernel.crypto;

public interface Decryptor {
    byte[] update(byte[] b, int off, int len);
    byte[] finish();
}
