/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ImageSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/ImageSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/ImageSvgNodeRendererTest/";

    private ISvgConverterProperties properties;

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Before
    public void before() {
        properties = new SvgConverterProperties()
                .setBaseUri(sourceFolder);
    }

    @Test
    public void singleImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImage", properties);
    }

    @Test
    public void imageWithRectangleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "imageWithRectangle", properties);
    }

    @Test
    public void imageWithMultipleShapesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "imageWithMultipleShapes", properties);
    }

    @Test
    public void imageXYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "imageXY", properties);
    }

    @Test
    public void multipleImagesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "multipleImages", properties);
    }

    @Test
    public void nonSquareImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "nonSquareImage", properties);
    }

    @Test
    public void singleImageTranslateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageTranslate", properties);
    }

    @Test
    public void singleImageRotateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageRotate", properties);
    }

    @Test
    public void singleImageScaleUpTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageScaleUp", properties);
    }

    @Test
    public void singleImageScaleDownTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageScaleDown", properties);
    }

    @Test
    public void singleImageMultipleTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageMultipleTransformations", properties);
    }

    @Test
    public void twoImagesWithTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "twoImagesWithTransformations", properties);
    }

    @Test
    @Ignore("DEVSIX-2240")
    public void differentDimensionsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "differentDimensions", properties);
    }

    @Test
    public void imageWithTransparencyTest() throws IOException, InterruptedException {
        //TODO: update cmp_ when DEVSIX-2250, DEVSIX-2258 fixed
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "imageWithTransparency", properties);
    }
}
