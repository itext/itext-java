package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class Valid001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "valid-001";
    }
    @Override
    protected String getTestInfo() {
        return "Valid CFF flavored WOFF with no metadata and no private data";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}