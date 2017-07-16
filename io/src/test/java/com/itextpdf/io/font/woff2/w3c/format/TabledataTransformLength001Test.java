package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataTransformLength001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-transform-length-001";
    }
    @Override
    protected String getTestInfo() {
        return "The transformed loca table contains 4 zero bytes and its transformLength is 4.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}