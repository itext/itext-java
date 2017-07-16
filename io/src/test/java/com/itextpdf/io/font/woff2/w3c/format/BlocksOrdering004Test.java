package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksOrdering004Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-ordering-004";
    }
    @Override
    protected String getTestInfo() {
        return "The private data block is stored before the metadata block.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}