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
import com.itextpdf.commons.utils.ProcessInfo;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class that is used as an interface to run 3rd-party tool ImageMagick.
 * ImageMagick among other things allows to compare images and this class provides means to utilize this feature.
 *
 * <p>
 * The ImageMagick needs to be installed independently on the system. This class provides a convenient
 * way to run it by passing a terminal command. The command can either be specified explicitly or by a mean
 * of environment variable {@link #MAGICK_COMPARE_ENVIRONMENT_VARIABLE}.
 */
public class ImageMagickHelper {
    /**
     * The name of the environment variable with the command to execute ImageMagic comparison operations.
     */
    public static final String MAGICK_COMPARE_ENVIRONMENT_VARIABLE = "ITEXT_MAGICK_COMPARE_EXEC";

    static final String MAGICK_COMPARE_KEYWORD = "ImageMagick Studio LLC";

    private static final String TEMP_FILE_PREFIX = "itext_im_io_temp";
    private static final String DIFF_PIXELS_OUTPUT_REGEXP = "^\\d+\\.*\\d*(e\\+\\d+)?";

    private static final Pattern pattern = Pattern.compile(DIFF_PIXELS_OUTPUT_REGEXP);

    private String compareExec;

    /**
     * Creates new instance that will rely on ImageMagick execution command defined by {@link
     * #MAGICK_COMPARE_ENVIRONMENT_VARIABLE} environment variable.
     */
    public ImageMagickHelper() {
        this(null);
    }

    /**
     * Creates new instance that will rely on ImageMagick execution command defined as passed argument.
     *
     * @param newCompareExec the ImageMagick execution command; if null - environment variables will be used instead
     */
    public ImageMagickHelper(String newCompareExec) {
        compareExec = newCompareExec;
        if (compareExec == null) {
            compareExec = SystemUtil.getPropertyOrEnvironmentVariable(MAGICK_COMPARE_ENVIRONMENT_VARIABLE);
        }

        if (!CliCommandUtil.isVersionCommandExecutable(compareExec, MAGICK_COMPARE_KEYWORD)) {
            throw new IllegalArgumentException(IoExceptionMessageConstant.COMPARE_COMMAND_SPECIFIED_INCORRECTLY);
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
        return compareExec;
    }

    /**
     * Runs imageMagick to visually compare images and generate difference output.
     * 
     * <p>
     * Note, that this method may create temporary files.
     *
     * @param outImageFilePath Path to the output image file
     * @param cmpImageFilePath Path to the cmp image file
     * @param diffImageName    Path to the difference output image file
     * @return boolean result of comparing: true - images are visually equal
     * @throws IOException          if there are file's reading/writing issues
     * @throws InterruptedException if there is thread interruption while executing ImageMagick.
     */
    public boolean runImageMagickImageCompare(String outImageFilePath, String cmpImageFilePath, String diffImageName)
            throws IOException, InterruptedException {
        return runImageMagickImageCompare(outImageFilePath, cmpImageFilePath, diffImageName, null);
    }

    /**
     * Runs imageMagick to visually compare images with the specified fuzziness value and generate difference output.
     * 
     * <p>
     * Note, that this method may create temporary files.
     *
     * @param outImageFilePath Path to the output image file
     * @param cmpImageFilePath Path to the cmp image file
     * @param diffImageName    Path to the difference output image file
     * @param fuzzValue        String fuzziness value to compare images. Should be formatted as string with integer
     *                         or decimal number. Can be null, if it is not required to use fuzziness
     * @return boolean result of comparing: true - images are visually equal
     * @throws IOException          if there are file's reading/writing issues
     * @throws InterruptedException if there is thread interruption while executing ImageMagick.
     */
    public boolean runImageMagickImageCompare(String outImageFilePath, String cmpImageFilePath,
            String diffImageName, String fuzzValue) throws IOException, InterruptedException {
        ImageMagickCompareResult compareResult = runImageMagickImageCompareAndGetResult(outImageFilePath,
                cmpImageFilePath, diffImageName, fuzzValue);

        return compareResult.isComparingResultSuccessful();
    }

    /**
     * Runs imageMagick to visually compare images with the specified fuzziness value and given threshold
     * and generate difference output.
     * 
     * <p>
     * Note, that this method may create temporary files.
     *
     * @param outImageFilePath Path to the output image file
     * @param cmpImageFilePath Path to the cmp image file
     * @param diffImageName    Path to the difference output image file
     * @param fuzzValue        String fuzziness value to compare images. Should be formatted as string with integer
     *                         or decimal number. Can be null, if it is not required to use fuzziness
     * @param threshold        Long value of accepted threshold.
     *
     * @return boolean result of comparing: true - images are visually equal
     *
     * @throws IOException          if there are file's reading/writing issues
     * @throws InterruptedException if there is thread interruption while executing ImageMagick.
     */
    public boolean runImageMagickImageCompareWithThreshold(String outImageFilePath, String cmpImageFilePath,
            String diffImageName, String fuzzValue, long threshold) throws IOException, InterruptedException {
        ImageMagickCompareResult compareResult = runImageMagickImageCompareAndGetResult(outImageFilePath,
                cmpImageFilePath, diffImageName, fuzzValue);

        if (compareResult.isComparingResultSuccessful()) {
            return true;
        } else {
            return compareResult.getDiffPixels() <= threshold;
        }
    }

    /**
     * Runs imageMagick to visually compare images with the specified fuzziness value and generate difference output.
     * This method returns an object of {@link ImageMagickCompareResult}, containing comparing result information,
     * such as boolean result value and the number of different pixels.
     * 
     * <p>
     * Note, that this method may create temporary files.
     *
     * @param outImageFilePath Path to the output image file
     * @param cmpImageFilePath Path to the cmp image file
     * @param diffImageName    Path to the difference output image file
     * @param fuzzValue        String fuzziness value to compare images. Should be formatted as string with integer
     *                         or decimal number. Can be null, if it is not required to use fuzziness
     *
     * @return an object of {@link ImageMagickCompareResult}. containing comparing result information.
     *
     * @throws IOException          if there are file's reading/writing issues
     * @throws InterruptedException if there is thread interruption while executing ImageMagick.
     */
    public ImageMagickCompareResult runImageMagickImageCompareAndGetResult(String outImageFilePath,
            String cmpImageFilePath, String diffImageName, String fuzzValue) throws IOException, InterruptedException {
        if (!validateFuzziness(fuzzValue)) {
            throw new IllegalArgumentException("Invalid fuzziness value: " + fuzzValue);
        }
        fuzzValue = (fuzzValue == null) ? "" : " -metric AE -fuzz <fuzzValue>%".replace("<fuzzValue>", fuzzValue);

        String replacementOutFile = null;
        String replacementCmpFile = null;
        String replacementDiff = null;
        try {
            replacementOutFile = FileUtil.createTempCopy(outImageFilePath, TEMP_FILE_PREFIX, null);
            replacementCmpFile = FileUtil.createTempCopy(cmpImageFilePath, TEMP_FILE_PREFIX, null);

            // ImageMagick generates difference images in .png format, therefore we can specify it.
            // For some reason .webp comparison fails if the extension of diff image is not specified.
            replacementDiff = FileUtil.createTempFile(TEMP_FILE_PREFIX, ".png").getAbsolutePath();
            String currCompareParams = fuzzValue + " '"
                    + replacementOutFile + "' '"
                    + replacementCmpFile + "' '"
                    + replacementDiff + "'";
            ProcessInfo processInfo = SystemUtil.runProcessAndGetProcessInfo(compareExec, currCompareParams);
            boolean comparingResult = processInfo.getExitCode() == 0;
            long diffPixels = parseImageMagickProcessOutput(processInfo.getProcessErrOutput());
            ImageMagickCompareResult resultInfo = new ImageMagickCompareResult(comparingResult, diffPixels);

            if (FileUtil.fileExists(replacementDiff)) {
                FileUtil.copy(replacementDiff, diffImageName);
            }
            return resultInfo;
        } finally {
            FileUtil.removeFiles(new String[] {replacementOutFile, replacementCmpFile, replacementDiff});
        }
    }

    static boolean validateFuzziness(String fuzziness) {
        if (null == fuzziness) {
            return true;
        } else {
            try {
                return Double.parseDouble(fuzziness) >= 0;
            } catch (NumberFormatException e) {
                // In case of an exception the string could not be parsed to double,
                // therefore it is considered to be invalid.
                return false;
            }
        }
    }

    private static long parseImageMagickProcessOutput(String processOutput) throws IOException {
        if (null == processOutput) {
            throw new IllegalArgumentException(IoExceptionMessageConstant.IMAGE_MAGICK_OUTPUT_IS_NULL);
        }

        if (processOutput.isEmpty()) {
            return 0L;
        }

        String[] processOutputLines = processOutput.split("\n");

        for (String line : processOutputLines) {
            try {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return (long) Double.valueOf(matcher.group()).longValue();
                }
            } catch (NumberFormatException e) {
                // Nothing should be done here because of the exception, that will be thrown later.
            }
        }

        throw new IOException(IoExceptionMessageConstant.IMAGE_MAGICK_PROCESS_EXECUTION_FAILED + processOutput);
    }
}
