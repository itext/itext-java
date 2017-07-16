package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataExtraneousData001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-extraneous-data-001";
    }
    @Override
    protected String getTestInfo() {
        return "There is extraneous data before the last table.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}