package com.itextpdf.svg.renderers;

import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.IOException;
@Category(IntegrationTest.class)
public class OpacityTest extends SvgIntegrationTest {

  @Rule
  public ExpectedException junitExpectedException = ExpectedException.none();

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/OpacityTest/";
  private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/OpacityTest/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(DESTINATION_FOLDER);
  }

  @Test
  public void testOpacitySimple() throws IOException, InterruptedException {
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "opacity_simple");
  }

  @Test
  public void testOpacityRGBA() throws IOException, InterruptedException {
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "opacity_rgba");
  }

  @Test
  public void testOpacityComplex() throws IOException, InterruptedException {
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "opacity_complex");
  }

  @Test
  //TODO: update after DEVSIX-2673 fix
  public void testRGBA() throws IOException, InterruptedException {
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "svg_rgba");
  }

  @Test
  //TODO DEVSIX-2678
  public void testFillOpacityWithComma() throws IOException, InterruptedException {
    junitExpectedException.expect(NumberFormatException.class);
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "testFillOpacityWithComma");
  }

  @Test
  //TODO DEVSIX-2678
  public void testFillOpacityWithPercents() throws IOException, InterruptedException {
    junitExpectedException.expect(NumberFormatException.class);
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "testFillOpacityWithPercents");
  }

  @Test
  //TODO: update after DEVSIX-2678 fix
  public void testFillOpacity() throws IOException, InterruptedException {
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "svg_fill_opacity");
  }

  @Test
  //TODO DEVSIX-2679
  public void testStrokeOpacityWithComma() throws IOException, InterruptedException {
    junitExpectedException.expect(Exception.class);
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "testStrokeOpacityWithComma");
  }

  @Test
  //TODO DEVSIX-2679
  public void testStrokeOpacityWithPercents() throws IOException, InterruptedException {
    junitExpectedException.expect(NumberFormatException.class);
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "testStrokeOpacityWithPercents");
  }

  @Test
  //TODO: update after DEVSIX-2679 fix
  public void testStrokeOpacity() throws IOException, InterruptedException {
    convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "svg_stroke_opacity");
  }
}
