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
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.utils.TestUtils;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class GoogleChartsTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/googlecharts/GoogleChartsTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/googlecharts/GoogleChartsTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void barChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "barChart");
    }

    @Test
    public void annotationChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "annotationChart");
    }

    @Test
    public void areaChart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "areaChart.pdf",
                SOURCE_FOLDER + "areaChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "areaChart.pdf",
                SOURCE_FOLDER + "cmp_areaChart.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void bubbleChart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "bubbleChart.pdf",
                SOURCE_FOLDER + "bubbleChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "bubbleChart.pdf",
                SOURCE_FOLDER + "cmp_bubbleChart.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void calendarChart() throws IOException, java.io.IOException, InterruptedException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "calendarChart.pdf",
                SOURCE_FOLDER + "calendarChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "calendarChart.pdf",
                SOURCE_FOLDER + "cmp_calendarChart.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void candlestickChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "candlestickChart");
    }

    @Test
    public void comboChart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "comboChart.pdf",
                SOURCE_FOLDER + "comboChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "comboChart.pdf",
                SOURCE_FOLDER + "cmp_comboChart.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void diffChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "diffChart");
    }

    @Test
    public void donutChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "donutChart");
    }

    @Test
    public void waterfallChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "waterfallChart");
    }

    @Test
    public void histogramChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "histogramChart");
    }
}
