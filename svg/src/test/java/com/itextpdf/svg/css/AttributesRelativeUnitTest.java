package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

public class AttributesRelativeUnitTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/AttributesRelativeUnitTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/AttributesRelativeUnitTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)})
    // TODO DEVSIX-4834 support relative units in attributes of svg elements. Remove log message at this test
    public void rectangleAttributesEmUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleAttributesEmUnits");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)})
    // TODO DEVSIX-4834 support relative units in attributes of svg elements. Remove log message at this test
    public void rectangleAttributesExUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleAttributesExUnits");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)})
    // TODO DEVSIX-4834 support relative units in attributes of svg elements. Remove log message at this test
    public void rectangleAttributesPercentUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleAttributesPercentUnits");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)})
    // TODO DEVSIX-4834 support relative units in attributes of svg elements. Remove log message at this test
    public void imageAttributesEmUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "imageAttributesEmUnits");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)})
    // TODO DEVSIX-4834 support relative units in attributes of svg elements. Remove log message at this test
    public void imageAttributesExUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "imageAttributesExUnits");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)})
    // TODO DEVSIX-4834 support relative units in attributes of svg elements. Remove log message at this test
    public void imageAttributesPercentUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "imageAttributesPercentUnits");
    }
}
