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

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ViewBoxSvgTagSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/viewbox/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/viewbox/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }


    //Uniform viewboxes
    @Test
    public void viewBox100x100() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_100x100");
    }

    @Test
    public void viewBox200x200() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_200x200");
    }

    @Test
    public void viewBox400x400() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_400x400");
    }

    //Non-uniform viewboxes
    @Test
    public void viewBox100x200() throws IOException, InterruptedException {

        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_100x200");
    }

    @Test
    public void viewBox100x400() throws IOException, InterruptedException {

        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_100x400");
    }

    @Test
    public void viewBox200x100() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_200x100");
    }

    @Test
    public void viewBox200x400() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_200x400");
    }

    @Test
    public void viewBox400x100() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_400x100");
    }

    @Test
    public void viewBox400x200() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_400x200");
    }

    @Test
    //TODO (DEVSIX-3493) change cmp files after fix
    public void viewBoxXYValuesPreserveAspectRatioNoneValues() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"viewBoxXYValuesPreserveAspectRatioNoneValues");
    }

    @Test
    public void viewBoxXYValuesPreserveAspectRatioXMaxYMaxMeetValues() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"viewBoxXYValuesPreserveAspectRatioXMaxYMaxMeetValues");
    }

    @Test
    public void viewBoxXYValuesPreserveAspectRatioXMaxYMaxSliceValues() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"viewBoxXYValuesPreserveAspectRatioXMaxYMaxSliceValues");
    }

    @Test
    public void preserveAspectRationAllOptionsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"preserveAspectRationAllOptions");
    }
}
