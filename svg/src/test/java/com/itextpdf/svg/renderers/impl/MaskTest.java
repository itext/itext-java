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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class MaskTest extends SvgIntegrationTest {

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/MaskTest/";
  private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/MaskTest/";

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
  //TODO: update after DEVSIX-2378 implementation
  public void maskBasic() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskBasic", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskWithGradient() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskWithGradient", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskContentUnitsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "mask-content-units", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskPatternCombiTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternCombi", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskPatternAppliedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternApplied", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskMultiShapesTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskMultiShapes", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskPatternGradientAppliedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternGradientApplied", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskGradientAppliedMaskContentUnitsTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskGradientAppliedMaskContentUnits", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskPatternMaskContentUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternMaskContentUnitsUserSpaceOnUse", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskUnitsObjectBoundingBoxTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsObjectBoundingBox", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsUserSpaceOnUse", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskTransformTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransform", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskTransform2Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransform2", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskTransform3Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransform3", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskInheritedBasicTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskInheritedBasic", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskInherited3LevelTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskInherited3Level", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskPatternAppliedInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskPatternAppliedInherited", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskUnitsObjectBoundingBoxInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsObjectBoundingBoxInherited", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskUnitsUserSpaceOnUseInherited2Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsUserSpaceOnUseInherited2", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskUnitsUserSpaceOnUseInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsUserSpaceOnUseInherited", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskUnitsObjectBoundingBoxInherited2Test() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskUnitsObjectBoundingBoxInherited2", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  public void maskTransformInheritedTest() throws IOException, InterruptedException {
    convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "maskTransformInherited", properties);
  }
}
