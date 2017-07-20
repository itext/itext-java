package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksExtraneousData003Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-extraneous-data-003";
    }
    @Override
    protected String getTestInfo() {
        return "There are four null bytes between the table data and the metadata.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}