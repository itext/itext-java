package com.itextpdf.io.font.woff2.w3c.decoder;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class ValidationChecksum002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "validation-checksum-002";
    }
    @Override
    protected String getTestInfo() {
        return "Valid CFF flavored WOFF file, the output file is put through an OFF validator to check the validity of head table checkSumAdjustment.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}