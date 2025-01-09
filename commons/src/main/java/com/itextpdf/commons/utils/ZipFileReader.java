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
import com.itextpdf.commons.logs.CommonsLogMessageConstant;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows reading entries from a zip file.
 */
public class ZipFileReader implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipFileReader.class);

    private final ZipFile zipFile;

    private int thresholdSize = 1_000_000_000;
    private int thresholdEntries = 10000;
    private double thresholdRatio = 10;

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
     *
     * @throws IOException if some I/O exception occurs
     */
    public Set<String> getFileNames() throws IOException {
        final Set<String> fileNames = new HashSet<>();

        final Enumeration<? extends ZipEntry> entries = zipFile.entries();

        int totalSizeArchive = 0;
        int totalEntryArchive = 0;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            boolean zipBombSuspicious = false;
            try (InputStream in = new BufferedInputStream(zipFile.getInputStream(entry))) {
                totalEntryArchive++;
                int nBytes;
                byte[] buffer = new byte[2048];
                int totalSizeEntry = 0;
                while ((nBytes = in.read(buffer)) > 0) {
                    totalSizeEntry += nBytes;
                    totalSizeArchive += nBytes;
                    double compressionRatio = (double) totalSizeEntry / entry.getCompressedSize();
                    if (compressionRatio > thresholdRatio) {
                        zipBombSuspicious = true;
                        break;
                    }
                }
                if (zipBombSuspicious) {
                    LOGGER.warn(MessageFormatUtil.format(CommonsLogMessageConstant.RATIO_IS_HIGHLY_SUSPICIOUS,
                            thresholdRatio));
                    break;
                }
                if (totalSizeArchive > thresholdSize) {
                    LOGGER.warn(MessageFormatUtil.format(CommonsLogMessageConstant.UNCOMPRESSED_DATA_SIZE_IS_TOO_MUCH,
                            thresholdSize));
                    break;
                }
                if (totalEntryArchive > thresholdEntries) {
                    LOGGER.warn(MessageFormatUtil.format(CommonsLogMessageConstant.TOO_MUCH_ENTRIES_IN_ARCHIVE,
                            thresholdEntries));
                    break;
                }
            }
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

    /**
     * Sets the maximum total uncompressed data size to prevent a Zip Bomb Attack. Default value is 1 GB (1000000000).
     *
     * @param thresholdSize the threshold for maximum total size of the uncompressed data
     */
    public void setThresholdSize(int thresholdSize) {
        this.thresholdSize = thresholdSize;
    }

    /**
     * Sets the maximum number of file entries in the archive to prevent a Zip Bomb Attack. Default value is 10000.
     *
     * @param thresholdEntries maximum number of file entries in the archive
     */
    public void setThresholdEntries(int thresholdEntries) {
        this.thresholdEntries = thresholdEntries;
    }

    /**
     * Sets the maximum ratio between compressed and uncompressed data to prevent a Zip Bomb Attack. In general
     * the data compression ratio for most of the legit archives is 1 to 3. Default value is 10.
     *
     * @param thresholdRatio maximum ratio between compressed and uncompressed data
     */
    public void setThresholdRatio(double thresholdRatio) {
        this.thresholdRatio = thresholdRatio;
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
