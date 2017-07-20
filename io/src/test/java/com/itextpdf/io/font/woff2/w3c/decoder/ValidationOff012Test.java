package com.itextpdf.io.font.woff2.w3c.decoder;

import com.itextpdf.io.font.woff2.w3c.W3CWoff2DecodeTest;
import org.junit.Ignore;

@Ignore("Different from c++ version. See html font-face test for more details")
public class ValidationOff012Test extends W3CWoff2DecodeTest {
    @Override
    protected String getFontName() {
        return "validation-off-012";
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