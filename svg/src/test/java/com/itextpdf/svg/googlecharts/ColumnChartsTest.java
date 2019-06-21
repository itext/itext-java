package com.itextpdf.svg.googlecharts;

import com.itextpdf.io.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

  @Category(IntegrationTest.class)
  public class ColumnChartsTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/column_charts/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/column_charts/";

    @BeforeClass
    public static void beforeClass() {
      ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
  public void column_dualY_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "column_dualY_chart");
  }

    @Test
    public void column_material_chart() throws IOException, InterruptedException, java.io.IOException {
      convertAndCompareVisually(sourceFolder, destinationFolder, "column_material_chart");
    }

    @Test
    public void column_topX_chart() throws IOException, InterruptedException, java.io.IOException {
      convertAndCompareVisually(sourceFolder, destinationFolder, "column_topX_chart");
    }
  }


