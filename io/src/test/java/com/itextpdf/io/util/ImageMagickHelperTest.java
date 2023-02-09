/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.exceptions.IoExceptionMessage;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
// Android-Conversion-Skip-File (imagemagick isn't available on Android)
public class ImageMagickHelperTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/util/ImageMagickHelperTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/io/ImageMagickHelperTest/";

    // In some of the test we will check whether ImageMagick has printed something to the console.
    // For this reason the standard output stream will be customized. In .NET, however,
    // on the contrary to Java the name of the test gets to this stream, hence we cannot check
    // its length against zero and need to introduce some threshold, which should be definitely
    // less than the length of the help message.
    private static final int SYSTEM_OUT_LENGTH_LIMIT = 50;

    @Before
    public void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void imageMagickEnvVarIsDefault() {
        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Assert.assertNotNull(imageMagickHelper.getCliExecutionCommand());
    }

    @Test
    public void imageMagickEnvVarIsExplicitlySpecified() {
        String compareExec = SystemUtil
                .getPropertyOrEnvironmentVariable(ImageMagickHelper.MAGICK_COMPARE_ENVIRONMENT_VARIABLE);
        if (compareExec == null) {
            compareExec = SystemUtil
                    .getPropertyOrEnvironmentVariable(ImageMagickHelper.MAGICK_COMPARE_ENVIRONMENT_VARIABLE_LEGACY);
        }

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper(compareExec);

        Assert.assertNotNull(imageMagickHelper.getCliExecutionCommand());
    }

    @Test
    public void imageMagickEnvVarIsNull() throws IOException, InterruptedException {
        String inputImage = SOURCE_FOLDER + "image.png";
        String cmpImage = SOURCE_FOLDER + "cmp_image.png";
        String diff = DESTINATION_FOLDER + "diff.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper(null);
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff);

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void imageMagickEnvVarIsIncorrect() {
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> new ImageMagickHelper("-")
        );
        Assert.assertEquals(IoExceptionMessage.COMPARE_COMMAND_SPECIFIED_INCORRECTLY, e.getMessage());
    }

    @Test
    public void runImageMagickForEqualImages() throws IOException, InterruptedException {
        String inputImage = SOURCE_FOLDER + "image.png";
        String cmpImage = SOURCE_FOLDER + "cmp_image.png";
        String diff = DESTINATION_FOLDER + "diff_equalImages.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff);

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForEqualImagesWithFuzzParam() throws IOException, InterruptedException {
        String inputImage = SOURCE_FOLDER + "image.png";
        String cmpImage = SOURCE_FOLDER + "cmp_image.png";
        String diff = DESTINATION_FOLDER + "diff_equalImagesFuzzParam.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff, "0.5");

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForDifferentImages() throws IOException, InterruptedException {
        String inputImage = SOURCE_FOLDER + "Im1_1.jpg";
        String cmpImage = SOURCE_FOLDER + "cmp_Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff_differentImages.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff);

        Assert.assertFalse(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForDifferentImagesWithFuzzParamNotEqual() throws IOException, InterruptedException {
        String inputImage = SOURCE_FOLDER + "Im1_1.jpg";
        String cmpImage = SOURCE_FOLDER + "cmp_Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff_differentImagesFuzzNotEnough.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff, "0.1");

        Assert.assertFalse(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForDifferentImagesWithFuzzParamEqual() throws IOException, InterruptedException {
        String inputImage = SOURCE_FOLDER + "Im1_1.jpg";
        String cmpImage = SOURCE_FOLDER + "cmp_Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff_differentImagesFuzzEnough.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff, "2.1");

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void outImageCallsHelpTest() {
        String cmpImage = SOURCE_FOLDER + "cmp_Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff.png";

        String outImage = SOURCE_FOLDER + "Im1_1.jpg' -help '" + cmpImage + "' '" + diff;

        Object storedPrintStream = System.out;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(baos));

            ImageMagickHelper imageMagickHelper = new ImageMagickHelper();

            // In .NET the type of the thrown exception is different, therefore we just check here that
            // any exception has been thrown.
            Assert.assertThrows(Exception.class,
                    () -> imageMagickHelper.runImageMagickImageCompare(outImage, cmpImage, diff));

            // Previously a lengthy help message was printed
            System.out.flush();
            Assert.assertTrue(baos.toByteArray().length < SYSTEM_OUT_LENGTH_LIMIT);
        } catch (IOException e) {
            Assert.fail("No exception is excepted here.");
        } finally {
            StandardOutUtil.restoreStandardOut(storedPrintStream);
        }
    }

    @Test
    public void cmpImageCallsHelpTest() {
        String outImage = SOURCE_FOLDER + "Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff.png";
        String cmpImage = SOURCE_FOLDER + "cmp_Im1_1.jpg' -help '" + diff;

        Object storedPrintStream = System.out;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(baos));

            ImageMagickHelper imageMagickHelper = new ImageMagickHelper();

            // In .NET the type of the thrown exception is different, therefore we just check here that
            // any exception has been thrown.
            Assert.assertThrows(Exception.class,
                    () -> imageMagickHelper.runImageMagickImageCompare(outImage, cmpImage, diff));

            // Previously a lengthy help message was printed
            System.out.flush();
            Assert.assertTrue(baos.toByteArray().length < SYSTEM_OUT_LENGTH_LIMIT);
        } catch (IOException e) {
            Assert.fail("No exception is excepted here.");
        } finally {
            StandardOutUtil.restoreStandardOut(storedPrintStream);
        }
    }

    @Test
    public void fuzzinessCallsHelpTest() {
        String outImage = SOURCE_FOLDER + "Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff.png";
        String cmpImage = SOURCE_FOLDER + "cmp_Im1_1.jpg";

        String fuzziness = "1% -help ";

        Object storedPrintStream = System.out;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(baos));

            ImageMagickHelper imageMagickHelper = new ImageMagickHelper();

            // In .NET the type of the thrown exception is different, therefore we just check here that
            // any exception has been thrown.
            Assert.assertThrows(Exception.class,
                    () -> imageMagickHelper.runImageMagickImageCompare(outImage, cmpImage, diff, fuzziness));

            // Previously a lengthy help message was printed
            System.out.flush();
            Assert.assertTrue(baos.toByteArray().length < SYSTEM_OUT_LENGTH_LIMIT);
        } catch (IOException e) {
            Assert.fail("No exception is excepted here.");
        } finally {
            StandardOutUtil.restoreStandardOut(storedPrintStream);
        }
    }

    @Test
    // In this test we will pass several arguments as the first one. Previously that resulted in
    // different rather than equal images being compared. Now we expect an exception
    public void passOutAndCmpAndDiffAsOutTest() throws IOException, InterruptedException {
        String image = SOURCE_FOLDER + "image.png";
        String differentImage = SOURCE_FOLDER + "Im1_1.jpg";

        String diff = DESTINATION_FOLDER + "diff_equalImages.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Assert.assertThrows(Exception.class,
                () -> imageMagickHelper.runImageMagickImageCompare(
                        image + "' '" + differentImage + "' '" + diff,
                        image,
                        diff));
    }

    @Test
    // In this test we will pass several arguments as the second one. Previously that resulted in
    // diff being overridden (second diff was used). Now we expect an exception
    public void passCmpAndDiffAsDiffTest() throws IOException, InterruptedException {
        String image = SOURCE_FOLDER + "image.png";

        String diff = DESTINATION_FOLDER + "diff_equalImages.png";
        String secondDiff = DESTINATION_FOLDER + "diff_secondEqualImages.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Assert.assertThrows(Exception.class,
                () -> imageMagickHelper.runImageMagickImageCompare(
                        image,
                        image + "' '" + secondDiff,
                        diff));
    }

    @Test
    // In this test we will pass several arguments, including fuzziness, as the first one.
    // Previously that resulted in different images being compared and the number of different bytes
    // being printed to System.out. Now we expect an exception
    public void passFuzzinessAsOutTest() {
        String image = SOURCE_FOLDER + "image.png";
        String differentImage = SOURCE_FOLDER + "Im1_1.jpg";

        String diff = DESTINATION_FOLDER + "diff.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Assert.assertThrows(Exception.class, () ->
                imageMagickHelper.runImageMagickImageCompare(
                        image + "' -metric AE -fuzz 1% '" + differentImage + "' '" + diff,
                        image,
                        diff));
    }

    @Test
    // When fuzziness is specified, ImageMagick prints to standard output the number of different bytes.
    // Since we compare equal images, we expect this number to be zero.
    public void compareEqualsImagesAndCheckFuzzinessTest() {
        String image = SOURCE_FOLDER + "image.png";
        String diff = DESTINATION_FOLDER + "diff_equalImages.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Object storedPrintStream = System.out;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            System.setOut(new PrintStream(baos));
            boolean result = imageMagickHelper.runImageMagickImageCompare(
                    image,
                    image,
                    diff, "1");

            Assert.assertTrue(result);
            Assert.assertTrue(FileUtil.fileExists(diff));

            System.out.flush();
            String output = new String(baos.toByteArray()).trim();

            // This check is implemented in such a peculiar way because of .NET autoporting
            Assert.assertEquals('0', output.charAt(output.length() - 1));
            if (output.length() > 1) {
                Assert.assertFalse(Character.isDigit(output.charAt(output.length() - 2)));
            }
        } catch (Exception e) {
            Assert.fail("No exception is expected here.");
        } finally {
            StandardOutUtil.restoreStandardOut(storedPrintStream);
        }
    }

    @Test
    public void compareEqualImagesAndGetResult() throws InterruptedException, IOException {
        String image = SOURCE_FOLDER + "image.png";
        String diff = DESTINATION_FOLDER + "diff_equalImages_result.png";

        ImageMagickCompareResult result = new ImageMagickHelper().runImageMagickImageCompareAndGetResult(
                image,
                image,
                diff,
                "1");

        Assert.assertTrue(result.isComparingResultSuccessful());
        Assert.assertEquals(0, result.getDiffPixels());
    }

    @Test
    public void compareDifferentImagesAndGetResult() throws InterruptedException, IOException {
        String image = SOURCE_FOLDER + "image.png";
        String image2 = SOURCE_FOLDER + "Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff_equalImages.png";

        ImageMagickCompareResult result = new ImageMagickHelper().runImageMagickImageCompareAndGetResult(
                image,
                image2,
                diff, "1");

        Assert.assertFalse(result.isComparingResultSuccessful());
    }

    @Test
    public void runImageMagickImageCompareEqualWithThreshold() throws IOException, InterruptedException {
        String image = SOURCE_FOLDER + "image.png";
        String image2 = SOURCE_FOLDER + "image.png";
        String diff = DESTINATION_FOLDER + "diff_equalImages.png";

        boolean result = new ImageMagickHelper().runImageMagickImageCompareWithThreshold(
                image,
                image2,
                diff,
                "0",
                0);

        Assert.assertTrue(result);
    }

    @Test
    public void runImageMagickImageCompareWithEnoughThreshold() throws IOException, InterruptedException {
        String image = SOURCE_FOLDER + "image.png";
        String image2 = SOURCE_FOLDER + "Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff_equalImages.png";

        boolean result = new ImageMagickHelper().runImageMagickImageCompareWithThreshold(
                image,
                image2,
                diff,
                "20",
                2000000);

        Assert.assertTrue(result);
    }

    @Test
    public void runImageMagickImageCompareWithNotEnoughThreshold() throws IOException, InterruptedException {
        String image = SOURCE_FOLDER + "image.png";
        String image2 = SOURCE_FOLDER + "Im1_1.jpg";
        String diff = DESTINATION_FOLDER + "diff_equalImages.png";

        boolean result = new ImageMagickHelper().runImageMagickImageCompareWithThreshold(
                image,
                image2,
                diff,
                "20",
                2000);

        Assert.assertFalse(result);
    }

}
