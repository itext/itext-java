package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataLocaSize001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-loca-size-001";
    }
    @Override
    protected String getTestInfo() {
        return "A valid TTF flavoured font where the loca table uses the long format.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}