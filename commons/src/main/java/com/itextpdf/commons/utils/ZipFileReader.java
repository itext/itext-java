/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Allows reading entries from a zip file.
 */
public class ZipFileReader implements Closeable {

    private final ZipFile zipFile;

    /**
     * Creates an instance for zip file reading.
     *
     * @param archivePath the path to the zip file to read
     *
     * @throws IOException if some I/O exception occurs
     */
    public ZipFileReader(String archivePath) throws IOException {
        if (archivePath == null) {
            throw new IOException(CommonsExceptionMessageConstant.FILE_NAME_CAN_NOT_BE_NULL);
        }
        zipFile = new ZipFile(archivePath, StandardCharsets.UTF_8);
    }

    /**
     * Get all file entries paths inside the reading zip file.
     *
     * @return the {@link Set} of all file entries paths
     */
    public Set<String> getFileNames() {
        final Set<String> fileNames = new HashSet<>();

        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                fileNames.add(entry.getName());
            }
        }
        return fileNames;
    }

    /**
     * Read single file from zip.
     *
     * @param fileName the file path inside zip to read
     *
     * @return the {@link InputStream} represents read file content
     *
     * @throws IOException if some I/O exception occurs
     */
    public InputStream readFromZip(String fileName) throws IOException {
        if (fileName == null) {
            throw new IOException(CommonsExceptionMessageConstant.FILE_NAME_CAN_NOT_BE_NULL);
        }
        ZipEntry entry = zipFile.getEntry(fileName);
        if (entry == null || entry.isDirectory()) {
            throw new IOException(MessageFormatUtil.format(
                    CommonsExceptionMessageConstant.ZIP_ENTRY_NOT_FOUND, fileName));
        }
        return zipFile.getInputStream(entry);
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
