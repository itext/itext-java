package com.itextpdf.io.util;

/**
 * A helper data class, which aggregates true/false result of ImageMagick comparing
 * as well as the number of different pixels.
 */
public final class ImageMagickCompareResult {

    private final boolean result;
    private final long diffPixels;

    /**
     * Creates an instance that contains ImageMagick comparing result information.
     *
     * @param result     true, if the compared images are equal.
     * @param diffPixels number of different pixels.
     */
    public ImageMagickCompareResult(boolean result, long diffPixels) {
        this.result = result;
        this.diffPixels = diffPixels;
    }

    /**
     * Returns image compare boolean value.
     *
     * @return true if the compared images are equal.
     */
    public boolean isComparingResultSuccessful() {
        return result;
    }

    /**
     * Getter for a different pixels count.
     *
     * @return Returns a a different pixels count.
     */
    public long getDiffPixels() {
        return diffPixels;
    }
}
