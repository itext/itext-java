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
package com.itextpdf.io.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class FileUtil {

    private FileUtil() {
    }

    public static String getFontsDir() {
        try {
            String winDir = System.getenv("windir");
            String fileSeparator = System.getProperty("file.separator");
            return winDir + fileSeparator + "fonts";
        } catch (SecurityException e) {
            LoggerFactory.getLogger(FileUtil.class)
                    .warn("Can't access System.getenv(\"windir\") to load fonts. Please, add RuntimePermission for getenv.windir.");
            return null;
        }
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
                    Arrays.sort(files, new CaseSensitiveFileComparator());
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
            Arrays.sort(result, new CaseSensitiveFileComparator());
        }
        return result;
    }

    private static void listAllFiles(String dir, List<String> list) {
        File[] files = new File(dir).listFiles();
        if (files != null) {
            // Guarantee invariant order in all environments
            Arrays.sort(files, new CaseSensitiveFileComparator());
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

    public static InputStream getInputStreamForFile(String path) throws IOException {
        return Files.newInputStream(Paths.get(path));
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

    /**
     * Returns an URL of the parent directory for the resource
     *
     * @param url of resource
     * @return parent directory path| the same path if a catalog`s url is passed;
     */
    public static String parentDirectory(URL url) throws URISyntaxException {
            return url.toURI().resolve(".").toString();
    }

    /**
     * Creates a temporary file.
     *
     * @param tempFilePrefix the prefix of the copied file's name
     * @param tempFilePostfix the postfix of the copied file's name
     *
     * @return the path to the copied file
     */
    public static File createTempFile(String tempFilePrefix, String tempFilePostfix) throws IOException {
        return File.createTempFile(tempFilePrefix, tempFilePostfix);
    }

    /**
     * Creates a temporary copy of a file.
     *
     * @param file the path to the file to be copied
     * @param tempFilePrefix the prefix of the copied file's name
     * @param tempFilePostfix the postfix of the copied file's name
     *
     * @return the path to the copied file
     */
    public static String createTempCopy(String file, String tempFilePrefix, String tempFilePostfix)
            throws IOException {
        Path replacementFilePath = null;
        try {
            replacementFilePath = Files.createTempFile(tempFilePrefix, tempFilePostfix);
            Path pathToPassedFile = Paths.get(file);
            Files.copy(pathToPassedFile, replacementFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            if (null != replacementFilePath) {
                FileUtil.removeFiles(new String[] {replacementFilePath.toString()});
            }
            throw e;
        }
        return replacementFilePath.toString();
    }

    /**
     * Creates a copy of a file.
     *
     * @param inputFile the path to the file to be copied
     * @param outputFile the path, to which the passed file should be copied
     */
    public static void copy(String inputFile, String outputFile)
            throws IOException {
        Files.copy(Paths.get(inputFile), Paths.get(outputFile), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Creates a temporary directory.
     *
     * @param tempFilePrefix the prefix of the temporary directory's name
     * @return the path to the temporary directory
     */
    public static String createTempDirectory(String tempFilePrefix)
            throws IOException {
        return Files.createTempDirectory(tempFilePrefix).toString();
    }

    /**
     * Removes all of the passed files.
     *
     * @param paths paths to files, which should be removed
     *
     * @return true if all the files have been successfully removed, false otherwise
     */
    public static boolean removeFiles(String[] paths) {
        boolean allFilesAreRemoved = true;
        for (String path : paths) {
            try {
                if (null != path) {
                    Files.delete(Paths.get(path));
                }
            } catch (Exception e) {
                allFilesAreRemoved = false;
            }
        }
        return allFilesAreRemoved;
    }

    private static class CaseSensitiveFileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return f1.getPath().compareTo(f2.getPath());
        }
    }
}
