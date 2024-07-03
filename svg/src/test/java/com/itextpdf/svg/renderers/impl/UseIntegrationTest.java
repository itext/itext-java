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
public class UseIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/UseIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/UseIntegrationTest/";

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
    public void singleUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleUse");
    }

    @Test
    public void singleUseFillTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleUseFill");
    }

    @Test
    public void doubleNestedUseFillTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "doubleNestedUseFill");
    }

    @Test
    public void singleUseStrokeTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "singleUseStroke");
    }

    @Test
    public void doubleNestedUseStrokeTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "doubleNestedUseStroke");
    }

    @Test
    public void translateUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "translateUse");
    }

    @Test
    public void multipleTransformationsUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleTransformationsUse");
    }

    @Test
    public void coordinatesUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "coordinatesUse");
    }

    @Test
    public void imageUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageUse", properties);
    }

    @Test
    public void svgUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgUse", properties);
    }

    @Test
    public void complexUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "complexUse", properties);
    }

    @Test
    public void UseWithoutDefsTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useWithoutDefs", properties);
    }

    @Test
    public void UseWithoutDefsUsedElementAfterUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useWithoutDefsUsedElementAfterUse", properties);
    }

    @Test
    public void simpleRectReuseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleRectReuse", properties);
    }

    @Test
    public void transitiveTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "transitive", properties);
    }

    @Test
    public void circularTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "circular", properties);
    }

    @Test
    public void complexReferencesTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "complexReferences", properties);
    }

    @Test
    public void transformationsOnTransformationsTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "transformationsOnTransformations", properties);
    }

    @Test
    public void reuseLinesTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "reuseLines", properties);
    }

    @Test
    public void missingHashtagTest() throws IOException,InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "missingHashtag", properties);
    }

    @Test
    public void useInDifferentFilesExampleTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-2252 fixed
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useInDifferentFilesExampleTest");
    }
}
