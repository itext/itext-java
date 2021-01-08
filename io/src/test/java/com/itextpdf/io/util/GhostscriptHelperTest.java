/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.IoExceptionMessage;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;


@Category(IntegrationTest.class)
public class GhostscriptHelperTest extends ExtendedITextTest {
    private final static String sourceFolder = "./src/test/resources/com/itextpdf/io/util/GhostscriptHelperTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/io/GhostscriptHelperTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void setUp() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void ghostScriptEnvVarIsDefault() {
        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        Assert.assertNotNull(ghostscriptHelper.getCliExecutionCommand());
    }

    @Test
    public void ghostScriptEnvVarIsExplicitlySpecified() {
        String gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE);
        if (gsExec == null) {
            gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE_LEGACY);
        }

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper(gsExec);

        Assert.assertNotNull(ghostscriptHelper.getCliExecutionCommand());
    }

    @Test
    public void ghostScriptEnvVarIsNull() {
        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper(null);

        Assert.assertNotNull(ghostscriptHelper.getCliExecutionCommand());
    }

    @Test
    public void ghostScriptEnvVarIsIncorrect() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(IoExceptionMessage.GS_ENVIRONMENT_VARIABLE_IS_NOT_SPECIFIED);

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper("-");
    }

    @Test
    public void runGhostScriptIncorrectOutputDirectory() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "imageHandlerUtilTest.pdf";
        String exceptionMessage = "Cannot open output directory for " + inputPdf;

        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(exceptionMessage);

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, "-",
                "outputPageImage.png", "1");
    }

    @Test
    public void runGhostScriptIncorrectParams() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "imageHandlerUtilTest.pdf";
        String exceptionMessage = "GhostScript failed for " + inputPdf;

        junitExpectedException.expect(GhostscriptHelper.GhostscriptExecutionException.class);
        junitExpectedException.expectMessage(exceptionMessage);

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, destinationFolder,
                "outputPageImage.png", "q@W");
    }

    @Test
    public void runGhostScriptTestForSpecificPage() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "imageHandlerUtilTest.pdf";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, destinationFolder,
                "specificPage.png", "1");

        Assert.assertEquals(1, FileUtil.listFilesInDirectory(destinationFolder, true).length);
        Assert.assertTrue(FileUtil.fileExists(destinationFolder + "specificPage.png"));
    }

    @Test
    public void runGhostScriptTestForSeveralSpecificPages() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "imageHandlerUtilTest.pdf";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        String imageFileName = new File(inputPdf).getName() + "_severalSpecificPages-%03d.png";
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, destinationFolder,
                imageFileName, "1,3");

        Assert.assertEquals(2, FileUtil.listFilesInDirectory(destinationFolder, true).length);
        Assert.assertTrue(FileUtil.fileExists(destinationFolder + "imageHandlerUtilTest.pdf_severalSpecificPages-001.png"));
        Assert.assertTrue(FileUtil.fileExists(destinationFolder + "imageHandlerUtilTest.pdf_severalSpecificPages-002.png"));
    }

    @Test
    public void runGhostScriptTestForAllPages() throws IOException, InterruptedException {
        String inputPdf = sourceFolder + "imageHandlerUtilTest.pdf";

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        String imageFileName = new File(inputPdf).getName() + "_allPages-%03d.png";
        ghostscriptHelper.runGhostScriptImageGeneration(inputPdf, destinationFolder, imageFileName);

        Assert.assertEquals(3, FileUtil.listFilesInDirectory(destinationFolder, true).length);
        Assert.assertTrue(FileUtil.fileExists(destinationFolder + "imageHandlerUtilTest.pdf_allPages-001.png"));
        Assert.assertTrue(FileUtil.fileExists(destinationFolder + "imageHandlerUtilTest.pdf_allPages-002.png"));
        Assert.assertTrue(FileUtil.fileExists(destinationFolder + "imageHandlerUtilTest.pdf_allPages-003.png"));
    }

    @Test
    public void dSaferParamInGhostScriptHelperTest() throws IOException, InterruptedException {
        String cmpPdf = sourceFolder + "maliciousPsInvokingCalcExe.ps";
        String maliciousPsInvokingCalcExe = destinationFolder + "maliciousPsInvokingCalcExe.png";
        int majorVersion = 0;
        int minorVersion = 0;
        boolean isWindows = identifyOsType().toLowerCase().contains("win");
        if (isWindows) {
            String gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE);
            if (gsExec == null) {
                gsExec = SystemUtil.getPropertyOrEnvironmentVariable(GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE_LEGACY);
            }
            String[] pathParts = gsExec.split("\\d\\.\\d\\d");
            for (int i = 0; i < pathParts.length; i++) {
                gsExec = gsExec.replace(pathParts[i], "");
            }
            String[] version = gsExec.split("\\.");
            majorVersion = Integer.parseInt(version[0]);
            minorVersion = Integer.parseInt(version[1]);
        }
        try {
            GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
            ghostscriptHelper.runGhostScriptImageGeneration(cmpPdf, destinationFolder, "maliciousPsInvokingCalcExe.png");
            if (isWindows) {
                Assert.assertTrue((majorVersion > 9 || (majorVersion == 9 && minorVersion >= 50)));
            }
        } catch (GhostscriptHelper.GhostscriptExecutionException e) {
            if (isWindows) {
                Assert.assertTrue((majorVersion < 9 || (majorVersion == 9 && minorVersion < 50)));
            }
        }
        Assert.assertFalse(FileUtil.fileExists(maliciousPsInvokingCalcExe));
    }

    @Test
    public void ghostScriptImageGenerationTest() throws IOException, InterruptedException {
        String filename = "resultantImage.png";
        String psFile = sourceFolder + "simple.ps";
        String resultantImage = destinationFolder + filename;
        String cmpResultantImage = sourceFolder + "cmp_" + filename;
        String diff = destinationFolder + "diff_" + filename;

        GhostscriptHelper ghostscriptHelper = new GhostscriptHelper();
        ghostscriptHelper.runGhostScriptImageGeneration(psFile, destinationFolder, filename);
        Assert.assertTrue(FileUtil.fileExists(resultantImage));

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Assert.assertTrue(imageMagickHelper.runImageMagickImageCompare(resultantImage, cmpResultantImage, diff));
    }

    /**
     * Identifies type of current OS and return it (win, linux).
     *
     * @return type of current os as {@link java.lang.String}
     */
    private static String identifyOsType() {
        String os = System.getProperty("os.name") == null
                ? System.getProperty("OS") : System.getProperty("os.name");
        return os.toLowerCase();
    }
}
