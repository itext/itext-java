package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class TabledataDecompressedLength003Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-decompressed-length-003";
    }
    @Override
    protected String getTestInfo() {
        return "The transformed length of the glyf table in the directory is increased by 1, making the decompressed length of the table data less than the sum of transformed table lengths.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}