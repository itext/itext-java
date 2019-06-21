package com.itextpdf.svg.googlecharts;
import com.itextpdf.io.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IntervalsChartsTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/intervals_charts/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/intervals_charts/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  public void intervals_area_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_area_chart");
  }

  @Test
  public void intervals_backgroundBox_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_backgroundBox_chart");
  }

  @Test
  public void intervals_box_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_box_chart");
  }

  @Test
  public void intervals_boxPlot_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_boxPlot_chart");
  }

  @Test
  public void intervals_boxThick_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_boxThick_chart");
  }

  @Test
  public void intervals_combining_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_combining_chart");
  }

  @Test
  public void intervals_line_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_line_chart");
  }

  @Test
  public void intervals_points_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_points_chart");
  }

  @Test
  public void intervals_pointsWhiskers_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_pointsWhiskers_chart");
  }

  @Test
  public void intervals_stick_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_stick_chart");
  }

  @Test
  public void intervals_sticksHorizontal_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_sticksHorizontal_chart");
  }

  @Test
  public void intervals_tailored_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "intervals_tailored_chart");
  }
}
