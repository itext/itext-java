package com.itextpdf.io.font.woff2.w3c.decoder;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class ValidationLocaFormat001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "validation-loca-format-001";
    }
    @Override
    protected String getTestInfo() {
        return "Valid TTF flavored WOFF with simple composite glyphs where the loca table uses the short format, to check loca reconstruction";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}