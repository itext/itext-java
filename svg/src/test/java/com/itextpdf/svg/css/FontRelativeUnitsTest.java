package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class FontRelativeUnitsTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/FontRelativeUnitsTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/FontRelativeUnitsTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    // Text tests block

    @Test
    public void textFontSizeRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeRemUnitTest");
    }

    @Test
    public void textFontSizeEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeEmUnitTest");
    }

    @Test
    public void textNegativeFontSizeRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textNegativeFontSizeRemUnitTest");
    }

    @Test
    public void textNegativeFontSizeEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textNegativeFontSizeEmUnitTest");
    }

    @Test
    public void textFontSizeFromParentTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeFromParentTest");
    }

    @Test
    public void textFontSizeHierarchyEmAndRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeHierarchyEmAndRemUnitTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)
    })
    // TODO DEVSIX-2607 relative font-size value is not supported for tspan element
    public void textFontSizeInheritanceFromUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeInheritanceFromUseTest");
    }

    @Test
    public void textFontSizeFromUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeFromUseTest");
    }

    // Linear gradient tests block

    @Test
    public void lnrGrdntObjectBoundingBoxEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntObjectBoundingBoxEmUnitTest");
    }

    @Test
    public void lnrGrdntUserSpaceOnUseEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntUserSpaceOnUseEmUnitTest");
    }

    @Test
    public void lnrGrdntObjectBoundingBoxRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntObjectBoundingBoxRemUnitTest");
    }

    @Test
    public void lnrGrdntUserSpaceOnUseRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntUserSpaceOnUseRemUnitTest");
    }

    @Test
    public void lnrGrdntObjectBoundingBoxEmUnitFromDirectParentTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntObjectBoundingBoxEmUnitFromDirectParentTest");
    }

    @Test
    public void lnrGrdntUserSpaceOnUseEmUnitFromDirectParentTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntUserSpaceOnUseEmUnitFromDirectParentTest");
    }

    @Test
    public void lnrGrdntFontSizeFromDefsFillTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntFontSizeFromDefsFillTest");
    }

    // Symbol tests block

    @Test
    public void symbolFontSizeInheritanceFromUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"symbolFontSizeInheritanceFromUseTest");
    }

    // Marker tests block

    @Test
    public void markerFontSizeInheritanceFromDifsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"markerFontSizeInheritanceFromDifsTest");
    }
}
