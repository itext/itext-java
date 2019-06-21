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


