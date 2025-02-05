/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class FileUtilTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/commons/utils/FileUtilTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void getBufferedOutputStreamTest() throws IOException {
        String filePath = DESTINATION_FOLDER + "bufferedOutput.txt";
        String text = "Hello world!";

        try (OutputStream out = FileUtil.getBufferedOutputStream(filePath)) {
            out.write(text.getBytes(StandardCharsets.UTF_8));
        }

        byte[] resultBytes = Files.readAllBytes(Paths.get(filePath));
        Assertions.assertEquals(text, new String(resultBytes, StandardCharsets.UTF_8));
    }

    @Test
    public void getFileOutputStreamTest() throws IOException {
        String filePath = DESTINATION_FOLDER + "fileOutput.txt";
        File file = new File(filePath);
        String text = "Hello world!";

        try (OutputStream out = FileUtil.getFileOutputStream(file)) {
            out.write(text.getBytes(StandardCharsets.UTF_8));
        }

        byte[] resultBytes = Files.readAllBytes(Paths.get(filePath));
        Assertions.assertEquals(text, new String(resultBytes, StandardCharsets.UTF_8));
    }
}