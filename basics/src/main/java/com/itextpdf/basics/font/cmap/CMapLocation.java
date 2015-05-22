package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.io.PdfTokeniser;

import java.io.IOException;

/**
 * @author psoares
 */
public interface CMapLocation {
    public PdfTokeniser getLocation(String location) throws IOException;
}
