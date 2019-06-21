package com.itextpdf.svg.googlecharts;
import com.itextpdf.io.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SteppedAreaChartTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/stepped_area_charts/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/stepped_area_charts/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  public void stepped_area_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "stepped_area_chart");
  }

  @Test
  public void stepped_area2_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "stepped_area2_chart");
  }

  @Test
  public void stepped_area3_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "stepped_area3_chart");
  }
}
