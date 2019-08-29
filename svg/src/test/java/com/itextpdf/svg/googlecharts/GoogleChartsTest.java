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
import com.itextpdf.io.codec.Base64;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.utils.TestUtils;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class GoogleChartsTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/GoogleChartsTests/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/GoogleChartsTests/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  public void barChart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompare(sourceFolder, destinationFolder, "bar_chart");
  }

  @Test
  @LogMessages(messages = {
          @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
  })
  public void annotation_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompare(sourceFolder, destinationFolder, "annotation_chart");
  }

  @Test
  public void area_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "area_chart.pdf",
            sourceFolder + "area_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "area_chart.pdf",
            sourceFolder + "cmp_area_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  public void bubble_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "bubble_chart.pdf",
            sourceFolder + "bubble_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "bubble_chart.pdf",
            sourceFolder + "cmp_bubble_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  @LogMessages(messages = {
          @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
  })
  public void calendar_chart() throws IOException, java.io.IOException, InterruptedException {

    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "calendar_chart.pdf",
            sourceFolder + "calendar_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "calendar_chart.pdf",
            sourceFolder + "cmp_calendar_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  public void candlestick_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompare(sourceFolder, destinationFolder, "candlestick_chart");
  }

  @Test
  public void combo_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "combo_chart.pdf",
            sourceFolder + "combo_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "combo_chart.pdf",
            sourceFolder + "cmp_combo_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  @LogMessages(messages = {
          @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 5),
  })
  public void diff_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompare(sourceFolder, destinationFolder, "diff_chart");
  }

  @Test
  public void donut_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompare(sourceFolder, destinationFolder, "donut_chart");
  }

  @Test
  public void waterfall_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompare(sourceFolder, destinationFolder, "waterfall_chart");
  }

  @Test
  public void histogram_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompare(sourceFolder, destinationFolder, "histogram_chart");
  }

}