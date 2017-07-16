package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksMetadataPadding002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-metadata-padding-002";
    }
    @Override
    protected String getTestInfo() {
        return "The metadata block is not padded and there is no private data.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}