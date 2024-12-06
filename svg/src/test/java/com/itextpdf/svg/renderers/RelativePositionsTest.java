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
package com.itextpdf.svg.renderers;

import com.itextpdf.test.ITextTest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class RelativePositionsTest extends SvgIntegrationTest {

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/RelativePositionsTest/";
  private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/RelativePositionsTest/";

  @BeforeAll
  public static void beforeClass() {
    ITextTest.createDestinationFolder(DESTINATION_FOLDER);
  }

  //TODO DEVSIX-5740: Update cmp file after supporting
  @Test
  public void relativePositionsBasic1Test() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativePositionsBasic");
  }

  @Test
  public void relativePositionsBasic2Test() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativePositionsBasic2");
  }

  //TODO DEVSIX-5740: Update cmp file after supporting
  @Test
  public void relativePositionsListValuesTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativePositionsListValues");
  }

  //TODO DEVSIX-5740: Update cmp file after supporting
  @Test
  public void relativePositionsListValuesNoXTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativePositionsListValuesNoX");
  }

  //TODO DEVSIX-5740: Update cmp file after supporting
  @Test
  public void relativePositionsListValuesNoYTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativePositionsListValuesNoY");
  }
}
