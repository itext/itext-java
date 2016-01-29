package com.itextpdf.kernel.crypto;

/**
 * An initialization vector generator for a CBC block encryption. It's a random generator based on ARCFOUR.
 *
 * @author Paulo Soares
 */
public final class IVGenerator {

    private static final ARCFOUREncryption arcfour;

    static {
        arcfour = new ARCFOUREncryption();
        long time = System.currentTimeMillis();
        long mem = Runtime.getRuntime().freeMemory();
        String s = time + "+" + mem;
        arcfour.prepareARCFOURKey(s.getBytes());
    }

    /**
     * Creates a new instance of IVGenerator
     */
    private IVGenerator() {
    }

    /**
     * Gets a 16 byte random initialization vector.
     *
     * @return a 16 byte random initialization vector
     */
    public static byte[] getIV() {
        return getIV(16);
    }

    /**
     * Gets a random initialization vector.
     *
     * @param len the length of the initialization vector
     * @return a random initialization vector
     */
    public static byte[] getIV(int len) {
        byte[] b = new byte[len];
        synchronized (arcfour) {
            arcfour.encryptARCFOUR(b);
        }
        return b;
    }
}