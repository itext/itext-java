package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksExtraneousData001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-extraneous-data-001";
    }
    @Override
    protected String getTestInfo() {
        return "There are four null bytes between the table directory and the table data.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}