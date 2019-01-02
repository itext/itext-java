/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class FileUtil {

    private FileUtil() {
    }

    public static String getFontsDir() {
        String winDir = System.getenv("windir");
        String fileSeparator = System.getProperty("file.separator");
        return winDir + fileSeparator + "fonts";
    }

    public static boolean fileExists(String path) {
        if (path != null) {
            File f = new File(path);
            return f.exists() && f.isFile();
        }
        return false;
    }

    public static boolean directoryExists(String path) {
        if (path != null) {
            File f = new File(path);
            return f.exists() && f.isDirectory();
        }
        return false;
    }

    public static String[] listFilesInDirectory(String path, boolean recursive) {
        if (path != null) {
            File root = new File(path);
            if (root.exists() && root.isDirectory()) {
                File[] files = root.listFiles();
                if (files != null) {
                    // Guarantee invariant order in all environments
                    Arrays.sort(files);
                    List<String> list = new ArrayList<>();
                    for (File file : files) {
                        if (file.isDirectory() && recursive) {
                            listAllFiles(file.getAbsolutePath(), list);
                        } else {
                            list.add(file.getAbsolutePath());
                        }
                    }
                    return list.toArray(new String[list.size()]);
                }
            }
        }
        return null;
    }

    public static File[] listFilesInDirectoryByFilter(String outPath, FileFilter fileFilter) {
        File[] result = null;
        if (outPath != null && !outPath.isEmpty()) {
            result = new File(outPath).listFiles(fileFilter);
        }
        if (result != null) {
            // Guarantee invariant order in all environments
            Arrays.sort(result);
        }
        return result;
    }

    private static void listAllFiles(String dir, List<String> list) {
        File[] files = new File(dir).listFiles();
        if (files != null) {
            // Guarantee invariant order in all environments
            Arrays.sort(files);
            for (File file : files) {
                if (file.isDirectory()) {
                    listAllFiles(file.getAbsolutePath(), list);
                } else {
                    list.add(file.getAbsolutePath());
                }
            }
        }
    }

    public static PrintWriter createPrintWriter(OutputStream output, String encoding) throws UnsupportedEncodingException {
        return new PrintWriter(new OutputStreamWriter(output, encoding));
    }

    public static java.io.OutputStream getBufferedOutputStream(String filename) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(filename));
    }

    public static java.io.OutputStream wrapWithBufferedOutputStream(OutputStream outputStream) {
        if (outputStream instanceof ByteArrayOutputStream || (outputStream instanceof BufferedOutputStream)) {
            return outputStream;
        } else {
            return new BufferedOutputStream(outputStream);
        }
    }

    public static File createTempFile(String path) throws IOException {
        File tempFile = new File(path);
        if (tempFile.isDirectory()) {
            tempFile = File.createTempFile("pdf", null, tempFile);
        }
        return tempFile;
    }

    public static FileOutputStream getFileOutputStream(File tempFile) throws FileNotFoundException {
        return new FileOutputStream(tempFile);
    }

    public static RandomAccessFile getRandomAccessFile(File tempFile) throws FileNotFoundException {
        return new RandomAccessFile(tempFile, "rw");
    }

    public static void createDirectories(String outPath) {
        new File(outPath).mkdirs();
    }

    @Deprecated
    public static String getParentDirectory(String file) {
        return new File(file).getParent();
    }

    public static String getParentDirectory(File file) throws MalformedURLException {
        return file != null ? Paths.get(file.getParent()).toUri().toURL().toExternalForm() : "";
    }

    /**
     * Deletes a file and returns whether the operation succeeded.
     * Node that only *files* are supported, not directories.
     */
    public static boolean deleteFile(File file) {
        return file.delete();
    }
}
