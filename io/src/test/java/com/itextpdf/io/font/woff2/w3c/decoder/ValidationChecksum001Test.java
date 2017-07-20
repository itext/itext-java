package com.itextpdf.io.font.woff2.w3c.decoder;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class ValidationChecksum001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "validation-checksum-001";
    }
    @Override
    protected String getTestInfo() {
        return "Valid CFF flavored WOFF file, the output file is put through an OFF validator to check the validity of table checksums.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}