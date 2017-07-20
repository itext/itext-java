package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataDecompressedLength002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-decompressed-length-002";
    }
    @Override
    protected String getTestInfo() {
        return "The original length of the first table in the directory is decreased by 1, making the decompressed length of the table data greater than the sum of original table lengths.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}