package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class MaskTest extends SvgIntegrationTest {

  private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/MaskTest/";
  private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/MaskTest/";

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
  //TODO: update after DEVSIX-2378 implementation
  public void maskBasic() throws IOException, InterruptedException {
    convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "maskBasic", properties);
  }

  @Test
  //TODO: update after DEVSIX-2378 implementation
  @LogMessages(messages = {
          @LogMessage(messageTemplate =  SvgLogMessageConstant.UNMAPPEDTAG),
  })
  public void maskWithGradient() throws IOException, InterruptedException {
    convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "maskWithGradient", properties);
  }
}
