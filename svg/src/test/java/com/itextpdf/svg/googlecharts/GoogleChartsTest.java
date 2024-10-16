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
package com.itextpdf.svg.googlecharts;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.utils.TestUtils;
import com.itextpdf.test.ITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class GoogleChartsTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/GoogleChartsTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/GoogleChartsTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void barChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "barChart");
    }

    @Test
    public void annotationChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "annotationChart");
    }

    @Test
    public void areaChart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(destinationFolder + "areaChart.pdf",
                sourceFolder + "areaChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "areaChart.pdf",
                sourceFolder + "cmp_areaChart.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void bubbleChart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(destinationFolder + "bubbleChart.pdf",
                sourceFolder + "bubbleChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "bubbleChart.pdf",
                sourceFolder + "cmp_bubbleChart.pdf", destinationFolder, "diff_"));
    }

    @Test
    //TODO DEVSIX-4857 support stroke-linecap attribute
    public void calendarChart() throws IOException, java.io.IOException, InterruptedException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(destinationFolder + "calendarChart.pdf",
                sourceFolder + "calendarChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "calendarChart.pdf",
                sourceFolder + "cmp_calendarChart.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void candlestickChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "candlestickChart");
    }

    @Test
    public void comboChart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(destinationFolder + "comboChart.pdf",
                sourceFolder + "comboChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "comboChart.pdf",
                sourceFolder + "cmp_comboChart.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void diffChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "diffChart");
    }

    @Test
    public void donutChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "donutChart");
    }

    @Test
    public void waterfallChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "waterfallChart");
    }

    @Test
    public void histogramChart() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "histogramChart");
    }
}
