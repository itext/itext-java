package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;
import org.junit.Ignore;

@Ignore("Different in result form expected in w3c suite. See html font-face test for more details")
public class HeaderFlavor002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "header-flavor-002";
    }
    @Override
    protected String getTestInfo() {
        return "The header flavor is set to OTTO but the table data contains TTF data, not CFF data.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}