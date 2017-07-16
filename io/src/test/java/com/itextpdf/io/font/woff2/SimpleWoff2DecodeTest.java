package com.itextpdf.io.font.woff2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class SimpleWoff2DecodeTest extends Woff2DecodeTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/io/font/woff2/SimpleWoff2Decode/";
    private static final String targetFolder = "./target/test/com/itextpdf/io/font/woff2/SimpleWoff2Decode/";

    @BeforeClass
    public static void setUp() {
        if (DEBUG) {
            createOrClearDestinationFolder(targetFolder);
        }
    }

    @Test
    public void simpleTTFTest() throws IOException {
        runTest("NotoSansCJKtc-Regular");
    }

    @Test
    public void bigTTCTest() throws IOException {
        runTest("NotoSansCJK-Regular");
    }

    private void runTest(String fontName) throws IOException {
        runTest(fontName, sourceFolder, targetFolder, true);
    }
}
