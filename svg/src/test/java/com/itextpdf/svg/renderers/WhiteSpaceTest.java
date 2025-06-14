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
package com.itextpdf.svg.renderers;

import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class WhiteSpaceTest extends SvgIntegrationTest {

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/WhiteSpaceTest/";
  private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/WhiteSpaceTest/";

  @BeforeAll
  public static void beforeClass() {
    ITextTest.createDestinationFolder(DESTINATION_FOLDER);
  }

  @Test
  public void whiteSpacexLinkBasicTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "whiteSpace");
  }

  @Test
  public void whiteSpaceBasicTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "white-space-basic");
  }

  @Test
  public void whiteSpaceBasicTspanTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "white-space-basic-tspan");
  }

  @Test
  public void whiteSpaceNestedTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "white-space-nested");
  }

  @Test
  public void whiteSpaceRelativePositionsTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "white-space-basic-relative-positions");
  }

  @Test
  public void whiteSpaceEmptyTest() throws IOException, InterruptedException {
    // This test result is different from browser since we don't add empty (or whitespace only) children to the
    // text branch renderer. But even if we will, spaces will be preserved for pre, pre-wrap and pre-line, however
    // they shouldn't for pre-wrap and pre-line values.
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "white-space-empty");
  }

  @Test
  public void whiteSpaceLeadingTest() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "white-space-leading");
  }
}
