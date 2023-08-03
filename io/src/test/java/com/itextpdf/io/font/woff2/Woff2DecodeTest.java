/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.font.woff2;

import com.itextpdf.io.exceptions.FontCompressionException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.experimental.categories.Category;

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
