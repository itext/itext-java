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

import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class SystemUtilTest extends ExtendedITextTest {
    private static final String MAGICK_COMPARE_ENVIRONMENT_VARIABLE = "ITEXT_MAGICK_COMPARE_EXEC";
    private static final String MAGICK_COMPARE_ENVIRONMENT_VARIABLE_LEGACY = "compareExec";

    private final static String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/commons/utils/SystemUtilTest/";

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/commons/utils/SystemUtilTest/";

    // This is empty file that used to check the logic for existed execution file
    private final static String STUB_EXEC_FILE = SOURCE_FOLDER + "folder with space/stubFile";

    @BeforeEach
    public void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void prepareProcessArgumentsStubExecFileTest() {
        List<String> processArguments = SystemUtil.prepareProcessArguments(STUB_EXEC_FILE, "param1 param2");
        Assertions.assertEquals(Arrays.asList(
                "./src/test/resources/com/itextpdf/commons/utils/SystemUtilTest/folder with space/stubFile", "param1",
                "param2"),
                processArguments);
    }

    @Test
    public void prepareProcessArgumentsStubExecFileInQuotesTest() {
        String testLine = "\"" + STUB_EXEC_FILE + "\"" + " compare";
        List<String> processArguments = SystemUtil.prepareProcessArguments(testLine, "param1 param2");
        Assertions.assertEquals(Arrays.asList(
                "./src/test/resources/com/itextpdf/commons/utils/SystemUtilTest/folder with space/stubFile", "compare",
                "param1", "param2"),
                processArguments);
    }

    @Test
    public void prepareProcessArgumentsGsTest() {
        List<String> processArguments = SystemUtil.prepareProcessArguments("gs", "param1 param2");
        Assertions.assertEquals(Arrays.asList(
                "gs", "param1", "param2"),
                processArguments);
    }

    @Test
    public void prepareProcessArgumentsMagickCompareTest() {
        List<String> processArguments = SystemUtil.prepareProcessArguments("magick compare", "param1 param2");
        Assertions.assertEquals(Arrays.asList(
                "magick", "compare", "param1", "param2"),
                processArguments);
    }

    @Test
    public void splitIntoProcessArgumentsPathInQuotesTest() {
        List<String> processArguments = SystemUtil
                .splitIntoProcessArguments("\"C:\\Test directory with spaces\\file.exe\"");
        Assertions.assertEquals(Collections.singletonList(
                "C:\\Test directory with spaces\\file.exe"),
                processArguments);
    }

    @Test
    public void splitIntoProcessArgumentsGsParamsTest() {
        List<String> processArguments = SystemUtil.splitIntoProcessArguments(
                " -dSAFER -dNOPAUSE -dBATCH -sDEVICE=png16m -r150 -sOutputFile='./target/test/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf-%03d.png' './src/test/resources/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf'");
        Assertions.assertEquals(Arrays.asList(
                "-dSAFER", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-r150",
                "-sOutputFile=./target/test/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf-%03d.png",
                "./src/test/resources/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf"),
                processArguments);
    }

    @Test
    public void splitIntoProcessArgumentsMagickCompareParamsTest() {
        List<String> processArguments = SystemUtil.splitIntoProcessArguments(
                "'D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\simple_pdf.pdf-001.png' 'D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\cmp_simple_pdf_with_space .pdf-001.png' './target/test/com/itextpdf/kernel/utils/CompareToolTest/diff_simple_pdf.pdf_1.png'");
        Assertions.assertEquals(Arrays.asList(
                "D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\simple_pdf.pdf-001.png",
                "D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\cmp_simple_pdf_with_space .pdf-001.png",
                "./target/test/com/itextpdf/kernel/utils/CompareToolTest/diff_simple_pdf.pdf_1.png"),
                processArguments);
    }

    @Test
    // There is no similar test in the C# version, since no way was found to test the Process class.
    public void printProcessErrorsOutputTest() throws IOException {
        StringBuilder stringBuilder = SystemUtil.printProcessErrorsOutput(new TestProcess());
        Assertions.assertEquals("This is error info", stringBuilder.toString());
    }

    @Test
    // There is no similar test in the C# version, since no way was found to test the Process class.
    public void getProcessOutputTest() throws IOException {
        String result = SystemUtil.getProcessOutput(new TestProcess());
        Assertions.assertEquals("This is process info\n"
                + "This is error info", result);
    }

    @Test
    // There is no similar test in the C# version, since no way was found to test the Process class.
    public void getProcessOutputEmptyTest() throws IOException {
        String result = SystemUtil.getProcessOutput(new EmptyTestProcess());
        Assertions.assertEquals("This is error info", result);
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6441 fix problem with System#getEnv method)
    public void runProcessAndWaitWithWorkingDirectoryTest() throws IOException, InterruptedException {
        String imageMagickPath = SystemUtil.getPropertyOrEnvironmentVariable(MAGICK_COMPARE_ENVIRONMENT_VARIABLE);
        if (imageMagickPath == null) {
            imageMagickPath = SystemUtil.getPropertyOrEnvironmentVariable(MAGICK_COMPARE_ENVIRONMENT_VARIABLE_LEGACY);
        }
        String inputImage = "image.jpg";
        String cmpImage = "cmp_image.jpg";
        String diff = System.getProperty("user.dir") + DESTINATION_FOLDER.substring(1) + "diff.png";

        StringBuilder currCompareParams = new StringBuilder();
        currCompareParams
                .append("'")
                .append(inputImage).append("' '")
                .append(cmpImage).append("' '")
                .append(diff).append("'");
        boolean result = SystemUtil.runProcessAndWait(imageMagickPath, currCompareParams.toString(), SOURCE_FOLDER);

        Assertions.assertFalse(result);
        Assertions.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6441 fix problem with System#getEnv method)
    public void runProcessAndGetProcessInfoTest() throws IOException, InterruptedException {
        String imageMagickPath = SystemUtil.getPropertyOrEnvironmentVariable(MAGICK_COMPARE_ENVIRONMENT_VARIABLE);
        if (imageMagickPath == null) {
            imageMagickPath = SystemUtil.getPropertyOrEnvironmentVariable(MAGICK_COMPARE_ENVIRONMENT_VARIABLE_LEGACY);
        }

        ProcessInfo processInfo = SystemUtil.runProcessAndGetProcessInfo(imageMagickPath,"--version");

        Assertions.assertNotNull(processInfo);
        Assertions.assertEquals(0, processInfo.getExitCode());
    }


    static class TestProcess extends Process {

        @Override
        public OutputStream getOutputStream() {
            return new ByteArrayOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream("This is process info".getBytes());
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream("This is error info".getBytes());
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {

        }
    }

    static class EmptyTestProcess extends TestProcess {

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}
