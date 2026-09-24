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
public class GanttChartsTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/googlecharts/GanttChartsTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/googlecharts/GanttChartsTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void ganttChart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "ganttChart.pdf",
                SOURCE_FOLDER + "ganttChart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "ganttChart.pdf",
                SOURCE_FOLDER + "cmp_ganttChart.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void gantt2Chart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "gantt2Chart.pdf",
                SOURCE_FOLDER + "gantt2Chart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "gantt2Chart.pdf",
                SOURCE_FOLDER + "cmp_gantt2Chart.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void gantt3Chart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "gantt3Chart.pdf",
                SOURCE_FOLDER + "gantt3Chart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "gantt3Chart.pdf",
                SOURCE_FOLDER + "cmp_gantt3Chart.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void gantt4Chart() throws IOException, InterruptedException, java.io.IOException {
        PageSize pageSize = PageSize.A4;
        TestUtils.convertSVGtoPDF(DESTINATION_FOLDER + "gantt4Chart.pdf",
                SOURCE_FOLDER + "gantt4Chart.svg", 1, pageSize);

        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "gantt4Chart.pdf",
                SOURCE_FOLDER + "cmp_gantt4Chart.pdf", DESTINATION_FOLDER, "diff_"));
    }
}
