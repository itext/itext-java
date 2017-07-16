package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksMetadataPadding003Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-metadata-padding-003";
    }
    @Override
    protected String getTestInfo() {
        return "The metadata block is padded to a four-byte boundary and there is private data.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}