package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataHmtxTransform002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-hmtx-transform-002";
    }
    @Override
    protected String getTestInfo() {
        return "Invalid TTF flavored WOFF with transformed hmtx table that has 0 flags (null transform).";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}