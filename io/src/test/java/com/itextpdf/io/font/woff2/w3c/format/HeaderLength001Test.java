package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class HeaderLength001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "header-length-001";
    }
    @Override
    protected String getTestInfo() {
        return "The length field contains a value that is four bytes shorter than the actual data.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}