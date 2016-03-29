package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSource;
import com.itextpdf.io.source.RandomAccessSourceFactory;

import java.net.URL;

public class TiffImage extends RawImage {

    private boolean recoverFromImageError;
    private int page;
    private boolean direct;

    protected TiffImage(URL url, boolean recoverFromImageError, int page, boolean direct) {
        super(url, ImageType.TIFF);
        this.recoverFromImageError = recoverFromImageError;
        this.page = page;
        this.direct = direct;
    }

    protected TiffImage(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        super(bytes, ImageType.TIFF);
        this.recoverFromImageError = recoverFromImageError;
        this.page = page;
        this.direct = direct;
    }

    private static Image getImage(URL url, boolean recoverFromImageError, int page, boolean direct) {
        return new TiffImage(url, recoverFromImageError, page, direct);
    }

    private static Image getImage(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        return new TiffImage(bytes, recoverFromImageError, page, direct);
    }

    /**
     * Gets the number of pages the TIFF document has.
     * @param raf a {@code RandomAccessFileOrArray} containing a TIFF image.
     * @return the number of pages.
     */
    public static int getNumberOfPages(RandomAccessFileOrArray raf) {
        try {
            return TIFFDirectory.getNumDirectories(raf);
        } catch (Exception e) {
            throw new IOException(IOException.TiffImageException, e);
        }
    }

    /** Gets the number of pages the TIFF document has.
     * @param bytes	a byte array containing a TIFF image.
     * @return the number of pages.
     */
    public static int getNumberOfPages(byte[] bytes) {
        RandomAccessSource ras = new RandomAccessSourceFactory().createSource(bytes);
        return getNumberOfPages(new RandomAccessFileOrArray(ras));
    }

    public boolean isRecoverFromImageError() {
        return recoverFromImageError;
    }

    public int getPage() {
        return page;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setOriginalType(ImageType originalType) {
        this.originalType = originalType;
    }
}
