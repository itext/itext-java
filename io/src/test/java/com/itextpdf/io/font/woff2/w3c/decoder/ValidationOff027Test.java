package com.itextpdf.io.font.woff2.w3c.decoder;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class ValidationOff027Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "validation-off-027";
    }
    @Override
    protected String getTestInfo() {
        return "Valid WOFF file from the fire format tests, the decoded file should run through a font validator to confirm the OFF structure validity.";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}