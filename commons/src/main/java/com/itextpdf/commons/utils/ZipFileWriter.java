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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Allows writing entries into a zip file.
 */
public class ZipFileWriter implements Closeable {

    private final ZipOutputStream outputStream;

    /**
     * Creates an instance for zip file writing.
     *
     * @param archivePath the path to the zip file to write
     *
     * @throws IOException if some I/O exception occurs
     */
    public ZipFileWriter(String archivePath) throws IOException {
        if (archivePath == null) {
            throw new IOException(CommonsExceptionMessageConstant.FILE_NAME_CAN_NOT_BE_NULL);
        }
        if (FileUtil.isFileNotEmpty(archivePath) || FileUtil.directoryExists(archivePath)) {
            throw new IOException(
                    MessageFormatUtil.format(CommonsExceptionMessageConstant.FILE_NAME_ALREADY_EXIST, archivePath));
        }
        outputStream = new ZipOutputStream(FileUtil.getFileOutputStream(archivePath), StandardCharsets.UTF_8);
        outputStream.setMethod(ZipOutputStream.DEFLATED);
        outputStream.setLevel(9);
    }

    /**
     * Add file from disk into zip archive.
     *
     * @param fileName the target name of the file inside zip after writing
     * @param file the path to the file on disk to archive
     *
     * @throws IOException if some I/O exception occurs
     */
    public void addEntry(String fileName, File file) throws IOException {
        if (file == null) {
            throw new IOException(CommonsExceptionMessageConstant.FILE_SHOULD_EXIST);
        }
        addEntry(fileName, Files.newInputStream(file.toPath()));
    }

    /**
     * Add file into zip archive with data from stream.
     *
     * @param fileName the target name of the file inside zip after writing
     * @param inputStream the input stream to archive
     *
     * @throws IOException if some I/O exception occurs
     */
    public void addEntry(String fileName, InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException(CommonsExceptionMessageConstant.STREAM_CAN_NOT_BE_NULL);
        }
        addEntryToZip(fileName, zos -> {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                zos.write(bytes, 0, length);
            }
        });
    }

    /**
     * Add file into zip archive with object serialized as JSON.
     *
     * @param fileName the target name of the file inside zip after writing
     * @param objectToAdd the object to serialize as JSON
     *
     * @throws IOException if some I/O exception occurs
     */
    public void addJsonEntry(String fileName, Object objectToAdd) throws IOException {
        if (objectToAdd == null) {
            throw new IOException(CommonsExceptionMessageConstant.JSON_OBJECT_CAN_NOT_BE_NULL);
        }
        addEntryToZip(fileName, zos -> {
            JsonUtil.serializeToStream(zos, objectToAdd);
        });
    }


    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    private void addEntryToZip(String fileName, ZipWriter writer) throws IOException {
        if (fileName == null) {
            throw new IOException(CommonsExceptionMessageConstant.FILE_NAME_SHOULD_BE_UNIQUE);
        }
        ZipEntry zipEntry = new ZipEntry(fileName);
        outputStream.putNextEntry(zipEntry);
        writer.write(outputStream);
    }

    @FunctionalInterface
    private interface ZipWriter {
        void write(ZipOutputStream outputStream) throws IOException;
    }
}
