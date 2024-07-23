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
package com.itextpdf.kernel.utils;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PdfWriter implementation which allows to create documents in memory and dump them on disk on purpose.
 * Currently it's private and used in automated tests only.
 */
class MemoryFirstPdfWriter extends PdfWriter {
    private static final int MAX_ALLOWED_STREAMS = 100;

    private static Map<String, MemoryFirstPdfWriter> waitingStreams = new ConcurrentHashMap<>();

    private String filePath;
    private ByteArrayOutputStream outStream;

    MemoryFirstPdfWriter(String filename, WriterProperties properties) throws FileNotFoundException {
        this(filename, MemoryFirstPdfWriter.createBAOutputStream(), properties);
    }

    private MemoryFirstPdfWriter(String filename, ByteArrayOutputStream outputStream, WriterProperties properties) {
        super(outputStream, properties);
        setCloseStream(false);

        filePath = filename;
        outStream = outputStream;
        if (MemoryFirstPdfWriter.waitingStreams.size() >= MAX_ALLOWED_STREAMS) {
            throw new RuntimeException("Too many PdfWriter's have been created. Verify that you call"
                    + " CompareTool.cleanup where necessary");
        }

        MemoryFirstPdfWriter.waitingStreams.put(filename, this);
    }

    static MemoryFirstPdfWriter get(String filename) {
        return MemoryFirstPdfWriter.waitingStreams.get(filename);
    }

    static void cleanup(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Provided path is null");
        }

        for (String filePath : MemoryFirstPdfWriter.waitingStreams.keySet()) {
            if (filePath.startsWith(path)) {
                MemoryFirstPdfWriter.waitingStreams.remove(filePath);
            }
        }
    }

    void dump() throws IOException {
        OutputStream fos = FileUtil.getFileOutputStream(filePath);
        outStream.writeTo(fos);
        fos.close();
    }

    ByteArrayOutputStream getBAOutputStream() {
        return outStream;
    }

    private static ByteArrayOutputStream createBAOutputStream() {
        return new ByteArrayOutputStream();
    }
}
