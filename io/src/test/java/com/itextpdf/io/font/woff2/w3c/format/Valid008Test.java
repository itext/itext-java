package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class Valid008Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "valid-008";
    }
    @Override
    protected String getTestInfo() {
        return "Valid TTF flavored WOFF with metadata and private data";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}