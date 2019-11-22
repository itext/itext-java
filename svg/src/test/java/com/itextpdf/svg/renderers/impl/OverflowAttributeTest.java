package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static com.itextpdf.svg.SvgNodeRendererIntegrationTestUtil.convertAndCompareSinglePage;

@Category(IntegrationTest.class)
public class OverflowAttributeTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/OverflowAttributeTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/OverflowAttributeTest/";

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
    public void overflowVisibleInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2860 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowVisibleInMarkerElement");
    }

    @Test
    public void overflowHiddenInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2860 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowHiddenInMarkerElement");
    }

    @Test
    public void overflowAutoInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2860 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowAutoInMarkerElement");
    }

    @Test
    public void overflowScrollInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2860 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowScrollInMarkerElement");
    }

    @Test
    public void overflowInitialInMarkerElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2860 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowInitialInMarkerElement");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 2),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES,count = 2),
    })
    public void overflowVisibleInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2257 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowVisibleInSymbol");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void overflowHiddenInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2257 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowHiddenInSymbol");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void overflowInitialInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2257 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowInitialInSymbol");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void overflowScrollInSymbolElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482, DEVSIX-2257 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowScrollInSymbol");
    }

    @Test
    public void overflowInSvgElementTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-3482 fixed
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "overflowInSvgElement");
    }
}