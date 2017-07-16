package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataTransformGlyfLoca002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-transform-glyf-loca-002";
    }
    @Override
    protected String getTestInfo() {
        return "The glyf table is not transformed while loca table is transformed.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}