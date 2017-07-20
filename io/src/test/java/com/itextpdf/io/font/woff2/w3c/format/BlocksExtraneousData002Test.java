package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksExtraneousData002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-extraneous-data-002";
    }
    @Override
    protected String getTestInfo() {
        return "There are four null bytes after the table data block and there is no metadata or private data.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}