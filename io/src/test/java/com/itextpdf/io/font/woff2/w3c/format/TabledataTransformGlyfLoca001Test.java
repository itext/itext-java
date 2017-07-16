package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataTransformGlyfLoca001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-transform-glyf-loca-001";
    }
    @Override
    protected String getTestInfo() {
        return "The glyf table is transformed while loca table is not.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}