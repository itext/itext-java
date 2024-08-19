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
package com.itextpdf.io.util;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * A utility class that is used as an interface to run 3rd-party tool Ghostscript.
 * Ghostscript is an interpreter for the PostScript language and PDF files, it allows to render them
 * as images.
 *
 * <p>
 * The Ghostscript needs to be installed independently on the system. This class provides a convenient
 * way to run it by passing a terminal command. The command can either be specified explicitly or by a mean
 * of environment variable {@link #GHOSTSCRIPT_ENVIRONMENT_VARIABLE}.
 */
public class GhostscriptHelper {
    /**
     * The name of the environment variable with the command to execute Ghostscript operations.
     */
    public static final String GHOSTSCRIPT_ENVIRONMENT_VARIABLE = "ITEXT_GS_EXEC";

    static final String GHOSTSCRIPT_KEYWORD = "GPL Ghostscript";
    private static final String TEMP_FILE_PREFIX = "itext_gs_io_temp";

    private static final String RENDERED_IMAGE_EXTENSION = "png";
    private static final String GHOSTSCRIPT_PARAMS = " -dSAFER -dNOPAUSE -dBATCH -sDEVICE="
            + RENDERED_IMAGE_EXTENSION + "16m -r150 {0} -sOutputFile=\"{1}\" \"{2}\"";
    private static final String PAGE_NUMBER_PATTERN = "%03d";

    private static final Pattern PAGE_LIST_REGEX = Pattern.compile("^(\\d+,)*\\d+$");

    private String gsExec;

    /**
     * Creates new instance that will rely on Ghostscript execution command defined by {@link
     * #GHOSTSCRIPT_ENVIRONMENT_VARIABLE} environment variable.
     */
    public GhostscriptHelper() {
        this(null);
    }

    /**
     * Creates new instance that will rely on Ghostscript execution command defined as passed argument.
     *
     * @param newGsExec the Ghostscript execution command; if null - environment variables will be used instead
     */
    public GhostscriptHelper(String newGsExec) {
        gsExec = newGsExec;
        if (gsExec == null) {
            gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GHOSTSCRIPT_ENVIRONMENT_VARIABLE);
        }

        if (!CliCommandUtil.isVersionCommandExecutable(gsExec, GHOSTSCRIPT_KEYWORD)) {
            throw new IllegalArgumentException(IoExceptionMessageConstant.GS_ENVIRONMENT_VARIABLE_IS_NOT_SPECIFIED);
        }
    }

    /**
     * Returns a command that is used to run the utility.
     * This command doesn't contain command parameters. Parameters are added on specific
     * methods invocation.
     *
     * @return a string command
     */
    public String getCliExecutionCommand() {
        return gsExec;
    }

    /**
     * Runs Ghostscript to render the PDF's pages as PNG images.
     * 
     * <p>
     * Note, that this method  may create temporary directory and files.
     *
     * @param pdf    Path to the PDF file to be rendered
     * @param outDir Path to the output directory, in which the rendered pages will be stored
     * @param image  String which defines the name of the resultant images. This string will be
     *               concatenated with the number of the rendered page from the start of the
     *               PDF in "-%03d" format, e.g. "-011" for the eleventh rendered page and so on.
     *               This number may not correspond to the actual page number: for example,
     *               if the passed pageList equals to "5,3", then images with postfixes "-001.png"
     *               and "-002.png" will be created: the former for the third page, the latter
     *               for the fifth page. "%" sign in the passed name is prohibited.
     *
     * @throws IOException          if there are file's reading/writing issues
     * @throws InterruptedException if there is thread interruption while executing GhostScript.
     */
    public void runGhostScriptImageGeneration(String pdf, String outDir, String image)
            throws IOException, InterruptedException {
        runGhostScriptImageGeneration(pdf, outDir, image, null);
    }

    /**
     * Runs Ghostscript to render the PDF's pages as PNG images.
     * 
     * <p>
     * Note, that this method  may create temporary directory and files.
     *
     * @param pdf    Path to the PDF file to be rendered
     * @param outDir Path to the output directory, in which the rendered pages will be stored
     * @param image  String which defines the name of the resultant images. This string will be
     *               concatenated with the number of the rendered page from the start of the
     *               PDF in "-%03d" format, e.g. "-011" for the eleventh rendered page and so on.
     *               This number may not correspond to the actual page number: for example,
     *               if the passed pageList equals to "5,3", then images with postfixes "-001.png"
     *               and "-002.png" will be created: the former for the third page, the latter
     *               for the fifth page. "%" sign in the passed name is prohibited.
     * @param pageList String with numbers of the required pages to be rendered as images.
     *                 This string should be formatted as a string with numbers, separated by commas,
     *                 without whitespaces. Can be null, if it is required to render all the PDF's pages.
     *
     * @throws IOException          if there are file's reading/writing issues
     * @throws InterruptedException if there is thread interruption while executing GhostScript.
     */
    public void runGhostScriptImageGeneration(String pdf, String outDir, String image, String pageList)
            throws IOException, InterruptedException {
        if (!FileUtil.directoryExists(outDir)) {
            throw new IllegalArgumentException(
                    IoExceptionMessageConstant.CANNOT_OPEN_OUTPUT_DIRECTORY.replace("<filename>", pdf));
        }
        if (!validateImageFilePattern(image)) {
            throw new IllegalArgumentException("Invalid output image pattern: " + image);
        }
        if (!validatePageList(pageList)) {
            throw new IllegalArgumentException("Invalid page list: " + pageList);
        }
        String formattedPageList = (pageList == null) ? "" : "-sPageList=<pagelist>".replace("<pagelist>", pageList);

        String replacementPdf = null;
        String replacementImagesDirectory = null;
        String[] temporaryOutputImages = null;
        try {
            replacementPdf = FileUtil.createTempCopy(pdf, TEMP_FILE_PREFIX, null);
            replacementImagesDirectory = FileUtil.createTempDirectory(TEMP_FILE_PREFIX);
            String currGsParams = MessageFormatUtil.format(GHOSTSCRIPT_PARAMS, formattedPageList,
                    Paths.get(replacementImagesDirectory,
                            TEMP_FILE_PREFIX + PAGE_NUMBER_PATTERN + "." + RENDERED_IMAGE_EXTENSION).toString(),
                    replacementPdf);

            if (!SystemUtil.runProcessAndWait(gsExec, currGsParams)) {
                temporaryOutputImages = FileUtil
                        .listFilesInDirectory(replacementImagesDirectory, false);
                throw new GhostscriptExecutionException(
                        IoExceptionMessageConstant.GHOSTSCRIPT_FAILED.replace("<filename>", pdf));
            }

            temporaryOutputImages = FileUtil
                    .listFilesInDirectory(replacementImagesDirectory, false);
            if (null != temporaryOutputImages) {
                for (int i = 0; i < temporaryOutputImages.length; i++) {
                    FileUtil.copy(temporaryOutputImages[i],
                            Paths.get(
                                    outDir,
                                    image + "-" + formatImageNumber(i + 1) + "." + RENDERED_IMAGE_EXTENSION
                            ).toString());
                }
            }
        } finally {
            if (null != temporaryOutputImages) {
                FileUtil.removeFiles(temporaryOutputImages);
            }
            FileUtil.removeFiles(new String[] {replacementImagesDirectory, replacementPdf});
        }
    }

    /**
     * Exceptions thrown when errors occur during generation and comparison of images obtained on the basis of pdf
     * files.
     */
    public static class GhostscriptExecutionException extends RuntimeException {
        /**
         * Creates a new {@link GhostscriptExecutionException}.
         *
         * @param msg the detail message.
         */
        public GhostscriptExecutionException(String msg) {
            super(msg);
        }
    }

    static boolean validatePageList(String pageList) {
        return null == pageList
                || PAGE_LIST_REGEX.matcher(pageList).matches();
    }

    static boolean validateImageFilePattern(String imageFilePattern) {
        return null != imageFilePattern
                && !imageFilePattern.trim().isEmpty()
                && !imageFilePattern.contains("%");
    }

    static String formatImageNumber(int pageNumber) {
        StringBuilder stringBuilder = new StringBuilder();
        int zeroFiller = pageNumber;
        while (0 == zeroFiller / 100) {
            stringBuilder.append('0');
            zeroFiller *= 10;
        }
        stringBuilder.append(pageNumber);
        return stringBuilder.toString();
    }
}
