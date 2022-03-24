/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.exceptions.IoExceptionMessage;

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

    @Deprecated
    static final String GHOSTSCRIPT_ENVIRONMENT_VARIABLE_LEGACY = "gsExec";

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

            if (gsExec == null) {
                gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GHOSTSCRIPT_ENVIRONMENT_VARIABLE_LEGACY);
            }
        }

        if (!CliCommandUtil.isVersionCommandExecutable(gsExec, GHOSTSCRIPT_KEYWORD)) {
            throw new IllegalArgumentException(IoExceptionMessage.GS_ENVIRONMENT_VARIABLE_IS_NOT_SPECIFIED);
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
                    IoExceptionMessage.CANNOT_OPEN_OUTPUT_DIRECTORY.replace("<filename>", pdf));
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
                        IoExceptionMessage.GHOSTSCRIPT_FAILED.replace("<filename>", pdf));
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
