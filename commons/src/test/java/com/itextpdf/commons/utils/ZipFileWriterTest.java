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

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ZipFileWriterTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/commons/utils/ZipFileWriter/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/commons/utils/ZipFileWriter/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void constructorWithNullPathTest() {
        Exception ex = Assertions.assertThrows(IOException.class, () -> new ZipFileWriter(null));
        Assertions.assertEquals(CommonsExceptionMessageConstant.FILE_NAME_CAN_NOT_BE_NULL, ex.getMessage());
    }

    @Test
    public void constructorWithNotExistingDirsInPathTest() {
        Assertions.assertThrows(IOException.class, () -> new ZipFileWriter(
                DESTINATION_FOLDER + "notExistingDir/archive.zip"));
    }

    @Test
    public void constructorWithAlreadyExistedFilePathTest() throws IOException {
        final String fileName = "constructorWithAlreadyExistedFilePath.zip";
        FileUtil.copy(SOURCE_FOLDER + fileName, DESTINATION_FOLDER + fileName);

        Exception ex = Assertions.assertThrows(IOException.class,
                () -> new ZipFileWriter(DESTINATION_FOLDER + fileName));
        Assertions.assertEquals(
                MessageFormatUtil.format(CommonsExceptionMessageConstant.FILE_NAME_ALREADY_EXIST,
                        DESTINATION_FOLDER + fileName),
                ex.getMessage());
    }

    @Test
    public void constructorWithNotZipFileTest() throws IOException {
        final String fileName = "testFile.txt";
        FileUtil.copy(SOURCE_FOLDER + fileName, DESTINATION_FOLDER + fileName);

        Exception ex = Assertions.assertThrows(IOException.class,
                () -> new ZipFileWriter(DESTINATION_FOLDER + fileName));
        Assertions.assertEquals(
                MessageFormatUtil.format(CommonsExceptionMessageConstant.FILE_NAME_ALREADY_EXIST,
                        DESTINATION_FOLDER + fileName), ex.getMessage());
    }

    @Test
    public void constructorWithDirectoryPathTest() throws IOException {
        final String pathToDirectory = DESTINATION_FOLDER + "constructorWithDirectoryPath/";
        FileUtil.createDirectories(pathToDirectory);

        Exception ex = Assertions.assertThrows(IOException.class, () -> new ZipFileWriter(pathToDirectory));
        Assertions.assertEquals(
                MessageFormatUtil.format(CommonsExceptionMessageConstant.FILE_NAME_ALREADY_EXIST, pathToDirectory),
                ex.getMessage());
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void emptyZipCreationTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "emptyZipCreation.zip";

        ZipFileWriter writer = new ZipFileWriter(pathToFile);
        writer.close();
        Assertions.assertTrue(FileUtil.fileExists(pathToFile));

        // We are not using ZipFileWriter in ZipFileReader tests, so we don't have testing cycles here.
        try (ZipFileReader zip = new ZipFileReader(pathToFile)) {
            Assertions.assertTrue(zip.getFileNames().isEmpty());
        }
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void addNullFileEntryTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addNullFileEntry.zip";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            Exception ex = Assertions.assertThrows(IOException.class,
                    () -> writer.addEntry("fileName.txt", (File) null));
            Assertions.assertEquals(CommonsExceptionMessageConstant.FILE_SHOULD_EXIST, ex.getMessage());
        }
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void addEntryWithNotExistingFileTest() throws IOException {
        try (ZipFileWriter writer = new ZipFileWriter(
                DESTINATION_FOLDER + "addEntryWithNotExistingFile.zip")) {
            Assertions.assertThrows(IOException.class,
                    () -> writer.addEntry("fileName", new File(SOURCE_FOLDER + "invalidPath")));
        }
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void addNullStreamEntryTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addNullStreamEntry.zip";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            Exception ex = Assertions.assertThrows(IOException.class,
                    () -> writer.addEntry("fileName.txt", (InputStream) null));
            Assertions.assertEquals(CommonsExceptionMessageConstant.STREAM_CAN_NOT_BE_NULL, ex.getMessage());
        }
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void addNullJsonEntryTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addNullJsonEntry.zip";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            Exception ex = Assertions.assertThrows(IOException.class,
                    () -> writer.addJsonEntry("fileName.txt", null));
            Assertions.assertEquals(CommonsExceptionMessageConstant.JSON_OBJECT_CAN_NOT_BE_NULL, ex.getMessage());
        }
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void addEntryWhenWriterIsClosedTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addEntryWhenWriterIsClosed.zip";

        ZipFileWriter writer = new ZipFileWriter(pathToFile);
        writer.close();

        Assertions.assertThrows(Exception.class,
                () -> writer.addEntry("firstName", new File(SOURCE_FOLDER + "testFile.txt")));
    }

    @Test
    public void addTextFileEntryTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addTextFileEntry.zip";
        final String textFilePath = SOURCE_FOLDER + "testFile.txt";
        final String fileNameInZip = "text.txt";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            writer.addEntry(fileNameInZip, new File(textFilePath));
        }

        try (ZipFileReader reader = new ZipFileReader(pathToFile);
                InputStream streamFromZip = reader.readFromZip(fileNameInZip);
                InputStream streamWithFile = FileUtil.getInputStreamForFile(textFilePath)) {

            Set<String> fileNames = reader.getFileNames();
            Assertions.assertEquals(1, fileNames.size());
            Assertions.assertTrue(fileNames.contains(fileNameInZip));
            Assertions.assertTrue(compareStreams(streamWithFile, streamFromZip));
        }
    }

    @Test
    public void addInputStreamEntryInSubfolderTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addInputStreamEntryInSubfolder.zip";
        final String textFilePath = SOURCE_FOLDER + "testFile.txt";
        final String fileNameInZip = "subfolder/text.txt";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            writer.addEntry(fileNameInZip, FileUtil.getInputStreamForFile(textFilePath));
        }

        try (ZipFileReader reader = new ZipFileReader(pathToFile);
                InputStream streamFromZip = reader.readFromZip(fileNameInZip);
                InputStream streamWithFile = FileUtil.getInputStreamForFile(textFilePath)) {

            Set<String> fileNames = reader.getFileNames();
            Assertions.assertEquals(1, fileNames.size());
            Assertions.assertTrue(fileNames.contains(fileNameInZip));
            Assertions.assertTrue(compareStreams(streamWithFile, streamFromZip));
        }
    }

    @Test
    public void addJsonEntryTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addJsonEntry.zip";
        final String compareString = "\"©\"";
        final String fileNameInZip = "entry.json";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            writer.addJsonEntry(fileNameInZip, "©");
        }

        try (ZipFileReader reader = new ZipFileReader(pathToFile);
                InputStream streamFromZip = reader.readFromZip(fileNameInZip);
                InputStream compareStream = new ByteArrayInputStream(compareString.getBytes(StandardCharsets.UTF_8))) {

            Set<String> fileNames = reader.getFileNames();
            Assertions.assertEquals(1, fileNames.size());
            Assertions.assertTrue(fileNames.contains(fileNameInZip));
            Assertions.assertTrue(compareStreams(compareStream, streamFromZip));
        }
    }

    @Test
    public void addEntryWithSameFilePathTwiceTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addEntryWithSameFilePathTwice.zip";
        final String fileNameInZip = "entry.json";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            writer.addJsonEntry(fileNameInZip, "©");
            Assertions.assertThrows(IOException.class, () -> writer.addJsonEntry(fileNameInZip, "aaa"));
        }
    }

    @Test
    public void addSeveralEntriesToZipTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addSeveralEntriesToZip.zip";
        final String firstTextFilePath = SOURCE_FOLDER + "testFile.txt";
        final String secondTextFilePath = SOURCE_FOLDER + "someTextFile.txt";
        final String compareString = "\"©\"";

        final String firstFileNameInZip = "firstName.txt";
        final String secondFileNameInZip = "subfolder/secondName.txt";
        final String thirdFileNameInZip = "subfolder/subfolder/thirdName.json";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            writer.addEntry(firstFileNameInZip, new File(firstTextFilePath));
            writer.addEntry(secondFileNameInZip, FileUtil.getInputStreamForFile(secondTextFilePath));
            writer.addJsonEntry(thirdFileNameInZip, "©");
        }

        try (ZipFileReader reader = new ZipFileReader(pathToFile);
                InputStream streamWithFirstFromZip = reader.readFromZip(firstFileNameInZip);
                InputStream streamWithFirstFile = FileUtil.getInputStreamForFile(firstTextFilePath);
                InputStream streamWithSecondFromZip = reader.readFromZip(secondFileNameInZip);
                InputStream streamWithSecondFile = FileUtil.getInputStreamForFile(secondTextFilePath);
                InputStream streamWithJsonFromZip = reader.readFromZip(thirdFileNameInZip);
                InputStream compareStream = new ByteArrayInputStream(compareString.getBytes(StandardCharsets.UTF_8))) {
            Set<String> fileNames = reader.getFileNames();
            Assertions.assertEquals(3, fileNames.size());
            Assertions.assertTrue(fileNames.contains(firstFileNameInZip));
            Assertions.assertTrue(fileNames.contains(secondFileNameInZip));
            Assertions.assertTrue(fileNames.contains(thirdFileNameInZip));

            Assertions.assertTrue(compareStreams(streamWithFirstFile, streamWithFirstFromZip));
            Assertions.assertTrue(compareStreams(streamWithSecondFile, streamWithSecondFromZip));
            Assertions.assertTrue(compareStreams(compareStream, streamWithJsonFromZip));
        }
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6906 fix different behavior of ZipFileWriter\Reader)
    public void addEntryWithNullFileNameTest() throws IOException {
        final String pathToFile = DESTINATION_FOLDER + "addEntryWithNullFileName.zip";
        final String firstTextFilePath = SOURCE_FOLDER + "testFile.txt";

        try (ZipFileWriter writer = new ZipFileWriter(pathToFile)) {
            Exception ex = Assertions.assertThrows(IOException.class,
                    () -> writer.addEntry(null, new File(firstTextFilePath)));
            Assertions.assertEquals(CommonsExceptionMessageConstant.FILE_NAME_SHOULD_BE_UNIQUE, ex.getMessage());
        }
    }

    private static boolean compareStreams(InputStream firstStream, InputStream secondStream) throws IOException {
        if (firstStream == null || secondStream == null) {
            throw new IOException(CommonsExceptionMessageConstant.STREAM_CAN_NOT_BE_NULL);
        }
        final byte[] firstStreamBytes = convertInputStreamToByteArray(firstStream);
        final byte[] secondStreamBytes = convertInputStreamToByteArray(secondStream);

        return Arrays.equals(firstStreamBytes, secondStreamBytes);
    }

    private static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            result.flush();
            return result.toByteArray();
        }
    }
}
