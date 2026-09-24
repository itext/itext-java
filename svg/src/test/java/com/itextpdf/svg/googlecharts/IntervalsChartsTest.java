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
public class IntervalsChartsTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/googlecharts/IntervalsChartsTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/googlecharts/IntervalsChartsTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void intervalsAreaChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsAreaChart");
    }

    @Test
    public void intervalsBackgroundBoxChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsBackgroundBoxChart");
    }

    @Test
    public void intervalsBoxChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsBoxChart");
    }

    @Test
    public void intervalsBoxPlotChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsBoxPlotChart");
    }

    @Test
    public void intervalsBoxThickChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsBoxThickChart");
    }

    @Test
    public void intervalsCombiningChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsCombiningChart");
    }

    @Test
    public void intervalsLineChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsLineChart");
    }

    @Test
    public void intervalsPointsChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsPointsChart");
    }

    @Test
    public void intervalsPointsWhiskersChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsPointsWhiskersChart");
    }

    @Test
    public void intervalsStickChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsStickChart");
    }

    @Test
    public void intervalsSticksHorizontalChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsSticksHorizontalChart");
    }

    @Test
    public void intervalsTailoredChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "intervalsTailoredChart");
    }
}
