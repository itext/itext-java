package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;
import org.junit.Ignore;

@Ignore("Different in result form expected in w3c suite. See html font-face test for more details")
public class HeaderReserved001Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "header-reserved-001";
    }
    @Override
    protected String getTestInfo() {
        return "The reserved field contains 1.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}