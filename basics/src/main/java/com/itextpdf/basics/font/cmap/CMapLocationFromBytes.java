package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.io.PdfTokeniser;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;

import java.io.IOException;

/**
 * @author psoares
 */
public class CMapLocationFromBytes implements CMapLocation {

    private byte[] data;

    public CMapLocationFromBytes(byte[] data) {
        this.data = data;
    }

    public PdfTokeniser getLocation(String location) throws IOException {
        return new PdfTokeniser(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(data)));
    }
}
