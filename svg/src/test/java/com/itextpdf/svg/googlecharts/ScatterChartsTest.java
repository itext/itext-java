package com.itextpdf.svg.googlecharts;
import com.itextpdf.io.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ScatterChartsTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/scatter_charts/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/scatter_charts/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  public void scatter_charts() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "scatter_chart");
  }

  @Test
  public void scatter_dualY_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "scatter_dualY_chart");
  }

  @Test
  public void scatter_material_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "scatter_material_chart");
  }

  @Test
  public void scatter_stars_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "scatter_stars_chart");
  }

  @Test
  public void scatter_topX_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "scatter_topX_chart");
  }
}
