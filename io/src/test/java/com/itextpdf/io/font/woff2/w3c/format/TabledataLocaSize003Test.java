package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataLocaSize003Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-loca-size-003";
    }
    @Override
    protected String getTestInfo() {
        return "A valid CFF flavoured font which naturally have no loca table.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}