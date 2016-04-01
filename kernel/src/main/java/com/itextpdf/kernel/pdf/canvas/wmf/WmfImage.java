package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Image implementation for WMF, Windows Metafile.
 */
public class WmfImage extends Image {

    private static final byte[] wmf = new byte[]{(byte) 0xD7, (byte) 0xCD};

    /**
     * Creates a WmfImage from a file.
     *
     * @param fileName pah to the file
     * @throws MalformedURLException
     */
    public WmfImage(String fileName) throws MalformedURLException {
        this(UrlUtil.toURL(fileName));
    }

    /**
     * Creates a WmfImage from a URL.
     *
     * @param url URL to the file
     */
    public WmfImage(URL url) {
        super(url, ImageType.WMF);
        byte[] imageType = readImageType(url);
        if (!imageTypeIs(imageType, wmf)) {
            throw new PdfException(PdfException.IsNotWmfImage);
        }
    }

    /**
     * Creates a WmfImage from a byte[].
     * @param bytes the image bytes
     */
    public WmfImage(byte[] bytes) {
        super(bytes, ImageType.WMF);
        byte[] imageType = readImageType(url);
        if (!imageTypeIs(imageType, wmf)) {
            throw new PdfException(PdfException.IsNotWmfImage);
        }
    }

    private static boolean imageTypeIs(byte[] imageType, byte[] compareWith) {
        for (int i = 0; i < compareWith.length; i++) {
            if (imageType[i] != compareWith[i])
                return false;
        }
        return true;
    }

    private static <T> byte[] readImageType(T source) {
        InputStream is = null;
        try {
            if (source instanceof URL) {
                is = ((URL) source).openStream();
            } else {
                is = new ByteArrayInputStream((byte[])source);
            }
            byte[] bytes = new byte[8];
            is.read(bytes);
            return bytes;
        } catch (IOException e) {
            throw new PdfException(PdfException.IoException, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) { }
            }
        }

    }
}
