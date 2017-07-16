package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class DirectoryTableOrder001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "directory-table-order-001";
    }
    @Override
    protected String getTestInfo() {
        return "A valid WOFF2 font with tables ordered correctly in the table directory";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}