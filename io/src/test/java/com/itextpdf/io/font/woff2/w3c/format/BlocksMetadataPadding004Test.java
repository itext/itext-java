package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksMetadataPadding004Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-metadata-padding-004";
    }
    @Override
    protected String getTestInfo() {
        return "The beginning of the metadata block is not padded.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}