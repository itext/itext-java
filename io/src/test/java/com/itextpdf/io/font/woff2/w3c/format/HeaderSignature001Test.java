package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;

public class HeaderSignature001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "header-signature-001";
    }
    @Override
    protected String getTestInfo() {
        return "The signature field contains XXXX instead of wOFF.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}