package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksMetadataAbsent002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-metadata-absent-002";
    }
    @Override
    protected String getTestInfo() {
        return "The metadata length is set to zero but the offset is set to the end of the file.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}