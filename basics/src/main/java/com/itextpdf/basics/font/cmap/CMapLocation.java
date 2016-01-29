package com.itextpdf.basics.font.cmap;

import com.itextpdf.basics.source.PdfTokenizer;

/**
 * @author psoares
 */
public interface CMapLocation {
    PdfTokenizer getLocation(String location) throws java.io.IOException;
}
