package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksOrdering003Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-ordering-003";
    }
    @Override
    protected String getTestInfo() {
        return "The metadata block is stored after the private data block.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}