package com.itextpdf.io.font.cmap;

import com.itextpdf.io.source.PdfTokenizer;

/**
 * @author psoares
 */
public interface CMapLocation {
    PdfTokenizer getLocation(String location) throws java.io.IOException;
}
