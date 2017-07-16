package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataTransformLength002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-transform-length-002";
    }
    @Override
    protected String getTestInfo() {
        return "The transformed tables does not have transformLength set.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}