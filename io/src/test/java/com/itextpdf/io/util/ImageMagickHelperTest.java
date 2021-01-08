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

import java.io.IOException;

@Category(IntegrationTest.class)
public class ImageMagickHelperTest extends ExtendedITextTest {
    private final static String sourceFolder = "./src/test/resources/com/itextpdf/io/util/ImageMagickHelperTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/io/ImageMagickHelperTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void setUp() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void imageMagickEnvVarIsDefault() {
        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        Assert.assertNotNull(imageMagickHelper.getCliExecutionCommand());
    }

    @Test
    public void imageMagickEnvVarIsExplicitlySpecified() {
        String compareExec = SystemUtil.getPropertyOrEnvironmentVariable(ImageMagickHelper.MAGICK_COMPARE_ENVIRONMENT_VARIABLE);
        if (compareExec == null) {
            compareExec = SystemUtil.getPropertyOrEnvironmentVariable(ImageMagickHelper.MAGICK_COMPARE_ENVIRONMENT_VARIABLE_LEGACY);
        }

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper(compareExec);

        Assert.assertNotNull(imageMagickHelper.getCliExecutionCommand());
    }

    @Test
    public void imageMagickEnvVarIsNull() throws IOException, InterruptedException {
        String inputImage = sourceFolder + "image.png";
        String cmpImage = sourceFolder + "cmp_image.png";
        String diff = destinationFolder + "diff.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper(null);
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff);

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void imageMagickEnvVarIsIncorrect() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(IoExceptionMessage.COMPARE_COMMAND_SPECIFIED_INCORRECTLY);

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper("-");
    }

    @Test
    public void runImageMagickForEqualImages() throws IOException, InterruptedException {
        String inputImage = sourceFolder + "image.png";
        String cmpImage = sourceFolder + "cmp_image.png";
        String diff = destinationFolder + "diff_equalImages.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff);

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForEqualImagesWithFuzzParam() throws IOException, InterruptedException {
        String inputImage = sourceFolder + "image.png";
        String cmpImage = sourceFolder + "cmp_image.png";
        String diff = destinationFolder + "diff_equalImagesFuzzParam.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff, "0.5");

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForDifferentImages() throws IOException, InterruptedException {
        String inputImage = sourceFolder + "Im1_1.jpg";
        String cmpImage = sourceFolder + "cmp_Im1_1.jpg";
        String diff = destinationFolder + "diff_differentImages.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff);

        Assert.assertFalse(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForDifferentImagesWithFuzzParamNotEqual() throws IOException, InterruptedException {
        String inputImage = sourceFolder + "Im1_1.jpg";
        String cmpImage = sourceFolder + "cmp_Im1_1.jpg";
        String diff = destinationFolder + "diff_differentImagesFuzzNotEnough.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff, "0.1");

        Assert.assertFalse(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }

    @Test
    public void runImageMagickForDifferentImagesWithFuzzParamEqual() throws IOException, InterruptedException {
        String inputImage = sourceFolder + "Im1_1.jpg";
        String cmpImage = sourceFolder + "cmp_Im1_1.jpg";
        String diff = destinationFolder + "diff_differentImagesFuzzEnough.png";

        ImageMagickHelper imageMagickHelper = new ImageMagickHelper();
        boolean result = imageMagickHelper.runImageMagickImageCompare(inputImage, cmpImage, diff, "1.2");

        Assert.assertTrue(result);
        Assert.assertTrue(FileUtil.fileExists(diff));
    }
}
