package com.itextpdf.canvas.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.io.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class WmfImage extends Image {

    private static final byte[] wmf = new byte[]{(byte) 0xD7, (byte) 0xCD};

    public WmfImage(String fileName) throws MalformedURLException {
        this(Utilities.toURL(fileName));
    }

    public WmfImage(URL url) {
        super(url, WMF);
        byte[] imageType = readImageType(url);
        if (!imageTypeIs(imageType, wmf)) {
            throw new PdfException(PdfException.IsNotWmfImage);
        }
    }

    public WmfImage(byte[] bytes) {
        super(bytes, WMF);
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
