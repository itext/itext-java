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
package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
//TODO DEVSIX-8832: Update cmp files
public class BackgroundTest extends SvgIntegrationTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/BackgroundTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/BackgroundTest/";

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
    public void topSVGBackGroundColorNameTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundColorName", properties);
    }

    @Test
    public void topSVGBackGroundBorderBoxAndColorNameTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundBorderBoxAndColorName", properties);
    }

    @Test
    public void backgroundTopLevelLinGradTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "backgroundTopLevelLinGrad", properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES)
    })
    public void backgroundTopLevelRadGradTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "backgroundTopLevelRadGrad", properties);
    }

    @Test
    public void topSVGBackGroundImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundImage", properties);
    }

    @Test
    public void topSVGImageSHNoRepeatCenterRelSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageSHNoRepeatCenterRelSize", properties);
    }

    @Test
    public void topSVGImageSHNoRepeatRightRelSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageSHNoRepeatRightRelSize", properties);
    }

    @Test
    public void topSVGRepeatYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatY", properties);
    }

    @Test
    public void topSVGRepeatYShortHandTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatYShortHand", properties);
    }

    @Test
    public void topSVGRepeatXTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatX", properties);
    }

    @Test
    public void topSVGRepeatXShortHandTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatXShortHand", properties);
    }

    @Test
    public void topSVGRepeatTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeat", properties);
    }

    @Test
    public void topSVGRepeatShortHandTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatShortHand", properties);
    }

    @Test
    public void topSVGRepeatSpaceTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatSpace", properties);
    }

    @Test
    public void topSVGRepeatSpaceShortHandTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatSpaceShortHand", properties);
    }

    @Test
    public void topSVGRepeatRoundTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatRound", properties);
    }

    @Test
    public void topSVGRepeatRoundShortHandTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGRepeatRoundShortHand", properties);
    }

    @Test
    public void topSVGNoRepeatTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGNoRepeat", properties);
    }

    @Test
    public void topSVGNoRepeatShortHandTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGNoRepeatShortHand", properties);
    }

    @Test
    public void topSVGSpaceRepeatTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGSpaceRepeat", properties);
    }

    @Test
    public void topSVGSpaceRepeatShortHandTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGSpaceRepeatShortHand", properties);
    }

    @Test
    public void topSvgRepeatSizePixelsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRepeatSizePixels", properties);
    }

    @Test
    public void topSvgRepeatRoundSizePixelsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRepeatRoundSizePixels", properties);
    }

    @Test
    public void topSvgRepeatYSizeRelativePercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRepeatYSizeRelativePercentage", properties);
    }

    @Test
    public void topSvgRepeatXSizeRelativePercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRepeatXSizeRelativePercentage", properties);
    }

    @Test
    public void topSvgRepeatSizeRelativeDiffPercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRepeatSizeRelativeDiffPercentage", properties);
    }

    @Test
    public void topSvgRepeatSizeRelativePercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRepeatSizeRelativePercentage", properties);
    }

    @Test
    public void topSvgRepeatAndSVGRelSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRepeatAndSVGRelSize", properties);
    }

    @Test
    public void topSvgRelSizeRepeatTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSvgRelSizeRepeat", properties);
    }

    @Test
    public void topSVGImageBlendingMultiplyTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageBlendingMultiply", properties);
    }

    @Test
    public void topSVGImageBlendingScreenTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageBlendingScreen", properties);
    }

    @Test
    public void topSVGImageBlendingHardLightTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageBlendingHardLight", properties);
    }

    @Test
    public void topSVGImageBlendingDifferenceTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageBlendingDifference", properties);
    }

    @Test
    public void topSVGImageBlendingDarkenTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageBlendingDarken", properties);
    }

    @Test
    public void topSVGImageBlendingLuminosityTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageBlendingLuminosity", properties);
    }

    @Test
    public void topSVGImageBlendingDarkenLuminosityTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGImageBlendingDarkenLuminosity", properties);
    }

    @Test
    public void topSVGBackGroundColorHexTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundColorHex", properties);
    }

    @Test
    public void topSVGBackGroundColorByNameTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundColorByName", properties);
    }

    @Test
    public void topSVGBackGroundColorRGBTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundColorRGB", properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION)
    })
    public void topSVGBackGroundColorHSLTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundColorHSL", properties);
    }

    @Test
    public void topSVGBackGroundColorTransparentTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundColorTransparent", properties);
    }

    @Test
    public void topSVGBackGroundColorCurrentColorTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGBackGroundColorCurrentColor", properties);
    }

    @Test
    public void topSVGPositionRightTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionRight", properties);
    }

    @Test
    public void topSVGPositionLeftTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionLeft", properties);
    }

    @Test
    public void topSVGPositionTopTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionTop", properties);
    }

    @Test
    public void topSVGPositionBottomTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionBottom", properties);
    }

    @Test
    public void topSVGPositionCenterTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionCenter", properties);
    }

    @Test
    public void topSVGPositionPercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionPercentage", properties);
    }

    @Test
    public void topSVGPositionMixedPixelsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionMixedPixels", properties);
    }

    @Test
    public void topSVGPositionMixedPercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionMixedPercentage", properties);
    }

    @Test
    public void topSVGPositionXRightTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionXRight", properties);
    }

    @Test
    public void topSVGPositionXPercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionXPercentage", properties);
    }

    @Test
    public void topSVGPositionXRemTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionXRem", properties);
    }

    @Test
    public void topSVGPositionXRightPixelsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionXRightPixels", properties);
    }

    @Test
    public void topSVGPositionYCenterTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionYCenter", properties);
    }

    @Test
    public void topSVGPositionYPercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionYPercentage", properties);
    }

    @Test
    public void topSVGPositionYRemTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionYRem", properties);
    }

    @Test
    public void topSVGPositionYBottomPixelsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGPositionYBottomPixels", properties);
    }

    @Test
    public void topSVGSizeCoverTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGSizeCover", properties);
    }

    @Test
    public void topSVGSizeContainTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGSizeContain", properties);
    }

    @Test
    public void topSVGSizePercentageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGSizePercentage", properties);
    }

    @Test
    public void topSVGSizePixelsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGSizePixels", properties);
    }

    @Test
    public void topSVGViewBoxTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBox", properties);
    }

    @Test
    public void topSVGViewBoxWithRelBGSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxWithRelBGSize", properties);
    }

    @Test
    public void topSVGViewBoxWithRelSVGSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxWithRelSVGSize", properties);
    }

    @Test
    public void topSVGAndInnerSVGViewBoxTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGAndInnerSVGViewBox", properties);
    }

    @Test
    public void topSVGViewBoxARNoneTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxARNone", properties);
    }

    @Test
    public void topSVGViewBoxARNoneRelSizeSVGTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxARNoneRelSizeSVG", properties);
    }

    @Test
    public void topSVGViewBoxARNoneRelSizeSVGAndBGTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxARNoneRelSizeSVGAndBG", properties);
    }

    @Test
    public void topSVGViewBoxARNoneRelSizeBGTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxARNoneRelSizeBG", properties);
    }

    @Test
    public void topSVGViewBoxARNoneBGSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxARNoneBGSize", properties);
    }

    @Test
    public void topSVGAspectRatioNoViewBoxTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGAspectRatioNoViewBox", properties);
    }

    @Test
    public void topSVGViewBoxNestedElementARTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "topSVGViewBoxNestedElementAR", properties);
    }

    //Expected to not show any background on inner elements.
    @Test
    public void nestedElementsBGTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedElementsBG", properties);
    }
}
