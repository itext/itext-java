/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.googlecharts;

import com.itextpdf.io.IOException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.utils.TestUtils;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GanttChartsTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/gantt_charts/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/gantt_charts/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  public void gantt_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "gantt_chart.pdf",
            sourceFolder + "gantt_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gantt_chart.pdf",
            sourceFolder + "cmp_gantt_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  public void gantt2_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "gantt2_chart.pdf",
            sourceFolder + "gantt2_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gantt2_chart.pdf",
            sourceFolder + "cmp_gantt2_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  public void gantt3_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "gantt3_chart.pdf",
            sourceFolder + "gantt3_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gantt3_chart.pdf",
            sourceFolder + "cmp_gantt3_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  public void gantt4_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "gantt4_chart.pdf",
            sourceFolder + "gantt4_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "gantt4_chart.pdf",
            sourceFolder + "cmp_gantt4_chart.pdf", destinationFolder, "diff_"));
  }
}


