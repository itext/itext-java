package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class HeaderNumTables001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "header-numTables-001";
    }
    @Override
    protected String getTestInfo() {
        return "The header contains 0 in the numTables field. A table directory and table data are present.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}