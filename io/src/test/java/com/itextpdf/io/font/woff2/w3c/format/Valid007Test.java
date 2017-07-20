package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class Valid007Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "valid-007";
    }
    @Override
    protected String getTestInfo() {
        return "Valid TTF flavored WOFF with private data";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}