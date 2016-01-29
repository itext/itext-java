package com.itextpdf.io.font.cmap;

import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;

/**
 * @author psoares
 */
public class CMapLocationFromBytes implements CMapLocation {

    private byte[] data;

    public CMapLocationFromBytes(byte[] data) {
        this.data = data;
    }

    public PdfTokenizer getLocation(String location) throws java.io.IOException {
        return new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(data)));
    }
}
