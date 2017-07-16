package com.itextpdf.io.font.woff2.w3c;

import com.itextpdf.io.font.woff2.Woff2DecodeTest;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class W3CWoff2DecodeTest extends Woff2DecodeTest{
    private static final String baseSourceFolder = "./src/test/resources/com/itextpdf/io/font/woff2/w3c/";
    private static final String baseDestinationFolder = "./target/test/com/itextpdf/io/font/woff2/w3c/";

    protected abstract String getFontName();

    protected abstract String getTestInfo();

    protected abstract boolean isFontValid();

    @Before
    public void setUp() {
        if (isDebug()) {
            createOrClearDestinationFolder(getDestinationFolder());
        }
    }

    @Test
    public void runTest() throws IOException{
        System.out.print("\n" + getTestInfo() + "\n");
        runTest(getFontName(), getSourceFolder(), getDestinationFolder(), isFontValid());
    }

    private String getDestinationFolder() {
        String localPackage = getLocalPackage();
        return baseDestinationFolder + localPackage + File.separatorChar + getTestClassName() + File.separatorChar;
    }

    private String getSourceFolder() {
        String localPackage = getLocalPackage();
        return baseSourceFolder + localPackage + File.separatorChar;
    }

    private String getTestClassName() {
        return getClass().getSimpleName();
    }

    private String getLocalPackage() {
        String packageName = getClass().getPackage().getName();
        String basePackageName = W3CWoff2DecodeTest.class.getPackage().getName();
        return packageName.substring(basePackageName.length()).replace('.', File.separatorChar);
    }
}
