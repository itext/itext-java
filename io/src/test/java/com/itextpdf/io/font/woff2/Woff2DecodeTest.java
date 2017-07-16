package com.itextpdf.io.font.woff2;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.experimental.categories.Category;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Category(UnitTest.class)
public abstract class Woff2DecodeTest extends ExtendedITextTest {
    protected static boolean DEBUG = true;

    protected boolean isDebug() {
        return DEBUG;
    }

    protected final void runTest(String fileName, String sourceFolder, String targetFolder, boolean isFontValid) throws IOException {
        final String inFile = fileName + ".woff2";
        final String outFile = fileName + ".ttf";
        final String cmpFile = "cmp_" + fileName + ".ttf";
        byte[] in = null;
        byte[] out = null;
        byte[] cmp = null;
        try {
            in = readFile(sourceFolder + inFile);
            if (isFontValid) {
                Assert.assertTrue(Woff2Converter.isWoff2Font(in));
            }
            out = Woff2Converter.convert(in);
            cmp = readFile(sourceFolder + cmpFile);
            Assert.assertTrue("Only valid fonts should reach this", isFontValid);
            Assert.assertArrayEquals(cmp, out);
        } catch (FontCompressionException e) {
            if (isFontValid) {
                throw e;
            }
        } finally {
            if (isDebug()) {
                saveFile(in, targetFolder + inFile);
                saveFile(out, targetFolder + outFile);
                saveFile(cmp, targetFolder + cmpFile);
            }
        }
    }

    protected final void saveFile(byte[] content, String fileName) throws IOException {
        if (content != null) {
            OutputStream os = new FileOutputStream(fileName);
            os.write(content);
            os.close();
        }
    }

}
