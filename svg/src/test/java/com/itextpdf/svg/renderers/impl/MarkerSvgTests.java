package com.itextpdf.svg.renderers.impl;

import org.junit.Test;
import java.io.IOException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;


@Category(IntegrationTest.class)
public class MarkerSvgTests extends SvgIntegrationTest {

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/MarkerSvgTests/";
  private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/MarkerSvgTests/";

  private ISvgConverterProperties properties;

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(DESTINATION_FOLDER);
  }

  @Before
  public void before() {
    properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
  }

  @Test
  public void markerTest() throws IOException, InterruptedException {
    //TODO: update when DEVSIX-2262, 2860 fixed
    convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "marker");
  }

  @Test
  public void Markers_in_different_elements() throws IOException, InterruptedException {
    //TODO: update when DEVSIX-2262, 2860 and DEVSIX-2719 fixed
    convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "Markers_in_elements");
  }

  @Test
  public void markerUnits() throws IOException, InterruptedException {
    //TODO: update when DEVSIX-2262, 2860 fixed
    convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "marker_Units");
  }

  @Test
  public void marker_RefXY_Orient() throws IOException, InterruptedException {
    //TODO: update when DEVSIX-2262,2860 fixed
    convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "marker_RefXY_orient");
  }
}
