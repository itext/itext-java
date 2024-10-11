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
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
// Android-Conversion-Skip-File (ghostscript isn't available on Android)
public class GhostscriptHelperTest extends ExtendedITextTest {
    private final static String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/util/GhostscriptHelperTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/io/GhostscriptHelperTest/";

    // In some of the test we will check whether Ghostscript has printed its help message to the console.
    // The value of this threshold should be definitely less than the length of the help message.
    private static final int SYSTEM_OUT_LENGTH_LIMIT = 450;

    @BeforeEach
    public void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void ghostScriptEnvVarIsDefault() {
        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        Assertions.assertNotNull(ghostscriptHelper.getCliExecutionCommand());
    }

    @Test
    public void ghostScriptEnvVarIsExplicitlySpecified() {
        String gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE);

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper(gsExec);

        Assertions.assertNotNull(ghostscriptHelper.getCliExecutionCommand());
    }

    @Test
    public void ghostScriptEnvVarIsNull() {
        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper(null);

        Assertions.assertNotNull(ghostscriptHelper.getCliExecutionCommand());
    }

    @Test
    public void ghostScriptEnvVarIsIncorrect() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new GhostscriptHelper("-")
        );
        Assertions.assertEquals(IoExceptionMessageConstant.GS_ENVIRONMENT_VARIABLE_IS_NOT_SPECIFIED, e.getMessage());
    }

    @Test
    public void runGhostScriptIncorrectOutputDirectory() throws IOException, InterruptedException {
        String inputPdf = SOURCE_FOLDER + "imageHandlerUtilTest.pdf";
        String exceptionMessage = "Cannot open output directory for " + inputPdf;

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, "-",
                        "outputPageImage.png", "1")
        );
        Assertions.assertEquals(exceptionMessage, e.getMessage());
    }

    @Test
    public void runGhostScriptIncorrectParams() {
        String inputPdf = SOURCE_FOLDER + "imageHandlerUtilTest.pdf";
        String invalidPageList = "q@W";
        String exceptionMessage = "Invalid page list: " + invalidPageList;

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();

        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, DESTINATION_FOLDER,
                        "outputPageImage.png", invalidPageList)
        );
        Assertions.assertEquals(exceptionMessage, e.getMessage());
    }

    @Test
    public void runGhostScriptTestForSpecificPage() throws IOException, InterruptedException {
        String inputPdf = SOURCE_FOLDER + "imageHandlerUtilTest.pdf";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, DESTINATION_FOLDER,
                "specificPage", "1");

        Assertions.assertEquals(1, FileUtil.listFilesInDirectory(DESTINATION_FOLDER, true).length);
        Assertions.assertTrue(FileUtil.fileExists(DESTINATION_FOLDER + "specificPage-001.png"));
    }

    @Test
    public void runGhostScriptTestForSeveralSpecificPages() throws IOException, InterruptedException {
        String inputPdf = SOURCE_FOLDER + "imageHandlerUtilTest.pdf";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        String imageFileName = new File(inputPdf).getName() + "_severalSpecificPages";
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, DESTINATION_FOLDER,
                imageFileName, "1,3");

        Assertions.assertEquals(2, FileUtil.listFilesInDirectory(DESTINATION_FOLDER, true).length);
        Assertions.assertTrue(
                FileUtil.fileExists(DESTINATION_FOLDER + "imageHandlerUtilTest.pdf_severalSpecificPages-001.png"));
        Assertions.assertTrue(
                FileUtil.fileExists(DESTINATION_FOLDER + "imageHandlerUtilTest.pdf_severalSpecificPages-002.png"));
    }

    @Test
    public void runGhostScriptTestForAllPages() throws IOException, InterruptedException {
        String inputPdf = SOURCE_FOLDER + "imageHandlerUtilTest.pdf";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        String imageFileName = new File(inputPdf).getName() + "_allPages";
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, DESTINATION_FOLDER, imageFileName);

        Assertions.assertEquals(3, FileUtil.listFilesInDirectory(DESTINATION_FOLDER, true).length);
        Assertions.assertTrue(FileUtil.fileExists(DESTINATION_FOLDER + "imageHandlerUtilTest.pdf_allPages-001.png"));
        Assertions.assertTrue(FileUtil.fileExists(DESTINATION_FOLDER + "imageHandlerUtilTest.pdf_allPages-002.png"));
        Assertions.assertTrue(FileUtil.fileExists(DESTINATION_FOLDER + "imageHandlerUtilTest.pdf_allPages-003.png"));
    }

    @Test
    public void dSaferParamInGhostScriptHelperTest() throws IOException, InterruptedException {
        String input = SOURCE_FOLDER + "unsafePostScript.ps";
        String outputName = "unsafePostScript.png";

        try {
            GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
            ghostscriptHelper.runGhostScriptImageGeneration(input, DESTINATION_FOLDER, outputName);
        } catch (GhostscriptHelper.GhostscriptExecutionException e) {
            System.out.println("Error code was returned on processing of malicious script with -dSAFER option enabled. "
                    + "This is expected for some environments and ghostscript versions. "
                    + "We assert only the absence of malicious script result (created file).\n");
        }

        // If we had not set -dSAFER option, the following files would be created
        String maliciousResult1 = DESTINATION_FOLDER + "output1.txt";
        String maliciousResult2 = DESTINATION_FOLDER + "output2.txt";

        Assertions.assertFalse(FileUtil.fileExists(maliciousResult1));
        Assertions.assertFalse(FileUtil.fileExists(maliciousResult2));
    }

    @Test
    public void ghostScriptImageGenerationTest() throws IOException, InterruptedException {
        String name = "resultantImage";
        String filename = name + ".png";
        String psFile = SOURCE_FOLDER + "simple.ps";
        String resultantImage = DESTINATION_FOLDER + name + "-001.png";
        String cmpResultantImage = SOURCE_FOLDER + "cmp_" + filename;
        String diff = DESTINATION_FOLDER + "diff_" + filename;

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        ghostscriptHelper.runGhostScriptImageGeneration(psFile, DESTINATION_FOLDER, name);
        Assertions.assertTrue(FileUtil.fileExists(resultantImage));

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Assertions.assertTrue(imageMagickHelper.runImageMagickImageCompare(resultantImage, cmpResultantImage, diff));
    }

    @Test
    // Previously this test printed help message. Now an exception should be thrown.
    public void pdfCallsHelpTest() {
        String inputPdf = SOURCE_FOLDER + "../test.pdf -h";
        String outputImagePattern = "image";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();

        // In .NET the type of the thrown exception is different, therefore we just check here that
        // any exception has been thrown.
        Assertions.assertThrows(Exception.class, () ->
                ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, DESTINATION_FOLDER, outputImagePattern));
    }

    @Test
    public void outputImageCallsHelpTest() throws IOException {
        String inputPdf = SOURCE_FOLDER + "../test.pdf";
        String outputImagePattern = "justSomeText \" -h";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();

        Object storedPrintStream = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(baos));
            ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, DESTINATION_FOLDER, outputImagePattern);
        } catch (Exception e) {
            // This test fails on Windows, but works on Linux. So our goal is not to check
            // whether an exception was thrown, but whether there is the help message in the output
        } finally {
            System.out.flush();
            StandardOutUtil.restoreStandardOut(storedPrintStream);
            Assertions.assertTrue(baos.toByteArray().length < SYSTEM_OUT_LENGTH_LIMIT);
            baos.close();
        }
    }

    @Test
    // Previously this test printed help message. Now an exception should be thrown.
    public void pageListCallsHelpTest() {
        String inputPdf = SOURCE_FOLDER + "../test.pdf";
        String outputImagePattern = "justSomeText";
        String pageList = "1 -h";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();

        // In .NET the type of the thrown exception is different, therefore we just check here that
        // any exception has been thrown.
        Assertions.assertThrows(Exception.class, () ->
                ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, DESTINATION_FOLDER,
                        outputImagePattern, pageList));
    }

    @Test
    public void nonExistingDestinationFolder() {
        String inputPdf = SOURCE_FOLDER + "../test.pdf";
        String outputImagePattern = "justSomeText";
        String destinationFolder = "notExistingFolder";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();

        // In .NET the type of the thrown exception is different, therefore we just check here that
        // any exception has been thrown.
        Assertions.assertThrows(Exception.class,
                () -> ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, destinationFolder,
                        outputImagePattern));
    }
}
