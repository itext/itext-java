/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.util;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.ZipFileReader;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.InputStream;
import java.io.OutputStream;

@Category(IntegrationTest.class)
public class ZlibUtilTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/util/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/io/util/";

    @Before
    public void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void arrayIndexOutOfBoundsDeflateTest() throws Exception {
        // Test file is taken from https://issues.jenkins.io/browse/JENKINS-19473
        // Unzip test file first
        try (ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "jzlib.zip");
             InputStream is = reader.readFromZip("jzlib.fail");
             OutputStream os = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "jzlib.fail")) {
            byte[] buf = new byte[8192];
            int length;
            while ((length = is.read(buf)) != -1) {
                os.write(buf, 0, length);
            }
        }

        // Deflate it
        try (InputStream is = FileUtil.getInputStreamForFile(DESTINATION_FOLDER + "jzlib.fail");
             OutputStream os = FileUtil.getFileOutputStream(DESTINATION_FOLDER + "jzlib.fail.zz");
             // -1 stands for default compression
             DeflaterOutputStream zip = new DeflaterOutputStream(os, -1)) {
            byte[] buf = new byte[8192];
            int length;
            while ((length = is.read(buf)) != -1) {
                zip.write(buf, 0, length);
            }
        }

        Assert.assertTrue(FileUtil.fileExists(DESTINATION_FOLDER + "jzlib.fail.zz"));
        Assert.assertTrue(FileUtil.isFileNotEmpty(DESTINATION_FOLDER + "jzlib.fail.zz"));
    }
}
