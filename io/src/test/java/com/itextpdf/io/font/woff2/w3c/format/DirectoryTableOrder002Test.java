package com.itextpdf.io.font.woff2.w3c.format;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;
import org.junit.Ignore;

@Ignore("Different from c++ version. See html font-face test for more details")
public class DirectoryTableOrder002Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "directory-table-order-002";
    }
    @Override
    protected String getTestInfo() {
        return "An invalid WOFF2 font with loca before glyf in the table directory";
    }
    @Override
    protected boolean isFontValid() {
        return true;
    }
}