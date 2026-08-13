/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.image.WebPLogMessageConstant;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ImageSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/ImageSvgNodeRendererTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/ImageSvgNodeRendererTest/";

    private ISvgConverterProperties properties;

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void before() {
        properties = new SvgConverterProperties()
                .setBaseUri(SOURCE_FOLDER);
    }

    @Test
    public void singleImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleImage", properties);
    }

    @Test
    public void singleImageHrefTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleImageHref", properties);
    }

    @Test
    public void imageWithRectangleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithRectangle", properties);
    }

    @Test
    public void imageWithMultipleShapesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithMultipleShapes", properties);
    }

    @Test
    public void imageXYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageXY", properties);
    }

    @Test
    public void multipleImagesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleImages", properties);
    }

    @Test
    public void nonSquareImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "nonSquareImage", properties);
    }

    @Test
    public void singleImageTranslateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleImageTranslate", properties);
    }

    @Test
    public void singleImageRotateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleImageRotate", properties);
    }

    @Test
    public void singleImageScaleUpTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleImageScaleUp", properties);
    }

    @Test
    public void singleImageScaleDownTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleImageScaleDown", properties);
    }

    @Test
    public void singleImageMultipleTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleImageMultipleTransformations", properties);
    }

    @Test
    public void twoImagesWithTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "twoImagesWithTransformations", properties);
    }

    @Test
    public void differentDimensionsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "differentDimensions", properties);
    }

    @Test
    public void imageWithTransparencyTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithTransparency", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioNoneTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioNone", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioInvalidValueTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioInvalidValue", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMinYMinTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMinYMin", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMinYMidTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMinYMid", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMinYMaxTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMinYMax", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMidYMinTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMidYMin", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMidYMidTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMidYMid", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMidYMaxTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMidYMax", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMaxYMinTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMaxYMin", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMaxYMidTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMaxYMid", properties);
    }

    @Test
    public void imageWithPreserveAspectRatioXMaxYMaxTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithPreserveAspectRatioXMaxYMax", properties);
    }

    @Test
    public void imageRenderingTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "image-rendering", properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG)
    })
    public void imageWithDescriptionsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "image-descriptions", properties);
    }

    //TODO DEVSIX-4589: update after supporting
    //TODO DEVSIX-4901: update after supporting
    @Test
    public void imageBase64WithUrlTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "base64Image", properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = WebPLogMessageConstant.WEBP_NOT_FOUND),
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_PROCESS_IMAGE_WITH_GIVEN_BASE_URI)
    })
    public void webPImageWithoutWebPModuleTest() throws IOException {
        convertToSinglePage(new File(SOURCE_FOLDER + "webPImageWithoutWebPModule.svg"),
                new File(DESTINATION_FOLDER + "webPImageWithoutWebPModule.pdf"), properties);
    }
}
