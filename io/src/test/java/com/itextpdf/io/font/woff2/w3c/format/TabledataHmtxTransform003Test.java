package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;
import org.junit.Ignore;

@Ignore("Different in result form expected in w3c suite. See html font-face test for more details")
public class TabledataHmtxTransform003Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "tabledata-hmtx-transform-003";
    }
    @Override
    protected String getTestInfo() {
        return "Invalid TTF flavored WOFF with transformed hmtx table that has all flags bits (including reserved bits) set.";
    }
    @Override
    protected boolean isFontValid() {
        return false;
    }
}