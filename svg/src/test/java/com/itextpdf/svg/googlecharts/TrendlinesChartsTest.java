package com.itextpdf.svg.googlecharts;
import com.itextpdf.io.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TrendlinesChartsTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/trendlines_charts/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/trendlines_charts/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  public void trendlines_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "trendlines_chart");
  }

  @Test
  public void trendlines2_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "trendlines2_chart");
  }

  @Test
  public void trendlines3_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "trendlines3_chart");
  }

  @Test
  public void trendlines4_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "trendlines4_chart");
  }

  @Test
  public void trendlines5_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "trendlines5_chart");
  }
}
