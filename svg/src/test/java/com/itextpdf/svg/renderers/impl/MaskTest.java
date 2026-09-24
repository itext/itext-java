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

import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class MaskTest extends SvgIntegrationTest {

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/MaskTest/";
  private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/MaskTest/";

  private ISvgConverterProperties properties;

  @BeforeAll
  public static void beforeClass() {
    ITextTest.createDestinationFolder(DESTINATION_FOLDER);
  }

  @BeforeEach
  public void before() {
    properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
  }

  @Test
  public void maskBasic() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskBasic", properties);
  }

  @Test
  public void maskUnquotedUrlReferenceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnquotedUrlReference", properties);
  }

  @Test
  public void maskSingleQuotedUrlReferenceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskSingleQuotedUrlReference", properties);
  }

  @Test
  public void maskDoubleQuotedUrlReferenceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskDoubleQuotedUrlReference", properties);
  }

  @Test
  public void maskSingleQuotedUrlReferenceWithWhitespaceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "maskSingleQuotedUrlReferenceWithWhitespace", properties);
  }

  @Test
  public void maskDoubleQuotedUrlReferenceWithWhitespaceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "maskDoubleQuotedUrlReferenceWithWhitespace", properties);
  }

  @Test
  //TODO DEVSIX-4136 update after gradient opacity support implementation
  public void maskWithGradientWithStopOpacity() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskWithGradientWithStopOpacity", properties);
  }

  @Test
  public void maskContentUnitsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskContentUnits", properties);
  }

  @Test
  public void maskPatternCombiTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternCombi", properties);
  }

  @Test
  public void maskPatternAppliedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternApplied", properties);
  }

  @Test
  public void maskMultiShapesTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskMultiShapes", properties);
  }

  @Test
  public void maskPatternGradientAppliedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternGradientApplied", properties);
  }

  @Test
  public void maskGradientAppliedMaskContentUnitsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskGradientAppliedMaskContentUnits", properties);
  }

  @Test
  public void maskPatternMaskContentUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternMaskContentUnitsUserSpaceOnUse", properties);
  }

  @Test
  public void maskUnitsObjectBoundingBoxTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsObjectBoundingBox", properties);
  }

  @Test
  public void maskUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsUserSpaceOnUse", properties);
  }

  @Test
  public void maskTransformTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransform", properties);
  }

  @Test
  public void maskTransform2Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransform2", properties);
  }

  @Test
  public void maskTransform3Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransform3", properties);
  }

  @Test
  public void maskInheritedBasicTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskInheritedBasic", properties);
  }

  @Test
  public void maskInherited3LevelTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskInherited3Level", properties);
  }

  @Test
  public void maskPatternAppliedInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternAppliedInherited", properties);
  }

  @Test
  public void maskUnitsObjectBoundingBoxInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsObjectBoundingBoxInherited", properties);
  }

  @Test
  public void maskUnitsUserSpaceOnUseInherited2Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsUserSpaceOnUseInherited2", properties);
  }

  @Test
  public void maskUnitsUserSpaceOnUseInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsUserSpaceOnUseInherited", properties);
  }

  @Test
  public void maskUnitsObjectBoundingBoxInherited2Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsObjectBoundingBoxInherited2", properties);
  }

  @Test
  public void maskTransformInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransformInherited", properties);
  }

  @Test
  public void maskWithLinearGradient() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskWithLinearGradient", properties);
  }

  @Test
  public void maskWithRadialGradientTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskWithRadialGradient", properties);
  }

  @Test
  public void luminanceMaskWithLinearGradientTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "luminanceMaskWithLinearGradient", properties);
  }

  @Test
  public void luminanceMaskWithRadialGradientTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "luminanceMaskWithRadialGradient", properties);
  }

  @Test
  public void luminanceMaskWithPatternTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "luminanceMaskWithPattern", properties);
  }

  @Test
  public void maskWithPatternMaskTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskWithPatternMask", properties);
  }

  @Test
  public void maskReferenceOnContainerTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskReferenceOnContainer", properties);
  }

  @Test
  @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.INVALID_MASK_REFERENCE))
  public void maskMissingReferenceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskMissingReference", properties);
  }

  @Test
  @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.INVALID_MASK_REFERENCE))
  public void maskReferenceToNonMaskElementTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskReferenceToNonMaskElement", properties);
  }

  @Test
  public void maskOffscreenCompositingWithBackgroundTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskOffscreenCompositingWithBackground", properties);
  }

  @Test
  public void maskAppliedAfterStrokeAndMarkerCompositingTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "maskAppliedAfterStrokeAndMarkerCompositing", properties);
  }

  @Test
  public void maskAlphaMultiplicationSemanticsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskAlphaMultiplicationSemantics", properties);
  }

  @Test
  public void maskTypeLuminanceWithColoredContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeLuminanceWithColoredContent", properties);
  }

  @Test
  public void maskTypeLuminanceWithStrokeContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeLuminanceWithStrokeContent", properties);
  }

  @Test
  public void maskTypeAlphaWithColoredContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeAlphaWithColoredContent", properties);
  }

  @Test
  public void maskTypeDefaultLuminanceWhenOmittedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeDefaultLuminanceWhenOmitted", properties);
  }

  @Test
  public void maskTypePresentationAttributeAlphaTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypePresentationAttributeAlpha", properties);
  }

  @Test
  public void maskTypeStyleOverridesPresentationAttributeTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeStyleOverridesPresentationAttribute", properties);
  }

  @Test
  public void maskTypeInvalidTokenDefaultsToLuminanceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeInvalidTokenDefaultsToLuminance", properties);
  }

  @Test
  public void maskTypeAlphaComplexContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeAlphaComplexContent", properties);
  }

  @Test
  public void maskTypeAlphaAndLuminanceSideBySideTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTypeAlphaAndLuminanceSideBySide", properties);
  }

  // TODO DEVSIX-10201 update cmp when image mask failures are fixed
  @Test
  public void maskImageAlphaAndLuminanceSideBySideTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "maskImageAlphaAndLuminanceSideBySide", properties);
  }

  @Test
  public void maskLuminanceFromRgbaContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskLuminanceFromRgbaContent", properties);
  }

  @Test
  public void maskLuminanceFromRgbContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskLuminanceFromRgbContent", properties);
  }

  @Test
  public void maskLuminanceFromGrayscaleGradientTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskLuminanceFromGrayscaleGradient", properties);
  }

  @Test
  public void maskTransparentColorsInContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransparentColorsInContent", properties);
  }

  @Test
  public void maskColorInterpolationSrgbTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskColorInterpolationSrgb", properties);
  }

  @Test
  public void maskColorInterpolationLinearRgbTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskColorInterpolationLinearRgb", properties);
  }

  @Test
  public void maskColorInterpolationInvalidTokenFallbackTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskColorInterpolationInvalidTokenFallback", properties);
  }

  @Test
  public void maskOverlappingTransparentElementsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskOverlappingTransparentElements", properties);
  }

  @Test
  public void maskPaintOrderAffectsResultTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPaintOrderAffectsResult", properties);
  }

  @Test
  public void maskNestedMaskUsageTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskNestedMaskUsage", properties);
  }

  @Test
  public void maskDefinitionNotRenderedWhenUnusedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskDefinitionNotRenderedWhenUnused", properties);
  }

  @Test
  public void maskDisplayNoneStillReferenceableTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskDisplayNoneStillReferenceable", properties);
  }

  @Test
  public void maskAncestorDisplayNoneStillReferenceableTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskAncestorDisplayNoneStillReferenceable", properties);
  }

  @Test
  public void maskElementOpacityIgnoredTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskElementOpacityIgnored", properties);
  }

  @Test
  public void maskElementFilterIgnoredTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskElementFilterIgnored", properties);
  }

  @Test
  public void maskUnitsDefaultObjectBoundingBoxTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsDefaultObjectBoundingBox", properties);
  }

  @Test
  public void maskContentUnitsDefaultUserSpaceOnUseTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskContentUnitsDefaultUserSpaceOnUse", properties);
  }

  @Test
  public void maskDefaultXyWidthHeightValuesTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskDefaultXyWidthHeightValues", properties);
  }

  @Test
  @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.MASK_WIDTH_OR_HEIGHT_IS_NEGATIVE))
  public void maskNegativeWidthTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskNegativeWidth", properties);
  }

  @Test
  @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.MASK_WIDTH_OR_HEIGHT_IS_NEGATIVE))
  public void maskNegativeHeightTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskNegativeHeight", properties);
  }

  @Test
  public void maskZeroWidthDisablesRenderingTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskZeroWidthDisablesRendering", properties);
  }

  @Test
  public void maskZeroHeightDisablesRenderingTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskZeroHeightDisablesRendering", properties);
  }

  @Test
  public void maskClipIntersectionWithClipPathTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskClipIntersectionWithClipPath", properties);
  }

  @Test
  public void maskRadialGradientTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskRadialGradient", properties);
  }

  @Test
  public void maskTransparentOverTransparentTargetsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransparentOverTransparentTargets", properties);
  }

  @Test
  public void maskGroupOverTransparentBackgroundTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskGroupOverTransparentBackground", properties);
  }

  @Test
  public void maskInheritsFromOwnAncestorsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskInheritsFromOwnAncestors", properties);
  }

  @Test
  public void maskDoesNotInheritFromReferencingElementTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskDoesNotInheritFromReferencingElement", properties);
  }

  @Test
  public void maskPropertyNoneTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPropertyNone", properties);
  }

  @Test
  public void maskUrlOverridesInheritedNoneTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUrlOverridesInheritedNone", properties);
  }

  @Test
  public void maskPropertyInheritTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPropertyInherit", properties);
  }

  @Test
  public void maskPropertyInheritDefaultsToNoneTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPropertyInheritDefaultsToNone", properties);
  }

  @Test
  @LogMessages(messages = {@LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG)})
  public void maskAnimatedPropertyInputTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskAnimatedPropertyInput", properties);
  }

  @Test
  public void maskCombinedStressCaseTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskCombinedStressCase", properties);
  }

  @Test
  public void maskContentUnitsObjectBoundingBoxGroupCompensationTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "maskContentUnitsObjectBoundingBoxGroupCompensation", properties);
  }

  @Test
  public void maskLargeCoordinateScalingStressTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskLargeCoordinateScalingStress", properties);
  }

  @Test
  public void maskSharedMaskAcrossSiblingsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskSharedMaskAcrossSiblings", properties);
  }

  @Test
  public void maskDistinctMasksOnSiblingsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskDistinctMasksOnSiblings", properties);
  }

  @Test
  public void transformedMaskedRendererAppliesTransformOnceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "transformedMaskedRendererAppliesTransformOnce", properties);
  }

  @Test
  public void objectBoundingBoxMaskContentIsNormalizedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "objectBoundingBoxMaskContentIsNormalized", properties);
  }

  @Test
  public void objectBoundingBoxMaskContentPercentagesTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,
            "objectBoundingBoxMaskContentPercentages", properties);
  }

  @Test
  public void imageWithDefaultMaskUnitsIsDrawnTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageWithDefaultMaskUnitsIsDrawn", properties);
  }

  @Test
  public void selfReferencingMaskCycleDoesNotOverflowTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "selfReferencingMaskCycleDoesNotOverflow", properties);
  }

  @Test
  public void indirectMaskCycleDoesNotOverflowTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "indirectMaskCycleDoesNotOverflow", properties);
  }

  @Test
  public void maskTextBasicTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTextBasic", properties);
  }

  @Test
  public void maskTextTypeLuminanceTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTextTypeLuminance", properties);
  }

  @Test
  public void maskTextLuminanceWithColoredFillTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTextLuminanceWithColoredFill", properties);
  }

  @Test
  public void maskTextWithTspanTransformTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTextWithTspanTransform", properties);
  }

  @Test
  public void maskUsedOnTextBasicTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUsedOnTextBasic", properties);
  }

  @Test
  public void maskUsedOnTextWithGradientMaskTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUsedOnTextWithGradientMask", properties);
  }

  @Test
  public void maskUsedOnTextWithTspanTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUsedOnTextWithTspan", properties);
  }

  @Test
  public void maskImageAsMaskContentTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskImageAsMaskContent", properties);
  }

  @Test
  public void maskAppliedToImageElementTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskAppliedToImageElement", properties);
  }

  @Test
  public void maskGroupWithDisplayNoneChildTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskGroupWithDisplayNoneChild", properties);
  }

  @Test
  public void maskedUseCycleTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskedUseCycle", properties);
  }

  @Test
  public void maskImageSliceWithOffsetTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskImageSliceWithOffset", properties);
  }
}

