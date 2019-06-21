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
    convertAndCompareVisually(sourceFolder, destinationFolder, "bar_chart");
  }

  @Test
  @LogMessages(messages = {
          @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
  })
  public void annotation_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "annotation_chart");
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
    convertAndCompareVisually(sourceFolder, destinationFolder, "candlestick_chart");
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
    convertAndCompareVisually(sourceFolder, destinationFolder, "diff_chart");
  }

  @Test
  public void donut_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "donut_chart");
  }

  @Test
  public void waterfall_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "waterfall_chart");
  }

  @Test
  public void histogram_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "histogram_chart");
  }

}