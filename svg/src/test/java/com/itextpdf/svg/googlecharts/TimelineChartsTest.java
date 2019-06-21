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
public class TimelineChartsTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/timeline_charts/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/timeline_charts/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  public void timeline_advanced_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "timeline_advanced_chart");
  }

  @Test
  public void timeline_chart() throws IOException, InterruptedException, java.io.IOException {
    PageSize pageSize = PageSize.A4;
    TestUtils.convertSVGtoPDF(destinationFolder + "timeline_chart.pdf",
            sourceFolder + "timeline_chart.svg", 1, pageSize);

    Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "timeline_chart.pdf",
            sourceFolder + "cmp_timeline_chart.pdf", destinationFolder, "diff_"));
  }

  @Test
  public void timeline_labeled_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "timeline_labeled_chart");
  }
}