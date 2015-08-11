package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.codec.Jbig2SegmentReader;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSource;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Jbig2Image extends Image {

    private int page;

    protected Jbig2Image(URL url, int page) {
        super(url, JBIG2);
        this.page = page;
    }

    protected Jbig2Image(byte[] bytes, int page) {
        super(bytes, JBIG2);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    /**
     * Gets the number of pages in a JBIG2 image.
     * @param bytes	a byte array containing a JBIG2 image
     * @return the number of pages
     */
    public static int getNumberOfPages(byte[] bytes) {
        RandomAccessSource ras = new RandomAccessSourceFactory().createSource(bytes);
        return getNumberOfPages(new RandomAccessFileOrArray(ras));
    }

    /**
     * Gets the number of pages in a JBIG2 image.
     * @param raf a {@code RandomAccessFileOrArray} containing a JBIG2 image
     * @return the number of pages
     */
    public static int getNumberOfPages(RandomAccessFileOrArray raf) {
        try {
            Jbig2SegmentReader sr = new Jbig2SegmentReader(raf);
            sr.read();
            return sr.numberOfPages();
        } catch (Exception e) {
            throw new PdfException(PdfException.Jbig2ImageException, e);
        }
    }

    @Override
    public boolean canImageBeInline() {
        Logger logger = LoggerFactory.getLogger(Image.class);
        logger.warn("Image cannot be inline if it has JBIG2Decode filter");
        return false;
    }
}
