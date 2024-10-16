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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;

@Tag("IntegrationTest")
public class OpacityTest extends SvgIntegrationTest {

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/OpacityTest/";
  private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/OpacityTest/";

  @BeforeAll
  public static void beforeClass() {
    ITextTest.createDestinationFolder(DESTINATION_FOLDER);
  }

  @Test
  public void testOpacitySimple() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacity_simple");
  }

  @Test
  public void testOpacityRGBA() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacity_rgba");
  }

  @Test
  public void testOpacityComplex() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacity_complex");
  }

  @Test
  //TODO: update after DEVSIX-2673 fix
  public void testRGBA() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svg_rgba");
  }

  @Test
  //TODO DEVSIX-2678
  public void testFillOpacityWithComma() throws IOException, InterruptedException {
    Assertions.assertThrows(NumberFormatException.class,
            () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "testFillOpacityWithComma")
    );
  }

  @Test
  //TODO DEVSIX-2678
  public void testFillOpacityWithPercents() throws IOException, InterruptedException {
    Assertions.assertThrows(NumberFormatException.class,
            () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "testFillOpacityWithPercents")
    );
  }

  @Test
  //TODO: update after DEVSIX-2678 fix
  public void testFillOpacity() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svg_fill_opacity");
  }

  @Test
  //TODO DEVSIX-2679
  public void testStrokeOpacityWithComma() throws IOException, InterruptedException {
    Assertions.assertThrows(Exception.class,
            () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "testStrokeOpacityWithComma")
    );
  }

  @Test
  //TODO DEVSIX-2679
  public void testStrokeOpacityWithPercents() throws IOException, InterruptedException {
    Assertions.assertThrows(NumberFormatException.class,
            () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "testStrokeOpacityWithPercents")
    );
  }

  @Test
  //TODO: update after DEVSIX-2679 fix
  public void testStrokeOpacity() throws IOException, InterruptedException {
    convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svg_stroke_opacity");
  }
}
