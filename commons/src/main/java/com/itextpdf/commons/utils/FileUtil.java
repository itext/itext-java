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
import java.nio.file.attribute.FileAttribute;
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

    /**
     * Gets the default windows font directory.
     *
     * @return the default windows font directory
     */
    public static String getFontsDir() {
        try {
            String winDir = System.getenv("windir");
            String fileSeparator = System.getProperty("file.separator");
            return winDir + fileSeparator + "fonts";
        } catch (SecurityException e) {
            LoggerFactory.getLogger(FileUtil.class).warn("Can't access System.getenv(\"windir\") to load fonts. " +
                    "Please, add RuntimePermission for getenv.windir.");
            return null;
        }
    }

    /**
     * Checks whether there is a file at the provided path.
     *
     * @param path the path to the file to be checked on existence
     *
     * @return {@code true} if such a file exists, otherwise {@code false}
     */
    public static boolean fileExists(String path) {
        if (path != null) {
            File f = new File(path);
            return f.exists() && f.isFile();
        }
        return false;
    }

    /**
     * Checks whether is provided file not empty.
     *
     * @param path the path to the file to be checked on emptiness
     *
     * @return {@code true} if such file is not empty, {@code false} otherwise
     */
    public static boolean isFileNotEmpty(String path) {
        if (path != null) {
            File f = new File(path);
            return f.exists() && f.isFile() && f.length() > 0;
        }
        return false;
    }

    /**
     * Checks whether there is a directory at the provided path.
     *
     * @param path the path to the directory to be checked on existence
     *
     * @return {@code true} if such a directory exists, otherwise {@code false}
     */

    public static boolean directoryExists(String path) {
        if (path != null) {
            File f = new File(path);
            return f.exists() && f.isDirectory();
        }
        return false;
    }

    /**
     * Lists all the files located at the provided directory.
     *
     * @param path path to the directory
     * @param recursive if {@code true}, files from all the subdirectories will be returned
     *
     * @return all the files located at the provided directory
     */
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
                    return list.toArray(new String[0]);
                }
            }
        }
        return null;
    }

    /**
     * Lists all the files located at the provided directory, which are accepted by the provided filter.
     *
     * @param outPath  path to the directory
     * @param fileFilter filter to accept files to be listed
     *
     * @return all the files located at the provided directory, which are accepted by the provided filter
     */
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

    /**
     * Creates {@code PrintWriter} instance.
     *
     * @param output output stream where data will be written.
     * @param encoding encoding in which data will be written.
     *
     * @return {@code PrintWriter} instance.
     *
     * @throws UnsupportedEncodingException in case of unknown encoding.
     */
    public static PrintWriter createPrintWriter(OutputStream output,
                                                String encoding) throws UnsupportedEncodingException {
        return new PrintWriter(new OutputStreamWriter(output, encoding));
    }

    /**
     * Creates {@code OutputStream} instance for filename.
     *
     * @param filename name of the file for which output stream will be created.
     *
     * @return {@code OutputStream} instance.
     *
     * @throws FileNotFoundException if file not found.
     */
    public static OutputStream getBufferedOutputStream(String filename) throws IOException {
        return new BufferedOutputStream(getFileOutputStream(filename));
    }

    /**
     * Wraps provided output stream with buffered one.
     *
     * @param outputStream output stream to wrap.
     *
     * @return {@code BufferedOutputStream} instance if provided stream was not buffered before.
     */
    public static java.io.OutputStream wrapWithBufferedOutputStream(OutputStream outputStream) {
        if (outputStream instanceof ByteArrayOutputStream || (outputStream instanceof BufferedOutputStream)) {
            return outputStream;
        } else {
            return new BufferedOutputStream(outputStream);
        }
    }

    /**
     * Create {@code File} instance.
     *
     * @param directory the parent pathname string.
     * @param fileName The child pathname string.
     *
     * @return {@code File} instance.
     */
    public static File constructFileByDirectoryAndName(String directory, String fileName) {
        return new File(directory, fileName);
    }

    /**
     * Creates a temporary file at the provided path.
     *
     * @param path path to the temporary file to be created. If it is a directory, then the temporary file
     *             will be created at this directory
     *
     * @return the created temporary file
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    public static File createTempFile(String path) throws IOException {
        File tempFile = new File(path);
        if (tempFile.isDirectory()) {
            tempFile = File.createTempFile("pdf", null, tempFile);
        }
        return tempFile;
    }

    /**
     * Creates {@code FileOutputStream} instance.
     *
     * @param tempFile filename for which output stream will be created.
     *
     * @return {@code FileOutputStream} instance.
     *
     @throws IOException in file reading errors.
     */
    public static OutputStream getFileOutputStream(File tempFile) throws IOException {
        return Files.newOutputStream(tempFile.toPath());
    }

    /**
     * Creates {@code InputStream} instance.
     *
     * @param path filename for which output stream will be created.
     *
     * @return {@code InputStream} instance.
     *
     * @throws IOException in file reading errors.
     */
    public static InputStream getInputStreamForFile(String path) throws IOException {
        return Files.newInputStream(Paths.get(path));
    }

    /**
     * Creates {@code InputStream} instance.
     *
     * @param file filename for which output stream will be created.
     *
     * @return {@code InputStream} instance.
     *
     * @throws IOException in file reading errors.
     */
    public static InputStream getInputStreamForFile(File file) throws IOException {
        return Files.newInputStream(file.toPath());
    }

    /**
     * Creates {@code OutputStream} instance.
     *
     * @param path filename for which output stream will be created.
     *
     * @return {@code OutputStream} instance.
     *
     * @throws IOException in file r/w errors.
     */
    public static OutputStream getFileOutputStream(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    /**
     * Creates {@code RandomAccessFile} instance.
     *
     * @param tempFile file for which RAF will be created.
     *
     * @return {@code RandomAccessFile} instance.
     *
     * @throws FileNotFoundException in case file not found.
     */
    public static RandomAccessFile getRandomAccessFile(File tempFile) throws FileNotFoundException {
        return new RandomAccessFile(tempFile, "rw");
    }

    /**
     * Creates a directory at the provided path.
     *
     * @param outPath path to the directory to be created
     */
    public static void createDirectories(String outPath) {
        new File(outPath).mkdirs();
    }

    /**
     * Returns an URI of the parent directory for the resource.
     *
     * @param file for which an URI of the parent will be constructed.
     *
     * @return parent directory URI.
     *
     * @throws MalformedURLException If a protocol handler for the URL could not be found,
     *                               or if some other error occurred while constructing the URL.
     */
    public static String getParentDirectoryUri(File file) throws MalformedURLException {
        return file != null ? Paths.get(file.getParent()).toUri().toURL().toExternalForm() : "";
    }

    /**
     * Deletes a file and returns whether the operation succeeded.
     * Note that only *files* are supported, not directories.
     *
     * @param file file to be deleted
     * @return true if file was deleted successfully, false otherwise
     */
    public static boolean deleteFile(File file) {
        return file.delete();
    }

    /**
     * Returns an URL of the parent directory for the resource.
     *
     * @param url of resource
     *
     * @return parent directory path| the same path if a catalog`s url is passed;
     * @throws URISyntaxException if this URL is not formatted strictly according
     *                            to RFC2396 and cannot be converted to a URI.
     */
    public static String parentDirectory(URL url) throws URISyntaxException {
            return url.toURI().resolve(".").toString();
    }

    /**
     * Creates a temporary file.
     * 
     * <p>
     * Note, that this method creates temporary file with provided file's prefix and postfix
     * using {@link File#createTempFile(String, String)}.
     *
     * @param tempFilePrefix  the prefix of the copied file's name
     * @param tempFilePostfix the postfix of the copied file's name
     *
     * @return the path to the copied file
     *
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static File createTempFile(String tempFilePrefix, String tempFilePostfix) throws IOException {
        return File.createTempFile(tempFilePrefix, tempFilePostfix);
    }

    /**
     * Creates a temporary copy of a file.
     * 
     * <p>
     * Note, that this method creates temporary file with provided file's prefix and postfix
     * using {@link Files#createTempFile(String, String, FileAttribute[])}.
     *
     * @param file            the path to the file to be copied
     * @param tempFilePrefix  the prefix of the copied file's name
     * @param tempFilePostfix the postfix of the copied file's name
     *
     * @return the path to the copied file
     *
     * @throws IOException signals that an I/O exception has occurred.
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
     * @param inputFile  the path to the file to be copied
     * @param outputFile the path, to which the passed file should be copied
     *
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static void copy(String inputFile, String outputFile)
            throws IOException {
        Files.copy(Paths.get(inputFile), Paths.get(outputFile), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Creates a temporary directory.
     * 
     * <p>
     * Note, that this method creates temporary directory with provided directory prefix
     * using {@link Files#createTempDirectory(String, FileAttribute[])}.
     *
     * @param tempFilePrefix the prefix of the temporary directory's name
     *
     * @return the path to the temporary directory
     *
     * @throws IOException signals that an I/O exception has occurred.
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
     * @return {@code true} if all the files have been successfully removed, {@code false} otherwise
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
