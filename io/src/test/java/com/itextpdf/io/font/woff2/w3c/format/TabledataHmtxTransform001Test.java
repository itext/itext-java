package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataHmtxTransform001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-hmtx-transform-001";
    }
    @Override
    protected String getTestInfo() {
        return "Valid TTF flavored WOFF with transformed hmtx table.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}