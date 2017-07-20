package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class BlocksPrivate002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "blocks-private-002";
    }
    @Override
    protected String getTestInfo() {
        return "The private data does not correspond to the end of the WOFF2 file because there are 4 null bytes after it.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}