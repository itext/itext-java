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

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
// TODO DEVSIX-2263 SVG: Update cmp files
public class MediaTest extends SvgIntegrationTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/MediaTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/MediaTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void mediaQueryWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryWidth");
    }

    @Test
    public void mediaQueryMinWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryMinWidth");
    }

    @Test
    public void mediaQueryBigMinWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryMinWidth2");
    }

    @Test
    public void mediaQueryBigMaxWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryMaxWidth");
    }

    @Test
    public void mediaQueryHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryHeight");
    }


    @Test
    public void mediaQueryMinHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryMinHeight");
    }


    @Test
    public void mediaQueryMaxHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryMaxHeight");
    }

    @Test
    public void mediaQueryOrientationLandscapeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryOrientationLandscape");
    }

    @Test
    public void mediaQueryOrientationPortraitTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryOrientationPortrait");
    }

    @Test
    public void mediaQueryAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryRatio");
    }

    @Test
    public void mediaQueryAspectRatioSingleValTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryRatioSingleVal");
    }

    @Test
    public void mediaQueryScreenTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryScreen");
    }

    @Test
    public void mediaQueryPrintTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryPrint");
    }

    @Test
    public void mediaQueryResolutionTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryResolution");
    }

    @Test
    public void mediaQueryOverflowBlockTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryOverflowBlock");
    }

    @Test
    public void mediaQueryOverflowInlineTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryOverflowInline");
    }

    @Test
    public void mediaQueryNotTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryNot");
    }

    @Test
    public void mediaQueryOnlyTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryOnly");
    }

    @Test
    public void mediaQueryOnlyAndTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryOnlyAnd");
    }

    @Test
    public void mediaQueryColorTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryColor");
    }

    @Test
    public void mediaQueryMinColorTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryMinColor");
    }

    @Test
    public void mediaQueryColorGamutTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryColorGamut");
    }

    @Test
    public void mediaQueryDisplayBrowserTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQueryDisplayBrowser");
    }
}
