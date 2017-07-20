package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksPrivate001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-private-001";
    }
    @Override
    protected String getTestInfo() {
        return "The private data does not begin on a four byte boundary because the metadata is not padded.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}