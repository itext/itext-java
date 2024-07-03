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
package com.itextpdf.commons.utils;

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ZipFileReaderTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/commons/utils/ZipFileReaderTest/";

    @Test
    public void constructorWithNullPathTest() {
        Exception ex = Assertions.assertThrows(IOException.class, () -> new ZipFileReader(null));
        Assertions.assertEquals(CommonsExceptionMessageConstant.FILE_NAME_CAN_NOT_BE_NULL, ex.getMessage());
    }

    @Test
    public void constructorWithInvalidPathTest() {
        Assertions.assertThrows(Exception.class, () -> new ZipFileReader("invalidPath"));
    }

    @Test
    public void constructorWithNonZipPathTest() {
        Assertions.assertThrows(Exception.class, () -> new ZipFileReader(SOURCE_FOLDER + "firstFile.txt"));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void getFileNamesFromEmptyZipTest() throws IOException {
        try (ZipFileReader fileReader = new ZipFileReader(SOURCE_FOLDER + "emptyZip.zip")) {
            Set<String> nameSet = fileReader.getFileNames();

            Assertions.assertTrue(nameSet.isEmpty());
        }
    }

    @Test
    public void getFileNamesFromZipTest() throws IOException {
        try (ZipFileReader fileReader = new ZipFileReader(SOURCE_FOLDER + "archive.zip")) {
            Set<String> nameSet = fileReader.getFileNames();

            Assertions.assertNotNull(nameSet);
            Assertions.assertEquals(6, nameSet.size());
            Assertions.assertTrue(nameSet.contains("firstFile.txt"));
            Assertions.assertTrue(nameSet.contains("secondFile.txt"));
            Assertions.assertTrue(nameSet.contains("subfolder/thirdFile.txt"));
            Assertions.assertTrue(nameSet.contains("subfolder/fourthFile.txt"));
            Assertions.assertTrue(nameSet.contains("subfolder/subsubfolder/fifthFile.txt"));
            Assertions.assertTrue(nameSet.contains("subfolder/subsubfolder/sixthFile.txt"));
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = CommonsLogMessageConstant.UNCOMPRESSED_DATA_SIZE_IS_TOO_MUCH))
    public void getFileNamesFromZipBombBySettingThresholdSizeTest() throws IOException {
        try (ZipFileReader fileReader = new ZipFileReader(SOURCE_FOLDER + "zipBombTest.zip")) {
            fileReader.setThresholdRatio(1000);
            fileReader.setThresholdSize(10000);
            Set<String> nameSet = fileReader.getFileNames();

            Assertions.assertNotNull(nameSet);
            Assertions.assertEquals(0, nameSet.size());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = CommonsLogMessageConstant.RATIO_IS_HIGHLY_SUSPICIOUS))
    public void getFileNamesFromZipBombBySettingThresholdRatioTest() throws IOException {
        try (ZipFileReader fileReader = new ZipFileReader(SOURCE_FOLDER + "zipBombTest.zip")) {
            fileReader.setThresholdRatio(5);
            Set<String> nameSet = fileReader.getFileNames();

            Assertions.assertNotNull(nameSet);
            Assertions.assertEquals(0, nameSet.size());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = CommonsLogMessageConstant.TOO_MUCH_ENTRIES_IN_ARCHIVE))
    public void getFileNamesFromZipBombBySettingThresholdEntriesTest() throws IOException {
        try (ZipFileReader fileReader = new ZipFileReader(SOURCE_FOLDER + "archive.zip")) {
            fileReader.setThresholdEntries(5);
            Set<String> nameSet = fileReader.getFileNames();

            Assertions.assertNotNull(nameSet);
            Assertions.assertTrue(nameSet.size() <= 5);
        }
    }

    @Test
    public void readFromZipWithNullPathTest() throws IOException {
        try (ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "archive.zip")) {

            Exception ex = Assertions.assertThrows(IOException.class, () -> reader.readFromZip(null));
            Assertions.assertEquals(CommonsExceptionMessageConstant.FILE_NAME_CAN_NOT_BE_NULL, ex.getMessage());
        }
    }

    @Test
    public void readFromZipWithNotExistingPathTest() throws IOException {
        final String fileName = "name";

        try (ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "archive.zip")) {
            Exception ex = Assertions.assertThrows(IOException.class, () -> reader.readFromZip(fileName));
            Assertions.assertEquals(MessageFormatUtil
                    .format(CommonsExceptionMessageConstant.ZIP_ENTRY_NOT_FOUND, fileName), ex.getMessage());
        }
    }

    @Test
    public void readFromZipWithInvalidPathTest() throws IOException {
        final String fileName = "thirdFile.txt";

        try (ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "archive.zip")) {
            Exception ex = Assertions.assertThrows(IOException.class, () -> reader.readFromZip(fileName));
            Assertions.assertEquals(MessageFormatUtil
                    .format(CommonsExceptionMessageConstant.ZIP_ENTRY_NOT_FOUND, fileName), ex.getMessage());
        }
    }

    @Test
    public void readFromZipWithPathAtRootTest() throws IOException {
        try (ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "archive.zip");
                InputStream inputStream = reader.readFromZip("firstFile.txt")) {

            Assertions.assertNotNull(inputStream);
            Assertions.assertEquals("1", convertInputStreamToString(inputStream));
        }
    }

    @Test
    public void readFromZipWithFileInSubFolderTest() throws IOException {
        try (ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "archive.zip");
                InputStream inputStream = reader.readFromZip("subfolder/thirdFile.txt")) {

            Assertions.assertNotNull(inputStream);
            Assertions.assertEquals("3", convertInputStreamToString(inputStream));
        }
    }

    @Test
    public void readFromZipWithFileInSubSubFolderPathTest() throws IOException {
        try (ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "archive.zip");
                InputStream inputStream = reader.readFromZip("subfolder/subsubfolder/fifthFile.txt")) {

            Assertions.assertNotNull(inputStream);
            Assertions.assertEquals("5", convertInputStreamToString(inputStream));
        }
    }

    @Test
    public void readFromZipWithClosedReaderTest() throws IOException {
        ZipFileReader reader = new ZipFileReader(SOURCE_FOLDER + "archive.zip");
        reader.close();
        Assertions.assertThrows(IllegalStateException.class,
                () -> reader.readFromZip("subfolder/subsubfolder/fifthFile.txt"));
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            result.flush();
            return EncodingUtil.convertToString(result.toByteArray(), "UTF-8");
        }
    }
}
