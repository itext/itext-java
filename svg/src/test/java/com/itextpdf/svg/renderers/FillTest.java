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

import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.ITextTest;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FillTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/FillTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/FillTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void normalRectangleFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "normalRectangleFill");
    }

    @Test
    public void multipleNormalRectangleFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleNormalRectangleFill");
    }

    @Test
    public void noRectangleFillColorTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noRectangleFillColor");
    }

    @Test
    public void eoFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill");
    }

    /* This test should fail when DEVSIX-2251 is resolved*/
    @Test
    public void eoFillTest01() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill01");
    }

    @Test
    public void eoFillTest02() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill02");
    }

    @Test
    public void eoFillTest03() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofill03");
    }

    @Test
    public void  multipleObjectsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleObjectsTest");
    }

    @Test
    public void eoFillStrokeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofillstroke");
    }

    /* This test should fail when DEVSIX-2251 is resolved*/
    @Test
    public void nonZeroFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nonzerofill");
    }

    @Test
    public void opacityFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacityfill");
    }

    @Test
    public void eofillUnsuportedAtributeTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofillUnsuportedAtributeTest")
        );
    }

    @Test
    public void pathVerticalLineFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathVerticalLineFillTest");
    }

    @Test
    public void pathHorizontalLineFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHorizontalLineFillTest");
    }

    @Test
    //TODO update cmp file after DEVSIX-3365 will be fixed
    public void invalidUrlFillTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "invalidUrlFillTest");
    }
}
