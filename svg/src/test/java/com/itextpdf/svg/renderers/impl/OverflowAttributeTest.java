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

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;


@Tag("IntegrationTest")
public class OverflowAttributeTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/OverflowAttributeTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/OverflowAttributeTest/";

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
    public void overflowVisibleInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowVisibleInMarkerElement");
    }

    @Test
    public void overflowHiddenInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowHiddenInMarkerElement");
    }

    @Test
    public void overflowAutoInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowAutoInMarkerElement");
    }

    @Test
    public void overflowScrollInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowScrollInMarkerElement");
    }

    @Test
    public void overflowInitialInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowInitialInMarkerElement");
    }

    @Test
    public void overflowVisibleInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowVisibleInSymbol");
    }

    @Test
    public void overflowHiddenInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowHiddenInSymbol");
    }

    @Test
    public void overflowInitialInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowInitialInSymbol");
    }

    @Test
    public void overflowScrollInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowScrollInSymbol");
    }

    @Test
    public void overflowInSvgElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowInSvgElement");
    }
}
