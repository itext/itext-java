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
package com.itextpdf.svg.googlecharts;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class GeoChartsTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/googlecharts/GeoChartsTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/googlecharts/GeoChartsTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void geoChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "geoChart");
    }

    @Test
    public void geoColoredChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "geoColoredChart");
    }

    @Test
    public void geoMarkerChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "geoMarkerChart");
    }

    @Test
    public void geoPropontionalChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "geoPropontionalChart");
    }

    @Test
    public void geoTextChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "geoTextChart");
    }
}
