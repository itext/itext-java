package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.io.PdfTokenizer;

import java.io.IOException;

/**
 * @author psoares
 */
public interface CMapLocation {
    public PdfTokenizer getLocation(String location) throws IOException;
}
