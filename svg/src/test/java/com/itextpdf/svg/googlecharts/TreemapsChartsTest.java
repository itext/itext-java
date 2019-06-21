package com.itextpdf.svg.googlecharts;
import com.itextpdf.io.IOException;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TreemapsChartsTest extends SvgIntegrationTest {

  public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/googlecharts/treemaps_charts/";
  public static final String destinationFolder = "./target/test/com/itextpdf/svg/googlecharts/treemaps_charts/";

  @BeforeClass
  public static void beforeClass() {
    ITextTest.createDestinationFolder(destinationFolder);
  }

  @Test
  @LogMessages(messages = {
          @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 2),
  })
  public void treemaps_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "treemaps_chart");
  }

  @Test
  @LogMessages(messages = {
          @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 2),
  })
  public void treemaps2_chart() throws IOException, InterruptedException, java.io.IOException {
    convertAndCompareVisually(sourceFolder, destinationFolder, "treemaps2_chart");
  }
}
