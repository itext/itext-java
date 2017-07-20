package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataBrotli001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-brotli-001";
    }
    @Override
    protected String getTestInfo() {
        return "Font table data is compressed with zlib instead of Brotli.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}