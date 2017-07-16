package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class Valid005Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "valid-005";
    }
    @Override
    protected String getTestInfo() {
        return "Valid TTF flavored WOFF with no metadata and no private data";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}